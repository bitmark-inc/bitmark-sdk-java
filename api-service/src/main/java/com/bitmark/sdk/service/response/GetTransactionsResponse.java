package com.bitmark.sdk.service.response;

import com.bitmark.sdk.utils.annotation.VisibleForTesting;
import com.google.gson.annotations.SerializedName;
import com.bitmark.sdk.utils.record.AssetRecord;
import com.bitmark.sdk.utils.record.TransactionRecord;

import java.util.List;
import java.util.Objects;

/**
 * @author Hieu Pham
 * @since 9/18/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class GetTransactionsResponse implements Response {

    @SerializedName("txs")
    private List<TransactionRecord> transactions;

    private List<AssetRecord> assets;

    @VisibleForTesting
    public GetTransactionsResponse(List<TransactionRecord> transactions, List<AssetRecord> assets) {
        this.transactions = transactions;
        this.assets = assets;
    }

    public List<TransactionRecord> getTransactions() {
        return transactions;
    }

    public List<AssetRecord> getAssets() {
        return assets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetTransactionsResponse that = (GetTransactionsResponse) o;
        return Objects.equals(transactions, that.transactions) &&
                Objects.equals(assets, that.assets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactions, assets);
    }
}
