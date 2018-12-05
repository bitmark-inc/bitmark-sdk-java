package com.bitmark.apiservice.response;

import com.bitmark.apiservice.utils.annotation.VisibleForTesting;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * @author Hieu Pham
 * @since 9/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class RegistrationResponse implements Response {

    private List<Asset> assets;

    @VisibleForTesting
    public RegistrationResponse(List<Asset> assets) {
        this.assets = assets;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationResponse response = (RegistrationResponse) o;
        return Objects.equals(assets, response.assets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assets);
    }

    public static final class Asset {

        String id;

        @SerializedName("duplicate")
        boolean isDuplicate;

        @VisibleForTesting
        public Asset(String id, boolean isDuplicate) {
            this.id = id;
            this.isDuplicate = isDuplicate;
        }

        public String getId() {
            return id;
        }

        public boolean isDuplicate() {
            return isDuplicate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Asset asset = (Asset) o;
            return isDuplicate == asset.isDuplicate &&
                    Objects.equals(id, asset.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, isDuplicate);
        }
    }
}
