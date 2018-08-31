package service.params;

import crypto.key.KeyPair;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public abstract class AbsParams implements Params {

    protected byte[] signature;

    @Override
    public void sign(KeyPair key) {
        throw new UnsupportedOperationException("Unsupport right now");
    }
}
