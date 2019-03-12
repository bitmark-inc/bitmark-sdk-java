package com.bitmark.apiservice.utils.record;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Objects;

/**
 * @author Hieu Pham
 * @since 9/16/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class AssetRecord implements Record {

    public enum Status {
        @SerializedName("confirmed")
        CONFIRMED,

        @SerializedName("pending")
        PENDING
    }

    @SerializedName("block_number")
    private long blockNumber;

    @SerializedName("block_offset")
    private long blockOffset;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("expired_at")
    private String expiredAt;

    private String fingerprint;

    private String id;

    private Map<String, String> metadata;

    private String name;

    private long offset;

    private String registrant;

    private Status status;

    public long getBlockNumber() {
        return blockNumber;
    }

    public long getBlockOffset() {
        return blockOffset;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getExpiredAt() {
        return expiredAt;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getId() {
        return id;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public String getName() {
        return name;
    }

    public long getOffset() {
        return offset;
    }

    public String getRegistrant() {
        return registrant;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetRecord asset = (AssetRecord) o;
        return blockNumber == asset.blockNumber &&
                blockOffset == asset.blockOffset &&
                offset == asset.offset &&
                Objects.equals(createdAt, asset.createdAt) &&
                Objects.equals(expiredAt, asset.expiredAt) &&
                Objects.equals(fingerprint, asset.fingerprint) &&
                Objects.equals(id, asset.id) &&
                Objects.equals(metadata, asset.metadata) &&
                Objects.equals(name, asset.name) &&
                Objects.equals(registrant, asset.registrant) &&
                status == asset.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockNumber, blockOffset, createdAt, expiredAt, fingerprint, id,
                metadata, name, offset, registrant, status);
    }
}
