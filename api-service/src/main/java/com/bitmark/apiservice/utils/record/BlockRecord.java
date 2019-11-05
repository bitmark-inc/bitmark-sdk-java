/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.utils.record;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class BlockRecord implements Record {

    private long number;

    private String hash;

    @SerializedName("bitmark_id")
    private String bitmarkId;

    @SerializedName("created_at")
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
