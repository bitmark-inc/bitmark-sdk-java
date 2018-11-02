package apiservice.utils;

import apiservice.utils.callback.Callable1;
import apiservice.utils.callback.Callable2;
import apiservice.utils.callback.Callback1;
import apiservice.utils.callback.Callback2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * @author Hieu Pham
 * @since 9/25/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Awaitility {

    private Awaitility() {

    }

    public static <T> T await(Callable1<T> callable) throws Throwable {
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
        try {
            return completableFuture.join();
        } catch (CompletionException e) {
            throw e.getCause();
        }

    }

    public static <T1, T2> Pair<T1, T2> await(Callable2<T1, T2> callable) throws Throwable {
        final CompletableFuture<Pair<T1, T2>> completableFuture = new CompletableFuture<>();
        callable.call(new Callback2<T1, T2>() {
            @Override
            public void onSuccess(T1 t1, T2 t2) {
                completableFuture.complete(new Pair<>(t1, t2));
            }

            @Override
            public void onError(Throwable throwable) {
                completableFuture.completeExceptionally(throwable);
            }
        });
        try {
            return completableFuture.join();
        } catch (CompletionException e) {
            throw e.getCause();
        }

    }
}
