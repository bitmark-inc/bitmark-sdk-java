/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.androidsdksample.sdksamples;

import com.bitmark.apiservice.params.IssuanceParams;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.Bitmark;

import java.util.List;

import io.reactivex.Single;

public class BitmarkIssuanceSample {
    public static Single<List<String>> issueBitmarks(Account issuer, String assetId, int quantity) {
        IssuanceParams issuanceParams = new IssuanceParams(assetId, issuer.toAddress(), quantity);
        issuanceParams.sign(issuer.getKeyPair());

        Single<List<String>> single = Single.create(singleSubscriber -> {
            Bitmark.issue(issuanceParams, new Callback1<List<String>>() {
                @Override
                public void onSuccess(List<String> bitmarkIds) {
                    singleSubscriber.onSuccess(bitmarkIds);
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
