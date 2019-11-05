/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.authentication;

import android.annotation.SuppressLint;
import android.content.Context;

import static com.bitmark.sdk.utils.DeviceUtils.isAboveP;

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
