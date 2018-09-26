package com.bitmark.sdk.features;

import com.bitmark.sdk.utils.annotation.MainThread;
import com.bitmark.sdk.utils.annotation.VisibleForTesting;
import com.bitmark.sdk.service.configuration.GlobalConfiguration;

/**
 * @author Hieu Pham
 * @since 8/22/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class BitmarkSDK {

    @MainThread
    public static void init(String apiToken) {
        init(GlobalConfiguration.builder().withApiToken(apiToken));
    }

    @MainThread
    public static void init(GlobalConfiguration.Builder builder) {
        validate();
        GlobalConfiguration.createInstance(builder);
    }

    @VisibleForTesting
    public static void destroy() {
        GlobalConfiguration.destroy();
    }

    private static void validate() {
        if (GlobalConfiguration.isInitialized()) throw new UnsupportedOperationException("You " +
                "must call BitmarkSDK.init() once");
    }

}

