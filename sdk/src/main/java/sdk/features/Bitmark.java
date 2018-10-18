package sdk.features;

import apiservice.ApiService;
import apiservice.params.IssuanceParams;
import apiservice.params.TransferOfferParams;
import apiservice.params.TransferParams;
import apiservice.params.TransferResponseParams;
import apiservice.params.query.BitmarkQueryBuilder;
import apiservice.response.GetBitmarkResponse;
import apiservice.response.GetBitmarksResponse;
import apiservice.utils.callback.Callback1;

import java.util.List;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Bitmark {

    public static void issue(IssuanceParams params, Callback1<List<String>> callback) {
        ApiService.getInstance().issueBitmark(params, callback);
    }

    public static void transfer(TransferParams params, Callback1<String> callback) {
        ApiService.getInstance().transferBitmark(params, callback);
    }

    public static void offer(TransferOfferParams params, Callback1<String> callback) {
        ApiService.getInstance().offerBitmark(params, callback);
    }

    public static void respond(TransferResponseParams params, Callback1<String> callback) {
        ApiService.getInstance().respondBitmarkOffer(params, callback);
    }

    public static void get(String bitmarkId, boolean includeAsset,
                           Callback1<GetBitmarkResponse> callback) {
        ApiService.getInstance().getBitmark(bitmarkId, includeAsset, callback);
    }

    public static void get(String bitmarkId, Callback1<GetBitmarkResponse> callback) {
        get(bitmarkId, false, callback);
    }

    public static void list(BitmarkQueryBuilder builder, Callback1<GetBitmarksResponse> callback) {
        ApiService.getInstance().listBitmarks(builder.build(), callback);
    }

}
