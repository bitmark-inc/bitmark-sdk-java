package com.bitmark.cryptography.test.encoder;

import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Hieu Pham
 * @since 8/24/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class HexTest extends BaseEncoderTest {

    @DisplayName("Verify function Hex.encode(byte[]) works well with happy condition")
    @ParameterizedTest
    @MethodSource("createBytesHex")
    public void testEncode_ValidByteArrayInput_CorrectHexIsReturn(byte[] input,
                                                                  String expectedHex) {
        String output = HEX.encode(input);
        assertEquals(output, expectedHex);
    }

    @DisplayName("Verify function Hex.encode(byte[]) throws exception when byte array input is " +
            "null or empty")
    @ParameterizedTest
    @MethodSource("createInvalidBytes")
    public void testEncode_InvalidByteArrayInput_ErrorIsThrow(byte[] input) {
        assertThrows(ValidateException.class, () -> HEX.encode(input));
    }

    @DisplayName("Verify function Hex.decode(String) works well with happy condition")
    @ParameterizedTest
    @MethodSource("createHexBytes")
    public void testDecode_ValidHexString_CorrectByteArrayReturn(String hex, byte[] expectedBytes) {
        final byte[] output = HEX.decode(hex);
        assertTrue(Arrays.equals(output, expectedBytes));
    }

    @DisplayName("Verify function Hex.decode(String) throws exception when the hex string is " +
            "invalid")
    @ParameterizedTest
    @ValueSource(strings = {"@!#$@23ASAfFHFT", "1233453,./123//34.", " "})
    public void testDecode_HexIsInvalid_ErrorIsThrow(String hex) {
        assertThrows(ValidateException.InvalidHex.class, () -> HEX.decode(hex));
    }

    private static Stream<Arguments> createBytesHex() {
        return Stream.of(Arguments.of(new byte[]{1, 15, 13, 38, 47, 51, 0, 73, 80},
                "010f0d262f33004950"),
                Arguments.of(new byte[]{13, 33, 50, 7, 120}, "0d21320778"),
                Arguments.of(new byte[]{2, 4, 6, 8, 10}, "020406080a"));
    }

    private static Stream<byte[]> createInvalidBytes() {
        return Stream.of(null, new byte[]{});
    }

    private static Stream<Arguments> createHexBytes() {
        return Stream.of(Arguments.of("10f0d262f33004950", new byte[]{1, 15, 13, 38, 47, 51, 0, 73, 80}),
                Arguments.of("0d21320778", new byte[]{13, 33, 50, 7, 120}),
                Arguments.of("020406080a", new byte[]{2, 4, 6, 8, 10}));
    }

}
