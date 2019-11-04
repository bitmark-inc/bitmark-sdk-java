/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.androidsdksample.sdksamples;

import com.bitmark.apiservice.params.RegistrationParams;
import com.bitmark.apiservice.response.RegistrationResponse;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.Asset;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;

public class AssetRegistrationSample {
    public static Single<List<RegistrationResponse.Asset>> registerAsset(Account registrant, String assetName, String assetFilePath, HashMap<String, String> metadata) {
        RegistrationParams params = new RegistrationParams(assetName, metadata, registrant.toAddress());
        params.generateFingerprint(new File(assetFilePath));
        params.sign(registrant.getKeyPair());

        Single<List<RegistrationResponse.Asset>> single = Single.create(singleSubscriber -> {
            Asset.register(params, new Callback1<RegistrationResponse>() {
                @Override
                public void onSuccess(RegistrationResponse data) {
                    List<RegistrationResponse.Asset> assets = data.getAssets();
                    singleSubscriber.onSuccess(assets);
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
