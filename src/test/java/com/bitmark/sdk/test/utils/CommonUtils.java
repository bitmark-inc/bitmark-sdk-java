package com.bitmark.sdk.test.utils;

import com.bitmark.sdk.utils.callback.Callback1;

import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;

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

    public static <T> T await(Callable<T> callable) {
        final CompletableFuture<T> completableFuture = new CompletableFuture<>();
        callable.call(new Callback1<T>() {
            @Override
            public void onSuccess(T data) {
                completableFuture.complete(data);
            }

            @Override
            public void onError(Throwable throwable) {
                completableFuture.completeExceptionally(throwable);
            }
        });
        return completableFuture.join();

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
