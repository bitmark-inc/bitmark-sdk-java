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

import java.util.Objects;

public class GetBitmarkResponse implements Response {

    private BitmarkRecord bitmark;

    private AssetRecord asset;

    @VisibleForTesting
    public GetBitmarkResponse(BitmarkRecord bitmark, AssetRecord asset) {
        this.bitmark = bitmark;
        this.asset = asset;
    }

    public BitmarkRecord getBitmark() {
        return bitmark;
    }

    public AssetRecord getAsset() {
        return asset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GetBitmarkResponse that = (GetBitmarkResponse) o;
        return Objects.equals(bitmark, that.bitmark) &&
                Objects.equals(asset, that.asset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitmark, asset);
    }
}
