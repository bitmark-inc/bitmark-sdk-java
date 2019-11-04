/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.response;

import com.bitmark.apiservice.utils.annotation.VisibleForTesting;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.apiservice.utils.record.BitmarkRecord;

import java.util.List;
import java.util.Objects;

public class GetBitmarksResponse implements Response {

    private List<BitmarkRecord> bitmarks;

    private List<AssetRecord> assets;

    @VisibleForTesting
    public GetBitmarksResponse(
            List<BitmarkRecord> bitmarks,
            List<AssetRecord> assets
    ) {
        this.bitmarks = bitmarks;
        this.assets = assets;
    }

    public List<BitmarkRecord> getBitmarks() {
        return bitmarks;
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
        GetBitmarksResponse that = (GetBitmarksResponse) o;
        return Objects.equals(bitmarks, that.bitmarks) &&
                Objects.equals(assets, that.assets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitmarks, assets);
    }
}
