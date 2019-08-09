package com.bitmark.apiservice.utils.record;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * @author Hieu Pham
 * @since 9/16/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class BitmarkRecord implements Record {

    public enum Status {
        @SerializedName("issuing")
        ISSUING,

        @SerializedName("transferring")
        TRANSFERRING,

        @SerializedName("offering")
        OFFERING,

        @SerializedName("settled")
        SETTLED
    }

    @SerializedName("asset_id")
    private String assetId;

    @SerializedName("block_number")
    private long blockNumber;

    @SerializedName("confirmed_at")
    private String confirmedAt;

    @SerializedName("created_at")
    private String createdAt;

    private Head head;

    @SerializedName("head_id")
    private String headId;

    private String id;

    @SerializedName("issued_at")
    private String issuedAt;

    private String issuer;

    private long offset;

    private String owner;

    private Status status;

    private OfferRecord offer;

    private Integer edition;

    public String getAssetId() {
        return assetId;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public String getConfirmedAt() {
        return confirmedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Head getHead() {
        return head;
    }

    public String getHeadId() {
        return headId;
    }

    public String getId() {
        return id;
    }

    public String getIssuedAt() {
        return issuedAt;
    }

    public String getIssuer() {
        return issuer;
    }

    public long getOffset() {
        return offset;
    }

    public String getOwner() {
        return owner;
    }

    public Status getStatus() {
        return status;
    }

    public OfferRecord getOffer() {
        return offer;
    }

    public boolean isOffer() {
        return offer != null;
    }

    public Integer getEdition() {
        return edition;
    }

    public void setEdition(Integer edition) {
        this.edition = edition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BitmarkRecord that = (BitmarkRecord) o;
        return blockNumber == that.blockNumber &&
               offset == that.offset &&
               Objects.equals(assetId, that.assetId) &&
               Objects.equals(confirmedAt, that.confirmedAt) &&
               Objects.equals(createdAt, that.createdAt) &&
               head == that.head &&
               Objects.equals(headId, that.headId) &&
               Objects.equals(id, that.id) &&
               Objects.equals(issuedAt, that.issuedAt) &&
               Objects.equals(issuer, that.issuer) &&
               Objects.equals(owner, that.owner) &&
               status == that.status &&
               Objects.equals(offer, that.offer) &&
               Objects.equals(edition, that.edition);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(assetId, blockNumber, confirmedAt, createdAt, head, headId, id, issuedAt,
                      issuer, offset, owner, status, offer, edition);
    }
}
