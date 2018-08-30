import okhttp3.Response;
import params.Params;
import params.query.QueryParams;
import utils.callback.Callback1;

/**
 * @author Hieu Pham
 * @since 8/30/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface HttpClient {

    void get(String url, Callback1<Response> callback);

    void get(String url, QueryParams params, Callback1<Response> callback);

    void post(String url, Params params, Callback1<Response> callback);

    void patch(String url, Params params, Callback1<Response> callback);

    void delete(String url, Callback1<Response> callback);

    void delete(String url, Params params, Callback1<Response> callback);
}
