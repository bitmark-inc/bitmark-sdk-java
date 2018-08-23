package features.impl;

import config.Network;
import crypto.key.PrivateKey;
import crypto.key.PublicKey;
import utils.Callback2;
import utils.RecoveryPhrase;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Account {

    private String accountNumber;

    private PrivateKey privateKey;

    public static Account fromSeed(byte[] seed) {
        throw new UnsupportedOperationException();
    }

    public static Account fromRecoveryPhrase(RecoveryPhrase recoveryPhrase) {
        throw new UnsupportedOperationException();
    }

    public static Account fromRecoveryPhrase(String... mnemonicWords) {
        throw new UnsupportedOperationException();
    }

    public RecoveryPhrase getRecoveryPhrase() {
        throw new UnsupportedOperationException();
    }

    public byte[] getSeed() {
        throw new UnsupportedOperationException();
    }

    public static boolean isValidAccountNumber(String accountNumber) {
        throw new UnsupportedOperationException();
    }

    public static void parseAccountNumber(String accountNumber,
                                          Callback2<Network, PublicKey> callback) {
        throw new UnsupportedOperationException();
    }

}
