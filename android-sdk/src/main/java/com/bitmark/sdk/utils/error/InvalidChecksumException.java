/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.utils.error;

import com.bitmark.cryptography.error.ValidateException;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;

public class InvalidChecksumException extends ValidateException {

    public InvalidChecksumException(String message) {
        super(message);
    }

    public InvalidChecksumException(byte[] actual, byte[] expected) {
        this("Invalid checksum. Expected is " + HEX.encode(expected) + " but actual is " + HEX
                .encode(actual));
    }
}
