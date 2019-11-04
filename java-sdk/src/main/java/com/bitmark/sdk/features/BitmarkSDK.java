/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.features;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.utils.annotation.MainThread;

public class BitmarkSDK {

    @MainThread
    public static void init(String apiToken) {
        init(GlobalConfiguration.builder().withApiToken(apiToken));
    }

    @MainThread
    public static void init(GlobalConfiguration.Builder builder) {
        validate();
        GlobalConfiguration.createInstance(builder);
    }

    public static boolean isInitialized() {
        return GlobalConfiguration.isInitialized();
    }

    private static void validate() {
        if (GlobalConfiguration.isInitialized()) {
            throw new UnsupportedOperationException(
                    "You can only call BitmarkSDK.init() once");
        }
    }

}

