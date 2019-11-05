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
import com.bitmark.cryptography.crypto.Sha3256;
import com.bitmark.cryptography.crypto.encoder.VarInt;

import java.util.Map;

import static com.bitmark.apiservice.utils.HttpUtils.mapToJson;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.utils.Validator.checkValid;

public class ShareGrantingParams extends AbsSingleParams {

    private String shareId;

    private int quantity;

    private Address owner;

    private Address receiver;

    private Integer beforeBlock;

    private Map<String, String> extraInfo;

    public ShareGrantingParams(
            String shareId,
            int quantity,
            Address owner,
            Address receiver,
            int beforeBlock
    ) {
        this(shareId, quantity, owner, receiver, beforeBlock, null);
    }

    public ShareGrantingParams(
            String shareId,
            int quantity,
            Address owner,
            Address receiver,
            int beforeBlock,
            Map<String, String> extraInfo
    ) {
        checkValid(() -> isValidShareId(shareId), "invalid share id");
        checkValid(
                () -> quantity > 0,
                "invalid quantity. must be greater than zero"
        );
        checkValid(() -> owner != null, "invalid owner address");
        checkValid(() -> receiver != null, "invalid receiver address");
        checkValid(
                () -> beforeBlock >= 0,
                "invalid before block, must be greater or equal zero"
        );
        this.shareId = shareId;
        this.owner = owner;
        this.receiver = receiver;
        this.quantity = quantity;
        this.beforeBlock = beforeBlock;
        this.extraInfo = extraInfo;
    }

    private static boolean isValidShareId(String shareId) {
        return shareId != null && !shareId.isEmpty() && HEX.decode(shareId).length == Sha3256.HASH_BYTE_LENGTH;
    }

    public String getShareId() {
        return shareId;
    }

    public Address getOwner() {
        return owner;
    }

    public Address getReceiver() {
        return receiver;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getBeforeBlock() {
        return beforeBlock;
    }

    public void setBeforeBlock(Integer beforeBlock) {
        checkValid(
                () -> beforeBlock >= 0,
                "invalid before block, must be greater or equal zero"
        );
        this.beforeBlock = beforeBlock;
    }

    public Map<String, String> getExtraInfo() {
        return extraInfo;
    }

    @Override
    byte[] pack() {
        byte[] data = VarInt.writeUnsignedVarInt(0x09);
        data = BinaryPacking.concat(HEX.decode(shareId), data);
        data = ArrayUtil.concat(data, VarInt.writeUnsignedVarInt(quantity));
        data = BinaryPacking.concat(owner.pack(), data);
        data = BinaryPacking.concat(receiver.pack(), data);
        data = ArrayUtil.concat(data, VarInt.writeUnsignedVarInt(beforeBlock));
        return data;
    }

    @Override
    public String toJson() {
        checkSigned();
        return "{\"record\":{\"shareId\":\"" + shareId +
                "\",\"quantity\":" + quantity +
                ",\"owner\":\"" + owner.getAddress() +
                "\",\"recipient\":\"" + receiver.getAddress() +
                "\",\"beforeBlock\":" + beforeBlock + ",\"signature\":\"" +
                HEX.encode(signature) +
                "\"},\"extra_info\":" + mapToJson(extraInfo) + "}";
    }
}
