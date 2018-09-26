package com.bitmark.sdk.test.unittest;

import com.bitmark.sdk.service.configuration.GlobalConfiguration;
import com.bitmark.sdk.service.configuration.Network;
import com.bitmark.sdk.features.BitmarkSDK;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * @author Hieu Pham
 * @since 9/12/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public abstract class BaseTest {

    @BeforeAll
    public static void beforeAll() {
        BitmarkSDK.init(GlobalConfiguration.builder().withApiToken("dummy-token").withNetwork(Network.TEST_NET));

    }

    @AfterAll
    public static void afterAll() {
        BitmarkSDK.destroy();
    }

}
