/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.features.internal;

import com.bitmark.apiservice.configuration.Network;
import com.bitmark.apiservice.utils.error.UnexpectedException;
import com.bitmark.cryptography.error.ValidateException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bitmark.apiservice.utils.ArrayUtil.*;
import static com.bitmark.cryptography.utils.Validator.checkValid;
import static com.bitmark.sdk.features.internal.Version.TWELVE;
import static com.bitmark.sdk.utils.FileUtils.getResourceAsFile;

public class RecoveryPhrase {

    private final String[] mnemonicWords;

    private static String[] EN_WORDS;

    private static String[] CN_WORDS;

    private static final int[] MASKS = new int[]{
            0,
            1,
            3,
            7,
            15,
            31,
            63,
            127,
            255,
            511,
            1023
    };

    public static String[] getWords(Locale locale) {
        checkValid(
                () -> locale == Locale.ENGLISH || locale == Locale.TRADITIONAL_CHINESE,
                "does not support this locale"
        );
        if (locale == Locale.ENGLISH && EN_WORDS != null) {
            return EN_WORDS;
        }
        if (locale == Locale.TRADITIONAL_CHINESE && CN_WORDS != null) {
            return CN_WORDS;
        }

        try {
            final int size = 2048;
            File file = getResourceAsFile(locale == Locale.ENGLISH
                                          ? "bip/bip39_eng.txt"
                                          : "bip/bip39_cn.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String[] words = new String[size];
            for (int i = 0; i < size; i++) {
                String word = reader.readLine();
                if (word == null) {
                    break;
                }
                words[i] = word;
            }
            if (locale == Locale.ENGLISH) {
                EN_WORDS = words;
            } else {
                CN_WORDS = words;
            }
            return words;
        } catch (IOException e) {
            throw new UnexpectedException(e.getMessage());
        }
    }

    public static RecoveryPhrase fromSeed(Seed seed) {
        return fromSeed(seed, Locale.ENGLISH);
    }

    public static RecoveryPhrase fromSeed(Seed seed, Locale locale) {
        checkValid(() -> seed != null, "invalid seed");
        final byte[] seedBytes = seed.getSeedBytes();
        final Version version = seed.getVersion();
        return version == TWELVE
               ? new RecoveryPhrase(generateMnemonic(seedBytes, locale))
               :
               new RecoveryPhrase(generateMnemonic(concat(toByteArray(seed.getNetwork()
                       .value()), seedBytes), locale));
    }

    public static RecoveryPhrase fromMnemonicWords(String... mnemonicWords) {
        return new RecoveryPhrase(mnemonicWords);
    }

    private RecoveryPhrase(String... mnemonicWords) {
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
        final Version version = Version.fromMnemonicWords(mnemonicWord);
        final int wordsLength = version.getMnemonicWordsLength();
        final int seedBytesLen =
                version == TWELVE
                ? SeedTwelve.SEED_BYTE_LENGTH
                : SeedTwentyFour.SEED_BYTE_LENGTH;
        final int entropyLength = version == TWELVE
                                  ? seedBytesLen
                                  : seedBytesLen + 1;
        final Locale locale = detectLocale(mnemonicWord[0]);
        if (locale == null) {
            throw new ValidateException("does not support this language");
        }

        String[] words = getWords(locale);
        int[] data = new int[]{};
        int remainder = 0;
        int bits = 0;

        for (int i = 0; i < wordsLength; i++) {
            final String word = mnemonicWord[i];
            final int index = indexOf(words, word);
            remainder = (remainder << 11) + index;
            for (bits += 11; bits >= 8; bits -= 8) {
                final int a = 0xFF & (remainder >> (bits - 8));
                data = concat(data, new int[]{a});
            }
            remainder &= MASKS[bits];
        }
        final byte[] entropy = version == TWELVE ? concat(
                toByteArray(data),
                new byte[]{(byte) (remainder << 4)}
        ) : toByteArray(data);
        checkValid(
                () -> entropy.length == entropyLength,
                "invalid mnemonic words"
        );
        if (version == TWELVE) {
            return new SeedTwelve(entropy);
        } else {
            final Network network = Network.valueOf(entropy[0]);
            final byte[] core = slice(entropy, 1, entropyLength);
            return new SeedTwentyFour(core, network);
        }
    }

    public static String[] generateMnemonic(byte[] entropy) {
        return generateMnemonic(entropy, Locale.ENGLISH);
    }

    public static String[] generateMnemonic(byte[] entropy, Locale locale) {
        checkValid(
                () -> entropy != null && Version.matchesEntropy(entropy),
                "invalid entropy"
        );
        final String[] words = getWords(locale);
        final Version version = Version.fromEntropy(entropy);
        final int mnenonicWordsLength = version.getMnemonicWordsLength();
        final int entropyLength = version.getEntropyLength();

        final List<String> mnemonicWords = new ArrayList<>(mnenonicWordsLength);
        final int[] unsignedEntropy = toUInt(entropy);
        int accumulator = 0;
        int bits = 0;
        for (int i = 0; i < entropyLength; i++) {
            accumulator = (accumulator << 8) + unsignedEntropy[i];
            bits += 8;
            if (bits >= 11) {
                bits -= 11;
                int index = accumulator >> bits;
                accumulator &= MASKS[bits];
                mnemonicWords.add(words[index]);
            }
        }
        return mnemonicWords.toArray(new String[mnenonicWordsLength]);
    }

    private static void validate(String... mnemonicWords) {
        checkValid(() -> mnemonicWords != null && Version.matchesMnemonicWords(
                mnemonicWords) && (contains(
                getWords(Locale.ENGLISH),
                mnemonicWords
        ) || contains(getWords(Locale.TRADITIONAL_CHINESE), mnemonicWords)));
    }

    private static Locale detectLocale(String examined) {
        return contains(getWords(Locale.ENGLISH), examined)
               ? Locale.ENGLISH
               : contains(getWords(Locale.TRADITIONAL_CHINESE), examined)
                 ? Locale.TRADITIONAL_CHINESE
                 : null;
    }
}
