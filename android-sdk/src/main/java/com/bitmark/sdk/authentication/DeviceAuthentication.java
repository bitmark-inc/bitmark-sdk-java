package com.bitmark.sdk.authentication;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bitmark.sdk.R;

import javax.crypto.Cipher;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;
import static com.bitmark.sdk.utils.DeviceUtils.isAboveM;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
class DeviceAuthentication extends AbsAuthentication {

    DeviceAuthentication(Activity activity, AuthenticationCallback callback) {
        super(activity, callback);
    }

    public static boolean isDeviceSecured(Context context) {
        KeyguardManager keyguardManager =
                (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
        if (keyguardManager == null) return false;
        return isAboveM() ? keyguardManager.isDeviceSecure() : keyguardManager
                .isKeyguardSecure();
    }

    @Override
    public void authenticate(@NonNull Cipher cipher) {
        new DeviceAuthenticationHandler(activity, callback).authenticate(cipher);
    }

    private static class DeviceAuthenticationHandler implements ActivityListener {

        private static final int REQUEST_CODE = 0x99;

        private AuthenticationCallback callback;

        private Activity activity;

        private Cipher cipher;

        DeviceAuthenticationHandler(@NonNull Activity activity,
                                    @NonNull AuthenticationCallback callback) {
            this.activity = activity;
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
            if (keyguardManager == null)
                throw new UnsupportedOperationException(
                        "Not support this kind of service " + KEYGUARD_SERVICE);
            Intent intent = keyguardManager
                    .createConfirmDeviceCredentialIntent(
                            context.getString(R.string.identification),
                            context.getString(R.string.application_need_to_authenticate_you));
            activity.startActivityForResult(intent, REQUEST_CODE, null);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if (requestCode == REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    callback.onSucceeded(cipher);
                } else if (resultCode == RESULT_CANCELED) callback.onCancelled();
            }
        }
    }
}
