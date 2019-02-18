package com.bitmark.apiservice.utils;

import com.bitmark.cryptography.crypto.encoder.VarInt;

import static com.bitmark.cryptography.crypto.encoder.Raw.RAW;

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
        final byte[] fromBytes = from.isEmpty() ? new byte[]{} : RAW.decode(from);
        return concat(fromBytes, to);
    }
}
