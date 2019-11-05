/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.utils;

import com.bitmark.apiservice.configuration.Network;
import com.bitmark.cryptography.crypto.key.PublicKey;

public class AccountNumberData {

    private PublicKey publicKey;

    private Network network;

    public static AccountNumberData from(PublicKey publicKey, Network network) {
        return new AccountNumberData(publicKey, network);
    }

    private AccountNumberData(PublicKey publicKey, Network network) {
        this.publicKey = publicKey;
        this.network = network;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public Network getNetwork() {
        return network;
    }
}
