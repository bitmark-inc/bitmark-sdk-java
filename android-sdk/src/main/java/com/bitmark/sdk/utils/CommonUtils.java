/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.utils;

import android.os.Handler;
import android.os.Looper;
import com.bitmark.apiservice.utils.callback.Callback1;

public class CommonUtils {

    private CommonUtils() {
    }

    public static <T> Callback1<T> wrapCallbackOnMain(Callback1<T> callback) {
        return new Callback1<T>() {
            @Override
            public void onSuccess(T data) {
                switchOnMain(() -> callback.onSuccess(data));
            }

            @Override
            public void onError(Throwable throwable) {
                switchOnMain(() -> callback.onError(throwable));
            }
        };
    }

    public static void switchOnMain(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
