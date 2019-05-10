package com.bitmark.androidsdksample.samples;

import com.bitmark.apiservice.params.query.AssetQueryBuilder;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.params.query.TransactionQueryBuilder;
import com.bitmark.apiservice.response.GetBitmarkResponse;
import com.bitmark.apiservice.response.GetBitmarksResponse;
import com.bitmark.apiservice.response.GetTransactionResponse;
import com.bitmark.apiservice.response.GetTransactionsResponse;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.sdk.features.Asset;
import com.bitmark.sdk.features.Bitmark;
import com.bitmark.sdk.features.Transaction;

import java.util.List;

import io.reactivex.Single;

public class QuerySample {
    public static Single<GetBitmarksResponse> queryBitmarks(BitmarkQueryBuilder builder) {
        Single<GetBitmarksResponse> single = Single.create(singleSubscriber -> {
            Bitmark.list(builder, new Callback1<GetBitmarksResponse>() {
                @Override
                public void onSuccess(GetBitmarksResponse data) {
                    singleSubscriber.onSuccess(data);
                }

                @Override
                public void onError(Throwable throwable) {
                    singleSubscriber.onError(throwable);
                }
            });
        });

        return single;
    }

    public static Single<GetBitmarkResponse> queryBitmarkById(String bitmarkId) {
        Single<GetBitmarkResponse> single = Single.create(singleSubscriber -> {
            Bitmark.get(bitmarkId, new Callback1<GetBitmarkResponse>() {
                @Override
                public void onSuccess(GetBitmarkResponse data) {
                    singleSubscriber.onSuccess(data);
                }

                @Override
                public void onError(Throwable throwable) {
                    singleSubscriber.onError(throwable);
                }
            });
        });

        return single;
    }

    public static Single<List<AssetRecord>> queryAssets(AssetQueryBuilder builder) {
        Single<List<AssetRecord>> single = Single.create(singleSubscriber -> {
            Asset.list(builder, new Callback1<List<AssetRecord>>() {
                @Override
                public void onSuccess(List<AssetRecord> data) {
                    singleSubscriber.onSuccess(data);
                }

                @Override
                public void onError(Throwable throwable) {
                    singleSubscriber.onError(throwable);
                }
            });
        });

        return single;
    }

    public static Single<AssetRecord> queryAssetById(String assetId) {
        Single<AssetRecord> single = Single.create(singleSubscriber -> {
            Asset.get(assetId, new Callback1<AssetRecord>() {
                @Override
                public void onSuccess(AssetRecord data) {
                    singleSubscriber.onSuccess(data);
                }

                @Override
                public void onError(Throwable throwable) {
                    singleSubscriber.onError(throwable);
                }
            });
        });

        return single;
    }

    public static Single<GetTransactionsResponse> queryTransactions(TransactionQueryBuilder builder) {
        Single<GetTransactionsResponse> single = Single.create(singleSubscriber -> {
            Transaction.list(builder, new Callback1<GetTransactionsResponse>() {
                @Override
                public void onSuccess(GetTransactionsResponse data) {
                    singleSubscriber.onSuccess(data);
                }

                @Override
                public void onError(Throwable throwable) {
                    singleSubscriber.onError(throwable);
                }
            });
        });

        return single;
    }

    public static Single<GetTransactionResponse> queryTransactionById(String txId) {
        Single<GetTransactionResponse> single = Single.create(singleSubscriber -> {
            Transaction.get(txId, new Callback1<GetTransactionResponse>() {
                @Override
                public void onSuccess(GetTransactionResponse data) {
                    singleSubscriber.onSuccess(data);
                }

                @Override
                public void onError(Throwable throwable) {
                    singleSubscriber.onError(throwable);
                }
            });
        });

        return single;
    }
}
