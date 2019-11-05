/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.authentication.error;

public class AuthenticationException extends Exception {

    public enum Type {
        FAILED,

        ERROR,

        CANCELLED
    }

    private Type type;

    public AuthenticationException(Type type) {
        this(type, "");
    }

    public AuthenticationException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
