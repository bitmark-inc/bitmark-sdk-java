package test.unittest;

import config.GlobalConfiguration;
import config.Network;
import features.BitmarkSDK;

/**
 * @author Hieu Pham
 * @since 9/12/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public abstract class BaseTest {

    static {
        BitmarkSDK.init(GlobalConfiguration.builder().withApiToken("DummyApiToken").withNetwork(Network.TEST_NET));
    }

}
