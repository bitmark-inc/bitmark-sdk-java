package com.bitmark.apiservice.params;

import com.bitmark.apiservice.utils.annotation.VisibleForTesting;
import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.crypto.key.KeyPair;

import java.util.ArrayList;
import java.util.List;

import static com.bitmark.cryptography.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 9/4/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

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

    @VisibleForTesting
    public List<byte[]> getSignatures() {
        return signatures;
    }

    @Override
    public boolean isSigned() {
        return signatures != null && !signatures.isEmpty();
    }

    protected void checkSigned() {
        if (!isSigned()) throw new UnsupportedOperationException("Params need to be signed before");
    }
}
