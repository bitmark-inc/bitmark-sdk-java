package com.bitmark.sdk.utils;

/**
 * @author Hieu Pham
 * @since 10/4/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public abstract class Task<T, R> {

    private final T[] params;

    public Task(T... params) {
        this.params = params;
    }

    public R run() throws Throwable {
        return run(this.params);
    }

    public abstract R run(T... params) throws Throwable;
}
