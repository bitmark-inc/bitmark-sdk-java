package com.bitmark.sdk.authentication.error;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public class FingerprintException extends Exception {

    public FingerprintException(Throwable throwable) {
        super(throwable);
    }

    public FingerprintException(String message) {
        super(message);
    }
}
