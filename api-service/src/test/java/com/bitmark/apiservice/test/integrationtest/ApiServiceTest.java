package com.bitmark.apiservice.test.integrationtest;

import com.bitmark.apiservice.ApiService;
import com.bitmark.apiservice.params.*;
import com.bitmark.apiservice.params.query.AssetQueryBuilder;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.params.query.TransactionQueryBuilder;
import com.bitmark.apiservice.response.*;
import com.bitmark.apiservice.test.utils.extensions.TemporaryFolderExtension;
import com.bitmark.apiservice.test.utils.extensions.annotations.TemporaryFile;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.Data;
import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.callback.Callable1;
import com.bitmark.apiservice.utils.error.HttpException;
import com.bitmark.apiservice.utils.record.*;
import com.bitmark.cryptography.crypto.Random;
import com.bitmark.cryptography.crypto.Sha3256;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.bitmark.apiservice.test.integrationtest.DataProvider.*;
import static com.bitmark.apiservice.utils.Awaitility.await;
import static com.bitmark.apiservice.utils.record.BitmarkRecord.Status.SETTLED;
import static com.bitmark.apiservice.utils.record.Head.MOVED;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static java.net.HttpURLConnection.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hieu Pham
 * @since 3/21/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
@ExtendWith({TemporaryFolderExtension.class})
public class ApiServiceTest extends BaseTest {

    //region ============= ASSET TESTS ===============
    @Test
    public void testRegisterAsset_NewAsset_CorrectResponseIsReturn(File asset) throws Throwable {
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from java api service test");
        }};
        RegistrationParams params = new RegistrationParams(asset.getName(), metadata);
        params.setFingerprintFromFile(asset);
        params.sign(KEY1);
        RegistrationResponse response =
                await(callback -> new ApiService().registerAsset(params, callback));
        List<RegistrationResponse.Asset> assets = response.getAssets();
        assertNotNull(assets.get(0).getId());
        assertFalse(assets.get(0).isDuplicate());
    }

    @Test
    public void testRegisterAsset_ExistedAsset_CorrectResponseIsReturn(
            @TemporaryFile("This is an existing file on Bitmark Block chain") File asset)
            throws Throwable {
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from java api service test");
        }};
        RegistrationParams params = new RegistrationParams(asset.getName(), metadata);
        params.setFingerprintFromFile(asset);
        params.sign(KEY1);

        HttpException exception = assertThrows(HttpException.class,
                                               () -> await(
                                                       (Callable1<RegistrationResponse>) callback -> new ApiService()
                                                               .registerAsset(params, callback)));

        assertEquals(HTTP_BAD_REQUEST, exception.getStatusCode());
        assertEquals(1000, exception.getErrorCode());
        assertEquals("asset already registered", exception.getReason());
    }


    @Test
    public void testQueryAssetById_ExistedAssetId_CorrectResponseIsReturn() throws Throwable {
        // Query existed assets
        AssetQueryBuilder builder =
                new AssetQueryBuilder().registeredBy(ADDRESS1.getAddress()).limit(1);
        List<AssetRecord> assets =
                await(callback -> new ApiService().listAssets(builder.build(), callback));
        assertFalse(assets.isEmpty(), "This guy has not registered any assets");

        // Get asset by id
        String id = assets.get(0).getId();
        AssetRecord asset =
                await(callback -> new ApiService().getAsset(id, callback));
        assertNotNull(asset);
        assertEquals(id, asset.getId());
    }

    @Test
    public void testQueryAssetById_NotExistedAssetId_ErrorIsThrow() {
        String id =
                "12345678901234567890123456789012345678901234567890123456789012341234567890123456789012345678901234567890123456789012345678901234";
        HttpException exception = assertThrows(HttpException.class,
                                               () -> await(
                                                       (Callable1<AssetRecord>) callback -> new ApiService()
                                                               .getAsset(id, callback)));
        assertEquals(HTTP_NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void testQueryAssets_NoCondition_CorrectResponseIsReturn() throws Throwable {
        int limit = 1;
        String registrant = ADDRESS1.getAddress();
        AssetQueryBuilder builder = new AssetQueryBuilder().limit(limit).registeredBy(registrant);
        List<AssetRecord> assets =
                await(callback -> new ApiService().listAssets(builder.build(), callback));
        assertEquals(limit, assets.size());
        assets.forEach(asset -> assertEquals(registrant, asset.getRegistrant()));
    }

    @Test
    public void testQueryAssets_ByAtAndTo_CorrectResponseIsReturn() throws Throwable {
        int limit = 10;
        long at = 5;
        AssetQueryBuilder builder =
                new AssetQueryBuilder().limit(limit).at(at).to("earlier").pending(true);
        List<AssetRecord> assets =
                await(callback -> new ApiService().listAssets(builder.build(), callback));
        assets.forEach(asset -> assertTrue(asset.getOffset() <= at));
    }

    //endregion ============= ASSET TESTS ===============

    //region ============= BITMARK TESTS ===============
    @Test
    public void testIssueBitmark_OwnedMultipleBitmark_CorrectSuccessResponseIsReturn(File asset)
            throws Throwable {
        // Register asset
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from java api service test");
        }};
        RegistrationParams registrationParams = new RegistrationParams(asset.getName(), metadata);
        registrationParams.setFingerprintFromFile(asset);
        registrationParams.sign(KEY1);
        RegistrationResponse registrationResponse =
                await(callback -> new ApiService().registerAsset(registrationParams,
                                                                 callback));
        List<RegistrationResponse.Asset> assets = registrationResponse.getAssets();
        String assetId = assets.get(0).getId();

        // Issue bitmarks
        final int quantity = 5;
        IssuanceParams issuanceParams = new IssuanceParams(assetId, ADDRESS1, quantity);
        issuanceParams.sign(KEY1);
        List<String> txIds =
                await(callback -> new ApiService().issueBitmark(issuanceParams,
                                                                callback));
        assertEquals(txIds.size(), quantity);
        assertFalse(txIds.get(0).isEmpty());
    }

    @Test
    public void testIssueBitmark_OwnedSingleBitmark_CorrectSuccessResponseIsReturn(File asset)
            throws Throwable {
        // Register asset
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from java api service test");
        }};
        RegistrationParams registrationParams = new RegistrationParams(asset.getName(), metadata);
        registrationParams.setFingerprintFromFile(asset);
        registrationParams.sign(KEY1);
        RegistrationResponse registrationResponse =
                await(callback -> new ApiService()
                        .registerAsset(registrationParams, callback));
        List<RegistrationResponse.Asset> assets = registrationResponse.getAssets();
        String assetId = assets.get(0).getId();

        // Issue bitmarks
        IssuanceParams issuanceParams = new IssuanceParams(assetId, ADDRESS1);
        issuanceParams.sign(KEY1);
        List<String> txIds =
                await(callback -> new ApiService().issueBitmark(issuanceParams, callback));
        assertEquals(txIds.size(), 1);
        assertFalse(txIds.get(0).isEmpty());
    }

    @Test
    public void testIssueBitmark_OwnedSingleBitmarkWithoutMetadata_CorrectSuccessResponseIsReturn(
            File asset)
            throws Throwable {
        // Register asset
        Address owner = ADDRESS1;
        RegistrationParams registrationParams =
                new RegistrationParams(asset.getName(), new HashMap<>());
        registrationParams.setFingerprintFromFile(asset);
        registrationParams.sign(KEY1);
        RegistrationResponse registrationResponse =
                await(callback -> new ApiService()
                        .registerAsset(registrationParams, callback));
        List<RegistrationResponse.Asset> assets = registrationResponse.getAssets();
        String assetId = assets.get(0).getId();

        // Issue bitmarks
        IssuanceParams issuanceParams = new IssuanceParams(assetId, owner);
        issuanceParams.sign(KEY1);
        List<String> txIds =
                await(callback -> new ApiService().issueBitmark(issuanceParams, callback));
        assertEquals(txIds.size(), 1);
        assertFalse(txIds.get(0).isEmpty());
    }

    @Test
    public void testIssueBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn() {
        Address issuer = Address.fromAccountNumber(ADDRESS1.getAddress());
        IssuanceParams issuanceParams = new IssuanceParams(
                "bda050d2235402751ed09e73486c2cced34424c35d4d799eaa37ab73c3dba663", issuer);
        issuanceParams.sign(KEY1);
        HttpException exception = assertThrows(HttpException.class, () ->
                await((Callable1<List<String>>) callback -> new ApiService()
                        .issueBitmark(issuanceParams,
                                      callback)));
        assertEquals(1000, exception.getErrorCode());
        assertEquals(HTTP_BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    public void testIssueMoreBitmark_OwnedBitmark_CorrectSuccessResponseReturn() throws Throwable {
        final String assetId =
                "1d32321120c0725b4ef88732588164748393edb697ca1ef5fdafb29b933d3267af6b503e12d7c454ed7fbf7ec5cf1c864f502e5d688c9bac76f723f13109ba96";
        Address owner = ADDRESS1;
        int quantity = 100;
        IssuanceParams issuanceParams = new IssuanceParams(assetId, owner, 100);
        issuanceParams.sign(KEY1);
        List<String> txIds =
                await(callback -> new ApiService().issueBitmark(issuanceParams, callback));
        assertEquals(txIds.size(), quantity);
        assertFalse(txIds.get(0).isEmpty());
    }

    @Test
    public void testIssueMoreBitmarkParallel_OwnedBitmark_CorrectSuccessResponseReturn()
            throws Throwable {
        final String assetId =
                "1d32321120c0725b4ef88732588164748393edb697ca1ef5fdafb29b933d3267af6b503e12d7c454ed7fbf7ec5cf1c864f502e5d688c9bac76f723f13109ba96";
        Address owner = ADDRESS1;
        int quantity = 100;

        // Issue 100 bitmarks in 5 times
        for (int i = 0; i < 5; i++) {
            IssuanceParams issuanceParams = new IssuanceParams(assetId, owner, 100);
            issuanceParams.sign(KEY1);
            List<String> txIds = await(callback -> new ApiService()
                    .issueBitmark(issuanceParams,
                                  callback));
            assertEquals(txIds.size(), quantity);
            assertFalse(txIds.get(0).isEmpty());
        }
    }

    @Test
    public void testTransferBitmarkFromLink_OwnedBitmark_CorrectSuccessResponseIsReturn()
            throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).pending(true)
                                         .limit(100);
        BitmarkRecord bitmark = findBitmark(builder, b -> !b.isOffer() && b.getStatus() == SETTLED);
        assertNotNull(bitmark, "No bitmark matches the specification");

        // Transfer bitmark
        TransferParams params = new TransferParams(ADDRESS2);
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        String txId = await(callback -> new ApiService().transferBitmark(params, callback));
        assertNotNull(txId);
        assertEquals(Sha3256.HASH_BYTE_LENGTH, HEX.decode(txId).length);

    }


    @Test
    public void testTransferBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn()
            throws Throwable {
        // Get not owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ADDRESS2.getAddress()).pending(true);
        BitmarkRecord bitmark = findBitmark(builder, b -> !b.isOffer() && b.getStatus() == SETTLED);
        assertNotNull(bitmark, "No bitmark matches the specification");

        // Transfer bitmark
        TransferParams params = new TransferParams(ADDRESS3);
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        HttpException exception = assertThrows(HttpException.class,
                                               () -> await(
                                                       (Callable1<String>) callback -> new ApiService()
                                                               .transferBitmark(params, callback)));
        assertEquals(HTTP_INTERNAL_ERROR, exception.getStatusCode());
        assertEquals(7000, exception.getErrorCode());

    }

    @Test
    public void testOfferBitmark_OwnedBitmark_CorrectSuccessResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).pending(true);
        BitmarkRecord bitmark =
                findBitmark(builder, b -> !b.isOffer() && b.getStatus() == SETTLED &&
                                          b.getHead() != MOVED);
        assertNotNull(bitmark, "No bitmark matches the specification");

        // Offer bitmark
        TransferOfferParams params = new TransferOfferParams(ADDRESS2);
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        String offerId = await(callback -> new ApiService().offerBitmark(params, callback));
        assertNotNull(offerId);
        assertFalse(offerId.isEmpty());
    }

    @Test
    public void testOfferBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn() throws Throwable {
        // Get not owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ADDRESS2.getAddress()).pending(true);
        BitmarkRecord bitmark =
                findBitmark(builder, b -> !b.isOffer() && b.getStatus() == SETTLED &&
                                          b.getHead() != MOVED);
        assertNotNull(bitmark, "No bitmark matches the specification");

        // Offer bitmark
        TransferOfferParams params = new TransferOfferParams(ADDRESS3);
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        HttpException exception = assertThrows(HttpException.class,
                                               () -> await(
                                                       (Callable1<String>) callback -> new ApiService()
                                                               .offerBitmark(params, callback)));
        assertEquals(HTTP_FORBIDDEN, exception.getStatusCode());
        assertEquals(2013, exception.getErrorCode());
    }

    @Test
    public void testReplyOffer_AcceptOffer_CorrectSuccessResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).pending(true);
        BitmarkRecord bitmark =
                findBitmark(builder, b -> !b.isOffer() && b.getStatus() == SETTLED &&
                                          b.getHead() != MOVED);

        assertNotNull(bitmark, "No bitmark matches the specification");
        String link = bitmark.getHeadId();
        TransferOfferParams offerParams = new TransferOfferParams(ADDRESS2);
        offerParams.setLink(link);
        offerParams.sign(KEY1);
        String offerId =
                await(callback -> new ApiService().offerBitmark(offerParams, callback));
        assertNotNull(offerId, "Offer is not successful");
        assertFalse(offerId.isEmpty(), "Offer is not successful");

        // Respond offer
        GetBitmarkResponse response =
                await(callback -> new ApiService().getBitmark(bitmark.getId(), false,
                                                              callback));
        OfferRecord offerRecord = response.getBitmark().getOffer();
        TransferResponseParams responseParams = TransferResponseParams.accept(offerRecord);
        responseParams.sign(KEY2);
        String txId = await(callback -> new ApiService()
                .respondBitmarkOffer(responseParams, callback));
        assertNotNull(txId);
        assertEquals(Sha3256.HASH_BYTE_LENGTH, HEX.decode(txId).length);
    }

    @Test
    public void testReplyOffer_CancelOffer_CorrectSuccessResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).pending(true)
                                         .limit(100);
        BitmarkRecord bitmark =
                findBitmark(builder, b -> !b.isOffer() && b.getStatus() == SETTLED &&
                                          b.getHead() != MOVED);
        assertNotNull(bitmark, "No bitmark matches the specification");

        // Offer bitmark
        TransferOfferParams offerParams = new TransferOfferParams(ADDRESS2);
        String link = bitmark.getHeadId();
        offerParams.setLink(link);
        offerParams.sign(KEY1);
        String offerId =
                await(callback -> new ApiService().offerBitmark(offerParams, callback));
        assertNotNull(offerId, "Offer is not successful");
        assertFalse(offerId.isEmpty(), "Offer is not successful");

        // Respond offer
        GetBitmarkResponse response = await(callback -> new ApiService()
                .getBitmark(bitmark.getId(),
                            false, callback));
        OfferRecord offerRecord = response.getBitmark().getOffer();
        TransferResponseParams responseParams = TransferResponseParams.cancel(offerRecord,
                                                                              bitmark.getOwner());
        responseParams.setSigningKey(KEY1);
        String txId = assertDoesNotThrow(() -> await(
                callback -> new ApiService().respondBitmarkOffer(responseParams, callback)));
        assertEquals("", txId);
    }

    @Test
    public void testReplyOffer_RejectOffer_CorrectSuccessResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).pending(true);
        BitmarkRecord bitmark =
                findBitmark(builder, b -> !b.isOffer() && b.getStatus() == SETTLED &&
                                          b.getHead() != MOVED);
        assertNotNull(bitmark, "No bitmark matches the specification");

        // Offer bitmark
        TransferOfferParams offerParams = new TransferOfferParams(ADDRESS2);
        String link = bitmark.getHeadId();
        offerParams.setLink(link);
        offerParams.sign(KEY1);
        String offerId =
                await(callback -> new ApiService().offerBitmark(offerParams, callback));
        assertNotNull(offerId, "Offer is not successful");
        assertFalse(offerId.isEmpty(), "Offer is not successful");

        // Respond offer
        GetBitmarkResponse response = await(callback -> new ApiService()
                .getBitmark(bitmark.getId(),
                            false, callback));
        OfferRecord offerRecord = response.getBitmark().getOffer();
        TransferResponseParams responseParams = TransferResponseParams.reject(offerRecord);
        responseParams.setSigningKey(KEY2);
        String txId = assertDoesNotThrow(() -> await(
                callback -> new ApiService().respondBitmarkOffer(responseParams, callback)));
        assertEquals("", txId);
    }

    @Test
    public void testQueryBitmarkByIdWithAsset_ExistedBitmarkId_CorrectResponseIsReturn()
            throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).limit(1);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> new ApiService().listBitmarks(builder.build(), callback));
        assertFalse(bitmarksResponse.getBitmarks().isEmpty(), "This guy has not owned bitmarks");

        // Get bitmark by id
        String id = bitmarksResponse.getBitmarks().get(0).getId();
        GetBitmarkResponse bitmarkResponse =
                await(callback -> new ApiService().getBitmark(id, true, callback));
        BitmarkRecord bitmark = bitmarkResponse.getBitmark();
        AssetRecord asset = bitmarkResponse.getAsset();
        assertNotNull(bitmark);
        assertNotNull(asset);
        assertEquals(id, bitmark.getId());
        assertEquals(bitmark.getAssetId(), asset.getId());
    }

    @Test
    public void testQueryBitmarkByIdWithoutAsset_ExistedBitmarkId_CorrectResponseIsReturn()
            throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).limit(1);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> new ApiService().listBitmarks(builder.build(), callback));
        assertFalse(bitmarksResponse.getBitmarks().isEmpty(), "This guy has not owned bitmarks");

        // Get bitmark by id
        String id = bitmarksResponse.getBitmarks().get(0).getId();
        GetBitmarkResponse bitmarkResponse =
                await(callback -> new ApiService().getBitmark(id, false, callback));
        BitmarkRecord bitmark = bitmarkResponse.getBitmark();
        AssetRecord asset = bitmarkResponse.getAsset();
        assertNotNull(bitmark);
        assertNull(asset);
        assertEquals(id, bitmark.getId());
    }

    @Test
    public void testQueryBitmarksByIds_ValidBitmarkIds_CorrectResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        int limit = 1;
        BitmarkQueryBuilder builder1 =
                new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).limit(limit);
        GetBitmarksResponse bitmarksResponse1 = await(callback -> new ApiService()
                .listBitmarks(
                        builder1.build(),
                        callback));
        assertFalse(bitmarksResponse1.getBitmarks().isEmpty(), "This guy has not owned bitmarks");
        String[] bitmarkIds =
                bitmarksResponse1.getBitmarks().stream().map(BitmarkRecord::getId)
                                 .toArray(String[]::new);

        BitmarkQueryBuilder builder2 =
                new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress())
                                         .bitmarkIds(bitmarkIds);
        GetBitmarksResponse bitmarksResponse2 = await(callback -> new ApiService()
                .listBitmarks(
                        builder2.build(),
                        callback));
        List<BitmarkRecord> bitmarks = bitmarksResponse2.getBitmarks();
        List<AssetRecord> assets = bitmarksResponse2.getAssets();
        assertFalse(bitmarks.isEmpty());
        assertEquals(bitmarkIds.length, bitmarks.size());
        assertNull(assets);
        assertTrue(Arrays.equals(bitmarkIds,
                                 bitmarks.stream().map(BitmarkRecord::getId).toArray()));
    }

    @Test
    public void testQueryBitmarkByLimitAndLoadAsset_ValidLimitValue_CorrectResponseIsReturn()
            throws Throwable {
        int limit = 1;
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).limit(limit)
                                         .loadAsset(true);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> new ApiService().listBitmarks(builder.build(), callback));
        List<BitmarkRecord> bitmarks = bitmarksResponse.getBitmarks();
        List<AssetRecord> assets = bitmarksResponse.getAssets();
        assertFalse(bitmarks.isEmpty());
        assertEquals(limit, bitmarks.size());
        assertNotNull(assets);
    }

    @Test
    public void testCreateShare_OwnedBitmark_CorrectResReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).limit(100);
        List<BitmarkRecord> bitmarks = findBitmarks(builder,
                                                    b -> !b.isOffer() && b.getStatus() == SETTLED &&
                                                         b.getHead() != MOVED);
        assertTrue(bitmarks != null && !bitmarks.isEmpty(),
                   "No bitmarks matches the specification");

        // Get all shares
        List<ShareRecord> shareRecords;
        try {
            shareRecords =
                    await(callback -> new ApiService()
                            .listShares(ADDRESS1.getAddress(), callback));

            // Remove all bitmark has already created share
            shareRecords.forEach(share -> {
                for (BitmarkRecord bitmark : bitmarks) {
                    if (bitmark.getId().equals(share.getId())) {
                        bitmarks.remove(bitmark);
                        break;
                    }
                }
            });
        } catch (HttpException e) {
            if (e.getErrorCode() != 1001) throw e;
        }

        SecureRandom random = new SecureRandom();
        BitmarkRecord bitmark = bitmarks.get(random.nextInt(bitmarks.size()));

        // Create share
        ShareParams params = new ShareParams(100, bitmark.getHeadId());
        params.sign(KEY1);
        Pair<String, String> res =
                await(callback -> new ApiService().createShare(params, callback));
        assertNotNull(res);
        assertNotNull(res.first());
        assertNotNull(res.second());
    }

    @Test
    public void testCreateShare_NotOwnedBitmark_CorrectResReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).limit(100);
        List<BitmarkRecord> bitmarks = findBitmarks(builder,
                                                    b -> !b.isOffer() && b.getStatus() == SETTLED &&
                                                         b.getHead() != MOVED);
        assertTrue(bitmarks != null && !bitmarks.isEmpty(),
                   "No bitmarks matches the specification");

        // Get all shares
        List<ShareRecord> shareRecords;
        try {
            shareRecords =
                    await(callback -> new ApiService()
                            .listShares(ADDRESS1.getAddress(), callback));

            // Remove all bitmark has already created share
            shareRecords.forEach(share -> {
                for (BitmarkRecord bitmark : bitmarks) {
                    if (bitmark.getId().equals(share.getId())) {
                        bitmarks.remove(bitmark);
                        break;
                    }
                }
            });
        } catch (HttpException e) {
            if (e.getErrorCode() != 1001) throw e;
        }

        SecureRandom random = new SecureRandom();
        BitmarkRecord bitmark = bitmarks.get(random.nextInt(bitmarks.size()));

        // Create share
        ShareParams params = new ShareParams(100, bitmark.getHeadId());
        params.sign(KEY2);
        HttpException exception = assertThrows(HttpException.class,
                                               () -> await(
                                                       (Callable1<Pair<String, String>>) callback ->
                                                               new ApiService().createShare(params,
                                                                                            callback)));
        assertEquals(HTTP_INTERNAL_ERROR, exception.getStatusCode());
        assertEquals(7000, exception.getErrorCode());

    }

    @Test
    public void testGrantShare_OwnedShare_CorrectResReturn() throws Throwable {
        // Get all shares
        List<ShareRecord> shareRecords =
                await(callback -> new ApiService()
                        .listShares(ADDRESS1.getAddress(), callback));
        assertTrue(shareRecords != null && !shareRecords.isEmpty(),
                   "This guy does not have any shares");

        ShareRecord shareRecord =
                shareRecords.stream().filter(share -> share.getAvailable() > 0).findFirst()
                            .orElse(null);
        String shareId;

        if (shareRecord == null) {
            // Get owned bitmarks
            BitmarkQueryBuilder builder =
                    new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).limit(100);
            List<BitmarkRecord> bitmarks = findBitmarks(builder,
                                                        b -> !b.isOffer() &&
                                                             b.getStatus() == SETTLED &&
                                                             b.getHead() != MOVED);
            assertTrue(bitmarks != null && !bitmarks.isEmpty(),
                       "No bitmarks matches the specification");

            SecureRandom random = new SecureRandom();
            BitmarkRecord bitmark = bitmarks.get(random.nextInt(bitmarks.size()));

            // Create share
            ShareParams params = new ShareParams(100, bitmark.getHeadId());
            params.sign(KEY1);
            Pair<String, String> res =
                    await(callback -> new ApiService().createShare(params, callback));
            shareId = res.second();
        } else shareId = shareRecord.getId();

        // Grant share
        ShareGrantingParams grantingParams =
                new ShareGrantingParams(shareId, 1, ADDRESS1, ADDRESS2, Random.secureRandomInt());
        grantingParams.sign(KEY1);
        String offerId =
                await(callback -> new ApiService().grantShare(grantingParams, callback));
        assertNotNull(offerId);
        assertFalse(offerId.isEmpty());
    }

    @Test
    public void testGrantShare_NotOwnedShare_CorrectResReturn() throws Throwable {
        // Get all shares
        List<ShareRecord> shareRecords =
                await(callback -> new ApiService()
                        .listShares(ADDRESS1.getAddress(), callback));
        assertTrue(shareRecords != null && !shareRecords.isEmpty(),
                   "This guy does not have any shares");

        ShareRecord shareRecord =
                shareRecords.stream().filter(share -> share.getAvailable() > 0).findFirst()
                            .orElse(null);

        String shareId;

        if (shareRecord == null) {
            // Get owned bitmarks
            BitmarkQueryBuilder builder =
                    new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).limit(100);
            List<BitmarkRecord> bitmarks = findBitmarks(builder,
                                                        b -> !b.isOffer() &&
                                                             b.getStatus() == SETTLED &&
                                                             b.getHead() != MOVED);
            assertTrue(bitmarks != null && !bitmarks.isEmpty(),
                       "No bitmarks matches the specification");

            SecureRandom random = new SecureRandom();
            BitmarkRecord bitmark = bitmarks.get(random.nextInt(bitmarks.size()));

            // Create share
            ShareParams params = new ShareParams(100, bitmark.getHeadId());
            params.sign(KEY1);
            Pair<String, String> res =
                    await(callback -> new ApiService().createShare(params, callback));
            shareId = res.second();
        } else shareId = shareRecord.getId();

        // Grant share
        ShareGrantingParams grantingParams =
                new ShareGrantingParams(shareId, 1, ADDRESS3, ADDRESS2, Random.secureRandomInt());
        grantingParams.sign(KEY3);
        HttpException exception = assertThrows(HttpException.class,
                                               () -> await(
                                                       (Callable1<String>) callback -> new ApiService()
                                                               .grantShare(grantingParams,
                                                                           callback)));
        assertEquals(HTTP_INTERNAL_ERROR, exception.getStatusCode());
        assertEquals(7000, exception.getErrorCode());
    }

    @Test
    public void testRespondGrantShare_AcceptGrant_CorrectResReturn() throws Throwable {
        // Query offer shares
        List<ShareGrantRecord> grantRecords = await(callback -> new ApiService()
                .listShareOffer(
                        ADDRESS1.getAddress(),
                        ADDRESS2.getAddress(),
                        callback));

        if (grantRecords == null || grantRecords.isEmpty()) {
            // Get all shares
            List<ShareRecord> shareRecords =
                    await(callback -> new ApiService()
                            .listShares(ADDRESS1.getAddress(), callback));
            assertTrue(shareRecords != null && !shareRecords.isEmpty(),
                       "This guy does not have any shares");

            ShareRecord shareRecord =
                    shareRecords.stream().filter(share -> share.getAvailable() > 0).findFirst()
                                .orElse(null);

            assertNotNull(shareRecord, "There are no ShareRecord for granting");

            // Grant share
            ShareGrantingParams grantingParams =
                    new ShareGrantingParams(shareRecord.getId(), 1, ADDRESS1, ADDRESS2,
                                            Random.secureRandomInt());
            grantingParams.sign(KEY1);
            String offerId = await(callback -> new ApiService()
                    .grantShare(grantingParams, callback));
            grantRecords = await(callback -> new ApiService()
                    .listShareOffer(ADDRESS1.getAddress(),
                                    ADDRESS2.getAddress(),
                                    callback));

            assertFalse(grantRecords.isEmpty(), "There are not GrantRecord for respond");
        }

        // Respond offer
        GrantResponseParams responseParams = GrantResponseParams.accept(grantRecords.get(0));
        responseParams.sign(KEY2);
        String txId = await(callback -> new ApiService()
                .respondShareOffer(responseParams, callback));
        assertNotNull(txId);
        assertFalse(txId.isEmpty());
    }

    @Test
    public void testRespondGrantShare_RejectGrant_CorrectResReturn() throws Throwable {
        // Query offer shares
        List<ShareGrantRecord> grantRecords = await(callback -> new ApiService()
                .listShareOffer(
                        ADDRESS1.getAddress(),
                        ADDRESS2.getAddress(),
                        callback));

        if (grantRecords == null || grantRecords.isEmpty()) {
            // Get all shares
            List<ShareRecord> shareRecords =
                    await(callback -> new ApiService()
                            .listShares(ADDRESS1.getAddress(), callback));
            assertTrue(shareRecords != null && !shareRecords.isEmpty(),
                       "This guy does not have any shares");

            ShareRecord shareRecord =
                    shareRecords.stream().filter(share -> share.getAvailable() > 0).findFirst()
                                .orElse(null);

            assertNotNull(shareRecord, "There are no ShareRecord for granting");

            // Grant share
            ShareGrantingParams grantingParams =
                    new ShareGrantingParams(shareRecord.getId(), 1, ADDRESS1, ADDRESS2,
                                            Random.secureRandomInt());
            grantingParams.sign(KEY1);
            String offerId = await(callback -> new ApiService()
                    .grantShare(grantingParams, callback));
            grantRecords = await(callback -> new ApiService()
                    .listShareOffer(ADDRESS1.getAddress(),
                                    ADDRESS2.getAddress(),
                                    callback));

            assertFalse(grantRecords.isEmpty(), "There are not GrantRecord for respond");
        }

        // Respond offer
        GrantResponseParams responseParams = GrantResponseParams.reject(grantRecords.get(0));
        responseParams.sign(KEY2);
        String txId = await(callback -> new ApiService()
                .respondShareOffer(responseParams, callback));
        assertNotNull(txId);
        assertFalse(txId.isEmpty());
    }

    @Test
    public void testRespondGrantShare_CancelGrant_CorrectResReturn() throws Throwable {
        // Query offer shares
        List<ShareGrantRecord> grantRecords = await(callback -> new ApiService()
                .listShareOffer(
                        ADDRESS1.getAddress(),
                        ADDRESS2.getAddress(),
                        callback));

        if (grantRecords == null || grantRecords.isEmpty()) {
            // Get all shares
            List<ShareRecord> shareRecords =
                    await(callback -> new ApiService()
                            .listShares(ADDRESS1.getAddress(), callback));
            assertTrue(shareRecords != null && !shareRecords.isEmpty(),
                       "This guy does not have any shares");

            ShareRecord shareRecord =
                    shareRecords.stream().filter(share -> share.getAvailable() > 0).findFirst()
                                .orElse(null);

            assertNotNull(shareRecord, "There are no ShareRecord for granting");

            // Grant share
            ShareGrantingParams grantingParams =
                    new ShareGrantingParams(shareRecord.getId(), 1, ADDRESS1, ADDRESS2,
                                            Random.secureRandomInt());
            grantingParams.sign(KEY1);
            String offerId = await(callback -> new ApiService()
                    .grantShare(grantingParams, callback));
            grantRecords = await(callback -> new ApiService()
                    .listShareOffer(ADDRESS1.getAddress(),
                                    ADDRESS2.getAddress(),
                                    callback));

            assertFalse(grantRecords.isEmpty(), "There are not GrantRecord for respond");
        }

        // Respond offer
        GrantResponseParams responseParams = GrantResponseParams.cancel(grantRecords.get(0));
        responseParams.sign(KEY1);
        String txId = await(callback -> new ApiService()
                .respondShareOffer(responseParams, callback));
        assertNotNull(txId);
        assertFalse(txId.isEmpty());
    }

    @Test
    public void testGetShare__CorrectResReturn() throws Throwable {

        String shareId;

        // Get all shares
        List<ShareRecord> shareRecords =
                await(callback -> new ApiService()
                        .listShares(ADDRESS1.getAddress(), callback));

        if (shareRecords == null || shareRecords.isEmpty()) {
            // Get owned bitmarks
            BitmarkQueryBuilder builder =
                    new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).limit(100);
            List<BitmarkRecord> bitmarks = findBitmarks(builder,
                                                        b -> !b.isOffer() &&
                                                             b.getStatus() == SETTLED &&
                                                             b.getHead() != MOVED);
            assertTrue(bitmarks != null && !bitmarks.isEmpty(),
                       "No bitmarks matches the specification");

            SecureRandom random = new SecureRandom();
            BitmarkRecord bitmark = bitmarks.get(random.nextInt(bitmarks.size()));

            // Create share
            ShareParams params = new ShareParams(100, bitmark.getHeadId());
            params.sign(KEY1);
            Pair<String, String> res =
                    await(callback -> new ApiService().createShare(params, callback));
            shareId = res.second();
        } else shareId = shareRecords.get(0).getId();

        ShareRecord shareRecord = await(callback -> new ApiService()
                .getShare(shareId, callback));
        assertNotNull(shareRecord);
        assertEquals(shareId, shareRecord.getId());
    }

    @Test
    public void testListShares__CorrectResReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).limit(100);
        List<BitmarkRecord> bitmarks = findBitmarks(builder,
                                                    b -> !b.isOffer() &&
                                                         b.getStatus() == SETTLED &&
                                                         b.getHead() != MOVED);
        assertTrue(bitmarks != null && !bitmarks.isEmpty(),
                   "No bitmarks matches the specification");

        SecureRandom random = new SecureRandom();
        BitmarkRecord bitmark = bitmarks.get(random.nextInt(bitmarks.size()));

        // Create share
        ShareParams params = new ShareParams(100, bitmark.getHeadId());
        params.sign(KEY1);
        Pair<String, String> res =
                await(callback -> new ApiService().createShare(params, callback));

        // Query shares
        List<ShareRecord> shareRecords =
                await(callback -> new ApiService()
                        .listShares(ADDRESS1.getAddress(), callback));
        assertNotNull(shareRecords);
        assertFalse(shareRecords.isEmpty());
    }

    @Test
    public void testQueryShareOffer__CorrectResReturn() throws Throwable {
        List<ShareGrantRecord> grantRecords = await(callback -> new ApiService()
                .listShareOffer(
                        ADDRESS1.getAddress(),
                        ADDRESS2.getAddress(),
                        callback));
        if (grantRecords == null || grantRecords.isEmpty()) {
            // Get all shares
            List<ShareRecord> shareRecords =
                    await(callback -> new ApiService()
                            .listShares(ADDRESS1.getAddress(), callback));
            assertTrue(shareRecords != null && !shareRecords.isEmpty(),
                       "This guy does not have any shares");

            ShareRecord shareRecord =
                    shareRecords.stream().filter(share -> share.getAvailable() > 0).findFirst()
                                .orElse(null);
            String shareId;

            if (shareRecord == null) {
                // Get owned bitmarks
                BitmarkQueryBuilder builder =
                        new BitmarkQueryBuilder().ownedBy(ADDRESS1.getAddress()).limit(100);
                List<BitmarkRecord> bitmarks = findBitmarks(builder,
                                                            b -> !b.isOffer() &&
                                                                 b.getStatus() == SETTLED &&
                                                                 b.getHead() != MOVED);
                assertTrue(bitmarks != null && !bitmarks.isEmpty(),
                           "No bitmarks matches the specification");

                SecureRandom random = new SecureRandom();
                BitmarkRecord bitmark = bitmarks.get(random.nextInt(bitmarks.size()));

                // Create share
                ShareParams params = new ShareParams(100, bitmark.getHeadId());
                params.sign(KEY1);
                Pair<String, String> res =
                        await(callback -> new ApiService().createShare(params, callback));
                shareId = res.second();
            } else shareId = shareRecord.getId();

            // Grant share
            ShareGrantingParams grantingParams =
                    new ShareGrantingParams(shareId, 1, ADDRESS1, ADDRESS2,
                                            Random.secureRandomInt());
            grantingParams.sign(KEY1);
            String offerId = await(callback -> new ApiService()
                    .grantShare(grantingParams, callback));
            grantRecords = await(callback -> new ApiService()
                    .listShareOffer(ADDRESS1.getAddress(),
                                    ADDRESS2.getAddress(),
                                    callback));
        }
        assertNotNull(grantRecords);
        assertFalse(grantRecords.isEmpty());
    }

    private BitmarkRecord findBitmark(BitmarkQueryBuilder queryBuilder,
                                      Predicate<? super BitmarkRecord> predicate)
            throws Throwable {
        final Data<Long> minOffset = new Data<>();
        int retryCount = 50; // Try 50 times in maximum
        while (retryCount > 0) {
            retryCount--;
            GetBitmarksResponse bitmarksResponse =
                    await(callback -> new ApiService()
                            .listBitmarks(
                                    minOffset.getValue() == null ? queryBuilder
                                            .build() : queryBuilder
                                            .at(minOffset.getValue())
                                            .to("earlier").build(),
                                    callback));
            assertFalse(bitmarksResponse.getBitmarks().isEmpty(),
                        "This guy has not owned bitmarks or cannot find the bitmark match specification");
            Optional<BitmarkRecord> maxOffsetOpt = bitmarksResponse.getBitmarks().stream().min(
                    Comparator.comparingLong(BitmarkRecord::getOffset));
            if (maxOffsetOpt.isPresent()) minOffset.setValue(maxOffsetOpt.get().getOffset());
            else throw new RuntimeException("Cannot get max offset");

            BitmarkRecord bitmark =
                    bitmarksResponse.getBitmarks().stream().filter(predicate).findFirst()
                                    .orElse(null);
            if (bitmark != null) return bitmark;
        }
        return null;
    }

    private List<BitmarkRecord> findBitmarks(BitmarkQueryBuilder queryBuilder,
                                             Predicate<? super BitmarkRecord> predicate)
            throws Throwable {
        final Data<Long> minOffset = new Data<>();
        int retryCount = 50; // Try 50 times in maximum
        while (retryCount > 0) {
            retryCount--;
            GetBitmarksResponse bitmarksResponse =
                    await(callback -> new ApiService()
                            .listBitmarks(
                                    minOffset.getValue() == null ? queryBuilder
                                            .build() : queryBuilder
                                            .at(minOffset.getValue())
                                            .to("earlier").build(),
                                    callback));
            assertFalse(bitmarksResponse.getBitmarks().isEmpty(),
                        "This guy has not owned bitmarks or cannot find the bitmarks match specification");
            Optional<BitmarkRecord> maxOffsetOpt = bitmarksResponse.getBitmarks().stream().min(
                    Comparator.comparingLong(BitmarkRecord::getOffset));
            if (maxOffsetOpt.isPresent()) minOffset.setValue(maxOffsetOpt.get().getOffset());
            else throw new RuntimeException("Cannot get max offset");

            List<BitmarkRecord> bitmarks =
                    bitmarksResponse.getBitmarks().stream().filter(predicate).collect(
                            Collectors.toList());
            if (!bitmarks.isEmpty()) return bitmarks;
        }
        return null;
    }

    //endregion ============= BITMARK TESTS ===============

    //region ============= TXS TESTS ===============

    @Test
    public void testQueryTransactionWithoutAsset_ExistedTxId_CorrectResponseIsReturn()
            throws Throwable {
        // Get existed tx
        TransactionQueryBuilder builder =
                new TransactionQueryBuilder().ownedBy(ADDRESS1.getAddress()).limit(1);
        GetTransactionsResponse getTransactionsResponse =
                await(callback -> new ApiService()
                        .listTransactions(builder.build(), callback));
        List<TransactionRecord> transactions = getTransactionsResponse.getTransactions();
        assertNotNull(transactions, "This guy does not have any transaction");
        assertFalse(transactions.isEmpty(), "This guy does not have any transaction");
        String txId = transactions.get(0).getId();

        // Get tx by id
        GetTransactionResponse getTransactionResponse =
                await(callback -> new ApiService().getTransaction(txId, false,
                                                                  callback));
        TransactionRecord transaction = getTransactionResponse.getTransaction();
        assertNotNull(transaction);
        assertEquals(txId, transaction.getId());
    }

    @Test
    public void testQueryTransactionWithAsset_ExistedTxId_CorrectResponseIsReturn()
            throws Throwable {
        // Get existed tx
        TransactionQueryBuilder builder =
                new TransactionQueryBuilder().ownedBy(ADDRESS1.getAddress()).limit(1);
        GetTransactionsResponse getTransactionsResponse =
                await(callback -> new ApiService()
                        .listTransactions(builder.build(), callback));
        List<TransactionRecord> transactions = getTransactionsResponse.getTransactions();
        assertNotNull(transactions, "This guy does not have any transaction");
        assertFalse(transactions.isEmpty(), "This guy does not have any transaction");
        String txId = transactions.get(0).getId();

        // Get tx by id
        GetTransactionResponse getTransactionResponse =
                await(callback -> new ApiService().getTransaction(txId,
                                                                  true,
                                                                  callback));
        TransactionRecord transaction = getTransactionResponse.getTransaction();
        AssetRecord asset = getTransactionResponse.getAsset();
        assertNotNull(transaction);
        assertNotNull(asset);
        assertEquals(txId, transaction.getId());
        assertEquals(transaction.getAssetId(), asset.getId());
    }

    @Test
    public void testQueryTransaction_NonExistedTxId_ErrorIsThrow() {
        String id =
                "1234567890123456789012345678901234567890123456789012345678901234";
        HttpException exception = assertThrows(HttpException.class,
                                               () -> await(
                                                       (Callable1<GetTransactionResponse>) callback -> new ApiService()
                                                               .getTransaction(id, false,
                                                                               callback)));
        assertEquals(HTTP_INTERNAL_ERROR, exception.getStatusCode());
    }

    @Test
    public void testQueryTransactions_NoCondition_CorrectResponseIsReturn() throws Throwable {
        // With limit and owner
        int limit = 1;
        String owner = ADDRESS1.getAddress();
        TransactionQueryBuilder builder =
                new TransactionQueryBuilder().ownedBy(owner).limit(limit);
        GetTransactionsResponse getTransactionsResponse =
                await(callback -> new ApiService()
                        .listTransactions(builder.build(), callback));
        List<TransactionRecord> transactions = getTransactionsResponse.getTransactions();
        assertEquals(limit, transactions.size());
        transactions.forEach(transaction -> assertEquals(owner, transaction.getOwner()));
    }

    @Test
    public void testQueryTransactions_ByAtAndTo_CorrectResponseIsReturn() throws Throwable {
        int limit = 10;
        long at = 5;
        TransactionQueryBuilder builder =
                new TransactionQueryBuilder().at(at).to("earlier").limit(limit);
        GetTransactionsResponse getTransactionsResponse =
                await(callback -> new ApiService()
                        .listTransactions(builder.build(), callback));
        List<TransactionRecord> transactions = getTransactionsResponse.getTransactions();
        transactions.forEach(transaction -> assertTrue(transaction.getBlockOffset() <= at));
    }

    @Test
    public void testQueryTransactions_ByBlockNumber_CorrectResponseIsReturn() throws Throwable {
        long blockNumber = 10;
        TransactionQueryBuilder builder =
                new TransactionQueryBuilder().referencedBlockNumber(blockNumber).pending(true);
        GetTransactionsResponse getTransactionsResponse =
                await(callback -> new ApiService()
                        .listTransactions(builder.build(), callback));
        List<TransactionRecord> transactions = getTransactionsResponse.getTransactions();
        transactions
                .forEach(transaction -> assertTrue(transaction.getBlockNumber() == blockNumber));
    }

    @Test
    public void testQueryTransactionsByBlock_NoCondition_CorrectResponseIsReturn()
            throws Throwable {
        // With limit and owner
        int limit = 1;
        String owner = ADDRESS1.getAddress();
        TransactionQueryBuilder builder =
                new TransactionQueryBuilder().ownedBy(owner).loadBlock(true).limit(limit);
        GetTransactionsResponse getTransactionsResponse =
                await(callback -> new ApiService()
                        .listTransactions(builder.build(), callback));
        List<TransactionRecord> transactions = getTransactionsResponse.getTransactions();
        assertEquals(limit, transactions.size());
        assertFalse(getTransactionsResponse.getBlocks().isEmpty());
        transactions.forEach(transaction -> assertEquals(owner, transaction.getOwner()));
    }

    //endregion ============= TXS TESTS ===============

    //region =============== WS TESTS ===============

    @Test
    public void testRegisterWsToken__ValidTokenReturn() throws Throwable {
        RegisterWsTokenParams params = new RegisterWsTokenParams(ADDRESS1);
        params.sign(KEY1);
        String token =
                await(callback -> new ApiService().registerWsToken(params, callback));
        assertNotNull(token);
    }

    // endregion ================== WS TESTS ==================


}
