package com.bitmark.sdk.sample;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import com.bitmark.apiservice.params.query.AssetQueryBuilder;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.params.query.TransactionQueryBuilder;
import com.bitmark.apiservice.response.*;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.BitmarkSDK;

import java.util.HashMap;
import java.util.List;

import static com.bitmark.sdk.sample.samples.AccountSample.*;
import static com.bitmark.sdk.sample.samples.AssetRegistrationSample.registerAsset;
import static com.bitmark.sdk.sample.samples.BitmarkIssuanceSample.issueBitmarks;
import static com.bitmark.sdk.sample.samples.BitmarkTransferSample.*;
import static com.bitmark.sdk.sample.samples.QuerySample.*;

public class Main {
    final static String API_TOKEN = "YOUR_API_TOKEN";
    final static Network NETWORK_MODE = Network.TEST_NET; // Network.TEST_NET or Network.LIVE_NET

    public static void main(String[] args) throws Throwable {
        //////////////////////////////////////
        // 0. SDK INITIALIZATION
        //////////////////////////////////////

        // You must initialize SDK before using it
        final GlobalConfiguration.Builder builder = GlobalConfiguration.builder().withApiToken(API_TOKEN).withNetwork(NETWORK_MODE);
        BitmarkSDK.init(builder);

        //////////////////////////////////////
        // 1. ACCOUNT
        //////////////////////////////////////

        // 1.1 Create totally new account
        Account account = createNewAccount();

        /*
          1.2 Generate Recovery Phrase(twelve words)
          Recovery Phrase(twelve words) is your secret key. You should keep it in safe place
          (ex: write it down to paper, shouldn't store it on your machine) and don't reveal it to anyone else.
         */
        String recoveryPhrase = getRecoveryPhraseFromAccount(account);

        // 1.3 Create account using Recovery Phrase
        account = getAccountFromRecoveryPhrase(recoveryPhrase);


        //////////////////////////////////////
        // 2. REGISTER ASSET & ISSUE BITMARKS
        //////////////////////////////////////

        /*
          2.1 Register asset
          You need to register asset to Bitmark block-chain before you can issue bitmarks for it
         */
        String assetName = "YOUR_ASSET_NAME"; // Asset length must be less than or equal 64 characters
        String assetFilePath = "YOUR_ASSET_FILE_PATH";

        HashMap metadata = new HashMap<String, String>() {{
            put("key1", "value1");
            put("key2", "value2");
        }}; // Metadata length must be less than or equal 2048 characters

        List<RegistrationResponse.Asset> assets = registerAsset(account, assetName, assetFilePath, metadata);

        /*
          2.2 Issue bitmarks for asset
          You need provide asset ID to issue bitmarks for asset
         */
        String assetId = "YOUR_ASSET_ID";
        int quantity = 100; // Number of bitmarks you want to issue, quantity must be less than or equal 100.
        List<String> bitmarkIds = issueBitmarks(account, assetId, quantity);


        //////////////////////////////////////
        // 3. QUERY
        //////////////////////////////////////

        // 3.1 Query bitmark/bitmarks

        /*
          3.1.1 Query bitmarks
          Ex: Query bitmarks which you are owner
         */
        BitmarkQueryBuilder bitmarkQueryBuilder = new BitmarkQueryBuilder().ownedBy(account.getAccountNumber());
        GetBitmarksResponse bitmarksResponse = queryBitmarks(bitmarkQueryBuilder);

        // 3.1.2 Query bitmark
        String bitmarkId = "BITMARK_ID";
        GetBitmarkResponse bitmarkResponse = queryBitmarkById(bitmarkId);

        // 3.2 Query transaction/transactions

        /*
          3.2.1 Query transactions
          Ex: Query transactions which you are owner
         */
        TransactionQueryBuilder transactionQueryBuilder = new TransactionQueryBuilder().ownedBy(account.getAccountNumber());
        GetTransactionsResponse transactionsResponse = queryTransactions(transactionQueryBuilder);

        // 3.2.2 Query Transaction
        String txId = "TRANSACTION_ID";
        GetTransactionResponse txResponse = queryTransactionById(txId);

        // 3.3 Query assets/asset

        /**
         * 3.3.1 Query assets
         * Ex: Query assets which you registered
         */
        AssetQueryBuilder assetQueryBuilder = new AssetQueryBuilder().registrant(account.getAccountNumber());
        List<AssetRecord> assetRecords = queryAssets(assetQueryBuilder);

        // 3.3.2 Query asset
        assetId = "ASSET_ID";
        AssetRecord assetRecord = queryAssetById(assetId);


        //////////////////////////////////////
        // 4. TRANSFER BITMARKS
        //////////////////////////////////////

        /*
          4.1 Transfer bitmark using 1 signature
          You can transfer your bitmark to another account without their acceptance.
          Note: Your bitmark must be confirmed on Bitmark block-chain(status=settled) before you can transfer it. You can query bitmark by bitmark ID to check it's status.
         */
        bitmarkId = "YOUR_BITMARK_ID";
        String receiverAccountNumber = "ACCOUNT_NUMBER_YOU_WANT_TO_TRANSFER_BITMARK_TO";
        String transferResponse = transferOneSignature(account, bitmarkId, receiverAccountNumber);

        /*
          4.2 Transfer bitmark using 2 signatures
          When you transfer your bitmark to another account(receiver) using 2 signatures transfer, the receiver is able to accept or reject your transfer.
          The flow is:
          a. You(sender): Send a transfer offer to receiver
          b. Receiver: Accept/Reject your transfer offer
          Notes:
          1. Your bitmark must be confirmed on Bitmark block-chain(status=settled) before you can transfer it. You can query bitmark by bitmark ID to check it's status.
          2. You can cancel your transfer offer if the receiver doesn't accept/reject it yet.
         */

        // YOUR CODE: Send transfer offer to receiver
        bitmarkId = "YOUR_BITMARK_ID";
        receiverAccountNumber = "ACCOUNT_NUMBER_YOU_WANT_TO_TRANSFER_BITMARK_TO";
        String offerResponse = sendTransferOffer(account, bitmarkId, receiverAccountNumber);

        // 4.2.1 Receiver respond(accept/reject) your transfer offer
        // RECEIVER's CODE
        bitmarkId = "WILL_RECEIVE_BITMARK_ID";
        Account receiverAccount = getAccountFromRecoveryPhrase("RECEIVER_RECOVERY_PHRASE");
        String confirmationResponse = acceptTransferOffer(receiverAccount, bitmarkId);

        // 4.2.2 You cancel your own transfer offer
        // YOUR CODE
        bitmarkId = "YOUR_BITMARK_ID_SENT";
        String cancelResponse = cancelTransferOffer(account, bitmarkId);
    }
}
