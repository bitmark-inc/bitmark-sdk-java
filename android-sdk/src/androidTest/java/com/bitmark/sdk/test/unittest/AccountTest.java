/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.test.unittest;

import com.bitmark.apiservice.configuration.Network;
import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.crypto.Random;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.internal.BCRecoveryPhrase;
import com.bitmark.sdk.features.internal.Seed;
import com.bitmark.sdk.features.internal.SeedTwelve;
import com.bitmark.sdk.features.internal.SeedTwentyFour;
import com.bitmark.sdk.test.BaseTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.sdk.test.utils.TestUtils.assertDoesNotThrow;
import static com.bitmark.sdk.test.utils.TestUtils.assertThrows;
import static org.junit.Assert.*;

public class AccountTest extends BaseTest {

    @Test
    public void testNewAccount_NoCondition_ValidAccountIsCreated() {
        final Account account = new Account();
        assertEquals(
                account.getAuthKeyPair().privateKey().size(),
                Ed25519.PRIVATE_KEY_LENGTH
        );
        assertEquals(
                account.getAuthKeyPair().publicKey().size(),
                Ed25519.PUBLIC_KEY_LENGTH
        );
        assertNotNull(account.getAccountNumber());
        assertTrue(Account.isValidAccountNumber(account.getAccountNumber()));
    }

    @Test
    public void testNewAccountFromSeed_ValidSeed_ValidAccountIsCreated() {
        Seed seed = SeedTwentyFour.fromEncodedSeed("5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QGF4U2yGb1DABXZsVeD");
        String accountNumber = "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva";
        String publicKey = "58760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f";

        final Account account = Account.fromSeed(seed);
        assertEquals(account.getAccountNumber(), accountNumber);
        assertEquals(
                HEX.encode(account.getAuthKeyPair().publicKey().toBytes()),
                publicKey
        );
    }

    @Test
    public void testNewAccountFromEncodedSeed_ValidSeed_ValidAccountIsCreated() {
        String seed = "5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QGF4U2yGb1DABXZsVeD";
        String accountNumber = "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva";
        String publicKey = "58760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f";
        final Account account = Account.fromSeed(seed);
        assertEquals(account.getAccountNumber(), accountNumber);
        assertEquals(
                HEX.encode(account.getAuthKeyPair().publicKey().toBytes()),
                publicKey
        );
    }

    @Test
    public void testNewAccountFromSeed_InvalidSeed_ErrorIsThrow() {
        Seed[] seeds = new Seed[]{
                new SeedTwelve(HEX.decode("ba0e357d9157a1a7299fbc4cb4c933bd00")),
                new SeedTwentyFour(HEX.decode("7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"), Network.LIVE_NET)
        };

        for (Seed seed : seeds) {
            Exception e = assertThrows("", ValidateException.class, () -> Account.fromSeed(seed));
            assertEquals("Incorrect network from Seed", e.getMessage());
        }
    }

    @Test
    public void testNewAccountFromRecoveryPhrase_ValidRecoveryPhrase_ValidAccountIsCreated() {
        String recoveryPhrase = "name gaze apart lamp lift zone believe steak session laptop crowd hill";
        String accountNumber = "eMCcmw1SKoohNUf3LeioTFKaYNYfp2bzFYpjm3EddwxBSWYVCb";
        String publicKey = "369f6ceb1c23dbccc61b75e7990d0b2db8e1ee8da1c44db32280e63ca5804f38";

        final Account account = Account.fromRecoveryPhrase(recoveryPhrase.split(
                " "));
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(publicKey, HEX.encode(account.getAuthKeyPair().publicKey().toBytes()));
    }

    @Test
    public void testNewAccountFromRecoveryPhrase_InvalidRecoveryPhrase_ErrorIsThrow() {
        String[] invalidPhrases = new String[]{
                "% ^",
                "crime cricket castle fun purse announce nephew profit cloth trim delivery",
                "",
                "this is the recovery phrase that has 24 words but not contains the words from bip39 list so it will throw exception $ ^"
        };

        for (String phrase : invalidPhrases) {
            assertThrows("",
                    ValidateException.class,
                    () -> Account.fromRecoveryPhrase(phrase.split(" "))
            );
        }
    }

    @Test
    public void testGetSeedFromExistedAccount_ValidAccount_CorrectSeedIsReturn() {
        String[] phrase = new String[]{
                "file earn crack fever crack differ wreck crazy salon imitate swamp sample",
                "during kingdom crew atom practice brisk weird document eager artwork ride then"
        };
        String[] seed = new String[]{
                "9J878SbnM2GFqAELkkiZbqHJDkAj57fYK",
                "9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH"
        };

        for (int i = 0; i < phrase.length; i++) {
            final Account account = Account.fromRecoveryPhrase(phrase[i].split(
                    " "));
            final String encodedSeed = account.getSeed().getEncodedSeed();
            assertEquals(seed[i], encodedSeed);
        }
    }

    @Test
    public void testGetRecoveryPhrase_NoCondition_CorrectRecoveryPhraseIsReturn() {
        Seed seed = SeedTwelve.fromEncodedSeed("9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH");
        String phrase = "during kingdom crew atom practice brisk weird document eager artwork ride then";
        final Account account = Account.fromSeed(seed);
        assertArrayEquals(phrase.split(" "), account.getRecoveryPhrase().getMnemonicWords());
    }

    @Test
    public void testGetRecoveryPhraseWithLocale_NoCondition_CorrectRecoveryPhraseIsReturn() {
        Seed seed = SeedTwelve.fromEncodedSeed("9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH");
        Locale locale = Locale.ENGLISH;
        String phrase = "during kingdom crew atom practice brisk weird document eager artwork ride then";

        final Account account = Account.fromSeed(seed);
        assertArrayEquals(phrase.split(" "), account.getRecoveryPhrase(locale).getMnemonicWords());
    }

    @Test
    public void testCheckValidAccountNumber_NoCondition_CorrectResultIsReturn() {
        String[] accountNumbers = new String[]{
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                "ec6yMcJATX6gjNwvqpBNbc4jNEasoUgbfBBGGMM5NvoJ54NXva"
        };
        boolean[] valids = new boolean[]{
                true, true, false
        };

        for (int i = 0; i < accountNumbers.length; i++) {
            assertEquals(
                    Account.isValidAccountNumber(accountNumbers[i]),
                    valids[i]
            );
        }
    }

    @Test
    public void testSignVerify() {
        final byte[] message = Random.randomBytes(32);
        final Account account1 = new Account();
        final Account account2 = new Account();

        // test sign
        byte[] signature = assertDoesNotThrow(() -> account1.sign(message));
        assertNotNull(signature);
        assertTrue(signature.length > 0);

        // test verify
        boolean verified = assertDoesNotThrow(() -> Account.verify(
                account1.getAccountNumber(),
                signature,
                message
        ));
        assertTrue(verified);

        verified = assertDoesNotThrow(() -> Account.verify(
                account2.getAccountNumber(),
                signature,
                message
        ));
        assertFalse(verified);

    }

    @Test
    public void testGetBCRecoveryPhrase() {
        String encodedSeed = "9J876mP7wDJ6g5P41eNMN8N3jo9fycDs2";
        String[] words = "depend crime cricket castle fun purse announce nephew profit cloth trim deliver august".split(" ");

        Account account = Account.fromSeed(encodedSeed);
        BCRecoveryPhrase phrase = account.getBCRecoveryPhrase();
        assertTrue(Arrays.deepEquals(words, phrase.getMnemonicWords()));
    }

}
