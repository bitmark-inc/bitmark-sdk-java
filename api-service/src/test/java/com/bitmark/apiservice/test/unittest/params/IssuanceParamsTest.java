package com.bitmark.apiservice.test.unittest.params;

import com.bitmark.apiservice.params.IssuanceParams;
import com.bitmark.apiservice.test.BaseTest;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.cryptography.crypto.key.StandardKeyPair;
import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.bitmark.apiservice.test.utils.FileUtils.loadRequest;
import static com.bitmark.apiservice.utils.ArrayUtil.isDuplicate;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hieu Pham
 * @since 9/5/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class IssuanceParamsTest extends BaseTest {

    private static final String PRIVATE_KEY =
            "0246a917d422e596168185cea9943459c09751532c52fe4ddc27b06e2893ef2258760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f";

    private static final String PUBLIC_KEY =
            "58760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f";

    private static final KeyPair KEY = StandardKeyPair.from(HEX.decode(PUBLIC_KEY),
                                                            HEX.decode(PRIVATE_KEY));

    private static final Address ADDRESS = Address.fromAccountNumber(
            "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva");

    private static final String ASSET_ID =
            "f5ad8d9b58e122d2d229f86eaa5d276496a5a3da19d53c887a23f81955a3d07266b50a896d332abc1d1845850311e50570cb56ee507b89ec18bc91edc34c1059";


    @DisplayName("Verify function new IssuanceParams(String, Address) works well with happy " +
                 "condition")
    @ParameterizedTest
    @ValueSource(strings = {
            "f5ad8d9b58e122d2d229f86eaa5d276496a5a3da19d53c887a23f81955a3d07266b50a896d332abc1d1845850311e50570cb56ee507b89ec18bc91edc34c1059",
            "613ab997fb2172fd4d12fb2b82157e3cd081e2d95adefe96c6be9151667e67ed72fa0caedd109805b6f07c3efe428f03b836ad36ec51165f35adb4572530e2bf",
            "c5c834efb58a5106dd601676bf606da91b752cababfbe223122f71be5b2963dc57d593f5f45b61f1abe2fcf80277fd4075ee561813bc69c0343b4c4e68516237"})
    public void testConstructIssuanceParamsWithoutNonce_ValidAssetId_ValidInstanceIsReturn(
            String assetId) {
        assertDoesNotThrow(() -> new IssuanceParams(assetId, ADDRESS));
    }

    @DisplayName("Verify function new IssuanceParams(String, Address) throws exception with " +
                 "invalid assetId")
    @ParameterizedTest
    @MethodSource("createInvalidAssetId")
    public void testConstructIssuanceParamsWithoutNonce_InvalidAssetId_ErrorIsThrow(
            String assetId) {
        assertThrows(ValidateException.class, () -> new IssuanceParams(assetId, ADDRESS));
    }

    @DisplayName("Verify function new IssuanceParams(String, Address, int) works well with happy " +
                 "condition")
    @ParameterizedTest
    @ValueSource(ints = {1, 100, 1000})
    public void testConstructIssuanceParamsWithQuantity_ValidQuantity_ValidInstanceIsReturn(
            int quantity) {
        assertDoesNotThrow(() -> new IssuanceParams(ASSET_ID, ADDRESS, quantity));
        final IssuanceParams params = new IssuanceParams(ASSET_ID, ADDRESS, quantity);
        params.generateNonces(AssetRecord.Status.CONFIRMED);
        assertEquals(quantity, params.getNonces().length);
        assertFalse(isDuplicate(params.getNonces()));
    }

    @DisplayName("Verify function new IssuanceParams(String, Address, int) throws exception with " +
                 "invalid quantity")
    @ParameterizedTest
    @ValueSource(ints = {-1, -100, 0})
    public void testConstructIssuanceParamsWithQuantity_InvalidQuantity_ErrorIsThrow(int quantity) {
        assertThrows(ValidateException.class, () -> new IssuanceParams(ASSET_ID, ADDRESS,
                                                                       quantity));
    }

    @DisplayName("Verify function IssuanceParams.sign(KeyPair) in single params works well with " +
                 "happy condition")
    @ParameterizedTest
    @MethodSource("createValidAssetIdNonceSignature")
    public void testSignOneNonce_NoCondition_ValidSignatureIsReturn(String assetId, int[] nonces,
                                                                    byte[] signature) {
        final IssuanceParams params = new IssuanceParams(assetId, ADDRESS);
        params.setNonces(nonces);
        params.sign(KEY);
        assertEquals(params.getSignatures().size(), 1);
        assertTrue(Arrays.equals(signature, params.getSignatures().get(0)));
    }

    @DisplayName("Verify function IssuanceParams.sign(KeyPair) in multiple params works well with" +
                 " happy condition")
    @ParameterizedTest
    @MethodSource("createValidAssetIdNonceSignatures")
    public void testSignMultipleNonce_NoCondition_ValidSignatureIsReturn(String assetId,
                                                                         int[] nonces,
                                                                         List<byte[]> expectedSignature) {
        final IssuanceParams params = new IssuanceParams(assetId, ADDRESS);
        params.setNonces(nonces);
        params.sign(KEY);
        final List<byte[]> signatures = params.getSignatures();
        assertEquals(expectedSignature.size(), signatures.size());
        for (int i = 0, size = expectedSignature.size(); i < size; i++) {
            assertTrue(Arrays.equals(expectedSignature.get(i), signatures.get(i)));
        }
    }

    @DisplayName("Verify function IssuanceParams.toJson() works well with happy condition")
    @ParameterizedTest
    @MethodSource("createValidIssuanceParamsJson")
    public void testToJson_ParamsIsSigned_ValidJsonIsReturn(IssuanceParams params,
                                                            String expectedJson) {
        assertEquals(expectedJson, params.toJson());
    }

    @DisplayName("Verify function IssuanceParams.toJson() throw error when the IssuanceParams is " +
                 "not signed")
    @ParameterizedTest
    @MethodSource("createNotSignedIssuanceParams")
    public void testToJson_ParamsIsNotSigned_ErrorIsThrow(IssuanceParams params) {
        assertThrows(UnsupportedOperationException.class, params::toJson);
    }

    @Test
    public void testSignParams_MissingGenerateNonces_ErrorIsThrow(){
        IssuanceParams params = new IssuanceParams(ASSET_ID, ADDRESS);
        assertThrows(IllegalArgumentException.class, () -> params.sign(KEY));
    }

    @Test
    public void testGenerateNonce_ValidAssetStatus_ValidNonceIsReturn(){
        IssuanceParams params = new IssuanceParams(ASSET_ID, ADDRESS);
        params.generateNonces(AssetRecord.Status.PENDING);
        assertEquals(1, params.getNonces().length);
        assertEquals(0, params.getNonces()[0]);

        params = new IssuanceParams(ASSET_ID, ADDRESS, 3);
        params.generateNonces(AssetRecord.Status.PENDING);
        assertEquals(3, params.getNonces().length);
        assertEquals(0, params.getNonces()[0]);
        assertNotEquals(0, params.getNonces()[1]);

        params = new IssuanceParams(ASSET_ID, ADDRESS);
        params.generateNonces(AssetRecord.Status.CONFIRMED);
        assertEquals(1, params.getNonces().length);
        assertNotEquals(0, params.getNonces()[0]);

        params = new IssuanceParams(ASSET_ID, ADDRESS, 3);
        params.generateNonces(AssetRecord.Status.CONFIRMED);
        assertEquals(3, params.getNonces().length);
        assertNotEquals(0, params.getNonces()[0]);
    }

    @Test
    public void testGenerateNonce_InvalidAssetStatus_ErrorIsThrow(){

    }

    private static Stream<String> createInvalidAssetId() {
        return Stream.of(null, "",
                         "f5ad8d9b58e122d2d229f86eaa5d276496a5a3da19d53c887a23f81955a3d07266b50a896d332abc1d1845850311e50570cb56ee507b89ec18bc91edc34c1059ff");
    }

    private static Stream<Arguments> createValidAssetIdNonceSignatures() {
        final List<byte[]> signatures1 = new ArrayList<byte[]>() {{
            add(HEX.decode(
                    "0c1e8a5fb7ba6cbfa68da52354590df0d1979e0671dc2c9ba19811a166e8c4b54433dfbfb56c39ded50d0f54ba8676b9c01dad55f8ab1c1879bc335d635e4406"));
            add(HEX.decode(
                    "545bdabea2edaff9140f8c21da18613c827dd56be349b7f053a08128d453d66bbe01573ac7fce454797f4ba689da15a4cd1eb61addfc3c42f11a87d9d6a27d0d"));
            add(HEX.decode(
                    "edd0ca5311cdb8905a5709165f2f18730431237c587347f352fab813612437648bd598588e272e68c7a5edb29278c1e4c9d3e4bbb3fc88566cc63d5e2bc50306"));
            add(HEX.decode(
                    "43231b518617c5812307a1b084ddab34de71a6db751de23d2b359a6f3315ba7dbc518d6cab7c07cb57ec787ac94e50ae3a7a52d135c6e49883ac5692b8aa530c"));
            add(HEX.decode(
                    "21c1107d73d6da65ce5192bb12a93a22ed47400c76b076d4e43434728fdb369514a4956c19ce7116f6fd619c79f0aaae2d8cdfa9c4a2779702fe334fda4f8205"));
        }};

        final List<byte[]> signatures2 = new ArrayList<byte[]>() {{
            add(HEX.decode(
                    "5d18bb4eaf85b4712b2b462847bbeb2fe331650bbaed07d5b4b7a3a1b36397cfbd56dad75fb56109d25f2ccb78c6773ec2193a314cf9d498ea24bfe9e7aff205"));
            add(HEX.decode(
                    "08189dd164b429078e7dccf0965d1b9d452074a52edb89a6ab8db538733b8c2aa162a27ef5c94cb3b58e7a345c4bf901d8a455af417ff3879744e012cd53e10b"));
            add(HEX.decode(
                    "c149ee642b12ddc3ec503b985ad2d7121024db019698d9e370b94afb8a9ec9b6d1569e372f055f79b154db18c01badcfd542622e02b59431e03e869299822405"));
            add(HEX.decode(
                    "e8b4994605f3462184c490ce620bd87f7149647dd6eca1d7eafec29ad58ab521d28f3b6df4260ec3ff23f806053faae4c66f89b78dff45f707eca25077fe5c01"));
        }};
        return Stream.of(Arguments.of(
                "f5ad8d9b58e122d2d229f86eaa5d276496a5a3da19d53c887a23f81955a3d07266b50a896d332abc1d1845850311e50570cb56ee507b89ec18bc91edc34c1059",
                new int[]{1, 2, 3, 4, 5}, signatures1),
                         Arguments.of(
                                 "613ab997fb2172fd4d12fb2b82157e3cd081e2d95adefe96c6be9151667e67ed72fa0caedd109805b6f07c3efe428f03b836ad36ec51165f35adb4572530e2bf",
                                 new int[]{1, 3, 5, 7}, signatures2));
    }

    private static Stream<Arguments> createValidAssetIdNonceSignature() {
        return Stream.of(Arguments.of(
                "f5ad8d9b58e122d2d229f86eaa5d276496a5a3da19d53c887a23f81955a3d07266b50a896d332abc1d1845850311e50570cb56ee507b89ec18bc91edc34c1059",
                new int[]{1},
                HEX.decode(
                        "0c1e8a5fb7ba6cbfa68da52354590df0d1979e0671dc2c9ba19811a166e8c4b54433dfbfb56c39ded50d0f54ba8676b9c01dad55f8ab1c1879bc335d635e4406")),
                         Arguments.of(
                                 "613ab997fb2172fd4d12fb2b82157e3cd081e2d95adefe96c6be9151667e67ed72fa0caedd109805b6f07c3efe428f03b836ad36ec51165f35adb4572530e2bf",
                                 new int[]{3},
                                 HEX.decode(
                                         "08189dd164b429078e7dccf0965d1b9d452074a52edb89a6ab8db538733b8c2aa162a27ef5c94cb3b58e7a345c4bf901d8a455af417ff3879744e012cd53e10b")),
                         Arguments.of(
                                 "f5ad8d9b58e122d2d229f86eaa5d276496a5a3da19d53c887a23f81955a3d07266b50a896d332abc1d1845850311e50570cb56ee507b89ec18bc91edc34c1059",
                                 new int[]{5},
                                 HEX.decode(
                                         "21c1107d73d6da65ce5192bb12a93a22ed47400c76b076d4e43434728fdb369514a4956c19ce7116f6fd619c79f0aaae2d8cdfa9c4a2779702fe334fda4f8205")));
    }

    private static Stream<Arguments> createValidIssuanceParamsJson() throws IOException {
        final IssuanceParams params1 = new IssuanceParams(ASSET_ID, ADDRESS);
        params1.setNonces(new int[]{1});
        final IssuanceParams params2 = new IssuanceParams(ASSET_ID, ADDRESS);
        params2.setNonces(new int[]{3});
        final IssuanceParams params3 = new IssuanceParams(ASSET_ID, ADDRESS);
        params3.setNonces(new int[]{1, 3});
        params1.sign(KEY);
        params2.sign(KEY);
        params3.sign(KEY);
        final String json1 = loadRequest("/issue/single_issue1.json");
        final String json2 = loadRequest("/issue/single_issue2.json");
        final String json3 = loadRequest("/issue/multiple_issue1.json");
        return Stream.of(Arguments.of(params1, json1), Arguments.of(params2, json2),
                         Arguments.of(params3, json3));
    }

    private static Stream<IssuanceParams> createNotSignedIssuanceParams() {
        final IssuanceParams params1 = new IssuanceParams(ASSET_ID, ADDRESS, 1);
        final IssuanceParams params2 = new IssuanceParams(ASSET_ID, ADDRESS, 3);
        final IssuanceParams params3 = new IssuanceParams(ASSET_ID, ADDRESS, 2);
        return Stream.of(params1, params2, params3);
    }

}
