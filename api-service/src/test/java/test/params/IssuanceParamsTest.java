package test.params;

import crypto.key.KeyPair;
import crypto.key.StandardKeyPair;
import error.ValidateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import service.params.IssuanceParams;
import test.BaseTest;
import utils.Address;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;
import static util.FileUtils.loadRequest;
import static utils.ArrayUtil.isDuplicate;

/**
 * @author Hieu Pham
 * @since 9/5/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class IssuanceParamsTest extends BaseTest {

    private static final String PRIVATE_KEY =
            "0246A917D422E596168185CEA9943459C09751532C52FE4DDC27B06E2893EF2258760A01EDF5ED4F95BFE977D77A27627CD57A25DF5DEA885972212C2B1C0E2F";

    private static final String PUBLIC_KEY =
            "58760A01EDF5ED4F95BFE977D77A27627CD57A25DF5DEA885972212C2B1C0E2F";

    private static final KeyPair KEY = StandardKeyPair.from(HEX.decode(PUBLIC_KEY),
            HEX.decode(PRIVATE_KEY));

    private static final Address ADDRESS = Address.fromAccountNumber(
            "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva");

    private static final String ASSET_ID =
            "F5AD8D9B58E122D2D229F86EAA5D276496A5A3DA19D53C887A23F81955A3D07266B50A896D332ABC1D1845850311E50570CB56EE507B89EC18BC91EDC34C1059";


    @DisplayName("Verify function new IssuanceParams(String, Address) works well with happy " +
                         "condition")
    @ParameterizedTest
    @ValueSource(strings = {
            "F5AD8D9B58E122D2D229F86EAA5D276496A5A3DA19D53C887A23F81955A3D07266B50A896D332ABC1D1845850311E50570CB56EE507B89EC18BC91EDC34C1059",
            "613AB997FB2172FD4D12FB2B82157E3CD081E2D95ADEFE96C6BE9151667E67ED72FA0CAEDD109805B6F07C3EFE428F03B836AD36EC51165F35ADB4572530E2BF",
            "C5C834EFB58A5106DD601676BF606DA91B752CABABFBE223122F71BE5B2963DC57D593F5F45B61F1ABE2FCF80277FD4075EE561813BC69C0343B4C4E68516237"})
    public void testConstructIssuanceParamsWithoutNonce_ValidAssetId_ValidInstanceIsReturn(String assetId) {
        assertDoesNotThrow(() -> new IssuanceParams(assetId, ADDRESS));
    }

    @DisplayName("Verify function new IssuanceParams(String, Address) throws exception with " +
                         "invalid assetId")
    @ParameterizedTest
    @MethodSource("createInvalidAssetId")
    public void testConstructIssuanceParamsWithoutNonce_InvalidAssetId_ErrorIsThrow(String assetId) {
        assertThrows(ValidateException.class, () -> new IssuanceParams(assetId, ADDRESS));
    }

    @DisplayName("Verify function new IssuanceParams(String, Address, int[]) works well with " +
                         "happy condition")
    @ParameterizedTest
    @MethodSource("createValidNonce")
    public void testConstructIssuanceParamsWithNonce_ValidNonce_ValidInstanceIsReturn(int[] nonce) {
        assertDoesNotThrow(() -> new IssuanceParams(ASSET_ID, ADDRESS, nonce));
    }

    @DisplayName("Verify function new IssuanceParams(String, Address, int[]) throws exception " +
                         "with invalid nonce")
    @ParameterizedTest
    @MethodSource("createInvalidNonce")
    public void testConstructIssuanceParamsWithNonce_InvalidNonce_ErrorIsThrow(int[] nonce) {
        assertThrows(ValidateException.class, () -> new IssuanceParams(
                ASSET_ID, ADDRESS, nonce));
    }

    @DisplayName("Verify function new IssuanceParams(String, Address, int) works well with happy " +
                         "condition")
    @ParameterizedTest
    @ValueSource(ints = {1, 100, 1000})
    public void testConstructIssuanceParamsWithQuantity_ValidQuantity_ValidInstanceIsReturn(int quantity) {
        assertDoesNotThrow(() -> new IssuanceParams(ASSET_ID, ADDRESS, quantity));
        final IssuanceParams params = new IssuanceParams(ASSET_ID, ADDRESS, quantity);
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
    public void testSignOneNonce_NoCondition_ValidSignatureIsReturn(String assetId, int[] nonce,
                                                                    byte[] signature) {
        final IssuanceParams params = new IssuanceParams(assetId, ADDRESS, nonce);
        params.sign(KEY);
        assertEquals(params.getSignatures().size(), 1);
        assertTrue(Arrays.equals(signature, params.getSignatures().get(0)));
    }

    @DisplayName("Verify function IssuanceParams.sign(KeyPair) in multiple params works well with" +
                         " happy condition")
    @ParameterizedTest
    @MethodSource("createValidAssetIdNonceSignatures")
    public void testSignMultipleNonce_NoCondition_ValidSignatureIsReturn(String assetId,
                                                                         int[] nonce,
                                                                         List<byte[]> expectedSignature) {
        final IssuanceParams params = new IssuanceParams(assetId, ADDRESS, nonce);
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

    private static Stream<String> createInvalidAssetId() {
        return Stream.of(null, "",
                "F5AD8D9B58E122D2D229F86EAA5D276496A5A3DA19D53C887A23F81955A3D07266B50A896D332ABC1D1845850311E50570CB56EE507B89EC18BC91EDC34C1059FF");
    }

    private static Stream<int[]> createValidNonce() {
        return Stream.of(new int[]{1, 3, 5, 7, 9},
                new int[]{2, 4, 6, 8},
                new int[]{1, 4, 7});
    }

    private static Stream<int[]> createInvalidNonce() {
        return Stream.of(new int[]{}, new int[]{1, 1, 1, 2, 3, 4, 5, 5}, new int[]{1, 2, 3, 4, 5,
                6, 7, 3}, null);
    }

    private static Stream<Arguments> createValidAssetIdNonceSignatures() {
        final List<byte[]> signatures1 = new ArrayList<byte[]>() {{
            add(HEX.decode(
                    "0C1E8A5FB7BA6CBFA68DA52354590DF0D1979E0671DC2C9BA19811A166E8C4B54433DFBFB56C39DED50D0F54BA8676B9C01DAD55F8AB1C1879BC335D635E4406"));
            add(HEX.decode(
                    "545BDABEA2EDAFF9140F8C21DA18613C827DD56BE349B7F053A08128D453D66BBE01573AC7FCE454797F4BA689DA15A4CD1EB61ADDFC3C42F11A87D9D6A27D0D"));
            add(HEX.decode(
                    "EDD0CA5311CDB8905A5709165F2F18730431237C587347F352FAB813612437648BD598588E272E68C7A5EDB29278C1E4C9D3E4BBB3FC88566CC63D5E2BC50306"));
            add(HEX.decode(
                    "43231B518617C5812307A1B084DDAB34DE71A6DB751DE23D2B359A6F3315BA7DBC518D6CAB7C07CB57EC787AC94E50AE3A7A52D135C6E49883AC5692B8AA530C"));
            add(HEX.decode(
                    "21C1107D73D6DA65CE5192BB12A93A22ED47400C76B076D4E43434728FDB369514A4956C19CE7116F6FD619C79F0AAAE2D8CDFA9C4A2779702FE334FDA4F8205"));
        }};

        final List<byte[]> signatures2 = new ArrayList<byte[]>() {{
            add(HEX.decode(
                    "5D18BB4EAF85B4712B2B462847BBEB2FE331650BBAED07D5B4B7A3A1B36397CFBD56DAD75FB56109D25F2CCB78C6773EC2193A314CF9D498EA24BFE9E7AFF205"));
            add(HEX.decode(
                    "08189DD164B429078E7DCCF0965D1B9D452074A52EDB89A6AB8DB538733B8C2AA162A27EF5C94CB3B58E7A345C4BF901D8A455AF417FF3879744E012CD53E10B"));
            add(HEX.decode(
                    "C149EE642B12DDC3EC503B985AD2D7121024DB019698D9E370B94AFB8A9EC9B6D1569E372F055F79B154DB18C01BADCFD542622E02B59431E03E869299822405"));
            add(HEX.decode(
                    "E8B4994605F3462184C490CE620BD87F7149647DD6ECA1D7EAFEC29AD58AB521D28F3B6DF4260EC3FF23F806053FAAE4C66F89B78DFF45F707ECA25077FE5C01"));
        }};
        return Stream.of(Arguments.of(
                "F5AD8D9B58E122D2D229F86EAA5D276496A5A3DA19D53C887A23F81955A3D07266B50A896D332ABC1D1845850311E50570CB56EE507B89EC18BC91EDC34C1059", new int[]{1, 2, 3, 4, 5}, signatures1),
                Arguments.of(
                        "613AB997FB2172FD4D12FB2B82157E3CD081E2D95ADEFE96C6BE9151667E67ED72FA0CAEDD109805B6F07C3EFE428F03B836AD36EC51165F35ADB4572530E2BF", new int[]{1, 3, 5, 7}, signatures2));
    }

    private static Stream<Arguments> createValidAssetIdNonceSignature() {
        return Stream.of(Arguments.of(
                "F5AD8D9B58E122D2D229F86EAA5D276496A5A3DA19D53C887A23F81955A3D07266B50A896D332ABC1D1845850311E50570CB56EE507B89EC18BC91EDC34C1059", new int[]{1},
                HEX.decode(
                        "0C1E8A5FB7BA6CBFA68DA52354590DF0D1979E0671DC2C9BA19811A166E8C4B54433DFBFB56C39DED50D0F54BA8676B9C01DAD55F8AB1C1879BC335D635E4406")),
                Arguments.of(
                        "613AB997FB2172FD4D12FB2B82157E3CD081E2D95ADEFE96C6BE9151667E67ED72FA0CAEDD109805B6F07C3EFE428F03B836AD36EC51165F35ADB4572530E2BF", new int[]{3},
                        HEX.decode(
                                "08189DD164B429078E7DCCF0965D1B9D452074A52EDB89A6AB8DB538733B8C2AA162A27EF5C94CB3B58E7A345C4BF901D8A455AF417FF3879744E012CD53E10B")),
                Arguments.of(
                        "F5AD8D9B58E122D2D229F86EAA5D276496A5A3DA19D53C887A23F81955A3D07266B50A896D332ABC1D1845850311E50570CB56EE507B89EC18BC91EDC34C1059", new int[]{5},
                        HEX.decode(
                                "21C1107D73D6DA65CE5192BB12A93A22ED47400C76B076D4E43434728FDB369514A4956C19CE7116F6FD619C79F0AAAE2D8CDFA9C4A2779702FE334FDA4F8205")));
    }

    private static Stream<Arguments> createValidIssuanceParamsJson() throws IOException {
        final IssuanceParams params1 = new IssuanceParams(ASSET_ID, ADDRESS, new int[]{1});
        final IssuanceParams params2 = new IssuanceParams(ASSET_ID, ADDRESS, new int[]{3});
        final IssuanceParams params3 = new IssuanceParams(ASSET_ID, ADDRESS, new int[]{1, 3});
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
        final IssuanceParams params1 = new IssuanceParams(ASSET_ID, ADDRESS, new int[]{1});
        final IssuanceParams params2 = new IssuanceParams(ASSET_ID, ADDRESS, new int[]{3});
        final IssuanceParams params3 = new IssuanceParams(ASSET_ID, ADDRESS, new int[]{1, 3});
        return Stream.of(params1, params2, params3);
    }

}
