/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.params;

import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.ArrayUtil;
import com.bitmark.apiservice.utils.BinaryPacking;
import com.bitmark.apiservice.utils.annotation.VisibleForTesting;
import com.bitmark.apiservice.utils.record.ShareGrantRecord;
import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.crypto.encoder.VarInt;
import com.bitmark.cryptography.crypto.key.KeyPair;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.crypto.encoder.Raw.RAW;
import static com.bitmark.cryptography.utils.Validator.checkValid;

public class GrantResponseParams extends AbsSingleParams {

    private ShareGrantRecord shareGrantRecord;
    private Response response;
    private KeyPair keyPair;

    private GrantResponseParams(
            ShareGrantRecord shareGrantRecord,
            Response response
    ) {
        checkValid(
                () -> shareGrantRecord != null && shareGrantRecord.isValid(),
                "invalid ShareGrantRecord"
        );
        checkValid(() -> response != null, "invalid Response");
        this.shareGrantRecord = shareGrantRecord;
        this.response = response;
    }

    public static GrantResponseParams accept(ShareGrantRecord shareGrantRecord) {
        return new GrantResponseParams(shareGrantRecord, Response.ACCEPT);
    }

    public static GrantResponseParams reject(ShareGrantRecord shareGrantRecord) {
        return new GrantResponseParams(shareGrantRecord, Response.REJECT);
    }

    public static GrantResponseParams cancel(ShareGrantRecord shareGrantRecord) {
        return new GrantResponseParams(shareGrantRecord, Response.CANCEL);
    }

    @Override
    byte[] pack() {
        byte[] data = VarInt.writeUnsignedVarInt(0x09);
        data = BinaryPacking.concat(
                HEX.decode(shareGrantRecord.getShareId()),
                data
        );
        data = ArrayUtil.concat(
                data,
                VarInt.writeUnsignedVarInt(shareGrantRecord.getQuantity())
        );
        data = BinaryPacking.concat(Address.fromAccountNumber(shareGrantRecord.getOwner())
                .pack(), data);
        data = BinaryPacking.concat(Address.fromAccountNumber(shareGrantRecord.getReceiver())
                .pack(), data);
        data = ArrayUtil.concat(
                data,
                VarInt.writeUnsignedVarInt(shareGrantRecord.getBeforeBlock())
        );
        data = BinaryPacking.concat(
                HEX.decode(shareGrantRecord.getSignature()),
                data
        );
        return data;
    }

    @Override
    public String toJson() {
        if (isAccept()) {
            checkSigned();
        }
        return "{\"id\":\"" + shareGrantRecord.getId() + "\",\"action\":\"" + response
                .value() + "\"" + (isAccept() ? ",\"countersignature\":\"" + HEX
                .encode(signature) + "\"" : "") + "}";
    }

    @Override
    public byte[] sign(KeyPair keyPair) {
        this.keyPair = keyPair;
        return super.sign(keyPair);
    }

    public Map<String, String> buildHeaders() {
        return buildHeaders(Calendar.getInstance().getTimeInMillis());
    }

    @VisibleForTesting
    public Map<String, String> buildHeaders(long time) {
        checkValid(
                () -> keyPair != null && keyPair.isValid(),
                "Invalid or missing key for signing"
        );
        Map<String, String> headers = new HashMap<>();
        String requester =
                isCancel()
                ? shareGrantRecord.getOwner()
                : shareGrantRecord.getReceiver();
        String message = String.format(
                "updateOffer|%s|%s|%s",
                shareGrantRecord.getId(),
                requester,
                String.valueOf(time)
        );
        byte[] signature = Ed25519.sign(
                RAW.decode(message),
                keyPair.privateKey().toBytes()
        );
        headers.put("requester", requester);
        headers.put("timestamp", String.valueOf(time));
        headers.put("signature", HEX.encode(signature));
        return headers;
    }

    public ShareGrantRecord getShareGrantRecord() {
        return shareGrantRecord;
    }

    public boolean isAccept() {
        return response == Response.ACCEPT;
    }

    private boolean isCancel() {
        return response == Response.CANCEL;
    }

    enum Response {
        ACCEPT("accept"),
        REJECT("reject"),
        CANCEL("cancel");

        private String value;

        Response(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }
}
