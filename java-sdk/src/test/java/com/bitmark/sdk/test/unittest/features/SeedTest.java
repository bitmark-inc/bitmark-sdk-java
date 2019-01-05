package com.bitmark.sdk.test.unittest.features;

import com.bitmark.apiservice.configuration.Network;
import com.bitmark.apiservice.utils.ArrayUtil;
import com.bitmark.cryptography.crypto.Box;
import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.features.internal.Seed;
import com.bitmark.sdk.features.internal.SeedTwelve;
import com.bitmark.sdk.features.internal.SeedTwentyFour;
import com.bitmark.sdk.features.internal.Version;
import com.bitmark.sdk.test.unittest.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bitmark.apiservice.configuration.Network.LIVE_NET;
import static com.bitmark.apiservice.configuration.Network.TEST_NET;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hieu Pham
 * @since 9/2/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class SeedTest extends BaseTest {

    @ParameterizedTest
    @MethodSource("createValidTwelveSeedByteArrayNetwork")
    public void testConstructTwelveSeed__NewSeedInstanceIsReturn() {
        final Seed seed = new SeedTwelve();
        assertEquals(seed.length(), seed.getSeedBytes().length);
        assertEquals(NETWORK, seed.getNetwork());
    }

    @ParameterizedTest
    @MethodSource("createValidTwelveSeedByteArrayNetwork")
    public void testConstructTwelveSeedFromByteArray_ValidSeedBytes_NewSeedInstanceIsReturn(
            byte[] seedBytes, Network network) {
        final Seed seed = new SeedTwelve(seedBytes);
        assertTrue(ArrayUtil.equals(seedBytes, seed.getSeedBytes()));
        assertEquals(network, seed.getNetwork());
    }

    @ParameterizedTest
    @MethodSource("createInvalidSeedByteArray")
    public void testConstructSeedFromByteArray_InvalidSeedBytes_ErrorIsThrow(byte[] seedBytes) {
        assertThrows(ValidateException.class, () -> new SeedTwelve(seedBytes));
        assertThrows(ValidateException.class, () -> new SeedTwentyFour(seedBytes, NETWORK));
    }

    @ParameterizedTest
    @MethodSource("createValidEncodedTwelveSeedByteArraySeedNetwork")
    public void testConstructTwelveSeedFromEncodedSeed_ValidEncodedSeed_NewSeedInstanceIsReturn(
            String encodedSeed, byte[] seedBytes, Network network) {
        final Seed seed = SeedTwelve.fromEncodedSeed(encodedSeed);
        assertTrue(ArrayUtil.equals(seedBytes, seed.getSeedBytes()));
        assertEquals(network, seed.getNetwork());
    }

    @ParameterizedTest
    @MethodSource("createValidEncodedTwentyFourSeedByteArraySeedNetwork")
    public void testConstructTwentyFourSeedFromEncodedSeed_ValidEncodedSeed_NewSeedInstanceIsReturn(
            String encodedSeed, byte[] seedBytes, Network network) {
        final Seed seed = SeedTwentyFour.fromEncodedSeed(encodedSeed);
        assertTrue(ArrayUtil.equals(seedBytes, seed.getSeedBytes()));
        assertEquals(network, seed.getNetwork());
    }

    @ParameterizedTest
    @ValueSource(strings = {"5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QU2yGb1DABXZsVeD",
                            "5XEECt18HGBGNET1PpxLhy5CsCLG2yGb1DABXZsVeD",
                            "5XEECqWqA47qWg86DR5HJ29HhbVqwigHUAhgiBMqFSBycbiwnbY639sabc1"})
    public void testConstructSeedFromEncodedSeed_InvalidEncodedSeed_ErrorIsThrow(
            String encodedSeed) {
        assertThrows(ValidateException.class, () -> SeedTwelve.fromEncodedSeed(encodedSeed));
        assertThrows(ValidateException.class, () -> SeedTwentyFour.fromEncodedSeed(encodedSeed));
    }

    @Test
    public void testSeedGetter__CorrectValueReturn() {
        Seed twelve = new SeedTwelve();
        Seed twentyFour = new SeedTwentyFour(NETWORK);
        assertEquals(SeedTwelve.SEED_BYTE_LENGTH, twelve.length());
        assertEquals(SeedTwentyFour.SEED_BYTE_LENGTH, twentyFour.length());
        assertEquals(Version.TWELVE, twelve.getVersion());
        assertEquals(Version.TWENTY_FOUR, twentyFour.getVersion());
        assertEquals(Ed25519.PRIVATE_KEY_LENGTH, twelve.getAuthKeyPair().privateKey().size());
        assertEquals(Ed25519.PUBLIC_KEY_LENGTH, twelve.getAuthKeyPair().publicKey().size());
        assertEquals(Ed25519.PRIVATE_KEY_LENGTH, twentyFour.getAuthKeyPair().privateKey().size());
        assertEquals(Ed25519.PUBLIC_KEY_LENGTH, twentyFour.getAuthKeyPair().publicKey().size());
        assertEquals(Box.PRIVATE_KEY_BYTE_LENGTH, twelve.getEncKeyPair().privateKey().size());
        assertEquals(Box.PUB_KEY_BYTE_LENGTH, twelve.getEncKeyPair().publicKey().size());
        assertEquals(Box.PRIVATE_KEY_BYTE_LENGTH, twentyFour.getEncKeyPair().privateKey().size());
        assertEquals(Box.PUB_KEY_BYTE_LENGTH, twentyFour.getEncKeyPair().publicKey().size());
    }

    @ParameterizedTest
    @MethodSource("createSeedRecoveryPhrase")
    public void testTwelveSeedGetRecoveryPhrase__ValidPhraseReturn(Seed seed,
                                                                   String expectedPhrase) {
        assertEquals(expectedPhrase,
                     Arrays.stream(seed.getRecoveryPhrase(Locale.ENGLISH).getMnemonicWords())
                           .collect(
                                   Collectors.joining(" ")));
    }


    private static Stream<byte[]> createInvalidSeedByteArray() {
        return Stream.of(null, new byte[]{}, new byte[]{13, 45, 123, -23, 29, 98, -23, -34});
    }

    private static Stream<Arguments> createValidTwelveSeedByteArrayNetwork() {
        return Stream.of(Arguments.of(HEX.decode("442f54cd072a9638be4a0344e1a6e5f010"), TEST_NET),
                         Arguments.of(HEX.decode(
                                 "ba0e357d9157a1a7299fbc4cb4c933bd0"), LIVE_NET));
    }

    private static Stream<Arguments> createValidEncodedTwelveSeedByteArraySeedNetwork() {
        return Stream.of(Arguments.of("9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH",
                                      HEX.decode("442f54cd072a9638be4a0344e1a6e5f010"),
                                      TEST_NET),
                         Arguments.of("9J876mP7wDJ6g5P41eNMN8N3jo9fycDs2", HEX.decode(
                                 "3ae670cd91c5e15d0254a2abc57ba29d00"),
                                      TEST_NET));
    }

    private static Stream<Arguments> createValidEncodedTwentyFourSeedByteArraySeedNetwork() {
        return Stream.of(
                Arguments.of("5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QGF4U2yGb1DABXZsVeD",
                             HEX.decode(
                                     "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"),
                             TEST_NET));
    }

    private static Stream<Arguments> createSeedRecoveryPhrase() {
        return Stream.of(Arguments.of(new SeedTwelve(
                                              HEX.decode("442f54cd072a9638be4a0344e1a6e5f010")),
                                      "during kingdom crew atom practice brisk weird document eager artwork ride then"),
                         Arguments.of(new SeedTwentyFour(HEX.decode(
                                 "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"),
                                                         TEST_NET),
                                      "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard science"));
    }

}
