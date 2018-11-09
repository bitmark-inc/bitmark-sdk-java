package com.bitmark.apiservice;

import com.bitmark.apiservice.params.TransferParams;
import com.bitmark.apiservice.params.query.QueryParams;
import com.bitmark.apiservice.response.GetTransactionsResponse;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.record.AssetRecord;

import java.util.List;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface BitmarkApi {

    void issueBitmark(com.bitmark.apiservice.params.IssuanceParams params, Callback1<List<String>> callback);

    void registerAsset(com.bitmark.apiservice.params.RegistrationParams params, Callback1<com.bitmark.apiservice.response.RegistrationResponse> callback);

    void transferBitmark(TransferParams params, Callback1<String> callback);

    void offerBitmark(com.bitmark.apiservice.params.TransferOfferParams params, Callback1<String> callback);

    void respondBitmarkOffer(com.bitmark.apiservice.params.TransferResponseParams params, Callback1<String> callback);

    void getBitmark(String bitmarkId, boolean includeAsset, Callback1<com.bitmark.apiservice.response.GetBitmarkResponse> callback);

    void listBitmarks(QueryParams params, Callback1<com.bitmark.apiservice.response.GetBitmarksResponse> callback);

    void getAsset(String assetId, Callback1<AssetRecord> callback);

    void listAssets(QueryParams params, Callback1<List<AssetRecord>> callback);

    void getTransaction(String txId, boolean includeAsset,
                        Callback1<com.bitmark.apiservice.response.GetTransactionResponse> callback);

    void listTransactions(QueryParams params, Callback1<GetTransactionsResponse> callback);

}
