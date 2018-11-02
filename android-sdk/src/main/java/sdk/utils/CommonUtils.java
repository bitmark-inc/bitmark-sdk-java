package sdk.utils;

import android.os.Handler;
import android.os.Looper;
import apiservice.utils.callback.Callback1;

/**
 * @author Hieu Pham
 * @since 10/31/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

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
