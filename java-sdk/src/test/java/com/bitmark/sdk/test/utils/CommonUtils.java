/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.test.utils;

import java.security.SecureRandom;

public class CommonUtils {

    private static final String ALPHANUMERIC =
            "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    private CommonUtils() {
    }

    public static String randomString(int length) {
        StringBuilder builder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        int bound = ALPHANUMERIC.length() - 1;
        for (int i = 0; i < length; i++) {
            builder.append(ALPHANUMERIC.charAt(random.nextInt(bound)));
        }
        return builder.toString();
    }
}
