package com.bitmark.cryptography.utils;

import com.bitmark.cryptography.error.UnexpectedException;

/**
 * @author Hieu Pham
 * @since 12/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public class NativeUtils {

    private NativeUtils() {
    }

    public static void call(Callable<Integer> callable, String errorMessage) {
        if (callable.call() != 0) throw new UnexpectedException(errorMessage);
    }
}
