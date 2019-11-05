/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.test.encoder;

import com.bitmark.cryptography.crypto.encoder.VarInt;
import com.bitmark.cryptography.test.crypto.BaseCryptoTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VarIntTest extends BaseCryptoTest {


    @ParameterizedTest
    @MethodSource("createSignedIntByteArray")
    public void testWriteSignedVarInt_ValidInteger_CorrectResultIsReturn(
            int input,
            byte[] expectedResult
    ) {
        final byte[] output = VarInt.writeSignedVarInt(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @ParameterizedTest
    @MethodSource("createUnsignedIntByteArray")
    public void testWriteUnsignedVarInt_ValidInteger_CorrectResultIsReturn(
            int input,
            byte[] expectedResult
    ) {
        final byte[] output = VarInt.writeUnsignedVarInt(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @ParameterizedTest
    @MethodSource("createByteArraySignedInt")
    public void testReadSignedVarInt_ValidByteArray_CorrectResultIsReturn(
            byte[] input,
            int expectedResult
    ) {
        final int output = VarInt.readSignedVarInt(input);
        assertEquals(output, expectedResult);
    }

    @ParameterizedTest
    @MethodSource("createByteArrayUnsignedInt")
    public void testReadUnsignedVarInt_ValidByteArray_CorrectResultIsReturn(
            byte[] input,
            int expectedResult
    ) {
        final int output = VarInt.readUnsignedVarInt(input);
        assertEquals(output, expectedResult);
    }

    private static Stream<Arguments> createSignedIntByteArray() {
        return Stream.of(
                Arguments.of(-2147483648, new byte[]{-1, -1, -1, -1, 15}),
                Arguments.of(-127, new byte[]{-3, 1}),
                Arguments.of(1, new byte[]{2})
        );
    }

    private static Stream<Arguments> createByteArraySignedInt() {
        return Stream.of(
                Arguments.of(new byte[]{-1, -1, -1, -1, 15}, -2147483648),
                Arguments.of(new byte[]{-3, 1}, -127),
                Arguments.of(new byte[]{2}, 1)
        );
    }

    private static Stream<Arguments> createUnsignedIntByteArray() {
        return Stream.of(Arguments.of(300, new byte[]{-84, 2}),
                Arguments.of(1, new byte[]{1}),
                Arguments.of(65535, new byte[]{-1, -1, 3})
        );
    }

    private static Stream<Arguments> createByteArrayUnsignedInt() {
        return Stream.of(Arguments.of(new byte[]{-84, 2}, 300),
                Arguments.of(new byte[]{1}, 1),
                Arguments.of(new byte[]{-1, -1, 3}, 65535)
        );
    }
}
