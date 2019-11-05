/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.test.unittest.params;

import com.bitmark.apiservice.params.GrantResponseParams;
import com.bitmark.apiservice.test.BaseTest;
import com.bitmark.apiservice.test.utils.FileUtils;
import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.record.ShareGrantRecord;
import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static com.bitmark.apiservice.test.unittest.DataProvider.KEY_PAIR_4;
import static com.bitmark.apiservice.test.utils.TestUtils.reflectionSet;
import static org.junit.jupiter.api.Assertions.*;

public class GrantResponseParamsTest extends BaseTest {

    @Test
    public void testConstructor__ValidInstanceOrErrorThrow() {

        assertThrows(
                ValidateException.class,
                () -> GrantResponseParams.accept(new ShareGrantRecord())
        );
        assertThrows(
                ValidateException.class,
                () -> GrantResponseParams.reject(new ShareGrantRecord())
        );
        assertThrows(
                ValidateException.class,
                () -> GrantResponseParams.cancel(new ShareGrantRecord())
        );

        assertThrows(
                ValidateException.class,
                () -> GrantResponseParams.accept(null)
        );
        assertThrows(
                ValidateException.class,
                () -> GrantResponseParams.reject(null)
        );
        assertThrows(
                ValidateException.class,
                () -> GrantResponseParams.cancel(null)
        );

        assertDoesNotThrow(() -> GrantResponseParams.accept(
                createValidShareGrantRecord1()));
        assertDoesNotThrow(() -> GrantResponseParams.reject(
                createValidShareGrantRecord1()));
        assertDoesNotThrow(() -> GrantResponseParams.cancel(
                createValidShareGrantRecord1()));
    }

    @Test
    public void testGetter__ValidValueReturn()
            throws NoSuchFieldException, IllegalAccessException {
        ShareGrantRecord record = createValidShareGrantRecord1();
        GrantResponseParams params = GrantResponseParams.accept(record);
        assertEquals(record, params.getShareGrantRecord());
    }

    @Test
    public void testSignParams_ValidKey_CorrectSignatureReturn()
            throws NoSuchFieldException, IllegalAccessException {
        String expectedSig =
                "126a494fe2438547324783f50b9826585815f23a461cce6c4feffd28ead7182eed040e3b5007378b85a190869f52c086028b0f6a5855c83c5603106d3eb89a04";
        ShareGrantRecord record = createValidShareGrantRecord2();
        GrantResponseParams params = GrantResponseParams.accept(record);
        assertDoesNotThrow(() -> params.sign(KEY_PAIR_4));
        assertEquals(expectedSig, params.getSignature());
    }

    @Test
    public void testSignParams_InvalidKey_ErrorThrow()
            throws NoSuchFieldException, IllegalAccessException {
        ShareGrantRecord record = createValidShareGrantRecord2();
        GrantResponseParams params = GrantResponseParams.accept(record);
        assertThrows(ValidateException.class, () -> params.sign(null));
    }

    @ParameterizedTest
    @MethodSource("createValidGrantResponseParamsJsonString")
    public void testToJson_ValidParams_CorrectJsonReturn(
            GrantResponseParams params,
            String expectedJson
    ) {
        String json = params.toJson();
        assertNotNull(json);
        assertFalse(json.isEmpty());
        assertEquals(expectedJson, json);
    }

    @Test
    public void testToJson_WithoutSigning_CorrectValidation() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> GrantResponseParams.accept(createValidShareGrantRecord1())
                        .toJson()
        );
        assertDoesNotThrow(
                () -> GrantResponseParams.cancel(createValidShareGrantRecord1())
                        .toJson());
        assertDoesNotThrow(
                () -> GrantResponseParams.reject(createValidShareGrantRecord1())
                        .toJson());
    }

    private static Stream<Arguments> createValidGrantResponseParamsJsonString()
            throws IOException, NoSuchFieldException, IllegalAccessException {
        String json1 = FileUtils.loadRequest("/share/grant_response_share1.json");
        String json2 = FileUtils.loadRequest("/share/grant_response_share2.json");
        GrantResponseParams params1 = GrantResponseParams.accept(
                createValidShareGrantRecord2());
        params1.sign(KEY_PAIR_4);
        GrantResponseParams params2 = GrantResponseParams.cancel(
                createValidShareGrantRecord1());
        params2.sign(KEY_PAIR_4);
        return Stream.of(
                Arguments.of(params1, json1),
                Arguments.of(params2, json2)
        );
    }

    private static ShareGrantRecord createValidShareGrantRecord1()
            throws NoSuchFieldException, IllegalAccessException {
        ShareGrantRecord.Record record = new ShareGrantRecord.Record();
        reflectionSet(record, new Pair<>("quantity", 1), new Pair<>(
                        "shareId",
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41"
                ),
                new Pair<>(
                        "owner",
                        "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"
                ),
                new Pair<>(
                        "receiver",
                        "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"
                ),
                new Pair<>("beforeBlock", 150212348),
                new Pair<>(
                        "signature",
                        "63d0492088db46296f8fc220234be211a678dd6de576350250f79c716d9a5c920b4bbd3f1ce1c112365fa496f41e63fddd4899abfa9c1bf94a572e052ba50a0f"
                )
        );

        ShareGrantRecord shareGrantRecord = new ShareGrantRecord();
        reflectionSet(shareGrantRecord,
                new Pair<>("id", "73781d92-6352-4b79-a404-cb8e88f96a85"),
                new Pair<>(
                        "shareId",
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41"
                ),
                new Pair<>(
                        "from",
                        "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"
                ),
                new Pair<>(
                        "to",
                        "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"
                ),
                new Pair<>("record", record),
                new Pair<>("createdAt", "2019-03-11T15:35:23.456511Z"),
                new Pair<>("status", "open")
        );
        return shareGrantRecord;
    }

    private static ShareGrantRecord createValidShareGrantRecord2()
            throws NoSuchFieldException, IllegalAccessException {
        ShareGrantRecord.Record record = new ShareGrantRecord.Record();
        reflectionSet(record, new Pair<>("quantity", 1), new Pair<>(
                        "shareId",
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41"
                ),
                new Pair<>(
                        "owner",
                        "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"
                ),
                new Pair<>(
                        "receiver",
                        "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"
                ),
                new Pair<>("beforeBlock", 989824143),
                new Pair<>(
                        "signature",
                        "1efe017096bf5d159e4fb734f39f5684534e21a355623ddcf6a2f2de27f48698dc755af1c9715f544178c14cbbb360343fbd69b4f86b3c3e0a8c890b7fcb170e"
                )
        );

        ShareGrantRecord shareGrantRecord = new ShareGrantRecord();
        reflectionSet(shareGrantRecord,
                new Pair<>("id", "59e079b6-0534-4bc5-ae14-7ce965724562"),
                new Pair<>(
                        "shareId",
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41"
                ),
                new Pair<>(
                        "from",
                        "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"
                ),
                new Pair<>(
                        "to",
                        "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"
                ),
                new Pair<>("record", record),
                new Pair<>("createdAt", "2019-03-11T15:35:23.011111Z"),
                new Pair<>("status", "open")
        );
        return shareGrantRecord;
    }
}
