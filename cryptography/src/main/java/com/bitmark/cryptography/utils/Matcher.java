/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.utils;

import java.util.regex.Pattern;

public class Matcher {

    private static final String HEX_REGEX = "^[a-fA-F0-9]+$";

    public static boolean isHex(String input) {
        return Pattern.compile(HEX_REGEX).matcher(input).matches();
    }
}
