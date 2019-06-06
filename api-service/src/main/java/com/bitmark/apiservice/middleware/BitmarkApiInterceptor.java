package com.bitmark.apiservice.middleware;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author Hieu Pham
 * @since 8/30/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

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
                .addHeader("Accept-Encoding","*")
                .addHeader("User-Agent", String.format("%s %s %s", "bitmark-java-sdk", System.getProperty("os.name"), System.getProperty("java.version")))
                .build();
        return chain.proceed(request);
    }
}
