/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.test;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseTest {

    @BeforeAll
    public static void beforeAll() {
        if (!GlobalConfiguration.isInitialized())
            GlobalConfiguration.createInstance(GlobalConfiguration.builder().withNetwork(Network.TEST_NET).withApiToken("bmk-lljpzkhqdkzmblhg"));

    }

}
