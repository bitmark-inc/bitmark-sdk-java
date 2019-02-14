package com.bitmark.sdk.authentication;

import android.app.Activity;
import android.support.annotation.NonNull;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
abstract class AbsAuthenticator implements Authenticator {

    protected AuthenticationCallback callback;

    protected Activity activity;

    AbsAuthenticator(@NonNull Activity activity, @NonNull AuthenticationCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

}
