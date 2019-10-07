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

    @SerializedName("block_number")
    private Long blockNumber;

    @SerializedName("pending")
    private Boolean isPending = true;

    private Boolean sent;

    private Long at;

    private String to;

    private Integer limit = 100;

    @SerializedName("block")
    private Boolean loadBlock = false;

    public TransactionQueryBuilder ownedBy(String owner) {
        checkValidString(owner);
        this.owner = owner;
        return this;
    }

    public TransactionQueryBuilder ownedByWithTransient(String owner) {
        checkValidString(owner);
        this.owner = owner;
        this.sent = true;
        return this;
    }

    public TransactionQueryBuilder referencedAsset(String assetId) {
        checkValidString(assetId);
        this.assetId = assetId;
        return this;
    }

    public TransactionQueryBuilder referencedBitmark(String bitmarkId) {
        checkValidString(bitmarkId);
        this.bitmarkId = bitmarkId;
        return this;
    }

    public TransactionQueryBuilder referencedBlockNumber(Long blockNumber) {
        checkValid(
                () -> blockNumber != null && blockNumber > 0,
                "Invalid at value. Must greater than 0"
        );
        this.blockNumber = blockNumber;
        return this;
    }

    public TransactionQueryBuilder pending(Boolean pending) {
        checkNonNull(pending);
        isPending = pending;
        return this;
    }

    public TransactionQueryBuilder loadAsset(Boolean loadAsset) {
        checkNonNull(loadAsset);
        this.loadAsset = loadAsset;
        return this;
    }

    public TransactionQueryBuilder limit(Integer limit) {
        checkValid(
                () -> limit != null && limit > 0 && limit <= 100,
                "Invalid limit value"
        );
        this.limit = limit;
        return this;
    }

    public TransactionQueryBuilder at(Long at) {
        checkValid(
                () -> at != null && at > 0,
                "Invalid at value. Must greater than 0"
        );
        this.at = at;
        return this;
    }

    public TransactionQueryBuilder to(String to) {
        checkValid(
                () -> to != null && (to.equals("earlier") || to.equals("later")),
                "Invalid value to. It must be 'later' or 'earlier'."
        );
        this.to = to;
        return this;
    }

    public TransactionQueryBuilder loadBlock(Boolean loadBlock) {
        checkNonNull(loadBlock);
        this.loadBlock = loadBlock;
        return this;
    }
}
