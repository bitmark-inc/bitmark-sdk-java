package apiservice.utils.record;

import apiservice.utils.annotation.VisibleForTesting;
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

    @VisibleForTesting
    public BitmarkRecord(String assetId, long blockNumber, String confirmedAt, String createdAt,
                         Head head, String headId, String id, String issuedAt, String issuer,
                         long offset, String owner, Status status, OfferRecord offer) {
        this.assetId = assetId;
        this.blockNumber = blockNumber;
        this.confirmedAt = confirmedAt;
        this.createdAt = createdAt;
        this.head = head;
        this.headId = headId;
        this.id = id;
        this.issuedAt = issuedAt;
        this.issuer = issuer;
        this.offset = offset;
        this.owner = owner;
        this.status = status;
        this.offer = offer;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BitmarkRecord bitmark = (BitmarkRecord) o;
        return blockNumber == bitmark.blockNumber &&
                offset == bitmark.offset &&
                Objects.equals(assetId, bitmark.assetId) &&
                Objects.equals(confirmedAt, bitmark.confirmedAt) &&
                Objects.equals(createdAt, bitmark.createdAt) &&
                Objects.equals(head, bitmark.head) &&
                Objects.equals(headId, bitmark.headId) &&
                Objects.equals(id, bitmark.id) &&
                Objects.equals(issuedAt, bitmark.issuedAt) &&
                Objects.equals(issuer, bitmark.issuer) &&
                Objects.equals(owner, bitmark.owner) &&
                status == bitmark.status &&
                Objects.equals(offer, bitmark.offer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetId, blockNumber, confirmedAt, createdAt, head, headId, id,
                issuedAt, issuer, offset, owner, status, offer);
    }
}
