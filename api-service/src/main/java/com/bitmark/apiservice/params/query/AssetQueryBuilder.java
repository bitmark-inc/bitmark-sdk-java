/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.params.query;

import com.google.gson.annotations.SerializedName;

import static com.bitmark.cryptography.utils.Validator.*;

public class AssetQueryBuilder extends AbsQueryBuilder {

    private String registrant;

    @SerializedName("asset_ids")
    private String[] assetIds;

    @SerializedName("pending")
    private Boolean isPending = true;

    private Long at;

    private String to;

    private Integer limit = 100;

    public AssetQueryBuilder registeredBy(String registrant) {
        checkValidString(registrant);
        this.registrant = registrant;
        return this;
    }

    public AssetQueryBuilder assetIds(String[] assetIds) {
        checkValid(
                () -> assetIds != null && assetIds.length > 0,
                "Invalid asset ids"
        );
        this.assetIds = assetIds;
        return this;
    }

    public AssetQueryBuilder pending(Boolean pending) {
        checkNonNull(pending);
        isPending = pending;
        return this;
    }

    public AssetQueryBuilder limit(Integer limit) {
        checkValid(
                () -> limit != null && limit > 0 && limit <= 100,
                "Invalid limit value"
        );
        this.limit = limit;
        return this;
    }

    public AssetQueryBuilder at(Long at) {
        checkValid(
                () -> at != null && at > 0,
                "Invalid at value. Must greater than 0"
        );
        this.at = at;
        return this;
    }

    public AssetQueryBuilder to(String to) {
        checkValid(
                () -> to != null && (to.equals("earlier") || to.equals("later")),
                "Invalid value to. It must be 'later' or 'earlier'."
        );
        this.to = to;
        return this;
    }
}
