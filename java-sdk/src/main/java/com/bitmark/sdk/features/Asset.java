/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.features;

import com.bitmark.apiservice.ApiService;
import com.bitmark.apiservice.params.RegistrationParams;
import com.bitmark.apiservice.params.query.AssetQueryBuilder;
import com.bitmark.apiservice.response.RegistrationResponse;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.record.AssetRecord;

import java.util.List;

public class Asset {

    public static void register(
            RegistrationParams params,
            Callback1<RegistrationResponse> callback
    ) {
        ApiService.getInstance().registerAsset(params, callback);
    }

    public static void get(String assetId, Callback1<AssetRecord> callback) {
        ApiService.getInstance().getAsset(assetId, callback);
    }

    public static void list(
            AssetQueryBuilder builder,
            Callback1<List<AssetRecord>> callback
    ) {
        ApiService.getInstance().listAssets(builder.build(), callback);
    }

}
