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

public class Sha3256Test extends BaseCryptoTest {

    @DisplayName("Verify the function Sha3256.hash(byte[]) works correctly with happy condition")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, 148D5EFDCE00D2DC0CAA14DA6FC0BF3C9456DCBD2C680D86DD57BE112B7EDCCE",
            "JavaSDK, EC2A665796A2D8BE8F4DA565F57FE528C1D689764D8C56F8B31266ACEF15C404",
            "BitmarkInTheFuture, 11D77428B1EC48EEAC27B5263E66838665E357631CF97BAD5EB7EA07840D1AAF"})
    public void testHashByteArrayOnce_NoError_ReturnCorrectHash(String inputString, String hexExpectedResult) {
        byte[] input = RAW.decode(inputString);
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3256.hash(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha3256.hash(String) works correctly with happy condition ")
    @ParameterizedTest
    @CsvSource({"4269746D61726B53444B, 148D5EFDCE00D2DC0CAA14DA6FC0BF3C9456DCBD2C680D86DD57BE112B7EDCCE",
            "4A61766153444B, EC2A665796A2D8BE8F4DA565F57FE528C1D689764D8C56F8B31266ACEF15C404",
            "4269746D61726B496E546865467574757265, 11D77428B1EC48EEAC27B5263E66838665E357631CF97BAD5EB7EA07840D1AAF"})
    public void testHashHexStringOnce_NoError_ReturnCorrectHash(String input, String hexExpectedResult) {
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3256.hash(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }


    @DisplayName("Verify the function Sha3256.hash(String) throws an Exception with invalid hex " +
            "input")
    @ParameterizedTest
    @ValueSource(strings = {"4269746D61726B53444BHJKA", "NDRGGH64756DFASDVC", "@#$%sdfvsdf@#@#"})
    public void testHashHexStringOnce_InvalidHex_ErrorIsThrow(String hexInput) {
        assertThrows(ValidateException.InvalidHex.class, () -> Sha3256.hash(hexInput),
                ValidateException.InvalidHex.ORIGIN_MESSAGE);
    }

    @DisplayName("Verify the function Sha3256.hash(byte[], int, int) works well with happy " +
            "condition")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, 844AA9B0A09C37829B2BCAD976DAEC427A0C4A29C2DDF9CB959E9A3F8F14F9E0, S",
            "JavaSDK, DD11A00AEF092F4AD50319B613809B182712EBDD8E0549885C418B44A186565F, a",
            "BitmarkInTheFuture, 0245C3724E0B0DE137B30CFD8CE18750963123A4C685F32928D22FEB371ACC25, I"})
    public void testHashByteArrayOnceCustomOffsetAndLength_NoError_ReturnCorrectHash(String inputStr, String expectedHex, char startChar) {
        byte[] input = RAW.decode(inputStr);
        int length = RAW.decode(inputStr.substring(inputStr.indexOf(startChar))).length;
        byte[] expectedResult = HEX.decode(expectedHex);
        byte[] output = Sha3256.hash(input, inputStr.indexOf(startChar), length);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha3256.hash(byte[], int, int) throws Exception with " +
            "invalid length or offset")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, -1, 1", "JavaSDK, -5, 1", "BitmarkInTheFuture, 10, -1"})
    public void testHashByteArrayOnceCustomOffsetAndLength_InvalidOffsetOrLength_ErrorIsThrow(String inputStr, int offset, int length) {
        byte[] input = RAW.decode(inputStr);
        assertThrows(ValidateException.class, () -> Sha3256.hash(input, offset,
                length));
    }

    @DisplayName("Verify the function Sha3256.hashTwice(byte[]) works correctly with happy " +
            "condition")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, 334F57730E81985FF6B46CFB17A9C94B9C444F9B391AED7F4E190DC4FCDE2492",
            "JavaSDK, EA53A602C61DA880F9D49D9F508CB34D52001B3C72542D61C80779006F2865CB",
            "BitmarkInTheFuture, 84228E5C37CCE0CD4B4B985B3E7B43D0727CA0F3894FD9D8111B20F6D7EFDADA"})
    public void testHashByteArrayTwice_NoError_ReturnCorrectHash(String inputString, String hexExpectedResult) {
        byte[] input = RAW.decode(inputString);
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3256.hashTwice(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha3256.hashTwice(String) works correctly with happy " +
            "condition")
    @ParameterizedTest
    @CsvSource({"4269746D61726B53444B, 334F57730E81985FF6B46CFB17A9C94B9C444F9B391AED7F4E190DC4FCDE2492",
            "4A61766153444B, EA53A602C61DA880F9D49D9F508CB34D52001B3C72542D61C80779006F2865CB",
            "4269746D61726B496E546865467574757265, 84228E5C37CCE0CD4B4B985B3E7B43D0727CA0F3894FD9D8111B20F6D7EFDADA"})
    public void testHashHexStringTwice_NoError_ReturnCorrectHash(String hexInput, String hexExpectedResult) {
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3256.hashTwice(hexInput);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha3256.hashTwice(String) throws Exception with invalid Hex")
    @ParameterizedTest
    @ValueSource(strings = {"ABCVDF345236GRH", "!@#$#$%$%^$%", "VAFEBS#@GSDG$%"})
    public void testHashHexStringTwice_InvalidHex_ErrorIsThrow(String hexInput) {
        assertThrows(ValidateException.InvalidHex.class, () -> Sha3256.hashTwice(hexInput),
                ValidateException.InvalidHex.ORIGIN_MESSAGE);
    }

    @DisplayName("Verify the function Sha3256.hashTwice(byte[], int, int) works well with happy " +
            "condition")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, 417980E19C8D52D18A5EB8A6CE7D1F98719793E9D323F7B178EFE90AB5509268, S",
            "JavaSDK, C3BD2FC78A886DD6519DDA25756C132AEC82C3D43480D58E0B3302A27A83D252, a",
            "BitmarkInTheFuture, BD4E0B4B5A4E68B6F0861520F84E423970DF4B6FD98E0F4137372DD5CB770F48, I"})
    public void testHashByteArrayTwiceCustomOffsetAndLength_NoError_ReturnCorrectHash(String inputStr, String expectedHex, char startChar) {
        byte[] input = RAW.decode(inputStr);
        int length = RAW.decode(inputStr.substring(inputStr.indexOf(startChar))).length;
        byte[] expectedResult = HEX.decode(expectedHex);
        byte[] output = Sha3256.hashTwice(input, inputStr.indexOf(startChar), length);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha3256.hashTwice(byte[], int, int) throws Exception with " +
            "invalid length or offset")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, -1, 1", "JavaSDK, -5, 1", "BitmarkInTheFuture, 10, -1"})
    public void testHashByteArrayTwiceCustomOffsetAndLength_InvalidOffsetOrLength_ErrorIsThrow(String inputHex, int offset, int length) {
        byte[] input = RAW.decode(inputHex);
        assertThrows(ValidateException.class, () -> Sha3256.hashTwice(input, offset,
                length));
    }

    @DisplayName("Verify the Sha3256.compareTo(Sha3256) works well with equal hashes")
    @ParameterizedTest
    @CsvSource({"1AE34783A48E95B70AF442D2BD89E91CDBE4FB8A469B7002561A38AF7F27A1F8, 1AE34783A48E95B70AF442D2BD89E91CDBE4FB8A469B7002561A38AF7F27A1F8",
            "2C599996CF7A436E62D7A3992008F70712643FA2F1BDDDBF1CED940FE5E6942A, 2C599996CF7A436E62D7A3992008F70712643FA2F1BDDDBF1CED940FE5E6942A",
            "ACC2BFA9CD7C5EA4F704EB35E46A6814148D00EBA10675118792E0D2858AF2DD, ACC2BFA9CD7C5EA4F704EB35E46A6814148D00EBA10675118792E0D2858AF2DD"})
    public void testCompareEqualHashes_NoError_ReturnExactResult(String firstHex, String secondHex) {
        Sha3256 first = Sha3256.from(firstHex);
        Sha3256 second = Sha3256.from(secondHex);
        assertEquals(0, first.compareTo(second));
    }

    @DisplayName("Verify the Sha3256.compareTo(Sha3256) works well with not equal hashes")
    @ParameterizedTest
    @CsvSource({"1AE34783A48E95B70AF442D2BD89E91CDBE4FB8A469B7002561A38AF7F27A1F8, 1AD34783A48E95B70AF442D2BD89E91CDBE4FB8A469B7002561A38AF7F27A1F8",
            "2C599996CF7A436E62D7A3992008F70712643FA2F1BDDDBF1CED940FE5E6942A, 2C589996CF7A436E62D7A3992008F70712643FA2F1BDDDBF1CED940FE5E6942A",
            "ACC2BFA9CD7C5EA4F704EB35E46A6814148D00EBA10675118792E0D2858AF2DD, ACC1AFA9CD7C5EA4F704EB35E46A6814148D00EBA10675118792E0D2858AF2DD"})
    public void testCompareNotEqualHashes_NoError_ReturnExactResult(String firstHex, String secondHex) {
        Sha3256 first = Sha3256.from(firstHex);
        Sha3256 second = Sha3256.from(secondHex);
        assertEquals(1, first.compareTo(second));
    }

    @DisplayName("Verify that constructing a new instance Sha3256 with invalid length hex hash " +
            "should throws an Exception")
    @ParameterizedTest
    @ValueSource(strings = {"AB", "ABC2434534", "ABCDEF123445672324"})
    public void testConstructInstanceFromHexString_InvalidHashLength_ErrorIsThrow(String hexHash) {
        assertThrows(ValidateException.InvalidLength.class, () -> Sha3256.from(hexHash));
    }

    @DisplayName("Verify that constructing a new instance Sha3256 with invalid length byte array " +
            "should throws an Exception")
    @ParameterizedTest
    @ValueSource(strings = {"AB", "ABC2434534", "ABCDEF123445672324"})
    public void testConstructInstanceFromByteArray_InvalidHashLength_ErrorIsThrow(String hexHash) {
        assertThrows(ValidateException.InvalidLength.class,
                () -> Sha3256.from(HEX.decode(hexHash)));
    }

    @DisplayName("Verify that constructing a new instance Sha3256 from hex hash successfully in " +
            "happy condition")
    @ParameterizedTest
    @ValueSource(strings = {"1AE34783A48E95B70AF442D2BD89E91CDBE4FB8A469B7002561A38AF7F27A1F8",
            "2C599996CF7A436E62D7A3992008F70712643FA2F1BDDDBF1CED940FE5E6942A",
            "ACC2BFA9CD7C5EA4F704EB35E46A6814148D00EBA10675118792E0D2858AF2DD"})
    public void testConstructInstanceFromByteArray_ValidHash_ErrorIsNotThrow(String hexHash) {
        assertDoesNotThrow(() -> Sha3256.hash(hexHash));
    }
}
