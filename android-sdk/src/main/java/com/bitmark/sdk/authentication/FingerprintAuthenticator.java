package com.bitmark.sdk.authentication;

import android.animation.Animator;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bitmark.sdk.R;
import com.bitmark.sdk.authentication.error.AuthenticationRequiredException;
import com.bitmark.sdk.authentication.error.HardwareNotSupportedException;

import javax.crypto.Cipher;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.bitmark.sdk.authentication.FingerprintAuthenticator.FingerprintDialog.State.*;
import static com.bitmark.sdk.authentication.Provider.FINGERPRINT;
import static com.bitmark.sdk.utils.CommonUtils.switchOnMain;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
class FingerprintAuthenticator extends AbsAuthenticator {

    FingerprintAuthenticator(Context context) {
        super(context);
    }

    private static FingerprintManager getFingerPrintManager(Context context) {
        return (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);
    }

    @Override
    public void authenticate(
            Activity activity,
            String title,
            String description,
            Cipher cipher,
            AuthenticationCallback callback
    ) {
        switchOnMain(
                () -> new FingerprintAuthenticationHandler(
                        activity,
                        title,
                        description,
                        callback
                )
                        .authenticate(cipher));
    }

    @Override
    public boolean isHardwareDetected() {
        FingerprintManager manager = getFingerPrintManager(context);
        return manager != null && manager.isHardwareDetected();
    }

    @Override
    public boolean isEnrolled() {
        FingerprintManager manager = getFingerPrintManager(context);
        return manager != null && manager.hasEnrolledFingerprints();
    }

    @Override
    public void checkAvailability()
            throws
            AuthenticationRequiredException,
            HardwareNotSupportedException {
        if (!isHardwareDetected()) {
            throw new HardwareNotSupportedException(FINGERPRINT);
        }
        if (!isEnrolled()) {
            throw new AuthenticationRequiredException(FINGERPRINT);
        }
    }

    private static class FingerprintAuthenticationHandler
            extends FingerprintManager.AuthenticationCallback {

        private final FingerprintDialog dialog;
        private final CancellationSignal cancellationSignal = new CancellationSignal();
        private Activity activity;
        private AuthenticationCallback callback;

        @MainThread
        FingerprintAuthenticationHandler(
                @NonNull Activity activity,
                String title,
                String description,
                @NonNull AuthenticationCallback callback
        ) {
            this.activity = activity;
            this.callback = callback;
            dialog = new FingerprintDialog(activity, title, description,
                    v -> cancellationSignal.cancel()
            );
        }

        void authenticate(Cipher cipher) {
            final FingerprintManager manager =
                    (FingerprintManager) activity.getApplicationContext()
                            .getSystemService(FINGERPRINT_SERVICE);
            if (manager == null) {
                callback.onError("Cannot get FingerprintManager");
            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog.show();
                manager.authenticate(
                        cipher != null ? new FingerprintManager.CryptoObject(
                                cipher) : null,
                        cancellationSignal, 0, this,
                        new Handler(Looper.getMainLooper())
                );
            }
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            dialog.updateView(SUCCESS, null, (dialog) -> callback
                    .onSucceeded(result.getCryptoObject().getCipher()));
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            dialog.updateView(FAILED, null, null);
            callback.onFailed();
        }

        @Override
        public void onAuthenticationError(
                int errorCode,
                CharSequence errString
        ) {
            super.onAuthenticationError(errorCode, errString);
            if (errorCode == FingerprintManager.FINGERPRINT_ERROR_CANCELED) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                callback.onCancelled();
            } else {
                dialog.updateView(ERROR, errString.toString(), null);
                callback.onError(errString.toString());
            }
        }
    }

    static class FingerprintDialog extends AppCompatDialog {

        private TextView textStatus;
        private ImageView imageStatus;
        private String title;
        private String description;
        private View.OnClickListener onCancelListener;

        FingerprintDialog(
                @NonNull
                        Activity activity, String title, String description,
                @NonNull
                        View.OnClickListener onCancelListener
        ) {
            super(activity);
            this.title = title;
            this.description = description;
            this.onCancelListener = onCancelListener;
        }

        @Override
        public void onCreate(
                @Nullable
                        Bundle savedInstanceState
        ) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setCancelable(false);
            setContentView(R.layout.dialog_fingerprint);

            Window window = getWindow();
            if (window == null) {
                return;
            }
            window.setLayout(MATCH_PARENT, MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setWindowAnimations(R.style.Animation_AppCompat_Dialog_BottomUp);

            TextView textTitle = findViewById(R.id.text_title);
            TextView textDes = findViewById(R.id.text_description);
            Button buttonCancel = findViewById(R.id.button_cancel);
            textStatus = findViewById(R.id.text_status);
            imageStatus = findViewById(R.id.image_status);
            Animation tween = AnimationUtils.loadAnimation(
                    getContext(),
                    R.anim.tween
            );
            imageStatus.startAnimation(tween);
            textTitle.setText(title);
            textDes.setText(description);
            buttonCancel.setOnClickListener(v -> {
                dismiss();
                onCancelListener.onClick(v);
            });
        }

        void updateView(
                State state,
                @Nullable
                        String error,
                @Nullable
                        DialogInterface.OnDismissListener dismissListener
        ) {
            Context context = getContext();
            imageStatus.clearAnimation();
            switch (state) {
                case SUCCESS:
                    imageStatus.setImageResource(R.drawable.ic_success_circle);
                    imageStatus.animate()
                            .rotationBy(360)
                            .setInterpolator(new OvershootInterpolator(1.4f))
                            .setDuration(500)
                            .setListener(
                                    new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            dismiss();
                                            if (dismissListener != null) {
                                                dismissListener
                                                        .onDismiss(
                                                                FingerprintDialog.this);
                                            }
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });
                    textStatus.setText(context.getString(R.string.fingerprint_is_recognized));
                    textStatus.setTextColor(
                            ContextCompat.getColor(
                                    context,
                                    R.color.persian_green
                            ));
                    break;

                case FAILED:
                    imageStatus.setImageResource(R.drawable.ic_failed_circle);
                    imageStatus.animate()
                            .rotationBy(360)
                            .setInterpolator(new OvershootInterpolator(1.4f))
                            .setDuration(500);
                    textStatus.setText(context.getString(R.string.fingerprint_not_recognized));
                    textStatus
                            .setTextColor(ContextCompat.getColor(
                                    context,
                                    R.color.pomegranate
                            ));
                    break;

                case ERROR:
                    imageStatus.setImageResource(R.drawable.ic_failed_circle);
                    imageStatus.animate()
                            .rotationBy(360)
                            .setInterpolator(new OvershootInterpolator(1.4f))
                            .setDuration(500);
                    textStatus.setText(error);
                    textStatus
                            .setTextColor(ContextCompat.getColor(
                                    context,
                                    R.color.pomegranate
                            ));
                    break;

                case INITIALIZE:
                default:
                    imageStatus.setImageResource(R.drawable.ic_fingerprint_circle);
                    textStatus.setText(context.getString(R.string.touch_the_fingerprint));
                    textStatus.setTextColor(ContextCompat.getColor(
                            context,
                            R.color.lynch
                    ));
                    break;
            }
        }

        enum State {
            INITIALIZE,
            SUCCESS,
            FAILED,
            ERROR
        }
    }
}
