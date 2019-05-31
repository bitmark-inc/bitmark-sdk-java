package com.bitmark.apiservice.test.unittest.params;

import com.bitmark.apiservice.params.query.AssetQueryBuilder;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.params.query.TransactionQueryBuilder;
import com.bitmark.apiservice.test.BaseTest;
import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Hieu Pham
 * @since 11/5/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class BitmarkQueryBuilderTest extends BaseTest {

    @ParameterizedTest
    @MethodSource("createAssetQueryBuilder")
    public void testBuildAssetQueryBuilder_ValidQueryValues_CorrectParamsIsReturn(AssetQueryBuilder builder, String expectedParams) {
        assertEquals(expectedParams, builder.toUrlQuery());
    }

    @ParameterizedTest
    @MethodSource("createBitmarkQueryBuilder")
    public void testBuildBitmarkQueryBuilder_ValidQueryValues_CorrectParamsIsReturn(BitmarkQueryBuilder builder, String expectedParams) {
        assertEquals(expectedParams, builder.toUrlQuery());
    }

    @ParameterizedTest
    @MethodSource("createTxQueryBuilder")
    public void testBuildTxQueryBuilder_ValidQueryValues_CorrectParamsIsReturn(TransactionQueryBuilder builder, String expectedParams) {
        assertEquals(expectedParams, builder.toUrlQuery());
    }

    @Test
    public void testBuildAssetQueryBuilder_InvalidQueryValues_CorrectParamsIsReturn() {
        assertThrows(ValidateException.class, () -> new AssetQueryBuilder().assetIds(null));
        assertThrows(ValidateException.class, () -> new AssetQueryBuilder().limit(-1));
        assertThrows(ValidateException.class, () -> new AssetQueryBuilder().registeredBy(""));
    }

    @Test
    public void testBuildBitmarkQueryBuilder_InvalidQueryValues_CorrectParamsIsReturn() {
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().at(-1L));
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().bitmarkIds(null));
        assertThrows(ValidateException.class,
                () -> new BitmarkQueryBuilder().bitmarkIds(new String[]{}));
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().issuedBy(null));
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().limit(-1));
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().limit(null));
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().loadAsset(null));
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().offerFrom(null));
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().offerFrom(""));
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().ownedBy(null));
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().ownedBy(""));
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().pending(null));
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().offerTo(null));
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().offerTo(""));
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().referencedAsset(""));
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().referencedAsset(null));
    }

    @Test
    public void testBuildTxQueryBuilder_InvalidQueryValues_CorrectParamsIsReturn() {
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().limit(null));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().limit(-1));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().loadAsset(null));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().ownedBy(null));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().ownedBy(""));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().referencedAsset(null));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().referencedAsset(""));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().referencedBitmark(null));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().referencedBitmark(""));
    }

    private static Stream<Arguments> createAssetQueryBuilder() {
        return Stream.of(Arguments.of(new AssetQueryBuilder().registeredBy("registrantId").limit(15), "limit=15&pending=true&registrant=registrantId"),
                Arguments.of(new AssetQueryBuilder().assetIds(new String[]{"abc", "def"}),
                        "asset_ids=abc&asset_ids=def&limit=100&pending=true"),
                Arguments.of(new AssetQueryBuilder().limit(23).assetIds(new String[]{"abc"}).registeredBy("registrantId"), "asset_ids=abc&limit=23&pending=true&registrant=registrantId"));
    }

    private static Stream<Arguments> createBitmarkQueryBuilder() {
        return Stream.of(Arguments.of(new BitmarkQueryBuilder().issuedBy("issuedById").pending(true), "issued_by=issuedById&limit=100&pending=true"),
                Arguments.of(new BitmarkQueryBuilder().limit(23).referencedAsset("assetId").at(1234L).to("later"), "asset_id=assetId&at=1234&limit=23&pending=true&to=later"));
    }

    private static Stream<Arguments> createTxQueryBuilder() {
        return Stream.of(Arguments.of(new TransactionQueryBuilder().loadAsset(true).limit(10),
                "asset=true&limit=10&pending=true>"),
                Arguments.of(new TransactionQueryBuilder().referencedAsset("assetId").ownedBy(
                        "ownedById"), "asset_id=assetId&limit=100&owner=ownedById&pending=true>"));
    }
}
