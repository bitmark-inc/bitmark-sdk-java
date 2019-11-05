/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.test.unittest.params;

import com.bitmark.apiservice.params.ShareGrantingParams;
import com.bitmark.apiservice.test.BaseTest;
import com.bitmark.apiservice.test.utils.FileUtils;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static com.bitmark.apiservice.test.unittest.DataProvider.*;
import static org.junit.jupiter.api.Assertions.*;

public class ShareGrantingParamsTest extends BaseTest {

    private static Stream<Arguments> createValidShareGrantingParamsJsonString()
            throws IOException {
        String json1 = FileUtils.loadRequest("/share/grant_share1.json");
        String json2 = FileUtils.loadRequest("/share/grant_share2.json");
        ShareGrantingParams params1 =
                new ShareGrantingParams(
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41",
                        1,
                        Address.fromAccountNumber(
                                "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"),
                        Address.fromAccountNumber(
                                "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"),
                        1460119645
                );
        params1.sign(KEY_PAIR_3);
        ShareGrantingParams params2 =
                new ShareGrantingParams(
                        "03a70acb35ef1eca10634137967ea28b8f2ca90c5aaf77dbb53ee54ab04fc8b6",
                        1,
                        Address.fromAccountNumber(
                                "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"),
                        Address.fromAccountNumber(
                                "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"),
                        28929294,
                        new LinkedHashMap<String, String>() {{
                            put("source", "HealthKit");
                            put("quantity", "15");
                        }}
                );
        params2.sign(KEY_PAIR_3);
        return Stream.of(
                Arguments.of(params1, json1),
                Arguments.of(params2, json2)
        );
    }

    @Test
    public void testConstructor__ValidInstanceOrErrorThrow() {
        assertThrows(ValidateException.class, () -> new ShareGrantingParams(
                "", 1, ADDRESS1,
                ADDRESS2, 10
        ));

        assertThrows(ValidateException.class, () -> new ShareGrantingParams(
                "123LKJ", 1, ADDRESS1,
                ADDRESS2, 10
        ));

        assertThrows(ValidateException.class, () -> new ShareGrantingParams(
                "pff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41",
                1,
                ADDRESS1,
                ADDRESS2,
                10
        ));

        assertThrows(ValidateException.class, () -> new ShareGrantingParams(
                "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41",
                0,
                ADDRESS1,
                ADDRESS2,
                10
        ));

        assertThrows(ValidateException.class, () -> new ShareGrantingParams(
                "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41",
                -1,
                ADDRESS1,
                ADDRESS2,
                10
        ));

        assertThrows(ValidateException.class, () -> new ShareGrantingParams(
                "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41",
                10,
                null,
                ADDRESS2,
                10
        ));

        assertThrows(ValidateException.class, () -> new ShareGrantingParams(
                "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41",
                10,
                ADDRESS1,
                null,
                10
        ));

        assertThrows(ValidateException.class, () -> new ShareGrantingParams(
                "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41",
                1,
                ADDRESS1,
                ADDRESS2,
                -1
        ));

        assertDoesNotThrow(() -> new ShareGrantingParams(
                "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41",
                1,
                ADDRESS1,
                ADDRESS2,
                10000
        ));

        assertDoesNotThrow(() -> new ShareGrantingParams(
                "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41",
                1,
                ADDRESS1,
                ADDRESS2,
                10000,
                null
        ));

        assertDoesNotThrow(() -> new ShareGrantingParams(
                "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41",
                1,
                ADDRESS1,
                ADDRESS2,
                10000,
                new HashMap<>()
        ));
    }

    @Test
    public void testGetter__ValidValueReturn() {
        final String shareId = "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41";
        final int quantity = 1;
        final int beforeBlock = 10000;
        ShareGrantingParams params =
                new ShareGrantingParams(
                        shareId,
                        quantity,
                        ADDRESS1,
                        ADDRESS2,
                        beforeBlock
                );

        assertEquals(shareId, params.getShareId());
        assertEquals(quantity, params.getQuantity());
        assertEquals(beforeBlock, params.getBeforeBlock());
        assertEquals(ADDRESS1.getAddress(), params.getOwner().getAddress());
        assertEquals(ADDRESS2.getAddress(), params.getReceiver().getAddress());
    }

    @Test
    public void testSignParams_ValidKey_CorrectSignatureReturn() {
        String expectedSig =
                "9276781c373c8dd1f215d9c0b730ac174b3b79f5c2fced0ed5028ee94b283b6462fd3afdaae5989f0fa587a79e7b0a2787e5c240f02bde37044d45c631a61008";
        ShareGrantingParams params =
                new ShareGrantingParams(
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41",
                        1,
                        Address.fromAccountNumber(
                                "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"),
                        Address.fromAccountNumber(
                                "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"),
                        1460119645
                );
        assertDoesNotThrow(() -> params.sign(KEY_PAIR_3));
        assertEquals(expectedSig, params.getSignature());
    }

    @Test
    public void testSignParams_InvalidKey_ErrorThrow() {
        ShareGrantingParams params =
                new ShareGrantingParams(
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41",
                        1,
                        Address.fromAccountNumber(
                                "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"),
                        Address.fromAccountNumber(
                                "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"),
                        1460119645
                );
        assertThrows(ValidateException.class, () -> params.sign(null));
    }

    @ParameterizedTest
    @MethodSource("createValidShareGrantingParamsJsonString")
    public void testToJson_ValidParams_CorrectJsonReturn(
            ShareGrantingParams params,
            String expectedJson
    ) {
        String json = params.toJson();
        assertNotNull(json);
        assertFalse(json.isEmpty());
        assertEquals(expectedJson, json);
    }

    @Test
    public void testToJson_WithoutSigning_ErrorThrow() {
        ShareGrantingParams params =
                new ShareGrantingParams(
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41",
                        1,
                        Address.fromAccountNumber(
                                "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"),
                        Address.fromAccountNumber(
                                "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"),
                        1460119645
                );
        assertThrows(UnsupportedOperationException.class, params::toJson);
    }
}
