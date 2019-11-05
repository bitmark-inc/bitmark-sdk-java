/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.utils.error;

import com.bitmark.cryptography.error.ValidateException;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;

public class InvalidSeedException extends ValidateException {

    public InvalidSeedException(String message) {
        super(message);
    }

    public static class InvalidMagicNumberException
            extends InvalidSeedException {

        public InvalidMagicNumberException(String message) {
            super(message);
        }

        public InvalidMagicNumberException(byte[] actual, byte[] expected) {
            this("Invalid magic number. Expected is " + HEX.encode(expected) + " but actual is " + HEX
                    .encode(actual));
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
            this("Invalid version. Expected is " + HEX.encode(expected) + " but actual is " + HEX
                    .encode(actual));
        }
    }
}
