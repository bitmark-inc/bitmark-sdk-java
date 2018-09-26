package com.bitmark.sdk.features;

import com.bitmark.sdk.crypto.Ed25519;
import com.bitmark.sdk.crypto.Sha3256;
import com.bitmark.sdk.crypto.key.KeyPair;
import com.bitmark.sdk.crypto.key.PublicKey;
import com.bitmark.sdk.error.ValidateException;
import com.bitmark.sdk.service.configuration.GlobalConfiguration;
import com.bitmark.sdk.service.configuration.Network;
import com.bitmark.sdk.utils.AccountNumberData;
import com.bitmark.sdk.utils.Address;
import com.bitmark.sdk.utils.RecoveryPhrase;
import com.bitmark.sdk.utils.Seed;

import static com.bitmark.sdk.crypto.Random.randomBytes;
import static com.bitmark.sdk.crypto.SecretBox.generateSecretBox;
import static com.bitmark.sdk.crypto.encoder.Base58.BASE_58;
import static com.bitmark.sdk.crypto.encoder.Hex.HEX;
import static com.bitmark.sdk.utils.Address.CHECKSUM_LENGTH;
import static com.bitmark.sdk.utils.ArrayUtil.concat;
import static com.bitmark.sdk.utils.ArrayUtil.slice;
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
        return new Seed(core, data.getNetwork(), Seed.VERSION);
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
        Address address = Address.fromAccountNumber(accountNumber);
        return AccountNumberData.from(address.getKey(), address.getNetwork());
    }

    private static KeyPair generateKeyPair(byte[] core) {
        final byte[] seed = generateSecretBox(KEY_INDEX, new byte[NONCE_LENGTH], core);
        return Ed25519.generateKeyPairFromSeed(seed);
    }

    private static String generateAccountNumber(PublicKey key) {
        return generateAccountNumber(key, GlobalConfiguration.network());
    }

    private static String generateAccountNumber(PublicKey key, Network network) {
        Address address = Address.getDefault(key, network);
        final byte[] keyVariantVarInt = address.getPrefix();
        final byte[] publicKeyBytes = key.toBytes();
        final byte[] preChecksum = concat(keyVariantVarInt, publicKeyBytes);
        final byte[] checksum = slice(Sha3256.hash(preChecksum), 0, CHECKSUM_LENGTH);
        return BASE_58.encode(concat(keyVariantVarInt, publicKeyBytes, checksum));
    }

}
