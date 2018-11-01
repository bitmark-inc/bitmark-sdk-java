package cryptography.crypto.encoder;

import cryptography.error.UnexpectedException;
import cryptography.error.ValidateException;

import java.io.UnsupportedEncodingException;

import static cryptography.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Raw implements Encoder {

    public static final Raw RAW = new Raw();

    private static final String CHARSET = "UTF-8";

    private Raw() {
    }

    @Override
    public byte[] decode(final String data) throws ValidateException {
        checkValid(() -> data != null && !data.isEmpty());
        try {
            return data.getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new UnexpectedException(e.getCause());
        }
    }

    @Override
    public String encode(byte[] data) throws ValidateException {
        checkValid(() -> data != null && data.length > 0);
        try {
            return new String(data, CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new UnexpectedException(e.getCause());
        }
    }
}
