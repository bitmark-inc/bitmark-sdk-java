package com.bitmark.cryptography.test.encoder;

import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.bitmark.cryptography.crypto.encoder.Base58.BASE_58;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Hieu Pham
 * @since 8/24/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Base58Test extends BaseEncoderTest {

    @ParameterizedTest
    @MethodSource("createBytesBase58String")
    public void testEncode_ValidByteArrayInput_CorrectValueIsReturn(
            byte[] input,
            String expectedResult
    ) {
        final String output = BASE_58.encode(input);
        assertTrue(expectedResult.equalsIgnoreCase(output));
    }

    @ParameterizedTest
    @MethodSource("createInvalidBytes")
    public void testEncode_InvalidByteArrayInput_ErrorIsThrow(byte[] input) {
        assertThrows(ValidateException.class, () -> BASE_58.encode(input));
    }

    @ParameterizedTest
    @MethodSource("createBase58StringBytes")
    public void testDecode_ValidStringInput_CorrectValueReturn(
            String input,
            byte[] expectedResult
    ) {
        final byte[] output = BASE_58.decode(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @ParameterizedTest
    @MethodSource("createInvalidString")
    public void testDecode_InvalidStringInput_ErrorIsThrow(String input) {
        assertThrows(ValidateException.class, () -> BASE_58.decode(input));
    }

    private static Stream<Arguments> createBytesBase58String() {
        return Stream.of(
                Arguments.of(
                        new byte[]{66, 105, 116, 109, 97, 114, 107},
                        "3WyEDmUoFG"
                ),
                Arguments.of(new byte[]{97, 65, 98, 66, 99, 67}, "qRxhjkSW"),
                Arguments.of(new byte[]{
                        66,
                        105,
                        116,
                        109,
                        97,
                        114,
                        107,
                        83,
                        68,
                        75
                }, "4jQZmYmXmMpbDt")
        );
    }

    private static Stream<Arguments> createBase58StringBytes() {
        return Stream.of(
                Arguments.of(
                        "3WyEDmUoFG",
                        new byte[]{66, 105, 116, 109, 97, 114, 107}
                ),
                Arguments.of("qRxhjkSW", new byte[]{97, 65, 98, 66, 99, 67}),
                Arguments.of("4jQZmYmXmMpbDt", new byte[]{
                        66, 105, 116, 109, 97, 114, 107, 83, 68, 75
                })
        );
    }

    private static Stream<byte[]> createInvalidBytes() {
        return Stream.of(new byte[]{}, null);
    }

    private static Stream<String> createInvalidString() {
        return Stream.of(null, "");
    }


}
