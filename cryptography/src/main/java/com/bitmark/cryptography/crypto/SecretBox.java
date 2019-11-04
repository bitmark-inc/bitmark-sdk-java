/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.crypto;

import com.bitmark.cryptography.crypto.sodium.Sodium;

import java.util.Arrays;

import static com.bitmark.cryptography.utils.JniUtils.call;

public class SecretBox {

    private SecretBox() {
    }

    public static byte[] box(byte[] msg, byte[] nonce, byte[] key) {

        final int msgLength = msg.length;
        final byte[] m = new byte[key.length + msgLength];
        final byte[] c = new byte[m.length];

        System.arraycopy(msg, 0, m, m.length - msgLength, msgLength);

        call(
                () -> Sodium.crypto_secretbox(c, m, m.length, nonce, key),
                "cannot box"
        );
        return Arrays.copyOfRange(c, msgLength, c.length);
    }
}
