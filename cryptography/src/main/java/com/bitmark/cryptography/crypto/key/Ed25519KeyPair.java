package com.bitmark.cryptography.crypto.key;

import com.bitmark.cryptography.crypto.Ed25519;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Ed25519KeyPair implements KeyPair {

    private final byte[] publicKey;

    private final byte[] privateKey;

    public static Ed25519KeyPair from(byte[] publicKey, byte[] privateKey) {
        return new Ed25519KeyPair(publicKey, privateKey);
    }

    protected Ed25519KeyPair(byte[] publicKey, byte[] privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    @Override
    public PublicKey publicKey() {
        return PublicKey.from(publicKey);
    }

    @Override
    public PrivateKey privateKey() {
        return PrivateKey.from(privateKey);
    }

    @Override
    public boolean isValid() {
        boolean isValidLength = publicKey.length == Ed25519.PUBLIC_KEY_LENGTH &&
                                privateKey.length == Ed25519.PRIVATE_KEY_LENGTH;
        if (!isValidLength) return false;
        final byte[] message = "bitmark_sdk_java".getBytes();
        final byte[] sig = Ed25519.sign(message, privateKey);
        return Ed25519.verify(sig, message, publicKey);
    }
}
