package test.integrationtest.features;

import features.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.params.query.TransactionQueryBuilder;
import service.response.GetTransactionResponse;
import service.response.GetTransactionsResponse;
import test.utils.Callable;
import utils.error.HttpException;
import utils.record.TransactionRecord;

import java.util.List;
import java.util.concurrent.CompletionException;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static test.utils.CommonUtils.await;

/**
 * @author Hieu Pham
 * @since 9/18/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransactionTest extends BaseFeatureTest {

    @DisplayName("Verify function Transaction.get(String, Callback1<>) works well with existed " +
                         "transaction id")
    @Test
    public void testQueryTransaction_ExistedTxId_CorrectResponseIsReturn() {
        // Get existed tx
        TransactionQueryBuilder builder =
                new TransactionQueryBuilder().owner(ACCOUNT1.getAccountNumber()).limit(1);
        GetTransactionsResponse getTransactionsResponse =
                await(callback -> Transaction.list(builder,
                        callback));
        List<TransactionRecord> transactions = getTransactionsResponse.getTransactions();
        assertNotNull(transactions, "This guy does not have any transaction");
        assertFalse(transactions.isEmpty(), "This guy does not have any transaction");
        String txId = transactions.get(0).getId();

        // Get tx by id
        GetTransactionResponse getTransactionResponse = await(callback -> Transaction.get(txId,
                callback));
        TransactionRecord transaction = getTransactionResponse.getTransaction();
        assertNotNull(transaction);
        assertEquals(txId, transaction.getId());
    }

    @DisplayName("Verify function Transaction.get(String, Callback1<>) works well with not " +
                         "existed transaction id")
    @Test
    public void testQueryTransaction_NonExistedTxId_ErrorIsThrow() {
        String id =
                "1234567890123456789012345678901234567890123456789012345678901234";
        HttpException exception = (HttpException) assertThrows(CompletionException.class,
                () -> await((Callable<GetTransactionResponse>) callback -> Transaction.get(id,
                        callback))).getCause();
        assertEquals(HTTP_NOT_FOUND, exception.getStatusCode());
    }

    @DisplayName("Verify function Transaction.list(TransactionQueryBuilder, Callback1<>) works " +
                         "well")
    @Test
    public void testQueryTransactions_NoCondition_CorrectResponseIsReturn() {
        // With limit and owner
        int limit = 3;
        String owner = ACCOUNT1.getAccountNumber();
        TransactionQueryBuilder builder =
                new TransactionQueryBuilder().owner(owner).limit(limit);
        GetTransactionsResponse getTransactionsResponse =
                await(callback -> Transaction.list(builder,
                        callback));
        List<TransactionRecord> transactions = getTransactionsResponse.getTransactions();
        assertEquals(limit, transactions.size());
        transactions.forEach(transaction -> assertEquals(owner, transaction.getOwner()));
    }
}
