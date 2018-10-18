package sdk.test.unittest.utils;

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
import sdk.utils.RecoveryPhrase;
import sdk.utils.Seed;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

import static apiservice.configuration.Network.TEST_NET;
import static cryptography.crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;

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
    }

    @DisplayName("Verify function RecoveryPhrase.fromSeed(Seed, Locale) works well with happy " +
                         "condition")
    @ParameterizedTest
    @MethodSource("createValidByteArrayNetWorkLocaleMnemonicWords")
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
            "trim deliver"})
    public void testConstructRecoveryPhraseFromMnemonicWords_ValidMnemonicWords_ValidInstanceIsReturn(String mnemonicWord) {
        final String[] mnemonicWords = mnemonicWord.split(" ");
        final RecoveryPhrase recoveryPhrase = RecoveryPhrase.fromMnemonicWords(mnemonicWords);
        assertTrue(Arrays.equals(mnemonicWords, recoveryPhrase.getMnemonicWords()));
    }

    @DisplayName("Verify function RecoveryPhrase.fromMnemonicWords(String...) throws error with " +
                         "invalid mnemonic words")
    @ParameterizedTest
    @ValueSource(strings = {"during kingdom crew atom practice brisk weird document eager artwork" +
            " ride", "专 青 办 增 孔 咱 里 耕 穷 か 扑 易", ""})
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
            "genitori e amici"})
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
    }

    @DisplayName("Verify function RecoveryPhrase.generateMnemonic(byte[], Locale) works well with" +
                         " happy condition")
    @ParameterizedTest
    @MethodSource("createValidByteArrayLocaleMnemonicWords")
    public void testGenerateMnemonicWordsWithLocale_ValidEntropy_ValidMnemonicWordsIsReturn(byte[] entropy, Locale locale,
                                                                                            String[] expectedMnemonicWords) {
        final String[] mnemonicWords = RecoveryPhrase.generateMnemonic(entropy, locale);
        assertTrue(Arrays.equals(expectedMnemonicWords, mnemonicWords));
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
                "442f54cd072a9638be4a0344e1a6e5f010"), TEST_NET, (
                        "during kingdom crew atom practice brisk weird document eager artwork " +
                                "ride then").split(" ")),
                Arguments.of(HEX.decode(
                        "3ae670cd91c5e15d0254a2abc57ba29d00"),
                        TEST_NET,
                        ("depend crime cricket castle fun purse announce nephew profit cloth trim" +
                                " deliver").split(" ")));
    }

    private static Stream<Arguments> createValidByteArrayNetWorkLocaleMnemonicWords() {
        return Stream.of(Arguments.of(HEX.decode(
                "442f54cd072a9638be4a0344e1a6e5f010"), TEST_NET, Locale.ENGLISH, (
                        "during kingdom crew atom practice brisk weird document eager artwork " +
                                "ride then").split(" ")),
                Arguments.of(HEX.decode(
                        "3ae670cd91c5e15d0254a2abc57ba29d00"),
                        TEST_NET, Locale.CHINESE,
                        ("专 青 办 增 孔 咱 里 耕 穷 节 扑 易").split(" ")));
    }

    private static Stream<Arguments> createValidMnemonicSeed() {
        return Stream.of(Arguments.of(("during kingdom crew atom practice brisk weird document " +
                "eager artwork ride then").split(" "), new Seed(HEX.decode(
                "442f54cd072a9638be4a0344e1a6e5f010"),
                TEST_NET)), Arguments.of(("专 青 办 增 孔 咱 里 耕 穷 节 扑 易").split(" "),
                new Seed(HEX.decode(
                        "3ae670cd91c5e15d0254a2abc57ba29d00"),
                        TEST_NET)));
    }

    private static Stream<Arguments> createValidByteArrayMnemonicWords() {
        final byte[] entropy1 = HEX.decode("442f54cd072a9638be4a0344e1a6e5f010");
        final byte[] entropy2 = HEX.decode("3ae670cd91c5e15d0254a2abc57ba29d00");
        return Stream.of(Arguments.of(entropy1, ("during kingdom crew atom practice brisk weird " +
                "document eager artwork ride then").split(" ")), Arguments.of(entropy2, ("depend " +
                "crime cricket castle fun purse announce nephew profit cloth trim deliver").split(" ")));
    }

    private static Stream<Arguments> createValidByteArrayLocaleMnemonicWords() {
        final byte[] entropy1 = HEX.decode("442f54cd072a9638be4a0344e1a6e5f010");
        final byte[] entropy2 = HEX.decode("3ae670cd91c5e15d0254a2abc57ba29d00");
        return Stream.of(Arguments.of(entropy1, Locale.ENGLISH, ("during kingdom crew atom " +
                "practice brisk weird " +
                "document eager artwork ride then").split(" ")), Arguments.of(entropy2,
                Locale.CHINESE, ("专 青 办 增 孔 咱 里 耕 穷 节 扑 易").split(" ")));
    }

    private static Stream<byte[]> createInvalidEntropy() {
        return Stream.of(HEX.decode(
                "442f54cd072a9638be4a0344e1a6e5f01b30"), HEX.decode(
                "3ae670cd91c5e15d0254a2abc57ba290"), null,
                new byte[]{});
    }

}
