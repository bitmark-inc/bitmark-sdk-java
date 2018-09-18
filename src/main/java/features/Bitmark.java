package features;

import service.ApiService;
import service.params.IssuanceParams;
import service.params.TransferOfferParams;
import service.params.TransferParams;
import service.params.TransferResponseParams;
import service.params.query.BitmarkQueryBuilder;
import service.response.GetBitmarkResponse;
import service.response.GetBitmarksResponse;
import service.response.IssueResponse;
import utils.callback.Callback1;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Bitmark {

    public static void issue(IssuanceParams params, Callback1<IssueResponse> callback) {
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
