package sdk.utils.error;

import cryptography.error.ValidateException;

import static cryptography.crypto.encoder.Hex.HEX;

/**
 * @author Hieu Pham
 * @since 8/28/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class InvalidSeedException extends ValidateException {

    public InvalidSeedException(String message) {
        super(message);
    }

    public static class InvalidMagicNumberException extends InvalidSeedException {

        public InvalidMagicNumberException(String message) {
            super(message);
        }

        public InvalidMagicNumberException(byte[] actual, byte[] expected) {
            this("Invalid magic number. Expected is " + HEX.encode(expected) + " but actual is " + HEX.encode(actual));
        }
    }

    public static class InvalidVersionException extends InvalidSeedException {

        public InvalidVersionException(String message) {
            super(message);
        }

        public InvalidVersionException(int actual, int expected) {
            this("Invalid version. Expected is " + expected + " but actual is " + actual);
        }

        public InvalidVersionException(byte[] actual, byte[] expected) {
            this("Invalid version. Expected is " + HEX.encode(expected) + " but actual is " + HEX.encode(actual));
        }
    }
}
