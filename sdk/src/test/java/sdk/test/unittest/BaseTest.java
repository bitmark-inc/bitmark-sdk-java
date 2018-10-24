package sdk.test.unittest;

import apiservice.configuration.GlobalConfiguration;
import apiservice.configuration.Network;
import org.junit.jupiter.api.BeforeAll;
import sdk.features.BitmarkSDK;

/**
 * @author Hieu Pham
 * @since 9/12/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public abstract class BaseTest {

    @BeforeAll
    public static void beforeAll() {
        if (!BitmarkSDK.isInitialized())
            BitmarkSDK.init(GlobalConfiguration.builder().withApiToken("bmk-lljpzkhqdkzmblhg").withNetwork(Network.TEST_NET));

    }

}