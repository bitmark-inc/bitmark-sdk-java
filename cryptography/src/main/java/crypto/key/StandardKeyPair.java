package crypto.key;

import static utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class StandardKeyPair implements KeyPair {

    private final byte[] publicKey;

    private final byte[] privateKey;

    public static StandardKeyPair from(byte[] publicKey, byte[] privateKey) {
        return new StandardKeyPair(publicKey, privateKey);
    }

    protected StandardKeyPair(byte[] publicKey, byte[] privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        checkValid(this::isValid);
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
        return publicKey.length % 32 == 0 && privateKey.length % 32 == 0;
    }
}
