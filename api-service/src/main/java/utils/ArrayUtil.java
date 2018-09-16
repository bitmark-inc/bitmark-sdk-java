package utils;

import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Hieu Pham
 * @since 9/4/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class ArrayUtil {

    private ArrayUtil() {
    }

    public static int toPrimitiveInteger(byte[] value) {
        return new BigInteger(value).intValue();
    }

    public static byte[] toByteArray(int value) throws BufferUnderflowException {
        return value == 0 ? new byte[]{0} :
                minimize(ByteBuffer.allocate(Integer.SIZE / 8).putInt(value).array());
    }

    public static byte[] toByteArray(int[] value) {
        final int length = value.length;
        final byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) value[i];
        }
        return result;
    }

    public static boolean[] concat(boolean[] a, boolean[] b) {
        if (a != null && b != null) {
            boolean[] rv = new boolean[a.length + b.length];

            System.arraycopy(a, 0, rv, 0, a.length);
            System.arraycopy(b, 0, rv, a.length, b.length);

            return rv;
        } else if (b != null) {
            return clone(b);
        } else {
            return clone(a);
        }
    }

    public static int[] concat(int[] a, int[] b) {
        if (a != null && b != null) {
            int[] rv = new int[a.length + b.length];

            System.arraycopy(a, 0, rv, 0, a.length);
            System.arraycopy(b, 0, rv, a.length, b.length);

            return rv;
        } else if (b != null) {
            return clone(b);
        } else {
            return clone(a);
        }
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
            System.arraycopy(d, 0, rv, a.length + b.length + c.length, d.length);

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

    public static boolean equals(byte[] a, byte[] b) {
        return Arrays.equals(a, b);
    }

    public static int[] clone(int[] data) {
        if (data == null) {
            return null;
        }
        int[] copy = new int[data.length];

        System.arraycopy(data, 0, copy, 0, data.length);

        return copy;
    }

    public static byte[] clone(byte[] data) {
        if (data == null) {
            return null;
        }
        byte[] copy = new byte[data.length];

        System.arraycopy(data, 0, copy, 0, data.length);

        return copy;
    }

    public static boolean[] clone(boolean[] data) {
        if (data == null) {
            return null;
        }
        boolean[] copy = new boolean[data.length];

        System.arraycopy(data, 0, copy, 0, data.length);

        return copy;
    }

    public static byte[] slice(byte[] data, int from, int to) {
        return Arrays.copyOfRange(data, from, to);
    }

    public static boolean[] slice(boolean[] data, int from, int to) {
        return Arrays.copyOfRange(data, from, to);
    }

    public static byte[] minimize(byte[] data) {
        int i = 0;
        for (byte b : data) {
            if (b == 0) i++;
            else break;
        }
        return slice(data, i, data.length);
    }

    public static boolean contains(String[] source, String examined) {
        for (String item : source) {
            if (item.equals(examined)) return true;
        }
        return false;
    }

    public static boolean contains(String[] source, String[] examined) {
        for (String item : examined) {
            if (!contains(source, item)) return false;
        }
        return true;
    }

    public static int indexOf(String[] source, String examined) {
        for (int i = 0; i < source.length; i++) {
            if (source[i].equals(examined)) return i;
        }
        return -1;
    }

    public static int[] toUInt(byte[] data) {
        final int length = data.length;
        final int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = data[i] & 0xFF;
        }
        return result;
    }

    public static boolean isDuplicate(String[] input) {
        final List<String> nonDupArray = new ArrayList<>();
        for (String item : input) {
            if (nonDupArray.contains(item)) return true;
            else nonDupArray.add(item);
        }
        return false;
    }

    public static boolean isDuplicate(int[] input) {
        final List<Integer> nonDupArray = new ArrayList<>();
        for (int item : input) {
            if (nonDupArray.contains(item)) return true;
            else nonDupArray.add(item);
        }
        return false;
    }

    public static boolean isPositive(int[] input) {
        for (int item : input) {
            if (item < 0) return false;
        }
        return true;
    }
}
