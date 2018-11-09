package com.bitmark.apiservice.response;

import com.bitmark.apiservice.utils.annotation.VisibleForTesting;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.apiservice.utils.record.TransactionRecord;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * @author Hieu Pham
 * @since 9/18/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class GetTransactionResponse implements Response {

    @SerializedName("tx")
    private com.bitmark.apiservice.utils.record.TransactionRecord transaction;

    private com.bitmark.apiservice.utils.record.AssetRecord asset;

    @VisibleForTesting
    public GetTransactionResponse(com.bitmark.apiservice.utils.record.TransactionRecord transaction, com.bitmark.apiservice.utils.record.AssetRecord asset) {
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetTransactionResponse that = (GetTransactionResponse) o;
        return Objects.equals(transaction, that.transaction) &&
                Objects.equals(asset, that.asset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction, asset);
    }
}
