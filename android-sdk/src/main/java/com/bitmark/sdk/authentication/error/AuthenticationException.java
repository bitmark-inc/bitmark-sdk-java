package com.bitmark.sdk.authentication.error;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
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
