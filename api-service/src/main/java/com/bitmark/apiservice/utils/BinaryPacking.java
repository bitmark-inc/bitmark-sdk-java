/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.utils;

import com.bitmark.cryptography.crypto.encoder.VarInt;

import static com.bitmark.cryptography.crypto.encoder.Raw.RAW;

public class BinaryPacking {

    private BinaryPacking() {
    }

    public static byte[] concat(byte[] from, byte[] to) {
        final byte[] encodedLength = VarInt.writeUnsignedVarInt(from.length);
        return ArrayUtil.concat(to, encodedLength, from);
    }

    public static byte[] concat(String from, byte[] to) {
        final byte[] fromBytes = from.isEmpty()
                                 ? new byte[]{}
                                 : RAW.decode(from);
        return concat(fromBytes, to);
    }
}
