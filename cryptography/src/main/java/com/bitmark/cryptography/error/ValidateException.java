/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.error;

public class ValidateException extends RuntimeException {

    public ValidateException(String message) {
        super(message);
    }

    public static final class NullValueError extends ValidateException {

        public static final String ORIGIN_MESSAGE = "The value is null";

        public NullValueError() {
            super(ORIGIN_MESSAGE);
        }
    }

    public static final class InvalidHex extends ValidateException {

        public static final String ORIGIN_MESSAGE = "Invalid hexadecimal";

        public InvalidHex() {
            super(ORIGIN_MESSAGE);
        }

    }

    public static final class InvalidString extends ValidateException {

        public static final String ORIGIN_MESSAGE = "Invalid String";

        public InvalidString() {
            super(ORIGIN_MESSAGE);
        }

    }

    public static final class InvalidLength extends ValidateException {

        public static final String ORIGIN_MESSAGE = "Invalid length";

        public InvalidLength() {
            super(ORIGIN_MESSAGE);
        }

        public InvalidLength(int expected, int actual) {
            super(ORIGIN_MESSAGE + ". Expected is " + expected + " but actual is " + actual);
        }

    }

    public static class InvalidCharacter extends ValidateException {

        public final char character;

        public final int position;

        public InvalidCharacter(char character, int position) {
            super("Invalid character '" + Character.toString(character) + "' at position " + position);
            this.character = character;
            this.position = position;
        }
    }

}
