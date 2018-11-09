package com.bitmark.apiservice.params.query;

import com.google.gson.annotations.SerializedName;

import static com.bitmark.cryptography.utils.Validator.*;

/**
 * @author Hieu Pham
 * @since 9/18/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransactionQueryBuilder extends AbsQueryBuilder {

    private String owner;

    @SerializedName("asset_id")
    private String assetId;

    @SerializedName("bitmark_id")
    private String bitmarkId;

    @SerializedName("asset")
    private Boolean loadAsset;

    private Integer limit = 100;

    public TransactionQueryBuilder ownedBy(String owner) {
        checkValidString(owner);
        this.owner = owner;
        return this;
    }

    public TransactionQueryBuilder referenceAsset(String assetId) {
        checkValidString(assetId);
        this.assetId = assetId;
        return this;
    }

    public TransactionQueryBuilder referenceBitmark(String bitmarkId) {
        checkValidString(bitmarkId);
        this.bitmarkId = bitmarkId;
        return this;
    }

    public TransactionQueryBuilder loadAsset(Boolean loadAsset) {
        checkNonNull(loadAsset);
        this.loadAsset = loadAsset;
        return this;
    }

    public TransactionQueryBuilder limit(Integer limit) {
        checkValid(() -> limit != null && limit > 0, "Invalid limit value");
        this.limit = limit;
        return this;
    }
}
