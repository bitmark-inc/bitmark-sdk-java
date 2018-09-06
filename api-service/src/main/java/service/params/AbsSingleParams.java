package service.params;

import crypto.Ed25519;
import crypto.key.KeyPair;

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
        return signature = Ed25519.sign(pack(), key.privateKey().toBytes());
    }

    abstract byte[] pack();
}
