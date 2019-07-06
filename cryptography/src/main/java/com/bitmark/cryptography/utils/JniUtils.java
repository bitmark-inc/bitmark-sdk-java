package com.bitmark.cryptography.utils;

import com.bitmark.cryptography.error.JniCallException;

/**
 * @author Hieu Pham
 * @since 12/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public class JniUtils {

    private JniUtils() {
    }

    public static void call(Callable<Integer> callable, String errorMessage) {
        if (callable.call() != 0) throw new JniCallException(errorMessage);
    }
}
