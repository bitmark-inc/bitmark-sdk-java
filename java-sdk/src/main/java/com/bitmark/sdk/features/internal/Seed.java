package com.bitmark.sdk.features.internal;

import com.bitmark.apiservice.configuration.Network;
import com.bitmark.cryptography.crypto.key.KeyPair;

import java.util.Locale;


/**
 * @author Hieu Pham
 * @since 1/4/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public interface Seed {

    byte[] getSeedBytes();

    String getEncodedSeed();

    KeyPair getAuthKeyPair();

    KeyPair getEncKeyPair();

    RecoveryPhrase getRecoveryPhrase(Locale locale);

    Version getVersion();

    int length();

    Network getNetwork();

}
