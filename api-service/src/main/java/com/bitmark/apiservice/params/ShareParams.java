package com.bitmark.apiservice.params;

import com.bitmark.apiservice.utils.ArrayUtil;
import com.bitmark.apiservice.utils.BinaryPacking;
import com.bitmark.cryptography.crypto.Sha3256;
import com.bitmark.cryptography.crypto.encoder.VarInt;
import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.cryptography.error.ValidateException;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 1/15/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class ShareParams extends AbsSingleParams {

    private String link;

    private int quantity;

    public ShareParams(int quantity, String link) throws ValidateException {
        checkValid(() -> quantity > 0, "invalid quantity, must be greater than zero");
        checkValid(() -> link != null && HEX.decode(link).length == Sha3256.HASH_BYTE_LENGTH,
                   "invalid link");
        this.quantity = quantity;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public byte[] sign(KeyPair key) {
        checkLink(link);
        return super.sign(key);
    }

    @Override
    byte[] pack() {
        byte[] data = VarInt.writeUnsignedVarInt(0x08);
        data = BinaryPacking.concat(HEX.decode(link), data);
        data = ArrayUtil.concat(data, VarInt.writeUnsignedVarInt(quantity));
        return data;
    }

    @Override
    public String toJson() {
        checkSigned();
        return "{\"share\":{\"link\":\"" + link + "\",\"quantity\":" + quantity +
               ",\"signature\":\"" + HEX.encode(signature) + "\"}}";
    }

    private static void checkLink(String link) {
        if (!isValidLink(link)) throw new IllegalArgumentException("Invalid link");
    }

    private static boolean isValidLink(String link) {
        return link != null && !link.isEmpty() &&
               HEX.decode(link).length == Sha3256.HASH_BYTE_LENGTH;
    }
}
