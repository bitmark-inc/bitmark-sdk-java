package apiservice;

import apiservice.configuration.GlobalConfiguration;
import apiservice.params.*;
import apiservice.params.query.QueryParams;
import apiservice.response.*;
import apiservice.utils.callback.Callback1;
import apiservice.utils.record.AssetRecord;
import okhttp3.Headers;

import java.util.List;

import static apiservice.middleware.Converter.*;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class ApiService extends AbsApiService implements BitmarkApi {

    private static final String VERSION = "v3";

    private static volatile ApiService INSTANCE;

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
        super(apiToken);
    }

    @Override
    public void issueBitmark(IssuanceParams params, Callback1<List<String>> callback) {
        final String path = String.format("/%s/issue", VERSION);
        postAsync(path, params, toIssueResponse(callback));
    }

    @Override
    public void registerAsset(RegistrationParams params, Callback1<RegistrationResponse> callback) {
        final String path = String.format("/%s/register-asset", VERSION);
        postAsync(path, params, toRegistrationResponse(callback));
    }

    @Override
    public void transferBitmark(TransferParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", VERSION);
        postAsync(path, params, toTxId(callback));
    }

    @Override
    public void offerBitmark(TransferOfferParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", VERSION);
        postAsync(path, params, toOfferId(callback));
    }

    @Override
    public void respondBitmarkOffer(TransferResponseParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", VERSION);
        Headers headers = Headers.of(params.buildHeaders());
        if (params.isAccept()) patchAsync(path, headers, params, toTxId(callback));
        else patchAsync(path, headers, params, toStatus(callback));
    }

    @Override
    public void getBitmark(String bitmarkId, boolean includeAsset,
                           Callback1<GetBitmarkResponse> callback) {
        final String path = String.format("/%s/bitmarks/%s?asset=%b", VERSION
                , bitmarkId, includeAsset);
        getAsync(path, toGetBitmarkResponse(callback));

    }

    @Override
    public void listBitmarks(QueryParams params, Callback1<GetBitmarksResponse> callback) {
        final String path = String.format("/%s/bitmarks", VERSION);
        getAsync(path, params, toGetBitmarksResponse(callback));
    }

    @Override
    public void getAsset(String assetId, Callback1<AssetRecord> callback) {
        final String path = String.format("/%s/assets/%s", VERSION, assetId);
        getAsync(path, toAssetRecord(callback));
    }

    @Override
    public void listAssets(QueryParams params, Callback1<List<AssetRecord>> callback) {
        final String path = String.format("/%s/assets", VERSION);
        getAsync(path, params, toAssetRecords(callback));
    }

    @Override
    public void getTransaction(String txId, boolean includeAsset,
                               Callback1<GetTransactionResponse> callback) {
        final String path = String.format("/%s/txs/%s?asset=%b", VERSION,
                txId, includeAsset);
        getAsync(path, toGetTransactionResponse(callback));
    }

    @Override
    public void listTransactions(QueryParams params, Callback1<GetTransactionsResponse> callback) {
        final String path = String.format("/%s/txs", VERSION);
        getAsync(path, params, toGetTransactionsResponse(callback));
    }
}
