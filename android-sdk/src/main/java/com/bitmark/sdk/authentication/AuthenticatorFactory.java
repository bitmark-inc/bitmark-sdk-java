package com.bitmark.sdk.authentication;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.bitmark.sdk.authentication.error.AuthenticationRequiredException;

import static com.bitmark.sdk.authentication.DeviceAuthenticator.isDeviceSecured;
import static com.bitmark.sdk.authentication.error.AuthenticationRequiredException.*;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class AuthenticatorFactory {

    public static Authenticator getDeviceAuthenticator(Activity activity, String title,
                                                       String description,
                                                       AuthenticationCallback callback)
            throws AuthenticationRequiredException {
        if (!isDeviceSecured(activity)) throw new AuthenticationRequiredException(PASSWORD);
        return new DeviceAuthenticator(activity, title, description, callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Authenticator getFingerprintAuthenticator(Activity activity, String title,
                                                            String description,
                                                            AuthenticationCallback callback)
            throws AuthenticationRequiredException {
        if (FingerprintAuthenticator.isFingerprintEnrolled(activity)) {
            return new FingerprintAuthenticator(activity, title, description, callback);
        } else {
            // Has not setup fingerprint
            throw new AuthenticationRequiredException(FINGERPRINT);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static Authenticator getBiometricAuthenticator(Activity activity, String title,
                                                          String description,
                                                          AuthenticationCallback callback)
            throws AuthenticationRequiredException {
        if (BiometricAuthenticator.isFingerprintEnrolled(activity)) {
            // Has setup at least 1 fingerprint
            return new BiometricAuthenticator(activity, title, description, callback);
        } else {
            // Has not setup fingerprint
            throw new AuthenticationRequiredException(BIOMETRIC);
        }
    }
}
