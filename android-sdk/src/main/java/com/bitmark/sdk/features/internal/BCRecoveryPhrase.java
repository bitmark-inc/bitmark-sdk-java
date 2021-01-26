/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2020 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.features.internal;

import com.bc.bip39.Bip39;
import com.bitmark.sdk.utils.StringUtils;

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
        return Bip39.encode(secret).split(" ");
    }

    public static BCRecoveryPhrase fromSeed(Seed seed) {
        byte[] secret = seed.getSeedBytes();
        String[] mnemonics = generateMnemonic(secret);
        return new BCRecoveryPhrase(mnemonics);
    }

    public static Seed recoverSeed(String[] mnemonicWord) {
        validate(mnemonicWord);
        return new SeedTwelve(Bip39.decode(StringUtils.join(" ", mnemonicWord)));
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
