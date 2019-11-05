/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.configuration;

public enum KeyPart {

    PRIVATE_KEY(0x00), PUBLIC_KEY(0x01);

    private int value;

    KeyPart(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
