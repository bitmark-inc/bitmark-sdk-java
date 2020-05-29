/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2020 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.test.unittest.features;

import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.features.internal.*;
import com.bitmark.sdk.test.unittest.BaseTest;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.bitmark.apiservice.configuration.Network.TEST_NET;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hieu Pham
 * @since 5/29/20
 * Email: hieupham@bitmark.com
 */
public class BCRecoveryPhraseTest extends BaseTest {

    @Test
    public void testConstructFromSeed() {
        assertThrows(
                ValidateException.class,
                () -> BCRecoveryPhrase.fromSeed(new SeedTwentyFour(
                        HEX.decode(
                                "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"),
                        TEST_NET
                ))
        );
        assertDoesNotThrow(() -> BCRecoveryPhrase.fromSeed(new SeedTwelve(
                HEX.decode(
                        "442f54cd072a9638be4a0344e1a6e5f010")
        )));
    }

    @ParameterizedTest
    @MethodSource("provideValidSeedWords")
    public void testFromSeed(Seed seed, String[] words) {
        BCRecoveryPhrase phrase = BCRecoveryPhrase.fromSeed(seed);
        assertTrue(Arrays.deepEquals(words, phrase.getMnemonicWords()));
        assertEquals(13, words.length);
    }

    private static Stream<Arguments> provideValidSeedWords() {
        return Stream.of(
                Arguments.of(
                        SeedTwelve.fromEncodedSeed(
                                "9J876mP7wDJ6g5P41eNMN8N3jo9fycDs2"),
                        "depend crime cricket castle fun purse announce nephew profit cloth trim deliver august"
                                .split(" ")
                ),
                Arguments.of(
                        SeedTwelve.fromEncodedSeed(
                                "9J878SbnM2GFqAELkkiZbqHJDkAj57fYK"),
                        "file earn crack fever crack differ wreck crazy salon imitate swamp sample autumn"
                                .split(" ")
                ),
                Arguments.of(
                        SeedTwelve.fromEncodedSeed(
                                "9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH"),
                        "during kingdom crew atom practice brisk weird document eager artwork ride then area"
                                .split(" ")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidSeedWords")
    public void testRecoverSeed(Seed seed, String[] words) {
        Seed actualSeed = BCRecoveryPhrase.recoverSeed(words);
        assertEquals(seed.getEncodedSeed(), actualSeed.getEncodedSeed());
        assertSame(Version.TWELVE, actualSeed.getVersion());
    }

    @ParameterizedTest
    @MethodSource("provideValidBytesWords")
    public void testGenerateMnemonicWords(byte[] secret, String[] words) {
        String[] actualWords = BCRecoveryPhrase.generateMnemonic(secret);
        assertTrue(Arrays.deepEquals(words, actualWords));
    }

    private static Stream<Arguments> provideValidBytesWords() {
        return Stream.of(
                Arguments.of(
                        HEX.decode("3ae670cd91c5e15d0254a2abc57ba29d00"),
                        "depend crime cricket castle fun purse announce nephew profit cloth trim deliver august"
                                .split(" ")
                ),
                Arguments.of(
                        HEX.decode("5628a8c72ab31c7bbf8996be8e2f6cdf80"),
                        "file earn crack fever crack differ wreck crazy salon imitate swamp sample autumn"
                                .split(" ")
                ),
                Arguments.of(
                        HEX.decode("442f54cd072a9638be4a0344e1a6e5f010"),
                        "during kingdom crew atom practice brisk weird document eager artwork ride then area"
                                .split(" ")
                )
        );
    }
}
