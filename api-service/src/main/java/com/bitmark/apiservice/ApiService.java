package com.bitmark.apiservice;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.params.*;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.params.query.QueryParams;
import com.bitmark.apiservice.response.*;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.record.AssetRecord;
import okhttp3.Headers;

import java.util.List;

import static com.bitmark.apiservice.middleware.Converter.*;
import static com.bitmark.apiservice.utils.Awaitility.await;

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
    public void issueBitmark(IssuanceParams params, Callback1<List<String>> callback) {
        try {

            String assetId = params.getAssetId();
            GetBitmarksResponse bitmarksRes = await(bitmarkCallback -> listBitmarks(
                    new BitmarkQueryBuilder().referencedAssetId(assetId).pending(true).limit(1)
                                             .build(),
                    bitmarkCallback));

            params.setContainsGenesisBitmark(
                    bitmarksRes.getBitmarks() != null && bitmarksRes.getBitmarks().isEmpty());
            final String path = String.format("/%s/issue", VERSION);
            client.postAsync(path, params, toIssueResponse(callback));
        } catch (Throwable e) {
            callback.onError(e);
        }
    }

    @Override
    public void registerAsset(RegistrationParams params, Callback1<RegistrationResponse> callback) {
        final String path = String.format("/%s/register-asset", VERSION);
        client.postAsync(path, params, toRegistrationResponse(callback));
    }

    @Override
    public void transferBitmark(TransferParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", VERSION);
        client.postAsync(path, params, toTxId(callback));
    }

    @Override
    public void offerBitmark(TransferOfferParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", VERSION);
        client.postAsync(path, params, toOfferId(callback));
    }

    @Override
    public void respondBitmarkOffer(TransferResponseParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", VERSION);
        Headers headers = Headers.of(params.buildHeaders());
        if (params.isAccept()) client.patchAsync(path, headers, params, toTxId(callback));
        else client.patchAsync(path, headers, params, toStatus(callback));
    }

    @Override
    public void getBitmark(String bitmarkId, boolean includeAsset,
                           Callback1<GetBitmarkResponse> callback) {
        final String path = String.format("/%s/bitmarks/%s?asset=%b", VERSION
                , bitmarkId, includeAsset);
        client.getAsync(path, toGetBitmarkResponse(callback));

    }

    @Override
    public void listBitmarks(QueryParams params, Callback1<GetBitmarksResponse> callback) {
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
                               Callback1<GetTransactionResponse> callback) {
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
