package com.bitmark.sdk.service.params;

import com.bitmark.sdk.config.SdkConfig;
import com.bitmark.sdk.crypto.encoder.VarInt;
import com.bitmark.sdk.crypto.key.KeyPair;
import com.bitmark.sdk.utils.Address;
import com.bitmark.sdk.utils.ArrayUtil;
import com.bitmark.sdk.utils.BinaryPacking;

import java.util.Map;

import static com.bitmark.sdk.config.SdkConfig.Transfer.LINK_LENGTH;
import static com.bitmark.sdk.crypto.encoder.Hex.HEX;
import static com.bitmark.sdk.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 8/30/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransferOfferParams extends AbsSingleParams {

    private Address offeredOwner;

    private String link;

    private Map<String, String> extraInfo;

    public TransferOfferParams(Address offeredOwner) {
        checkValid(() -> offeredOwner != null && offeredOwner.isValid(), "Invalid offer owner " +
                "address");
        this.offeredOwner = offeredOwner;
    }

    public TransferOfferParams(Address offeredOwner, String link) {
        this(offeredOwner);
        setLink(link);
    }

    public void setExtraInfo(Map<String, String> extraInfo) {
        this.extraInfo = extraInfo;
    }

    public void setLink(String link) {
        checkValidLink(link);
        this.link = link;
    }

    @Override
    public String toJson() {
        checkSigned();
        return "{\"offer\":{\"extra_info\":{" + getExtraInfoJson() + "},\"record\":{\"link\":\"" + link
                + "\",\"owner\":\"" + offeredOwner.getAddress() + "\",\"signature\":\"" + HEX.encode(signature) + "\"}}}";
    }

    @Override
    public byte[] sign(KeyPair key) {
        checkValidLink(link);
        return super.sign(key);
    }

    @Override
    byte[] pack() {
        byte[] data = VarInt.writeUnsignedVarInt(SdkConfig.Transfer.OFFER_TAG);
        data = BinaryPacking.concat(HEX.decode(link), data);
        data = ArrayUtil.concat(data, new byte[]{0x00});
        data = BinaryPacking.concat(offeredOwner.pack(), data);
        return data;
    }

    private void checkValidLink(String link) {
        checkValid(() -> link != null && HEX.decode(link).length == LINK_LENGTH, "Invalid link");
    }

    private String getExtraInfoJson() {
        return "";
    }
}
