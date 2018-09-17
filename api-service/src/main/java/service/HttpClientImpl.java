package service;

import config.GlobalConfiguration;
import config.Network;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import service.middleware.BitmarkApiInterceptor;
import service.params.Params;
import service.params.query.QueryParams;
import utils.callback.Callback1;
import utils.error.HttpException;
import utils.error.NetworkException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static config.SdkConfig.ApiServer.LIVE_NET_ENDPOINT;
import static config.SdkConfig.ApiServer.TEST_NET_ENDPOINT;

/**
 * @author Hieu Pham
 * @since 8/30/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class HttpClientImpl implements HttpClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;

    HttpClientImpl(String apiToken) {
        client = buildClient(apiToken);
    }

    private OkHttpClient buildClient(String apiToken) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // Add Logging
        builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

        // Add Request Interceptor
        builder.addInterceptor(new BitmarkApiInterceptor(apiToken));

        // Configure the timeout
        int timeout = GlobalConfiguration.connectionTimeout();
        builder.readTimeout(timeout, TimeUnit.SECONDS);
        builder.connectTimeout(timeout, TimeUnit.SECONDS);

        return builder.build();
    }

    private String getRequestUrl(String path) {
        return (GlobalConfiguration.network() == Network.TEST_NET ? TEST_NET_ENDPOINT :
                LIVE_NET_ENDPOINT) + path;
    }

    private String getRequestUrl(String path, QueryParams params) {
        return getRequestUrl(path) + "?" + params.toUrlQuery();
    }

    @Override
    public void getAsync(String path, Callback1<Response> callback) {
        getAsync(path, null, callback);
    }

    @Override
    public void getAsync(String path, QueryParams params, Callback1<Response> callback) {
        String requestUrl = params == null ? getRequestUrl(path) : getRequestUrl(path, params);
        Request request = new Request.Builder().url(requestUrl).get().build();
        client.newCall(request).enqueue(wrapCallback(callback));
    }

    @Override
    public void postAsync(String path, Params params, Callback1<Response> callback) {
        postAsync(path, null, params, callback);
    }

    @Override
    public void postAsync(String path, Headers headers, Params params,
                          Callback1<Response> callback) {
        String requestUrl = getRequestUrl(path);
        Request.Builder builder = new Request.Builder()
                .url(requestUrl)
                .post(RequestBody.create(JSON, params.toJson()));
        if (headers != null) builder.headers(headers);
        client.newCall(builder.build()).enqueue(wrapCallback(callback));
    }

    @Override
    public void patchAsync(String path, Params params, Callback1<Response> callback) {
        patchAsync(path, null, params, callback);
    }

    @Override
    public void patchAsync(String path, Headers headers, Params params,
                           Callback1<Response> callback) {
        String requestUrl = getRequestUrl(path);
        Request.Builder builder =
                new Request.Builder().url(requestUrl).patch(RequestBody.create(JSON,
                        params.toJson()));
        if (headers != null) builder.headers(headers);
        client.newCall(builder.build()).enqueue(wrapCallback(callback));
    }

    @Override
    public void deleteAsync(String path, Callback1<Response> callback) {
        deleteAsync(path, null, callback);
    }

    @Override
    public void deleteAsync(String path, Params params, Callback1<Response> callback) {
        String requestUrl = getRequestUrl(path);
        Request.Builder builder = new Request.Builder()
                .url(requestUrl);
        Request request = params == null ? builder.delete().build() :
                builder.delete(RequestBody.create(JSON, params.toJson())).build();
        client.newCall(request).enqueue(wrapCallback(callback));
    }

    private Callback wrapCallback(Callback1<Response> callback) {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(new NetworkException(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful())
                    callback.onSuccess(response);
                else callback.onError(new HttpException(response.code(), response.body().string()));
            }
        };
    }

}
