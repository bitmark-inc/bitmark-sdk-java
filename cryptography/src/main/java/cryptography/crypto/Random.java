package cryptography.crypto;

import cryptography.crypto.sodium.Sodium;

import java.security.SecureRandom;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

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
