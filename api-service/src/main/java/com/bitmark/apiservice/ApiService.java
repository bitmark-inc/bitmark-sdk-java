package com.bitmark.apiservice;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.params.TransferParams;
import com.bitmark.apiservice.params.query.QueryParams;
import com.bitmark.apiservice.response.GetTransactionsResponse;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.record.AssetRecord;
import okhttp3.Headers;

import java.util.List;

import static com.bitmark.apiservice.middleware.Converter.*;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class ApiService implements BitmarkApi {

    private static final String VERSION = "v3";

    private static volatile ApiService INSTANCE;

    private HttpClient client;

    public static ApiService getInstance() {
        if (INSTANCE == null) {
            synchronized (ApiService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApiService(GlobalConfiguration.apiToken());
                }
            }
        }
        return INSTANCE;
    }

    private ApiService(String apiToken) {
        this.client = new HttpClientImpl(apiToken);
    }

    @Override
    public void issueBitmark(com.bitmark.apiservice.params.IssuanceParams params, Callback1<List<String>> callback) {
        final String path = String.format("/%s/issue", VERSION);
        client.postAsync(path, params, toIssueResponse(callback));
    }

    @Override
    public void registerAsset(com.bitmark.apiservice.params.RegistrationParams params, Callback1<com.bitmark.apiservice.response.RegistrationResponse> callback) {
        final String path = String.format("/%s/register-asset", VERSION);
        client.postAsync(path, params, toRegistrationResponse(callback));
    }

    @Override
    public void transferBitmark(TransferParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", VERSION);
        client.postAsync(path, params, toTxId(callback));
    }

    @Override
    public void offerBitmark(com.bitmark.apiservice.params.TransferOfferParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", VERSION);
        client.postAsync(path, params, toOfferId(callback));
    }

    @Override
    public void respondBitmarkOffer(com.bitmark.apiservice.params.TransferResponseParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", VERSION);
        Headers headers = Headers.of(params.buildHeaders());
        if (params.isAccept()) client.patchAsync(path, headers, params, toTxId(callback));
        else client.patchAsync(path, headers, params, toStatus(callback));
    }

    @Override
    public void getBitmark(String bitmarkId, boolean includeAsset,
                           Callback1<com.bitmark.apiservice.response.GetBitmarkResponse> callback) {
        final String path = String.format("/%s/bitmarks/%s?asset=%b", VERSION
                , bitmarkId, includeAsset);
        client.getAsync(path, toGetBitmarkResponse(callback));

    }

    @Override
    public void listBitmarks(QueryParams params, Callback1<com.bitmark.apiservice.response.GetBitmarksResponse> callback) {
        final String path = String.format("/%s/bitmarks", VERSION);
        client.getAsync(path, params, toGetBitmarksResponse(callback));
    }

    @Override
    public void getAsset(String assetId, Callback1<AssetRecord> callback) {
        final String path = String.format("/%s/assets/%s", VERSION, assetId);
        client.getAsync(path, toAssetRecord(callback));
    }

    @Override
    public void listAssets(QueryParams params, Callback1<List<AssetRecord>> callback) {
        final String path = String.format("/%s/assets", VERSION);
        client.getAsync(path, params, toAssetRecords(callback));
    }

    @Override
    public void getTransaction(String txId, boolean includeAsset,
                               Callback1<com.bitmark.apiservice.response.GetTransactionResponse> callback) {
        final String path = String.format("/%s/txs/%s?asset=%b", VERSION,
                txId, includeAsset);
        client.getAsync(path, toGetTransactionResponse(callback));
    }

    @Override
    public void listTransactions(QueryParams params, Callback1<GetTransactionsResponse> callback) {
        final String path = String.format("/%s/txs", VERSION);
        client.getAsync(path, params, toGetTransactionsResponse(callback));
    }
}
