/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.features;

import com.bitmark.apiservice.ApiService;
import com.bitmark.apiservice.params.query.TransactionQueryBuilder;
import com.bitmark.apiservice.response.GetTransactionResponse;
import com.bitmark.apiservice.response.GetTransactionsResponse;
import com.bitmark.apiservice.utils.callback.Callback1;

import static com.bitmark.sdk.utils.CommonUtils.wrapCallbackOnMain;

public class Transaction {

    public static void get(
            String txId,
            Callback1<GetTransactionResponse> callback
    ) {
        get(txId, false, wrapCallbackOnMain(callback));
    }

    public static void getWithAsset(
            String txId,
            Callback1<GetTransactionResponse> callback
    ) {
        get(txId, true, callback);
    }

    public static void get(
            String txId, boolean loadAsset,
            Callback1<GetTransactionResponse> callback
    ) {
        ApiService.getInstance()
                .getTransaction(txId, loadAsset, wrapCallbackOnMain(callback));
    }

    public static void list(
            TransactionQueryBuilder builder,
            Callback1<GetTransactionsResponse> callback
    ) {
        ApiService.getInstance()
                .listTransactions(
                        builder.build(),
                        wrapCallbackOnMain(callback)
                );
    }
}
