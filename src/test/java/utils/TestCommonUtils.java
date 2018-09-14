package utils;

import utils.callback.Callback1;

import java.util.concurrent.CompletableFuture;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TestCommonUtils {

    private TestCommonUtils() {
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

    public interface Callable<T> {

        void call(Callback1<T> callback);
    }
}
