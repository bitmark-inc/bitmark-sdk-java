/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.utils;

import com.bitmark.apiservice.utils.callback.Call;
import com.bitmark.apiservice.utils.callback.Callable1;
import com.bitmark.apiservice.utils.callback.Callback1;

import java.util.concurrent.TimeoutException;

public class Awaitility {

    private static final int TIMEOUT = 20000;

    private static final BackgroundJobScheduler EXECUTOR = new BackgroundJobScheduler();

    private Awaitility() {
    }

    /**
     * Utility for apply async/await in Java. The task is invoked via {@link Call} will be
     * executed in background thread
     *
     * @param call The interface for invoke the task
     * @param <T>  Type of data is return from task
     * @return Value return from the task
     * @throws Throwable Error is thrown from task
     */
    public static <T> T await(Call<T> call) throws Throwable {
        final Data<T> data = new Data<>();
        final Data<Throwable> error = new Data<>();
        EXECUTOR.execute(() -> {
            try {
                data.setValue(call.call());
            } catch (Throwable throwable) {
                error.setValue(throwable);
            }
        });

        final long timeStart = System.currentTimeMillis();
        while (data.getValue() == null && error.getValue() == null) {
            long timeSpent = System.currentTimeMillis() - timeStart;
            if (timeSpent >= TIMEOUT) {
                throw new TimeoutException("Timeout after " + timeSpent + " ms");
            }
        }

        T value = data.getValue();
        if (value != null) {
            return value;
        } else {
            throw error.getValue();
        }
    }

    public static <T> T await(Callable1<T> callable) throws Throwable {
        return await(callable, null);
    }

    public static <T> T await(Callable1<T> callable, Long timeout)
            throws Throwable {
        Data<T> data = new Data<>();
        Data<Throwable> error = new Data<>();

        callable.call(new Callback1<T>() {
            @Override
            public void onSuccess(T value) {
                data.setValue(value);
            }

            @Override
            public void onError(Throwable throwable) {
                error.setValue(throwable);
            }
        });

        long timeStart = System.currentTimeMillis();
        timeout = timeout == null ? TIMEOUT : timeout;
        while (data.getValue() == null && error.getValue() == null) {
            long timeSpent = System.currentTimeMillis() - timeStart;
            if (timeSpent >= timeout) {
                throw new TimeoutException("Timeout after " + timeSpent + " ms");
            }
        }

        T value = data.getValue();
        if (value != null) {
            return value;
        } else {
            throw error.getValue();
        }

    }
}
