package service.params.query;

import com.google.gson.annotations.SerializedName;

import static utils.Validator.checkValid;
import static utils.Validator.checkValidString;

/**
 * @author Hieu Pham
 * @since 8/30/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class BitmarkQueryBuilder extends AbsQueryBuilder {

    private String owner;

    @SerializedName("issued_by")
    private String issuedBy;

    @SerializedName("pending")
    private boolean isPending;

    @SerializedName("offer_to")
    private String offerTo;

    @SerializedName("offer_from")
    private String offerFrom;

    @SerializedName("bitmark_ids")
    private String[] bitmarkIds;

    @SerializedName("asset_id")
    private String referencedAssetId;

    @SerializedName("asset")
    private boolean isLoadAsset;

    private int limit = 100;

    public BitmarkQueryBuilder ownedBy(String owner) {
        checkValidString(owner);
        this.owner = owner;
        return this;
    }

    public BitmarkQueryBuilder issuedBy(String issuedBy) {
        checkValidString(issuedBy);
        this.issuedBy = issuedBy;
        return this;
    }

    public BitmarkQueryBuilder pending(boolean pending) {
        isPending = pending;
        return this;
    }

    public BitmarkQueryBuilder offerTo(String offerTo) {
        checkValidString(offerTo);
        this.offerTo = offerTo;
        return this;
    }

    public BitmarkQueryBuilder offerFrom(String offerFrom) {
        checkValidString(offerFrom);
        this.offerFrom = offerFrom;
        return this;
    }

    public BitmarkQueryBuilder bitmarkIds(String[] bitmarkIds) {
        checkValid(() -> bitmarkIds != null && bitmarkIds.length > 0, "Invalid bitmark id list");
        this.bitmarkIds = bitmarkIds;
        return this;
    }

    public BitmarkQueryBuilder referencedAssetId(String referencedAssetId) {
        checkValidString(referencedAssetId);
        this.referencedAssetId = referencedAssetId;
        return this;
    }

    public BitmarkQueryBuilder loadAsset(boolean loadAsset) {
        isLoadAsset = loadAsset;
        return this;
    }

    public BitmarkQueryBuilder limit(int limit) {
        checkValid(() -> limit > 0, "Invalid limit value. Must be greater 0");
        this.limit = limit;
        return this;
    }
}
