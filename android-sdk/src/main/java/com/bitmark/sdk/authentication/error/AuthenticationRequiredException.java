package com.bitmark.sdk.authentication.error;

import com.bitmark.sdk.authentication.Provider;

/**
 * @author Hieu Pham
 * @since 2/13/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class AuthenticationRequiredException extends Exception {

    private Provider provider;

    public AuthenticationRequiredException(Provider provider) {
        this.provider = provider;
    }

    public Provider getProvider() {
        return provider;
    }
}
