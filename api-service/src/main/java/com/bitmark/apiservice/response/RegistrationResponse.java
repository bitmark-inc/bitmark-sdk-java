/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.response;

import com.bitmark.apiservice.utils.annotation.VisibleForTesting;
import com.bitmark.apiservice.utils.record.AssetRecord;

import java.util.List;
import java.util.Objects;

public class RegistrationResponse implements Response {

    private List<AssetRecord> assets;

    @VisibleForTesting
    public RegistrationResponse(List<AssetRecord> assets) {
        this.assets = assets;
    }

    public List<AssetRecord> getAssets() {
        return assets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegistrationResponse response = (RegistrationResponse) o;
        return Objects.equals(assets, response.assets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assets);
    }
}
