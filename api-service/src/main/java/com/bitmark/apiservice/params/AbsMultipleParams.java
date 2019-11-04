/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.params;

import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.crypto.key.KeyPair;

import java.util.ArrayList;
import java.util.List;

import static com.bitmark.cryptography.utils.Validator.checkValid;

public abstract class AbsMultipleParams implements MultipleParams {

    protected List<byte[]> signatures;

    @Override
    public List<byte[]> sign(KeyPair key) {
        checkValid(() -> key != null && key.isValid(), "Invalid key pair");
        signatures = new ArrayList<>(size());
        for (int i = 0; i < size(); i++) {
            signatures.add(Ed25519.sign(pack(i), key.privateKey().toBytes()));
        }
        return signatures;
    }

    abstract byte[] pack(int index);

    abstract int size();

    public List<byte[]> getSignatures() {
        return signatures;
    }

    @Override
    public boolean isSigned() {
        return signatures != null && !signatures.isEmpty();
    }

    protected void checkSigned() {
        if (!isSigned()) {
            throw new UnsupportedOperationException(
                    "Params need to be signed before");
        }
    }
}
