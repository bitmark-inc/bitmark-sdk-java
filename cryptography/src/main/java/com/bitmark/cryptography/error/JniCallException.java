package com.bitmark.cryptography.error;

/**
 * @author Hieu Pham
 * @since 7/6/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class JniCallException extends RuntimeException {

    public JniCallException(String message) {
        super(message);
    }
}
