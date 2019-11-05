/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.response;

import com.bitmark.apiservice.utils.annotation.VisibleForTesting;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.apiservice.utils.record.TransactionRecord;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class GetTransactionResponse implements Response {

    @SerializedName("tx")
    private TransactionRecord transaction;

    private AssetRecord asset;

    @VisibleForTesting
    public GetTransactionResponse(
            TransactionRecord transaction,
            AssetRecord asset
    ) {
        this.transaction = transaction;
        this.asset = asset;
    }

    public AssetRecord getAsset() {
        return asset;
    }

    public TransactionRecord getTransaction() {
        return transaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GetTransactionResponse that = (GetTransactionResponse) o;
        return Objects.equals(transaction, that.transaction) &&
                Objects.equals(asset, that.asset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction, asset);
    }
}
