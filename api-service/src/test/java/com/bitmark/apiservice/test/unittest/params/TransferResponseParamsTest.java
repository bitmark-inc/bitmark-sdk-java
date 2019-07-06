package com.bitmark.apiservice.test.unittest.params;

import com.bitmark.apiservice.params.TransferResponseParams;
import com.bitmark.apiservice.test.BaseTest;
import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.record.OfferRecord;
import com.bitmark.cryptography.crypto.key.Ed25519KeyPair;
import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.bitmark.apiservice.test.unittest.DataProvider.KEY_PAIR_2;
import static com.bitmark.apiservice.test.utils.FileUtils.loadRequest;
import static com.bitmark.apiservice.test.utils.TestUtils.reflectionSet;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hieu Pham
 * @since 9/11/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransferResponseParamsTest extends BaseTest {

    @ParameterizedTest
    @MethodSource("createValidOffer")
    public void testConstructTransferResponseParams_ValidOfferRecord_CorrectInstanceIsReturn(
            OfferRecord offer) {
        assertDoesNotThrow(() -> TransferResponseParams.accept(offer));
        assertDoesNotThrow(() -> TransferResponseParams.reject(offer));
        assertDoesNotThrow(() -> TransferResponseParams.cancel(offer,
                                                               "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"));
    }

    @Test
    public void testConstructAcceptTransferResponseParams_InvalidOfferRecord_ErrorIsThrow() {

        assertThrows(ValidateException.class, () -> TransferResponseParams.accept(null));
    }

    @Test
    public void testConstructRejectTransferResponseParams_InvalidOfferRecord_ErrorIsThrow() {

        assertThrows(ValidateException.class, () -> TransferResponseParams.reject(null));
    }

    @ParameterizedTest
    @MethodSource("createInvalidOfferRecordOwner")
    public void testConstructCancelTransferResponseParams_InvalidParams_ErrorIsThrow(
            OfferRecord offerRecord, String owner) {

        assertThrows(ValidateException.class, () -> TransferResponseParams.cancel(offerRecord,
                                                                                  owner));
    }

    @ParameterizedTest
    @MethodSource("createSignedAcceptParamsJson")
    public void testToJsonWithAcceptParams_ParamsIsSigned_CorrectJsonIsReturn(
            TransferResponseParams params, String json) {
        assertTrue(json.equalsIgnoreCase(params.toJson()));
    }

    @ParameterizedTest
    @MethodSource("createUnsignedAcceptParams")
    public void testToJsonWithoutAcceptParams_ParamsIsNotSigned_ErrorIsThrow(
            TransferResponseParams params) {
        assertThrows(UnsupportedOperationException.class, params::toJson);
    }

    @ParameterizedTest
    @MethodSource("createUnsignedNotAcceptParamsJson")
    public void testToJsonWithoutAcceptParams_ParamsIsNotSigned_CorrectJsonIsReturn(
            TransferResponseParams params, String json) {
        assertTrue(json.equalsIgnoreCase(params.toJson()));
    }

    @ParameterizedTest
    @MethodSource("createAcceptParamsSignature")
    public void testSignWithAcceptParams_ValidCondition_CorrectSignatureIsReturn(
            TransferResponseParams params, String signature) {
        params.sign(KEY_PAIR_2);
        assertTrue(signature.equalsIgnoreCase(params.getSignature()));
    }

    @ParameterizedTest
    @MethodSource("createUnsignedNonAcceptParams")
    public void testSignWithoutAcceptParams_NoCondition_ErrorIsThrow(
            TransferResponseParams params) {
        assertThrows(ValidateException.class, () -> params.sign(KEY_PAIR_2));
    }

    @ParameterizedTest
    @MethodSource("createValidParamsHeader")
    public void testBuildRequestHeader_ValidCondition_CorrectHeaderIsReturn(
            TransferResponseParams params, long time, Map<String, String> header) {
        assertTrue(header.toString().equalsIgnoreCase(params.buildHeaders(time).toString()));
    }

    @ParameterizedTest
    @MethodSource("createMissingSigningKeyParams")
    public void testBuildRequestHeader_MissingSigningKey_ErrorIsThrow(
            TransferResponseParams params) {
        assertThrows(ValidateException.class, params::buildHeaders);
    }

    private static Stream<OfferRecord> createValidOffer()
            throws NoSuchFieldException, IllegalAccessException {
        final OfferRecord offer1 = new OfferRecord();
        final OfferRecord.Record record1 = new OfferRecord.Record();
        reflectionSet(record1,
                      new Pair<>("owner", "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"),
                      new Pair<>("link",
                                 "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa"),
                      new Pair<>("signature",
                                 "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f"));
        reflectionSet(offer1, new Pair<>("id", "c9b9911a4-4237-419b-9862-00957d7c4618"),
                      new Pair<>("record", record1));

        final OfferRecord offer2 = new OfferRecord();
        final OfferRecord.Record record2 = new OfferRecord.Record();
        reflectionSet(record2,
                      new Pair<>("owner", "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"),
                      new Pair<>("link",
                                 "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296"),
                      new Pair<>("signature",
                                 "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d"));
        reflectionSet(offer2, new Pair<>("id", "39de1800-ecec-4dea-8425-d2a87e468ace"),
                      new Pair<>("record", record2));

        return Stream.of(offer1, offer2);
    }

    private static Stream<Arguments> createSignedAcceptParamsJson()
            throws IOException, NoSuchFieldException, IllegalAccessException {
        final OfferRecord offer1 = new OfferRecord();
        final OfferRecord.Record record1 = new OfferRecord.Record();
        reflectionSet(record1,
                      new Pair<>("owner", "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                      new Pair<>("link",
                                 "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa"),
                      new Pair<>("signature",
                                 "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f"));
        reflectionSet(offer1, new Pair<>("id", "9b9911a4-4237-419b-9862-00957d7c4618"),
                      new Pair<>("record", record1));

        final OfferRecord offer2 = new OfferRecord();
        final OfferRecord.Record record2 = new OfferRecord.Record();
        reflectionSet(record2,
                      new Pair<>("owner", "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                      new Pair<>("link",
                                 "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296"),
                      new Pair<>("signature",
                                 "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d"));
        reflectionSet(offer2, new Pair<>("id", "196b12ee-770d-48b3-8402-32c72810c297"),
                      new Pair<>("record", record2));
        final TransferResponseParams params1 = TransferResponseParams.accept(offer1);
        final TransferResponseParams params2 = TransferResponseParams.accept(offer2);
        params1.sign(KEY_PAIR_2);
        params2.sign(KEY_PAIR_2);
        final String json1 = loadRequest("/transfer/transfer_response1.json");
        final String json2 = loadRequest("/transfer/transfer_response2.json");
        return Stream.of(Arguments.of(params1, json1), Arguments.of(params2, json2));
    }

    private static Stream<TransferResponseParams> createUnsignedAcceptParams()
            throws NoSuchFieldException, IllegalAccessException {
        final OfferRecord offer1 = new OfferRecord();
        final OfferRecord.Record record1 = new OfferRecord.Record();
        reflectionSet(record1,
                      new Pair<>("owner", "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"),
                      new Pair<>("link",
                                 "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa"),
                      new Pair<>("signature",
                                 "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f"));
        reflectionSet(offer1, new Pair<>("id", "9b9911a4-4237-419b-9862-00957d7c4618"),
                      new Pair<>("record", record1));

        final OfferRecord offer2 = new OfferRecord();
        final OfferRecord.Record record2 = new OfferRecord.Record();
        reflectionSet(record2,
                      new Pair<>("owner", "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                      new Pair<>("link",
                                 "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296"),
                      new Pair<>("signature",
                                 "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d"));
        reflectionSet(offer2, new Pair<>("id", "196b12ee-770d-48b3-8402-32c72810c297"),
                      new Pair<>("record", record2));
        final TransferResponseParams params1 = TransferResponseParams.accept(offer1);
        final TransferResponseParams params2 = TransferResponseParams.accept(offer2);
        return Stream.of(params1, params2);
    }

    private static Stream<Arguments> createUnsignedNotAcceptParamsJson()
            throws IOException, NoSuchFieldException, IllegalAccessException {
        final OfferRecord offer1 = new OfferRecord();
        final OfferRecord.Record record1 = new OfferRecord.Record();
        reflectionSet(record1,
                      new Pair<>("owner", "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"),
                      new Pair<>("link",
                                 "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa"),
                      new Pair<>("signature",
                                 "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f"));
        reflectionSet(offer1, new Pair<>("id", "9b9911a4-4237-419b-9862-00957d7c4618"),
                      new Pair<>("record", record1));

        final OfferRecord offer2 = new OfferRecord();
        final OfferRecord.Record record2 = new OfferRecord.Record();
        reflectionSet(record2,
                      new Pair<>("owner", "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                      new Pair<>("link",
                                 "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296"),
                      new Pair<>("signature",
                                 "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d"));
        reflectionSet(offer2, new Pair<>("id", "196b12ee-770d-48b3-8402-32c72810c297"),
                      new Pair<>("record", record2));
        final TransferResponseParams params1 = TransferResponseParams.cancel(offer1,
                                                                             "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX");
        final TransferResponseParams params2 = TransferResponseParams.reject(offer2);
        final String json1 = loadRequest("/transfer/transfer_response3.json");
        final String json2 = loadRequest("/transfer/transfer_response4.json");
        return Stream.of(Arguments.of(params1, json1), Arguments.of(params2, json2));
    }

    private static Stream<Arguments> createAcceptParamsSignature()
            throws NoSuchFieldException, IllegalAccessException {
        final OfferRecord offer1 = new OfferRecord();
        final OfferRecord.Record record1 = new OfferRecord.Record();
        reflectionSet(record1,
                      new Pair<>("owner", "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                      new Pair<>("link",
                                 "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa"),
                      new Pair<>("signature",
                                 "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f"));
        reflectionSet(offer1, new Pair<>("id", "9b9911a4-4237-419b-9862-00957d7c4618"),
                      new Pair<>("record", record1));

        final OfferRecord offer2 = new OfferRecord();
        final OfferRecord.Record record2 = new OfferRecord.Record();
        reflectionSet(record2,
                      new Pair<>("owner", "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                      new Pair<>("link",
                                 "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296"),
                      new Pair<>("signature",
                                 "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d"));
        reflectionSet(offer2, new Pair<>("id", "196b12ee-770d-48b3-8402-32c72810c297"),
                      new Pair<>("record", record2));
        final TransferResponseParams params1 = TransferResponseParams.accept(offer1);
        final TransferResponseParams params2 = TransferResponseParams.accept(offer2);
        return Stream.of(Arguments.of(params1,
                                      "fc107b1f0922ba645b0c728a9071cd2591e95ddb4197834145c5e0a685b9e72510e81c7ca8f4ea50ffa8aa2ef6f55001d3f6685599ece4d00ffe5a336ed29505"),
                         Arguments.of(params2,
                                      "7b4ca691ca494c4fe71142231fd839658d8aa2962496e2d416703e1da43e8e2b8a1df68761545be2f44d2375bfd2f0844169f64693488dff1056e95a82600105"));
    }

    private static Stream<Arguments> createValidParamsHeader()
            throws NoSuchFieldException, IllegalAccessException {
        final OfferRecord offer1 = new OfferRecord();
        final OfferRecord.Record record1 = new OfferRecord.Record();
        reflectionSet(record1,
                      new Pair<>("owner", "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                      new Pair<>("link",
                                 "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa"),
                      new Pair<>("signature",
                                 "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f"));
        reflectionSet(offer1, new Pair<>("id", "9b9911a4-4237-419b-9862-00957d7c4618"),
                      new Pair<>("record", record1));

        final OfferRecord offer2 = new OfferRecord();
        final OfferRecord.Record record2 = new OfferRecord.Record();
        reflectionSet(record2,
                      new Pair<>("owner", "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                      new Pair<>("link",
                                 "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296"),
                      new Pair<>("signature",
                                 "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d"));
        reflectionSet(offer2, new Pair<>("id", "196b12ee-770d-48b3-8402-32c72810c297"),
                      new Pair<>("record", record2));

        final OfferRecord offer3 = new OfferRecord();
        final OfferRecord.Record record3 = new OfferRecord.Record();
        reflectionSet(record3,
                      new Pair<>("owner", "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"),
                      new Pair<>("link",
                                 "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296"),
                      new Pair<>("signature",
                                 "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d"));
        reflectionSet(offer3, new Pair<>("id", "39de1800-ecec-4dea-8425-d2a87e468ace"),
                      new Pair<>("record", record3));

        final TransferResponseParams params1 = TransferResponseParams.accept(offer1);
        final TransferResponseParams params2 = TransferResponseParams.accept(offer2);
        final TransferResponseParams params3 = TransferResponseParams.cancel(offer3,
                                                                             "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva");
        params3.setSigningKey(Ed25519KeyPair.from(HEX.decode(
                "58760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f"), HEX.decode(
                "0246a917d422e596168185cea9943459c09751532c52fe4ddc27b06e2893ef2258760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f")));
        params1.sign(KEY_PAIR_2);
        params2.sign(KEY_PAIR_2);
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
        final Map<String, String> header3 = new HashMap<String, String>() {{
            put("requester", "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva");
            put("timestamp", String.valueOf(1536742584049L));
            put("signature",
                "317e75e03a7ce727a7833d682558a7a2cc4ef4df00ff50d3b95861c0bafba396135ad7a94bf4818ebc0d029e7c1e8adad127d28c8d127d5ef4733b7713d1440c");
        }};
        return Stream.of(Arguments.of(params1, 1536742580935L, header1), Arguments.of(params2,
                                                                                      1536742638942L,
                                                                                      header2),
                         Arguments.of(params3, 1536742584049L, header3));
    }

    private static Stream<TransferResponseParams> createUnsignedNonAcceptParams()
            throws NoSuchFieldException, IllegalAccessException {
        final OfferRecord offer1 = new OfferRecord();
        final OfferRecord.Record record1 = new OfferRecord.Record();
        reflectionSet(record1,
                      new Pair<>("owner", "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"),
                      new Pair<>("link",
                                 "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa"),
                      new Pair<>("signature",
                                 "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f"));
        reflectionSet(offer1, new Pair<>("id", "9b9911a4-4237-419b-9862-00957d7c4618"),
                      new Pair<>("record", record1));

        final OfferRecord offer2 = new OfferRecord();
        final OfferRecord.Record record2 = new OfferRecord.Record();
        reflectionSet(record2,
                      new Pair<>("owner", "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                      new Pair<>("link",
                                 "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296"),
                      new Pair<>("signature",
                                 "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d"));
        reflectionSet(offer2, new Pair<>("id", "196b12ee-770d-48b3-8402-32c72810c297"),
                      new Pair<>("record", record2));
        final TransferResponseParams params1 = TransferResponseParams.cancel(offer1,
                                                                             "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX");
        final TransferResponseParams params2 = TransferResponseParams.reject(offer2);
        return Stream.of(params1, params2);
    }

    private static Stream<TransferResponseParams> createMissingSigningKeyParams()
            throws NoSuchFieldException, IllegalAccessException {
        final OfferRecord offer1 = new OfferRecord();
        final OfferRecord.Record record1 = new OfferRecord.Record();
        reflectionSet(record1,
                      new Pair<>("owner", "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"),
                      new Pair<>("link",
                                 "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa"),
                      new Pair<>("signature",
                                 "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f"));
        reflectionSet(offer1, new Pair<>("id", "9b9911a4-4237-419b-9862-00957d7c4618"),
                      new Pair<>("record", record1));

        final OfferRecord offer2 = new OfferRecord();
        final OfferRecord.Record record2 = new OfferRecord.Record();
        reflectionSet(record2,
                      new Pair<>("owner", "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                      new Pair<>("link",
                                 "49e28a044a18d183d9a135745f4f01abd0a50e9cac0bb1d8e3de5f0e92713296"),
                      new Pair<>("signature",
                                 "f57a6edcf23f274a5d94d99b68dde2b9c20f167f269b4fc01e6486619f894cbf10913a96e77077c1c76508dd97cc36063496ed1c99684e1f92cbba4fd50cdc0d"));
        reflectionSet(offer2, new Pair<>("id", "196b12ee-770d-48b3-8402-32c72810c297"),
                      new Pair<>("record", record2));
        final TransferResponseParams params1 = TransferResponseParams.cancel(offer1,
                                                                             "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX");
        final TransferResponseParams params2 = TransferResponseParams.reject(offer2);
        return Stream.of(params1, params2);
    }

    private static Stream<Arguments> createInvalidOfferRecordOwner()
            throws NoSuchFieldException, IllegalAccessException {
        final OfferRecord offerRecord = new OfferRecord();
        final OfferRecord.Record record1 = new OfferRecord.Record();
        reflectionSet(record1,
                      new Pair<>("owner", "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"),
                      new Pair<>("link",
                                 "c1faf958d6247e4b398db1de1b454ea756eda3b840e10feaf3a37232f4c8b1fa"),
                      new Pair<>("signature",
                                 "4a669194150be01382fd204eded4df01341c1fd2b045fedadb1aee68c3fe68dc1d99bea13c4185af20ad83042ef6bb06ecf20678842cfa40eb879938eb024f0f"));
        reflectionSet(offerRecord, new Pair<>("id", "9b9911a4-4237-419b-9862-00957d7c4618"),
                      new Pair<>("record", record1));
        return Stream.of(Arguments.of(null, ""), Arguments.of(offerRecord, ""),
                         Arguments.of(offerRecord, null));
    }

}
