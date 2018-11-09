package com.bitmark.cryptography.test.encoder;

import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.bitmark.cryptography.crypto.encoder.Raw.RAW;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Hieu Pham
 * @since 8/24/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class RawTest extends BaseEncoderTest {

    @DisplayName("Verify function Raw.encode(byte[]) works well with happy condition")
    @ParameterizedTest
    @MethodSource("createBytesString")
    public void testEncode_ValidByteArrayInput_CorrectResultIsReturn(byte[] input,
                                                                     String expectedResult) {
        final String output = RAW.encode(input);
        assertTrue(output.equalsIgnoreCase(expectedResult));
    }

    @DisplayName("Verify function Raw.encode(byte[]) throws exception when byte array input is " +
            "invalid")
    @ParameterizedTest
    @MethodSource("createInvalidBytes")
    public void testEncode_InvalidByteArrayInput_ErrorIsThrow(byte[] input) {
        assertThrows(ValidateException.class, () -> RAW.encode(input));
    }

    @DisplayName("Verify function Raw.decode(String) works well with happy condition")
    @ParameterizedTest
    @MethodSource("createStringBytes")
    public void testDecode_ValidStringInput_CorrectResultIsReturn(String input,
                                                                  byte[] expectedResult) {
        final byte[] output = RAW.decode(input);
        assertTrue(Arrays.equals(output, expectedResult));
    }

    @DisplayName("Verify function Raw.decode(String) throws exception when string input is invalid")
    @ParameterizedTest
    @MethodSource("createInvalidString")
    public void testDecode_InvalidStringInput_ErrorIsThrow(String input) {
        assertThrows(ValidateException.class, () -> RAW.decode(input));
    }

    private static Stream<Arguments> createStringBytes() {
        return Stream.of(Arguments.of("Bitmark", new byte[]{66, 105, 116, 109, 97, 114, 107}),
                Arguments.of("aAbBcC", new byte[]{97, 65, 98, 66, 99, 67}), Arguments.of("z",
                        new byte[]{122}));
    }

    private static Stream<Arguments> createBytesString() {
        return Stream.of(Arguments.of(new byte[]{66, 105, 116, 109, 97, 114, 107}, "Bitmark"),
                Arguments.of(new byte[]{97, 65, 98, 66, 99, 67}, "aAbBcC"), Arguments.of(
                        new byte[]{122}, "z"));
    }

    private static Stream<byte[]> createInvalidBytes() {
        return Stream.of(new byte[]{}, null);
    }

    private static Stream<String> createInvalidString() {
        return Stream.of(null, "");
    }

}