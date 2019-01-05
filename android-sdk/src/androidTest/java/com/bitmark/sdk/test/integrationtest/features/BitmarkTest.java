package com.bitmark.sdk.test.integrationtest.features;

import com.bitmark.apiservice.params.*;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.response.GetBitmarkResponse;
import com.bitmark.apiservice.response.GetBitmarksResponse;
import com.bitmark.apiservice.response.RegistrationResponse;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.callback.Callable1;
import com.bitmark.apiservice.utils.error.HttpException;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.apiservice.utils.record.BitmarkRecord;
import com.bitmark.apiservice.utils.record.OfferRecord;
import com.annimon.stream.Stream;
import com.bitmark.cryptography.crypto.Sha3256;
import org.junit.Rule;
import org.junit.Test;
import com.bitmark.sdk.features.Asset;
import com.bitmark.sdk.features.Bitmark;
import com.bitmark.sdk.test.integrationtest.utils.extensions.TemporaryFolderRule;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bitmark.apiservice.utils.Awaitility.await;
import static com.bitmark.apiservice.utils.record.BitmarkRecord.Status.SETTLED;
import static com.bitmark.apiservice.utils.record.Head.MOVED;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class BitmarkTest extends BaseFeatureTest {

    @Rule
    public TemporaryFolderRule temporaryFolderRule = new TemporaryFolderRule();

    @Test
    public void testIssueBitmark_OwnedMultipleBitmark_CorrectSuccessResponseIsReturn() throws Throwable {
        // Register asset
        File asset = temporaryFolderRule.newFile();
        Address owner = ACCOUNT1.toAddress();
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from android sdk test");
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
        List<String> txIds =
                await(callback -> Bitmark.issue(issuanceParams,
                        callback));
        assertEquals(txIds.size(), quantity);
        assertFalse(txIds.get(0).isEmpty());
    }

    @Test
    public void testIssueBitmark_OwnedSingleBitmark_CorrectSuccessResponseIsReturn() throws Throwable {
        // Register asset
        File asset = temporaryFolderRule.newFile();
        Address owner = ACCOUNT1.toAddress();
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from android sdk test");
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
        List<String> txIds =
                await(callback -> Bitmark.issue(issuanceParams,
                        callback));
        assertEquals(txIds.size(), 1);
        assertFalse(txIds.get(0).isEmpty());
    }

    @Test
    public void testIssueBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn() throws Throwable {
        expectedException.expect(HttpException.class);
        expectedException.expectMessage(containsString("1000"));
        expectedException.expectMessage(containsString(String.valueOf(HTTP_BAD_REQUEST)));

        Address issuer = Address.fromAccountNumber(ACCOUNT1.getAccountNumber());
        IssuanceParams issuanceParams = new IssuanceParams(
                "bda050d2235402751ed09e73486c2cced34424c35d4d799eaa37ab73c3dba663", issuer);
        issuanceParams.sign(KEY1);
        await((Callable1<List<String>>) callback -> Bitmark.issue(issuanceParams, callback));
    }

    @Test
    public void testTransferBitmarkFromLink_OwnedBitmark_CorrectSuccessResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).pending(true);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> Bitmark.list(builder, callback));
        assertFalse("This guy has not owned bitmarks", bitmarksResponse.getBitmarks().isEmpty());

        // Transfer bitmark
        TransferParams params = new TransferParams(ACCOUNT2.toAddress());
        BitmarkRecord bitmark =
                Stream.of(bitmarksResponse.getBitmarks()).filter(b -> !b.isOffer() && b.getStatus() == SETTLED).findFirst().orElse(null);
        assertNotNull("No bitmark matches the specification", bitmark);
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        String txId = await(callback -> Bitmark.transfer(params, callback));
        assertNotNull(txId);
        assertEquals(Sha3256.HASH_BYTE_LENGTH, HEX.decode(txId).length);

    }


    @Test
    public void testTransferBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn() throws Throwable {
        expectedException.expect(HttpException.class);
        expectedException.expectMessage(containsString("7000"));
        expectedException.expectMessage(String.valueOf(HTTP_INTERNAL_ERROR));

        // Get not owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT2.getAccountNumber()).pending(true);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> Bitmark.list(builder, callback));
        assertFalse("This guy has not owned bitmarks", bitmarksResponse.getBitmarks().isEmpty());

        // Transfer bitmark
        TransferParams params = new TransferParams(ACCOUNT3.toAddress());
        BitmarkRecord bitmark =
                Stream.of(bitmarksResponse.getBitmarks()).filter(b -> !b.isOffer() && b.getStatus() == SETTLED).findFirst().orElse(null);
        assertNotNull("No bitmark matches the specification", bitmark);
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        await((Callable1<String>) callback -> Bitmark.transfer(params, callback));

    }

    @Test
    public void testOfferBitmark_OwnedBitmark_CorrectSuccessResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).pending(true);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> Bitmark.list(builder, callback));
        assertFalse("This guy has not owned bitmarks", bitmarksResponse.getBitmarks().isEmpty());

        // Offer bitmark
        TransferOfferParams params = new TransferOfferParams(ACCOUNT2.toAddress());
        BitmarkRecord bitmark =
                Stream.of(bitmarksResponse.getBitmarks()).filter(b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED).findFirst().orElse(null);
        assertNotNull("No bitmark matches the specification", bitmark);
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        String offerId = await(callback -> Bitmark.offer(params, callback));
        assertNotNull(offerId);
        assertFalse(offerId.isEmpty());
    }

    @Test
    public void testOfferBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn() throws Throwable {
        expectedException.expect(HttpException.class);
        expectedException.expectMessage(containsString("2013"));
        expectedException.expectMessage(containsString(String.valueOf(HTTP_FORBIDDEN)));

        // Get not owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT2.getAccountNumber()).pending(true);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> Bitmark.list(builder, callback));
        assertFalse("This guy has not owned bitmarks", bitmarksResponse.getBitmarks().isEmpty());

        // Offer bitmark
        TransferOfferParams params = new TransferOfferParams(ACCOUNT3.toAddress());
        BitmarkRecord bitmark =
                Stream.of(bitmarksResponse.getBitmarks()).filter(b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED).findFirst().orElse(null);
        assertNotNull("No bitmark matches the specification", bitmark);
        String link = bitmark.getHeadId();
        params.setLink(link);
        params.sign(KEY1);
        await((Callable1<String>) callback -> Bitmark.offer(params, callback));

    }

    @Test
    public void testReplyOffer_AcceptOffer_CorrectSuccessResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).pending(true);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> Bitmark.list(builder, callback));
        assertFalse("This guy has not owned bitmarks", bitmarksResponse.getBitmarks().isEmpty());

        // Offer bitmark
        TransferOfferParams offerParams = new TransferOfferParams(ACCOUNT2.toAddress());
        BitmarkRecord bitmark =
                Stream.of(bitmarksResponse.getBitmarks()).filter(b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED).findFirst().orElse(null);
        assertNotNull("No bitmark matches the specification", bitmark);
        String link = bitmark.getHeadId();
        offerParams.setLink(link);
        offerParams.sign(KEY1);
        String offerId = await(callback -> Bitmark.offer(offerParams,
                callback));
        assertNotNull("Offer is not successful", offerId);
        assertFalse("Offer is not successful", offerId.isEmpty());

        // Respond offer
        GetBitmarkResponse response =
                await(callback -> Bitmark.get(bitmark.getId(),
                        false,
                        callback));
        OfferRecord offerRecord = response.getBitmark().getOffer();
        TransferResponseParams responseParams = TransferResponseParams.accept(offerRecord);
        responseParams.sign(KEY2);
        String txId = await(callback -> Bitmark.respond(responseParams,
                callback));
        assertNotNull(txId);
        assertEquals(Sha3256.HASH_BYTE_LENGTH, HEX.decode(txId).length);
    }

    @Test
    public void testReplyOffer_CancelOffer_CorrectSuccessResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).pending(true);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> Bitmark.list(builder, callback));
        assertFalse("This guy has not owned bitmarks", bitmarksResponse.getBitmarks().isEmpty());

        // Offer bitmark
        TransferOfferParams offerParams = new TransferOfferParams(ACCOUNT2.toAddress());
        BitmarkRecord bitmark =
                Stream.of(bitmarksResponse.getBitmarks()).filter(b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED).findFirst().orElse(null);
        assertNotNull("No bitmark matches the specification", bitmark);
        String link = bitmark.getHeadId();
        offerParams.setLink(link);
        offerParams.sign(KEY1);
        String offerId = await(callback -> Bitmark.offer(offerParams,
                callback));
        assertNotNull(offerId, "Offer is not successful");
        assertFalse("Offer is not successful", offerId.isEmpty());

        // Respond offer
        GetBitmarkResponse response =
                await(callback -> Bitmark.get(bitmark.getId(),
                        callback));
        OfferRecord offerRecord = response.getBitmark().getOffer();
        TransferResponseParams responseParams = TransferResponseParams.cancel(offerRecord,
                bitmark.getOwner());
        responseParams.setSigningKey(KEY1);
        String status = await(callback -> Bitmark.respond(responseParams,
                callback));
        assertNotNull(status);
        assertEquals("ok", status);
    }

    @Test
    public void testReplyOffer_RejectOffer_CorrectSuccessResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).pending(true);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> Bitmark.list(builder, callback));
        assertFalse("This guy has not owned bitmarks", bitmarksResponse.getBitmarks().isEmpty());

        // Offer bitmark
        TransferOfferParams offerParams = new TransferOfferParams(ACCOUNT2.toAddress());
        BitmarkRecord bitmark =
                Stream.of(bitmarksResponse.getBitmarks()).filter(b -> !b.isOffer() && b.getStatus() == SETTLED && b.getHead() != MOVED).findFirst().orElse(null);
        assertNotNull("No bitmark matches the specification", bitmark);
        String link = bitmark.getHeadId();
        offerParams.setLink(link);
        offerParams.sign(KEY1);
        String offerId = await(callback -> Bitmark.offer(offerParams,
                callback));
        assertNotNull("Offer is not successful", offerId);
        assertFalse("Offer is not successful", offerId.isEmpty());

        // Respond offer
        GetBitmarkResponse response =
                await(callback -> Bitmark.get(bitmark.getId(),
                        callback));
        OfferRecord offerRecord = response.getBitmark().getOffer();
        TransferResponseParams responseParams = TransferResponseParams.reject(offerRecord);
        responseParams.setSigningKey(KEY2);
        String status = await(callback -> Bitmark.respond(responseParams,
                callback));
        assertNotNull(status);
        assertEquals("ok", status);
    }

    @Test
    public void testQueryBitmarkByIdWithAsset_ExistedBitmarkId_CorrectResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).limit(1);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> Bitmark.list(builder, callback));
        assertFalse("This guy has not owned bitmarks", bitmarksResponse.getBitmarks().isEmpty());

        // Get bitmark by id
        String id = bitmarksResponse.getBitmarks().get(0).getId();
        GetBitmarkResponse bitmarkResponse =
                await(callback -> Bitmark.get(id, true, callback));
        BitmarkRecord bitmark = bitmarkResponse.getBitmark();
        AssetRecord asset = bitmarkResponse.getAsset();
        assertNotNull(bitmark);
        assertNotNull(asset);
        assertEquals(id, bitmark.getId());
        assertEquals(bitmark.getAssetId(), asset.getId());
    }

    @Test
    public void testQueryBitmarkByIdWithoutAsset_ExistedBitmarkId_CorrectResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).limit(1);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> Bitmark.list(builder, callback));
        assertFalse("This guy has not owned bitmarks", bitmarksResponse.getBitmarks().isEmpty());

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
    public void testQueryBitmarksByIds_ValidBitmarkIds_CorrectResponseIsReturn() throws Throwable {
        // Get owned bitmarks
        int limit = 1;
        BitmarkQueryBuilder builder1 =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).limit(limit);
        GetBitmarksResponse bitmarksResponse1 =
                await(callback -> Bitmark.list(builder1,
                        callback));
        assertFalse("This guy has not owned bitmarks", bitmarksResponse1.getBitmarks().isEmpty());
        String[] bitmarkIds =
                Stream.of(bitmarksResponse1.getBitmarks()).map(BitmarkRecord::getId).toArray(String[]::new);

        BitmarkQueryBuilder builder2 =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).bitmarkIds(bitmarkIds);
        GetBitmarksResponse bitmarksResponse2 =
                await(callback -> Bitmark.list(builder2,
                        callback));
        List<BitmarkRecord> bitmarks = bitmarksResponse2.getBitmarks();
        List<AssetRecord> assets = bitmarksResponse2.getAssets();
        assertFalse(bitmarks.isEmpty());
        assertEquals(bitmarkIds.length, bitmarks.size());
        assertNull(assets);
        assertTrue(Arrays.equals(bitmarkIds,
                Stream.of(bitmarks).map(BitmarkRecord::getId).toArray()));
    }

    @Test
    public void testQueryBitmarkByLimitAndLoadAsset_ValidLimitValue_CorrectResponseIsReturn() throws Throwable {
        int limit = 1;
        BitmarkQueryBuilder builder =
                new BitmarkQueryBuilder().ownedBy(ACCOUNT1.getAccountNumber()).limit(limit).loadAsset(true);
        GetBitmarksResponse bitmarksResponse =
                await(callback -> Bitmark.list(builder, callback));
        List<BitmarkRecord> bitmarks = bitmarksResponse.getBitmarks();
        List<AssetRecord> assets = bitmarksResponse.getAssets();
        assertFalse(bitmarks.isEmpty());
        assertEquals(limit, bitmarks.size());
        assertNotNull(assets);
    }


}
