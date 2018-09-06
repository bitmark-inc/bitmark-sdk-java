package service;

import okhttp3.Response;
import service.params.Params;
import service.params.query.QueryParams;
import utils.callback.Callback1;

/**
 * @author Hieu Pham
 * @since 8/30/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface HttpClient {

    void getAsync(String path, Callback1<Response> callback);

    void getAsync(String path, QueryParams params, Callback1<Response> callback);

    void postAsync(String path, Params params, Callback1<Response> callback);

    void patchAsync(String path, Params params, Callback1<Response> callback);

    void deleteAsync(String path, Callback1<Response> callback);

    void deleteAsync(String path, Params params, Callback1<Response> callback);
}
