package cryptography.crypto.key;

import cryptography.error.ValidateException;

import static cryptography.crypto.encoder.Hex.HEX;
import static cryptography.utils.Validator.checkValid;
import static cryptography.utils.Validator.checkValidHex;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class PublicKey implements Key {

    private final byte[] key;

    public static PublicKey from(byte[] key) {
        return new PublicKey(key);
    }

    public static PublicKey from(String hexKey) throws ValidateException.InvalidHex {
        checkValidHex(hexKey);
        return new PublicKey(hexKey);
    }

    protected PublicKey(byte[] key) {
        this.key = key;
        checkValid(this::isValid);
    }

    protected PublicKey(String hexKey) {
        this(HEX.decode(hexKey));
    }

    public byte[] toBytes() {
        return key;
    }

    @Override
    public boolean isValid() {
        return key != null && key.length % 32 == 0;
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
