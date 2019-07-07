package com.bitmark.sdk.features;

import com.bitmark.apiservice.ApiService;
import com.bitmark.apiservice.params.RegistrationParams;
import com.bitmark.apiservice.params.query.AssetQueryBuilder;
import com.bitmark.apiservice.response.RegistrationResponse;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.record.AssetRecord;

import java.util.List;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Asset {

    public static void register(RegistrationParams params,
                                Callback1<RegistrationResponse> callback) {
        new ApiService().registerAsset(params, callback);
    }

    public static void get(String assetId, Callback1<AssetRecord> callback) {
        new ApiService().getAsset(assetId, callback);
    }

    public static void list(AssetQueryBuilder builder, Callback1<List<AssetRecord>> callback) {
        new ApiService().listAssets(builder.build(), callback);
    }

}
