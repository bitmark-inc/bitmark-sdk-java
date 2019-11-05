/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.test.unittest;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.BeforeAll;
import com.bitmark.sdk.features.BitmarkSDK;

public abstract class BaseTest {

    protected static final Network NETWORK = Network.TEST_NET;

    @BeforeAll
    public static void beforeAll() {
        if (!BitmarkSDK.isInitialized())
            BitmarkSDK.init(GlobalConfiguration.builder().withApiToken("bmk-lljpzkhqdkzmblhg").withNetwork(NETWORK).withLogLevel(HttpLoggingInterceptor.Level.NONE));

    }

}
