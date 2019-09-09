package com.bitmark.sdk.features.internal;

import com.bitmark.cryptography.error.ValidateException;

import java.util.Locale;

import static com.bitmark.cryptography.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 1/4/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
abstract class AbsSeed implements Seed {

    static final int CHECKSUM_LENGTH = 4;

    byte[] seedBytes;

    AbsSeed(byte[] seedBytes) throws ValidateException {
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
