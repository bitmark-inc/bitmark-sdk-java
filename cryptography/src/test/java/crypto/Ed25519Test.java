package crypto;

import crypto.key.KeyPair;
import error.ValidateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hieu Pham
 * @since 8/24/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Ed25519Test extends BaseCryptoTest {

    @DisplayName("Verify that Ed25519.generateKeyPair() works well without unexpected exception with a happy condition")
    @Test
    public void testGenerateKeyPair_NoError_ValidKeyPairIsReturn() {
        KeyPair pair = Ed25519.generateKeyPair();
        assertEquals(pair.privateKey().size(), Ed25519.PRIVATE_KEY_LENGTH);
        assertEquals(pair.publicKey().size(), Ed25519.PUBLIC_KEY_LENGTH);
    }

    @DisplayName("Verify that Ed25519.generateKeyPairFromSeed(byte[]) works well with happy condition")
    @ParameterizedTest
    @ValueSource(strings = {"f2b15c7200965a7e6827f80749657a3fe655980c1f03f2ecb475cfcf31f6ca31",
            "d9b7a41748db4da24f7dadc0d0a8c87e6cd1c2c754239e5009ff06fe163d6e34",
            "731ea25f9b40fe5ff88c8a35061ecc35e8f9958098c97e9eb523cda97485277b"})
    public void testGenerateKeyPairFromSeed_ValidSeed_ValidKeyPairIsReturn(String seedHex) {
        KeyPair pair = Ed25519.generateKeyPairFromSeed(HEX.decode(seedHex));
        assertEquals(pair.privateKey().size(), Ed25519.PRIVATE_KEY_LENGTH);
        assertEquals(pair.publicKey().size(), Ed25519.PUBLIC_KEY_LENGTH);
    }

    @DisplayName("Verify that Ed25519.generateKeyPairFromSeed(byte[]) throws an exception when the seed is invalid")
    @ParameterizedTest
    @ValueSource(strings = {"f2b15c7200965a7e6827f80749657a3fe655980c1f03f2ecb475cfcf31f6ca",
            "d9b7a41748db4da24f7dadc0d0a8c87e6c",
            "731ea25f"})
    public void testGenerateKeyPairFromSeed_InvalidSeedLength_ErrorIsThrow(String seedHex) {
        assertThrows(ValidateException.InvalidLength.class, () -> Ed25519.generateKeyPairFromSeed(HEX.decode(seedHex)));
    }

    @DisplayName("Verify that Ed25519.getKeyPair(byte[]) works well with happy condition")
    @ParameterizedTest
    @CsvSource({"c65246785ff374c03de0171019df4ac977174bf8db6f89a944246132a967f49e, f2b15c7200965a7e6827f80749657a3fe655980c1f03f2ecb475cfcf31f6ca31c65246785ff374c03de0171019df4ac977174bf8db6f89a944246132a967f49e",
            "d3df923eeae7825550d1de4f1cc2d21724c6f77e8c07dc5a082908eb8d0e94fc, d9b7a41748db4da24f7dadc0d0a8c87e6cd1c2c754239e5009ff06fe163d6e34d3df923eeae7825550d1de4f1cc2d21724c6f77e8c07dc5a082908eb8d0e94fc",
            "ff79e97c179d48e82c64c1c820f907ead2113209aeff9a0b78a4f0c09357907b, 731ea25f9b40fe5ff88c8a35061ecc35e8f9958098c97e9eb523cda97485277bff79e97c179d48e82c64c1c820f907ead2113209aeff9a0b78a4f0c09357907b"})
    public void testGetKeyPairFromPrivateKey_ValidPrivateKey_ValidKeyPairIsReturn(String publicKeyHex, String privateKeyHex) {
        final byte[] privateKey = HEX.decode(privateKeyHex);
        final byte[] publicKey = HEX.decode(publicKeyHex);
        KeyPair pair = Ed25519.getKeyPair(privateKey);
        assertTrue(Arrays.equals(publicKey, pair.publicKey().toBytes()));
    }

    @DisplayName("Verify that Ed25519.getKeyPair(byte[]) throws an exception when the private key is invalid")
    @ParameterizedTest
    @ValueSource(strings = {"c65246785ff374c03de0171019df4ac977174bf8db6f89a944246132a967f49e",
            "d3df923eeae7825550d1de4f1cc2d21724c6f77e8c07dc5a082908eb8d0e94fc",
            "ff79e97c179d48e82c64c1c820f907ead2113209aeff9a0b78a4f0c09357907b"})
    public void testGetKeyPairFromPrivateKey_InvalidPrivateKey_ErrorIsThrow(String privateKeyHex) {
        assertThrows(ValidateException.InvalidLength.class, () -> Ed25519.getKeyPair(HEX.decode(privateKeyHex)));
    }

    @DisplayName("Verify that Ed25519.getSeed(byte[]) works well with the happy condition")
    @ParameterizedTest
    @CsvSource({"f2b15c7200965a7e6827f80749657a3fe655980c1f03f2ecb475cfcf31f6ca31c65246785ff374c03de0171019df4ac977174bf8db6f89a944246132a967f49e, f2b15c7200965a7e6827f80749657a3fe655980c1f03f2ecb475cfcf31f6ca31",
            "d9b7a41748db4da24f7dadc0d0a8c87e6cd1c2c754239e5009ff06fe163d6e34d3df923eeae7825550d1de4f1cc2d21724c6f77e8c07dc5a082908eb8d0e94fc, d9b7a41748db4da24f7dadc0d0a8c87e6cd1c2c754239e5009ff06fe163d6e34",
            "731ea25f9b40fe5ff88c8a35061ecc35e8f9958098c97e9eb523cda97485277bff79e97c179d48e82c64c1c820f907ead2113209aeff9a0b78a4f0c09357907b, 731ea25f9b40fe5ff88c8a35061ecc35e8f9958098c97e9eb523cda97485277b"})
    public void testGetSeedFromPrivateKey_ValidPrivateKey_ValidSeedIsReturn(String privateKeyHex, String seedHex) {
        final byte[] privateKey = HEX.decode(privateKeyHex);
        final byte[] seed = HEX.decode(seedHex);
        final byte[] expectedSeed = Ed25519.getSeed(privateKey);
        assertTrue(Arrays.equals(expectedSeed, seed));
    }

    @DisplayName("Verify that Ed25519.getSeed(byte[]) throws an exception when the private key is invalid")
    @ParameterizedTest
    @ValueSource(strings = {"f2b15c7200965a7e6827f80749657a3fe655980c1f03f2ecb475", "d9b7a41748db4da24f7dad", "731ea25f9b"})
    public void testGetSeedFromPrivateKey_InvalidPrivateKey_ErrorIsThrow(String privateKeyHex) {
        assertThrows(ValidateException.InvalidLength.class, () -> Ed25519.getSeed(HEX.decode(privateKeyHex)));
    }

    @DisplayName("Verify that Ed25519.getSeed(String) works well with the happy condition")
    @ParameterizedTest
    @CsvSource({"f2b15c7200965a7e6827f80749657a3fe655980c1f03f2ecb475cfcf31f6ca31c65246785ff374c03de0171019df4ac977174bf8db6f89a944246132a967f49e, f2b15c7200965a7e6827f80749657a3fe655980c1f03f2ecb475cfcf31f6ca31",
            "d9b7a41748db4da24f7dadc0d0a8c87e6cd1c2c754239e5009ff06fe163d6e34d3df923eeae7825550d1de4f1cc2d21724c6f77e8c07dc5a082908eb8d0e94fc, d9b7a41748db4da24f7dadc0d0a8c87e6cd1c2c754239e5009ff06fe163d6e34",
            "731ea25f9b40fe5ff88c8a35061ecc35e8f9958098c97e9eb523cda97485277bff79e97c179d48e82c64c1c820f907ead2113209aeff9a0b78a4f0c09357907b, 731ea25f9b40fe5ff88c8a35061ecc35e8f9958098c97e9eb523cda97485277b"})
    public void testGetSeedFromHexPrivateKey_ValidHexPrivateKey_ValidSeedIsReturn(String hexPrivateKey, String hexSeed) {
        String expectedSeed = Ed25519.getSeed(hexPrivateKey);
        assertTrue(hexSeed.equalsIgnoreCase(expectedSeed));
    }

    @DisplayName("Verify that Ed25519.getSeed(String) throws an exception when the hex private key is invalid")
    @ParameterizedTest
    @ValueSource(strings = {"f2b15c7200965a7e6827f80749657a3fe655980c1f03f2ecb475", "d9b7a41748db4da24f7dad", "731ea25f9b"})
    public void testGetSeedFromHexPrivateKey_InvalidHexPrivateKey_ErrorIsThrow(String privateKeyHex) {
        assertThrows(ValidateException.InvalidLength.class, () -> Ed25519.getSeed(privateKeyHex));
    }

    @DisplayName("Verify that Ed25519.sign(byte[], byte[]) works well with happy condition")
    @ParameterizedTest
    @CsvSource({"4269746d61726b53444b, f2b15c7200965a7e6827f80749657a3fe655980c1f03f2ecb475cfcf31f6ca31c65246785ff374c03de0171019df4ac977174bf8db6f89a944246132a967f49e, 51c7cf9e80ee62418189a6a323e4a994bceb1108c7e9ce61d74d3179128aa8de3681e00f3f17f23de34e0c97882103d20f27cf641863d1c903a3a4a5bd9fed05",
            "4a61766153444b, d9b7a41748db4da24f7dadc0d0a8c87e6cd1c2c754239e5009ff06fe163d6e34d3df923eeae7825550d1de4f1cc2d21724c6f77e8c07dc5a082908eb8d0e94fc, c0ff03170acfc2437fe2a5c6950fbd7f037da5e34e5396276815503da34b8f2cb0cce5af8baa3c928bdf4a475ac5e31e61fed7553f14580d9acbe5529a5d6007",
            "4269746d61726b496e546865467574757265, 731ea25f9b40fe5ff88c8a35061ecc35e8f9958098c97e9eb523cda97485277bff79e97c179d48e82c64c1c820f907ead2113209aeff9a0b78a4f0c09357907b, 3819ca546280ed55a26675fe7469caaa15388dbe5656c49cd21c74a749739123730ed23618c15934bbf32367df1410b4538ffbdeddb47e75d377fd47ef1d1905"})
    public void testSignMessageByByteArrayPrivateKey_ValidByteArrayPrivateKey_ValidSignatureIsReturn(String hexMessage, String hexPrivateKey, String hexSignature) {
        final byte[] message = HEX.decode(hexMessage);
        final byte[] privateKey = HEX.decode(hexPrivateKey);
        final byte[] signature = Ed25519.sign(message, privateKey);
        assertTrue(hexSignature.equalsIgnoreCase(HEX.encode(signature)));
    }

    @DisplayName("Verify that Ed25519.sign(byte[], byte[]) throws an exception when the private key is invalid")
    @ParameterizedTest
    @CsvSource({"4269746d61726b53444b, f2b15c72f00965a7e6827f80749657a3fe655980c1f03f2ecb47abc1234251234bcfdacbcafefeaefcbfeadcdfebcbcdeaf56734523643456235436436bc2353634cdef23234a234abbbba235235124235",
            "4a61766153444b, d9b7a41748db4da24f7dad",
            "4269746d61726b496e546865467574757265, 745a"})
    public void testSignMessageByByteArrayPrivateKey_InvalidByteArrayPrivateKey_ErrorIsThrow(String hexMessage, String hexPrivateKey) {
        final byte[] message = HEX.decode(hexMessage);
        final byte[] privateKey = HEX.decode(hexPrivateKey);
        assertThrows(ValidateException.InvalidLength.class, () -> Ed25519.sign(message, privateKey));
    }

    @DisplayName("Verify that Ed25519.sign(String, String) works well with happy condition")
    @ParameterizedTest
    @CsvSource({"4269746d61726b53444b, f2b15c7200965a7e6827f80749657a3fe655980c1f03f2ecb475cfcf31f6ca31c65246785ff374c03de0171019df4ac977174bf8db6f89a944246132a967f49e, 51c7cf9e80ee62418189a6a323e4a994bceb1108c7e9ce61d74d3179128aa8de3681e00f3f17f23de34e0c97882103d20f27cf641863d1c903a3a4a5bd9fed05",
            "4a61766153444b, d9b7a41748db4da24f7dadc0d0a8c87e6cd1c2c754239e5009ff06fe163d6e34d3df923eeae7825550d1de4f1cc2d21724c6f77e8c07dc5a082908eb8d0e94fc, c0ff03170acfc2437fe2a5c6950fbd7f037da5e34e5396276815503da34b8f2cb0cce5af8baa3c928bdf4a475ac5e31e61fed7553f14580d9acbe5529a5d6007",
            "4269746d61726b496e546865467574757265, 731ea25f9b40fe5ff88c8a35061ecc35e8f9958098c97e9eb523cda97485277bff79e97c179d48e82c64c1c820f907ead2113209aeff9a0b78a4f0c09357907b, 3819ca546280ed55a26675fe7469caaa15388dbe5656c49cd21c74a749739123730ed23618c15934bbf32367df1410b4538ffbdeddb47e75d377fd47ef1d1905"})
    public void testSignMessageByHexPrivateKey_ValidHexPrivateKey_ValidSignatureIsReturn(String message, String privateKey, String signature) {
        final String expectedSignature = Ed25519.sign(message, privateKey);
        assertTrue(expectedSignature.equalsIgnoreCase(signature));
    }

    @DisplayName("Verify that Ed25519.sign(String, String) throws an exception when the hex private key is invalid")
    @ParameterizedTest
    @CsvSource({"4269746d61726b53444b, f2b15c72f00965a7e6827f80749657a3fe655980c1f03f2ecb47abc1234251234bcfdacbcafefeaefcbfeadcdfebcbcdeaf56734523643456235436436bc2353634cdef23234a234abbbba235235124235",
            "4a61766153444b, d9b7a41748db4da24f7dad",
            "4269746d61726b496e546865467574757265, 745a"})
    public void testSignMessageByHexPrivateKey_InvalidHexPrivateKey_ErrorIsThrow(String message, String privateKey) {
        assertThrows(ValidateException.InvalidLength.class, () -> Ed25519.sign(message, privateKey));
    }

    @DisplayName("Verify that Ed25519.verify(byte[], byte[], byte[]) works well with happy condition")
    @ParameterizedTest
    @CsvSource({"4269746d61726b53444b, c65246785ff374c03de0171019df4ac977174bf8db6f89a944246132a967f49e, 51c7cf9e80ee62418189a6a323e4a994bceb1108c7e9ce61d74d3179128aa8de3681e00f3f17f23de34e0c97882103d20f27cf641863d1c903a3a4a5bd9fed05",
            "4a61766153444b, d3df923eeae7825550d1de4f1cc2d21724c6f77e8c07dc5a082908eb8d0e94fc, c0ff03170acfc2437fe2a5c6950fbd7f037da5e34e5396276815503da34b8f2cb0cce5af8baa3c928bdf4a475ac5e31e61fed7553f14580d9acbe5529a5d6007",
            "4269746d61726b496e546865467574757265, ff79e97c179d48e82c64c1c820f907ead2113209aeff9a0b78a4f0c09357907b, 3819ca546280ed55a26675fe7469caaa15388dbe5656c49cd21c74a749739123730ed23618c15934bbf32367df1410b4538ffbdeddb47e75d377fd47ef1d1905"})
    public void testVerifySignatureByByteArrayPublicKey_ValidByteArrayPublicKey_ValidResultIsReturn(String hexMessage, String hexPublicKey, String hexSignature) {
        final byte[] message = HEX.decode(hexMessage);
        final byte[] publicKey = HEX.decode(hexPublicKey);
        final byte[] signature = HEX.decode(hexSignature);
        assertTrue(Ed25519.verify(signature, message, publicKey));
    }

    @DisplayName("Verify that Ed25519.verify(byte[], byte[], byte[]) throws exception when one of the params is invalid")
    @ParameterizedTest
    @CsvSource({"4269746d61726b53444b, c65246785ff374c03de0171019df4ac977174bf8db6f89a944246132a967f49e, 51c7cf9e3e4a994bceb108c7e9ce61d74d3179128aa8de3681e00f3f17f23de34e0c97882103d20f27cf641863d1c903a3a4a5bd9fed05",
            "4a61766153444b, d3df923eeae7825550d1de4f1cc2d21724c6f77e8ceb8d0e94fc, c0ff03170acfc2437fe2a5c6950fbd7f037da5e34e5396276815503da34b8f2cb0cce5af8baa3c928bdf4a475ac5e31e61fed7553f14580d9acbe5529a5d6007",
            "4269746d61726b496e546865467574757265, ff79e97c179d48e82c64c1a4f0c09357907b, 3819ca546280ed55a26675fe7469caaa3618c15934bbf32367df1410b4538ffbdeddb47e75d377fd47ef1d1905"})
    public void testVerifySignatureByByteArrayPublicKey_InvalidPublicKey_ErrorIsThrow(String hexMessage, String hexPublicKey, String hexSignature) {
        final byte[] message = HEX.decode(hexMessage);
        final byte[] publicKey = HEX.decode(hexPublicKey);
        final byte[] signature = HEX.decode(hexSignature);
        assertThrows(ValidateException.InvalidLength.class, () -> Ed25519.verify(signature, message, publicKey));
    }

    @DisplayName("Verify that Ed25519.verify(String, String, String) works well with happy condition")
    @ParameterizedTest
    @CsvSource({"4269746d61726b53444b, c65246785ff374c03de0171019df4ac977174bf8db6f89a944246132a967f49e, 51c7cf9e80ee62418189a6a323e4a994bceb1108c7e9ce61d74d3179128aa8de3681e00f3f17f23de34e0c97882103d20f27cf641863d1c903a3a4a5bd9fed05",
            "4a61766153444b, d3df923eeae7825550d1de4f1cc2d21724c6f77e8c07dc5a082908eb8d0e94fc, c0ff03170acfc2437fe2a5c6950fbd7f037da5e34e5396276815503da34b8f2cb0cce5af8baa3c928bdf4a475ac5e31e61fed7553f14580d9acbe5529a5d6007",
            "4269746d61726b496e546865467574757265, ff79e97c179d48e82c64c1c820f907ead2113209aeff9a0b78a4f0c09357907b, 3819ca546280ed55a26675fe7469caaa15388dbe5656c49cd21c74a749739123730ed23618c15934bbf32367df1410b4538ffbdeddb47e75d377fd47ef1d1905"})
    public void testVerifySignatureByHexPublicKey_ValidHexPublicKey_ValidResultIsReturn(String message, String publicKey, String signature) {
        assertTrue(Ed25519.verify(signature, message, publicKey));
    }

    @DisplayName("Verify that Ed25519.verify(String, String, String) throws an exception when one of the params is invalid")
    @ParameterizedTest
    @CsvSource({"4269746d61726b53444b, c65246785ff374c03de0171019df4ac977174bf8db6f89a944246132a967f49e, 51c7cf9e3e4a994bceb108c7e9ce61d74d3179128aa8de3681e00f3f17f23de34e0c97882103d20f27cf641863d1c903a3a4a5bd9fed05",
            "4a61766153444b, d3df923eeae7825550d1de4f1cc2d21724c6f77e8ceb8d0e94fc, c0ff03170acfc2437fe2a5c6950fbd7f037da5e34e5396276815503da34b8f2cb0cce5af8baa3c928bdf4a475ac5e31e61fed7553f14580d9acbe5529a5d6007",
            "4269746d61726b496e546865467574757265, ff79e97c179d48e82c64c1a4f0c09357907b, 3819ca546280ed55a26675fe7469caaa3618c15934bbf32367df1410b4538ffbdeddb47e75d377fd47ef1d1905"})
    public void testVerifySignatureByHexPublicKey_InvalidHexPublicKey_ErrorIsThrow(String message, String publicKey, String signature) {
        assertThrows(ValidateException.InvalidLength.class, () -> Ed25519.verify(signature, message, publicKey));
    }


}
