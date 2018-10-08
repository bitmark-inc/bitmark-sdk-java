package apiservice.params;

import apiservice.utils.Address;
import apiservice.utils.ArrayUtil;
import apiservice.utils.BinaryPacking;
import cryptography.crypto.Sha3256;
import cryptography.crypto.encoder.VarInt;
import cryptography.crypto.key.KeyPair;

import static cryptography.crypto.encoder.Hex.HEX;
import static cryptography.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransferParams extends AbsSingleParams {

    private Address owner;

    private String link;

    public TransferParams(Address owner) {
        checkValid(() -> owner != null && owner.isValid(), "Invalid owner address");
        this.owner = owner;
    }

    public TransferParams(Address owner, String link) {
        this(owner);
        setLink(link);
    }

    public void setLink(String link) {
        checkValidLink(link);
        this.link = link;
    }

    @Override
    public String toJson() {
        checkSigned();
        return "{\"transfer\":{\"link\":\"" + link + "\",\"owner\":\"" + owner.getAddress() +
                "\",\"signature\":\"" + HEX.encode(signature) + "\"}}";
    }

    @Override
    public byte[] sign(KeyPair key) {
        checkValidLink(link);
        return super.sign(key);
    }

    @Override
    byte[] pack() {
        byte[] data = VarInt.writeUnsignedVarInt(0x04);
        data = BinaryPacking.concat(HEX.decode(link), data);
        data = ArrayUtil.concat(data, new byte[]{0x00});
        data = BinaryPacking.concat(owner.pack(), data);
        return data;
    }

    private void checkValidLink(String link) {
        checkValid(() -> link != null && HEX.decode(link).length == Sha3256.HASH_LENGTH, "Invalid" +
                " link");
    }
}
