package sdk.features;

import apiservice.configuration.GlobalConfiguration;
import apiservice.utils.annotation.MainThread;

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

    public static boolean isInitialized() {
        return GlobalConfiguration.isInitialized();
    }

    private static void validate() {
        if (GlobalConfiguration.isInitialized()) throw new UnsupportedOperationException("You " +
                "can only call BitmarkSDK.init() once");
    }

}

