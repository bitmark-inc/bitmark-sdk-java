package com.bitmark.sdk.utils;

import com.bitmark.sdk.service.configuration.Network;
import com.bitmark.sdk.crypto.key.PublicKey;

/**
 * @author Hieu Pham
 * @since 8/28/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

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
