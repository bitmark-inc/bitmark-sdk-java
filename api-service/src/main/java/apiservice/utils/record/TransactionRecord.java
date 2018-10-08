package apiservice.utils.record;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * @author Hieu Pham
 * @since 9/18/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransactionRecord implements Record {

    public enum Status {
        @SerializedName("confirmed")
        CONFIRMED,

        @SerializedName("pending")
        PENDING
    }

    private String id;

    private String owner;

    @SerializedName("asset_id")
    private String assetId;

    private Head head;

    private Status status;

    @SerializedName("block_number")
    private long blockNumber;

    @SerializedName("block_offset")
    private long blockOffset;

    private long offset;

    @SerializedName("expired_at")
    private String expiredAt;

    @SerializedName("pay_id")
    private String payId;

    @SerializedName("previous_id")
    private String previousId;

    @SerializedName("bitmark_id")
    private String bitmarkId;

    public TransactionRecord(String id, String owner, String assetId, Head head, Status status,
                             long blockNumber, long blockOffset, long offset, String expiredAt,
                             String payId,
                             String previousId, String bitmarkId) {
        this.id = id;
        this.owner = owner;
        this.assetId = assetId;
        this.head = head;
        this.status = status;
        this.blockNumber = blockNumber;
        this.blockOffset = blockOffset;
        this.offset = offset;
        this.expiredAt = expiredAt;
        this.payId = payId;
        this.previousId = previousId;
        this.bitmarkId = bitmarkId;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getAssetId() {
        return assetId;
    }

    public Head getHead() {
        return head;
    }

    public Status getStatus() {
        return status;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public long getBlockOffset() {
        return blockOffset;
    }

    public long getOffset() {
        return offset;
    }

    public String getExpiredAt() {
        return expiredAt;
    }

    public String getPayId() {
        return payId;
    }

    public String getPreviousId() {
        return previousId;
    }

    public String getBitmarkId() {
        return bitmarkId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionRecord that = (TransactionRecord) o;
        return blockNumber == that.blockNumber &&
                blockOffset == that.blockOffset &&
                offset == that.offset &&
                Objects.equals(id, that.id) &&
                Objects.equals(owner, that.owner) &&
                Objects.equals(assetId, that.assetId) &&
                head == that.head &&
                status == that.status &&
                Objects.equals(expiredAt, that.expiredAt) &&
                Objects.equals(payId, that.payId) &&
                Objects.equals(previousId, that.previousId) &&
                Objects.equals(bitmarkId, that.bitmarkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner, assetId, head, status, blockNumber, blockOffset, offset, expiredAt, payId, previousId, bitmarkId);
    }
}
