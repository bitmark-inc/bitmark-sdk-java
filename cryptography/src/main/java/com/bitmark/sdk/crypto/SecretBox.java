package com.bitmark.sdk.crypto;

import java.util.Arrays;

import static com.bitmark.sdk.crypto.libsodium.LibSodium.sodium;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class SecretBox {

    private SecretBox() {
    }

    public static byte[] generateSecretBox(byte[] msg, byte[] nonce, byte[] key) {

        final int msgLength = msg.length;
        final byte[] m = new byte[key.length + msgLength];
        final byte[] c = new byte[m.length];

        System.arraycopy(msg, 0, m, m.length - msgLength, msgLength);

        sodium().crypto_secretbox(c, m, m.length, nonce, key);
        return Arrays.copyOfRange(c, msgLength, c.length);
    }
}
