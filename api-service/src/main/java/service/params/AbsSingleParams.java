package service.params;

import annotation.VisibleForTesting;
import crypto.Ed25519;
import crypto.key.KeyPair;

import static crypto.encoder.Hex.HEX;
import static utils.Validator.checkValid;

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
}
