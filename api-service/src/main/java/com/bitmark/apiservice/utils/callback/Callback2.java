/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.utils.callback;

public interface Callback2<T1, T2> extends Callback {

    void onSuccess(T1 t1, T2 t2);

    void onError(Throwable throwable);
}
