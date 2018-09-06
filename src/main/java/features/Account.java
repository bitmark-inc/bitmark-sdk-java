package features;

import config.GlobalConfiguration;
import config.Network;
import config.SdkConfig;
import crypto.Ed25519;
import crypto.Sha3256;
import crypto.encoder.VarInt;
import crypto.key.KeyPair;
import crypto.key.PublicKey;
import error.ValidateException;
import utils.AccountNumberData;
import utils.ArrayUtil;
import utils.RecoveryPhrase;
import utils.Seed;
import utils.error.InvalidAddressException;
import utils.error.InvalidNetworkException;

import java.util.Arrays;

import static config.SdkConfig.CHECKSUM_LENGTH;
import static config.SdkConfig.KEY_TYPE;
import static config.SdkConfig.KeyPart.PUBLIC_KEY;
import static crypto.Random.randomBytes;
import static crypto.SecretBox.generateSecretBox;
import static crypto.encoder.Base58.BASE_58;
import static crypto.encoder.Hex.HEX;
import static utils.ArrayUtil.*;
import static utils.Validator.checkValid;

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

    public static Account fromSeed(Seed seed) throws ValidateException.InvalidLength {
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
        if (!Network.isValid(networkValue)) throw new InvalidNetworkException(networkValue);
        final Network network = Network.valueOf(networkValue);

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
