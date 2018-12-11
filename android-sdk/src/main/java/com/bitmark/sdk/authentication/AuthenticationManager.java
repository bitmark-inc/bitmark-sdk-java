package com.bitmark.sdk.authentication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import com.bitmark.sdk.authentication.error.BiometricException;
import com.bitmark.sdk.authentication.error.FingerprintException;
import com.bitmark.sdk.authentication.error.KeyGuardRequiredException;

import static com.bitmark.sdk.authentication.DeviceAuthentication.isDeviceSecured;
import static com.bitmark.sdk.utils.DeviceUtils.isAboveM;
import static com.bitmark.sdk.utils.DeviceUtils.isAboveP;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public class AuthenticationManager {

    private Activity activity;

    private AuthenticationCallback callback;

    public AuthenticationManager(@NonNull Activity activity,
                                 @NonNull AuthenticationCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @SuppressLint("NewApi")
    public Authentication getAuthentication()
            throws FingerprintException, KeyGuardRequiredException, BiometricException {
        if (isAboveP() && BiometricAuthentication.isFingerprintHardwareDetected(activity)) {
            return getBiometricAuthentication(activity, callback);
        } else if (isAboveM() &&
                   FingerprintAuthentication.isFingerprintHardwareDetected(activity)) {
            // Using fingerprint authentication
            return getFingerprintAuthentication(activity, callback);
        } else {
            // Using device authentication (PIN/Password) only, not fingerprint
            return getDeviceAuthentication(activity, callback);
        }

    }

    private Authentication getDeviceAuthentication(Activity activity,
                                                   AuthenticationCallback callback)
            throws KeyGuardRequiredException {
        if (!isDeviceSecured(activity)) throw new KeyGuardRequiredException();
        return new DeviceAuthentication(activity, callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Authentication getFingerprintAuthentication(Activity activity,
                                                        AuthenticationCallback callback)
            throws FingerprintException {
        if (FingerprintAuthentication.isFingerprintEnrolled(activity)) {
            return new FingerprintAuthentication(activity, callback);
        } else {
            // Has not setup fingerprint
            throw new FingerprintException("Has not setup at least one fingerprint");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private Authentication getBiometricAuthentication(Activity activity,
                                                      AuthenticationCallback callback)
            throws BiometricException {
        if (BiometricAuthentication.isFingerprintEnrolled(activity)) {
            // Has setup at least 1 fingerprint
            return new BiometricAuthentication(activity, callback);
        } else {
            // Has not setup fingerprint
            throw new BiometricException("Has not setup at least one fingerprint");
        }
    }

    @SuppressLint("NewApi")
    public static boolean isHardwareDetected(Context context) {
        return (isAboveP() && BiometricAuthentication.isFingerprintHardwareDetected(context)) ||
               (isAboveM() && FingerprintAuthentication.isFingerprintHardwareDetected(context));
    }
}
