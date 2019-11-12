/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.ktx.api

import com.bitmark.apiservice.params.query.TransactionQueryBuilder
import com.bitmark.apiservice.response.GetTransactionResponse
import com.bitmark.apiservice.response.GetTransactionsResponse
import com.bitmark.apiservice.utils.callback.Callback1
import io.reactivex.Single

class Transaction {

    companion object {

        fun get(txId: String, loadAsset: Boolean = false) =
            Single.create<GetTransactionResponse> { emt ->
                com.bitmark.sdk.features.Transaction.get(txId,
                                                         loadAsset,
                                                         object : Callback1<GetTransactionResponse> {
                                                             override fun onSuccess(data: GetTransactionResponse?) {
                                                                 emt.onSuccess(data!!)
                                                             }

                                                             override fun onError(throwable: Throwable?) {
                                                                 emt.onError(throwable!!)
                                                             }

                                                         })
            }

        fun list(builder: TransactionQueryBuilder) = Single.create<GetTransactionsResponse> { emt ->
            com.bitmark.sdk.features.Transaction.list(builder,
                                                      object : Callback1<GetTransactionsResponse> {
                                                          override fun onSuccess(data: GetTransactionsResponse?) {
                                                              emt.onSuccess(data!!)
                                                          }

                                                          override fun onError(throwable: Throwable?) {
                                                              emt.onError(throwable!!)
                                                          }

                                                      })
        }
    }
}