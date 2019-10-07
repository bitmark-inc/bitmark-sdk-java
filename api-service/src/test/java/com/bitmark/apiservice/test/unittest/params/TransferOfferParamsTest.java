package com.bitmark.apiservice.test.unittest.params;

import com.bitmark.apiservice.params.TransferOfferParams;
import com.bitmark.apiservice.test.BaseTest;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static com.bitmark.apiservice.test.unittest.DataProvider.ADDRESS1;
import static com.bitmark.apiservice.test.unittest.DataProvider.KEY_PAIR_1;
import static com.bitmark.apiservice.test.utils.FileUtils.loadRequest;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hieu Pham
 * @since 9/11/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransferOfferParamsTest extends BaseTest {

    private static Stream<Arguments> createValidAddressLink() {
        return Stream.of(
                Arguments.of(
                        Address.fromAccountNumber(
                                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                        "bfdc91b7abc9960048649857974e2ff42a76ed35c98415f52c976fb66ba92115"
                ),
                Arguments.of(
                        Address.fromAccountNumber(
                                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                        "ce49d8b5095e531bd02ee4005349c845cdaf9800862cbbf40d6bea57a5319b7e"
                )
        );
    }

    private static Stream<Arguments> createInvalidAddressLink() {
        return Stream.of(Arguments.of(
                Address.fromAccountNumber(
                        "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                ""
                ),
                Arguments.of(
                        Address.fromAccountNumber(
                                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                        null
                ), Arguments.of(
                        null,
                        "ad22ae9c46bdf53b7b8b6f520650e1aaa84960739cd7f7461cd3a8695e24cc96"
                )
        );
    }

    private static Stream<Arguments> createSignedParamsJson()
            throws IOException {
        final TransferOfferParams params1 = new TransferOfferParams(
                Address.fromAccountNumber(
                        "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "bfdc91b7abc9960048649857974e2ff42a76ed35c98415f52c976fb66ba92115"
        );
        final TransferOfferParams params2 = new TransferOfferParams(
                Address.fromAccountNumber(
                        "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "ce49d8b5095e531bd02ee4005349c845cdaf9800862cbbf40d6bea57a5319b7e"
        );
        final String json1 = loadRequest("/transfer/transfer_offer1.json");
        final String json2 = loadRequest("/transfer/transfer_offer2.json");
        params1.sign(KEY_PAIR_1);
        params2.sign(KEY_PAIR_1);
        return Stream.of(
                Arguments.of(params1, json1),
                Arguments.of(params2, json2)
        );
    }

    private static Stream<TransferOfferParams> createUnsignedParamsJson() {
        final TransferOfferParams params1 = new TransferOfferParams(
                Address.fromAccountNumber(
                        "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "bfdc91b7abc9960048649857974e2ff42a76ed35c98415f52c976fb66ba92115"
        );
        final TransferOfferParams params2 = new TransferOfferParams(
                Address.fromAccountNumber(
                        "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "ce49d8b5095e531bd02ee4005349c845cdaf9800862cbbf40d6bea57a5319b7e"
        );
        return Stream.of(params1, params2);
    }

    private static Stream<Arguments> createValidParamsSignature() {
        final TransferOfferParams params1 = new TransferOfferParams(
                Address.fromAccountNumber(
                        "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "bfdc91b7abc9960048649857974e2ff42a76ed35c98415f52c976fb66ba92115"
        );
        final TransferOfferParams params2 = new TransferOfferParams(
                Address.fromAccountNumber(
                        "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "ce49d8b5095e531bd02ee4005349c845cdaf9800862cbbf40d6bea57a5319b7e"
        );
        return Stream.of(
                Arguments.of(
                        params1,
                        "36bfb3b55ac370b4ad1e9cce74e0120fa09deeb22b7d6bef8c485465186b5f818832c682786ee3759097ec7bdde8739596921cc0ef73c5efbb698dec70427b09"
                ),
                Arguments.of(
                        params2,
                        "211c33b238d552982c9e9a05a384f809370f9f95ebfdd2890278fb790aee42518a271dc15984bb05e8af36ff6ce8eb8e0e55434e70dd1e481b59f5394bd0f109"
                )
        );
    }

    @Test
    public void testConstructTransferOfferParamsWithoutLink_ValidOwnerAddress_CorrectInstanceIsReturn() {
        assertDoesNotThrow(() -> new TransferOfferParams(ADDRESS1));
    }

    @Test
    public void testConstructTransferOfferParamsWithoutLink_InvalidOwnerAddress_ErrorIsThrow() {
        assertThrows(
                ValidateException.class,
                () -> new TransferOfferParams(null)
        );
    }

    @ParameterizedTest
    @MethodSource("createValidAddressLink")
    public void testConstructTransferOfferParamsWithLink_ValidParams_CorrectInstanceIsReturn(
            Address owner,
            String link
    ) {
        assertDoesNotThrow(() -> new TransferOfferParams(owner, link));
    }

    @ParameterizedTest
    @MethodSource("createInvalidAddressLink")
    public void testConstructTransferOfferParamsWithLink_InvalidParams_ErrorIsThrow(
            Address owner,
            String link
    ) {
        assertThrows(
                ValidateException.class,
                () -> new TransferOfferParams(owner, link)
        );
    }

    @ParameterizedTest
    @MethodSource("createSignedParamsJson")
    public void testToJson_ParamsIsSigned_ValidJsonIsReturn(
            TransferOfferParams params,
            String json
    ) {
        assertTrue(json.equalsIgnoreCase(params.toJson()));
    }

    @ParameterizedTest
    @MethodSource("createUnsignedParamsJson")
    public void testToJson_ParamsIsNotSigned_ErrorIsThrow(TransferOfferParams params) {
        assertThrows(UnsupportedOperationException.class, params::toJson);
    }

    @ParameterizedTest
    @MethodSource("createValidParamsSignature")
    public void testSignParams_ParamsIsValid_CorrectSignatureIsReturn(
            TransferOfferParams params,
            String signature
    ) {
        params.sign(KEY_PAIR_1);
        assertTrue(signature.equalsIgnoreCase(params.getSignature()));
    }

    @Test
    public void testSignParams_ParamsIsInvalid_ErrorIsThrow() {
        final TransferOfferParams params = new TransferOfferParams(ADDRESS1);
        assertThrows(ValidateException.class, () -> params.sign(KEY_PAIR_1));
    }
}
