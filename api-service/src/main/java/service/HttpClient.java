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

    void get(String path, Callback1<Response> callback);

    void get(String path, QueryParams params, Callback1<Response> callback);

    void post(String path, Params params, Callback1<Response> callback);

    void patch(String path, Params params, Callback1<Response> callback);

    void delete(String path, Callback1<Response> callback);

    void delete(String path, Params params, Callback1<Response> callback);
}
