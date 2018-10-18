package sdk.test.unittest.utils;

import apiservice.configuration.Network;
import apiservice.utils.ArrayUtil;
import cryptography.error.ValidateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import sdk.test.unittest.BaseTest;
import sdk.utils.Seed;

import java.util.stream.Stream;

import static apiservice.configuration.Network.LIVE_NET;
import static apiservice.configuration.Network.TEST_NET;
import static cryptography.crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hieu Pham
 * @since 9/2/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class SeedTest extends BaseTest {

    @DisplayName("Verify function new Seed(byte[]) works well with happy condition")
    @ParameterizedTest
    @MethodSource("createValidByteArrayNetwork")
    public void testConstructSeedFromByteArray_ValidSeedBytes_NewSeedInstanceIsReturn(byte[] seedBytes, Network network) {
        final Seed seed = new Seed(seedBytes);
        Assertions.assertTrue(ArrayUtil.equals(seedBytes, seed.getSeed()));
        assertEquals(network, seed.getNetwork());
    }

    @DisplayName("Verify function new Seed(byte[]) throws exception with invalid byte array")
    @ParameterizedTest
    @MethodSource("createInvalidByteArray")
    public void testConstructSeedFromByteArray_NullSeedBytes_ErrorIsThrow(byte[] seedBytes) {
        assertThrows(ValidateException.class, () -> new Seed(seedBytes));
    }

    @DisplayName("Verify function new Seed(byte[], Network) works well with happy condition")
    @ParameterizedTest
    @MethodSource("createValidByteArrayNetwork")
    public void testConstructSeedFromByteArrayNetwork_ValidSeedBytesAndNetwork_NewSeedInstanceIsReturn(byte[] seedBytes, Network network) {
        final Seed seed = new Seed(seedBytes, network);
        assertEquals(seedBytes, seed.getSeed());
        assertEquals(network, seed.getNetwork());
    }

    @DisplayName("Verify function new Seed(byte[], Network) throws exception with invalid network")
    @ParameterizedTest
    @MethodSource("createByteArrayInvalidNetwork")
    public void testConstructSeedFromByteArrayNetwork_InvalidNetwork_ErrorIsThrow(byte[] seedBytes, Network network) {
        assertThrows(Exception.class, () -> new Seed(seedBytes, network));
    }

    @DisplayName("Verify function Seed.fromEncodedSeed(String) works well with happy condition")
    @ParameterizedTest
    @MethodSource("createValidEncodedSeedByteArrayNetwork")
    public void testConstructSeedFromEncodedSeed_ValidEncodedSeed_NewSeedInstanceIsReturn(String encodedSeed, byte[] seedBytes, Network network) {
        final Seed seed = Seed.fromEncodedSeed(encodedSeed);
        assertTrue(ArrayUtil.equals(seedBytes, seed.getSeed()));
        assertEquals(network, seed.getNetwork());
    }

    @DisplayName("Verify function Seed.fromEncodedSeed(String) throws exception with invalid " +
                         "network")
    @ParameterizedTest
    @ValueSource(strings = {"5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QU2yGb1DABXZsVeD",
            "5XEECt18HGBGNET1PpxLhy5CsCLG2yGb1DABXZsVeD"})
    public void testConstructSeedFromEncodedSeed_InvalidEncodedSeed_ErrorIsThrow(String encodedSeed) {
        assertThrows(ValidateException.class, () -> Seed.fromEncodedSeed(encodedSeed));
    }

    private static Stream<byte[]> createInvalidByteArray() {
        return Stream.of(null, new byte[]{}, new byte[]{13, 45, 123, -23, 29, 98, -23, -34});
    }

    private static Stream<Arguments> createValidByteArrayNetwork() {
        return Stream.of(Arguments.of(HEX.decode("442f54cd072a9638be4a0344e1a6e5f010"), TEST_NET)
                , Arguments.of(HEX.decode("ba0e357d9157a1a7299fbc4cb4c933bd00"), LIVE_NET));
    }

    private static Stream<Arguments> createByteArrayInvalidNetwork() {
        return Stream.of(Arguments.of(HEX.decode("442f54cd072a9638be4a0344e1a6e5f010"), null),
                Arguments.of(HEX.decode("ba0e357d9157a1a7299fbc4cb4c933bd00"), null),
                Arguments.of(HEX.decode("442f54cd072a9638be4a0344e1a6e5f010"), LIVE_NET));
    }

    private static Stream<Arguments> createValidEncodedSeedByteArrayNetwork() {
        return Stream.of(Arguments.of("9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH",
                HEX.decode("442f54cd072a9638be4a0344e1a6e5f010"),
                TEST_NET),
                Arguments.of("9J876mP7wDJ6g5P41eNMN8N3jo9fycDs2", HEX.decode(
                        "3ae670cd91c5e15d0254a2abc57ba29d00"),
                        TEST_NET));
    }


}
