package com.bitmark.sdk.test.unittest.utils;

import com.bitmark.sdk.config.Network;
import com.bitmark.sdk.error.ValidateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import com.bitmark.sdk.test.unittest.BaseTest;
import com.bitmark.sdk.utils.ArrayUtil;
import com.bitmark.sdk.utils.RecoveryPhrase;
import com.bitmark.sdk.utils.Seed;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.bitmark.sdk.config.Network.LIVE_NET;
import static com.bitmark.sdk.config.Network.TEST_NET;
import static com.bitmark.sdk.crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;
import static com.bitmark.sdk.utils.ArrayUtil.*;

/**
 * @author Hieu Pham
 * @since 9/2/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class RecoveryPhraseTest extends BaseTest {

    @DisplayName("Verify function new RecoveryPhrase() works well with happy condition")
    @Test
    public void testConstructRecoveryPhrase_NoCondition_ValidInstanceIsReturn() {
        final RecoveryPhrase phrase = new RecoveryPhrase();
        assertNotNull(phrase.getMnemonicWords());
        assertEquals(RecoveryPhrase.MNEMONIC_WORD_LENGTH, phrase.getMnemonicWords().length);
    }

    @DisplayName("Verify function RecoveryPhrase.fromSeed(Seed) works well with happy condition")
    @ParameterizedTest
    @MethodSource("createValidByteArrayNetWorkMnemonicWords")
    public void testConstructRecoveryPhraseFromSeed_ValidSeed_ValidInstanceIsReturn(byte[] seedBytes, Network network,
                                                                                    String[] mnemonicWords) {
        final RecoveryPhrase recoveryPhrase = RecoveryPhrase.fromSeed(new Seed(seedBytes, network));
        assertTrue(Arrays.equals(mnemonicWords, recoveryPhrase.getMnemonicWords()));
        assertFalse(isDuplicate(recoveryPhrase.getMnemonicWords()));
    }

    @DisplayName("Verify function RecoveryPhrase.fromMnemonicWords(String...) works well with " +
                         "happy condition")
    @ParameterizedTest
    @ValueSource(strings = {"accident syrup inquiry you clutch liquid fame upset joke glow best " +
            "school repeat birth library combine access camera organ trial crazy jeans lizard " +
            "science", "ability panel leave spike mixture token voice certain today market grief " +
            "crater cruise smart camera palm wheat rib swamp labor bid rifle piano glass"})
    public void testConstructRecoveryPhraseFromMnemonicWords_ValidMnemonicWords_ValidInstanceIsReturn(String mnemonicWord) {
        final String[] mnemonicWords = mnemonicWord.split(" ");
        final RecoveryPhrase recoveryPhrase = RecoveryPhrase.fromMnemonicWords(mnemonicWords);
        assertTrue(Arrays.equals(mnemonicWords, recoveryPhrase.getMnemonicWords()));
        assertFalse(isDuplicate(recoveryPhrase.getMnemonicWords()));
    }

    @DisplayName("Verify function RecoveryPhrase.fromMnemonicWords(String...) throws error with " +
                         "invalid mnemonic words")
    @ParameterizedTest
    @ValueSource(strings = {"accident syrup accident you clutch liquid fame upset joke glow best " +
            "school repeat birth library combine access camera organ trial crazy jeans lizard " +
            "science", "ability leave spike mixture voice certain today market grief " +
            "crater cruise smart camera palm wheat rib swamp labor bid rifle piano glass"})
    public void testConstructRecoveryPhraseFromMnemonicWords_InvalidMnemonicWords_ErrorIsThrow(String mnemonicWords) {
        assertThrows(ValidateException.class,
                () -> RecoveryPhrase.fromMnemonicWords(mnemonicWords));
    }

    @DisplayName("Verify function RecoveryPhrase.recoverSeed(String...) works well with happy " +
                         "condition")
    @ParameterizedTest
    @MethodSource("createValidMnemonicSeed")
    public void testRecoverSeed_ValidMnemonicWords_ValidInstanceIsReturn(String[] mnemonicWords,
                                                                         Seed expectedSeed) {
        final Seed seed = RecoveryPhrase.recoverSeed(mnemonicWords);
        Assertions.assertTrue(ArrayUtil.equals(expectedSeed.getSeed(), seed.getSeed()));
        assertEquals(expectedSeed.getNetwork(), seed.getNetwork());
        assertEquals(expectedSeed.getVersion(), seed.getVersion());
    }

    @DisplayName("Verify function RecoveryPhrase.recoverSeed(String...) throws error with invalid" +
                         " mnemonic words")
    @ParameterizedTest
    @ValueSource(strings = {"accident syrup hiring you clutch liquid fame upset joke glow best " +
            "school repeat birth library combine access camera organ trial crazy jeans lizard " +
            "science", "ability leave spike mixture voice certain today market grief " +
            "crater cruise smart camera palm wheat rib swamp labor bid rifle piano glass"})
    public void testRecoverSeed_InvalidMnemonicWords_ErrorIsThrow(String mnemonicWord) {
        final String[] mnemonicWords = mnemonicWord.split(" ");
        assertThrows(ValidateException.class,
                () -> RecoveryPhrase.fromMnemonicWords(mnemonicWords));
    }

    @DisplayName("Verify function RecoveryPhrase.generateMnemonic(byte[]) works well with happy " +
                         "condition")
    @ParameterizedTest
    @MethodSource("createValidByteArrayMnemonicWords")
    public void testGenerateMnemonicWords_ValidEntropy_ValidMnemonicWordsIsReturn(byte[] entropy,
                                                                                  String[] expectedMnemonicWords) {
        final String[] mnemonicWords = RecoveryPhrase.generateMnemonic(entropy);
        assertTrue(Arrays.equals(expectedMnemonicWords, mnemonicWords));
        assertFalse(isDuplicate(mnemonicWords));
    }

    @DisplayName("Verify function RecoveryPhrase.generateMnemonic(byte[]) throws error with " +
                         "invalid entropy")
    @ParameterizedTest
    @MethodSource("createInvalidEntropy")
    public void testGenerateMnemonicWords_InvalidEntropy_ErrorIsThrow(byte[] entropy) {
        assertThrows(ValidateException.class, () -> RecoveryPhrase.generateMnemonic(entropy));
    }

    private static Stream<Arguments> createValidByteArrayNetWorkMnemonicWords() {
        return Stream.of(Arguments.of(HEX.decode(
                "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"), TEST_NET, (
                        "accident " +
                                "syrup inquiry you clutch liquid fame upset joke glow best school" +
                                " repeat " +
                                "birth " +
                                "library combine access camera organ trial crazy jeans lizard " +
                                "science").split(" ")),
                Arguments.of(HEX.decode(
                        "33f1fbe8e8e5c7fd592de351059a19434b99082cfaf9f71f6cbe216173690317"),
                        LIVE_NET,
                        ("ability panel leave spike mixture token voice certain today market " +
                                "grief" +
                                " crater cruise smart camera palm wheat rib swamp labor bid rifle" +
                                " piano glass").split(" ")));
    }

    private static Stream<Arguments> createValidMnemonicSeed() {
        return Stream.of(Arguments.of(("accident syrup inquiry you clutch liquid fame upset joke " +
                "glow best school repeat birth library combine access camera organ trial crazy " +
                "jeans lizard science").split(" "), new Seed(HEX.decode(
                "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"),
                TEST_NET)), Arguments.of(("ability panel leave spike mixture token voice certain " +
                "today market grief crater cruise smart camera palm wheat rib swamp labor bid " +
                "rifle piano glass").split(" "), new Seed(HEX.decode(
                "33f1fbe8e8e5c7fd592de351059a19434b99082cfaf9f71f6cbe216173690317"),
                LIVE_NET)));
    }

    private static Stream<Arguments> createValidByteArrayMnemonicWords() {
        final byte[] entropy1 = concat(toByteArray(TEST_NET.value()), HEX.decode(
                "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"));
        final byte[] entropy2 = concat(toByteArray(LIVE_NET.value()), HEX.decode(
                "33f1fbe8e8e5c7fd592de351059a19434b99082cfaf9f71f6cbe216173690317"));
        return Stream.of(Arguments.of(entropy1, ("accident " +
                "syrup inquiry you clutch liquid fame upset joke " +
                "glow best school repeat birth library combine access camera organ trial crazy " +
                "jeans lizard science").split(" ")), Arguments.of(entropy2, ("ability " +
                "panel leave spike mixture token voice certain " +
                "today market grief crater cruise smart camera palm wheat rib swamp labor bid " +
                "rifle piano glass").split(" ")));
    }

    private static Stream<byte[]> createInvalidEntropy() {
        return Stream.of(HEX.decode(
                "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f32cef60b607"), HEX.decode(
                "33f1fbe8e8e5c7fd592de3534b99082cfaf9f71f6cbe216173690317"), null,
                new byte[]{});
    }

}
