package com.bitmark.sdk.authentication;

import android.app.Activity;
import android.support.annotation.NonNull;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
abstract class AbsAuthentication implements Authentication {

    protected AuthenticationCallback callback;

    protected Activity activity;

    AbsAuthentication(@NonNull Activity activity, @NonNull AuthenticationCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }
}
