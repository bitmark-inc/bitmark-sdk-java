package com.bitmark.cryptography.test.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Hieu Pham
 * @since 12/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public class TestUtils {

    public static void assertNotZeroBytes(byte[] input) {
        assertNotNull(input);
        boolean isAllZero = true;
        for (byte item : input) {
            isAllZero &= item == 0;
        }
        assertFalse(isAllZero);
    }
}
