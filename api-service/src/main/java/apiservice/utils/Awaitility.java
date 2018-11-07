package apiservice.utils;

import apiservice.utils.callback.Call;
import apiservice.utils.callback.Callable1;
import apiservice.utils.callback.Callback1;

import java.util.concurrent.TimeoutException;

/**
 * @author Hieu Pham
 * @since 9/25/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Awaitility {

    private static final int TIMEOUT = 10000;

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
        final BackgroundJobScheduler scheduler = new BackgroundJobScheduler();
        scheduler.execute(() -> {
            try {
                data.setValue(call.call());
            } catch (Throwable throwable) {
                error.setValue(throwable);
            }
        });

        final long timeStart = System.currentTimeMillis();
        while (data.getValue() == null && error.getValue() == null) {
            long timeSpent = System.currentTimeMillis() - timeStart;
            if (timeSpent >= TIMEOUT)
                throw new TimeoutException("Timeout after " + timeSpent + " ms");
        }

        T value = data.getValue();
        if (value != null) return value;
        else throw error.getValue();
    }

    public static <T> T await(Callable1<T> callable) throws Throwable {
        return await(callable, null);
    }

    public static <T> T await(Callable1<T> callable, Long timeout) throws Throwable {
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
            if (timeSpent >= timeout)
                throw new TimeoutException("Timeout after " + timeSpent + " ms");
        }

        T value = data.getValue();
        if (value != null) return value;
        else throw error.getValue();

    }
}
