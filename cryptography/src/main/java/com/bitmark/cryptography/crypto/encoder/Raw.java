/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.crypto.encoder;

import com.bitmark.cryptography.error.UnexpectedException;

import java.io.UnsupportedEncodingException;

import static com.bitmark.cryptography.utils.Validator.checkValid;

public class Raw implements Encoder {

    public static final Raw RAW = new Raw();

    private static final String CHARSET = "UTF-8";

    private Raw() {
    }

    @Override
    public byte[] decode(final String data) {
        checkValid(() -> data != null && !data.isEmpty());
        try {
            return data.getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new UnexpectedException(e.getCause());
        }
    }

    @Override
    public String encode(byte[] data) {
        checkValid(() -> data != null && data.length > 0);
        try {
            return new String(data, CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new UnexpectedException(e.getCause());
        }
    }
}
