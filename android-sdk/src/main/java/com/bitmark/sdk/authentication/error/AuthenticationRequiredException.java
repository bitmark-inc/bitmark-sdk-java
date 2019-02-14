package com.bitmark.sdk.authentication.error;

/**
 * @author Hieu Pham
 * @since 2/13/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class AuthenticationRequiredException extends Exception {

    public static final String BIOMETRIC = "biometric";

    public static final String FINGERPRINT = "fingerprint";

    public static final String PASSWORD = "password";

    private String type;

    public AuthenticationRequiredException(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
