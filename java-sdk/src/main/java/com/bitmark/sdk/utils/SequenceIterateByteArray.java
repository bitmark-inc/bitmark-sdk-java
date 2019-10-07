package com.bitmark.sdk.utils;

import static com.bitmark.apiservice.utils.ArrayUtil.slice;
import static com.bitmark.cryptography.utils.Validator.checkNonNull;
import static com.bitmark.cryptography.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 8/27/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

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
