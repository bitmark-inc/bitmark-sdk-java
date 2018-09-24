package com.bitmark.sdk.features;

import com.bitmark.sdk.service.ApiService;
import com.bitmark.sdk.service.params.IssuanceParams;
import com.bitmark.sdk.service.params.TransferOfferParams;
import com.bitmark.sdk.service.params.TransferParams;
import com.bitmark.sdk.service.params.TransferResponseParams;
import com.bitmark.sdk.service.params.query.BitmarkQueryBuilder;
import com.bitmark.sdk.service.response.GetBitmarkResponse;
import com.bitmark.sdk.service.response.GetBitmarksResponse;
import com.bitmark.sdk.utils.callback.Callback1;

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
