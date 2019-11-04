/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.params;

import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.crypto.key.Ed25519KeyPair;
import com.bitmark.cryptography.crypto.key.KeyPair;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.utils.Validator.checkValid;

public abstract class AbsSingleParams implements SingleParams {

    protected byte[] signature;

    @Override
    public byte[] sign(KeyPair key) {
        checkValid(
                () -> key instanceof Ed25519KeyPair && key.isValid(),
                "Invalid key pair"
        );
        return signature = Ed25519.sign(pack(), key.privateKey().toBytes());
    }

    abstract byte[] pack();

    public String getSignature() {
        return HEX.encode(signature);
    }

    @Override
    public boolean isSigned() {
        return signature != null;
    }

    protected void checkSigned() {
        if (!isSigned()) {
            throw new UnsupportedOperationException(
                    "Params need to be signed before");
        }
    }
}
