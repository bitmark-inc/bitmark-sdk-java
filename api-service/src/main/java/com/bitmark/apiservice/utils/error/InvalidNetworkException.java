/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.utils.error;

import com.bitmark.cryptography.error.ValidateException;

public class InvalidNetworkException extends ValidateException {

    public InvalidNetworkException(String message) {
        super(message);
    }

    public InvalidNetworkException(int actual) {
        this("Invalid network with value is " + actual);
    }
}
