package com.bitmark.apiservice;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.params.*;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.params.query.QueryParams;
import com.bitmark.apiservice.response.*;
import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.apiservice.utils.record.ShareGrantRecord;
import com.bitmark.apiservice.utils.record.ShareRecord;
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

    private static final String V3 = "v3";

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
                    new BitmarkQueryBuilder().referencedAsset(assetId).pending(true).limit(1)
                                             .build(),
                    bitmarkCallback));

            params.setContainsGenesisBitmark(
                    bitmarksRes.getBitmarks() != null && bitmarksRes.getBitmarks().isEmpty());
            final String path = String.format("/%s/issue", V3);
            client.postAsync(path, params, toIssueResponse(callback));
        } catch (Throwable e) {
            callback.onError(e);
        }
    }

    @Override
    public void registerAsset(RegistrationParams params, Callback1<RegistrationResponse> callback) {
        final String path = String.format("/%s/register-asset", V3);
        client.postAsync(path, params, toRegistrationResponse(callback));
    }

    @Override
    public void transferBitmark(TransferParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", V3);
        client.postAsync(path, params, toTxId(callback));
    }

    @Override
    public void offerBitmark(TransferOfferParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", V3);
        client.postAsync(path, params, toOfferId(callback));
    }

    @Override
    public void respondBitmarkOffer(TransferResponseParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", V3);
        Headers headers = Headers.of(params.buildHeaders());
        client.patchAsync(path, headers, params,
                          params.isAccept() ? toTxId(callback) : toStatus(callback));
    }

    @Override
    public void getBitmark(String bitmarkId, boolean includeAsset,
                           Callback1<GetBitmarkResponse> callback) {
        final String path = String.format("/%s/bitmarks/%s?asset=%b&pending=true", V3
                , bitmarkId, includeAsset);
        client.getAsync(path, toGetBitmarkResponse(callback));

    }

    @Override
    public void listBitmarks(QueryParams params, Callback1<GetBitmarksResponse> callback) {
        final String path = String.format("/%s/bitmarks", V3);
        client.getAsync(path, params, toGetBitmarksResponse(callback));
    }

    @Override
    public void getAsset(String assetId, Callback1<AssetRecord> callback) {
        final String path = String.format("/%s/assets/%s", V3, assetId);
        client.getAsync(path, toAssetRecord(callback));
    }

    @Override
    public void listAssets(QueryParams params, Callback1<List<AssetRecord>> callback) {
        final String path = String.format("/%s/assets", V3);
        client.getAsync(path, params, toAssetRecords(callback));
    }

    @Override
    public void getTransaction(String txId, boolean includeAsset,
                               Callback1<GetTransactionResponse> callback) {
        final String path = String.format("/%s/txs/%s?asset=%b&pending=true", V3,
                                          txId, includeAsset);
        client.getAsync(path, toGetTransactionResponse(callback));
    }

    @Override
    public void listTransactions(QueryParams params, Callback1<GetTransactionsResponse> callback) {
        final String path = String.format("/%s/txs", V3);
        client.getAsync(path, params, toGetTransactionsResponse(callback));
    }

    @Override
    public void createShare(ShareParams params, Callback1<Pair<String, String>> callback) {
        final String path = String.format("/%s/shares", V3);
        client.postAsync(path, params, toCreateShareResponse(callback));
    }

    @Override
    public void grantShare(ShareGrantingParams params, Callback1<String> callback) {
        final String path = String.format("/%s/share-offer", V3);
        client.postAsync(path, params, toGrantShareResponse(callback));
    }

    @Override
    public void respondShareOffer(GrantResponseParams params, Callback1<String> callback) {
        final String path = String.format("/%s/share-offer", V3);
        Headers headers = Headers.of(params.buildHeaders());
        client.patchAsync(path, headers, params,
                          params.isAccept() ? toTxId(callback) : toStatus(callback));
    }

    @Override
    public void getShare(String shareId, Callback1<ShareRecord> callback) {
        final String path = String.format("/%s/shares?share_id=%s", V3, shareId);
        client.getAsync(path, toGetShareResponse(callback));
    }

    @Override
    public void listShares(String owner, Callback1<List<ShareRecord>> callback) {
        final String path = String.format("/%s/shares?owner=%s", V3, owner);
        client.getAsync(path, toListSharesResponse(callback));
    }

    @Override
    public void listShareOffer(String from, String to, Callback1<List<ShareGrantRecord>> callback) {
        final String path = String.format("/%s/share-offer?from=%s&to=%s", V3, from, to);
        client.getAsync(path, toListShareOffersResponse(callback));
    }

    @Override
    public void registerWsToken(RegisterWsTokenParams params, Callback1<String> callback) {
        final String path = String.format("/%s/ws-auth", V3);
        final Headers header = Headers.of(params.buildHeader());
        client.postAsync(path, header, params, toWsToken(callback));
    }
}
