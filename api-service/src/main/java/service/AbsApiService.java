package service;

import okhttp3.Headers;
import okhttp3.Response;
import service.params.Params;
import service.params.query.QueryParams;
import utils.callback.Callback;
import utils.callback.Callback1;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hieu Pham
 * @since 9/4/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public abstract class AbsApiService {

    private HttpClient client;

    private List<Callback> callbacks = new ArrayList<>();

    protected AbsApiService(String apiToken) {
        this.client = new HttpClientImpl(apiToken);
    }

    protected void subscribe(Callback callback) {
        if (!callbacks.contains(callback)) callbacks.add(callback);
    }

    public void unsubscribe(Callback callback) {
        callbacks.remove(callback);
    }

    protected void getAsync(String path, Callback1<Response> callback) {
        client.getAsync(path, callback);
        subscribe(callback);
    }

    protected void getAsync(String path, QueryParams params, Callback1<Response> callback) {
        client.getAsync(path, params, callback);
        subscribe(callback);
    }

    protected void postAsync(String path, Params params, Callback1<Response> callback) {
        postAsync(path, null, params, callback);
    }

    protected void postAsync(String path, Headers headers, Params params,
                             Callback1<Response> callback) {
        client.postAsync(path, headers, params, callback);
        subscribe(callback);
    }

    protected void patchAsync(String path, Params params, Callback1<Response> callback) {
        patchAsync(path, null, params, callback);
    }

    protected void patchAsync(String path, Headers headers, Params params,
                              Callback1<Response> callback) {
        client.patchAsync(path, headers, params, callback);
        subscribe(callback);
    }

    protected void deleteAsync(String path, Callback1<Response> callback) {
        client.deleteAsync(path, callback);
        subscribe(callback);
    }

    protected void deleteAsync(String path, Params params, Callback1<Response> callback) {
        client.deleteAsync(path, params, callback);
        subscribe(callback);
    }

}
