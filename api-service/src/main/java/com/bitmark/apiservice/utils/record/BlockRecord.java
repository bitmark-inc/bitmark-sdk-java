package com.bitmark.apiservice.utils.record;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * @author Hieu Pham
 * @since 2019-07-15
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class BlockRecord implements Record {

    private long number;

    private String hash;

    @SerializedName("bitmark_id")
    private String bitmarkId;

    @SerializedName("createdAt")
    private String createdAt;

    public long getNumber() {
        return number;
    }

    public String getHash() {
        return hash;
    }

    public String getBitmarkId() {
        return bitmarkId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockRecord that = (BlockRecord) o;
        return number == that.number &&
               Objects.equals(hash, that.hash) &&
               Objects.equals(bitmarkId, that.bitmarkId) &&
               Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, hash, bitmarkId, createdAt);
    }
}
