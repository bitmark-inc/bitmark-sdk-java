/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.ktx.api

import com.bitmark.apiservice.utils.callback.Callback1
import com.bitmark.sdk.features.Account
import io.reactivex.Single

class Migration {

    companion object {

        fun rekey(from: Account, to: Account) = Single.create<List<String>> { emt ->
            com.bitmark.sdk.features.Migration.rekey(from, to, object : Callback1<List<String>> {
                override fun onSuccess(data: List<String>?) {
                    emt.onSuccess(data!!)
                }

                override fun onError(throwable: Throwable?) {
                    emt.onError(throwable!!)
                }
            })
        }
    }
}