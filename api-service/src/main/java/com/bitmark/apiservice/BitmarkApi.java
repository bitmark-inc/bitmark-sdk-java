/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice;

import com.bitmark.apiservice.params.*;
import com.bitmark.apiservice.params.query.QueryParams;
import com.bitmark.apiservice.response.*;
import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.apiservice.utils.record.ShareGrantRecord;
import com.bitmark.apiservice.utils.record.ShareRecord;

import java.util.List;

public interface BitmarkApi {

    void issueBitmark(IssuanceParams params, Callback1<List<String>> callback);

    void registerAsset(
            RegistrationParams params,
            Callback1<RegistrationResponse> callback
    );

    void transferBitmark(TransferParams params, Callback1<String> callback);

    void offerBitmark(TransferOfferParams params, Callback1<String> callback);

    void respondBitmarkOffer(
            TransferResponseParams params,
            Callback1<String> callback
    );

    void getBitmark(
            String bitmarkId,
            boolean includeAsset,
            Callback1<GetBitmarkResponse> callback
    );

    void listBitmarks(
            QueryParams params,
            Callback1<GetBitmarksResponse> callback
    );

    void getAsset(String assetId, Callback1<AssetRecord> callback);

    void listAssets(QueryParams params, Callback1<List<AssetRecord>> callback);

    void getTransaction(
            String txId, boolean includeAsset,
            Callback1<GetTransactionResponse> callback
    );

    void listTransactions(
            QueryParams params,
            Callback1<GetTransactionsResponse> callback
    );

    void createShare(
            ShareParams params,
            Callback1<Pair<String, String>> callback
    );

    void grantShare(ShareGrantingParams params, Callback1<String> callback);

    void respondShareOffer(
            GrantResponseParams params,
            Callback1<String> callback
    );

    void getShare(String shareId, Callback1<ShareRecord> callback);

    void listShares(String owner, Callback1<List<ShareRecord>> callback);

    void listShareOffer(
            String from,
            String to,
            Callback1<List<ShareGrantRecord>> callback
    );

    void registerWsToken(
            RegisterWsTokenParams params,
            Callback1<String> callback
    );

}
