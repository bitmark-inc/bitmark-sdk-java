package com.bitmark.cryptography.error;

/**
 * @author Hieu Pham
 * @since 11/1/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class UnexpectedException extends RuntimeException {

    public UnexpectedException(String message){
        super(message);
    }

    public UnexpectedException(Throwable cause) {
        super(cause);
    }
}
