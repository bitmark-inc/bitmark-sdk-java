package com.bitmark.cryptography.crypto.key;


import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.utils.Validator.checkValidHex;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class PrivateKey implements Key {

    private final byte[] key;

    public static PrivateKey from(byte[] key) {
        return new PrivateKey(key);
    }

    public static PrivateKey from(String hexKey) {
        checkValidHex(hexKey);
        return new PrivateKey(hexKey);
    }

    protected PrivateKey(byte[] key) {
        this.key = key;
    }

    protected PrivateKey(String hexKey) {
        this(HEX.decode(hexKey));
    }

    @Override
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
