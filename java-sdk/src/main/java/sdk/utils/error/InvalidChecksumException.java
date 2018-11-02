package sdk.utils.error;

import cryptography.error.ValidateException;

import static cryptography.crypto.encoder.Hex.HEX;

/**
 * @author Hieu Pham
 * @since 8/27/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class InvalidChecksumException extends ValidateException {

    public InvalidChecksumException(String message) {
        super(message);
    }

    public InvalidChecksumException(byte[] actual, byte[] expected) {
        this("Invalid checksum. Expected is " + HEX.encode(expected) + " but actual is " + HEX.encode(actual));
    }
}
