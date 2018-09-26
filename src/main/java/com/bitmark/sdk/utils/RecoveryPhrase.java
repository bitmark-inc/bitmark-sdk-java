package com.bitmark.sdk.utils;

import com.bitmark.sdk.error.ValidateException;
import com.bitmark.sdk.service.configuration.GlobalConfiguration;
import com.bitmark.sdk.service.configuration.Network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bitmark.sdk.crypto.Random.secureRandomBytes;
import static com.bitmark.sdk.utils.ArrayUtil.*;
import static com.bitmark.sdk.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class RecoveryPhrase {

    public static final int MNEMONIC_WORD_LENGTH = 24;

    private static final int ENTROPY_LENGTH = 33;

    private final String[] mnemonicWords;

    private static final String[] WORDS = new String[2048];

    private static final int[] MASKS = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023};

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

    public static RecoveryPhrase fromSeed(Seed seed) throws ValidateException {
        checkValid(() -> seed != null && seed.getSeed().length == Seed.LENGTH, "Invalid" +
                " Seed");
        final Network network = seed.getNetwork();
        final byte[] seedData = seed.getSeed();
        return new RecoveryPhrase(generateMnemonic(getEntropy(network, seedData)));
    }

    public static RecoveryPhrase fromMnemonicWords(String... mnemonicWords) throws ValidateException {
        return new RecoveryPhrase(mnemonicWords);
    }

    public RecoveryPhrase() {
        final byte[] randomBytes = secureRandomBytes(ENTROPY_LENGTH - 1);
        final byte[] entropy = getEntropy(GlobalConfiguration.network(), randomBytes);
        this.mnemonicWords = generateMnemonic(entropy);
    }

    private RecoveryPhrase(String... mnemonicWords) throws ValidateException {
        validate(mnemonicWords);
        this.mnemonicWords = mnemonicWords;
    }

    public String[] getMnemonicWords() {
        return mnemonicWords;
    }

    public Seed recoverSeed() {
        return recoverSeed(mnemonicWords);
    }

    public static Seed recoverSeed(String[] mnemonicWord) {
        validate(mnemonicWord);
        int[] data = new int[]{};
        int remainder = 0;
        int bits = 0;

        for (int i = 0; i < MNEMONIC_WORD_LENGTH; i++) {
            final String word = mnemonicWord[i];
            final int index = indexOf(WORDS, word);
            remainder = (remainder << 11) + index;
            for (bits += 11; bits >= 8; bits -= 8) {
                final int a = 0xFF & (remainder >> (bits - 8));
                data = concat(data, new int[]{a});
            }
            remainder &= MASKS[bits];
        }
        final byte[] entropy = toByteArray(data);
        checkValid(() -> entropy.length == ENTROPY_LENGTH, "Invalid mnemonic words");
        final Network network = Network.valueOf(entropy[0]);
        final byte[] seed = slice(entropy, 1, ENTROPY_LENGTH);
        return new Seed(seed, network);
    }

    public static String[] generateMnemonic(byte[] entropy) throws ValidateException {
        checkValid(() -> entropy != null && entropy.length == ENTROPY_LENGTH, "Invalid entropy " +
                "length. The valid length must be " + ENTROPY_LENGTH);
        final List<String> mnemonicWords = new ArrayList<>(MNEMONIC_WORD_LENGTH);
        final int[] unsignedEntropy = toUInt(entropy);
        int accumulator = 0;
        int bits = 0;
        for (int i = 0; i < ENTROPY_LENGTH; i++) {
            accumulator = (accumulator << 8) + unsignedEntropy[i];
            bits += 8;
            if (bits >= 11) {
                bits -= 11;
                int index = accumulator >> bits;
                accumulator &= MASKS[bits];
                mnemonicWords.add(WORDS[index]);
            }
        }
        return mnemonicWords.toArray(new String[MNEMONIC_WORD_LENGTH]);
    }

    private static void validate(String... mnemonicWords) {
        checkValid(() -> mnemonicWords != null && mnemonicWords.length == MNEMONIC_WORD_LENGTH && contains(WORDS, mnemonicWords));
    }

    private static byte[] getEntropy(Network network, byte[] seed) {
        return concat(toByteArray(network.value()), seed);
    }
}
