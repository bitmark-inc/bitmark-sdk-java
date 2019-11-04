/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.utils;

import java.util.Arrays;

public class ArrayUtils {

    private ArrayUtils() {
    }

    public static byte[] concat(byte[] a, byte[] b) {
        if (a != null && b != null) {
            byte[] rv = new byte[a.length + b.length];

            System.arraycopy(a, 0, rv, 0, a.length);
            System.arraycopy(b, 0, rv, a.length, b.length);

            return rv;
        } else if (b != null) {
            return clone(b);
        } else {
            return clone(a);
        }
    }

    public static byte[] concat(byte[] a, byte[] b, byte[] c) {
        if (a != null && b != null && c != null) {
            byte[] rv = new byte[a.length + b.length + c.length];

            System.arraycopy(a, 0, rv, 0, a.length);
            System.arraycopy(b, 0, rv, a.length, b.length);
            System.arraycopy(c, 0, rv, a.length + b.length, c.length);

            return rv;
        } else if (a == null) {
            return concat(b, c);
        } else if (b == null) {
            return concat(a, c);
        } else {
            return concat(a, b);
        }
    }

    public static byte[] concat(byte[] a, byte[] b, byte[] c, byte[] d) {
        if (a != null && b != null && c != null && d != null) {
            byte[] rv = new byte[a.length + b.length + c.length + d.length];

            System.arraycopy(a, 0, rv, 0, a.length);
            System.arraycopy(b, 0, rv, a.length, b.length);
            System.arraycopy(c, 0, rv, a.length + b.length, c.length);
            System.arraycopy(
                    d,
                    0,
                    rv,
                    a.length + b.length + c.length,
                    d.length
            );

            return rv;
        } else if (d == null) {
            return concat(a, b, c);
        } else if (c == null) {
            return concat(a, b, d);
        } else if (b == null) {
            return concat(a, c, d);
        } else {
            return concat(b, c, d);
        }
    }

    public static byte[] clone(byte[] data) {
        if (data == null) {
            return null;
        }
        byte[] copy = new byte[data.length];

        System.arraycopy(data, 0, copy, 0, data.length);

        return copy;
    }

    public static byte[] slice(byte[] data, int from, int to) {
        return Arrays.copyOfRange(data, from, to);
    }

    public static byte[] prependZeros(int n, byte[] message) {
        byte[] result = new byte[n + message.length];
        Arrays.fill(result, (byte) 0);
        System.arraycopy(message, 0, result, n, message.length);
        return result;
    }

    public static byte[] removeZeros(int n, byte[] message) {
        return Arrays.copyOfRange(message, n, message.length);
    }
}
