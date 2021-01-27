/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.test.integrationtest;

import com.annimon.stream.Stream;
import com.bitmark.apiservice.params.query.TransactionQueryBuilder;
import com.bitmark.apiservice.response.GetTransactionResponse;
import com.bitmark.apiservice.response.GetTransactionsResponse;
import com.bitmark.apiservice.utils.callback.Callable1;
import com.bitmark.apiservice.utils.error.HttpException;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.apiservice.utils.record.TransactionRecord;
import com.bitmark.sdk.features.Transaction;
import com.bitmark.sdk.test.BaseTest;
import org.junit.Test;

import java.util.List;

import static com.bitmark.apiservice.utils.Awaitility.await;
import static com.bitmark.sdk.test.integrationtest.DataProvider.ACCOUNT1;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static org.junit.Assert.*;

public class TransactionTest extends BaseTest {

    @Test
    public void testQueryTransactionWithoutAsset_ExistedTxId_CorrectResponseIsReturn()
            throws Throwable {
        // Get existed tx
        TransactionQueryBuilder builder =
                new TransactionQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                        .limit(1);
        GetTransactionsResponse getTransactionsResponse =
                await(callback -> Transaction.list(builder, callback));
        List<TransactionRecord> transactions = getTransactionsResponse.getTransactions();
        assertNotNull("This guy does not have any transaction", transactions);
        assertFalse(
                "This guy does not have any transaction",
                transactions.isEmpty()
        );
        String txId = transactions.get(0).getId();

        // Get tx by id
        GetTransactionResponse getTransactionResponse = await(callback -> Transaction
                .get(
                        txId,
                        callback
                ));
        TransactionRecord transaction = getTransactionResponse.getTransaction();
        assertNotNull(transaction);
        assertEquals(txId, transaction.getId());
    }


    @Test
    public void testQueryTransactionWithAsset_ExistedTxId_CorrectResponseIsReturn()
            throws Throwable {
        // Get existed tx
        TransactionQueryBuilder builder =
                new TransactionQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                        .limit(1);
        GetTransactionsResponse getTransactionsResponse =
                await(callback -> Transaction.list(builder, callback));
        List<TransactionRecord> transactions = getTransactionsResponse.getTransactions();
        assertNotNull("This guy does not have any transaction", transactions);
        assertFalse(
                "This guy does not have any transaction",
                transactions.isEmpty()
        );
        String txId = transactions.get(0).getId();

        // Get tx by id
        GetTransactionResponse getTransactionResponse =
                await(callback -> Transaction.getWithAsset(txId, callback));
        TransactionRecord transaction = getTransactionResponse.getTransaction();
        AssetRecord asset = getTransactionResponse.getAsset();
        assertNotNull(transaction);
        assertNotNull(asset);
        assertEquals(txId, transaction.getId());
        assertEquals(transaction.getAssetId(), asset.getId());
    }

    @Test
    public void testQueryTransaction_NonExistedTxId_ErrorIsThrow()
            throws Throwable {
        expectedException.expect(HttpException.class);
        expectedException.expectMessage(String.valueOf(HTTP_INTERNAL_ERROR));

        String id =
                "1234567890123456789012345678901234567890123456789012345678901234";
        await((Callable1<GetTransactionResponse>) callback -> Transaction.get(
                id,
                callback
        ));
    }

    @Test
    public void testQueryTransactions_NoCondition_CorrectResponseIsReturn()
            throws Throwable {
        // With limit and owner
        int limit = 1;
        String owner = ACCOUNT1.getAccountNumber();
        TransactionQueryBuilder builder =
                new TransactionQueryBuilder().ownedBy(owner).limit(limit);
        GetTransactionsResponse getTransactionsResponse =
                await(callback -> Transaction.list(builder, callback));
        List<TransactionRecord> transactions = getTransactionsResponse.getTransactions();
        assertEquals(limit, transactions.size());
        Stream.of(transactions)
                .forEach(transaction -> assertEquals(
                        owner,
                        transaction.getOwner()
                ));
    }

    @Test
    public void testQueryTransactionsByBlock_NoCondition_CorrectResponseIsReturn()
            throws Throwable {
        // With limit and owner
        int limit = 1;
        String owner = ACCOUNT1.getAccountNumber();
        TransactionQueryBuilder builder =
                new TransactionQueryBuilder().ownedBy(owner)
                        .loadBlock(true)
                        .limit(limit);
        GetTransactionsResponse getTransactionsResponse =
                await(callback -> Transaction.list(builder, callback));
        assertFalse(getTransactionsResponse.getBlocks().isEmpty());
        List<TransactionRecord> transactions = getTransactionsResponse.getTransactions();
        assertEquals(limit, transactions.size());
        Stream.of(transactions)
                .forEach(transaction -> assertEquals(
                        owner,
                        transaction.getOwner()
                ));
    }
}
