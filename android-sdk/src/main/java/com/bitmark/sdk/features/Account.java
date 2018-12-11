package com.bitmark.sdk.features;

import android.app.Activity;
import android.text.TextUtils;
import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.callback.Callback0;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.crypto.Sha3256;
import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.cryptography.crypto.key.PublicKey;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.keymanagement.KeyManager;
import com.bitmark.sdk.keymanagement.KeyManagerImpl;
import com.bitmark.sdk.utils.AccountNumberData;
import com.bitmark.sdk.utils.Version;

import java.util.Locale;

import static com.bitmark.apiservice.utils.Address.CHECKSUM_LENGTH;
import static com.bitmark.apiservice.utils.ArrayUtil.concat;
import static com.bitmark.apiservice.utils.ArrayUtil.slice;
import static com.bitmark.cryptography.crypto.SecretBox.generateSecretBox;
import static com.bitmark.cryptography.crypto.encoder.Base58.BASE_58;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.utils.Validator.checkValid;
import static com.bitmark.sdk.utils.SdkUtils.generateSeedKey;
import static com.bitmark.sdk.utils.SdkUtils.randomEntropy;
import static com.bitmark.sdk.utils.Version.TWELVE;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Account {

    private static final byte[] KEY_INDEX = HEX.decode("000000000000000000000000000003E7"); // 999

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

    public static void loadFromKeyStore(Activity activity, String accountNumber,
                                        Callback1<Account> callback) {
        final KeyManager keyManager = new KeyManagerImpl(activity);
        keyManager.getKey(accountNumber, new Callback1<byte[]>() {
            @Override
            public void onSuccess(byte[] core) {
                try {
                    Seed seed = new Seed(core);
                    Account account = Account.fromSeed(seed);
                    callback.onSuccess(account);
                } catch (Throwable e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
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

    public void saveToKeyStore(Activity activity, String alias, boolean isAuthenticationRequired,
                               Callback0 callback) {
        final String keyAlias = TextUtils.isEmpty(alias) ? accountNumber : alias;
        final KeyManager keyManager = new KeyManagerImpl(activity);
        keyManager.saveKey(keyAlias, core, isAuthenticationRequired, callback);
    }

    public void saveToKeyStore(Activity activity, boolean isAuthenticationRequired,
                               Callback0 callback) {
        saveToKeyStore(activity, null, isAuthenticationRequired, callback);
    }

    public void removeFromKeyStore(Activity activity, Callback0 callback) {
        removeFromKeyStore(activity, accountNumber, callback);
    }

    public static void removeFromKeyStore(Activity activity, String alias, Callback0 callback) {
        final KeyManager keyManager = new KeyManagerImpl(activity);
        keyManager.removeKey(alias, callback);
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
        final Version version = Version.fromCore(core);
        final byte[] seed = version == TWELVE ? generateSeedKey(core, Ed25519.SEED_LENGTH) :
                generateSecretBox(KEY_INDEX, new byte[NONCE_LENGTH], core);
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
