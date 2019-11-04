/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.features.internal;

import com.bitmark.cryptography.crypto.key.KeyPair;

import java.util.Locale;

import static com.bitmark.cryptography.utils.Validator.checkValid;

abstract class AbsSeed implements Seed {

    static final int CHECKSUM_LENGTH = 4;

    byte[] seedBytes;

    KeyPair authKeyPair;

    KeyPair encKeyPair;

    AbsSeed(byte[] seedBytes) {
        checkValid(
                () -> seedBytes != null && seedBytes.length == length(),
                "Invalid Seed"
        );
        this.seedBytes = seedBytes;
    }

    @Override
    public byte[] getSeedBytes() {
        return seedBytes;
    }

    @Override
    public RecoveryPhrase getRecoveryPhrase(Locale locale) {
        return RecoveryPhrase.fromSeed(this, locale);
    }

}
