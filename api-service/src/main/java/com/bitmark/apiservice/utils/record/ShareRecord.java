/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.utils.record;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class ShareRecord implements Record {

    @SerializedName("share_id")
    private String id;

    private String owner;

    private int balance;

    private int available;

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public int getBalance() {
        return balance;
    }

    public int getAvailable() {
        return available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShareRecord that = (ShareRecord) o;
        return balance == that.balance &&
                available == that.available &&
                Objects.equals(id, that.id) &&
                Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner, balance, available);
    }
}
