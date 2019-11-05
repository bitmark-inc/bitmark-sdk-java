/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.authentication;

import android.content.Intent;
import com.bitmark.sdk.utils.annotation.Experimental;
import com.facebook.react.ReactActivity;

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
