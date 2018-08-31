import config.GlobalConfiguration;

/**
 * @author Hieu Pham
 * @since 8/22/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class BitmarkSDK {

    public static void init(String apiToken) {
        init(GlobalConfiguration.builder().withApiToken(apiToken));
    }

    public static void init(GlobalConfiguration.Builder builder) {
        validate();
        GlobalConfiguration.createInstance(builder);
    }

    private static void validate() {
        if (GlobalConfiguration.isInitialized()) throw new UnsupportedOperationException("You " +
                "must call BitmarkSDK.init() once");
    }

}

