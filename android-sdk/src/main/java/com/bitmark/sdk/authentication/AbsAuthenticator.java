package com.bitmark.sdk.authentication;

import android.content.Context;

/**
 * @author Hieu Pham
 * @since 2019-08-26
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
abstract class AbsAuthenticator implements Authenticator {

    protected final Context context;

    AbsAuthenticator(Context context) {
        this.context = context;
    }
}
