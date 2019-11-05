/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.test.unittest.features;

import com.bitmark.apiservice.utils.ArrayUtil;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.features.internal.RecoveryPhrase;
import com.bitmark.sdk.features.internal.Seed;
import com.bitmark.sdk.features.internal.SeedTwelve;
import com.bitmark.sdk.features.internal.SeedTwentyFour;
import com.bitmark.sdk.test.unittest.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

import static com.bitmark.apiservice.configuration.Network.LIVE_NET;
import static com.bitmark.apiservice.configuration.Network.TEST_NET;
import static com.bitmark.apiservice.utils.ArrayUtil.concat;
import static com.bitmark.apiservice.utils.ArrayUtil.toByteArray;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;

public class RecoveryPhraseTest extends BaseTest {

    @ParameterizedTest
    @MethodSource("createValidSeedMnemonicWords")
    public void testConstructRecoveryPhraseFromSeed_ValidSeed_ValidInstanceIsReturn(
            Seed seed,
            String[] mnemonicWords
    ) {
        final RecoveryPhrase recoveryPhrase = RecoveryPhrase.fromSeed(seed);
        assertTrue(Arrays.equals(
                mnemonicWords,
                recoveryPhrase.getMnemonicWords()
        ));
    }

    @Test
    public void testConstructRecoveryPhraseFromSeed_InvalidSeed_ErrorIsThrow() {
        ValidateException exception = assertThrows(
                ValidateException.class,
                () -> RecoveryPhrase.fromSeed(null)
        );
        assertEquals("invalid seed", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("createValidSeedLocaleMnemonicWords")
    public void testConstructRecoveryPhraseFromSeedAndLocale_ValidSeed_ValidInstanceIsReturn(
            Seed seed, Locale locale, String[] mnemonicWords
    ) {
        final RecoveryPhrase recoveryPhrase = RecoveryPhrase.fromSeed(
                seed,
                locale
        );
        assertTrue(Arrays.equals(
                mnemonicWords,
                recoveryPhrase.getMnemonicWords()
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "during kingdom crew atom practice brisk weird document eager artwork ride then",
            "depend crime cricket castle fun purse announce nephew profit cloth trim deliver",
            "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard science",
            "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard science"
    })
    public void testConstructRecoveryPhraseFromMnemonicWords_ValidMnemonicWords_ValidInstanceIsReturn(
            String mnemonicWord
    ) {
        final String[] mnemonicWords = mnemonicWord.split(" ");
        final RecoveryPhrase recoveryPhrase = RecoveryPhrase.fromMnemonicWords(
                mnemonicWords);
        assertTrue(Arrays.equals(
                mnemonicWords,
                recoveryPhrase.getMnemonicWords()
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "during kingdom crew atom practice brisk weird document eager artwork ride",
            "专 青 办 增 孔 咱 里 耕 穷 か 扑 易",
            "",
            "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard scientist",
            "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard scientist"
    })
    public void testConstructRecoveryPhraseFromMnemonicWords_InvalidMnemonicWords_ErrorIsThrow(
            String mnemonicWords
    ) {
        assertThrows(
                ValidateException.class,
                () -> RecoveryPhrase.fromMnemonicWords(mnemonicWords)
        );
    }

    @ParameterizedTest
    @MethodSource("createValidMnemonicSeed")
    public void testRecoverSeed_ValidMnemonicWords_ValidInstanceIsReturn(
            String[] mnemonicWords,
            Seed expectedSeed
    ) {
        final Seed seed = RecoveryPhrase.recoverSeed(mnemonicWords);
        Assertions.assertTrue(ArrayUtil.equals(
                expectedSeed.getSeedBytes(),
                seed.getSeedBytes()
        ));
        assertEquals(expectedSeed.getNetwork(), seed.getNetwork());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "during kingdom crew atom practice brisk weird document eager artwork rider then",
            "depend crime cricket castle fun purse announcement nephew profit cloth trim deliver",
            " ",
            "我 想 把 它 交 给 你 か 孔 咱 里 耕",
            "Quiero ir a casa porque extraño a mis padres y mis amigos",
            "Voglio andare a casa perché mi mancano i miei genitori e amici",
            "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard scientist"
    })
    public void testRecoverSeed_InvalidMnemonicWords_ErrorIsThrow(String mnemonicWord) {
        final String[] mnemonicWords = mnemonicWord.split(" ");
        assertThrows(
                ValidateException.class,
                () -> RecoveryPhrase.fromMnemonicWords(mnemonicWords)
        );
    }

    @ParameterizedTest
    @MethodSource("createValidEntropyByteArrayMnemonicWords")
    public void testGenerateMnemonicWords_ValidEntropy_ValidMnemonicWordsIsReturn(
            byte[] entropy,
            String[] expectedMnemonicWords
    ) {
        final String[] mnemonicWords = RecoveryPhrase.generateMnemonic(entropy);
        assertTrue(Arrays.equals(expectedMnemonicWords, mnemonicWords));
    }

    @ParameterizedTest
    @MethodSource("createValidEntropyByteArrayLocaleMnemonicWords")
    public void testGenerateMnemonicWordsWithLocale_ValidEntropy_ValidMnemonicWordsIsReturn(
            byte[] entropy, Locale locale,
            String[] expectedMnemonicWords
    ) {
        final String[] mnemonicWords = RecoveryPhrase.generateMnemonic(
                entropy,
                locale
        );
        assertTrue(Arrays.equals(expectedMnemonicWords, mnemonicWords));
    }

    @ParameterizedTest
    @MethodSource("createInvalidEntropyByteArray")
    public void testGenerateMnemonicWords_InvalidEntropy_ErrorIsThrow(byte[] entropy) {
        assertThrows(
                ValidateException.class,
                () -> RecoveryPhrase.generateMnemonic(entropy)
        );
    }

    private static Stream<Arguments> createValidSeedMnemonicWords() {
        return Stream.of(
                Arguments.of(new SeedTwelve(HEX.decode(
                        "442f54cd072a9638be4a0344e1a6e5f010")), (
                        "during kingdom crew atom practice brisk weird document eager artwork ride then")
                        .split(" ")),
                Arguments.of(
                        new SeedTwelve(
                                HEX.decode("3ae670cd91c5e15d0254a2abc57ba29d00")),
                        ("depend crime cricket castle fun purse announce nephew profit cloth trim deliver")
                                .split(" ")
                )
        );
    }

    private static Stream<Arguments> createValidSeedLocaleMnemonicWords() {
        return Stream.of(
                Arguments.of(
                        new SeedTwelve(HEX.decode(
                                "442f54cd072a9638be4a0344e1a6e5f010")),
                        Locale.ENGLISH,
                        (
                                "during kingdom crew atom practice brisk weird document eager artwork ride then")
                                .split(" ")
                ),
                Arguments.of(
                        new SeedTwelve(
                                HEX.decode("3ae670cd91c5e15d0254a2abc57ba29d00")),
                        Locale.TRADITIONAL_CHINESE,
                        ("專 青 辦 增 孔 咱 裡 耕 窮 節 撲 易").split(" ")
                ),
                Arguments.of(new SeedTwentyFour(
                                HEX.decode(
                                        "33f1fbe8e8e5c7fd592de351059a19434b99082cfaf9f71f6cbe216173690317"),
                                LIVE_NET
                        ), Locale.ENGLISH,
                        ("ability panel leave spike mixture token voice certain today market grief crater cruise smart camera palm wheat rib swamp labor bid rifle piano glass")
                                .split(" ")
                )
        );
    }

    private static Stream<Arguments> createValidMnemonicSeed() {
        return Stream.of(
                Arguments.of(
                        ("during kingdom crew atom practice brisk weird document eager artwork ride then")
                                .split(" "),
                        new SeedTwelve(HEX.decode(
                                "442f54cd072a9638be4a0344e1a6e5f010"))
                ),
                Arguments.of(
                        ("專 青 辦 增 孔 咱 裡 耕 窮 節 撲 易").split(" "),
                        new SeedTwelve(HEX.decode(
                                "3ae670cd91c5e15d0254a2abc57ba29d00"))
                ),
                Arguments.of(
                        ("accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard science")
                                .split(" "),
                        new SeedTwentyFour(
                                HEX.decode(
                                        "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"),
                                TEST_NET
                        )
                ),
                Arguments.of(
                        ("accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard science")
                                .split(" "),
                        new SeedTwentyFour(
                                HEX.decode(
                                        "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"),
                                TEST_NET
                        )
                )
        );
    }

    private static Stream<Arguments> createValidEntropyByteArrayMnemonicWords() {
        final byte[] entropy1 = HEX.decode("442f54cd072a9638be4a0344e1a6e5f010");
        final byte[] entropy2 = HEX.decode("3ae670cd91c5e15d0254a2abc57ba29d00");
        final byte[] entropy3 = concat(
                toByteArray(TEST_NET.value()),
                HEX.decode(
                        "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607")
        );
        return Stream.of(
                Arguments.of(
                        entropy1,
                        ("during kingdom crew atom practice brisk weird document eager artwork ride then")
                                .split(" ")
                ),
                Arguments.of(entropy2, (
                        "depend crime cricket castle fun purse announce nephew profit cloth trim deliver")
                        .split(" ")),
                Arguments.of(
                        entropy3,
                        ("accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard science")
                                .split(" ")
                )
        );
    }

    private static Stream<Arguments> createValidEntropyByteArrayLocaleMnemonicWords() {
        final byte[] entropy1 = HEX.decode("442f54cd072a9638be4a0344e1a6e5f010");
        final byte[] entropy2 = HEX.decode("3ae670cd91c5e15d0254a2abc57ba29d00");
        final byte[] entropy3 = concat(
                toByteArray(TEST_NET.value()),
                HEX.decode(
                        "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607")
        );
        return Stream.of(Arguments.of(
                entropy1,
                Locale.ENGLISH,
                ("during kingdom crew atom practice brisk weird document eager artwork ride then")
                        .split(" ")
                ), Arguments.of(
                entropy2,
                Locale.TRADITIONAL_CHINESE,
                ("專 青 辦 增 孔 咱 裡 耕 窮 節 撲 易").split(" ")
                ),
                Arguments.of(entropy3, Locale.ENGLISH,
                        ("accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard science")
                                .split(" ")
                )
        );
    }

    private static Stream<byte[]> createInvalidEntropyByteArray() {
        return Stream.of(HEX.decode(
                "442f54cd072a9638be4a0344e1a6e5f01b30"), HEX.decode(
                "3ae670cd91c5e15d0254a2abc57ba290"), null,
                new byte[]{}, concat(toByteArray(TEST_NET.value()), HEX.decode(
                        "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b6071"))
        );
    }

}
