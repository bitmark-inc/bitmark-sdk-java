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
 * @since 9/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Sha3512Test extends BaseCryptoTest {

    @DisplayName("Verify the function Sha3512.hash(byte[]) works correctly with happy condition")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, F5AD8D9B58E122D2D229F86EAA5D276496A5A3DA19D53C887A23F81955A3D07266B50A896D332ABC1D1845850311E50570CB56EE507B89EC18BC91EDC34C1059",
                       "JavaSDK, 613AB997FB2172FD4D12FB2B82157E3CD081E2D95ADEFE96C6BE9151667E67ED72FA0CAEDD109805B6F07C3EFE428F03B836AD36EC51165F35ADB4572530E2BF",
                       "BitmarkInTheFuture, C5C834EFB58A5106DD601676BF606DA91B752CABABFBE223122F71BE5B2963DC57D593F5F45B61F1ABE2FCF80277FD4075EE561813BC69C0343B4C4E68516237"})
    public void testHashByteArrayOnce_NoError_ReturnCorrectHash(String inputString, String hexExpectedResult) {
        byte[] input = RAW.decode(inputString);
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3512.hash(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha3512.hash(String) works correctly with happy condition ")
    @ParameterizedTest
    @CsvSource({"4269746D61726B53444B, F5AD8D9B58E122D2D229F86EAA5D276496A5A3DA19D53C887A23F81955A3D07266B50A896D332ABC1D1845850311E50570CB56EE507B89EC18BC91EDC34C1059",
                       "4A61766153444B, 613AB997FB2172FD4D12FB2B82157E3CD081E2D95ADEFE96C6BE9151667E67ED72FA0CAEDD109805B6F07C3EFE428F03B836AD36EC51165F35ADB4572530E2BF",
                       "4269746D61726B496E546865467574757265, C5C834EFB58A5106DD601676BF606DA91B752CABABFBE223122F71BE5B2963DC57D593F5F45B61F1ABE2FCF80277FD4075EE561813BC69C0343B4C4E68516237"})
    public void testHashHexStringOnce_NoError_ReturnCorrectHash(String input, String hexExpectedResult) {
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3512.hash(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }


    @DisplayName("Verify the function Sha3512.hash(String) throws an Exception with invalid hex " +
                         "input")
    @ParameterizedTest
    @ValueSource(strings = {"4269746D61726B53444BHJKA", "NDRGGH64756DFASDVC", "@#$%sdfvsdf@#@#"})
    public void testHashHexStringOnce_InvalidHex_ErrorIsThrow(String hexInput) {
        assertThrows(ValidateException.InvalidHex.class, () -> Sha3512.hash(hexInput),
                ValidateException.InvalidHex.ORIGIN_MESSAGE);
    }

    @DisplayName("Verify the function Sha3512.hash(byte[], int, int) works well with happy " +
                         "condition")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, 85A33DA78D99A4394FF1F73FC561401F87E369606FA15ADE52C4FFF332E868B690B950056FCB1EEBB8AB94F1E853F34D8C5B9D7DF10E9A3090393E465032CFEC, S",
                       "JavaSDK, DCA5F7CF9A28BB000CF7B199F85968E1B4ABC79BBF7339CDDBD3AEC004D59950E368F1E89250A22D00660FEE59596DBAD4CBE9AC6B3187B4B9C62EB42B3CA563, a",
                       "BitmarkInTheFuture, 4B5D228224E081078BB263CC64E3A6D8D8DBF2C621015901714CCAB8BDC6BD1A73ACC436694B60383DBE911F9796B5BC7B8F84CBC77F3F01DFB8DAB7AC5B73F9, I"})
    public void testHashByteArrayOnceCustomOffsetAndLength_NoError_ReturnCorrectHash(String inputStr, String expectedHex, char startChar) {
        byte[] input = RAW.decode(inputStr);
        int length = RAW.decode(inputStr.substring(inputStr.indexOf(startChar))).length;
        byte[] expectedResult = HEX.decode(expectedHex);
        byte[] output = Sha3512.hash(input, inputStr.indexOf(startChar), length);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha3512.hash(byte[], int, int) throws Exception with " +
                         "invalid length or offset")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, -1, 1", "JavaSDK, -5, 1", "BitmarkInTheFuture, 10, -1"})
    public void testHashByteArrayOnceCustomOffsetAndLength_InvalidOffsetOrLength_ErrorIsThrow(String inputStr, int offset, int length) {
        byte[] input = RAW.decode(inputStr);
        assertThrows(ValidateException.class, () -> Sha3512.hash(input, offset,
                length));
    }

    @DisplayName("Verify the function Sha3512.hashTwice(byte[]) works correctly with happy " +
                         "condition")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, 5126292A6CCA69105CC803D94082C22465D4AF77E90F40D9A9D2B1CBC2D7412FAE521CA0D4EADA60906D517FB1DC144D685F193B11AB7A7CEA02E47E5A6E0F47",
                       "JavaSDK, 255D590F5474D573A9FE54C6802F2DF6A2994F1FA8E2E87B5C20B007C2AA68D9C91A41ACE0BA28DF458B363E268A0FBE75A5DFD8C1B40A3C6CE198CEE4F1D3F0",
                       "BitmarkInTheFuture, DD227B0A4BD4C9F320620ADAFA167C4AD8DF1AD39BDCE201C59126C7418BBA540921E644F7536887C3AC868DEC12AD4AAEF54766EA013C60002A7E55BA7262D6"})
    public void testHashByteArrayTwice_NoError_ReturnCorrectHash(String inputString, String hexExpectedResult) {
        byte[] input = RAW.decode(inputString);
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3512.hashTwice(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha3512.hashTwice(String) works correctly with happy " +
                         "condition")
    @ParameterizedTest
    @CsvSource({"4269746D61726B53444B, 5126292A6CCA69105CC803D94082C22465D4AF77E90F40D9A9D2B1CBC2D7412FAE521CA0D4EADA60906D517FB1DC144D685F193B11AB7A7CEA02E47E5A6E0F47",
                       "4A61766153444B, 255D590F5474D573A9FE54C6802F2DF6A2994F1FA8E2E87B5C20B007C2AA68D9C91A41ACE0BA28DF458B363E268A0FBE75A5DFD8C1B40A3C6CE198CEE4F1D3F0",
                       "4269746D61726B496E546865467574757265, DD227B0A4BD4C9F320620ADAFA167C4AD8DF1AD39BDCE201C59126C7418BBA540921E644F7536887C3AC868DEC12AD4AAEF54766EA013C60002A7E55BA7262D6"})
    public void testHashHexStringTwice_NoError_ReturnCorrectHash(String hexInput, String hexExpectedResult) {
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3512.hashTwice(hexInput);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha3512.hashTwice(String) throws Exception with invalid Hex")
    @ParameterizedTest
    @ValueSource(strings = {"ABCVDF345236GRH", "!@#$#$%$%^$%", "VAFEBS#@GSDG$%"})
    public void testHashHexStringTwice_InvalidHex_ErrorIsThrow(String hexInput) {
        assertThrows(ValidateException.InvalidHex.class, () -> Sha3512.hashTwice(hexInput),
                ValidateException.InvalidHex.ORIGIN_MESSAGE);
    }

    @DisplayName("Verify the function Sha3512.hashTwice(byte[], int, int) works well with happy " +
                         "condition")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, 32B91D3D93F73EC0587250870495D6A3D8226DDD279E9EFD59694DF15E28158B8262A2B91FAA73EC4A2A2356A529BF9A6E49936FA0D24D568951F33313238700, S",
                       "JavaSDK, F3D9AFA44C912B0BC5F75FD498ED47886964BC6080DC299C01B1CD557F4E6126230AB933539A42F5B8796BFAD0456E0548BD104A6BAD29A0224868BB0336330F, a",
                       "BitmarkInTheFuture, 7A4D1DDBCF6D72E8CA1063D9BA1F32EA1B208C2D55D26629802860C3C4E57EC0A72781D4C1D11F4F7AA1AC2F3BC9E3FD5201B966BE58F484F416FC51831C61DB, I"})
    public void testHashByteArrayTwiceCustomOffsetAndLength_NoError_ReturnCorrectHash(String inputStr, String expectedHex, char startChar) {
        byte[] input = RAW.decode(inputStr);
        int length = RAW.decode(inputStr.substring(inputStr.indexOf(startChar))).length;
        byte[] expectedResult = HEX.decode(expectedHex);
        byte[] output = Sha3512.hashTwice(input, inputStr.indexOf(startChar), length);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha3512.hashTwice(byte[], int, int) throws Exception with " +
                         "invalid length or offset")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, -1, 1", "JavaSDK, -5, 1", "BitmarkInTheFuture, 10, -1"})
    public void testHashByteArrayTwiceCustomOffsetAndLength_InvalidOffsetOrLength_ErrorIsThrow(String inputHex, int offset, int length) {
        byte[] input = RAW.decode(inputHex);
        assertThrows(ValidateException.class, () -> Sha3512.hashTwice(input, offset,
                length));
    }

    @DisplayName("Verify the Sha3512.compareTo(Sha3512) works well with equal hashes")
    @ParameterizedTest
    @CsvSource({"32B91D3D93F73EC0587250870495D6A3D8226DDD279E9EFD59694DF15E28158B8262A2B91FAA73EC4A2A2356A529BF9A6E49936FA0D24D568951F33313238700, 32B91D3D93F73EC0587250870495D6A3D8226DDD279E9EFD59694DF15E28158B8262A2B91FAA73EC4A2A2356A529BF9A6E49936FA0D24D568951F33313238700",
                       "F3D9AFA44C912B0BC5F75FD498ED47886964BC6080DC299C01B1CD557F4E6126230AB933539A42F5B8796BFAD0456E0548BD104A6BAD29A0224868BB0336330F, F3D9AFA44C912B0BC5F75FD498ED47886964BC6080DC299C01B1CD557F4E6126230AB933539A42F5B8796BFAD0456E0548BD104A6BAD29A0224868BB0336330F",
                       "7A4D1DDBCF6D72E8CA1063D9BA1F32EA1B208C2D55D26629802860C3C4E57EC0A72781D4C1D11F4F7AA1AC2F3BC9E3FD5201B966BE58F484F416FC51831C61DB, 7A4D1DDBCF6D72E8CA1063D9BA1F32EA1B208C2D55D26629802860C3C4E57EC0A72781D4C1D11F4F7AA1AC2F3BC9E3FD5201B966BE58F484F416FC51831C61DB"})
    public void testCompareEqualHashes_NoError_ReturnExactResult(String firstHex, String secondHex) {
        Sha3512 first = Sha3512.from(firstHex);
        Sha3512 second = Sha3512.from(secondHex);
        assertEquals(0, first.compareTo(second));
    }

    @DisplayName("Verify the Sha3512.compareTo(Sha3512) works well with not equal hashes")
    @ParameterizedTest
    @CsvSource({"32B91D3D93F73EC0587250870495D6A3D8226DDD279E9EFD59694DF15E28158B8262A2B91FAA73EC4A2A2356A529BF9A6E49936FA0D24D568951F33313238700, 31B91D3D93F73EC0587250870495D6A3D8226DDD279E9EFD59694DF15E28158B8262A2B91FAA73EC4A2A2356A529BF9A6E49936FA0D24D568951F33313238700",
                       "F3D9AFA44C912B0BC5F75FD498ED47886964BC6080DC299C01B1CD557F4E6126230AB933539A42F5B8796BFAD0456E0548BD104A6BAD29A0224868BB0336330F, F3D9AFA44C912B0BC5F75FD498ED47886964BC6080DC299C01B1CA557F4E6126230AB933539A42F5B8796BFAD0456E0548BD104A6BAD29A0224868BB0336330F",
                       "7A4D1DDBCF6D72E8CA1063D9BA1F32EA1B208C2D55D26629802860C3C4E57EC0A72781D4C1D11F4F7AA1AC2F3BC9E3FD5201B966BE58F484F416FC51831C61DB, 7A4D1DDBCF6D72E8CA1063D9BA1F32EA1B208C2D55D26629802860C3C4E57EC0172781D4C1D11F4F7AA1AC2F3BC9E3FD5201B966BE58F484F416FC51831C61DB"})
    public void testCompareNotEqualHashes_NoError_ReturnExactResult(String firstHex, String secondHex) {
        Sha3512 first = Sha3512.from(firstHex);
        Sha3512 second = Sha3512.from(secondHex);
        assertEquals(1, first.compareTo(second));
    }

    @DisplayName("Verify that constructing a new instance Sha3512 with invalid length hex hash " +
                         "should throws an Exception")
    @ParameterizedTest
    @ValueSource(strings = {"AB", "ABC2434534", "ABCDEF123445672324"})
    public void testConstructInstanceFromHexString_InvalidHashLength_ErrorIsThrow(String hexHash) {
        assertThrows(ValidateException.InvalidLength.class, () -> Sha3512.from(hexHash));
    }

    @DisplayName("Verify that constructing a new instance Sha3512 with invalid length byte array " +
                         "should throws an Exception")
    @ParameterizedTest
    @ValueSource(strings = {"AB", "ABC2434534", "ABCDEF123445672324"})
    public void testConstructInstanceFromByteArray_InvalidHashLength_ErrorIsThrow(String hexHash) {
        assertThrows(ValidateException.InvalidLength.class,
                () -> Sha3512.from(HEX.decode(hexHash)));
    }

    @DisplayName("Verify that constructing a new instance Sha3512 from hex hash successfully in " +
                         "happy condition")
    @ParameterizedTest
    @ValueSource(strings = {"1AE34783A48E95B70AF442D2BD89E91CDBE4FB8A469B7002561A38AF7F27A1F8",
            "2C599996CF7A436E62D7A3992008F70712643FA2F1BDDDBF1CED940FE5E6942A",
            "ACC2BFA9CD7C5EA4F704EB35E46A6814148D00EBA10675118792E0D2858AF2DD"})
    public void testConstructInstanceFromByteArray_ValidHash_ErrorIsNotThrow(String hexHash) {
        assertDoesNotThrow(() -> Sha3512.hash(hexHash));
    }
}
