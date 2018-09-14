package test.params;

import crypto.key.KeyPair;
import crypto.key.StandardKeyPair;
import error.ValidateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import service.params.TransferParams;
import test.BaseTest;
import utils.Address;

import java.io.IOException;
import java.util.stream.Stream;

import static crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;
import static util.FileUtils.loadRequest;

/**
 * @author Hieu Pham
 * @since 9/11/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransferParamsTest extends BaseTest {

    private static final String PRIVATE_KEY =
            "0246a917d422e596168185cea9943459c09751532c52fe4ddc27b06e2893ef2258760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f";

    private static final String PUBLIC_KEY =
            "58760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f";

    private static final KeyPair KEY = StandardKeyPair.from(HEX.decode(PUBLIC_KEY),
            HEX.decode(PRIVATE_KEY));

    private static final Address ADDRESS = Address.fromAccountNumber(
            "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva");

    @DisplayName("Verify function new TransferParams(Address) works well with valid owner address")
    @Test
    public void testConstructTransferParamsWithoutLink_ValidOwnerAddress_CorrectInstanceIsReturn() {
        assertDoesNotThrow(() -> new TransferParams(ADDRESS));
    }

    @DisplayName("Verify function new TransferParams(Address) throws exception with invalid owner" +
                         " address")
    @Test
    public void testConstructTransferParamsWithoutLink_InvalidOwnerAddress_ErrorIsThrow() {
        assertThrows(ValidateException.class, () -> new TransferParams(null));
    }

    @DisplayName("Verify function new TransferParams(Address, String) works well with valid params")
    @ParameterizedTest
    @MethodSource("createValidAddressLink")
    public void testConstructTransferParamsWithLink_ValidParams_CorrectInstanceIsReturn(Address owner, String link) {
        assertDoesNotThrow(() -> new TransferParams(owner, link));
    }

    @DisplayName("Verify function new TransferParams(Address, String) throws exception with " +
                         "invalid params")
    @ParameterizedTest
    @MethodSource("createInvalidAddressLink")
    public void testConstructTransferParamsWithLink_InvalidParams_ErrorIsThrow(Address owner,
                                                                               String link) {
        assertThrows(ValidateException.class, () -> new TransferParams(owner, link));
    }

    @DisplayName("Verify function TransferParams.toJson() works well with TransferParams is signed")
    @ParameterizedTest
    @MethodSource("createSignedParamsJson")
    public void testToJson_ParamsIsSigned_ValidJsonIsReturn(TransferParams params, String json) {
        assertTrue(json.equalsIgnoreCase(params.toJson()));
    }

    @DisplayName("Verify function TransferParams.toJson() throw exception with TransferParams is " +
                         "not signed")
    @ParameterizedTest
    @MethodSource("createUnsignedParamsJson")
    public void testToJson_ParamsIsNotSigned_ErrorIsThrow(TransferParams params) {
        assertThrows(UnsupportedOperationException.class, params::toJson);
    }

    @DisplayName("Verify function TransferParams.sign(KeyPair) works well with valid " +
                         "TransferParams")
    @ParameterizedTest
    @MethodSource("createValidParamsSignature")
    public void testSignParams_ParamsIsValid_CorrectSignatureIsReturn(TransferParams params,
                                                                      String signature) {
        params.sign(KEY);
        assertTrue(signature.equalsIgnoreCase(params.getSignature()));
    }

    @DisplayName("Verify function TransferParams.sign(KeyPair) throws exception with invalid " +
                         "TransferParams")
    @Test
    public void testSignParams_ParamsIsInvalid_ErrorIsThrow() {
        final TransferParams params = new TransferParams(ADDRESS);
        assertThrows(ValidateException.class, () -> params.sign(KEY));
    }

    private static Stream<Arguments> createValidAddressLink() {
        return Stream.of(Arguments.of(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "20df48ef393aa1f62d9ea6ff496fb4b5c9029eefc1dc888d8df7b03c1794ee82"),
                Arguments.of(Address.fromAccountNumber(
                        "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                        "ad22ae9c46bdf53b7b8b6f520650e1aaa84960739cd7f7461cd3a8695e24cc96"));
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
        final TransferParams params1 = new TransferParams(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "20df48ef393aa1f62d9ea6ff496fb4b5c9029eefc1dc888d8df7b03c1794ee82");
        final TransferParams params2 = new TransferParams(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "ad22ae9c46bdf53b7b8b6f520650e1aaa84960739cd7f7461cd3a8695e24cc96");
        final String json1 = loadRequest("/transfer/transfer1.json");
        final String json2 = loadRequest("/transfer/transfer2.json");
        params1.sign(KEY);
        params2.sign(KEY);
        return Stream.of(Arguments.of(params1, json1), Arguments.of(params2, json2));
    }

    private static Stream<TransferParams> createUnsignedParamsJson() {
        final TransferParams params1 = new TransferParams(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "20df48ef393aa1f62d9ea6ff496fb4b5c9029eefc1dc888d8df7b03c1794ee82");
        final TransferParams params2 = new TransferParams(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "ad22ae9c46bdf53b7b8b6f520650e1aaa84960739cd7f7461cd3a8695e24cc96");
        return Stream.of(params1, params2);
    }

    private static Stream<Arguments> createValidParamsSignature() {
        final TransferParams params1 = new TransferParams(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "20df48ef393aa1f62d9ea6ff496fb4b5c9029eefc1dc888d8df7b03c1794ee82");
        final TransferParams params2 = new TransferParams(Address.fromAccountNumber(
                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX"),
                "ad22ae9c46bdf53b7b8b6f520650e1aaa84960739cd7f7461cd3a8695e24cc96");
        return Stream.of(Arguments.of(params1,
                "b51ac89fced27e3b124b8f5ee4cf3410ef59e77a76d3a664fc5a202d0231de40500d051b0712b8fd6c509dfae1b3d7cb1c6faa0011e04c427b36768446c42f0f"),
                Arguments.of(params2,
                        "4d7fba1a2dcc24e113253244a45e705137ff8e75a682bd883abd354ee6b382b49f1fb2d399581d6b0714f2fcf3576ba657bd01701ece4f02e680d6812da4d500"));
    }

}
