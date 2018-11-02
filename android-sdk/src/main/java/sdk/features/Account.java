package sdk.features;

import apiservice.configuration.GlobalConfiguration;
import apiservice.configuration.Network;
import apiservice.utils.Address;
import cryptography.crypto.Ed25519;
import cryptography.crypto.Sha3256;
import cryptography.crypto.key.KeyPair;
import cryptography.crypto.key.PublicKey;
import cryptography.error.ValidateException;
import sdk.utils.AccountNumberData;
import sdk.utils.RecoveryPhrase;
import sdk.utils.Seed;

import java.util.Locale;

import static apiservice.utils.Address.CHECKSUM_LENGTH;
import static apiservice.utils.ArrayUtil.concat;
import static apiservice.utils.ArrayUtil.slice;
import static cryptography.crypto.encoder.Base58.BASE_58;
import static cryptography.utils.Validator.checkValid;
import static sdk.utils.SdkUtils.generateSeedKey;
import static sdk.utils.SdkUtils.randomEntropy;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Account {

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
        core = randomEntropy(GlobalConfiguration.network());
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
        return getRecoveryPhrase(Locale.ENGLISH);
    }

    public RecoveryPhrase getRecoveryPhrase(Locale locale) {
        return RecoveryPhrase.fromSeed(getSeed(), locale);
    }

    public Seed getSeed() {
        final AccountNumberData data = parseAccountNumber(accountNumber);
        return new Seed(core, data.getNetwork());
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
        final byte[] seed = generateSeedKey(core, Ed25519.SEED_LENGTH);
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
