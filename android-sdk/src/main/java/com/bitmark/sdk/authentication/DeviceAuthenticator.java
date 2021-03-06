/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.authentication;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bitmark.sdk.authentication.error.AuthenticationRequiredException;
import com.bitmark.sdk.authentication.error.HardwareNotSupportedException;

import javax.crypto.Cipher;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;
import static com.bitmark.sdk.authentication.Provider.DEVICE;
import static com.bitmark.sdk.utils.DeviceUtils.isAboveM;

class DeviceAuthenticator extends AbsAuthenticator {

    DeviceAuthenticator(Context context) {
        super(context);
    }

    @Override
    public void authenticate(
            Activity activity,
            String title,
            String description,
            Cipher cipher,
            AuthenticationCallback callback
    ) {
        new DeviceAuthenticationHandler(
                activity,
                title,
                description,
                callback
        ).authenticate(cipher);
    }

    @Override
    public boolean isHardwareDetected() {
        // all devices support PIN/Pattern/Password
        return true;
    }

    @Override
    public boolean isEnrolled() {
        KeyguardManager keyguardManager =
                (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
        if (keyguardManager == null) {
            return false;
        }
        return isAboveM()
               ? keyguardManager.isDeviceSecure()
               : keyguardManager.isKeyguardSecure();
    }

    @Override
    public void checkAvailability()
            throws
            AuthenticationRequiredException,
            HardwareNotSupportedException {
        if (!isHardwareDetected()) {
            throw new HardwareNotSupportedException(DEVICE);
        }
        if (!isEnrolled()) {
            throw new AuthenticationRequiredException(DEVICE);
        }
    }


    private static class DeviceAuthenticationHandler
            implements ActivityListener {

        private static final int REQUEST_CODE = 0x99;

        private AuthenticationCallback callback;

        private Activity activity;

        private Cipher cipher;

        private String title;

        private String description;

        DeviceAuthenticationHandler(
                @NonNull Activity activity,
                String title,
                String description,
                @NonNull AuthenticationCallback callback
        ) {
            this.activity = activity;
            this.title = title;
            this.description = description;
            this.callback = callback;
            if (this.activity instanceof StatefulActivity) {
                ((StatefulActivity) this.activity).setStateListener(this);
            }
            if (this.activity instanceof StatefulReactActivity) {
                ((StatefulReactActivity) this.activity).setStateListener(this);
            }
        }

        void authenticate(Cipher cipher) {
            this.cipher = cipher;
            final Context context = activity.getApplicationContext();
            KeyguardManager keyguardManager = (KeyguardManager) context
                    .getSystemService(KEYGUARD_SERVICE);
            if (keyguardManager == null) {
                throw new UnsupportedOperationException(
                        "Not support this kind of service " + KEYGUARD_SERVICE);
            }
            Intent intent = keyguardManager
                    .createConfirmDeviceCredentialIntent(title, description);
            activity.startActivityForResult(intent, REQUEST_CODE, null);
        }

        @Override
        public void onActivityResult(
                int requestCode,
                int resultCode,
                @Nullable Intent data
        ) {
            if (requestCode == REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    callback.onSucceeded(cipher);
                } else if (resultCode == RESULT_CANCELED) {
                    callback.onCancelled();
                }
            }
        }
    }
}
