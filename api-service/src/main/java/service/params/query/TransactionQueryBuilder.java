package service.params.query;

import com.google.gson.annotations.SerializedName;

import static utils.Validator.checkValid;
import static utils.Validator.checkValidString;

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
    private boolean loadAsset;

    private int limit = 100;

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

    public TransactionQueryBuilder loadAsset(boolean loadAsset) {
        this.loadAsset = loadAsset;
        return this;
    }

    public TransactionQueryBuilder limit(int limit) {
        checkValid(() -> limit > 0, "Invalid limit value");
        this.limit = limit;
        return this;
    }
}
