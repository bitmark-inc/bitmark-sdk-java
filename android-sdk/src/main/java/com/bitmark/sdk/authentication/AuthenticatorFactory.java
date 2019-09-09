package com.bitmark.sdk.authentication;

import android.annotation.SuppressLint;
import android.content.Context;

import static com.bitmark.sdk.utils.DeviceUtils.isAboveP;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public class AuthenticatorFactory {

    private final Provider provider;

    public static AuthenticatorFactory from(Provider provider) {
        return new AuthenticatorFactory(provider);
    }

    private AuthenticatorFactory(Provider provider) {
        this.provider = provider;
    }

    @SuppressLint("NewApi")
    public Authenticator getAuthenticator(Context context) {
        Authenticator authenticator = null;
        switch (provider) {
            case DEVICE:
                authenticator = new DeviceAuthenticator(context);
                break;
            case FINGERPRINT:
                authenticator = new FingerprintAuthenticator(context);
                break;
            case BIOMETRIC:
                if (isAboveP()) {
                    authenticator = new BiometricAuthenticator(context);
                } else {
                    throw new UnsupportedOperationException(
                            "provider is not supported");
                }
                break;
        }
        return authenticator;
    }
}
