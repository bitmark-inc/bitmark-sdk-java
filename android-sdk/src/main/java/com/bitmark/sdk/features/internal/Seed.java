/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.features.internal;

import com.bitmark.apiservice.configuration.Network;
import com.bitmark.cryptography.crypto.key.KeyPair;

import java.util.Locale;

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
