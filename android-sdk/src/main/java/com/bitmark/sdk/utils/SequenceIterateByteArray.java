/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.utils;

import static com.bitmark.apiservice.utils.ArrayUtil.slice;
import static com.bitmark.cryptography.utils.Validator.checkNonNull;
import static com.bitmark.cryptography.utils.Validator.checkValid;

public class SequenceIterateByteArray {

    private final byte[] bytes;

    private int currentPos;

    public SequenceIterateByteArray(byte[] bytes) {
        checkNonNull(bytes);
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public byte[] next(int length) {
        checkValid(() -> length > 0, "Length mush be greater than 0");
        if (currentPos + length > bytes.length) {
            throw new ArrayIndexOutOfBoundsException(
                    "Length is greater than remaining byte array length");
        }
        int startPos = currentPos;
        int endPos = startPos + length;
        currentPos = endPos;
        return slice(bytes, startPos, endPos);
    }

    public byte[] next() {
        final int remaining = bytes.length - currentPos;
        return next(remaining);
    }
}
