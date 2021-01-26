/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.test.unittest;

import com.bitmark.apiservice.utils.ArrayUtil;
import com.bitmark.cryptography.crypto.Box;
import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.features.internal.Seed;
import com.bitmark.sdk.features.internal.SeedTwelve;
import com.bitmark.sdk.features.internal.SeedTwentyFour;
import com.bitmark.sdk.features.internal.Version;
import com.bitmark.sdk.test.BaseTest;
import com.bitmark.sdk.utils.StringUtils;
import org.junit.Test;

import java.util.Locale;

import static com.bitmark.apiservice.configuration.Network.TEST_NET;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.sdk.test.utils.TestUtils.assertThrows;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SeedTest extends BaseTest {

    @Test
    public void testConstructTwelveSeed__NewSeedInstanceIsReturn() {
        final Seed seed = new SeedTwelve();
        assertEquals(seed.length(), seed.getSeedBytes().length);
        assertEquals(TEST_NET, seed.getNetwork());
    }

    @Test
    public void testConstructTwelveSeedFromByteArray_ValidSeedBytes_NewSeedInstanceIsReturn() {
        byte[] seedBytes = HEX.decode("442f54cd072a9638be4a0344e1a6e5f010");
        final Seed seed = new SeedTwelve(seedBytes);
        assertTrue(ArrayUtil.equals(seedBytes, seed.getSeedBytes()));
        assertEquals(TEST_NET, seed.getNetwork());
    }

    @Test
    public void testConstructSeedFromByteArray_InvalidSeedBytes_ErrorIsThrow() {
        byte[][] seedBytesArray = new byte[][]{
                null,
                new byte[]{},
                new byte[]{13, 45, 123, -23, 29, 98, -23, -34}
        };

        for (byte[] seedBytes : seedBytesArray) {
            assertThrows("", ValidateException.class, () -> new SeedTwelve(seedBytes));
            assertThrows("",
                    ValidateException.class,
                    () -> new SeedTwentyFour(seedBytes, TEST_NET)
            );
        }
    }

    @Test
    public void testConstructTwelveSeedFromEncodedSeed_ValidEncodedSeed_NewSeedInstanceIsReturn() {
        String encodedSeed = "9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH";
        byte[] seedBytes = HEX.decode("442f54cd072a9638be4a0344e1a6e5f010");
        final Seed seed = SeedTwelve.fromEncodedSeed(encodedSeed);
        assertTrue(ArrayUtil.equals(seedBytes, seed.getSeedBytes()));
        assertEquals(TEST_NET, seed.getNetwork());
    }

    @Test
    public void testConstructTwentyFourSeedFromEncodedSeed_ValidEncodedSeed_NewSeedInstanceIsReturn() {
        String encodedSeed = "5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QGF4U2yGb1DABXZsVeD";
        byte[] seedBytes = HEX.decode("7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607");
        final Seed seed = SeedTwentyFour.fromEncodedSeed(encodedSeed);
        assertTrue(ArrayUtil.equals(seedBytes, seed.getSeedBytes()));
        assertEquals(TEST_NET, seed.getNetwork());
    }

    @Test
    public void testConstructSeedFromEncodedSeed_InvalidEncodedSeed_ErrorIsThrow() {
        String[] encodedSeeds = new String[]{
                "5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QU2yGb1DABXZsVeD",
                "5XEECt18HGBGNET1PpxLhy5CsCLG2yGb1DABXZsVeD",
                "5XEECqWqA47qWg86DR5HJ29HhbVqwigHUAhgiBMqFSBycbiwnbY639sabc1"
        };

        for (String encodedSeed : encodedSeeds) {
            assertThrows("",
                    ValidateException.class,
                    () -> SeedTwelve.fromEncodedSeed(encodedSeed)
            );
            assertThrows("",
                    ValidateException.class,
                    () -> SeedTwentyFour.fromEncodedSeed(encodedSeed)
            );
        }
    }

    @Test
    public void testSeedGetter__CorrectValueReturn() {
        Seed twelve = new SeedTwelve();
        Seed twentyFour = new SeedTwentyFour(TEST_NET);
        assertEquals(SeedTwelve.SEED_BYTE_LENGTH, twelve.length());
        assertEquals(SeedTwentyFour.SEED_BYTE_LENGTH, twentyFour.length());
        assertEquals(Version.TWELVE, twelve.getVersion());
        assertEquals(Version.TWENTY_FOUR, twentyFour.getVersion());
        assertEquals(
                Ed25519.PRIVATE_KEY_LENGTH,
                twelve.getAuthKeyPair().privateKey().size()
        );
        assertEquals(
                Ed25519.PUBLIC_KEY_LENGTH,
                twelve.getAuthKeyPair().publicKey().size()
        );
        assertEquals(
                Ed25519.PRIVATE_KEY_LENGTH,
                twentyFour.getAuthKeyPair().privateKey().size()
        );
        assertEquals(
                Ed25519.PUBLIC_KEY_LENGTH,
                twentyFour.getAuthKeyPair().publicKey().size()
        );
        assertEquals(
                Box.PRIVATE_KEY_BYTE_LENGTH,
                twelve.getEncKeyPair().privateKey().size()
        );
        assertEquals(
                Box.PUB_KEY_BYTE_LENGTH,
                twelve.getEncKeyPair().publicKey().size()
        );
        assertEquals(
                Box.PRIVATE_KEY_BYTE_LENGTH,
                twentyFour.getEncKeyPair().privateKey().size()
        );
        assertEquals(
                Box.PUB_KEY_BYTE_LENGTH,
                twentyFour.getEncKeyPair().publicKey().size()
        );
    }

    @Test
    public void testTwelveSeedGetRecoveryPhrase__ValidPhraseReturn() {
        Seed seed = new SeedTwelve(HEX.decode("442f54cd072a9638be4a0344e1a6e5f010"));
        String words = "during kingdom crew atom practice brisk weird document eager artwork ride then";
        assertEquals(
                words,
                StringUtils.join(" ", seed.getRecoveryPhrase(Locale.ENGLISH).getMnemonicWords())
        );
    }

}
