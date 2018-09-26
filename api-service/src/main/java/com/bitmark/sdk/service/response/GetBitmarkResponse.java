package com.bitmark.sdk.service.response;

import com.bitmark.sdk.utils.annotation.VisibleForTesting;
import com.bitmark.sdk.utils.record.AssetRecord;
import com.bitmark.sdk.utils.record.BitmarkRecord;

import java.util.Objects;

/**
 * @author Hieu Pham
 * @since 9/16/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetBitmarkResponse that = (GetBitmarkResponse) o;
        return Objects.equals(bitmark, that.bitmark) &&
                Objects.equals(asset, that.asset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitmark, asset);
    }
}
