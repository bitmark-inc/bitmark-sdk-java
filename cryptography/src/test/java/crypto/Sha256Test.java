package crypto;

import error.ValidateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static crypto.encoder.Hex.HEX;
import static crypto.encoder.Raw.RAW;
import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Hieu Pham
 * @since 8/24/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Sha256Test extends BaseCryptoTest {

    @DisplayName("Verify the function Sha256.hash(byte[]) works correctly with happy condition")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, F2B15C7200965A7E6827F80749657A3FE655980C1F03F2ECB475CFCF31F6CA31",
            "JavaSDK, D9B7A41748DB4DA24F7DADC0D0A8C87E6CD1C2C754239E5009FF06FE163D6E34",
            "BitmarkInTheFuture, 731EA25F9B40FE5FF88C8A35061ECC35E8F9958098C97E9EB523CDA97485277B"})
    public void testHashByteArrayOnce_NoError_ReturnCorrectHash(String inputString, String hexExpectedResult) {
        byte[] input = RAW.decode(inputString);
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha256.hash(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha256.hash(String) works correctly with happy condition ")
    @ParameterizedTest
    @CsvSource({"4269746D61726B53444B, F2B15C7200965A7E6827F80749657A3FE655980C1F03F2ECB475CFCF31F6CA31",
            "4A61766153444B, D9B7A41748DB4DA24F7DADC0D0A8C87E6CD1C2C754239E5009FF06FE163D6E34",
            "4269746D61726B496E546865467574757265, 731EA25F9B40FE5FF88C8A35061ECC35E8F9958098C97E9EB523CDA97485277B"})
    public void testHashHexStringOnce_NoError_ReturnCorrectHash(String input, String hexExpectedResult) {
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha256.hash(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }


    @DisplayName("Verify the function Sha256.hash(String) throws an Exception with invalid hex input")
    @ParameterizedTest
    @ValueSource(strings = {"4269746D61726B53444BHJKA", "NDRGGH64756DFASDVC", "@#$%sdfvsdf@#@#"})
    public void testHashHexStringOnce_InvalidHex_ErrorIsThrow(String hexInput) {
        assertThrows(ValidateException.InvalidHex.class, () -> Sha256.hash(hexInput), ValidateException.InvalidHex.ORIGIN_MESSAGE);
    }

    @DisplayName("Verify the function Sha256.hash(byte[], int, int) works well with happy condition")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, 95ED80A9188DDC42B9200A97C50CE39A0910E982A3CC86AE4362F42702C603B4, S",
            "JavaSDK, C3CF3DAB5458F5B59F5B703D42A9268E7186154A771ADC787A13934FDD2E5C85, a",
            "BitmarkInTheFuture, FCFA353ED0CCE560BD25A39340F61144D83A69CF1C9E6C1F220726D3A782C7E0, I"})
    public void testHashByteArrayOnceCustomOffsetAndLength_NoError_ReturnCorrectHash(String inputStr, String expectedHex, char startChar) {
        byte[] input = RAW.decode(inputStr);
        int length = RAW.decode(inputStr.substring(inputStr.indexOf(startChar))).length;
        byte[] expectedResult = HEX.decode(expectedHex);
        byte[] output = Sha256.hash(input, inputStr.indexOf(startChar), length);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha256.hash(byte[], int, int) throws Exception with invalid length or offset")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, -1, 1", "JavaSDK, -5, 1", "BitmarkInTheFuture, 10, -1"})
    public void testHashByteArrayOnceCustomOffsetAndLength_InvalidOffsetOrLength_ErrorIsThrow(String inputStr, int offset, int length) {
        byte[] input = RAW.decode(inputStr);
        assertThrows(ValidateException.class, () -> Sha256.hash(input, offset, length));
    }

    @DisplayName("Verify the function Sha256.hashTwice(byte[]) works correctly with happy condition")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, FE29FB2A23DAFB456AA5BB04C91B8AE7CA1A8D5326CE37536C402A55727BF741",
            "JavaSDK, 64EA0FC63BA71319C44050ABF270DDFB07D2ED7E073212AFE5408EF51EB8F5CE",
            "BitmarkInTheFuture, 33988AC5511629AEE8FF15886496577F9C4EF7E52E85D6A27BF495316F560D3B"})
    public void testHashByteArrayTwice_NoError_ReturnCorrectHash(String inputString, String hexExpectedResult) {
        byte[] input = RAW.decode(inputString);
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha256.hashTwice(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha256.hashTwice(String) works correctly with happy condition")
    @ParameterizedTest
    @CsvSource({"4269746D61726B53444B, FE29FB2A23DAFB456AA5BB04C91B8AE7CA1A8D5326CE37536C402A55727BF741",
            "4A61766153444B, 64EA0FC63BA71319C44050ABF270DDFB07D2ED7E073212AFE5408EF51EB8F5CE",
            "4269746D61726B496E546865467574757265, 33988AC5511629AEE8FF15886496577F9C4EF7E52E85D6A27BF495316F560D3B"})
    public void testHashHexStringTwice_NoError_ReturnCorrectHash(String hexInput, String hexExpectedResult) {
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha256.hashTwice(hexInput);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha256.hashTwice(String) throws Exception with invalid Hex")
    @ParameterizedTest
    @ValueSource(strings = {"ABCVDF345236GRH", "!@#$#$%$%^$%", "VAFEBS#@GSDG$%"})
    public void testHashHexStringTwice_InvalidHex_ErrorIsThrow(String hexInput) {
        assertThrows(ValidateException.InvalidHex.class, () -> Sha256.hashTwice(hexInput), ValidateException.InvalidHex.ORIGIN_MESSAGE);
    }

    @DisplayName("Verify the function Sha256.hashTwice(byte[], int, int) works well with happy condition")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, 1AE34783A48E95B70AF442D2BD89E91CDBE4FB8A469B7002561A38AF7F27A1F8, S",
            "JavaSDK, 2C599996CF7A436E62D7A3992008F70712643FA2F1BDDDBF1CED940FE5E6942A, a",
            "BitmarkInTheFuture, ACC2BFA9CD7C5EA4F704EB35E46A6814148D00EBA10675118792E0D2858AF2DD, I"})
    public void testHashByteArrayTwiceCustomOffsetAndLength_NoError_ReturnCorrectHash(String inputStr, String expectedHex, char startChar) {
        byte[] input = RAW.decode(inputStr);
        int length = RAW.decode(inputStr.substring(inputStr.indexOf(startChar))).length;
        byte[] expectedResult = HEX.decode(expectedHex);
        byte[] output = Sha256.hashTwice(input, inputStr.indexOf(startChar), length);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha256.hashTwice(byte[], int, int) throws Exception with invalid length or offset")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, -1, 1", "JavaSDK, -5, 1", "BitmarkInTheFuture, 10, -1"})
    public void testHashByteArrayTwiceCustomOffsetAndLength_InvalidOffsetOrLength_ErrorIsThrow(String inputHex, int offset, int length) {
        byte[] input = RAW.decode(inputHex);
        assertThrows(ValidateException.class, () -> Sha256.hashTwice(input, offset, length));
    }

    @DisplayName("Verify the Sha256.compareTo(Sha256) works well with equal hashes")
    @ParameterizedTest
    @CsvSource({"1AE34783A48E95B70AF442D2BD89E91CDBE4FB8A469B7002561A38AF7F27A1F8, 1AE34783A48E95B70AF442D2BD89E91CDBE4FB8A469B7002561A38AF7F27A1F8",
            "2C599996CF7A436E62D7A3992008F70712643FA2F1BDDDBF1CED940FE5E6942A, 2C599996CF7A436E62D7A3992008F70712643FA2F1BDDDBF1CED940FE5E6942A",
            "ACC2BFA9CD7C5EA4F704EB35E46A6814148D00EBA10675118792E0D2858AF2DD, ACC2BFA9CD7C5EA4F704EB35E46A6814148D00EBA10675118792E0D2858AF2DD"})
    public void testCompareEqualHashes_NoError_ReturnExactResult(String firstHex, String secondHex) {
        Sha256 first = Sha256.from(firstHex);
        Sha256 second = Sha256.from(secondHex);
        assertEquals(0, first.compareTo(second));
    }

    @DisplayName("Verify the Sha256.compareTo(Sha256) works well with not equal hashes")
    @ParameterizedTest
    @CsvSource({"1AE34783A48E95B70AF442D2BD89E91CDBE4FB8A469B7002561A38AF7F27A1F8, 1AD34783A48E95B70AF442D2BD89E91CDBE4FB8A469B7002561A38AF7F27A1F8",
            "2C599996CF7A436E62D7A3992008F70712643FA2F1BDDDBF1CED940FE5E6942A, 2C589996CF7A436E62D7A3992008F70712643FA2F1BDDDBF1CED940FE5E6942A",
            "ACC2BFA9CD7C5EA4F704EB35E46A6814148D00EBA10675118792E0D2858AF2DD, ACC1AFA9CD7C5EA4F704EB35E46A6814148D00EBA10675118792E0D2858AF2DD"})
    public void testCompareNotEqualHashes_NoError_ReturnExactResult(String firstHex, String secondHex) {
        Sha256 first = Sha256.from(firstHex);
        Sha256 second = Sha256.from(secondHex);
        assertEquals(1, first.compareTo(second));
    }

    @DisplayName("Verify that constructing a new instance Sha256 with invalid length hex hash should throws an Exception")
    @ParameterizedTest
    @ValueSource(strings = {"AB", "ABC2434534", "ABCDEF123445672324"})
    public void testConstructInstanceFromHexString_InvalidHashLength_ErrorIsThrow(String hexHash) {
        assertThrows(ValidateException.InvalidLength.class, () -> Sha256.from(hexHash));
    }

    @DisplayName("Verify that constructing a new instance Sha256 with invalid length byte array should throws an Exception")
    @ParameterizedTest
    @ValueSource(strings = {"AB", "ABC2434534", "ABCDEF123445672324"})
    public void testConstructInstanceFromByteArray_InvalidHashLength_ErrorIsThrow(String hexHash) {
        assertThrows(ValidateException.InvalidLength.class, () -> Sha256.from(HEX.decode(hexHash)));
    }

    @DisplayName("Verify that constructing a new instance Sha256 from hex hash successfully in happy condition")
    @ParameterizedTest
    @ValueSource(strings = {"1AE34783A48E95B70AF442D2BD89E91CDBE4FB8A469B7002561A38AF7F27A1F8",
            "2C599996CF7A436E62D7A3992008F70712643FA2F1BDDDBF1CED940FE5E6942A",
            "ACC2BFA9CD7C5EA4F704EB35E46A6814148D00EBA10675118792E0D2858AF2DD"})
    public void testConstructInstanceFromByteArray_ValidHash_ErrorIsNotThrow(String hexHash) {
        assertDoesNotThrow(() -> Sha256.hash(hexHash));
    }

}
