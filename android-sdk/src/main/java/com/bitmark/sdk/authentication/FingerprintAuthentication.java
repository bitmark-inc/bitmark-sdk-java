package com.bitmark.sdk.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.*;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bitmark.sdk.R;

import javax.crypto.Cipher;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.bitmark.sdk.authentication.FingerprintAuthentication.FingerprintDialog.State.*;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
class FingerprintAuthentication extends AbsAuthentication {

    FingerprintAuthentication(Activity activity, AuthenticationCallback callback) {
        super(activity, callback);
    }

    @Override
    public void authenticate(@NonNull Cipher cipher) {
        new Handler(Looper.getMainLooper())
                .post(() -> new FingerprintAuthenticationHandler(activity, callback)
                        .authenticate(cipher));
    }

    private static FingerprintManager getFingerPrintManager(Context context) {
        return (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);
    }

    static boolean isFingerprintHardwareDetected(Context context) {
        FingerprintManager manager = getFingerPrintManager(context);
        return manager != null && manager.isHardwareDetected();
    }

    static boolean isFingerprintEnrolled(Context context) {
        FingerprintManager manager = getFingerPrintManager(context);
        return manager != null && manager.hasEnrolledFingerprints();
    }

    private static class FingerprintAuthenticationHandler
            extends FingerprintManager.AuthenticationCallback {

        private Activity activity;

        private AuthenticationCallback callback;

        private final FingerprintDialog dialog;

        private final CancellationSignal cancellationSignal = new CancellationSignal();

        @MainThread
        FingerprintAuthenticationHandler(@NonNull Activity activity,
                                         @NonNull AuthenticationCallback callback) {
            this.activity = activity;
            this.callback = callback;
            dialog = new FingerprintDialog(activity, v -> {
                cancellationSignal.cancel();
                callback.onCancelled();
            });
        }

        void authenticate(Cipher cipher) {
            if (cipher == null) return;
            final FingerprintManager manager =
                    (FingerprintManager) activity.getApplicationContext()
                                                 .getSystemService(FINGERPRINT_SERVICE);
            if (manager == null) callback.onError("Cannot get FingerprintManager");
            else {
                if (dialog.isShowing()) dialog.dismiss();
                dialog.show();
                manager.authenticate(new FingerprintManager.CryptoObject(cipher),
                                     cancellationSignal, 0, this, null);
            }
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            dialog.updateView(SUCCESS, (dialog) -> callback
                    .onSucceeded(result.getCryptoObject().getCipher()));
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            dialog.updateView(FAILED, null);
            callback.onFailed();
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            dialog.updateView(ERROR, null);
            callback.onError(errString.toString());
        }
    }

    static class FingerprintDialog extends AppCompatDialog {

        enum State {
            INITIALIZE, SUCCESS, FAILED, ERROR
        }

        private TextView textStatus;

        private ImageView imageStatus;

        private View.OnClickListener onCancelListener;

        FingerprintDialog(@NonNull Activity activity,
                          @NonNull View.OnClickListener onCancelListener) {
            super(activity);
            this.onCancelListener = onCancelListener;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setCancelable(false);
            setContentView(R.layout.dialog_fingerprint);

            Window window = getWindow();
            if (window == null) return;
            window.setLayout(MATCH_PARENT, MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setWindowAnimations(R.style.Animation_AppCompat_Dialog_BottomUp);

            TextView textTitle = findViewById(R.id.text_title);
            TextView textDes = findViewById(R.id.text_description);
            Button buttonCancel = findViewById(R.id.button_cancel);
            textStatus = findViewById(R.id.text_status);
            imageStatus = findViewById(R.id.image_status);
            textTitle.setText(getContext().getString(R.string.identification));
            textDes.setText(getContext().getString(R.string.application_need_to_authenticate_you));
            buttonCancel.setOnClickListener(v -> {
                dismiss();
                onCancelListener.onClick(v);
            });
        }

        void updateView(State state, @Nullable DialogInterface.OnDismissListener dismissListener) {
            Context context = getContext();
            switch (state) {
                case SUCCESS:
                    imageStatus.setImageResource(R.drawable.ic_success_circle);
                    textStatus.setText(context.getString(R.string.fingerprint_is_recognized));
                    textStatus.setTextColor(ContextCompat.getColor(context, R.color.persian_green));
                    break;

                case FAILED:
                    imageStatus.setImageResource(R.drawable.ic_failed_circle);
                    textStatus.setText(context.getString(R.string.fingerprint_not_recognized));
                    textStatus.setTextColor(ContextCompat.getColor(context, R.color.pomegranate));
                    break;

                case ERROR:
                    imageStatus.setImageResource(R.drawable.ic_failed_circle);
                    textStatus.setText(context.getString(R.string.something_error));
                    textStatus.setTextColor(ContextCompat.getColor(context, R.color.pomegranate));
                    break;

                case INITIALIZE:
                default:
                    imageStatus.setImageResource(R.drawable.ic_fingerprint_circle);
                    textStatus.setText(context.getString(R.string.touch_the_fingerprint));
                    textStatus.setTextColor(ContextCompat.getColor(context, R.color.lynch));
                    break;
            }
            if (state == SUCCESS && dismissListener != null) {
                new Handler().postDelayed(() -> {
                    dismiss();
                    dismissListener.onDismiss(FingerprintDialog.this);
                }, 500);
            }
        }
    }
}
