/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.crypto;

import com.bitmark.cryptography.crypto.sodium.Sodium;

import static com.bitmark.cryptography.utils.JniUtils.call;
import static com.bitmark.cryptography.utils.Validator.checkNonNull;
import static com.bitmark.cryptography.utils.Validator.checkValidLength;

public class Chacha20Poly1305 {

    public static final int IETF_ABYTE_LENGTH = Sodium.crypto_aead_chacha20poly1305_ietf_abytes();

    public static final int IETF_KEY_BYTE_LENGTH =
            Sodium.crypto_aead_chacha20poly1305_ietf_keybytes();

    public static final int IETF_NPUBBYTE_LENGTH =
            Sodium.crypto_aead_chacha20poly1305_ietf_npubbytes();

    private Chacha20Poly1305() {
    }

    public static byte[] generateIetfKey() {
        final byte[] key = new byte[IETF_KEY_BYTE_LENGTH];
        Sodium.crypto_aead_chacha20poly1305_ietf_keygen(key);
        return key;
    }

    public static byte[] aeadIetfEncrypt(
            byte[] message,
            byte[] additionalData,
            byte[] nonce,
            byte[] key
    ) {

        checkValidLength(key, IETF_KEY_BYTE_LENGTH);
        checkNonNull(nonce);

        byte[] additionalDataBytes = additionalData == null
                                     ? new byte[0]
                                     : additionalData;

        final int messageLength = message.length;
        final int additionalDataLength = additionalDataBytes.length;
        final int cipherLength = messageLength + IETF_ABYTE_LENGTH;

        final byte[] cipherBytes = new byte[cipherLength];

        call(
                () -> Sodium.crypto_aead_chacha20poly1305_ietf_encrypt(
                        cipherBytes,
                        new int[0],
                        message,
                        messageLength,
                        additionalDataBytes,
                        additionalDataLength,
                        new byte[0],
                        nonce,
                        key
                ),
                "Cannot encrypt message"
        );
        return cipherBytes;
    }

    public static byte[] aeadIetfDecrypt(
            byte[] cipherBytes,
            byte[] additionalData,
            byte[] nonce,
            byte[] key
    ) {

        checkValidLength(key, IETF_KEY_BYTE_LENGTH);
        checkNonNull(nonce);

        byte[] additionalDataBytes = additionalData == null
                                     ? new byte[0]
                                     : additionalData;

        final int cipherBytesLength = cipherBytes.length;
        final int additionalDataLength = additionalDataBytes.length;
        final int decryptedBytesLength = cipherBytesLength - IETF_ABYTE_LENGTH;
        final byte[] decryptedBytes = new byte[decryptedBytesLength];

        call(
                () -> Sodium.crypto_aead_chacha20poly1305_ietf_decrypt(
                        decryptedBytes,
                        new int[1],
                        new byte[0],
                        cipherBytes,
                        cipherBytesLength,
                        additionalDataBytes,
                        additionalDataLength,
                        nonce,
                        key
                ),
                "Cannot decrypt cipher"
        );
        return decryptedBytes;
    }
}
