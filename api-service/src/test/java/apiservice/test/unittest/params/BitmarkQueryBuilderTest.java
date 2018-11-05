package apiservice.test.unittest.params;

import apiservice.params.query.AssetQueryBuilder;
import apiservice.params.query.BitmarkQueryBuilder;
import apiservice.params.query.TransactionQueryBuilder;
import apiservice.test.BaseTest;
import cryptography.error.ValidateException;
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
        assertThrows(ValidateException.class, () -> new AssetQueryBuilder().registrant(""));
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
        assertThrows(ValidateException.class, () -> new BitmarkQueryBuilder().referencedAssetId(
                ""));
        assertThrows(ValidateException.class,
                () -> new BitmarkQueryBuilder().referencedAssetId(null));
    }

    @Test
    public void testBuildTxQueryBuilder_InvalidQueryValues_CorrectParamsIsReturn() {
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().limit(null));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().limit(-1));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().loadAsset(null));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().ownedBy(null));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().ownedBy(""));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().referenceAsset(null));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().referenceAsset(""));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().referenceBitmark(null));
        assertThrows(ValidateException.class, () -> new TransactionQueryBuilder().referenceBitmark(""));
    }

    private static Stream<Arguments> createAssetQueryBuilder() {
        return Stream.of(Arguments.of(new AssetQueryBuilder().registrant("registrantId").limit(15), "limit=15&registrant=registrantId"),
                Arguments.of(new AssetQueryBuilder().assetIds(new String[]{"abc", "def"}),
                        "asset_ids=abc&asset_ids=def&limit=100"),
                Arguments.of(new AssetQueryBuilder().limit(999).assetIds(new String[]{"abc"}).registrant("registrantId"), "asset_ids=abc&limit=999&registrant=registrantId"));
    }

    private static Stream<Arguments> createBitmarkQueryBuilder() {
        return Stream.of(Arguments.of(new BitmarkQueryBuilder().issuedBy("issuedById").pending(true), "issued_by=issuedById&limit=100&pending=true"),
                Arguments.of(new BitmarkQueryBuilder().limit(999).referencedAssetId("assetId").at(1234L).to("later"), "asset_id=assetId&at=1234&limit=999&to=later"));
    }

    private static Stream<Arguments> createTxQueryBuilder() {
        return Stream.of(Arguments.of(new TransactionQueryBuilder().loadAsset(true).limit(10),
                "asset=true&limit=10"),
                Arguments.of(new TransactionQueryBuilder().referenceAsset("assetId").ownedBy(
                        "ownedById"), "asset_id=assetId&limit=100&owner=ownedById"));
    }
}
