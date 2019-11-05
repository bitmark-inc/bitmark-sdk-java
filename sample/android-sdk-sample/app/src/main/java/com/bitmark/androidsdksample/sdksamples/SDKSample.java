/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.androidsdksample.sdksamples;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import com.bitmark.sdk.features.BitmarkSDK;

public class SDKSample {
    public static void initialize(String apiToken, Network networkMode) {
        if (!BitmarkSDK.isInitialized()) {
            final GlobalConfiguration.Builder builder = GlobalConfiguration.builder().withApiToken(apiToken).withNetwork(networkMode);
            BitmarkSDK.init(builder);
        }
    }
}
