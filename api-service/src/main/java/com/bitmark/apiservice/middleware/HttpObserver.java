package com.bitmark.apiservice.middleware;

import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Hieu Pham
 * @since 2019-09-10
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public interface HttpObserver {

    void onRequest(Request request);

    void onRespond(Response response);
}
