/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.crypto.key;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.utils.Validator.checkValidHex;

public class PublicKey implements Key {

    private final byte[] key;

    public static PublicKey from(byte[] key) {
        return new PublicKey(key);
    }

    public static PublicKey from(String hexKey) {
        checkValidHex(hexKey);
        return new PublicKey(hexKey);
    }

    protected PublicKey(byte[] key) {
        this.key = key;
    }

    protected PublicKey(String hexKey) {
        this(HEX.decode(hexKey));
    }

    public byte[] toBytes() {
        return key;
    }

    @Override
    public int size() {
        return key.length;
    }

    @Override
    public String toString() {
        return HEX.encode(key);
    }
}
