/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.features;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.callback.Callback0;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.crypto.Sha3256;
import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.cryptography.crypto.key.PrivateKey;
import com.bitmark.cryptography.crypto.key.PublicKey;
import com.bitmark.sdk.authentication.KeyAuthenticationSpec;
import com.bitmark.sdk.features.internal.RecoveryPhrase;
import com.bitmark.sdk.features.internal.Seed;
import com.bitmark.sdk.features.internal.SeedTwelve;
import com.bitmark.sdk.features.internal.SeedTwentyFour;
import com.bitmark.sdk.keymanagement.KeyManager;
import com.bitmark.sdk.keymanagement.KeyManagerImpl;

import java.util.Locale;

import static com.bitmark.apiservice.utils.Address.CHECKSUM_LENGTH;
import static com.bitmark.apiservice.utils.ArrayUtil.concat;
import static com.bitmark.apiservice.utils.ArrayUtil.slice;
import static com.bitmark.cryptography.crypto.encoder.Base58.BASE_58;
import static com.bitmark.cryptography.utils.Validator.checkNonNull;
import static com.bitmark.cryptography.utils.Validator.checkValid;

public class Account {

    private String accountNumber;

    private Seed seed;

    private KeyPair keyPair;

    public Account() {
        seed = new SeedTwelve();
        keyPair = seed.getAuthKeyPair();
        accountNumber = generateAccountNumber(seed.getAuthKeyPair()
                .publicKey());
    }

    private Account(Seed seed, String accountNumber) {
        this.seed = seed;
        this.keyPair = seed.getAuthKeyPair();
        this.accountNumber = accountNumber;
    }

    public Account(PrivateKey privateKey) {
        keyPair = Ed25519.getKeyPair(privateKey.toBytes());
        accountNumber = generateAccountNumber(keyPair.publicKey());
    }

    /**
     * This is deprecated.
     *
     * @see Account#fromSeed(String)
     */
    @Deprecated
    public static Account fromSeed(Seed seed) {
        checkValid(
                () -> seed.getNetwork() == GlobalConfiguration.network(),
                "Incorrect network from Seed"
        );

        final String accountNumber =
                generateAccountNumber(
                        seed.getAuthKeyPair().publicKey(),
                        seed.getNetwork()
                );
        return new Account(seed, accountNumber);
    }

    public static Account fromSeed(String encodedSeed) {
        checkNonNull(encodedSeed);

        byte[] encodedSeedBytes = BASE_58.decode(encodedSeed);
        checkValid(
                () -> (encodedSeedBytes.length == SeedTwelve.ENCODED_SEED_LENGTH ||
                        encodedSeedBytes.length == SeedTwentyFour.ENCODED_SEED_LENGTH),
                "invalid encoded seed"
        );
        Seed seed;
        if (encodedSeedBytes.length == SeedTwelve.ENCODED_SEED_LENGTH) {
            seed = SeedTwelve.fromEncodedSeed(encodedSeed);
        } else {
            seed = SeedTwentyFour.fromEncodedSeed(encodedSeed);
        }

        final String accountNumber =
                generateAccountNumber(
                        seed.getAuthKeyPair().publicKey(),
                        seed.getNetwork()
                );
        return new Account(seed, accountNumber);
    }

    public static Account fromRecoveryPhrase(String... recoveryPhrase) {
        final RecoveryPhrase phrase = RecoveryPhrase.fromMnemonicWords(
                recoveryPhrase);
        final Seed seed = phrase.recoverSeed();
        return fromSeed(seed);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void loadFromKeyStore(
            Activity activity, String accountNumber,
            KeyAuthenticationSpec spec,
            Callback1<Account> callback
    ) {
        final KeyManager keyManager = new KeyManagerImpl(activity);
        keyManager.getKey(accountNumber, spec, new Callback1<byte[]>() {
            @Override
            public void onSuccess(byte[] seedBytes) {
                Account account = null;
                try {
                    Seed seed = seedBytes.length == SeedTwelve.SEED_BYTE_LENGTH
                                ? new SeedTwelve(seedBytes)
                                : new SeedTwentyFour(
                                        seedBytes,
                                        GlobalConfiguration.network()
                                );
                    account = Account.fromSeed(seed);
                } catch (Throwable e) {
                    callback.onError(e);
                }

                if (account != null) {
                    callback.onSuccess(account);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void removeFromKeyStore(
            Activity activity, String alias,
            KeyAuthenticationSpec spec, Callback0 callback
    ) {
        final KeyManager keyManager = new KeyManagerImpl(activity);
        keyManager.removeKey(alias, spec, callback);
    }

    public static boolean verify(
            String accountNumber,
            byte[] signature,
            byte[] message
    ) {
        return Ed25519.verify(
                signature,
                message,
                Address.fromAccountNumber(accountNumber)
                        .getPublicKey()
                        .toBytes()
        );
    }

    public byte[] sign(byte[] message) {
        return Ed25519.sign(message, getAuthKeyPair().privateKey().toBytes());
    }

    public static boolean isValidAccountNumber(String accountNumber) {
        return Address.isValidAccountNumber(accountNumber);
    }

    private static String generateAccountNumber(PublicKey key) {
        return generateAccountNumber(key, GlobalConfiguration.network());
    }

    private static String generateAccountNumber(
            PublicKey key,
            Network network
    ) {
        Address address = Address.getDefault(key, network);
        final byte[] keyVariantVarInt = address.getPrefix();
        final byte[] publicKeyBytes = key.toBytes();
        final byte[] preChecksum = concat(keyVariantVarInt, publicKeyBytes);
        final byte[] checksum = slice(
                Sha3256.hash(preChecksum),
                0,
                CHECKSUM_LENGTH
        );
        return BASE_58.encode(concat(
                keyVariantVarInt,
                publicKeyBytes,
                checksum
        ));
    }

    public KeyPair getAuthKeyPair() {
        return keyPair;
    }

    public KeyPair getEncKeyPair() {
        if (seed != null) {
            return seed.getEncKeyPair();
        } else {
            return null;
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public RecoveryPhrase getRecoveryPhrase() {
        return getRecoveryPhrase(Locale.ENGLISH);
    }

    public RecoveryPhrase getRecoveryPhrase(Locale locale) {
        return seed.getRecoveryPhrase(locale);
    }

    public Seed getSeed() {
        return seed;
    }

    public Address toAddress() {
        return Address.fromAccountNumber(accountNumber);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void saveToKeyStore(
            Activity activity, String alias, KeyAuthenticationSpec spec,
            Callback0 callback
    ) {
        final String keyAlias = TextUtils.isEmpty(alias)
                                ? accountNumber
                                : alias;
        final KeyManager keyManager = new KeyManagerImpl(activity);
        keyManager.saveKey(keyAlias, spec, seed.getSeedBytes(), callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void saveToKeyStore(
            Activity activity, KeyAuthenticationSpec spec,
            Callback0 callback
    ) {
        saveToKeyStore(activity, accountNumber, spec, callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void removeFromKeyStore(
            Activity activity, KeyAuthenticationSpec spec,
            Callback0 callback
    ) {
        removeFromKeyStore(activity, accountNumber, spec, callback);
    }

}
