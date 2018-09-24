package com.bitmark.sdk.test.unittest.params;

import com.bitmark.sdk.crypto.key.KeyPair;
import com.bitmark.sdk.crypto.key.StandardKeyPair;
import com.bitmark.sdk.error.ValidateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import com.bitmark.sdk.service.params.TransferOfferParams;
import com.bitmark.sdk.test.BaseTest;
import com.bitmark.sdk.utils.Address;

import java.io.IOException;
import java.util.stream.Stream;

import static com.bitmark.sdk.crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;
import static com.bitmark.sdk.test.utils.FileUtils.loadRequest;

/**
 * @author Hieu Pham
 * @since 9/11/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransferOfferParamsTest extends BaseTest {

    private static final String PRIVATE_KEY =
            "0246a917d422e596168185cea9943459c09751532c52fe4ddc27b06e2893ef2258760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f";

    private static final String PUBLIC_KEY =
            "58760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f";

    private static final KeyPair KEY = StandardKeyPair.from(HEX.decode(PUBLIC_KEY),
            HEX.decode(PRIVATE_KEY));

    private static final Address ADDRESS = Address.fromAccountNumber(
            "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva");

    @DisplayName("Verify function new TransferOfferParams(Address) works well with valid owner " +
                         "address")
    @Test
    public void testConstructTransferOfferParamsWithoutLink_ValidOwnerAddress_CorrectInstanceIsReturn() {
        assertDoesNotThrow(() -> new TransferOfferParams(ADDRESS));
    }

    @DisplayName("Verify function new TransferOfferParams(Address) throws exception with invalid " +
                         "owner" +
                         " address")
    @Test
    public void testConstructTransferOfferParamsWithoutLink_InvalidOwnerAddress_ErrorIsThrow() {
        assertThrows(ValidateException.class, () -> new TransferOfferParams(null));
    }

    @DisplayName("Verify function new TransferOfferParams(Address, String) works well with valid " +
                         "params")
    @ParameterizedTest
    @MethodSource("createValidAddressLink")
    public void testConstructTransferOfferParamsWithLink_ValidParams_CorrectInstanceIsReturn(Address owner, String link) {
        assertDoesNotThrow(() -> new TransferOfferParams(owner, link));
    }

    @DisplayName("Verify function new TransferOfferParams(Address, String) throws exception with " +
                         "invalid params")
    @ParameterizedTest
    @MethodSource("createInvalidAddressLink")
    public void testConstructTransferOfferParamsWithLink_InvalidParams_ErrorIsThrow(Address owner,
                                                                                    String link) {
        assertThrows(ValidateException.class, () -> new TransferOfferParams(owner, link));
    }

    @DisplayName("Verify function TransferOfferParams.toJson() works well with " +
                         "TransferOfferParams is signed")
    @ParameterizedTest
    @MethodSource("createSignedParamsJson")
    public void testToJson_ParamsIsSigned_ValidJsonIsReturn(TransferOfferParams params,
                                                            String json) {
        assertTrue(json.equalsIgnoreCase(params.toJson()));
    }

    @DisplayName("Verify function TransferOfferParams.toJson() throw exception with " +
                         "TransferParams is " +
                         "not signed")
    @ParameterizedTest
    @MethodSource("createUnsignedParamsJson")
    public void testToJson_ParamsIsNotSigned_ErrorIsThrow(TransferOfferParams params) {
        assertThrows(UnsupportedOperationException.class, params::toJson);
    }

    @DisplayName("Verify function TransferOfferParams.sign(KeyPair) works well with valid " +
                         "TransferParams")
    @ParameterizedTest
    @MethodSource("createValidParamsSignature")
    public void testSignParams_ParamsIsValid_CorrectSignatureIsReturn(TransferOfferParams params,
                                                                      String signature) {
        params.sign(KEY);
        assertTrue(signature.equalsIgnoreCase(params.getSignature()));
    }

    @DisplayName("Verify function TransferOfferParams.sign(KeyPair) throws exception with invalid" +
                         " TransferOfferParams")
    @Test
    public void testSignParams_ParamsIsInvalid_ErrorIsThrow() {
        final TransferOfferParams params = new TransferOfferParams(ADDRESS);
        assertThrows(ValidateException.class, () -> params.sign(KEY));
    }

    private static Stream<Arguments> createValidAddressLink() {
        return Stream.of(Arguments.of(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "bfdc91b7abc9960048649857974e2ff42a76ed35c98415f52c976fb66ba92115"),
                Arguments.of(Address.fromAccountNumber(
                        "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                        "ce49d8b5095e531bd02ee4005349c845cdaf9800862cbbf40d6bea57a5319b7e"));
    }

    private static Stream<Arguments> createInvalidAddressLink() {
        return Stream.of(Arguments.of(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                ""),
                Arguments.of(Address.fromAccountNumber(
                        "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                        null), Arguments.of(null,
                        "ad22ae9c46bdf53b7b8b6f520650e1aaa84960739cd7f7461cd3a8695e24cc96"));
    }

    private static Stream<Arguments> createSignedParamsJson() throws IOException {
        final TransferOfferParams params1 = new TransferOfferParams(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "bfdc91b7abc9960048649857974e2ff42a76ed35c98415f52c976fb66ba92115");
        final TransferOfferParams params2 = new TransferOfferParams(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "ce49d8b5095e531bd02ee4005349c845cdaf9800862cbbf40d6bea57a5319b7e");
        final String json1 = loadRequest("/transfer/transfer_offer1.json");
        final String json2 = loadRequest("/transfer/transfer_offer2.json");
        params1.sign(KEY);
        params2.sign(KEY);
        return Stream.of(Arguments.of(params1, json1), Arguments.of(params2, json2));
    }

    private static Stream<TransferOfferParams> createUnsignedParamsJson() {
        final TransferOfferParams params1 = new TransferOfferParams(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "bfdc91b7abc9960048649857974e2ff42a76ed35c98415f52c976fb66ba92115");
        final TransferOfferParams params2 = new TransferOfferParams(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "ce49d8b5095e531bd02ee4005349c845cdaf9800862cbbf40d6bea57a5319b7e");
        return Stream.of(params1, params2);
    }

    private static Stream<Arguments> createValidParamsSignature() {
        final TransferOfferParams params1 = new TransferOfferParams(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "bfdc91b7abc9960048649857974e2ff42a76ed35c98415f52c976fb66ba92115");
        final TransferOfferParams params2 = new TransferOfferParams(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "ce49d8b5095e531bd02ee4005349c845cdaf9800862cbbf40d6bea57a5319b7e");
        return Stream.of(Arguments.of(params1,
                "36bfb3b55ac370b4ad1e9cce74e0120fa09deeb22b7d6bef8c485465186b5f818832c682786ee3759097ec7bdde8739596921cc0ef73c5efbb698dec70427b09"),
                Arguments.of(params2,
                        "211c33b238d552982c9e9a05a384f809370f9f95ebfdd2890278fb790aee42518a271dc15984bb05e8af36ff6ce8eb8e0e55434e70dd1e481b59f5394bd0f109"));
    }
}
