package com.bitmark.sdk.test.integrationtest.utils;

import java.security.SecureRandom;

/**
 * @author Hieu Pham
 * @since 9/15/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

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
