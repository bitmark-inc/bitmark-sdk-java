package com.bitmark.sdk.test.utils;

import com.bitmark.sdk.utils.callback.Callback1;

/**
 * @author Hieu Pham
 * @since 9/14/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface Callable<T> {

    void call(Callback1<T> callback);
}
