package com.bitmark.sdk.test.integrationtest.features;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.bitmark.apiservice.params.*;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.response.GetBitmarkResponse;
import com.bitmark.apiservice.response.GetBitmarksResponse;
import com.bitmark.apiservice.response.RegistrationResponse;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.Data;
import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.callback.Callable1;
import com.bitmark.apiservice.utils.error.HttpException;
import com.bitmark.apiservice.utils.record.*;
import com.bitmark.cryptography.crypto.Sha3256;
import com.bitmark.sdk.features.Asset;
import com.bitmark.sdk.features.Bitmark;
import com.bitmark.sdk.test.integrationtest.BaseTest;
import com.bitmark.sdk.test.integrationtest.utils.extensions.TemporaryFolderRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Predicate;

import static com.bitmark.apiservice.utils.Awaitility.await;
import static com.bitmark.apiservice.utils.record.BitmarkRecord.Status.SETTLED;
import static com.bitmark.apiservice.utils.record.Head.MOVED;
import static com.bitmark.cryptography.crypto.Random.secureRandomInt;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.sdk.test.integrationtest.DataProvider.*;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class BitmarkTest extends BaseTest {

    @Rule
    public TemporaryFolderRule temporaryFolderRule = new TemporaryFolderRule();

    @Test
    public void testIssueBitmark_OwnedMultipleBitmark_CorrectSuccessResponseIsReturn()
            throws Throwable {
        // Register asset
        File asset = temporaryFolderRule.newFile();
        Address owner = ACCOUNT1.toAddress();
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from android sdk test");
        }};
        RegistrationParams registrationParams = new RegistrationParams(
                asset.getName(),
                metadata
        );
        registrationParams.setFingerprintFromFile(asset);
        registrationParams.sign(KEY1);
        RegistrationResponse registrationResponse =
                await(callback -> Asset.register(
                        registrationParams,
                        callback
                ));
        List<RegistrationResponse.Asset> assets = registrationResponse.getAssets();
        String assetId = assets.get(0).getId();

        // Issue bitmarks
        final int quantity = 5;
        IssuanceParams issuanceParams = new IssuanceParams(
                assetId,
                owner,
                quantity
        );
        issuanceParams.sign(KEY1);
        List<String> txIds =
                await(callback -> Bitmark.issue(
                        issuanceParams,
                        callback
                ));
        assertEquals(txIds.size(), quantity);
        assertFalse(txIds.get(0).isEmpty());
    }

    @Test
    public void testIssueBitmark_OwnedSingleBitmark_CorrectSuccessResponseIsReturn()
            throws Throwable {
        // Register asset
        File asset = temporaryFolderRule.newFile();
        Address owner = ACCOUNT1.toAddress();
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from android sdk test");
        }};
        RegistrationParams registrationParams = new RegistrationParams(
                asset.getName(),
                metadata
        );
        registrationParams.setFingerprintFromFile(asset);
        registrationParams.sign(KEY1);
        RegistrationResponse registrationResponse =
                await(callback -> Asset.register(
                        registrationParams,
                        callback
                ));
        List<RegistrationResponse.Asset> assets = registrationResponse.getAssets();
        String assetId = assets.get(0).getId();

        // Issue bitmarks
        IssuanceParams issuanceParams = new IssuanceParams(assetId, owner);
        issuanceParams.sign(KEY1);
        List<String> txIds =
                await(callback -> Bitmark.issue(
                        issuanceParams,
                        callback
                ));
        assertEquals(txIds.size(), 1);
        assertFalse(txIds.get(0).isEmpty());
    }

    @Test
    public void testIssueBitmark_OwnedSingleBitmarkWithoutMetadata_CorrectSuccessResponseIsReturn()
            throws Throwable {
        // Register asset
        File asset = temporaryFolderRule.newFile();
        Address owner = ACCOUNT1.toAddress();
        RegistrationParams registrationParams = new RegistrationParams(
                asset.getName(),
                null
        );
        registrationParams.setFingerprintFromFile(asset);
        registrationParams.sign(KEY1);
        RegistrationResponse registrationResponse =
                await(callback -> Asset.register(
                        registrationParams,
                        callback
                ));
        List<RegistrationResponse.Asset> assets = registrationResponse.getAssets();
        String assetId = assets.get(0).getId();

        // Issue bitmarks
        IssuanceParams issuanceParams = new IssuanceParams(assetId, owner);
        issuanceParams.sign(KEY1);
        List<String> txIds =
                await(callback -> Bitmark.issue(
                        issuanceParams,
                        callback
                ));
        assertEquals(txIds.size(), 1);
        assertFalse(txIds.get(0).isEmpty());
    }

    @Test
    public void testIssueBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn()
            throws Throwable {
        expectedException.expect(HttpException.class);
        expectedException.expectMessage(containsString("1000"));
        expectedException.expectMessage(containsString(String.valueOf(
                HTTP_BAD_REQUEST)));

        Address issuer = Address.fromAccountNumber(ACCOUNT1.getAccountNumber());
        IssuanceParams issuanceParams = new IssuanceParams(
                "bda050d2235402751ed09e73486c2cced34424c35d4d799eaa37ab73c3dba663",
                issuer
        );
        issuanceParams.sign(KEY1);
        await((Callable1<List<String>>) callback -> Bitmark.issue(
                issuanceParams,
                callback
        ));
    }

    @Test
    public void testIssueMoreBitmark_OwnedBitmark_CorrectSuccessResponseReturn()
            throws Throwable {
        final String assetId =
                "1d32321120c0725b4ef88732588164748393edb697ca1ef5fdafb29b933d3267af6b503e12d7c454ed7fbf7ec5cf1c864f502e5d688c9bac76f723f13109ba96";
        Address owner = ACCOUNT1.toAddress();
        int quantity = 100;
        IssuanceParams issuanceParams = new IssuanceParams(assetId, owner, 100);
        issuanceParams.sign(KEY1);
        List<String> txIds = await(callback -> Bitmark.issue(
                issuanceParams,
                callback
        ));
        assertEquals(txIds.size(), quantity);
        assertFalse(txIds.get(0).isEmpty());
    }

    @Test
    public void testIssueMoreBitmarkParallel_OwnedBitmark_CorrectSuccessResponseReturn()
            throws Throwable {
        final String assetId =
                "1d32321120c0725b4ef88732588164748393edb697ca1ef5fdafb29b933d3267af6b503e12d7c454ed7fbf7ec5cf1c864f502e5d688c9bac76f723f13109ba96";
        Address owner = ACCOUNT1.toAddress();
        int quantity = 100;

        // Issue 100 bitmarks in 5 times
        for (int i = 0; i < 5; i++) {
            IssuanceParams issuanceParams = new IssuanceParams(
                    assetId,
                    owner,
                    100
            );
            issuanceParams.sign(KEY1);
            List<String> txIds = await(callback -> Bitmark.issue(
                    issuanceParams,
                    callback
            ));
            assertEquals(txIds.size(), quantity);
            assertFalse(txIds.get(0).isEmpty());
        }
    }

    @Test
    public void testTransferBitmarkFromLink_OwnedBitmark_CorrectSuccessResponseIsReturn()
            throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                        .pending(true)
                        .limit(100);
        BitmarkRecord bitmark = findBitmark(
                builder,
                b -> !b.isOffer() && b.getStatus() == SETTLED
        );
        assertNotNull("No bitmark matches the specification", bitmark);

        // Transfer bitmark
        TransferParams params = new TransferParams(ACCOUNT2.toAddress());
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        String txId = await(callback -> Bitmark.transfer(params, callback));
        assertNotNull(txId);
        assertEquals(Sha3256.HASH_BYTE_LENGTH, HEX.decode(txId).length);

    }


    @Test
    public void testTransferBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn()
            throws Throwable {
        expectedException.expect(HttpException.class);
        expectedException.expectMessage(containsString("7000"));
        expectedException.expectMessage(String.valueOf(HTTP_INTERNAL_ERROR));

        // Get not owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT2.getAccountNumber())
                        .pending(true);
        BitmarkRecord bitmark = findBitmark(
                builder,
                b -> !b.isOffer() && b.getStatus() == SETTLED
        );
        assertNotNull("No bitmark matches the specification", bitmark);

        // Transfer bitmark
        TransferParams params = new TransferParams(ACCOUNT3.toAddress());
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        await((Callable1<String>) callback -> Bitmark.transfer(
                params,
                callback
        ));

    }

    @Test
    public void testOfferBitmark_OwnedBitmark_CorrectSuccessResponseIsReturn()
            throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder = new BitmarkQueryBuilder().ownedBy(ACCOUNT1
                .getAccountNumber()).pending(true);
        BitmarkRecord bitmark = findBitmark(
                builder,
                b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED
        );
        assertNotNull("No bitmark matches the specification", bitmark);

        // Offer bitmark
        TransferOfferParams params = new TransferOfferParams(ACCOUNT2.toAddress());
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        String offerId = await(callback -> Bitmark.offer(params, callback));
        assertNotNull(offerId);
        assertFalse(offerId.isEmpty());
    }

    @Test
    public void testOfferBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn()
            throws Throwable {
        expectedException.expect(HttpException.class);
        expectedException.expectMessage(containsString("2013"));
        expectedException.expectMessage(containsString(String.valueOf(
                HTTP_FORBIDDEN)));

        // Get not owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT2.getAccountNumber())
                        .pending(true);
        BitmarkRecord bitmark = findBitmark(
                builder,
                b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED
        );
        assertNotNull("No bitmark matches the specification", bitmark);

        // Offer bitmark
        TransferOfferParams params = new TransferOfferParams(ACCOUNT3.toAddress());
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        await((Callable1<String>) callback -> Bitmark.offer(params, callback));

    }

    @Test
    public void testReplyOffer_AcceptOffer_CorrectSuccessResponseIsReturn()
            throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder = new BitmarkQueryBuilder().ownedBy(ACCOUNT1
                .getAccountNumber()).pending(true);
        BitmarkRecord bitmark = findBitmark(
                builder,
                b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED
        );
        assertNotNull("No bitmark matches the specification", bitmark);

        // Offer bitmark
        TransferOfferParams offerParams = new TransferOfferParams(ACCOUNT2.toAddress());
        String link = bitmark.getHeadId();
        offerParams.setLink(link);
        offerParams.sign(KEY1);
        String offerId = await(callback -> Bitmark.offer(
                offerParams,
                callback
        ));
        assertNotNull("Offer is not successful", offerId);
        assertFalse("Offer is not successful", offerId.isEmpty());

        // Respond offer
        GetBitmarkResponse response =
                await(callback -> Bitmark.get(bitmark.getId(), callback));
        OfferRecord offerRecord = response.getBitmark().getOffer();
        TransferResponseParams responseParams = TransferResponseParams.accept(
                offerRecord);
        responseParams.sign(KEY2);
        String txId = await(callback -> Bitmark.respond(
                responseParams,
                callback
        ));
        assertNotNull(txId);
        assertEquals(Sha3256.HASH_BYTE_LENGTH, HEX.decode(txId).length);
    }

    @Test
    public void testReplyOffer_CancelOffer_CorrectSuccessResponseIsReturn()
            throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                        .pending(true)
                        .limit(100);
        BitmarkRecord bitmark = findBitmark(
                builder,
                b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED
        );
        assertNotNull("No bitmark matches the specification", bitmark);

        // Offer bitmark
        TransferOfferParams offerParams = new TransferOfferParams(ACCOUNT2.toAddress());
        String link = bitmark.getHeadId();
        offerParams.setLink(link);
        offerParams.sign(KEY1);
        String offerId = await(callback -> Bitmark.offer(
                offerParams,
                callback
        ));
        assertNotNull(offerId, "Offer is not successful");
        assertFalse("Offer is not successful", offerId.isEmpty());

        // Respond offer
        GetBitmarkResponse response =
                await(callback -> Bitmark.get(bitmark.getId(), callback));
        OfferRecord offerRecord = response.getBitmark().getOffer();
        TransferResponseParams responseParams = TransferResponseParams.cancel(
                offerRecord,
                bitmark.getOwner()
        );
        responseParams.setSigningKey(KEY1);
        String status = await(callback -> Bitmark.respond(
                responseParams,
                callback
        ));
        assertNotNull(status);
        assertEquals("ok", status);
    }

    @Test
    public void testReplyOffer_RejectOffer_CorrectSuccessResponseIsReturn()
            throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                        .pending(true);
        BitmarkRecord bitmark = findBitmark(
                builder,
                b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED
        );
        assertNotNull("No bitmark matches the specification", bitmark);

        // Offer bitmark
        TransferOfferParams offerParams = new TransferOfferParams(ACCOUNT2.toAddress());
        String link = bitmark.getHeadId();
        offerParams.setLink(link);
        offerParams.sign(KEY1);
        String offerId = await(callback -> Bitmark.offer(
                offerParams,
                callback
        ));
        assertNotNull("Offer is not successful", offerId);
        assertFalse("Offer is not successful", offerId.isEmpty());

        // Respond offer
        GetBitmarkResponse response =
                await(callback -> Bitmark.get(bitmark.getId(), callback));
        OfferRecord offerRecord = response.getBitmark().getOffer();
        TransferResponseParams responseParams = TransferResponseParams.reject(
                offerRecord);
        responseParams.setSigningKey(KEY2);
        String status = await(callback -> Bitmark.respond(
                responseParams,
                callback
        ));
        assertNotNull(status);
        assertEquals("ok", status);
    }

    @Test
    public void testQueryBitmarkByIdWithAsset_ExistedBitmarkId_CorrectResponseIsReturn()
            throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                        .limit(1);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> Bitmark.list(builder, callback));
        assertFalse(
                "This guy has not owned bitmarks",
                bitmarksResponse.getBitmarks().isEmpty()
        );

        // Get bitmark by id
        String id = bitmarksResponse.getBitmarks().get(0).getId();
        GetBitmarkResponse bitmarkResponse =
                await(callback -> Bitmark.getWithAsset(id, callback));
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
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                        .limit(1);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> Bitmark.list(builder, callback));
        assertFalse(
                "This guy has not owned bitmarks",
                bitmarksResponse.getBitmarks().isEmpty()
        );

        // Get bitmark by id
        String id = bitmarksResponse.getBitmarks().get(0).getId();
        GetBitmarkResponse bitmarkResponse =
                await(callback -> Bitmark.get(id, callback));
        BitmarkRecord bitmark = bitmarkResponse.getBitmark();
        AssetRecord asset = bitmarkResponse.getAsset();
        assertNotNull(bitmark);
        assertNull(asset);
        assertEquals(id, bitmark.getId());
    }

    @Test
    public void testQueryBitmarksByIds_ValidBitmarkIds_CorrectResponseIsReturn()
            throws Throwable {
        // Get owned bitmarks
        int limit = 1;
        BitmarkQueryBuilder builder1 =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                        .limit(limit);
        GetBitmarksResponse bitmarksResponse1 =
                await(callback -> Bitmark.list(
                        builder1,
                        callback
                ));
        assertFalse(
                "This guy has not owned bitmarks",
                bitmarksResponse1.getBitmarks().isEmpty()
        );
        String[] bitmarkIds =
                Stream.of(bitmarksResponse1.getBitmarks())
                        .map(BitmarkRecord::getId)
                        .toArray(String[]::new);

        BitmarkQueryBuilder builder2 =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                        .bitmarkIds(bitmarkIds);
        GetBitmarksResponse bitmarksResponse2 =
                await(callback -> Bitmark.list(
                        builder2,
                        callback
                ));
        List<BitmarkRecord> bitmarks = bitmarksResponse2.getBitmarks();
        List<AssetRecord> assets = bitmarksResponse2.getAssets();
        assertFalse(bitmarks.isEmpty());
        assertEquals(bitmarkIds.length, bitmarks.size());
        assertNull(assets);
        assertTrue(Arrays.equals(
                bitmarkIds,
                Stream.of(bitmarks).map(BitmarkRecord::getId).toArray()
        ));
    }

    @Test
    public void testQueryBitmarkByLimitAndLoadAsset_ValidLimitValue_CorrectResponseIsReturn()
            throws Throwable {
        int limit = 1;
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                        .limit(limit)
                        .loadAsset(true);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> Bitmark.list(builder, callback));
        List<BitmarkRecord> bitmarks = bitmarksResponse.getBitmarks();
        List<AssetRecord> assets = bitmarksResponse.getAssets();
        assertFalse(bitmarks.isEmpty());
        assertEquals(limit, bitmarks.size());
        assertNotNull(assets);
    }

    @Test
    public void testCreateShare_OwnedBitmark_CorrectResReturn()
            throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                        .limit(100);
        List<BitmarkRecord> bitmarks = findBitmarks(
                builder,
                b -> !b.isOffer() && b.getStatus() == SETTLED &&
                        b.getHead() != MOVED
        );
        assertTrue(
                "No bitmarks matches the specification",
                bitmarks != null && !bitmarks.isEmpty()
        );

        // Get all shares
        List<ShareRecord> shareRecords;
        try {
            shareRecords =
                    await(callback -> Bitmark.listShares(
                            ACCOUNT1.getAccountNumber(),
                            callback
                    ));

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
            if (e.getErrorCode() != 1001) {
                throw e;
            }
        }

        SecureRandom random = new SecureRandom();
        BitmarkRecord bitmark = bitmarks.get(random.nextInt(bitmarks.size()));

        // Create share
        ShareParams params = new ShareParams(100, bitmark.getHeadId());
        params.sign(ACCOUNT1.getKeyPair());
        Pair<String, String> res = await(callback -> Bitmark.createShare(
                params,
                callback
        ));
        assertNotNull(res);
        assertNotNull(res.first());
        assertNotNull(res.second());
    }

    @Test
    public void testCreateShare_NotOwnedBitmark_CorrectResReturn()
            throws Throwable {
        expectedException.expect(HttpException.class);
        expectedException.expectMessage(containsString("7000"));
        expectedException.expectMessage(containsString(String.valueOf(
                HTTP_INTERNAL_ERROR)));

        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                        .limit(100);
        List<BitmarkRecord> bitmarks = findBitmarks(
                builder,
                b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED
        );
        assertTrue(
                "No bitmarks matches the specification",
                bitmarks != null && !bitmarks.isEmpty()
        );

        // Get all shares
        List<ShareRecord> shareRecords;
        try {
            shareRecords =
                    await(callback -> Bitmark.listShares(
                            ACCOUNT1.getAccountNumber(),
                            callback
                    ));

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
            if (e.getErrorCode() != 1001) {
                throw e;
            }
        }

        SecureRandom random = new SecureRandom();
        BitmarkRecord bitmark = bitmarks.get(random.nextInt(bitmarks.size()));

        // Create share
        ShareParams params = new ShareParams(100, bitmark.getHeadId());
        params.sign(ACCOUNT2.getKeyPair());
        await((Callable1<Pair<String, String>>) callback -> Bitmark.createShare(
                params,
                callback
        ));
    }

    @Test
    public void testGrantShare_OwnedShare_CorrectResReturn() throws Throwable {
        // Get all shares
        List<ShareRecord> shareRecords =
                await(callback -> Bitmark.listShares(
                        ACCOUNT1.getAccountNumber(),
                        callback
                ));
        assertTrue(
                "This guy does not have any shares",
                shareRecords != null && !shareRecords.isEmpty()
        );

        ShareRecord shareRecord =
                shareRecords.stream()
                        .filter(share -> share.getAvailable() > 0)
                        .findFirst()
                        .orElse(null);
        String shareId;

        if (shareRecord == null) {
            // Get owned bitmarks
            BitmarkQueryBuilder builder =
                    new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                            .limit(100);
            List<BitmarkRecord> bitmarks = findBitmarks(
                    builder,
                    b -> !b.isOffer() &&
                            b.getStatus() == SETTLED &&
                            b.getHead() != MOVED
            );
            assertTrue(
                    "No bitmarks matches the specification",
                    bitmarks != null && !bitmarks.isEmpty()
            );

            SecureRandom random = new SecureRandom();
            BitmarkRecord bitmark = bitmarks.get(random.nextInt(bitmarks.size()));

            // Create share
            ShareParams params = new ShareParams(100, bitmark.getHeadId());
            params.sign(ACCOUNT1.getKeyPair());
            Pair<String, String> res = await(callback -> Bitmark.createShare(
                    params,
                    callback
            ));
            shareId = res.second();
        } else {
            shareId = shareRecord.getId();
        }

        // Grant share
        ShareGrantingParams grantingParams =
                new ShareGrantingParams(shareId, 1, ACCOUNT1.toAddress(),
                        ACCOUNT2.toAddress(),
                        secureRandomInt()
                );
        grantingParams.sign(ACCOUNT1.getKeyPair());
        String offerId = await(callback -> Bitmark.grantShare(
                grantingParams,
                callback
        ));
        assertNotNull(offerId);
        assertFalse(offerId.isEmpty());
    }

    @Test
    public void testGrantShare_NotOwnedShare_CorrectResReturn()
            throws Throwable {

        expectedException.expect(HttpException.class);
        expectedException.expectMessage(containsString("7000"));
        expectedException.expectMessage(containsString(String.valueOf(
                HTTP_INTERNAL_ERROR)));

        // Get all shares
        List<ShareRecord> shareRecords =
                await(callback -> Bitmark.listShares(
                        ACCOUNT1.getAccountNumber(),
                        callback
                ));
        assertTrue(
                "This guy does not have any shares",
                shareRecords != null && !shareRecords.isEmpty()
        );

        ShareRecord shareRecord =
                shareRecords.stream()
                        .filter(share -> share.getAvailable() > 0)
                        .findFirst()
                        .orElse(null);

        String shareId;

        if (shareRecord == null) {
            // Get owned bitmarks
            BitmarkQueryBuilder builder =
                    new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                            .limit(100);
            List<BitmarkRecord> bitmarks = findBitmarks(
                    builder,
                    b -> !b.isOffer() &&
                            b.getStatus() == SETTLED &&
                            b.getHead() != MOVED
            );
            assertTrue(
                    "No bitmarks matches the specification",
                    bitmarks != null && !bitmarks.isEmpty()
            );

            SecureRandom random = new SecureRandom();
            BitmarkRecord bitmark = bitmarks.get(random.nextInt(bitmarks.size()));

            // Create share
            ShareParams params = new ShareParams(100, bitmark.getHeadId());
            params.sign(ACCOUNT1.getKeyPair());
            Pair<String, String> res = await(callback -> Bitmark.createShare(
                    params,
                    callback
            ));
            shareId = res.second();
        } else {
            shareId = shareRecord.getId();
        }

        // Grant share
        ShareGrantingParams grantingParams =
                new ShareGrantingParams(shareId, 1, ACCOUNT3.toAddress(),
                        ACCOUNT2.toAddress(),
                        secureRandomInt()
                );
        grantingParams.sign(ACCOUNT3.getKeyPair());
        await((Callable1<String>) callback -> Bitmark.grantShare(
                grantingParams,
                callback
        ));
    }

    @Test
    public void testRespondGrantShare_AcceptGrant_CorrectResReturn()
            throws Throwable {
        // Query offer shares
        List<ShareGrantRecord> grantRecords = await(callback -> Bitmark
                .listShareOffer(
                        ACCOUNT1.getAccountNumber(),
                        ACCOUNT2.getAccountNumber(),
                        callback
                ));

        if (grantRecords == null || grantRecords.isEmpty()) {
            // Get all shares
            List<ShareRecord> shareRecords =
                    await(callback -> Bitmark.listShares(
                            ACCOUNT1.getAccountNumber(),
                            callback
                    ));
            assertTrue(
                    "This guy does not have any shares",
                    shareRecords != null && !shareRecords.isEmpty()
            );

            ShareRecord shareRecord =
                    shareRecords.stream()
                            .filter(share -> share.getAvailable() > 0)
                            .findFirst()
                            .orElse(null);

            assertNotNull("There are no ShareRecord for granting", shareRecord);

            // Grant share
            ShareGrantingParams grantingParams =
                    new ShareGrantingParams(
                            shareRecord.getId(),
                            1,
                            ACCOUNT1.toAddress(),
                            ACCOUNT2.toAddress(),
                            secureRandomInt()
                    );
            grantingParams.sign(ACCOUNT1.getKeyPair());
            String offerId = await(callback -> Bitmark.grantShare(
                    grantingParams,
                    callback
            ));
            grantRecords = await(callback -> Bitmark
                    .listShareOffer(
                            ACCOUNT1.getAccountNumber(),
                            ACCOUNT2.getAccountNumber(),
                            callback
                    ));

            assertFalse(
                    "There are not GrantRecord for respond",
                    grantRecords.isEmpty()
            );
        }

        // Respond offer
        GrantResponseParams responseParams = GrantResponseParams.accept(
                grantRecords.get(0));
        responseParams.sign(ACCOUNT2.getKeyPair());
        String txId = await(callback -> Bitmark.respondShareOffer(
                responseParams,
                callback
        ));
        assertNotNull(txId);
        assertFalse(txId.isEmpty());
    }

    @Test
    public void testRespondGrantShare_RejectGrant_CorrectResReturn()
            throws Throwable {
        // Query offer shares
        List<ShareGrantRecord> grantRecords = await(callback -> Bitmark
                .listShareOffer(
                        ACCOUNT1.getAccountNumber(),
                        ACCOUNT2.getAccountNumber(),
                        callback
                ));

        if (grantRecords == null || grantRecords.isEmpty()) {
            // Get all shares
            List<ShareRecord> shareRecords =
                    await(callback -> Bitmark.listShares(
                            ACCOUNT1.getAccountNumber(),
                            callback
                    ));
            assertTrue(
                    "This guy does not have any shares",
                    shareRecords != null && !shareRecords.isEmpty()
            );

            ShareRecord shareRecord =
                    shareRecords.stream()
                            .filter(share -> share.getAvailable() > 0)
                            .findFirst()
                            .orElse(null);

            assertNotNull("There are no ShareRecord for granting", shareRecord);

            // Grant share
            ShareGrantingParams grantingParams =
                    new ShareGrantingParams(
                            shareRecord.getId(),
                            1,
                            ACCOUNT1.toAddress(),
                            ACCOUNT2.toAddress(),
                            secureRandomInt()
                    );
            grantingParams.sign(ACCOUNT1.getKeyPair());
            String offerId = await(callback -> Bitmark.grantShare(
                    grantingParams,
                    callback
            ));
            grantRecords = await(callback -> Bitmark
                    .listShareOffer(
                            ACCOUNT1.getAccountNumber(),
                            ACCOUNT2.getAccountNumber(),
                            callback
                    ));

            assertFalse(
                    "There are not GrantRecord for respond",
                    grantRecords.isEmpty()
            );
        }

        // Respond offer
        GrantResponseParams responseParams = GrantResponseParams.reject(
                grantRecords.get(0));
        responseParams.sign(ACCOUNT2.getKeyPair());
        String txId = await(callback -> Bitmark.respondShareOffer(
                responseParams,
                callback
        ));
        assertNotNull(txId);
        assertFalse(txId.isEmpty());
    }

    @Test
    public void testRespondGrantShare_CancelGrant_CorrectResReturn()
            throws Throwable {
        // Query offer shares
        List<ShareGrantRecord> grantRecords = await(callback -> Bitmark
                .listShareOffer(
                        ACCOUNT1.getAccountNumber(),
                        ACCOUNT2.getAccountNumber(),
                        callback
                ));

        if (grantRecords == null || grantRecords.isEmpty()) {
            // Get all shares
            List<ShareRecord> shareRecords =
                    await(callback -> Bitmark.listShares(
                            ACCOUNT1.getAccountNumber(),
                            callback
                    ));
            assertTrue(
                    "This guy does not have any shares",
                    shareRecords != null && !shareRecords.isEmpty()
            );

            ShareRecord shareRecord =
                    shareRecords.stream()
                            .filter(share -> share.getAvailable() > 0)
                            .findFirst()
                            .orElse(null);

            assertNotNull("There are no ShareRecord for granting", shareRecord);

            // Grant share
            ShareGrantingParams grantingParams =
                    new ShareGrantingParams(
                            shareRecord.getId(),
                            1,
                            ACCOUNT1.toAddress(),
                            ACCOUNT2.toAddress(),
                            secureRandomInt()
                    );
            grantingParams.sign(ACCOUNT1.getKeyPair());
            String offerId = await(callback -> Bitmark.grantShare(
                    grantingParams,
                    callback
            ));
            grantRecords = await(callback -> Bitmark
                    .listShareOffer(
                            ACCOUNT1.getAccountNumber(),
                            ACCOUNT2.getAccountNumber(),
                            callback
                    ));

            assertFalse(
                    "There are not GrantRecord for respond",
                    grantRecords.isEmpty()
            );
        }

        // Respond offer
        GrantResponseParams responseParams = GrantResponseParams.cancel(
                grantRecords.get(0));
        responseParams.sign(ACCOUNT1.getKeyPair());
        String txId = await(callback -> Bitmark.respondShareOffer(
                responseParams,
                callback
        ));
        assertNotNull(txId);
        assertFalse(txId.isEmpty());
    }

    @Test
    public void testGetShare__CorrectResReturn() throws Throwable {

        String shareId;

        // Get all shares
        List<ShareRecord> shareRecords =
                await(callback -> Bitmark.listShares(
                        ACCOUNT1.getAccountNumber(),
                        callback
                ));

        if (shareRecords == null || shareRecords.isEmpty()) {
            // Get owned bitmarks
            BitmarkQueryBuilder builder =
                    new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                            .limit(100);
            List<BitmarkRecord> bitmarks = findBitmarks(
                    builder,
                    b -> !b.isOffer() &&
                            b.getStatus() == SETTLED &&
                            b.getHead() != MOVED
            );
            assertTrue(
                    "No bitmarks matches the specification",
                    bitmarks != null && !bitmarks.isEmpty()
            );

            SecureRandom random = new SecureRandom();
            BitmarkRecord bitmark = bitmarks.get(random.nextInt(bitmarks.size()));

            // Create share
            ShareParams params = new ShareParams(100, bitmark.getHeadId());
            params.sign(ACCOUNT1.getKeyPair());
            Pair<String, String> res = await(callback -> Bitmark.createShare(
                    params,
                    callback
            ));
            shareId = res.second();
        } else {
            shareId = shareRecords.get(0).getId();
        }

        ShareRecord shareRecord = await(callback -> Bitmark
                .getShare(shareId, callback));
        assertNotNull(shareRecord);
        assertEquals(shareId, shareRecord.getId());
    }

    @Test
    public void testListShares__CorrectResReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                        .limit(100);
        List<BitmarkRecord> bitmarks = findBitmarks(
                builder,
                b -> !b.isOffer() &&
                        b.getStatus() == SETTLED &&
                        b.getHead() != MOVED
        );
        assertTrue(
                "No bitmarks matches the specification",
                bitmarks != null && !bitmarks.isEmpty()
        );

        SecureRandom random = new SecureRandom();
        BitmarkRecord bitmark = bitmarks.get(random.nextInt(bitmarks.size()));

        // Create share
        ShareParams params = new ShareParams(100, bitmark.getHeadId());
        params.sign(ACCOUNT1.getKeyPair());
        Pair<String, String> res = await(callback -> Bitmark.createShare(
                params,
                callback
        ));

        // Query shares
        List<ShareRecord> shareRecords =
                await(callback -> Bitmark.listShares(
                        ACCOUNT1.getAccountNumber(),
                        callback
                ));
        assertNotNull(shareRecords);
        assertFalse(shareRecords.isEmpty());
    }

    @Test
    public void testQueryShareOffer__CorrectResReturn() throws Throwable {
        List<ShareGrantRecord> grantRecords = await(callback -> Bitmark
                .listShareOffer(
                        ACCOUNT1.getAccountNumber(),
                        ACCOUNT2.getAccountNumber(),
                        callback
                ));
        if (grantRecords == null || grantRecords.isEmpty()) {
            // Get all shares
            List<ShareRecord> shareRecords =
                    await(callback -> Bitmark.listShares(
                            ACCOUNT1.getAccountNumber(),
                            callback
                    ));
            assertTrue(
                    "This guy does not have any shares",
                    shareRecords != null && !shareRecords.isEmpty()
            );

            ShareRecord shareRecord =
                    shareRecords.stream()
                            .filter(share -> share.getAvailable() > 0)
                            .findFirst()
                            .orElse(null);
            String shareId;

            if (shareRecord == null) {
                // Get owned bitmarks
                BitmarkQueryBuilder builder =
                        new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber())
                                .limit(100);
                List<BitmarkRecord> bitmarks = findBitmarks(
                        builder,
                        b -> !b.isOffer() &&
                                b.getStatus() == SETTLED &&
                                b.getHead() != MOVED
                );
                assertTrue(
                        "No bitmarks matches the specification",
                        bitmarks != null && !bitmarks.isEmpty()
                );

                SecureRandom random = new SecureRandom();
                BitmarkRecord bitmark = bitmarks.get(random.nextInt(bitmarks.size()));

                // Create share
                ShareParams params = new ShareParams(100, bitmark.getHeadId());
                params.sign(ACCOUNT1.getKeyPair());
                Pair<String, String> res = await(callback -> Bitmark.createShare(
                        params,
                        callback
                ));
                shareId = res.second();
            } else {
                shareId = shareRecord.getId();
            }

            // Grant share
            ShareGrantingParams grantingParams =
                    new ShareGrantingParams(shareId, 1, ACCOUNT1.toAddress(),
                            ACCOUNT2.toAddress(), secureRandomInt()
                    );
            grantingParams.sign(ACCOUNT1.getKeyPair());
            String offerId = await(callback -> Bitmark.grantShare(
                    grantingParams,
                    callback
            ));
            grantRecords = await(callback -> Bitmark
                    .listShareOffer(
                            ACCOUNT1.getAccountNumber(),
                            ACCOUNT2.getAccountNumber(),
                            callback
                    ));
        }
        assertNotNull(grantRecords);
        assertFalse(grantRecords.isEmpty());
    }

    private BitmarkRecord findBitmark(
            BitmarkQueryBuilder queryBuilder,
            Predicate<? super BitmarkRecord> predicate
    )
            throws Throwable {
        final Data<Long> minOffset = new Data<>();
        int retryCount = 50; // Try 50 times in maximum
        while (retryCount > 0) {
            retryCount--;
            GetBitmarksResponse bitmarksResponse =
                    await(callback -> Bitmark
                            .list(
                                    minOffset.getValue() == null
                                    ? queryBuilder
                                    : queryBuilder
                                            .at(minOffset.getValue())
                                            .to("earlier"),
                                    callback
                            ));
            assertFalse(
                    "This guy has not owned bitmarks or cannot find the bitmark match specification",
                    bitmarksResponse.getBitmarks().isEmpty()
            );
            com.annimon.stream.Optional<BitmarkRecord>
                    maxOffsetOpt = Stream.of(bitmarksResponse.getBitmarks())
                    .min(
                            Comparator.comparingLong(BitmarkRecord::getOffset));
            if (maxOffsetOpt.isPresent()) {
                minOffset.setValue(maxOffsetOpt.get().getOffset());
            } else {
                throw new RuntimeException("Cannot get max offset");
            }

            BitmarkRecord bitmark =
                    bitmarksResponse.getBitmarks()
                            .stream()
                            .filter(predicate)
                            .findFirst()
                            .orElse(null);
            if (bitmark != null) {
                return bitmark;
            }
        }
        return null;
    }

    private List<BitmarkRecord> findBitmarks(
            BitmarkQueryBuilder queryBuilder,
            com.annimon.stream.function.Predicate<? super BitmarkRecord> predicate
    )
            throws Throwable {
        final Data<Long> minOffset = new Data<>();
        int retryCount = 50; // Try 50 times in maximum
        while (retryCount > 0) {
            retryCount--;
            GetBitmarksResponse bitmarksResponse =
                    await(callback -> Bitmark
                            .list(
                                    minOffset.getValue() == null
                                    ? queryBuilder
                                    : queryBuilder
                                            .at(minOffset.getValue())
                                            .to("earlier"),
                                    callback
                            ));
            assertFalse(
                    "This guy has not owned bitmarks or cannot find the bitmarks match specification",
                    bitmarksResponse.getBitmarks().isEmpty()
            );
            com.annimon.stream.Optional<BitmarkRecord> maxOffsetOpt =
                    Stream.of(bitmarksResponse.getBitmarks()).min(
                            Comparator.comparingLong(BitmarkRecord::getOffset));
            if (maxOffsetOpt.isPresent()) {
                minOffset.setValue(maxOffsetOpt.get().getOffset());
            } else {
                throw new RuntimeException("Cannot get max offset");
            }

            List<BitmarkRecord> bitmarks =
                    Stream.of(bitmarksResponse.getBitmarks())
                            .filter(predicate)
                            .collect(
                                    Collectors.toList());
            if (!bitmarks.isEmpty()) {
                return bitmarks;
            }
        }
        return null;
    }


}
