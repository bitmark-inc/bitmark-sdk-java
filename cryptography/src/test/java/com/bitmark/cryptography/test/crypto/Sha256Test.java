package com.bitmark.cryptography.test.crypto;

import com.bitmark.cryptography.crypto.Sha256;
import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.crypto.encoder.Raw.RAW;
import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Hieu Pham
 * @since 8/24/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Sha256Test extends BaseCryptoTest {

    @ParameterizedTest
    @CsvSource({
                       "BitmarkSDK, f2b15c7200965a7e6827f80749657a3fe655980c1f03f2ecb475cfcf31f6ca31",
                       "JavaSDK, d9b7a41748db4da24f7dadc0d0a8c87e6cd1c2c754239e5009ff06fe163d6e34",
                       "BitmarkInTheFuture, 731ea25f9b40fe5ff88c8a35061ecc35e8f9958098c97e9eb523cda97485277b"
               })
    public void testHashByteArrayOnce_NoError_ReturnCorrectHash(
            String inputString,
            String hexExpectedResult
    ) {
        byte[] input = RAW.decode(inputString);
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha256.hash(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @ParameterizedTest
    @CsvSource({
                       "4269746d61726b53444b, f2b15c7200965a7e6827f80749657a3fe655980c1f03f2ecb475cfcf31f6ca31",
                       "4a61766153444b, d9b7a41748db4da24f7dadc0d0a8c87e6cd1c2c754239e5009ff06fe163d6e34",
                       "4269746d61726b496e546865467574757265, 731ea25f9b40fe5ff88c8a35061ecc35e8f9958098c97e9eb523cda97485277b"
               })
    public void testHashHexStringOnce_NoError_ReturnCorrectHash(
            String input,
            String hexExpectedResult
    ) {
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha256.hash(input);
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
                () -> Sha256.hash(hexInput),
                ValidateException.InvalidHex.ORIGIN_MESSAGE
        );
    }

    @ParameterizedTest
    @CsvSource({
                       "BitmarkSDK, 95ed80a9188ddc42b9200a97c50ce39a0910e982a3cc86ae4362f42702c603b4, S",
                       "JavaSDK, c3cf3dab5458f5b59f5b703d42a9268e7186154a771adc787a13934fdd2e5c85, a",
                       "BitmarkInTheFuture, fcfa353ed0cce560bd25a39340f61144d83a69cf1c9e6c1f220726d3a782c7e0, I"
               })
    public void testHashByteArrayOnceCustomOffsetAndLength_NoError_ReturnCorrectHash(
            String inputStr,
            String expectedHex,
            char startChar
    ) {
        byte[] input = RAW.decode(inputStr);
        int length = RAW.decode(inputStr.substring(inputStr.indexOf(startChar))).length;
        byte[] expectedResult = HEX.decode(expectedHex);
        byte[] output = Sha256.hash(input, inputStr.indexOf(startChar), length);
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
        assertThrows(
                ValidateException.class,
                () -> Sha256.hash(input, offset, length)
        );
    }

    @ParameterizedTest
    @CsvSource({
                       "BitmarkSDK, fe29fb2a23dafb456aa5bb04c91b8ae7ca1a8d5326ce37536c402a55727bf741",
                       "JavaSDK, 64ea0fc63ba71319c44050abf270ddfb07d2ed7e073212afe5408ef51eb8f5ce",
                       "BitmarkInTheFuture, 33988ac5511629aee8ff15886496577f9c4ef7e52e85d6a27bf495316f560d3b"
               })
    public void testHashByteArrayTwice_NoError_ReturnCorrectHash(
            String inputString,
            String hexExpectedResult
    ) {
        byte[] input = RAW.decode(inputString);
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha256.hashTwice(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @ParameterizedTest
    @CsvSource({
                       "4269746d61726b53444b, fe29fb2a23dafb456aa5bb04c91b8ae7ca1a8d5326ce37536c402a55727bf741",
                       "4a61766153444b, 64ea0fc63ba71319c44050abf270ddfb07d2ed7e073212afe5408ef51eb8f5ce",
                       "4269746d61726b496e546865467574757265, 33988ac5511629aee8ff15886496577f9c4ef7e52e85d6a27bf495316f560d3b"
               })
    public void testHashHexStringTwice_NoError_ReturnCorrectHash(
            String hexInput,
            String hexExpectedResult
    ) {
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha256.hashTwice(hexInput);
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
                () -> Sha256.hashTwice(hexInput),
                ValidateException.InvalidHex.ORIGIN_MESSAGE
        );
    }

    @ParameterizedTest
    @CsvSource({
                       "BitmarkSDK, 1ae34783a48e95b70af442d2bd89e91cdbe4fb8a469b7002561a38af7f27a1f8, S",
                       "JavaSDK, 2c599996cf7a436e62d7a3992008f70712643fa2f1bdddbf1ced940fe5e6942a, a",
                       "BitmarkInTheFuture, acc2bfa9cd7c5ea4f704eb35e46a6814148d00eba10675118792e0d2858af2dd, I"
               })
    public void testHashByteArrayTwiceCustomOffsetAndLength_NoError_ReturnCorrectHash(
            String inputStr,
            String expectedHex,
            char startChar
    ) {
        byte[] input = RAW.decode(inputStr);
        int length = RAW.decode(inputStr.substring(inputStr.indexOf(startChar))).length;
        byte[] expectedResult = HEX.decode(expectedHex);
        byte[] output = Sha256.hashTwice(
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
                () -> Sha256.hashTwice(input, offset, length)
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
        Sha256 first = Sha256.from(firstHex);
        Sha256 second = Sha256.from(secondHex);
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
        Sha256 first = Sha256.from(firstHex);
        Sha256 second = Sha256.from(secondHex);
        assertEquals(1, first.compareTo(second));
    }

    @ParameterizedTest
    @ValueSource(strings = {"AB", "ABC2434534", "ABCDEF123445672324"})
    public void testConstructInstanceFromHexString_InvalidHashLength_ErrorIsThrow(
            String hexHash
    ) {
        assertThrows(
                ValidateException.InvalidLength.class,
                () -> Sha256.from(hexHash)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"AB", "ABC2434534", "ABCDEF123445672324"})
    public void testConstructInstanceFromByteArray_InvalidHashLength_ErrorIsThrow(
            String hexHash
    ) {
        assertThrows(
                ValidateException.InvalidLength.class,
                () -> Sha256.from(HEX.decode(hexHash))
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
        assertDoesNotThrow(() -> Sha256.hash(hexHash));
    }

}
