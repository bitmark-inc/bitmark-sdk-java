package com.bitmark.apiservice.utils.callback;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface Callback2<T1, T2> extends Callback {

    void onSuccess(T1 t1, T2 t2);

    void onError(Throwable throwable);
}
