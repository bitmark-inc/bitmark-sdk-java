package cryptography.test.crypto;

import cryptography.crypto.Sha3512;
import cryptography.error.ValidateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static cryptography.crypto.encoder.Hex.HEX;
import static cryptography.crypto.encoder.Raw.RAW;
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
    @CsvSource({"BitmarkSDK, f5ad8d9b58e122d2d229f86eaa5d276496a5a3da19d53c887a23f81955a3d07266b50a896d332abc1d1845850311e50570cb56ee507b89ec18bc91edc34c1059",
                       "JavaSDK, 613ab997fb2172fd4d12fb2b82157e3cd081e2d95adefe96c6be9151667e67ed72fa0caedd109805b6f07c3efe428f03b836ad36ec51165f35adb4572530e2bf",
                       "BitmarkInTheFuture, c5c834efb58a5106dd601676bf606da91b752cababfbe223122f71be5b2963dc57d593f5f45b61f1abe2fcf80277fd4075ee561813bc69c0343b4c4e68516237"})
    public void testHashByteArrayOnce_NoError_ReturnCorrectHash(String inputString, String hexExpectedResult) {
        byte[] input = RAW.decode(inputString);
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3512.hash(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha3512.hash(String) works correctly with happy condition ")
    @ParameterizedTest
    @CsvSource({"4269746d61726b53444b, f5ad8d9b58e122d2d229f86eaa5d276496a5a3da19d53c887a23f81955a3d07266b50a896d332abc1d1845850311e50570cb56ee507b89ec18bc91edc34c1059",
                       "4a61766153444b, 613ab997fb2172fd4d12fb2b82157e3cd081e2d95adefe96c6be9151667e67ed72fa0caedd109805b6f07c3efe428f03b836ad36ec51165f35adb4572530e2bf",
                       "4269746d61726b496e546865467574757265, c5c834efb58a5106dd601676bf606da91b752cababfbe223122f71be5b2963dc57d593f5f45b61f1abe2fcf80277fd4075ee561813bc69c0343b4c4e68516237"})
    public void testHashHexStringOnce_NoError_ReturnCorrectHash(String input, String hexExpectedResult) {
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3512.hash(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }


    @DisplayName("Verify the function Sha3512.hash(String) throws an Exception with invalid hex " +
                         "input")
    @ParameterizedTest
    @ValueSource(strings = {"4269746d61726b53444bhjka", "ndrggh64756dfasdvc", "@#$%sdfvsdf@#@#"})
    public void testHashHexStringOnce_InvalidHex_ErrorIsThrow(String hexInput) {
        assertThrows(ValidateException.InvalidHex.class, () -> Sha3512.hash(hexInput),
                ValidateException.InvalidHex.ORIGIN_MESSAGE);
    }

    @DisplayName("Verify the function Sha3512.hash(byte[], int, int) works well with happy " +
                         "condition")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, 85a33da78d99a4394ff1f73fc561401f87e369606fa15ade52c4fff332e868b690b950056fcb1eebb8ab94f1e853f34d8c5b9d7df10e9a3090393e465032cfec, S",
                       "JavaSDK, dca5f7cf9a28bb000cf7b199f85968e1b4abc79bbf7339cddbd3aec004d59950e368f1e89250a22d00660fee59596dbad4cbe9ac6b3187b4b9c62eb42b3ca563, a",
                       "BitmarkInTheFuture, 4b5d228224e081078bb263cc64e3a6d8d8dbf2c621015901714ccab8bdc6bd1a73acc436694b60383dbe911f9796b5bc7b8f84cbc77f3f01dfb8dab7ac5b73f9, I"})
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
    @CsvSource({"BitmarkSDK, 5126292a6cca69105cc803d94082c22465d4af77e90f40d9a9d2b1cbc2d7412fae521ca0d4eada60906d517fb1dc144d685f193b11ab7a7cea02e47e5a6e0f47",
                       "JavaSDK, 255d590f5474d573a9fe54c6802f2df6a2994f1fa8e2e87b5c20b007c2aa68d9c91a41ace0ba28df458b363e268a0fbe75a5dfd8c1b40a3c6ce198cee4f1d3f0",
                       "BitmarkInTheFuture, dd227b0a4bd4c9f320620adafa167c4ad8df1ad39bdce201c59126c7418bba540921e644f7536887c3ac868dec12ad4aaef54766ea013c60002a7e55ba7262d6"})
    public void testHashByteArrayTwice_NoError_ReturnCorrectHash(String inputString, String hexExpectedResult) {
        byte[] input = RAW.decode(inputString);
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3512.hashTwice(input);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha3512.hashTwice(String) works correctly with happy " +
                         "condition")
    @ParameterizedTest
    @CsvSource({"4269746d61726b53444b, 5126292a6cca69105cc803d94082c22465d4af77e90f40d9a9d2b1cbc2d7412fae521ca0d4eada60906d517fb1dc144d685f193b11ab7a7cea02e47e5a6e0f47",
                       "4a61766153444b, 255d590f5474d573a9fe54c6802f2df6a2994f1fa8e2e87b5c20b007c2aa68d9c91a41ace0ba28df458b363e268a0fbe75a5dfd8c1b40a3c6ce198cee4f1d3f0",
                       "4269746d61726b496e546865467574757265, dd227b0a4bd4c9f320620adafa167c4ad8df1ad39bdce201c59126c7418bba540921e644f7536887c3ac868dec12ad4aaef54766ea013c60002a7e55ba7262d6"})
    public void testHashHexStringTwice_NoError_ReturnCorrectHash(String hexInput, String hexExpectedResult) {
        byte[] expectedResult = HEX.decode(hexExpectedResult);
        byte[] output = Sha3512.hashTwice(hexInput);
        assertTrue(Arrays.equals(expectedResult, output));
    }

    @DisplayName("Verify the function Sha3512.hashTwice(String) throws Exception with invalid Hex")
    @ParameterizedTest
    @ValueSource(strings = {"abcvdf345236grh", "!@#$#$%$%^$%", "vafebs#@gsdg$%"})
    public void testHashHexStringTwice_InvalidHex_ErrorIsThrow(String hexInput) {
        assertThrows(ValidateException.InvalidHex.class, () -> Sha3512.hashTwice(hexInput),
                ValidateException.InvalidHex.ORIGIN_MESSAGE);
    }

    @DisplayName("Verify the function Sha3512.hashTwice(byte[], int, int) works well with happy " +
                         "condition")
    @ParameterizedTest
    @CsvSource({"BitmarkSDK, 32b91d3d93f73ec0587250870495d6a3d8226ddd279e9efd59694df15e28158b8262a2b91faa73ec4a2a2356a529bf9a6e49936fa0d24d568951f33313238700, S",
                       "JavaSDK, f3d9afa44c912b0bc5f75fd498ed47886964bc6080dc299c01b1cd557f4e6126230ab933539a42f5b8796bfad0456e0548bd104a6bad29a0224868bb0336330f, a",
                       "BitmarkInTheFuture, 7a4d1ddbcf6d72e8ca1063d9ba1f32ea1b208c2d55d26629802860c3c4e57ec0a72781d4c1d11f4f7aa1ac2f3bc9e3fd5201b966be58f484f416fc51831c61db, I"})
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
    @CsvSource({"32b91d3d93f73ec0587250870495d6a3d8226ddd279e9efd59694df15e28158b8262a2b91faa73ec4a2a2356a529bf9a6e49936fa0d24d568951f33313238700, 32b91d3d93f73ec0587250870495d6a3d8226ddd279e9efd59694df15e28158b8262a2b91faa73ec4a2a2356a529bf9a6e49936fa0d24d568951f33313238700",
                       "f3d9afa44c912b0bc5f75fd498ed47886964bc6080dc299c01b1cd557f4e6126230ab933539a42f5b8796bfad0456e0548bd104a6bad29a0224868bb0336330f, f3d9afa44c912b0bc5f75fd498ed47886964bc6080dc299c01b1cd557f4e6126230ab933539a42f5b8796bfad0456e0548bd104a6bad29a0224868bb0336330f",
                       "7a4d1ddbcf6d72e8ca1063d9ba1f32ea1b208c2d55d26629802860c3c4e57ec0a72781d4c1d11f4f7aa1ac2f3bc9e3fd5201b966be58f484f416fc51831c61db, 7a4d1ddbcf6d72e8ca1063d9ba1f32ea1b208c2d55d26629802860c3c4e57ec0a72781d4c1d11f4f7aa1ac2f3bc9e3fd5201b966be58f484f416fc51831c61db"})
    public void testCompareEqualHashes_NoError_ReturnExactResult(String firstHex, String secondHex) {
        Sha3512 first = Sha3512.from(firstHex);
        Sha3512 second = Sha3512.from(secondHex);
        assertEquals(0, first.compareTo(second));
    }

    @DisplayName("Verify the Sha3512.compareTo(Sha3512) works well with not equal hashes")
    @ParameterizedTest
    @CsvSource({"32b91d3d93f73ec0587250870495d6a3d8226ddd279e9efd59694df15e28158b8262a2b91faa73ec4a2a2356a529bf9a6e49936fa0d24d568951f33313238700, 31b91d3d93f73ec0587250870495d6a3d8226ddd279e9efd59694df15e28158b8262a2b91faa73ec4a2a2356a529bf9a6e49936fa0d24d568951f33313238700",
                       "f3d9afa44c912b0bc5f75fd498ed47886964bc6080dc299c01b1cd557f4e6126230ab933539a42f5b8796bfad0456e0548bd104a6bad29a0224868bb0336330f, f3d9afa44c912b0bc5f75fd498ed47886964bc6080dc299c01b1ca557f4e6126230ab933539a42f5b8796bfad0456e0548bd104a6bad29a0224868bb0336330f",
                       "7a4d1ddbcf6d72e8ca1063d9ba1f32ea1b208c2d55d26629802860c3c4e57ec0a72781d4c1d11f4f7aa1ac2f3bc9e3fd5201b966be58f484f416fc51831c61db, 7a4d1ddbcf6d72e8ca1063d9ba1f32ea1b208c2d55d26629802860c3c4e57ec0172781d4c1d11f4f7aa1ac2f3bc9e3fd5201b966be58f484f416fc51831c61db"})
    public void testCompareNotEqualHashes_NoError_ReturnExactResult(String firstHex, String secondHex) {
        Sha3512 first = Sha3512.from(firstHex);
        Sha3512 second = Sha3512.from(secondHex);
        assertEquals(1, first.compareTo(second));
    }

    @DisplayName("Verify that constructing a new instance Sha3512 with invalid length hex hash " +
                         "should throws an Exception")
    @ParameterizedTest
    @ValueSource(strings = {"ab", "abc2434534", "abcdef123445672324"})
    public void testConstructInstanceFromHexString_InvalidHashLength_ErrorIsThrow(String hexHash) {
        assertThrows(ValidateException.InvalidLength.class, () -> Sha3512.from(hexHash));
    }

    @DisplayName("Verify that constructing a new instance Sha3512 with invalid length byte array " +
                         "should throws an Exception")
    @ParameterizedTest
    @ValueSource(strings = {"ab", "abc2434534", "abcdef123445672324"})
    public void testConstructInstanceFromByteArray_InvalidHashLength_ErrorIsThrow(String hexHash) {
        assertThrows(ValidateException.InvalidLength.class,
                () -> Sha3512.from(HEX.decode(hexHash)));
    }

    @DisplayName("Verify that constructing a new instance Sha3512 from hex hash successfully in " +
                         "happy condition")
    @ParameterizedTest
    @ValueSource(strings = {"1ae34783a48e95b70af442d2bd89e91cdbe4fb8a469b7002561a38af7f27a1f8",
            "2c599996cf7a436e62d7a3992008f70712643fa2f1bdddbf1ced940fe5e6942a",
            "acc2bfa9cd7c5ea4f704eb35e46a6814148d00eba10675118792e0d2858af2dd"})
    public void testConstructInstanceFromByteArray_ValidHash_ErrorIsNotThrow(String hexHash) {
        assertDoesNotThrow(() -> Sha3512.hash(hexHash));
    }
}
