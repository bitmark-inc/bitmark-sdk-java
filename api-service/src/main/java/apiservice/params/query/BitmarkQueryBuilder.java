package apiservice.params.query;

import com.google.gson.annotations.SerializedName;

import static cryptography.utils.Validator.*;

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
    private Boolean isPending;

    @SerializedName("offer_to")
    private String offerTo;

    @SerializedName("offer_from")
    private String offerFrom;

    @SerializedName("bitmark_ids")
    private String[] bitmarkIds;

    @SerializedName("asset_id")
    private String referencedAssetId;

    @SerializedName("asset")
    private Boolean loadAsset;

    private Long at;

    private String to;

    private Integer limit = 100;

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

    public BitmarkQueryBuilder pending(Boolean pending) {
        checkNonNull(pending);
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

    public BitmarkQueryBuilder loadAsset(Boolean loadAsset) {
        checkNonNull(loadAsset);
        this.loadAsset = loadAsset;
        return this;
    }

    public BitmarkQueryBuilder limit(Integer limit) {
        checkValid(() -> limit != null && limit > 0, "Invalid limit value. Must be greater 0");
        this.limit = limit;
        return this;
    }

    public BitmarkQueryBuilder at(Long at) {
        checkValid(() -> at != null && at > 0, "Invalid at value. Must greater than 0");
        this.at = at;
        return this;
    }

    public BitmarkQueryBuilder to(String to) {
        checkValid(() -> to != null && (to.equals("earlier") || to.equals("later")), "Invalid " +
                "value to. It must be 'later' or 'earlier'.");
        this.to = to;
        return this;
    }
}
