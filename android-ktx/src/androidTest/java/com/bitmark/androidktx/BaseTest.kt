/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.androidktx

import com.bitmark.apiservice.configuration.GlobalConfiguration
import com.bitmark.apiservice.configuration.Network
import com.bitmark.sdk.features.BitmarkSDK
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.rules.Timeout
import java.util.concurrent.TimeUnit


abstract class BaseTest {

    @Rule
    @JvmField
    val globalTimeout = Timeout(60, TimeUnit.SECONDS)

    companion object {

        @BeforeClass
        @JvmStatic
        fun beforeAll() {
            if (!BitmarkSDK.isInitialized()) {
                BitmarkSDK.init(GlobalConfiguration.builder()
                                        .withApiToken("bmk-lljpzkhqdkzmblhg")
                                        .withNetwork(Network.TEST_NET)
                                        .withLogLevel(HttpLoggingInterceptor.Level.BODY)
                )
            }
        }
    }
}