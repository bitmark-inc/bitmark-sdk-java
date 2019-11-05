/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.crypto;

import com.bitmark.cryptography.crypto.sodium.Sodium;

import java.security.SecureRandom;

public class Random {

    private Random() {
    }

    public static byte[] randomBytes(int size) {
        final byte[] bytes = new byte[size];
        Sodium.randombytes(bytes, size);
        return bytes;
    }

    public static byte[] secureRandomBytes(int size) {
        final byte[] bytes = new byte[size];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    public static int secureRandomInt() {
        return new SecureRandom().nextInt() & Integer.MAX_VALUE;
    }

    public static int[] secureRandomInts(int size) {
        final int[] ints = new int[size];
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < size; i++) {
            ints[i] = random.nextInt() & Integer.MAX_VALUE;
        }
        return ints;
    }
}
