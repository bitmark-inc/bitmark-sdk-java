package cryptography.crypto.encoder;

import cryptography.error.ValidateException;

import static cryptography.utils.Validator.*;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Hex implements Encoder {

    public static final Hex HEX = new Hex();

    private static final char[] DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private Hex() {
    }

    @Override
    public byte[] decode(String value) throws ValidateException {
        checkValidHex(value);
        if (value.length() % 2 == 1) value = "0" + value;
        final char[] data = value.toCharArray();
        final int len = data.length;

        final byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    @Override
    public String encode(byte[] data) throws ValidateException {
        checkNonNull(data);
        checkValid(() -> data.length > 0);
        final int len = data.length;
        final char[] out = new char[len << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < len; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return new String(out);
    }

    private static int toDigit(final char ch, final int index) {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }
}