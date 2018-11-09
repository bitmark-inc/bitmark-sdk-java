package com.bitmark.apiservice.test;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import org.junit.jupiter.api.BeforeAll;

/**
 * @author Hieu Pham
 * @since 9/5/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public abstract class BaseTest {

    @BeforeAll
    public static void beforeAll() {
        if (!GlobalConfiguration.isInitialized())
            GlobalConfiguration.createInstance(GlobalConfiguration.builder().withNetwork(Network.TEST_NET).withApiToken("DummyApiToken"));

    }

}
