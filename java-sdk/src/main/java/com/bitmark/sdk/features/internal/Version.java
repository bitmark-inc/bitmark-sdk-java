/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.features.internal;

import com.bitmark.cryptography.error.ValidateException;

public enum Version {

    TWELVE,
    TWENTY_FOUR;

    public static Version fromMnemonicWords(String... words) {
        int length = words.length;
        Version version = length == 12
                          ? TWELVE
                          : length == 24 ? TWENTY_FOUR : null;
        if (version == null) {
            throw new ValidateException("Invalid mnemonic word length " + length);
        }
        return version;
    }

    public static Version fromEntropy(byte[] entropy) {
        int length = entropy.length;
        Version version = length == 17 ? TWELVE : length == 33 ? TWENTY_FOUR :
                                                  null;
        if (version == null) {
            throw new ValidateException("Invalid entropy length " + length);
        }
        return version;
    }

    public static boolean matchesMnemonicWords(String... words) {
        try {
            fromMnemonicWords(words);
            return true;
        } catch (ValidateException e) {
            return false;
        }
    }

    public static boolean matchesEntropy(byte[] entropy) {
        try {
            fromEntropy(entropy);
            return true;
        } catch (ValidateException e) {
            return false;
        }
    }

    public int getMnemonicWordsLength() {
        return this == TWELVE ? 12 : 24;
    }

    public int getEntropyLength() {
        return this == TWELVE ? 17 : 33;
    }
}
