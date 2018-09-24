package com.bitmark.sdk.crypto.encoder;

import com.bitmark.sdk.error.ValidateException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.bitmark.sdk.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Raw implements Encoder {

    public static final Raw RAW = new Raw();

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private Raw() {
    }

    @Override
    public byte[] decode(final String data) throws ValidateException {
        checkValid(() -> data != null && !data.isEmpty());
        return data.getBytes(CHARSET);
    }

    @Override
    public String encode(byte[] data) throws ValidateException {
        checkValid(() -> data != null && data.length > 0);
        return new String(data, CHARSET);
    }
}
