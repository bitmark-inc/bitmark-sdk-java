/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.ktx.api

import android.util.Pair
import com.bitmark.apiservice.params.*
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder
import com.bitmark.apiservice.response.GetBitmarkResponse
import com.bitmark.apiservice.response.GetBitmarksResponse
import com.bitmark.apiservice.utils.callback.Callback1
import com.bitmark.apiservice.utils.record.ShareGrantRecord
import com.bitmark.apiservice.utils.record.ShareRecord
import io.reactivex.Single

class Bitmark {

    companion object {

        fun issue(params: IssuanceParams) = Single.create<List<String>> { emt ->
            com.bitmark.sdk.features.Bitmark.issue(params, object : Callback1<List<String>> {
                override fun onSuccess(data: List<String>?) {
                    emt.onSuccess(data!!)
                }

                override fun onError(throwable: Throwable?) {
                    emt.onError(throwable!!)
                }
            })
        }

        fun transfer(params: TransferParams) = Single.create<String> { emt ->
            com.bitmark.sdk.features.Bitmark.transfer(params, object : Callback1<String> {
                override fun onSuccess(data: String?) {
                    emt.onSuccess(data!!)
                }

                override fun onError(throwable: Throwable?) {
                    emt.onError(throwable!!)
                }

            })
        }

        fun offer(params: TransferOfferParams) = Single.create<String> { emt ->
            com.bitmark.sdk.features.Bitmark.offer(params, object : Callback1<String> {
                override fun onSuccess(data: String?) {
                    emt.onSuccess(data!!)
                }

                override fun onError(throwable: Throwable?) {
                    emt.onError(throwable!!)
                }

            })
        }

        fun respond(params: TransferResponseParams) = Single.create<String> { emt ->
            com.bitmark.sdk.features.Bitmark.respond(params, object : Callback1<String> {
                override fun onSuccess(data: String?) {
                    emt.onSuccess(data!!)
                }

                override fun onError(throwable: Throwable?) {
                    emt.onError(throwable!!)
                }

            })
        }

        fun get(bitmarkId: String, loadAsset: Boolean = false) =
            Single.create<GetBitmarkResponse> { emt ->
                com.bitmark.sdk.features.Bitmark.get(bitmarkId,
                                                     loadAsset,
                                                     object : Callback1<GetBitmarkResponse> {
                                                         override fun onSuccess(data: GetBitmarkResponse?) {
                                                             emt.onSuccess(data!!)
                                                         }

                                                         override fun onError(throwable: Throwable?) {
                                                             emt.onError(throwable!!)
                                                         }
                                                     })
            }

        fun list(builder: BitmarkQueryBuilder) = Single.create<GetBitmarksResponse> { emt ->
            com.bitmark.sdk.features.Bitmark.list(builder, object : Callback1<GetBitmarksResponse> {
                override fun onSuccess(data: GetBitmarksResponse?) {
                    emt.onSuccess(data!!)
                }

                override fun onError(throwable: Throwable?) {
                    emt.onError(throwable!!)
                }
            })
        }

        fun createShare(params: ShareParams) = Single.create<Pair<String, String>> { emt ->
            com.bitmark.sdk.features.Bitmark.createShare(params, object :
                Callback1<com.bitmark.apiservice.utils.Pair<String, String>> {
                override fun onSuccess(data: com.bitmark.apiservice.utils.Pair<String, String>?) {
                    emt.onSuccess(Pair(data!!.first(), data.second()))
                }

                override fun onError(throwable: Throwable?) {
                    emt.onError(throwable!!)
                }

            })
        }

        fun grantShare(params: ShareGrantingParams) = Single.create<String> { emt ->
            com.bitmark.sdk.features.Bitmark.grantShare(params, object : Callback1<String> {
                override fun onSuccess(data: String?) {
                    emt.onSuccess(data!!)
                }

                override fun onError(throwable: Throwable?) {
                    emt.onError(throwable!!)
                }

            })
        }

        fun respondShareOffer(params: GrantResponseParams) = Single.create<String> { emt ->
            com.bitmark.sdk.features.Bitmark.respondShareOffer(params, object : Callback1<String> {
                override fun onSuccess(data: String?) {
                    emt.onSuccess(data!!)
                }

                override fun onError(throwable: Throwable?) {
                    emt.onError(throwable!!)
                }

            })
        }

        fun getShare(shareId: String) = Single.create<ShareRecord> { emt ->
            com.bitmark.sdk.features.Bitmark.getShare(shareId, object : Callback1<ShareRecord> {
                override fun onSuccess(data: ShareRecord?) {
                    emt.onSuccess(data!!)
                }

                override fun onError(throwable: Throwable?) {
                    emt.onError(throwable!!)
                }
            })
        }

        fun listShares(owner: String) = Single.create<List<ShareRecord>> { emt ->
            com.bitmark.sdk.features.Bitmark.listShares(owner,
                                                        object : Callback1<List<ShareRecord>> {
                                                            override fun onSuccess(data: List<ShareRecord>?) {
                                                                emt.onSuccess(data!!)
                                                            }

                                                            override fun onError(throwable: Throwable?) {
                                                                emt.onError(throwable!!)
                                                            }

                                                        })
        }

        fun listShareOffer(from: String, to: String) =
            Single.create<List<ShareGrantRecord>> { emt ->
                com.bitmark.sdk.features.Bitmark.listShareOffer(from,
                                                                to,
                                                                object : Callback1<List<ShareGrantRecord>> {
                                                                    override fun onSuccess(data: List<ShareGrantRecord>?) {
                                                                        emt.onSuccess(data!!)
                                                                    }

                                                                    override fun onError(throwable: Throwable?) {
                                                                        emt.onError(throwable!!)
                                                                    }

                                                                })
            }

    }
}