package sdk.test.integrationtest.features;

import apiservice.params.*;
import apiservice.params.query.BitmarkQueryBuilder;
import apiservice.response.GetBitmarkResponse;
import apiservice.response.GetBitmarksResponse;
import apiservice.response.RegistrationResponse;
import apiservice.utils.Address;
import apiservice.utils.callback.Callable1;
import apiservice.utils.error.HttpException;
import apiservice.utils.record.AssetRecord;
import apiservice.utils.record.BitmarkRecord;
import apiservice.utils.record.OfferRecord;
import cryptography.crypto.Sha3256;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import sdk.features.Asset;
import sdk.features.Bitmark;
import sdk.test.utils.extensions.TemporaryFolderExtension;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static apiservice.utils.Awaitility.await;
import static apiservice.utils.record.BitmarkRecord.Status.SETTLED;
import static apiservice.utils.record.Head.MOVED;
import static cryptography.crypto.encoder.Hex.HEX;
import static java.net.HttpURLConnection.*;
import static org.junit.jupiter.api.Assertions.*;

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
    public void testIssueBitmark_OwnedMultipleBitmark_CorrectSuccessResponseIsReturn(File asset) throws Throwable {
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
                await((Callable1<RegistrationResponse>) callback -> Asset.register(registrationParams,
                        callback));
        List<RegistrationResponse.Asset> assets = registrationResponse.getAssets();
        String assetId = assets.get(0).getId();

        // Issue bitmarks
        final int quantity = 5;
        IssuanceParams issuanceParams = new IssuanceParams(assetId, owner, quantity);
        issuanceParams.sign(KEY1);
        List<String> txIds =
                await((Callable1<List<String>>) callback -> Bitmark.issue(issuanceParams,
                        callback));
        assertEquals(txIds.size(), quantity);
        assertFalse(txIds.get(0).isEmpty());
    }

    @DisplayName("Verify function Bitmark.issue(IssuanceParams, Callback1<>) works well with " +
                         "owned single bitmark")
    @Test
    public void testIssueBitmark_OwnedSingleBitmark_CorrectSuccessResponseIsReturn(File asset) throws Throwable {
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
                await(callback -> Asset.register(registrationParams, callback));
        List<RegistrationResponse.Asset> assets = registrationResponse.getAssets();
        String assetId = assets.get(0).getId();

        // Issue bitmarks
        IssuanceParams issuanceParams = new IssuanceParams(assetId, owner);
        issuanceParams.sign(KEY1);
        List<String> txIds = await(callback -> Bitmark.issue(issuanceParams, callback));
        assertEquals(txIds.size(), 1);
        assertFalse(txIds.get(0).isEmpty());
    }

    @DisplayName("Verify function Bitmark.issue(IssuanceParams, Callback1<>) works well with " +
                         "not owned bitmarks")
    @Test
    public void testIssueBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn() {
        Address issuer = Address.fromAccountNumber(ACCOUNT1.getAccountNumber());
        IssuanceParams issuanceParams = new IssuanceParams(
                "bda050d2235402751ed09e73486c2cced34424c35d4d799eaa37ab73c3dba663", issuer);
        issuanceParams.sign(KEY1);
        HttpException exception = assertThrows(HttpException.class, () ->
                await((Callable1<List<String>>) callback -> Bitmark.issue(issuanceParams,
                        callback)));
        assertEquals(1000, exception.getErrorCode());
        assertEquals(HTTP_BAD_REQUEST, exception.getStatusCode());
    }

    @DisplayName("Verify function Bitmark.transfer(TransferParams, Callback1<>) works well with " +
                         "owned bitmarks and TransferParams is build from link")
    @Test
    public void testTransferBitmarkFromLink_OwnedBitmark_CorrectSuccessResponseIsReturn() throws Throwable {
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
        assertEquals(Sha3256.HASH_LENGTH, HEX.decode(txId).length);

    }


    @DisplayName("Verify function Bitmark.transfer(TransferParams, Callback1<>) works well with " +
                         "not owned bitmarks")
    @Test
    public void testTransferBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn() throws Throwable {
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
        HttpException exception = assertThrows(HttpException.class,
                () -> await((Callable1<String>) callback -> Bitmark.transfer(params,
                        callback)));
        assertEquals(HTTP_INTERNAL_ERROR, exception.getStatusCode());
        assertEquals(7000, exception.getErrorCode());

    }

    @DisplayName("Verify function Bitmark.offer(TransferOfferParams, Callback1<>) works well with" +
                         " owned bitmarks")
    @Test
    public void testOfferBitmark_OwnedBitmark_CorrectSuccessResponseIsReturn() throws Throwable {
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
    public void testOfferBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn() throws Throwable {
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
        HttpException exception = assertThrows(HttpException.class,
                () -> await((Callable1<String>) callback -> Bitmark.offer(params,
                        callback)));
        assertEquals(HTTP_FORBIDDEN, exception.getStatusCode());
        assertEquals(2013, exception.getErrorCode());
    }

    @DisplayName("Verify function Bitmark.respond(TransferResponseParams, Callback1<>) works well" +
                         " with accept response")
    @Test
    public void testReplyOffer_AcceptOffer_CorrectSuccessResponseIsReturn() throws Throwable {
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
        assertEquals(Sha3256.HASH_LENGTH, HEX.decode(txId).length);
    }

    @DisplayName("Verify function Bitmark.respond(TransferResponseParams, Callback1<>) works well" +
                         " with cancel response")
    @Test
    public void testReplyOffer_CancelOffer_CorrectSuccessResponseIsReturn() throws Throwable {
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
    public void testReplyOffer_RejectOffer_CorrectSuccessResponseIsReturn() throws Throwable {
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
        String status = await((Callable1<String>) callback -> Bitmark.respond(responseParams,
                callback));
        assertNotNull(status);
        assertEquals("ok", status);
    }

    @DisplayName("Verify function Bitmark.get(String, boolean, Callback1<>) works well")
    @Test
    public void testQueryBitmarkByIdWithAsset_ExistedBitmarkId_CorrectResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).limit(1);
        GetBitmarksResponse bitmarksResponse = await(callback -> Bitmark.list(builder, callback));
        assertFalse(bitmarksResponse.getBitmarks().isEmpty(), "This guy has not owned bitmarks");

        // Get bitmark by id
        String id = bitmarksResponse.getBitmarks().get(0).getId();
        GetBitmarkResponse bitmarkResponse = await(callback -> Bitmark.get(id, true, callback));
        BitmarkRecord bitmark = bitmarkResponse.getBitmark();
        AssetRecord asset = bitmarkResponse.getAsset();
        assertNotNull(bitmark);
        assertNotNull(asset);
        assertEquals(id, bitmark.getId());
        assertEquals(bitmark.getAssetId(), asset.getId());
    }

    @DisplayName("Verify function Bitmark.get(String, Callback1<>) works well")
    @Test
    public void testQueryBitmarkByIdWithoutAsset_ExistedBitmarkId_CorrectResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).limit(1);
        GetBitmarksResponse bitmarksResponse = await(callback -> Bitmark.list(builder, callback));
        assertFalse(bitmarksResponse.getBitmarks().isEmpty(), "This guy has not owned bitmarks");

        // Get bitmark by id
        String id = bitmarksResponse.getBitmarks().get(0).getId();
        GetBitmarkResponse bitmarkResponse = await(callback -> Bitmark.get(id, callback));
        BitmarkRecord bitmark = bitmarkResponse.getBitmark();
        AssetRecord asset = bitmarkResponse.getAsset();
        assertNotNull(bitmark);
        assertNull(asset);
        assertEquals(id, bitmark.getId());
    }

    @DisplayName("Verify function Bitmark.list(BitmarkQueryBuilder, Callback1<>) with list " +
                         "bitmark id works well")
    @Test
    public void testQueryBitmarksByIds_ValidBitmarkIds_CorrectResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        int limit = 1;
        BitmarkQueryBuilder builder1 =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).limit(limit);
        GetBitmarksResponse bitmarksResponse1 = await(callback -> Bitmark.list(builder1, callback));
        assertFalse(bitmarksResponse1.getBitmarks().isEmpty(), "This guy has not owned bitmarks");
        String[] bitmarkIds =
                bitmarksResponse1.getBitmarks().stream().map(BitmarkRecord::getId).toArray(String[]::new);

        BitmarkQueryBuilder builder2 =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).bitmarkIds(bitmarkIds);
        GetBitmarksResponse bitmarksResponse2 = await(callback -> Bitmark.list(builder2, callback));
        List<BitmarkRecord> bitmarks = bitmarksResponse2.getBitmarks();
        List<AssetRecord> assets = bitmarksResponse2.getAssets();
        assertFalse(bitmarks.isEmpty());
        assertEquals(bitmarkIds.length, bitmarks.size());
        assertNull(assets);
        assertTrue(Arrays.equals(bitmarkIds,
                bitmarks.stream().map(BitmarkRecord::getId).toArray()));
    }

    @DisplayName("Verify function Bitmark.list(BitmarkQueryBuilder, Callback1<>) with the limit " +
                         "record and load asset works well")
    @Test
    public void testQueryBitmarkByLimitAndLoadAsset_ValidLimitValue_CorrectResponseIsReturn() throws Throwable {
        int limit = 1;
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).limit(limit).loadAsset(true);
        GetBitmarksResponse bitmarksResponse = await(callback -> Bitmark.list(builder, callback));
        List<BitmarkRecord> bitmarks = bitmarksResponse.getBitmarks();
        List<AssetRecord> assets = bitmarksResponse.getAssets();
        assertFalse(bitmarks.isEmpty());
        assertEquals(limit, bitmarks.size());
        assertNotNull(assets);
    }


}
