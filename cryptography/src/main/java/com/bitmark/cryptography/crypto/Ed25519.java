/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.crypto;

import com.bitmark.cryptography.crypto.key.Ed25519KeyPair;
import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.cryptography.crypto.sodium.Sodium;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.utils.JniUtils.call;
import static com.bitmark.cryptography.utils.Validator.checkValidHex;
import static com.bitmark.cryptography.utils.Validator.checkValidLength;

public class Ed25519 {

    public static final int SEED_LENGTH = Sodium.crypto_sign_ed25519_seedbytes();

    public static final int PUBLIC_KEY_LENGTH = Sodium.crypto_sign_ed25519_publickeybytes();

    public static final int PRIVATE_KEY_LENGTH = Sodium.crypto_sign_ed25519_secretkeybytes();

    public static final int SIG_LENGTH = 64;

    private Ed25519() {
    }

    public static KeyPair generateKeyPair() {
        final byte[] publicKey = new byte[PUBLIC_KEY_LENGTH];
        final byte[] privateKey = new byte[PRIVATE_KEY_LENGTH];
        call(
                () -> Sodium.crypto_sign_ed25519_keypair(publicKey, privateKey),
                "cannot generate Ed25519 key pair"
        );
        return Ed25519KeyPair.from(publicKey, privateKey);
    }

    public static KeyPair generateKeyPairFromSeed(byte[] seed) {
        checkValidLength(seed, SEED_LENGTH);
        final byte[] publicKey = new byte[PUBLIC_KEY_LENGTH];
        final byte[] privateKey = new byte[PRIVATE_KEY_LENGTH];
        call(
                () -> Sodium.crypto_sign_ed25519_seed_keypair(
                        publicKey,
                        privateKey,
                        seed
                ),
                "cannot generate Ed25519 key pair from seed"
        );
        return Ed25519KeyPair.from(publicKey, privateKey);
    }

    public static KeyPair getKeyPair(byte[] privateKey) {
        checkValidLength(privateKey, PRIVATE_KEY_LENGTH);
        final byte[] publicKey = new byte[PUBLIC_KEY_LENGTH];
        call(
                () -> Sodium.crypto_sign_ed25519_sk_to_pk(
                        publicKey,
                        privateKey
                ),
                "cannot derive public key from private key"
        );
        return Ed25519KeyPair.from(publicKey, privateKey);
    }

    public static byte[] getSeed(byte[] privateKey) {
        checkValidLength(privateKey, PRIVATE_KEY_LENGTH);
        final byte[] seed = new byte[SEED_LENGTH];
        call(
                () -> Sodium.crypto_sign_ed25519_sk_to_seed(seed, privateKey),
                "cannot get seed from private key"
        );
        return seed;
    }

    public static String getSeed(String hexPrivateKey) {
        checkValidHex(hexPrivateKey);
        return HEX.encode(getSeed(HEX.decode(hexPrivateKey)));
    }

    public static byte[] sign(byte[] message, byte[] privateKey) {
        checkValidLength(privateKey, PRIVATE_KEY_LENGTH);
        final byte[] signature = new byte[SIG_LENGTH];
        call(
                () -> Sodium.crypto_sign_ed25519_detached(
                        signature,
                        new int[]{signature.length},
                        message,
                        message.length,
                        privateKey
                ),
                "cannot sign message"
        );
        return signature;
    }

    public static String sign(String hexMessage, String hexPrivateKey) {
        checkValidHex(hexMessage);
        checkValidHex(hexPrivateKey);
        return HEX.encode(sign(
                HEX.decode(hexMessage),
                HEX.decode(hexPrivateKey)
        ));
    }

    public static boolean verify(
            byte[] signature,
            byte[] message,
            byte[] publicKey
    ) {
        checkValidLength(publicKey, PUBLIC_KEY_LENGTH);
        checkValidLength(signature, SIG_LENGTH);
        return Sodium.crypto_sign_ed25519_verify_detached(
                signature,
                message,
                message.length,
                publicKey
        ) == 0;
    }

    public static boolean verify(
            String hexSignature,
            String hexMessage,
            String hexPublicKey
    ) {
        checkValidHex(hexSignature);
        checkValidHex(hexMessage);
        checkValidHex(hexPublicKey);
        return verify(
                HEX.decode(hexSignature),
                HEX.decode(hexMessage),
                HEX.decode(hexPublicKey)
        );
    }


}
