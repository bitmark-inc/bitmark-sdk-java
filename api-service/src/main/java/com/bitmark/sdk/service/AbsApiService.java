package com.bitmark.sdk.service;

import okhttp3.Headers;
import okhttp3.Response;
import com.bitmark.sdk.service.params.Params;
import com.bitmark.sdk.service.params.query.QueryParams;
import com.bitmark.sdk.utils.callback.Callback1;

/**
 * @author Hieu Pham
 * @since 9/4/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public abstract class AbsApiService {

    private HttpClient client;

    protected AbsApiService(String apiToken) {
        this.client = new HttpClientImpl(apiToken);
    }

    protected void getAsync(String path, Callback1<Response> callback) {
        client.getAsync(path, callback);
    }

    protected void getAsync(String path, QueryParams params, Callback1<Response> callback) {
        client.getAsync(path, params, callback);
    }

    protected void postAsync(String path, Params params, Callback1<Response> callback) {
        postAsync(path, null, params, callback);
    }

    protected void postAsync(String path, Headers headers, Params params,
                             Callback1<Response> callback) {
        client.postAsync(path, headers, params, callback);
    }

    protected void patchAsync(String path, Params params, Callback1<Response> callback) {
        patchAsync(path, null, params, callback);
    }

    protected void patchAsync(String path, Headers headers, Params params,
                              Callback1<Response> callback) {
        client.patchAsync(path, headers, params, callback);
    }

    protected void deleteAsync(String path, Callback1<Response> callback) {
        client.deleteAsync(path, callback);
    }

    protected void deleteAsync(String path, Params params, Callback1<Response> callback) {
        client.deleteAsync(path, params, callback);
    }

}