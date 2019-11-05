/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.utils;

import com.bitmark.cryptography.error.JniCallException;

public class JniUtils {

    private JniUtils() {
    }

    public static void call(Callable<Integer> callable, String errorMessage) {
        if (callable.call() != 0) {
            throw new JniCallException(errorMessage);
        }
    }
}
