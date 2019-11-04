/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.params;

import com.bitmark.apiservice.utils.Address;
import com.bitmark.cryptography.crypto.key.KeyPair;

import java.nio.charset.Charset;
import java.util.*;

import static com.bitmark.cryptography.utils.Validator.checkNonNull;
import static com.bitmark.cryptography.utils.Validator.checkValid;

public class RegisterWsTokenParams extends AbsSingleParams {

    private Address requester;

    private long timestamp;

    public RegisterWsTokenParams(Address requester) {
        checkNonNull(requester);
        this.requester = requester;
    }

    @Override
    byte[] pack() {
        timestamp = Calendar.getInstance().getTimeInMillis();
        final String signableMessage = String.format(
                Locale.getDefault(),
                "register|websocket|%s|%d",
                requester.getAddress(),
                timestamp
        );
        return signableMessage.getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public byte[] sign(KeyPair key) {
        checkValid(
                () -> null != key && Objects.deepEquals(
                        requester.getPublicKey().toBytes(),
                        key.publicKey().toBytes()
                ),
                "invalid public key, the public key must be corresponding to requester"
        );
        return super.sign(key);
    }

    public Map<String, String> buildHeader() {
        checkSigned();
        final Map<String, String> header = new HashMap<>();
        header.put("requester", requester.getAddress());
        header.put("timestamp", String.valueOf(timestamp));
        header.put("signature", getSignature());
        return header;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Address getRequester() {
        return requester;
    }

    @Override
    public String toJson() {
        return "";
    }
}
