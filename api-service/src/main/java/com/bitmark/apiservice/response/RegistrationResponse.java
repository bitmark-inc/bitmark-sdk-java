/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.response;

import com.bitmark.apiservice.utils.annotation.VisibleForTesting;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

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
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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
