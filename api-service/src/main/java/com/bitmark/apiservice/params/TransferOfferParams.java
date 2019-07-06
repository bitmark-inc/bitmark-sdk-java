package com.bitmark.apiservice.params;

import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.ArrayUtil;
import com.bitmark.apiservice.utils.BinaryPacking;
import com.bitmark.cryptography.crypto.Sha3256;
import com.bitmark.cryptography.crypto.encoder.VarInt;
import com.bitmark.cryptography.crypto.key.KeyPair;

import java.util.Map;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.utils.Validator.checkValid;

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
        checkValid(() -> offeredOwner != null, "invalid offer owner address");
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

    public String getLink() {
        return link;
    }

    public Address getOfferedOwner() {
        return offeredOwner;
    }

    public Map<String, String> getExtraInfo() {
        return extraInfo;
    }

    @Override
    public String toJson() {
        checkSigned();
        return "{\"offer\":{\"extra_info\":{" + getExtraInfoJson() + "},\"record\":{\"link\":\"" +
               link
               + "\",\"owner\":\"" + offeredOwner.getAddress() + "\",\"signature\":\"" +
               HEX.encode(signature) + "\"}}}";
    }

    @Override
    public byte[] sign(KeyPair key) {
        checkValidLink(link);
        return super.sign(key);
    }

    @Override
    byte[] pack() {
        byte[] data = VarInt.writeUnsignedVarInt(0x05);
        data = BinaryPacking.concat(HEX.decode(link), data);
        data = ArrayUtil.concat(data, new byte[]{0x00});
        data = BinaryPacking.concat(offeredOwner.pack(), data);
        return data;
    }

    private void checkValidLink(String link) {
        checkValid(() -> link != null && HEX.decode(link).length == Sha3256.HASH_BYTE_LENGTH,
                   "invalid link");
    }

    private String getExtraInfoJson() {
        return "";
    }
}
