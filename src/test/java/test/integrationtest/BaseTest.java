package test.integrationtest;

import config.GlobalConfiguration;
import config.Network;
import features.BitmarkSDK;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public abstract class BaseTest {

    @BeforeAll
    public static void beforeAll() {
        BitmarkSDK.init(GlobalConfiguration.builder().withApiToken("bmk-lljpzkhqdkzmblhg").withNetwork(Network.TEST_NET));

    }

    @AfterAll
    public static void afterAll() {
        BitmarkSDK.destroy();
    }
}
