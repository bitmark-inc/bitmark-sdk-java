/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.test.unittest;

import com.bitmark.apiservice.utils.ArrayUtil;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.features.internal.RecoveryPhrase;
import com.bitmark.sdk.features.internal.Seed;
import com.bitmark.sdk.features.internal.SeedTwelve;
import com.bitmark.sdk.test.BaseTest;
import org.junit.Test;

import java.util.Locale;

import static com.bitmark.apiservice.configuration.Network.TEST_NET;
import static com.bitmark.apiservice.utils.ArrayUtil.concat;
import static com.bitmark.apiservice.utils.ArrayUtil.toByteArray;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.sdk.test.utils.TestUtils.assertThrows;
import static org.junit.Assert.*;

public class RecoveryPhraseTest extends BaseTest {

    @Test
    public void testConstructRecoveryPhraseFromSeed_ValidSeed_ValidInstanceIsReturn() {
        Seed seed = new SeedTwelve(HEX.decode("442f54cd072a9638be4a0344e1a6e5f010"));
        String[] words = "during kingdom crew atom practice brisk weird document eager artwork ride then".split(" ");
        final RecoveryPhrase recoveryPhrase = RecoveryPhrase.fromSeed(seed);
        assertArrayEquals(words, recoveryPhrase.getMnemonicWords());
    }

    @Test
    public void testConstructRecoveryPhraseFromSeed_InvalidSeed_ErrorIsThrow() {
        ValidateException exception = assertThrows("",
                ValidateException.class,
                () -> RecoveryPhrase.fromSeed(null)
        );
        assertEquals("invalid seed", exception.getMessage());
    }

    @Test
    public void testConstructRecoveryPhraseFromSeedAndLocale_ValidSeed_ValidInstanceIsReturn() {
        Seed seed = new SeedTwelve(HEX.decode("442f54cd072a9638be4a0344e1a6e5f010"));
        Locale locale = Locale.ENGLISH;
        String[] words = "during kingdom crew atom practice brisk weird document eager artwork ride then".split(" ");

        final RecoveryPhrase recoveryPhrase = RecoveryPhrase.fromSeed(
                seed,
                locale
        );
        assertArrayEquals(words, recoveryPhrase.getMnemonicWords());
    }

    @Test
    public void testConstructRecoveryPhraseFromMnemonicWords_ValidMnemonicWords_ValidInstanceIsReturn() {
        String[] words = new String[]{
                "during kingdom crew atom practice brisk weird document eager artwork ride then",
                "depend crime cricket castle fun purse announce nephew profit cloth trim deliver",
                "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard science",
                "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard science"
        };

        for (String word : words) {
            final String[] mnemonicWords = word.split(" ");
            final RecoveryPhrase recoveryPhrase = RecoveryPhrase.fromMnemonicWords(
                    mnemonicWords);
            assertArrayEquals(mnemonicWords, recoveryPhrase.getMnemonicWords());
        }
    }

    @Test
    public void testConstructRecoveryPhraseFromMnemonicWords_InvalidMnemonicWords_ErrorIsThrow() {
        String[] words = new String[]{
                "during kingdom crew atom practice brisk weird document eager artwork ride",
                "专 青 办 增 孔 咱 里 耕 穷 か 扑 易",
                "",
                "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard scientist",
                "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard scientist"
        };

        for (String word : words) {
            assertThrows("",
                    ValidateException.class,
                    () -> RecoveryPhrase.fromMnemonicWords(word)
            );
        }
    }

    @Test
    public void testRecoverSeed_ValidMnemonicWords_ValidInstanceIsReturn() {
        String[] words = "during kingdom crew atom practice brisk weird document eager artwork ride then".split(" ");
        Seed expectedSeed = new SeedTwelve(HEX.decode("442f54cd072a9638be4a0344e1a6e5f010"));
        Seed seed = RecoveryPhrase.recoverSeed(words);
        assertTrue(ArrayUtil.equals(
                expectedSeed.getSeedBytes(),
                seed.getSeedBytes()
        ));
        assertEquals(expectedSeed.getNetwork(), seed.getNetwork());
    }

    @Test
    public void testRecoverSeed_InvalidMnemonicWords_ErrorIsThrow() {
        String[] words = new String[]{
                "during kingdom crew atom practice brisk weird document eager artwork rider then",
                "depend crime cricket castle fun purse announcement nephew profit cloth trim deliver",
                " ",
                "我 想 把 它 交 给 你 か 孔 咱 里 耕",
                "Quiero ir a casa porque extraño a mis padres y mis amigos",
                "Voglio andare a casa perché mi mancano i miei genitori e amici",
                "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard scientist"
        };

        for (String word : words) {
            final String[] mnemonicWords = word.split(" ");
            assertThrows("",
                    ValidateException.class,
                    () -> RecoveryPhrase.fromMnemonicWords(mnemonicWords)
            );
        }
    }

    @Test
    public void testGenerateMnemonicWords_ValidEntropy_ValidMnemonicWordsIsReturn() {
        final byte[] entropy = HEX.decode("442f54cd072a9638be4a0344e1a6e5f010");
        String[] words = "during kingdom crew atom practice brisk weird document eager artwork ride then".split(" ");

        final String[] mnemonicWords = RecoveryPhrase.generateMnemonic(entropy);
        assertArrayEquals(words, mnemonicWords);
    }

    @Test
    public void testGenerateMnemonicWordsWithLocale_ValidEntropy_ValidMnemonicWordsIsReturn() {
        byte[] entropy = HEX.decode("442f54cd072a9638be4a0344e1a6e5f010");
        Locale locale = Locale.ENGLISH;
        String[] words = "during kingdom crew atom practice brisk weird document eager artwork ride then".split(" ");


        final String[] mnemonicWords = RecoveryPhrase.generateMnemonic(entropy, locale);
        assertArrayEquals(words, mnemonicWords);
    }

    @Test
    public void testGenerateMnemonicWords_InvalidEntropy_ErrorIsThrow() {
        byte[][] entropies = new byte[][]{
                HEX.decode("442f54cd072a9638be4a0344e1a6e5f01b30"),
                HEX.decode("3ae670cd91c5e15d0254a2abc57ba290"),
                null,
                new byte[]{}, concat(toByteArray(TEST_NET.value()),
                HEX.decode("7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b6071"))
        };

        for (byte[] entropy : entropies) {
            assertThrows("",
                    ValidateException.class,
                    () -> RecoveryPhrase.generateMnemonic(entropy)
            );
        }
    }

}
