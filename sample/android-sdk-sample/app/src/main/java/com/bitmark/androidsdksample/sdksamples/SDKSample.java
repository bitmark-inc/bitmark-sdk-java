package com.bitmark.androidsdksample.sdksamples;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import com.bitmark.sdk.features.BitmarkSDK;

public class SDKSample {
    public static void initialize(String apiToken, Network networkMode) {
        if (!BitmarkSDK.isInitialized()) {
            final GlobalConfiguration.Builder builder = GlobalConfiguration.builder().withApiToken(apiToken).withNetwork(networkMode);
            BitmarkSDK.init(builder);
        }
    }
}
