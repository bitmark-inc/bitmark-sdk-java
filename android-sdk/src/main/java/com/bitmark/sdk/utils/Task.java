/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.utils;

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
