/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.authentication.error;

import com.bitmark.sdk.authentication.Provider;

public class HardwareNotSupportedException extends Exception {

    private Provider provider;

    public HardwareNotSupportedException(Provider provider) {
        this.provider = provider;
    }

    public Provider getProvider() {
        return provider;
    }
}
