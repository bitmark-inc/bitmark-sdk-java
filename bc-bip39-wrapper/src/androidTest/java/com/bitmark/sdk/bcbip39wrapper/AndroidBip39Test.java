package com.bitmark.sdk.bcbip39wrapper;

import android.util.Log;
import android.util.Pair;
import com.bitmark.sdk.bcbip39wrapper.bip39.Bip39;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AndroidBip39Test {

    @Test
    public void verifyWordFromMnemonic() {
        assertEquals(0, Bip39.bip39_word_from_mnemonic("abandon"));
        assertEquals(1018, Bip39.bip39_word_from_mnemonic("leg"));
        assertEquals(1024, Bip39.bip39_word_from_mnemonic("length"));
        assertEquals(2047, Bip39.bip39_word_from_mnemonic("zoo"));
        assertEquals(-1, Bip39.bip39_word_from_mnemonic("abandonn"));
        assertEquals(-1, Bip39.bip39_word_from_mnemonic("legx"));
        assertEquals(-1, Bip39.bip39_word_from_mnemonic("lengthz"));
        assertEquals(-1, Bip39.bip39_word_from_mnemonic("zooo"));
    }

    @Test
    public void testMnemonicFromWord() {
        Pair<Short, String>[] data = new Pair[]{
                new Pair<>((short) 0, "abandon"),
                new Pair<>((short) 1018, "leg"),
                new Pair<>((short) 1024, "length"),
                new Pair<>((short) 2047, "zoo")
        };
        for (Pair<Short, String> d : data) {
            byte[] actual = new byte[100];
            Bip39.bip39_mnemonic_from_word(d.first, actual);
            byte[] expected = d.second.getBytes();
            assertTrue(Objects.deepEquals(
                    expected,
                    Arrays.copyOfRange(actual, 0, expected.length)
            ));
        }

    }

    @Test
    public void testWordsFromSecret() {

        Pair<byte[], short[]>[] data = new Pair[]{
                new Pair<>(
                        Hex.decode("baadf00dbaadf00d"),
                        new short[]{1493, 892, 27, 938, 1784, 55}
                ),
                new Pair<>(
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
                new Pair<>(
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
        };

        for (Pair<byte[], short[]> d : data) {
            short[] actual = new short[100];
            Bip39.bip39_words_from_secret(
                    d.first,
                    d.first.length,
                    actual,
                    actual.length
            );
            assertTrue(Objects.deepEquals(
                    d.second,
                    Arrays.copyOfRange(actual, 0, d.second.length)
            ));
        }
    }

    @Test
    public void testMnemonicFromSecret() {
        Pair<byte[], byte[]>[] data = new Pair[]{
                new Pair(
                        Hex.decode("baadf00dbaadf00d"),
                        "rival hurdle address inspire tenant alone".getBytes()
                ),
                new Pair(
                        Hex.decode("baadf00dbaadf00dbaadf00dbaadf00d"),
                        "rival hurdle address inspire tenant almost turkey safe asset step lab boy"
                                .getBytes()
                ),
                new Pair(
                        Hex.decode("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f"),
                        "legal winner thank year wave sausage worth useful legal winner thank yellow"
                                .getBytes()
                )
        };

        for (Pair<byte[], byte[]> d : data) {
            byte[] actual = new byte[1000];
            Bip39.bip39_mnemonics_from_secret(
                    d.first,
                    d.first.length,
                    actual,
                    actual.length
            );
            assertTrue(Objects.deepEquals(
                    d.second,
                    Arrays.copyOfRange(actual, 0, d.second.length)
            ));
        }
    }

    @Test
    public void testWordsFromMnemonic() {
        Pair<short[], String>[] data = new Pair[]{
                new Pair(
                        new short[]{1493, 892, 27, 938, 1784, 55},
                        "rival hurdle address inspire tenant alone"
                ),
                new Pair(
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
                new Pair(
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
                )
        };

        for (Pair<short[], String> d : data) {
            short[] actual = new short[100];
            Bip39.bip39_words_from_mnemonics(
                    d.second,
                    actual,
                    actual.length
            );
            assertTrue(Objects.deepEquals(
                    d.first,
                    Arrays.copyOfRange(actual, 0, d.first.length)
            ));
        }
    }

    @Test
    public void testSecretFromMnemonic() {

        Pair<byte[], String>[] data = new Pair[]{
                new Pair(
                        Hex.decode("baadf00dbaadf00d"),
                        "rival hurdle address inspire tenant alone"
                ),
                new Pair(
                        Hex.decode("baadf00dbaadf00dbaadf00dbaadf00d"),
                        "rival hurdle address inspire tenant almost turkey safe asset step lab boy"
                ),
                new Pair(
                        Hex.decode("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f"),
                        "legal winner thank year wave sausage worth useful legal winner thank yellow"
                )
        };

        for (Pair<byte[], String> d : data) {
            byte[] actual = new byte[32];
            Bip39.bip39_secret_from_mnemonics(
                    d.second,
                    actual,
                    actual.length
            );
            assertTrue(Objects.deepEquals(
                    d.first,
                    Arrays.copyOfRange(actual, 0, d.first.length)
            ));
        }

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
        Log.e(
                "testSeedFromString",
                String.format(
                        "secret: %s \n actual: %s",
                        Hex.toHexString(secret),
                        Hex.toHexString(Arrays.copyOfRange(
                                actual,
                                0,
                                secret.length
                        ))
                )
        );
        assertTrue(Objects.deepEquals(
                secret,
                Arrays.copyOfRange(actual, 0, secret.length)
        ));
    }
}