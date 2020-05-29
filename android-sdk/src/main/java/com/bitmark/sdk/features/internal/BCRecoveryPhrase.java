/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2020 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.features.internal;

import com.bitmark.sdk.bcbip39wrapper.bip39.Bip39;

import java.nio.charset.StandardCharsets;

import static com.bitmark.apiservice.utils.ArrayUtil.slice;
import static com.bitmark.cryptography.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 5/28/20
 * Email: hieupham@bitmark.com
 */
public class BCRecoveryPhrase {

    public static final int MNEMONIC_LENGTH = 13;

    private final String[] mnemonicWords;

    private BCRecoveryPhrase(String... mnemonicWords) {
        validate(mnemonicWords);
        this.mnemonicWords = mnemonicWords;
    }

    public static BCRecoveryPhrase fromMnemonicWords(String... mnemonicWords) {
        return new BCRecoveryPhrase(mnemonicWords);
    }

    public static String[] generateMnemonic(byte[] secret) {
        checkValid(
                () -> secret != null && secret.length == Version.TWELVE.getEntropyLength(),
                "invalid secret"
        );
        final int maxLength = 1024;
        byte[] mnemonics = new byte[maxLength];
        int length = Bip39.bip39_mnemonics_from_secret(
                secret,
                secret.length,
                mnemonics,
                maxLength
        );
        mnemonics = slice(mnemonics, 0, length);
        return getMnemonicAsArray(new String(
                mnemonics,
                StandardCharsets.US_ASCII
        ));
    }

    public static BCRecoveryPhrase fromSeed(Seed seed) {
        byte[] secret = seed.getSeedBytes();
        String[] mnemonics = generateMnemonic(secret);
        return new BCRecoveryPhrase(mnemonics);
    }

    public static Seed recoverSeed(String[] mnemonicWord) {
        validate(mnemonicWord);
        final int secretLength = Version.TWELVE.getEntropyLength();
        byte[] secret = new byte[secretLength];
        int length = Bip39.bip39_secret_from_mnemonics(getMnemonicAsString(
                mnemonicWord), secret, secretLength);
        if (length != secretLength) {
            throw new IllegalStateException("cannot recover seed");
        }
        return new SeedTwelve(secret);
    }

    private static String getMnemonicAsString(String[] words) {
        StringBuilder builder = new StringBuilder();
        int length = words.length;
        for (int i = 0; i < length; i++) {
            builder.append(words[i]);
            if (i < length - 1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    private static String[] getMnemonicAsArray(String word) {
        return word.split(" ");
    }

    private static void validate(String... mnemonicWords) {
        checkValid(
                () -> mnemonicWords != null && mnemonicWords.length == MNEMONIC_LENGTH,
                "invalid mnemonic words"
        );
    }

    public String[] getMnemonicWords() {
        return mnemonicWords;
    }

    public Seed recoverSeed() {
        return recoverSeed(mnemonicWords);
    }
}
