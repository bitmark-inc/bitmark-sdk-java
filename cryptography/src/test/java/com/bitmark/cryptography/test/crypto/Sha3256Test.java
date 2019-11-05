/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.test.crypto;

import com.bitmark.cryptography.crypto.Sha3256;
import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.crypto.encoder.Raw.RAW;
import static org.junit.jupiter.api.Assertions.*;

public class Sha3256Test extends BaseCryptoTest {

    @ParameterizedTest
    @CsvSource({
                       "BitmarkSDK, 148d5efdce00d2dc0caa14da6fc0bf3c9456dcbd2c680d86dd57be112b7edcce",
                       "JavaSDK, ec2a665796a2d8be8f4da565f57fe528c1d689764d8c56f8b31266acef15c404",
                       "BitmarkInTheFuture, 11d77428b1ec48eeac27b5263e66838665e357631cf97bad5eb7ea07840d1aaf"
               })
    public void testHashByteArrayOnce_NoError_ReturnCorrectHash(
            String inputString,
            String hexExpectedResult
    ) {
        byte[] input = RAW.decode(inputString);
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3256.hash(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @ParameterizedTest
    @CsvSource({
                       "4269746d61726b53444b, 148d5efdce00d2dc0caa14da6fc0bf3c9456dcbd2c680d86dd57be112b7edcce",
                       "4a61766153444b, ec2a665796a2d8be8f4da565f57fe528c1d689764d8c56f8b31266acef15c404",
                       "4269746d61726b496e546865467574757265, 11d77428b1ec48eeac27b5263e66838665e357631cf97bad5eb7ea07840d1aaf"
               })
    public void testHashHexStringOnce_NoError_ReturnCorrectHash(
            String input,
            String hexExpectedResult
    ) {
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3256.hash(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "4269746d61726b53444bhjka",
            "ndrggh64756dfasdvc",
            "@#$%sdfvsdf@#@#"
    })
    public void testHashHexStringOnce_InvalidHex_ErrorIsThrow(String hexInput) {
        assertThrows(
                ValidateException.InvalidHex.class,
                () -> Sha3256.hash(hexInput),
                ValidateException.InvalidHex.ORIGIN_MESSAGE
        );
    }

    @ParameterizedTest
    @CsvSource({
                       "BitmarkSDK, 844aa9b0a09c37829b2bcad976daec427a0c4a29c2ddf9cb959e9a3f8f14f9e0, S",
                       "JavaSDK, dd11a00aef092f4ad50319b613809b182712ebdd8e0549885c418b44a186565f, a",
                       "BitmarkInTheFuture, 0245c3724e0b0de137b30cfd8ce18750963123a4c685f32928d22feb371acc25, I"
               })
    public void testHashByteArrayOnceCustomOffsetAndLength_NoError_ReturnCorrectHash(
            String inputStr,
            String expectedHex,
            char startChar
    ) {
        byte[] input = RAW.decode(inputStr);
        int length = RAW.decode(inputStr.substring(inputStr.indexOf(startChar))).length;
        byte[] expectedResult = HEX.decode(expectedHex);
        byte[] output = Sha3256.hash(
                input,
                inputStr.indexOf(startChar),
                length
        );
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @ParameterizedTest
    @CsvSource({
                       "BitmarkSDK, -1, 1",
                       "JavaSDK, -5, 1",
                       "BitmarkInTheFuture, 10, -1"
               })
    public void testHashByteArrayOnceCustomOffsetAndLength_InvalidOffsetOrLength_ErrorIsThrow(
            String inputStr,
            int offset,
            int length
    ) {
        byte[] input = RAW.decode(inputStr);
        assertThrows(ValidateException.class, () -> Sha3256.hash(input, offset,
                length
        ));
    }

    @ParameterizedTest
    @CsvSource({
                       "BitmarkSDK, 334f57730e81985ff6b46cfb17a9c94b9c444f9b391aed7f4e190dc4fcde2492",
                       "JavaSDK, ea53a602c61da880f9d49d9f508cb34d52001b3c72542d61c80779006f2865cb",
                       "BitmarkInTheFuture, 84228e5c37cce0cd4b4b985b3e7b43d0727ca0f3894fd9d8111b20f6d7efdada"
               })
    public void testHashByteArrayTwice_NoError_ReturnCorrectHash(
            String inputString,
            String hexExpectedResult
    ) {
        byte[] input = RAW.decode(inputString);
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3256.hashTwice(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @ParameterizedTest
    @CsvSource({
                       "4269746d61726b53444b, 334f57730e81985ff6b46cfb17a9c94b9c444f9b391aed7f4e190dc4fcde2492",
                       "4a61766153444b, ea53a602c61da880f9d49d9f508cb34d52001b3c72542d61c80779006f2865cb",
                       "4269746d61726b496e546865467574757265, 84228e5c37cce0cd4b4b985b3e7b43d0727ca0f3894fd9d8111b20f6d7efdada"
               })
    public void testHashHexStringTwice_NoError_ReturnCorrectHash(
            String hexInput,
            String hexExpectedResult
    ) {
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3256.hashTwice(hexInput);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ABCVDF345236GRH",
            "!@#$#$%$%^$%",
            "VAFEBS#@GSDG$%"
    })
    public void testHashHexStringTwice_InvalidHex_ErrorIsThrow(String hexInput) {
        assertThrows(
                ValidateException.InvalidHex.class,
                () -> Sha3256.hashTwice(hexInput),
                ValidateException.InvalidHex.ORIGIN_MESSAGE
        );
    }

    @ParameterizedTest
    @CsvSource({
                       "BitmarkSDK, 417980e19c8d52d18a5eb8a6ce7d1f98719793e9d323f7b178efe90ab5509268, S",
                       "JavaSDK, c3bd2fc78a886dd6519dda25756c132aec82c3d43480d58e0b3302a27a83d252, a",
                       "BitmarkInTheFuture, bd4e0b4b5a4e68b6f0861520f84e423970df4b6fd98e0f4137372dd5cb770f48, I"
               })
    public void testHashByteArrayTwiceCustomOffsetAndLength_NoError_ReturnCorrectHash(
            String inputStr,
            String expectedHex,
            char startChar
    ) {
        byte[] input = RAW.decode(inputStr);
        int length = RAW.decode(inputStr.substring(inputStr.indexOf(startChar))).length;
        byte[] expectedResult = HEX.decode(expectedHex);
        byte[] output = Sha3256.hashTwice(
                input,
                inputStr.indexOf(startChar),
                length
        );
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @ParameterizedTest
    @CsvSource({
                       "BitmarkSDK, -1, 1",
                       "JavaSDK, -5, 1",
                       "BitmarkInTheFuture, 10, -1"
               })
    public void testHashByteArrayTwiceCustomOffsetAndLength_InvalidOffsetOrLength_ErrorIsThrow(
            String inputHex,
            int offset,
            int length
    ) {
        byte[] input = RAW.decode(inputHex);
        assertThrows(
                ValidateException.class,
                () -> Sha3256.hashTwice(input, offset,
                        length
                )
        );
    }

    @ParameterizedTest
    @CsvSource({
                       "1ae34783a48e95b70af442d2bd89e91cdbe4fb8a469b7002561a38af7f27a1f8, 1ae34783a48e95b70af442d2bd89e91cdbe4fb8a469b7002561a38af7f27a1f8",
                       "2c599996cf7a436e62d7a3992008f70712643fa2f1bdddbf1ced940fe5e6942a, 2c599996cf7a436e62d7a3992008f70712643fa2f1bdddbf1ced940fe5e6942a",
                       "acc2bfa9cd7c5ea4f704eb35e46a6814148d00eba10675118792e0d2858af2dd, acc2bfa9cd7c5ea4f704eb35e46a6814148d00eba10675118792e0d2858af2dd"
               })
    public void testCompareEqualHashes_NoError_ReturnExactResult(
            String firstHex,
            String secondHex
    ) {
        Sha3256 first = Sha3256.from(firstHex);
        Sha3256 second = Sha3256.from(secondHex);
        assertEquals(0, first.compareTo(second));
    }

    @ParameterizedTest
    @CsvSource({
                       "1ae34783a48e95b70af442d2bd89e91cdbe4fb8a469b7002561a38af7f27a1f8, 1ad34783a48e95b70af442d2bd89e91cdbe4fb8a469b7002561a38af7f27a1f8",
                       "2c599996cf7a436e62d7a3992008f70712643fa2f1bdddbf1ced940fe5e6942a, 2c589996cf7a436e62d7a3992008f70712643fa2f1bdddbf1ced940fe5e6942a",
                       "acc2bfa9cd7c5ea4f704eb35e46a6814148d00eba10675118792e0d2858af2dd, acc1afa9cd7c5ea4f704eb35e46a6814148d00eba10675118792e0d2858af2dd"
               })
    public void testCompareNotEqualHashes_NoError_ReturnExactResult(
            String firstHex,
            String secondHex
    ) {
        Sha3256 first = Sha3256.from(firstHex);
        Sha3256 second = Sha3256.from(secondHex);
        assertEquals(1, first.compareTo(second));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "abc2434534", "abcdef123445672324"})
    public void testConstructInstanceFromHexString_InvalidHashLength_ErrorIsThrow(
            String hexHash
    ) {
        assertThrows(
                ValidateException.InvalidLength.class,
                () -> Sha3256.from(hexHash)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "abc2434534", "abcdef123445672324"})
    public void testConstructInstanceFromByteArray_InvalidHashLength_ErrorIsThrow(
            String hexHash
    ) {
        assertThrows(
                ValidateException.InvalidLength.class,
                () -> Sha3256.from(HEX.decode(hexHash))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1ae34783a48e95b70af442d2bd89e91cdbe4fb8a469b7002561a38af7f27a1f8",
            "2c599996cf7a436e62d7a3992008f70712643fa2f1bdddbf1ced940fe5e6942a",
            "acc2bfa9cd7c5ea4f704eb35e46a6814148d00eba10675118792e0d2858af2dd"
    })
    public void testConstructInstanceFromByteArray_ValidHash_ErrorIsNotThrow(
            String hexHash
    ) {
        assertDoesNotThrow(() -> Sha3256.hash(hexHash));
    }
}
