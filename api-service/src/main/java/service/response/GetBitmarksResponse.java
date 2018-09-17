package service.response;

import utils.record.AssetRecord;
import utils.record.BitmarkRecord;

import java.util.List;
import java.util.Objects;

/**
 * @author Hieu Pham
 * @since 9/16/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class GetBitmarksResponse implements Response {

    private List<BitmarkRecord> bitmarks;

    private List<AssetRecord> assets;

    public GetBitmarksResponse(List<BitmarkRecord> bitmarks, List<AssetRecord> assets) {
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetBitmarksResponse that = (GetBitmarksResponse) o;
        return Objects.equals(bitmarks, that.bitmarks) &&
                Objects.equals(assets, that.assets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitmarks, assets);
    }
}
