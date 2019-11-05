/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.configuration;

public enum Network {

    LIVE_NET(0x00), TEST_NET(0x01);

    private int value;

    Network(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static Network valueOf(int value) {
        switch (value) {
            case 0x00:
                return LIVE_NET;
            case 0x01:
                return TEST_NET;
            default:
                throw new IllegalArgumentException("Invalid network");
        }
    }

    public static boolean isValid(int value) {
        return value == LIVE_NET.value || value == TEST_NET.value;
    }
}
