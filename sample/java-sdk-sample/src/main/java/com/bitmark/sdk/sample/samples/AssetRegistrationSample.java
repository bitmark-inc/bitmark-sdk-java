/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.sample.samples;

import com.bitmark.apiservice.params.RegistrationParams;
import com.bitmark.apiservice.response.RegistrationResponse;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.Asset;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static com.bitmark.apiservice.utils.Awaitility.await;

public class AssetRegistrationSample {
    public static List<RegistrationResponse.Asset> registerAsset(Account registrant, String assetName, String assetFilePath, HashMap<String, String> metadata) throws Throwable {
        RegistrationParams params = new RegistrationParams(assetName, metadata);
        params.setFingerprintFromFile(new File(assetFilePath));
        params.sign(registrant.getAuthKeyPair());
        RegistrationResponse response = await(callback -> Asset.register(params, callback));
        List<RegistrationResponse.Asset> assets = response.getAssets();
        return assets;
    }
}
