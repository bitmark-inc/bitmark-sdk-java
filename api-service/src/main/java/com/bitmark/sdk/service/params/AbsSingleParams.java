package com.bitmark.sdk.service.params;

import com.bitmark.sdk.utils.annotation.VisibleForTesting;
import com.bitmark.sdk.crypto.Ed25519;
import com.bitmark.sdk.crypto.key.KeyPair;

import static com.bitmark.sdk.crypto.encoder.Hex.HEX;
import static com.bitmark.sdk.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public abstract class AbsSingleParams implements SingleParams {

    protected byte[] signature;

    @Override
    public byte[] sign(KeyPair key) {
        checkValid(() -> key != null && key.isValid(), "Invalid key pair");
        return signature = Ed25519.sign(pack(), key.privateKey().toBytes());
    }

    abstract byte[] pack();

    @VisibleForTesting
    public String getSignature() {
        return HEX.encode(signature);
    }

    @Override
    public boolean isSigned() {
        return signature != null;
    }

    protected void checkSigned() {
        if (!isSigned()) throw new UnsupportedOperationException("Params need to be signed before");
    }
}
