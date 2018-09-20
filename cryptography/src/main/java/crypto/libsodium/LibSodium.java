package crypto.libsodium;

import jnr.ffi.LibraryLoader;
import jnr.ffi.Platform;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class LibSodium {

    private static final String LIBRARY_NAME = libraryName();

    private static volatile Sodium SODIUM_INSTANCE;

    private LibSodium() {
    }

    public static Sodium sodium() {
        if (SODIUM_INSTANCE == null) {
            synchronized (LibSodium.class) {
                if (SODIUM_INSTANCE == null) {
                    final String path = "../cryptography/src/main/java/libs";
                    SODIUM_INSTANCE =
                            LibraryLoader.create(Sodium.class).search(path).load(LIBRARY_NAME);
                }
            }
        }
        return SODIUM_INSTANCE;
    }

    private static String libraryName() {
        switch (Platform.getNativePlatform().getOS()) {
            case WINDOWS:
                return "libsodium";
            default:
                return "sodium";
        }
    }
}
