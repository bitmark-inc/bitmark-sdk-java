package com.bitmark.sdk.utils.error;

/**
 * @author Hieu Pham
 * @since 9/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class UnexpectedException extends RuntimeException {

    public UnexpectedException(Throwable throwable) {
        super(throwable.getMessage());
    }

    public UnexpectedException(String message) {
        super(message);
    }
}
