package utils;

import crypto.Sha256;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

import static crypto.encoder.Raw.RAW;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class RecoveryPhrase {

    private static final int MNEMONIC_WORD_LENGTH = 24;

    private static final int SEED_LENGTH = 512;

    private static final int SEED_ITERATIONS = 2048;

    private final String mnemonicWords;

    private static final String[] WORDS = new String[2048];

    static {
        try {
            File file = new File(RecoveryPhrase.class.getResource("/bip39_eng.txt").getFile());
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (int i = 0; i < WORDS.length; i++) {
                String word = reader.readLine();
                if (word == null) break;
                WORDS[i] = word;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RecoveryPhrase() {
        this(generateMnemonic());
    }

    public RecoveryPhrase(String mnemonicWords) {
        this.mnemonicWords = mnemonicWords;
    }

    public String getMnemonicWords() {
        return mnemonicWords;
    }

    public byte[] toSeed(String passphrase) {
        return toSeed(mnemonicWords, passphrase);
    }

    public static byte[] toSeed(String mnemonic, String passphrase) {
        passphrase = passphrase == null ? "" : passphrase;
        String salt = String.format("mnemonic%s", passphrase);
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA512Digest());
        gen.init(RAW.decode(mnemonic), salt.getBytes(UTF_8), SEED_ITERATIONS);
        return ((KeyParameter) gen.generateDerivedParameters(SEED_LENGTH)).getKey();
    }

    public static String generateMnemonic() {
        // Random entropy
        final byte[] entropy = new byte[32];
        new SecureRandom().nextBytes(entropy);
        final int entropyLength = entropy.length * 8;
        final int checksumLength = entropyLength / 32;

        // Calculate checksum
        final byte mask = (byte) (0xFF << 8 - checksumLength);
        final byte[] bytes = Sha256.hash(entropy);
        final byte checksum = (byte) (bytes[0] & mask);

        // Convert to bit array
        final int length = checksumLength + entropyLength;
        final boolean[] bits = new boolean[length];
        final boolean[] entropyBits = toBits(entropy);
        final boolean[] checksumBits = toBits(new byte[]{checksum});
        System.arraycopy(entropyBits, 0, bits, 0, entropyBits.length);
        System.arraycopy(checksumBits, 0, bits, entropyBits.length, checksumLength);

        int iterations = (entropyLength + checksumLength) / 11;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            boolean[] examinedBits = Arrays.copyOfRange(bits, i * 11, i * 11 + 11);
            int index = toInt(examinedBits);
            builder.append(WORDS[index]);
            if (i < iterations - 1) builder.append(" ");
        }
        return builder.toString();
    }

    private static boolean[] toBits(byte[] value) {
        final int length = value.length * 8;
        final boolean[] bits = new boolean[length];
        for (int i = 0; i < value.length; i++) {
            for (int j = 0; j < 8; j++) {
                bits[8 * i + j] = ((value[i] >>> (7 - j)) & 1) > 0;
            }
        }
        return bits;
    }

    private static int toInt(boolean[] bits) {
        int value = 0;
        for (int i = 0; i < bits.length; i++) {
            boolean isSet = bits[i];
            if (isSet) {
                value += 1 << bits.length - i - 1;
            }
        }
        return value;
    }
}
