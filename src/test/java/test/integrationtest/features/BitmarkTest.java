package test.integrationtest.features;

import config.SdkConfig;
import features.Asset;
import features.Bitmark;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import service.params.*;
import service.params.query.BitmarkQueryBuilder;
import service.response.GetBitmarkResponse;
import service.response.GetBitmarksResponse;
import service.response.IssueResponse;
import service.response.RegistrationResponse;
import test.utils.Callable;
import test.utils.extensions.TemporaryFolderExtension;
import utils.Address;
import utils.error.HttpException;
import utils.record.BitmarkRecord;
import utils.record.OfferRecord;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;

import static crypto.encoder.Hex.HEX;
import static java.net.HttpURLConnection.*;
import static org.junit.jupiter.api.Assertions.*;
import static test.utils.CommonUtils.await;
import static utils.record.BitmarkRecord.Head.MOVED;
import static utils.record.BitmarkRecord.Status.SETTLED;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

@ExtendWith({TemporaryFolderExtension.class})
public class BitmarkTest extends BaseFeatureTest {

    @DisplayName("Verify function Bitmark.issue(IssuanceParams, Callback1<>) works well with " +
                         "owned multiple bitmark ")
    @Test
    public void testIssueBitmark_OwnedMultipleBitmark_CorrectSuccessResponseIsReturn(File asset) {
        // Register asset
        Address owner = ACCOUNT1.toAddress();
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from java sdk test");
        }};
        RegistrationParams registrationParams = new RegistrationParams(asset.getName(), metadata,
                owner);
        registrationParams.generateFingerprint(asset);
        registrationParams.sign(KEY1);
        RegistrationResponse registrationResponse =
                await(callback -> Asset.register(registrationParams,
                        callback));
        List<RegistrationResponse.Asset> assets = registrationResponse.getAssets();
        String assetId = assets.get(0).getId();

        // Issue bitmarks
        final int quantity = 5;
        IssuanceParams issuanceParams = new IssuanceParams(assetId, owner, quantity);
        issuanceParams.sign(KEY1);
        IssueResponse issueResponse = await(callback -> Bitmark.issue(issuanceParams, callback));
        assertEquals(issueResponse.getBitmarks().size(), quantity);
        assertFalse(issueResponse.getBitmarks().get(0).getId().isEmpty());
    }

    @DisplayName("Verify function Bitmark.issue(IssuanceParams, Callback1<>) works well with " +
                         "owned single bitmark")
    @Test
    public void testIssueBitmark_OwnedSingleBitmark_CorrectSuccessResponseIsReturn(File asset) {
        // Register asset
        Address owner = ACCOUNT1.toAddress();
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from java sdk test");
        }};
        RegistrationParams registrationParams = new RegistrationParams(asset.getName(), metadata,
                owner);
        registrationParams.generateFingerprint(asset);
        registrationParams.sign(KEY1);
        RegistrationResponse registrationResponse =
                await(callback -> Asset.register(registrationParams,
                        callback));
        List<RegistrationResponse.Asset> assets = registrationResponse.getAssets();
        String assetId = assets.get(0).getId();

        // Issue bitmarks
        IssuanceParams issuanceParams = new IssuanceParams(assetId, owner);
        issuanceParams.sign(KEY1);
        IssueResponse issueResponse = await(callback -> Bitmark.issue(issuanceParams, callback));
        assertEquals(issueResponse.getBitmarks().size(), 1);
        assertFalse(issueResponse.getBitmarks().get(0).getId().isEmpty());
    }

    @DisplayName("Verify function Bitmark.issue(IssuanceParams, Callback1<>) works well with " +
                         "not owned bitmarks")
    @Test
    public void testIssueBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn() {
        Address issuer = Address.fromAccountNumber(ACCOUNT1.getAccountNumber());
        IssuanceParams issuanceParams = new IssuanceParams(
                "bda050d2235402751ed09e73486c2cced34424c35d4d799eaa37ab73c3dba663", issuer);
        issuanceParams.sign(KEY1);
        HttpException exception = (HttpException) assertThrows(CompletionException.class, () ->
                await((Callable<IssueResponse>) callback -> Bitmark.issue(issuanceParams,
                        callback))).getCause();
        assertEquals(1000, exception.getErrorCode());
        assertEquals(HTTP_BAD_REQUEST, exception.getStatusCode());
    }

    @DisplayName("Verify function Bitmark.transfer(TransferParams, Callback1<>) works well with " +
                         "owned bitmarks")
    @Test
    public void testTransferBitmark_OwnedBitmark_CorrectSuccessResponseIsReturn() {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).pending(true);
        GetBitmarksResponse bitmarksResponse = await(callback -> Bitmark.list(builder, callback));
        assertFalse(bitmarksResponse.getBitmarks().isEmpty(), "This guy has not owned bitmarks");

        // Transfer bitmark
        TransferParams params = new TransferParams(ACCOUNT2.toAddress());
        BitmarkRecord bitmark =
                bitmarksResponse.getBitmarks().stream().filter(b -> !b.isOffer() && b.getStatus() == SETTLED).findFirst().orElse(null);
        assertNotNull(bitmark, "No bitmark matches the specification");
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        String txId = await(callback -> Bitmark.transfer(params, callback));
        assertNotNull(txId);
        assertEquals(SdkConfig.Transfer.TRANSACTION_ID_LENGTH, HEX.decode(txId).length);

    }

    @DisplayName("Verify function Bitmark.transfer(TransferParams, Callback1<>) works well with " +
                         "not owned bitmarks")
    @Test
    public void testTransferBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn() {
        // Get not owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT2.getAccountNumber()).pending(true);
        GetBitmarksResponse bitmarksResponse = await(callback -> Bitmark.list(builder, callback));
        assertFalse(bitmarksResponse.getBitmarks().isEmpty(), "This guy has not owned bitmarks");

        // Transfer bitmark
        TransferParams params = new TransferParams(ACCOUNT3.toAddress());
        BitmarkRecord bitmark =
                bitmarksResponse.getBitmarks().stream().filter(b -> !b.isOffer() && b.getStatus() == SETTLED).findFirst().orElse(null);
        assertNotNull(bitmark, "No bitmark matches the specification");
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        HttpException exception = (HttpException) assertThrows(CompletionException.class,
                () -> await((Callable<String>) callback -> Bitmark.transfer(params,
                        callback))).getCause();
        assertEquals(HTTP_INTERNAL_ERROR, exception.getStatusCode());
        assertEquals(7000, exception.getErrorCode());

    }

    @DisplayName("Verify function Bitmark.offer(TransferOfferParams, Callback1<>) works well with" +
                         " owned bitmarks")
    @Test
    public void testOfferBitmark_OwnedBitmark_CorrectSuccessResponseIsReturn() {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).pending(true);
        GetBitmarksResponse bitmarksResponse = await(callback -> Bitmark.list(builder, callback));
        assertFalse(bitmarksResponse.getBitmarks().isEmpty(), "This guy has not owned bitmarks");

        // Offer bitmark
        TransferOfferParams params = new TransferOfferParams(ACCOUNT2.toAddress());
        BitmarkRecord bitmark =
                bitmarksResponse.getBitmarks().stream().filter(b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED).findFirst().orElse(null);
        assertNotNull(bitmark, "No bitmark matches the specification");
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        String offerId = await(callback -> Bitmark.offer(params, callback));
        assertNotNull(offerId);
        assertFalse(offerId.isEmpty());
    }

    @DisplayName("Verify function Bitmark.offer(TransferOfferParams, Callback1<>) works well with" +
                         " owned bitmarks")
    @Test
    public void testOfferBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn() {
        // Get not owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT2.getAccountNumber()).pending(true);
        GetBitmarksResponse bitmarksResponse = await(callback -> Bitmark.list(builder, callback));
        assertFalse(bitmarksResponse.getBitmarks().isEmpty(), "This guy has not owned bitmarks");

        // Offer bitmark
        TransferOfferParams params = new TransferOfferParams(ACCOUNT3.toAddress());
        BitmarkRecord bitmark =
                bitmarksResponse.getBitmarks().stream().filter(b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED).findFirst().orElse(null);
        assertNotNull(bitmark, "No bitmark matches the specification");
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        HttpException exception = (HttpException) assertThrows(CompletionException.class,
                () -> await((Callable<String>) callback -> Bitmark.offer(params,
                        callback))).getCause();
        assertEquals(HTTP_FORBIDDEN, exception.getStatusCode());
        assertEquals(2013, exception.getErrorCode());
    }

    @DisplayName("Verify function Bitmark.respond(TransferResponseParams, Callback1<>) works well" +
                         " with accept response")
    @Test
    public void testReplyOffer_AcceptOffer_CorrectSuccessResponseIsReturn() {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).pending(true);
        GetBitmarksResponse bitmarksResponse = await(callback -> Bitmark.list(builder, callback));
        assertFalse(bitmarksResponse.getBitmarks().isEmpty(), "This guy has not owned bitmarks");

        // Offer bitmark
        TransferOfferParams offerParams = new TransferOfferParams(ACCOUNT2.toAddress());
        BitmarkRecord bitmark =
                bitmarksResponse.getBitmarks().stream().filter(b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED).findFirst().orElse(null);
        assertNotNull(bitmark, "No bitmark matches the specification");
        String link = bitmark.getHeadId();
        offerParams.setLink(link);
        offerParams.sign(KEY1);
        String offerId = await(callback -> Bitmark.offer(offerParams, callback));
        assertNotNull(offerId, "Offer is not successful");
        assertFalse(offerId.isEmpty(), "Offer is not successful");

        // Respond offer
        GetBitmarkResponse response = await(callback -> Bitmark.get(bitmark.getId(), false,
                callback));
        OfferRecord offerRecord = response.getBitmark().getOffer();
        TransferResponseParams responseParams = TransferResponseParams.accept(offerRecord);
        responseParams.sign(KEY2);
        String txId = await(callback -> Bitmark.respond(responseParams, callback));
        assertNotNull(txId);
        assertEquals(SdkConfig.Transfer.TRANSACTION_ID_LENGTH, HEX.decode(txId).length);
    }

    @DisplayName("Verify function Bitmark.respond(TransferResponseParams, Callback1<>) works well" +
                         " with cancel response")
    @Test
    public void testReplyOffer_CancelOffer_CorrectSuccessResponseIsReturn() {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).pending(true);
        GetBitmarksResponse bitmarksResponse = await(callback -> Bitmark.list(builder, callback));
        assertFalse(bitmarksResponse.getBitmarks().isEmpty(), "This guy has not owned bitmarks");

        // Offer bitmark
        TransferOfferParams offerParams = new TransferOfferParams(ACCOUNT2.toAddress());
        BitmarkRecord bitmark =
                bitmarksResponse.getBitmarks().stream().filter(b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED).findFirst().orElse(null);
        assertNotNull(bitmark, "No bitmark matches the specification");
        String link = bitmark.getHeadId();
        offerParams.setLink(link);
        offerParams.sign(KEY1);
        String offerId = await(callback -> Bitmark.offer(offerParams, callback));
        assertNotNull(offerId, "Offer is not successful");
        assertFalse(offerId.isEmpty(), "Offer is not successful");

        // Respond offer
        GetBitmarkResponse response = await(callback -> Bitmark.get(bitmark.getId(), callback));
        OfferRecord offerRecord = response.getBitmark().getOffer();
        TransferResponseParams responseParams = TransferResponseParams.cancel(offerRecord,
                bitmark.getOwner());
        responseParams.setSigningKey(KEY1);
        String status = await(callback -> Bitmark.respond(responseParams, callback));
        assertNotNull(status);
        assertEquals("ok", status);
    }

    @DisplayName("Verify function Bitmark.respond(TransferResponseParams, Callback1<>) works well" +
                         " with reject response")
    @Test
    public void testReplyOffer_RejectOffer_CorrectSuccessResponseIsReturn() {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).pending(true);
        GetBitmarksResponse bitmarksResponse = await(callback -> Bitmark.list(builder, callback));
        assertFalse(bitmarksResponse.getBitmarks().isEmpty(), "This guy has not owned bitmarks");

        // Offer bitmark
        TransferOfferParams offerParams = new TransferOfferParams(ACCOUNT2.toAddress());
        BitmarkRecord bitmark =
                bitmarksResponse.getBitmarks().stream().filter(b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED).findFirst().orElse(null);
        assertNotNull(bitmark, "No bitmark matches the specification");
        String link = bitmark.getHeadId();
        offerParams.setLink(link);
        offerParams.sign(KEY1);
        String offerId = await(callback -> Bitmark.offer(offerParams, callback));
        assertNotNull(offerId, "Offer is not successful");
        assertFalse(offerId.isEmpty(), "Offer is not successful");

        // Respond offer
        GetBitmarkResponse response = await(callback -> Bitmark.get(bitmark.getId(), callback));
        OfferRecord offerRecord = response.getBitmark().getOffer();
        TransferResponseParams responseParams = TransferResponseParams.reject(offerRecord);
        responseParams.setSigningKey(KEY2);
        String status = await(callback -> Bitmark.respond(responseParams, callback));
        assertNotNull(status);
        assertEquals("ok", status);
    }


}
