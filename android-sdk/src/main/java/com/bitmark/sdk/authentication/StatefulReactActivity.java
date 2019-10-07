package com.bitmark.sdk.authentication;

import android.content.Intent;
import com.bitmark.sdk.utils.annotation.Experimental;
import com.facebook.react.ReactActivity;

/**
 * @author Hieu Pham
 * @since 12/11/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
@Experimental
public abstract class StatefulReactActivity extends ReactActivity {

    private ActivityListener stateListener;

    public void setStateListener(ActivityListener stateListener) {
        this.stateListener = stateListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (stateListener != null) {
            stateListener.onActivityResult(requestCode, resultCode, data);
        }
    }
}
