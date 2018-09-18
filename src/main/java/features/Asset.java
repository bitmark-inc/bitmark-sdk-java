package features;

import service.ApiService;
import service.params.RegistrationParams;
import service.params.query.AssetQueryBuilder;
import service.response.RegistrationResponse;
import utils.callback.Callback1;
import utils.record.AssetRecord;

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
        ApiService.getInstance().registerAsset(params, callback);
    }

    public static void get(String assetId, Callback1<AssetRecord> callback) {
        ApiService.getInstance().getAsset(assetId, callback);
    }

    public static void list(AssetQueryBuilder builder, Callback1<List<AssetRecord>> callback) {
        ApiService.getInstance().listAssets(builder.build(), callback);
    }

}
