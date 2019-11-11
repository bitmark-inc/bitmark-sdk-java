/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.androidktx.sdk

import com.bitmark.apiservice.params.RegistrationParams
import com.bitmark.apiservice.params.query.AssetQueryBuilder
import com.bitmark.apiservice.response.RegistrationResponse
import com.bitmark.apiservice.utils.callback.Callback1
import com.bitmark.apiservice.utils.record.AssetRecord
import io.reactivex.Single

class Asset {

    companion object {

        fun register(params: RegistrationParams) = Single.create<RegistrationResponse> { emt ->
            com.bitmark.sdk.features.Asset.register(params,
                                                    object : Callback1<RegistrationResponse> {
                                                        override fun onSuccess(data: RegistrationResponse?) {
                                                            emt.onSuccess(data!!)
                                                        }

                                                        override fun onError(throwable: Throwable?) {
                                                            emt.onError(throwable!!)
                                                        }
                                                    })
        }

        fun get(assetId: String) = Single.create<AssetRecord> { emt ->
            com.bitmark.sdk.features.Asset.get(assetId, object : Callback1<AssetRecord> {
                override fun onSuccess(data: AssetRecord?) {
                    emt.onSuccess(data!!)
                }

                override fun onError(throwable: Throwable?) {
                    emt.onError(throwable!!)
                }

            })
        }

        fun list(builder: AssetQueryBuilder) = Single.create<List<AssetRecord>> { emt ->
            com.bitmark.sdk.features.Asset.list(builder, object : Callback1<List<AssetRecord>> {
                override fun onSuccess(data: List<AssetRecord>?) {
                    emt.onSuccess(data!!)
                }

                override fun onError(throwable: Throwable?) {
                    emt.onError(throwable!!)
                }
            })
        }
    }
}