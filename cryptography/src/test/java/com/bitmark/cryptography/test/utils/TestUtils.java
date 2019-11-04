/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.test.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
