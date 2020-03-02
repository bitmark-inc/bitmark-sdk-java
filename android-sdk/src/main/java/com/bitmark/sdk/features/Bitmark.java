/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.features;

import com.bitmark.apiservice.ApiService;
import com.bitmark.apiservice.params.*;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.response.GetBitmarkResponse;
import com.bitmark.apiservice.response.GetBitmarksResponse;
import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.record.BitmarkRecord;
import com.bitmark.apiservice.utils.record.ShareGrantRecord;
import com.bitmark.apiservice.utils.record.ShareRecord;

import java.util.List;

import static com.bitmark.sdk.utils.CommonUtils.wrapCallbackOnMain;

public class Bitmark {

    public static void issue(
            IssuanceParams params,
            Callback1<List<BitmarkRecord>> callback
    ) {
        ApiService.getInstance()
                .issueBitmark(params, wrapCallbackOnMain(callback));
    }

    public static void transfer(
            TransferParams params,
            Callback1<String> callback
    ) {
        ApiService.getInstance()
                .transferBitmark(params, wrapCallbackOnMain(callback));
    }

    public static void offer(
            TransferOfferParams params,
            Callback1<String> callback
    ) {
        ApiService.getInstance()
                .offerBitmark(params, wrapCallbackOnMain(callback));
    }

    public static void respond(
            TransferResponseParams params,
            Callback1<String> callback
    ) {
        ApiService.getInstance()
                .respondBitmarkOffer(params, wrapCallbackOnMain(callback));
    }

    public static void get(
            String bitmarkId, boolean loadAsset,
            Callback1<GetBitmarkResponse> callback
    ) {
        ApiService.getInstance()
                .getBitmark(bitmarkId, loadAsset, wrapCallbackOnMain(callback));
    }

    public static void get(
            String bitmarkId,
            Callback1<GetBitmarkResponse> callback
    ) {
        get(bitmarkId, false, callback);
    }

    public static void getWithAsset(
            String bitmarkId,
            Callback1<GetBitmarkResponse> callback
    ) {
        get(bitmarkId, true, callback);
    }

    public static void list(
            BitmarkQueryBuilder builder,
            Callback1<GetBitmarksResponse> callback
    ) {
        ApiService.getInstance()
                .listBitmarks(builder.build(), wrapCallbackOnMain(callback));
    }

    public static void createShare(
            ShareParams params,
            Callback1<Pair<String, String>> callback
    ) {
        ApiService.getInstance().createShare(params, callback);
    }

    public static void grantShare(
            ShareGrantingParams params,
            Callback1<String> callback
    ) {
        ApiService.getInstance().grantShare(params, callback);
    }

    public static void respondShareOffer(
            GrantResponseParams params,
            Callback1<String> callback
    ) {
        ApiService.getInstance().respondShareOffer(params, callback);
    }

    public static void getShare(
            String shareId,
            Callback1<ShareRecord> callback
    ) {
        ApiService.getInstance().getShare(shareId, callback);
    }

    public static void listShares(
            String owner,
            Callback1<List<ShareRecord>> callback
    ) {
        ApiService.getInstance().listShares(owner, callback);
    }

    public static void listShareOffer(
            String from, String to,
            Callback1<List<ShareGrantRecord>> callback
    ) {
        ApiService.getInstance().listShareOffer(from, to, callback);
    }

}
