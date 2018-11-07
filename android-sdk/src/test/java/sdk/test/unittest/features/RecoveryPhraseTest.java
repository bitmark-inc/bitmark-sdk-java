package sdk.test.unittest.features;

import apiservice.configuration.Network;
import apiservice.utils.ArrayUtil;
import cryptography.error.ValidateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import sdk.test.unittest.BaseTest;
import sdk.features.RecoveryPhrase;
import sdk.features.Seed;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

import static apiservice.configuration.Network.LIVE_NET;
import static apiservice.configuration.Network.TEST_NET;
import static apiservice.utils.ArrayUtil.concat;
import static apiservice.utils.ArrayUtil.toByteArray;
import static cryptography.crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;
import static sdk.utils.Version.TWELVE;

/**
 * @author Hieu Pham
 * @since 9/2/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class RecoveryPhraseTest extends BaseTest {

    @DisplayName("Verify function new RecoveryPhrase() works well with happy condition. The " +
                         "phrase generate is 12 words")
    @Test
    public void testConstructRecoveryPhrase_NoCondition_ValidInstanceIsReturn() {
        final RecoveryPhrase phrase = new RecoveryPhrase();
        assertNotNull(phrase.getMnemonicWords());
        assertEquals(TWELVE.getMnemonicWordsLength(), phrase.getMnemonicWords().length);
    }

    @DisplayName("Verify function RecoveryPhrase.fromSeed(Seed) works well with happy condition")
    @ParameterizedTest
    @MethodSource("createValidSeedByteArrayNetWorkMnemonicWords")
    public void testConstructRecoveryPhraseFromSeed_ValidSeed_ValidInstanceIsReturn(byte[] seedBytes, Network network,
                                                                                    String[] mnemonicWords) {
        final RecoveryPhrase recoveryPhrase = RecoveryPhrase.fromSeed(new Seed(seedBytes, network));
        assertTrue(Arrays.equals(mnemonicWords, recoveryPhrase.getMnemonicWords()));
    }

    @DisplayName("Verify function RecoveryPhrase.fromSeed(Seed) throws error with invalid seed")
    @Test
    public void testConstructRecoveryPhraseFromSeed_InvalidSeed_ErrorIsThrow() {
        ValidateException exception = assertThrows(ValidateException.class,
                () -> RecoveryPhrase.fromSeed(null));
        assertEquals("Invalid Seed", exception.getMessage());
    }

    @DisplayName("Verify function RecoveryPhrase.fromSeed(Seed, Locale) works well with happy " +
                         "condition")
    @ParameterizedTest
    @MethodSource("createValidSeedByteArrayNetWorkLocaleMnemonicWords")
    public void testConstructRecoveryPhraseFromSeedAndLocale_ValidSeed_ValidInstanceIsReturn(byte[] seedBytes, Network network, Locale locale,
                                                                                             String[] mnemonicWords) {
        final RecoveryPhrase recoveryPhrase = RecoveryPhrase.fromSeed(new Seed(seedBytes,
                network), locale);
        assertTrue(Arrays.equals(mnemonicWords, recoveryPhrase.getMnemonicWords()));
    }

    @DisplayName("Verify function RecoveryPhrase.fromMnemonicWords(String...) works well with " +
                         "happy condition")
    @ParameterizedTest
    @ValueSource(strings = {"during kingdom crew atom practice brisk weird document eager artwork" +
            " ride then", "depend crime cricket castle fun purse announce nephew profit cloth " +
            "trim deliver", "accident syrup inquiry you clutch liquid fame upset joke glow best " +
            "school repeat birth library combine access camera organ trial crazy jeans lizard " +
            "science", "accident syrup inquiry you clutch liquid fame upset joke glow best school" +
            " repeat birth library combine access camera organ " +
            "trial crazy jeans lizard science"})
    public void testConstructRecoveryPhraseFromMnemonicWords_ValidMnemonicWords_ValidInstanceIsReturn(String mnemonicWord) {
        final String[] mnemonicWords = mnemonicWord.split(" ");
        final RecoveryPhrase recoveryPhrase = RecoveryPhrase.fromMnemonicWords(mnemonicWords);
        assertTrue(Arrays.equals(mnemonicWords, recoveryPhrase.getMnemonicWords()));
    }

    @DisplayName("Verify function RecoveryPhrase.fromMnemonicWords(String...) throws error with " +
                         "invalid mnemonic words")
    @ParameterizedTest
    @ValueSource(strings = {"during kingdom crew atom practice brisk weird document eager artwork" +
            " ride", "专 青 办 增 孔 咱 里 耕 穷 か 扑 易", "", "accident syrup inquiry you clutch liquid " +
            "fame upset joke glow best school repeat birth library combine access camera organ " +
            "trial crazy jeans lizard scientist", "accident syrup inquiry you clutch liquid fame " +
            "upset joke glow best school repeat birth library combine access camera organ trial " +
            "crazy jeans lizard scientist"})
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
    }

    @DisplayName("Verify function RecoveryPhrase.recoverSeed(String...) throws error with invalid" +
                         " mnemonic words")
    @ParameterizedTest
    @ValueSource(strings = {"during kingdom crew atom practice brisk weird document eager artwork" +
            " rider then", "depend crime cricket castle fun purse announcement nephew profit " +
            "cloth trim deliver", " ", "我 想 把 它 交 给 你 か 孔 咱 里 耕", "Quiero ir a casa porque " +
            "extraño a mis padres y mis amigos", "Voglio andare a casa perché mi mancano i miei " +
            "genitori e amici", "accident syrup inquiry you clutch liquid fame upset joke glow " +
            "best school repeat birth library combine access camera organ trial crazy jeans " +
            "lizard scientist"})
    public void testRecoverSeed_InvalidMnemonicWords_ErrorIsThrow(String mnemonicWord) {
        final String[] mnemonicWords = mnemonicWord.split(" ");
        assertThrows(ValidateException.class,
                () -> RecoveryPhrase.fromMnemonicWords(mnemonicWords));
    }

    @DisplayName("Verify function RecoveryPhrase.generateMnemonic(byte[]) works well with happy " +
                         "condition")
    @ParameterizedTest
    @MethodSource("createValidEntropyByteArrayMnemonicWords")
    public void testGenerateMnemonicWords_ValidEntropy_ValidMnemonicWordsIsReturn(byte[] entropy,
                                                                                  String[] expectedMnemonicWords) {
        final String[] mnemonicWords = RecoveryPhrase.generateMnemonic(entropy);
        assertTrue(Arrays.equals(expectedMnemonicWords, mnemonicWords));
    }

    @DisplayName("Verify function RecoveryPhrase.generateMnemonic(byte[], Locale) works well with" +
                         " happy condition")
    @ParameterizedTest
    @MethodSource("createValidEntropyByteArrayLocaleMnemonicWords")
    public void testGenerateMnemonicWordsWithLocale_ValidEntropy_ValidMnemonicWordsIsReturn(byte[] entropy, Locale locale,
                                                                                            String[] expectedMnemonicWords) {
        final String[] mnemonicWords = RecoveryPhrase.generateMnemonic(entropy, locale);
        assertTrue(Arrays.equals(expectedMnemonicWords, mnemonicWords));
    }

    @DisplayName("Verify function RecoveryPhrase.generateMnemonic(byte[]) throws error with " +
                         "invalid entropy")
    @ParameterizedTest
    @MethodSource("createInvalidEntropyByteArray")
    public void testGenerateMnemonicWords_InvalidEntropy_ErrorIsThrow(byte[] entropy) {
        assertThrows(ValidateException.class, () -> RecoveryPhrase.generateMnemonic(entropy));
    }

    private static Stream<Arguments> createValidSeedByteArrayNetWorkMnemonicWords() {
        return Stream.of(Arguments.of(HEX.decode(
                "442f54cd072a9638be4a0344e1a6e5f010"), TEST_NET, (
                        "during kingdom crew atom practice brisk weird document eager artwork " +
                                "ride then").split(" ")),
                Arguments.of(HEX.decode(
                        "3ae670cd91c5e15d0254a2abc57ba29d00"),
                        TEST_NET,
                        ("depend crime cricket castle fun purse announce nephew profit cloth trim" +
                                " deliver").split(" ")));
    }

    private static Stream<Arguments> createValidSeedByteArrayNetWorkLocaleMnemonicWords() {
        return Stream.of(Arguments.of(HEX.decode(
                "442f54cd072a9638be4a0344e1a6e5f010"), TEST_NET, Locale.ENGLISH, (
                        "during kingdom crew atom practice brisk weird document eager artwork " +
                                "ride then").split(" ")),
                Arguments.of(HEX.decode(
                        "3ae670cd91c5e15d0254a2abc57ba29d00"),
                        TEST_NET, Locale.CHINESE,
                        ("專 青 辦 增 孔 咱 裡 耕 窮 節 撲 易").split(" ")),
                Arguments.of(HEX.decode(
                        "33f1fbe8e8e5c7fd592de351059a19434b99082cfaf9f71f6cbe216173690317"),
                        LIVE_NET, Locale.ENGLISH,
                        ("ability panel leave spike mixture token voice certain today market " +
                                "grief" +
                                " crater cruise smart camera palm wheat rib swamp labor bid rifle" +
                                " piano glass").split(" ")));
    }

    private static Stream<Arguments> createValidMnemonicSeed() {
        return Stream.of(Arguments.of(("during kingdom crew atom practice brisk weird document " +
                "eager artwork ride then").split(" "), new Seed(HEX.decode(
                "442f54cd072a9638be4a0344e1a6e5f010"),
                TEST_NET)), Arguments.of(("專 青 辦 增 孔 咱 裡 耕 窮 節 撲 易").split(" "),
                new Seed(HEX.decode(
                        "3ae670cd91c5e15d0254a2abc57ba29d00"),
                        TEST_NET)), Arguments.of(("accident syrup inquiry you clutch liquid fame " +
                "upset joke glow best school repeat birth library combine access camera organ " +
                "trial crazy jeans lizard science").split(" "), new Seed(HEX.decode(
                "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"),
                TEST_NET)), Arguments.of(("accident syrup inquiry you clutch liquid fame upset " +
                "joke glow best school repeat birth library combine access camera organ trial " +
                "crazy jeans lizard science").split(" "), new Seed(HEX.decode(
                "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"),
                TEST_NET)));
    }

    private static Stream<Arguments> createValidEntropyByteArrayMnemonicWords() {
        final byte[] entropy1 = HEX.decode("442f54cd072a9638be4a0344e1a6e5f010");
        final byte[] entropy2 = HEX.decode("3ae670cd91c5e15d0254a2abc57ba29d00");
        final byte[] entropy3 = concat(toByteArray(TEST_NET.value()), HEX.decode(
                "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"));
        return Stream.of(Arguments.of(entropy1, ("during kingdom crew atom practice brisk weird " +
                        "document eager artwork ride then").split(" ")), Arguments.of(entropy2, (
                        "depend crime cricket castle fun purse announce nephew profit cloth trim " +
                                "deliver").split(" ")),
                Arguments.of(entropy3, ("accident syrup inquiry you clutch liquid fame upset joke" +
                        " glow best school repeat birth library combine access camera organ trial" +
                        " crazy jeans lizard science").split(" ")));
    }

    private static Stream<Arguments> createValidEntropyByteArrayLocaleMnemonicWords() {
        final byte[] entropy1 = HEX.decode("442f54cd072a9638be4a0344e1a6e5f010");
        final byte[] entropy2 = HEX.decode("3ae670cd91c5e15d0254a2abc57ba29d00");
        final byte[] entropy3 = concat(toByteArray(TEST_NET.value()), HEX.decode(
                "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"));
        return Stream.of(Arguments.of(entropy1, Locale.ENGLISH, ("during kingdom crew atom " +
                        "practice brisk weird " +
                        "document eager artwork ride then").split(" ")), Arguments.of(entropy2,
                Locale.CHINESE, ("專 青 辦 增 孔 咱 裡 耕 窮 節 撲 易").split(" ")),
                Arguments.of(entropy3, Locale.ENGLISH, ("accident syrup inquiry you clutch liquid" +
                        " fame upset joke glow best school repeat birth library combine access " +
                        "camera organ trial crazy jeans lizard science").split(" ")));
    }

    private static Stream<byte[]> createInvalidEntropyByteArray() {
        return Stream.of(HEX.decode(
                "442f54cd072a9638be4a0344e1a6e5f01b30"), HEX.decode(
                "3ae670cd91c5e15d0254a2abc57ba290"), null,
                new byte[]{}, concat(toByteArray(TEST_NET.value()), HEX.decode(
                        "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b6071")));
    }

}
