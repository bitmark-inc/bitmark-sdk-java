/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.middleware;

import okhttp3.Request;
import okhttp3.Response;

public interface HttpObserver {

    void onRequest(Request request);

    void onRespond(Response response);
}
