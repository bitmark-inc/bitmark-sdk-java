/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.crypto;

import com.bitmark.cryptography.crypto.key.BoxKeyPair;
import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.cryptography.crypto.sodium.Sodium;

import static com.bitmark.cryptography.utils.ArrayUtils.prependZeros;
import static com.bitmark.cryptography.utils.ArrayUtils.removeZeros;
import static com.bitmark.cryptography.utils.JniUtils.call;
import static com.bitmark.cryptography.utils.Validator.checkNonNull;
import static com.bitmark.cryptography.utils.Validator.checkValidLength;

public class Box {

    public static final int PUB_KEY_BYTE_LENGTH = Sodium.crypto_box_publickeybytes();

    public static final int PRIVATE_KEY_BYTE_LENGTH = Sodium.crypto_box_secretkeybytes();

    public static final int NONCE_BYTE_LENGTH = Sodium.crypto_box_noncebytes();

    private Box() {
    }

    public static KeyPair generateKeyPair() {
        byte[] publicKey = new byte[PUB_KEY_BYTE_LENGTH];
        byte[] privateKey = new byte[PRIVATE_KEY_BYTE_LENGTH];
        call(
                () -> Sodium.crypto_box_keypair(publicKey, privateKey),
                "cannot generate key pair"
        );
        return BoxKeyPair.from(publicKey, privateKey);
    }

    public static KeyPair generateKeyPair(byte[] privateKey) {
        checkValidLength(privateKey, PRIVATE_KEY_BYTE_LENGTH);
        byte[] publicKey = new byte[PUB_KEY_BYTE_LENGTH];
        call(
                () -> Sodium.crypto_scalarmult_base(publicKey, privateKey),
                "cannot generate key pair from private key"
        );
        return BoxKeyPair.from(publicKey, privateKey);
    }

    public static byte[] box(
            byte[] message,
            byte[] nonce,
            byte[] publicKey,
            byte[] privateKey
    ) {

        checkNonNull(message);
        checkNonNull(publicKey);
        checkNonNull(privateKey);
        checkNonNull(nonce);
        checkValidLength(nonce, NONCE_BYTE_LENGTH);
        checkValidLength(publicKey, PUB_KEY_BYTE_LENGTH);
        checkValidLength(privateKey, PRIVATE_KEY_BYTE_LENGTH);

        byte[] msg = prependZeros(32, message);
        byte[] cipher = new byte[msg.length];
        call(
                () -> Sodium
                        .crypto_box(cipher, msg, msg.length, nonce,
                                publicKey, privateKey
                        ),
                "Cannot box"
        );
        return removeZeros(16, cipher);
    }

    public static byte[] unbox(
            byte[] cipher,
            byte[] nonce,
            byte[] publicKey,
            byte[] privateKey
    ) {

        checkNonNull(cipher);
        checkNonNull(publicKey);
        checkNonNull(privateKey);
        checkNonNull(nonce);
        checkValidLength(nonce, NONCE_BYTE_LENGTH);
        checkValidLength(publicKey, PUB_KEY_BYTE_LENGTH);
        checkValidLength(privateKey, PRIVATE_KEY_BYTE_LENGTH);

        byte[] cp = prependZeros(16, cipher);
        byte[] message = new byte[cp.length];
        call(
                () -> Sodium.crypto_box_open(message, cp, cp.length, nonce,
                        publicKey, privateKey
                ),
                "Cannot open box"
        );
        return removeZeros(32, message);
    }

    public static byte[] boxCurve25519XSalsa20Poly1305(
            byte[] message,
            byte[] nonce,
            byte[] publicKey,
            byte[] privateKey
    ) {

        checkNonNull(message);
        checkNonNull(publicKey);
        checkNonNull(privateKey);
        checkNonNull(nonce);
        checkValidLength(nonce, NONCE_BYTE_LENGTH);
        checkValidLength(publicKey, PUB_KEY_BYTE_LENGTH);
        checkValidLength(privateKey, PRIVATE_KEY_BYTE_LENGTH);

        byte[] msg = prependZeros(32, message);
        byte[] cipher = new byte[msg.length];
        call(
                () -> Sodium
                        .crypto_box_curve25519xsalsa20poly1305(
                                cipher,
                                msg,
                                msg.length,
                                nonce,
                                publicKey,
                                privateKey
                        ),
                "Cannot box"
        );
        return removeZeros(16, cipher);
    }

    public static byte[] unboxCurve25519XSalsa20Poly1305(
            byte[] cipher,
            byte[] nonce,
            byte[] publicKey,
            byte[] privateKey
    ) {

        checkNonNull(cipher);
        checkNonNull(publicKey);
        checkNonNull(privateKey);
        checkNonNull(nonce);
        checkValidLength(nonce, NONCE_BYTE_LENGTH);
        checkValidLength(publicKey, PUB_KEY_BYTE_LENGTH);
        checkValidLength(privateKey, PRIVATE_KEY_BYTE_LENGTH);

        byte[] cp = prependZeros(16, cipher);
        byte[] message = new byte[cp.length];
        call(
                () -> Sodium.crypto_box_curve25519xsalsa20poly1305_open(
                        message,
                        cp,
                        cp.length,
                        nonce,
                        publicKey,
                        privateKey
                ),
                "Cannot open box"
        );
        return removeZeros(32, message);
    }
}
