package sdk.features;

import apiservice.ApiService;
import apiservice.params.RegistrationParams;
import apiservice.params.query.AssetQueryBuilder;
import apiservice.response.RegistrationResponse;
import apiservice.utils.callback.Callback1;
import apiservice.utils.record.AssetRecord;

import java.util.List;

import static sdk.utils.CommonUtils.wrapCallbackOnMain;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Asset {

    public static void register(RegistrationParams params,
                                Callback1<RegistrationResponse> callback) {
        ApiService.getInstance().registerAsset(params, wrapCallbackOnMain(callback));
    }

    public static void get(String assetId, Callback1<AssetRecord> callback) {
        ApiService.getInstance().getAsset(assetId, wrapCallbackOnMain(callback));
    }

    public static void list(AssetQueryBuilder builder, Callback1<List<AssetRecord>> callback) {
        ApiService.getInstance().listAssets(builder.build(), wrapCallbackOnMain(callback));
    }

}
