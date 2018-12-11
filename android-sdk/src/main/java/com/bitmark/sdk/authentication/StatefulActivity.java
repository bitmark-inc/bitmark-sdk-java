package com.bitmark.sdk.authentication;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public abstract class StatefulActivity extends AppCompatActivity {

    private ActivityListener stateListener;

    public void setStateListener(ActivityListener stateListener) {
        this.stateListener = stateListener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (stateListener != null) stateListener.onActivityResult(requestCode, resultCode, data);
    }
}
