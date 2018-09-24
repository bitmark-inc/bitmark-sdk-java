package com.bitmark.sdk.features;

import com.bitmark.sdk.config.GlobalConfiguration;
import com.bitmark.sdk.config.Network;
import com.bitmark.sdk.config.SdkConfig;
import com.bitmark.sdk.crypto.Ed25519;
import com.bitmark.sdk.crypto.Sha3256;
import com.bitmark.sdk.crypto.encoder.VarInt;
import com.bitmark.sdk.crypto.key.KeyPair;
import com.bitmark.sdk.crypto.key.PublicKey;
import com.bitmark.sdk.error.ValidateException;
import com.bitmark.sdk.utils.Address;
import com.bitmark.sdk.utils.ArrayUtil;
import com.bitmark.sdk.utils.*;
import com.bitmark.sdk.utils.error.InvalidAddressException;
import com.bitmark.sdk.utils.error.InvalidNetworkException;

import java.util.Arrays;

import static com.bitmark.sdk.config.SdkConfig.CHECKSUM_LENGTH;
import static com.bitmark.sdk.config.SdkConfig.KEY_TYPE;
import static com.bitmark.sdk.config.SdkConfig.KeyPart.PUBLIC_KEY;
import static com.bitmark.sdk.crypto.Random.randomBytes;
import static com.bitmark.sdk.crypto.SecretBox.generateSecretBox;
import static com.bitmark.sdk.crypto.encoder.Base58.BASE_58;
import static com.bitmark.sdk.crypto.encoder.Hex.HEX;
import static com.bitmark.sdk.utils.ArrayUtil.*;
import static com.bitmark.sdk.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Account {

    private static final byte[] KEY_INDEX = HEX.decode("000000000000000000000000000003E7"); // 999

    private static final int CORE_LENGTH = 32;

    private static final int NONCE_LENGTH = 24;

    private String accountNumber;

    private byte[] core;

    public static Account fromSeed(Seed seed) throws ValidateException {
        checkValid(() -> seed.getNetwork() == GlobalConfiguration.network(), "Incorrect network " +
                "from Seed");
        final byte[] core = seed.getSeed();
        final KeyPair key = generateKeyPair(core);
        final String accountNumber = generateAccountNumber(key.publicKey(), seed.getNetwork());
        return new Account(core, accountNumber);
    }

    public static Account fromRecoveryPhrase(String... recoveryPhrase) throws ValidateException {
        final RecoveryPhrase phrase = RecoveryPhrase.fromMnemonicWords(recoveryPhrase);
        final Seed seed = phrase.recoverSeed();
        return fromSeed(seed);
    }

    public Account() {
        core = randomBytes(CORE_LENGTH);
        final KeyPair key = generateKeyPair(core);
        accountNumber = generateAccountNumber(key.publicKey());
    }

    private Account(byte[] core, String accountNumber) {
        this.core = core;
        this.accountNumber = accountNumber;
    }

    public KeyPair getKey() {
        return generateKeyPair(core);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public RecoveryPhrase getRecoveryPhrase() {
        return RecoveryPhrase.fromSeed(getSeed());
    }

    public Seed getSeed() {
        final AccountNumberData data = parseAccountNumber(accountNumber);
        return new Seed(core, data.getNetwork(), SdkConfig.Seed.VERSION);
    }

    public Address toAddress() {
        return Address.fromAccountNumber(accountNumber);
    }

    public static boolean isValidAccountNumber(String accountNumber) {
        try {
            parseAccountNumber(accountNumber);
            return true;
        } catch (ValidateException ex) {
            return false;
        }
    }

    public static AccountNumberData parseAccountNumber(String accountNumber) {
        final byte[] addressBytes = BASE_58.decode(accountNumber);
        int keyVariant = VarInt.readUnsignedVarInt(addressBytes);
        final int keyVariantLength = toByteArray(keyVariant).length;

        // Verify address length
        int addressLength = keyVariantLength + Ed25519.PUBLIC_KEY_LENGTH + CHECKSUM_LENGTH;
        if (addressLength != addressBytes.length) throw new InvalidAddressException("Address " +
                "length is invalid. The expected is " + addressLength + " but actual is " + addressBytes.length);

        // Verify checksum
        final byte[] checksumData = slice(addressBytes, 0,
                keyVariantLength + Ed25519.PUBLIC_KEY_LENGTH);
        final byte[] checksum = slice(Sha3256.hash(checksumData), 0, CHECKSUM_LENGTH);
        final byte[] checksumFromAddress = slice(addressBytes,
                addressLength - CHECKSUM_LENGTH, addressLength);
        if (!ArrayUtil.equals(checksumFromAddress, checksum)) throw new InvalidAddressException(
                "Invalid checksum. The expected is " + Arrays.toString(checksum) + " but actual " +
                        "is " + Arrays.toString(checksumFromAddress));

        // Check for whether it's an address
        if ((keyVariant & 0x01) != SdkConfig.KeyPart.PUBLIC_KEY.value())
            throw new InvalidAddressException();

        // Verify network value
        int networkValue = (keyVariant >> 1) & 0x01;
        final Network network = Network.valueOf(networkValue);
        if (!Network.isValid(networkValue) || GlobalConfiguration.network() != network)
            throw new InvalidNetworkException(networkValue);

        final byte[] publicKey = slice(addressBytes, keyVariantLength,
                addressLength - CHECKSUM_LENGTH);

        return AccountNumberData.from(PublicKey.from(publicKey), network);
    }

    private static KeyPair generateKeyPair(byte[] core) {
        final byte[] seed = generateSecretBox(KEY_INDEX, new byte[NONCE_LENGTH], core);
        return Ed25519.generateKeyPairFromSeed(seed);
    }

    private static String generateAccountNumber(PublicKey key) {
        return generateAccountNumber(key, GlobalConfiguration.network());
    }

    private static String generateAccountNumber(PublicKey key, Network network) {
        int keyVariantValue = KEY_TYPE << 4;
        keyVariantValue |= PUBLIC_KEY.value();
        keyVariantValue |= (network.value() << 1);

        final byte[] keyVariantVarInt = VarInt.writeUnsignedVarInt(keyVariantValue);
        final byte[] publicKeyBytes = key.toBytes();
        final byte[] preChecksum = concat(keyVariantVarInt, publicKeyBytes);
        final byte[] checksum = slice(Sha3256.hash(preChecksum), 0, CHECKSUM_LENGTH);
        final byte[] address = concat(keyVariantVarInt, publicKeyBytes, checksum);
        return BASE_58.encode(address);
    }

}
