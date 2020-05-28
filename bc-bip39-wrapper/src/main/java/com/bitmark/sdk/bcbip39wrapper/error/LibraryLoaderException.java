/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.bcbip39wrapper.error;

public class LibraryLoaderException extends RuntimeException {

    public LibraryLoaderException(String message) {
        super(message);
    }
}
