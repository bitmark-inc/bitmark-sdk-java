package com.bitmark.apiservice.response;

import com.bitmark.apiservice.utils.annotation.VisibleForTesting;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.apiservice.utils.record.TransactionRecord;
import com.google.gson.annotations.SerializedName;

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
    private List<com.bitmark.apiservice.utils.record.TransactionRecord> transactions;

    private List<com.bitmark.apiservice.utils.record.AssetRecord> assets;

    @VisibleForTesting
    public GetTransactionsResponse(List<com.bitmark.apiservice.utils.record.TransactionRecord> transactions, List<com.bitmark.apiservice.utils.record.AssetRecord> assets) {
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
