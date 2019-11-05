/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.middleware;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class BitmarkApiInterceptor implements Interceptor {

    private String apiToken;

    public BitmarkApiInterceptor(String apiToken) {
        this.apiToken = apiToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
                .addHeader("API-TOKEN", apiToken)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Cache-Control", "no-store")
                .addHeader("Accept-Encoding", "*")
                .addHeader(
                        "User-Agent",
                        String.format(
                                "%s, %s, %s",
                                "bitmark-sdk-java",
                                System.getProperty("os.name"),
                                System.getProperty("java.version")
                        )
                )
                .build();

        HttpObserver observer = GlobalConfiguration.httpObserver();
        if (observer != null) {
            observer.onRequest(request.newBuilder().build());
        }
        Response response = chain.proceed(request);
        if (observer != null) {
            observer.onRespond(response.newBuilder().build());
        }
        return response;
    }
}
