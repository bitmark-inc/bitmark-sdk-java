/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.test.integrationtest;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import com.bitmark.sdk.features.BitmarkSDK;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public abstract class BaseTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeAll() {
        if (!BitmarkSDK.isInitialized()) {
            BitmarkSDK.init(GlobalConfiguration.builder()
                    .withApiToken("bmk-lljpzkhqdkzmblhg")
                    .withNetwork(Network.TEST_NET)
                    .withLogLevel(HttpLoggingInterceptor.Level.BODY));
        }

    }
}
