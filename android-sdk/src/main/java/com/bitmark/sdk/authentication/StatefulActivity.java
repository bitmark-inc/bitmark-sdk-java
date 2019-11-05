/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.authentication;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class StatefulActivity extends AppCompatActivity {

    private ActivityListener stateListener;

    public void setStateListener(ActivityListener stateListener) {
        this.stateListener = stateListener;
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            @Nullable Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        if (stateListener != null) {
            stateListener.onActivityResult(requestCode, resultCode, data);
        }
    }
}
