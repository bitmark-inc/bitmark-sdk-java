package com.bitmark.sdk.authentication.error;

import com.bitmark.sdk.authentication.Provider;

/**
 * @author Hieu Pham
 * @since 2019-08-26
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class HardwareNotSupportedException extends Exception {

    private Provider provider;

    public HardwareNotSupportedException(Provider provider) {
        this.provider = provider;
    }

    public Provider getProvider() {
        return provider;
    }
}
