/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.sample.samples;

import com.bitmark.apiservice.params.query.AssetQueryBuilder;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.params.query.TransactionQueryBuilder;
import com.bitmark.apiservice.response.GetBitmarkResponse;
import com.bitmark.apiservice.response.GetBitmarksResponse;
import com.bitmark.apiservice.response.GetTransactionResponse;
import com.bitmark.apiservice.response.GetTransactionsResponse;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.sdk.features.Asset;
import com.bitmark.sdk.features.Bitmark;
import com.bitmark.sdk.features.Transaction;

import java.util.List;

import static com.bitmark.apiservice.utils.Awaitility.await;

public class QuerySample {
    public static GetBitmarksResponse queryBitmarks(BitmarkQueryBuilder builder) throws Throwable {
        GetBitmarksResponse bitmarksResponse = await(callback -> Bitmark.list(builder, callback));

        return bitmarksResponse;
    }

    public static GetBitmarkResponse queryBitmarkById(String bitmarkId) throws Throwable {
        GetBitmarkResponse bitmarkResponse = await(callback -> Bitmark.get(bitmarkId, callback));

        return bitmarkResponse;
    }

    public static List<AssetRecord> queryAssets(AssetQueryBuilder builder) throws Throwable {
        List<AssetRecord> assetsResponse = await(callback -> Asset.list(builder, callback));
        return assetsResponse;
    }

    public static AssetRecord queryAssetById(String assetId) throws Throwable {
        AssetRecord assetRecord = await(callback -> Asset.get(assetId, callback));

        return assetRecord;
    }

    public static GetTransactionsResponse queryTransactions(TransactionQueryBuilder builder) throws Throwable {
        GetTransactionsResponse transactionsResponse = await(callback -> Transaction.list(builder, callback));

        return transactionsResponse;
    }

    public static GetTransactionResponse queryTransactionById(String txId) throws Throwable {
        GetTransactionResponse transactionResponse = await(callback -> Transaction.get(txId, callback));

        return transactionResponse;
    }
}
