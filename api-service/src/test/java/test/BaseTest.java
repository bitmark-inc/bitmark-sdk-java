package test;

import config.GlobalConfiguration;
import config.Network;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * @author Hieu Pham
 * @since 9/5/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class BaseTest {

    @BeforeEach
    public void beforeEach() {
        GlobalConfiguration.createInstance(GlobalConfiguration.builder().withNetwork(Network.TEST_NET).withApiToken("DummyApiToken"));
    }

    @AfterEach
    public void afterEach() {
        GlobalConfiguration.destroy();
    }
}
