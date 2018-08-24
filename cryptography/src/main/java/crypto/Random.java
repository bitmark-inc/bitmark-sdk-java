package crypto;

import java.security.SecureRandom;

import static crypto.libsodium.LibSodium.sodium;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Random {

    private Random() {
    }

    public static byte[] random(int size) {
        final byte[] bytes = new byte[size];
        sodium().randombytes(bytes, size);
        return bytes;
    }

    public static byte[] secureRandom(int size) {
        final byte[] bytes = new byte[size];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
}
