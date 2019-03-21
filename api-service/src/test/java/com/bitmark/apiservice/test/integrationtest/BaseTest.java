package com.bitmark.apiservice.test.integrationtest;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import org.junit.jupiter.api.BeforeAll;

/**
 * @author Hieu Pham
 * @since 3/21/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */

public abstract class BaseTest {

    @BeforeAll
    public static void beforeAll() {
        if (!GlobalConfiguration.isInitialized())
            GlobalConfiguration.builder().withApiToken("bmk-lljpzkhqdkzmblhg")
                               .withNetwork(Network.TEST_NET).build();

    }
}
