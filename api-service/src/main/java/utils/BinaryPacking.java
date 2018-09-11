package utils;

import crypto.encoder.VarInt;

import static crypto.encoder.Raw.RAW;

/**
 * @author Hieu Pham
 * @since 9/4/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class BinaryPacking {

    private BinaryPacking() {
    }

    public static byte[] concat(byte[] from, byte[] to) {
        final byte[] encodedLength = VarInt.writeUnsignedVarInt(from.length);
        return ArrayUtil.concat(to, encodedLength, from);
    }

    public static byte[] concat(String from, byte[] to) {
        final byte[] fromBytes = RAW.decode(from);
        return concat(fromBytes, to);
    }
}
