/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.response;

import com.bitmark.apiservice.utils.annotation.VisibleForTesting;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.apiservice.utils.record.BlockRecord;
import com.bitmark.apiservice.utils.record.TransactionRecord;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class GetTransactionsResponse implements Response {

    @SerializedName("txs")
    private List<TransactionRecord> transactions;

    private List<AssetRecord> assets;

    private List<BlockRecord> blocks;

    @VisibleForTesting
    public GetTransactionsResponse(
            List<TransactionRecord> transactions,
            List<AssetRecord> assets
    ) {
        this.transactions = transactions;
        this.assets = assets;
    }

    public List<TransactionRecord> getTransactions() {
        return transactions;
    }

    public List<AssetRecord> getAssets() {
        return assets;
    }

    public List<BlockRecord> getBlocks() {
        return blocks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GetTransactionsResponse that = (GetTransactionsResponse) o;
        return Objects.equals(transactions, that.transactions) &&
                Objects.equals(assets, that.assets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactions, assets);
    }
}
