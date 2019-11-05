/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice;

import okhttp3.Headers;
import okhttp3.Response;
import com.bitmark.apiservice.params.Params;
import com.bitmark.apiservice.params.query.QueryParams;
import com.bitmark.apiservice.utils.callback.Callback1;

public interface HttpClient {

    void getAsync(String path, Callback1<Response> callback);

    void getAsync(
            String path,
            QueryParams params,
            Callback1<Response> callback
    );

    void postAsync(String path, Params params, Callback1<Response> callback);

    void postAsync(
            String path,
            Headers headers,
            Params params,
            Callback1<Response> callback
    );

    void patchAsync(String path, Params params, Callback1<Response> callback);

    void patchAsync(
            String path,
            Headers headers,
            Params params,
            Callback1<Response> callback
    );

    void deleteAsync(String path, Callback1<Response> callback);

    void deleteAsync(String path, Params params, Callback1<Response> callback);
}
