package com.bitmark.sdk.bcbip39wrapper;

import com.bitmark.sdk.bcbip39wrapper.bip39.Bip39;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class Bip39Test {

    @ParameterizedTest
    @MethodSource("createMnemonicWord1")
    public void testWordFromMnemonic(int index, String word) {
        assertEquals(index, Bip39.bip39_word_from_mnemonic(word));
    }

    private static Stream<Arguments> createMnemonicWord1() {
        return Stream.of(
                Arguments.of((short) 1018, "leg"),
                Arguments.of((short) 1024, "length"),
                Arguments.of((short) 2047, "zoo"),
                Arguments.of((short) -1, "zoos"),
                Arguments.of((short) -1, "b+mrk"),
                Arguments.of((short) 0, "abandon"),
                Arguments.of((short) 16, "acoustic"),
                Arguments.of((short) 490, "diary"),
                Arguments.of((short) 1541, "scheme")
        );
    }

    @ParameterizedTest
    @MethodSource("createMnemonicWord2")
    public void testMnemonicFromWord(short index, String word) {
        byte[] actual = new byte[100];
        Bip39.bip39_mnemonic_from_word(index, actual);
        byte[] expected = word.getBytes();
        assertTrue(Objects.deepEquals(
                expected,
                Arrays.copyOfRange(actual, 0, expected.length)
        ));
    }

    private static Stream<Arguments> createMnemonicWord2() {
        return Stream.of(
                Arguments.of((short) 0, "abandon"),
                Arguments.of((short) 1018, "leg"),
                Arguments.of((short) 1024, "length"),
                Arguments.of((short) 2047, "zoo")
        );
    }

    @ParameterizedTest
    @MethodSource("createWordIndexSecret")
    public void testWordsFromSecret(byte[] secret, short[] words) {
        short[] actual = new short[100];
        Bip39.bip39_words_from_secret(
                secret,
                secret.length,
                actual,
                actual.length
        );
        assertTrue(Objects.deepEquals(
                words,
                Arrays.copyOfRange(actual, 0, words.length)
        ));
    }

    private static Stream<Arguments> createWordIndexSecret() {
        return Stream.of(
                Arguments.of(
                        Hex.decode("baadf00dbaadf00d"),
                        new short[]{1493, 892, 27, 938, 1784, 55}
                ),
                Arguments.of(
                        Hex.decode("baadf00dbaadf00dbaadf00dbaadf00d"),
                        new short[]{
                                1493,
                                892,
                                27,
                                938,
                                1784,
                                54,
                                1877,
                                1520,
                                109,
                                1707,
                                992,
                                213
                        }
                ),
                Arguments.of(
                        Hex.decode("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f"),
                        new short[]{
                                1019,
                                2015,
                                1790,
                                2039,
                                1983,
                                1533,
                                2031,
                                1919,
                                1019,
                                2015,
                                1790,
                                2040
                        }
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createMnemonicSecret")
    public void testMnemonicFromSecret(byte[] secret, byte[] mnemonic) {
        byte[] actual = new byte[1000];
        Bip39.bip39_mnemonics_from_secret(
                secret,
                secret.length,
                actual,
                actual.length
        );
        assertTrue(Objects.deepEquals(
                mnemonic,
                Arrays.copyOfRange(actual, 0, mnemonic.length)
        ));
    }

    private static Stream<Arguments> createMnemonicSecret() {
        return Stream.of(
                Arguments.of(
                        Hex.decode("baadf00dbaadf00d"),
                        "rival hurdle address inspire tenant alone".getBytes(StandardCharsets.US_ASCII)
                ),
                Arguments.of(
                        Hex.decode("baadf00dbaadf00dbaadf00dbaadf00d"),
                        "rival hurdle address inspire tenant almost turkey safe asset step lab boy"
                                .getBytes(StandardCharsets.US_ASCII)
                ),
                Arguments.of(
                        Hex.decode("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f"),
                        "legal winner thank year wave sausage worth useful legal winner thank yellow"
                                .getBytes(StandardCharsets.US_ASCII)
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createWordMnemonic")
    public void testWordsFromMnemonic(short[] words, String mnemonic) {
        short[] actual = new short[100];
        Bip39.bip39_words_from_mnemonics(
                mnemonic,
                actual,
                actual.length
        );
        assertTrue(Objects.deepEquals(
                words,
                Arrays.copyOfRange(actual, 0, words.length)
        ));
    }

    private static Stream<Arguments> createWordMnemonic() {
        return Stream.of(
                Arguments.of(
                        new short[]{1493, 892, 27, 938, 1784, 55},
                        "rival hurdle address inspire tenant alone"
                ),
                Arguments.of(
                        new short[]{
                                1493,
                                892,
                                27,
                                938,
                                1784,
                                54,
                                1877,
                                1520,
                                109,
                                1707,
                                992,
                                213
                        },
                        "rival hurdle address inspire tenant almost turkey safe asset step lab boy"
                ),
                Arguments.of(
                        new short[]{
                                1019,
                                2015,
                                1790,
                                2039,
                                1983,
                                1533,
                                2031,
                                1919,
                                1019,
                                2015,
                                1790,
                                2040
                        },
                        "legal winner thank year wave sausage worth useful legal winner thank yellow"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createSecretMnemonic")
    public void testSecretFromMnemonic(byte[] secret, String mnemonic) {
        byte[] actual = new byte[1000];
        Bip39.bip39_secret_from_mnemonics(
                mnemonic,
                actual,
                actual.length
        );
        assertTrue(Objects.deepEquals(
                secret,
                Arrays.copyOfRange(actual, 0, secret.length)
        ));
    }

    private static Stream<Arguments> createSecretMnemonic() {
        return Stream.of(
                Arguments.of(
                        Hex.decode("baadf00dbaadf00d"),
                        "rival hurdle address inspire tenant alone"
                ),
                Arguments.of(
                        Hex.decode("baadf00dbaadf00dbaadf00dbaadf00d"),
                        "rival hurdle address inspire tenant almost turkey safe asset step lab boy"
                ),
                Arguments.of(
                        Hex.decode("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f"),
                        "legal winner thank year wave sausage worth useful legal winner thank yellow"
                )
        );
    }

    @Test
    public void testSeedFromString() {
        String string = "123456";
        byte[] secret = new byte[]{
                (byte) 0x8d,
                (byte) 0x96,
                (byte) 0x9e,
                (byte) 0xef,
                0x6e,
                (byte) 0xca,
                (byte) 0xd3,
                (byte) 0xc2,
                (byte) 0x9a,
                0x3a,
                0x62,
                (byte) 0x92,
                (byte) 0x80,
                (byte) 0xe6,
                (byte) 0x86,
                (byte) 0xcf
        };

        byte[] actual = new byte[1000];
        Bip39.bip39_seed_from_string(string, actual);
        assertTrue(Objects.deepEquals(
                secret,
                Arrays.copyOfRange(actual, 0, secret.length)
        ));
    }
}