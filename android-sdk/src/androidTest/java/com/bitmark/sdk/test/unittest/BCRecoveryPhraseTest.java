/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2020 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.test.unittest;

import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.features.internal.*;
import com.bitmark.sdk.test.BaseTest;
import org.junit.Test;

import java.util.Arrays;

import static com.bitmark.apiservice.configuration.Network.TEST_NET;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.sdk.test.utils.TestUtils.assertDoesNotThrow;
import static com.bitmark.sdk.test.utils.TestUtils.assertThrows;
import static org.junit.Assert.*;

/**
 * @author Hieu Pham
 * @since 5/29/20
 * Email: hieupham@bitmark.com
 */
public class BCRecoveryPhraseTest extends BaseTest {

    @Test
    public void testConstructFromSeed() {
        assertThrows("", ValidateException.class, () ->
                BCRecoveryPhrase.fromSeed(
                        new SeedTwentyFour(
                                HEX.decode("7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"), TEST_NET
                        )));
        assertDoesNotThrow(() -> BCRecoveryPhrase.fromSeed(new SeedTwelve(
                HEX.decode("442f54cd072a9638be4a0344e1a6e5f010")
        )));
    }

    @Test
    public void testFromSeed() {
        Seed seed = SeedTwelve.fromEncodedSeed("9J876mP7wDJ6g5P41eNMN8N3jo9fycDs2");
        String[] words = "depend crime cricket castle fun purse announce nephew profit cloth trim deliver august".split(" ");

        BCRecoveryPhrase phrase = BCRecoveryPhrase.fromSeed(seed);
        assertTrue(Arrays.deepEquals(words, phrase.getMnemonicWords()));
        assertEquals(13, words.length);
    }

    @Test
    public void testRecoverSeed() {
        Seed seed = SeedTwelve.fromEncodedSeed("9J876mP7wDJ6g5P41eNMN8N3jo9fycDs2");
        String[] words = "depend crime cricket castle fun purse announce nephew profit cloth trim deliver august".split(" ");

        Seed actualSeed = BCRecoveryPhrase.recoverSeed(words);
        assertEquals(seed.getEncodedSeed(), actualSeed.getEncodedSeed());
        assertSame(Version.TWELVE, actualSeed.getVersion());
    }

    @Test
    public void testGenerateMnemonicWords() {
        byte[] secret = HEX.decode("3ae670cd91c5e15d0254a2abc57ba29d00");
        String[] words = "depend crime cricket castle fun purse announce nephew profit cloth trim deliver august".split(" ");
        String[] actualWords = BCRecoveryPhrase.generateMnemonic(secret);
        assertTrue(Arrays.deepEquals(words, actualWords));
    }
}
