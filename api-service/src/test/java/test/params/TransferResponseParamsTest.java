package test.params;

import crypto.key.KeyPair;
import crypto.key.StandardKeyPair;
import error.ValidateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import service.params.TransferResponseParams;
import test.BaseTest;
import utils.Address;
import utils.record.TransferOffer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;
import static service.params.TransferResponseParams.Response.*;
import static util.FileUtils.loadRequest;

/**
 * @author Hieu Pham
 * @since 9/11/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransferResponseParamsTest extends BaseTest {

    private static final String PRIVATE_KEY =
            "02732AC92FD70EA402393F28163C3B45F5DA6F9D61DCF1254E0F2CEC805FE737807F4D123C944E0C3ECC95D9BDE89916CED6341A8C8CEDEB8CAAFEF8F35654E7";

    private static final String PUBLIC_KEY =
            "807F4D123C944E0C3ECC95D9BDE89916CED6341A8C8CEDEB8CAAFEF8F35654E7";

    private static final KeyPair KEY = StandardKeyPair.from(HEX.decode(PUBLIC_KEY),
            HEX.decode(PRIVATE_KEY));

    @DisplayName("Verify function new TransferResponseParams(TransferOffer, Response) " +
                         "works well with valid params")
    @ParameterizedTest
    @MethodSource("createValidTransferOfferResponse")
    public void testConstructTransferResponseParams_ValidParams_CorrectInstanceIsReturn(
            TransferOffer transferOffer, TransferResponseParams.Response response) {
        assertDoesNotThrow(() -> new TransferResponseParams(transferOffer, response));
    }

    @DisplayName("Verify function new TransferResponseParams(TransferOffer, Response) " +
                         "throws exception with invalid params")
    @ParameterizedTest
    @MethodSource("createInvalidTransferOfferResponse")
    public void testConstructTransferResponseParams_InvalidParams_ErrorIsThrow(TransferOffer transferOffer, TransferResponseParams.Response response) {

        assertThrows(ValidateException.class, () -> new TransferResponseParams(transferOffer,
                response));
    }

    @DisplayName("Verify function TransferResponseParams.toJson() works well with signed accept " +
                         "TransferResponseParams")
    @ParameterizedTest
    @MethodSource("createSignedAcceptParamsJson")
    public void testToJsonWithAcceptParams_ParamsIsSigned_CorrectJsonIsReturn(TransferResponseParams params, String json) {
        assertTrue(json.equalsIgnoreCase(params.toJson()));
    }

    @DisplayName("Verify function TransferResponseParams.toJson() throws error with unsigned " +
                         "accept TransferResponseParams")
    @ParameterizedTest
    @MethodSource("createUnsignedAcceptParams")
    public void testToJsonWithoutAcceptParams_ParamsIsNotSigned_ErrorIsThrow(TransferResponseParams params) {
        assertThrows(UnsupportedOperationException.class, params::toJson);
    }

    @DisplayName("Verify function TransferResponseParams.toJson() works well with un-accept " +
                         "response")
    @ParameterizedTest
    @MethodSource("createUnsignedNotAcceptParamsJson")
    public void testToJsonWithoutAcceptParams_ParamsIsNotSigned_CorrectJsonIsReturn(TransferResponseParams params, String json) {
        assertTrue(json.equalsIgnoreCase(params.toJson()));
    }

    @DisplayName("Verify function TransferResponseParams.sign(KeyPair) works well with accept " +
                         "response")
    @ParameterizedTest
    @MethodSource("createAcceptParamsSignature")
    public void testSignWithAcceptParams_ValidCondition_CorrectSignatureIsReturn(TransferResponseParams params, String signature) {
        params.sign(KEY);
        assertTrue(signature.equalsIgnoreCase(params.getSignature()));
    }

    @DisplayName("Verify function TransferResponseParams.sign(KeyPair) throw exception with " +
                         "non-accept response")
    @ParameterizedTest
    @MethodSource("createUnsignedNonAcceptParams")
    public void testSignWithoutAcceptParams_NoCondition_ErrorIsThrow(TransferResponseParams params) {
        assertThrows(ValidateException.class, () -> params.sign(KEY));
    }

    @DisplayName("Verify function TransferResponseParams.buildHeaders() works well")
    @ParameterizedTest
    @MethodSource("createValidParamsHeader")
    public void testBuildRequestHeader_ValidCondition_CorrectHeaderIsReturn(TransferResponseParams params, long time, Map<String, String> header) {
        assertTrue(header.toString().equalsIgnoreCase(params.buildHeaders(time).toString()));
    }

    @DisplayName("Verify function TransferResponseParams.buildHeaders() throws exception if the " +
                         "TransferResponseParams is not signed")
    @ParameterizedTest
    @MethodSource("createUnsignedNonAcceptParams")
    public void testBuildRequestHeader_ParamsIsNotSigned_ErrorIsThrow(TransferResponseParams params) {
        assertThrows(UnsupportedOperationException.class, params::buildHeaders);
    }

    private static Stream<Arguments> createValidTransferOfferResponse() {
        final TransferOffer transferOffer1 = new TransferOffer("c9b9911a4-4237-419b-9862" +
                "-00957d7c4618", Address.fromAccountNumber(
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"),
                "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa",
                "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f");
        final TransferOffer transferOffer2 = new TransferOffer("39de1800-ecec-4dea-8425" +
                "-d2a87e468ace", Address.fromAccountNumber(
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"),
                "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296",
                "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d");
        return Stream.of(Arguments.of(transferOffer1, ACCEPT), Arguments.of(transferOffer2,
                CANCEL));
    }

    private static Stream<Arguments> createInvalidTransferOfferResponse() {
        final TransferOffer transferOffer = new TransferOffer("39de1800-ecec-4dea-8425" +
                "-d2a87e468ace", Address.fromAccountNumber(
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"),
                "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296",
                "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d");
        return Stream.of(Arguments.of(null, null), Arguments.of(transferOffer, null),
                Arguments.of(null, ACCEPT));
    }

    private static Stream<Arguments> createSignedAcceptParamsJson() throws IOException {
        final TransferOffer transferOffer1 = new TransferOffer("9b9911a4-4237-419b-9862" +
                "-00957d7c4618", Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa",
                "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f");
        final TransferOffer transferOffer2 = new TransferOffer("196b12ee-770d-48b3-8402" +
                "-32c72810c297", Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296",
                "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d");
        final TransferResponseParams params1 = new TransferResponseParams(transferOffer1, ACCEPT);
        final TransferResponseParams params2 = new TransferResponseParams(transferOffer2, ACCEPT);
        params1.sign(KEY);
        params2.sign(KEY);
        final String json1 = loadRequest("/transfer/transfer_response1.json");
        final String json2 = loadRequest("/transfer/transfer_response2.json");
        return Stream.of(Arguments.of(params1, json1), Arguments.of(params2, json2));
    }

    private static Stream<TransferResponseParams> createUnsignedAcceptParams() {
        final TransferOffer transferOffer1 = new TransferOffer("9b9911a4-4237-419b-9862" +
                "-00957d7c4618", Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa",
                "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f");
        final TransferOffer transferOffer2 = new TransferOffer("196b12ee-770d-48b3-8402" +
                "-32c72810c297", Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296",
                "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d");
        final TransferResponseParams params1 = new TransferResponseParams(transferOffer1, ACCEPT);
        final TransferResponseParams params2 = new TransferResponseParams(transferOffer2, ACCEPT);
        return Stream.of(params1, params2);
    }

    private static Stream<Arguments> createUnsignedNotAcceptParamsJson() throws IOException {
        final TransferOffer transferOffer1 = new TransferOffer("9b9911a4-4237-419b-9862" +
                "-00957d7c4618", Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa",
                "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f");
        final TransferOffer transferOffer2 = new TransferOffer("196b12ee-770d-48b3-8402" +
                "-32c72810c297", Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296",
                "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d");
        final TransferResponseParams params1 = new TransferResponseParams(transferOffer1, CANCEL);
        final TransferResponseParams params2 = new TransferResponseParams(transferOffer2, REJECT);
        final String json1 = loadRequest("/transfer/transfer_response3.json");
        final String json2 = loadRequest("/transfer/transfer_response4.json");
        return Stream.of(Arguments.of(params1, json1), Arguments.of(params2, json2));
    }

    private static Stream<Arguments> createAcceptParamsSignature() {
        final TransferOffer transferOffer1 = new TransferOffer("9b9911a4-4237-419b-9862" +
                "-00957d7c4618", Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa",
                "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f");
        final TransferOffer transferOffer2 = new TransferOffer("196b12ee-770d-48b3-8402" +
                "-32c72810c297", Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296",
                "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d");
        final TransferResponseParams params1 = new TransferResponseParams(transferOffer1, ACCEPT);
        final TransferResponseParams params2 = new TransferResponseParams(transferOffer2, ACCEPT);
        return Stream.of(Arguments.of(params1,
                "fc107b1f0922ba645b0c728a9071cd2591e95ddb4197834145c5e0a685b9e72510e81c7ca8f4ea50ffa8aa2ef6f55001d3f6685599ece4d00ffe5a336ed29505"),
                Arguments.of(params2,
                        "7b4ca691ca494c4fe71142231fd839658d8aa2962496e2d416703e1da43e8e2b8a1df68761545be2f44d2375bfd2f0844169f64693488dff1056e95a82600105"));
    }

    private static Stream<TransferResponseParams> createUnsignedNonAcceptParams() {
        final TransferOffer transferOffer1 = new TransferOffer("9b9911a4-4237-419b-9862" +
                "-00957d7c4618", Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa",
                "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f");
        final TransferOffer transferOffer2 = new TransferOffer("196b12ee-770d-48b3-8402" +
                "-32c72810c297", Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296",
                "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d");
        final TransferResponseParams params1 = new TransferResponseParams(transferOffer1, CANCEL);
        final TransferResponseParams params2 = new TransferResponseParams(transferOffer2, REJECT);
        return Stream.of(params1, params2);
    }

    private static Stream<Arguments> createValidParamsHeader() {
        final TransferOffer transferOffer1 = new TransferOffer("9b9911a4-4237-419b-9862" +
                "-00957d7c4618", Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa",
                "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f");
        final TransferOffer transferOffer2 = new TransferOffer("196b12ee-770d-48b3-8402" +
                "-32c72810c297", Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296",
                "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d");
        final TransferResponseParams params1 = new TransferResponseParams(transferOffer1, ACCEPT);
        final TransferResponseParams params2 = new TransferResponseParams(transferOffer2, ACCEPT);
        params1.sign(KEY);
        params2.sign(KEY);
        final Map<String, String> header1 = new HashMap<String, String>() {{
            put("requester", "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX");
            put("timestamp", String.valueOf(1536742580935L));
            put("signature",
                    "2d373bf2321bd2fc94ee4a2154b8162e2a7189f053878ff8fe3c032de07104eeaf980abfbe0ad61799cbd8c9759487e6bfcd00c3ab7e1ba2bde468a8e7b4a702");
        }};

        final Map<String, String> header2 = new HashMap<String, String>() {{
            put("requester", "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX");
            put("timestamp", String.valueOf(1536742638942L));
            put("signature",
                    "6417788791887e313c7397455a6711436a7ac08a0530c522169acdd82973653b6a55f1f370c23885a26cd8b86721304ad8e657b06e4a5356a82796be228cf50c");
        }};
        return Stream.of(Arguments.of(params1, 1536742580935L, header1), Arguments.of(params2,
                1536742638942L, header2));
    }

}
