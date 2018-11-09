package com.bitmark.apiservice.configuration;

/**
 * @author Hieu Pham
 * @since 9/26/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public enum KeyPart {

    PRIVATE_KEY(0x00), PUBLIC_KEY(0x01);

    private int value;

    KeyPart(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
