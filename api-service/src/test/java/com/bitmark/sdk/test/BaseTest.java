package com.bitmark.sdk.test;

import com.bitmark.sdk.config.GlobalConfiguration;
import com.bitmark.sdk.config.Network;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * @author Hieu Pham
 * @since 9/5/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class BaseTest {

    @BeforeAll
    public static void beforeAll() {
        GlobalConfiguration.createInstance(GlobalConfiguration.builder().withNetwork(Network.TEST_NET).withApiToken("DummyApiToken"));

    }

    @AfterAll
    public static void afterAll() {
        GlobalConfiguration.destroy();
    }
}
