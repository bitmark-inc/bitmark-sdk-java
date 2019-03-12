package com.bitmark.apiservice.test.unittest.params;

import com.bitmark.apiservice.params.ShareParams;
import com.bitmark.apiservice.test.BaseTest;
import com.bitmark.apiservice.test.utils.FileUtils;
import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static com.bitmark.apiservice.test.unittest.DataProvider.KEY_PAIR_3;
import static com.bitmark.apiservice.test.unittest.DataProvider.TX_ID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hieu Pham
 * @since 3/12/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class ShareParamsTest extends BaseTest {

    @Test
    public void testConstructor__ValidInstanceOrErrorThrow() {

        assertThrows(ValidateException.class, () -> new ShareParams(-1, TX_ID));
        assertThrows(ValidateException.class, () -> new ShareParams(0, TX_ID));
        assertThrows(ValidateException.class, () -> new ShareParams(1000, ""));
        assertThrows(ValidateException.class, () -> new ShareParams(1000,
                                                                    "d97cb558f58fcaa70d1a654e45c21545096077f2de4adfeb0bf8f239b97115aZ"));
        assertDoesNotThrow(() -> new ShareParams(100, TX_ID));
    }

    @Test
    public void testGetter__ValidValueReturn() {
        int quantity = 10;
        String link = TX_ID;
        ShareParams params = new ShareParams(quantity, link);
        assertEquals(quantity, params.getQuantity());
        assertEquals(link, params.getLink());
    }

    @Test
    public void testSignParams_ValidKey_CorrectSignatureReturn() {
        final String expectedSig =
                "8e53c07581e8b61012ec9b37fb6acb1c3a84d903a050a79f4cf0090d833dc498677dc2832183aca56b85ec04bc97e0e60847cd19bc6d27cdc8b37da724856102";
        ShareParams params = new ShareParams(100, TX_ID);
        params.sign(KEY_PAIR_3);
        assertNotNull(params.getSignature());
        assertFalse(params.getSignature().isEmpty());
        assertEquals(expectedSig, params.getSignature());
    }

    @Test
    public void testSignParams_InvalidKey_ErrorThrow() {
        ShareParams params = new ShareParams(100, TX_ID);
        assertThrows(ValidateException.class, () -> params.sign(null));
    }

    @ParameterizedTest
    @MethodSource("createValidShareParamsJsonString")
    public void testToJson_ValidParams_CorrectJsonReturn(ShareParams params, String expectedJson) {
        String json = params.toJson();
        assertNotNull(json);
        assertFalse(json.isEmpty());
        assertEquals(expectedJson, json);
    }

    @Test
    public void testToJson_WithoutSigning_ErrorThrow() {
        ShareParams params = new ShareParams(100, TX_ID);
        assertThrows(UnsupportedOperationException.class, params::toJson);
    }

    private static Stream<Arguments> createValidShareParamsJsonString() throws IOException {
        String json1 = FileUtils.loadRequest("/share/create_share1.json");
        String json2 = FileUtils.loadRequest("/share/create_share2.json");
        ShareParams params1 = new ShareParams(100,
                                              "03a70acb35ef1eca10634137967ea28b8f2ca90c5aaf77dbb53ee54ab04fc8b6");
        params1.sign(KEY_PAIR_3);
        ShareParams params2 = new ShareParams(100,
                                              "d6e16ac271e3bdce061092056729da0401362fe796a563b76c75fcbf6c79fd05");
        params2.sign(KEY_PAIR_3);
        return Stream.of(Arguments.of(params1, json1), Arguments.of(params2, json2));
    }
}
