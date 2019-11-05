/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.utils;

public class Data<T> {

    private volatile T value;

    public synchronized void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
