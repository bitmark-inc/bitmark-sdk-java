package com.bitmark.sdk.test.integrationtest;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import org.junit.BeforeClass;
import com.bitmark.sdk.features.BitmarkSDK;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public abstract class BaseTest {

    @BeforeClass
    public static void beforeAll() {
        if (!BitmarkSDK.isInitialized())
            BitmarkSDK.init(GlobalConfiguration.builder().withApiToken("bmk-lljpzkhqdkzmblhg").withNetwork(Network.TEST_NET));

    }
}
