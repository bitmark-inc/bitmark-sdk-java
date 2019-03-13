package com.bitmark.sdk.test.unittest.utils;

import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.test.unittest.BaseTest;
import com.bitmark.sdk.utils.SequenceIterateByteArray;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Hieu Pham
 * @since 8/27/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class SequenceIterateByteArrayTest extends BaseTest {

    @Test
    public void testConstructInstance_NullByteArray_ErrorIsThrow() {
        assertThrows(ValidateException.NullValueError.class,
                () -> new SequenceIterateByteArray(null));
    }

    @ParameterizedTest
    @MethodSource("createNonNullByteArrays")
    public void testConstructInstance_NonNullByteArray_CorrectInstanceCreated(byte[] input) {
        final SequenceIterateByteArray sequenceIterateByteArray =
                new SequenceIterateByteArray(input);
        assertTrue(Arrays.equals(input, sequenceIterateByteArray.getBytes()));
    }

    @ParameterizedTest
    @MethodSource("createByteArrayLengthByteArray")
    public void testNextByteArray_ValidLength_CorrectResultIsReturn(byte[] bytes, int[] lengths,
                                                                    List<byte[]> expectedBytes) {
        final SequenceIterateByteArray byteArray = new SequenceIterateByteArray(bytes);
        for (int i = 0; i < lengths.length; i++) {
            final byte[] output = byteArray.next(lengths[i]);
            assertTrue(Arrays.equals(expectedBytes.get(i), output));
        }
    }

    @ParameterizedTest
    @MethodSource("createByteArrayLength")
    public void testNextByteArray_InvalidLength_ErrorIsThrow(byte[] bytes, int length) {
        final SequenceIterateByteArray byteArray = new SequenceIterateByteArray(bytes);
        byteArray.next(bytes.length / 2); // Slice to middle of arrays to simulate current state
        assertThrows(Exception.class, () -> byteArray.next(length));
    }

    @ParameterizedTest
    @MethodSource("createByteArrayByteArray")
    public void testNextByteArray_EmptyLength_RemainingByteArrayIsReturn(byte[] bytes,
                                                                         byte[] expectedResult) {
        final SequenceIterateByteArray byteArray = new SequenceIterateByteArray(bytes);
        byteArray.next(bytes.length / 2); // Slice to middle of arrays to simulate current state
        final byte[] output = byteArray.next();
        assertTrue(Arrays.equals(output, expectedResult));
    }

    private static Stream<byte[]> createNonNullByteArrays() {
        return Stream.of(new byte[]{}, new byte[]{0x4A, 0x3B, 0x1F, 0x2A}, new byte[]{-1, 2, -3,
                56, 127, -128});
    }

    private static Stream<Arguments> createByteArrayLengthByteArray() {
        return Stream.of(Arguments.of(new byte[]{0x55, 0x7F, 0x3A, 0x5F, 0x23, 0x34, 0x17},
                new int[]{2, 3, 1},
                new ArrayList<byte[]>() {{
                    add(new byte[]{0x55, 0x7F});
                    add(new byte[]{0x3A, 0x5F, 0x23});
                    add(new byte[]{0x34});
                }}),
                Arguments.of(new byte[]{0x55, 0x7F, 0x3A, 0x5F, 0x23, 0x34, 0x17},
                        new int[]{1, 1, 5},
                        new ArrayList<byte[]>() {{
                            add(new byte[]{0x55});
                            add(new byte[]{0x7F});
                            add(new byte[]{0x3A, 0x5F, 0x23, 0x34, 0x17});
                        }}),
                Arguments.of(new byte[]{0x55, 0x7F, 0x3A, 0x5F, 0x23, 0x34, 0x17},
                        new int[]{5, 1, 1},
                        new ArrayList<byte[]>() {{
                            add(new byte[]{0x55, 0x7F, 0x3A, 0x5F, 0x23});
                            add(new byte[]{0x34});
                            add(new byte[]{0x17});
                        }}));
    }

    private static Stream<Arguments> createByteArrayLength() {
        return Stream.of(Arguments.of(new byte[]{0x55, 0x7F, 0x3A, 0x5F, 0x23, 0x34, 0x17}, 5),
                Arguments.of(new byte[]{0x55, 0x7F, 0x3A, 0x5F, 0x23, 0x34, 0x17}, -1),
                Arguments.of(new byte[]{0x55, 0x7F, 0x3A, 0x5F, 0x23, 0x34, 0x17}, 100));
    }

    private static Stream<Arguments> createByteArrayByteArray() {
        return Stream.of(Arguments.of(new byte[]{0x55, 0x7F, 0x3A, 0x5F, 0x23, 0x34, 0x17},
                new byte[]{0x5F, 0x23, 0x34, 0x17}),
                Arguments.of(new byte[]{0x12, 0x45}, new byte[]{0x45}),
                Arguments.of(new byte[]{0x12, 0x54, 0x4F, 0x5B, 0x5F, 0x2F},
                        new byte[]{0x5B, 0x5F, 0x2F}));
    }
}
