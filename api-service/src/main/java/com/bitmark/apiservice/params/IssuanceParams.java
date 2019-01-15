package com.bitmark.apiservice.params;

import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.ArrayUtil;
import com.bitmark.apiservice.utils.BinaryPacking;
import com.bitmark.apiservice.utils.annotation.VisibleForTesting;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.cryptography.crypto.encoder.VarInt;
import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.cryptography.error.ValidateException;

import java.util.List;

import static com.bitmark.apiservice.utils.ArrayUtil.concat;
import static com.bitmark.cryptography.crypto.Random.secureRandomInts;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.utils.Validator.checkValid;
import static com.bitmark.cryptography.utils.Validator.checkValidHex;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.¬
 */

public class IssuanceParams extends AbsMultipleParams {

    private static final int ASSET_ID_LENGTH = 64;

    private String assetId;

    private int[] nonces;

    private Address owner;

    private int quantity;

    public IssuanceParams(String assetId, Address owner) throws ValidateException {
        this(assetId, owner, 1);
    }

    public IssuanceParams(String assetId, Address owner, int quantity) throws ValidateException {
        checkValidHex(assetId);
        checkValid(() -> HEX.decode(assetId).length <= ASSET_ID_LENGTH);
        checkValid(() -> owner != null && owner.isValid(), "Invalid Address");
        checkValid(() -> quantity > 0);
        this.assetId = assetId;
        this.owner = owner;
        this.quantity = quantity;
    }

    public void generateNonces(AssetRecord.Status assetStatus)
            throws ValidateException {
        checkValid(() -> quantity > 0 && assetStatus != null, "Invalid params");
        if (assetStatus == AssetRecord.Status.PENDING) {
            nonces = quantity == 1 ? new int[]{0} : concat(new int[]{0},
                                                           secureRandomInts(quantity - 1));
        } else nonces = secureRandomInts(quantity);
        checkValid(
                () -> !ArrayUtil.isDuplicate(nonces) && ArrayUtil.isPositive(nonces),
                "Invalid generate nonce. The generated nonce cannot be duplicated and positive");
    }

    public int[] getNonces() {
        return nonces;
    }

    public String getAssetId() {
        return assetId;
    }

    public Address getOwner() {
        return owner;
    }

    @VisibleForTesting
    public void setNonces(int[] nonces) {
        this.nonces = nonces;
    }

    @Override
    public List<byte[]> sign(KeyPair key) {
        checkNonces();
        return super.sign(key);
    }

    @Override
    public String toJson() {
        checkSigned();
        final StringBuilder builder = new StringBuilder();
        builder.append("{\"issues\":[");
        for (int i = 0, size = size(); i < size; i++) {
            builder.append(buildSingleJson(i));
            if (i < size - 1) builder.append(",");
        }
        builder.append("]}");
        return builder.toString();
    }

    @Override
    byte[] pack(int index) {
        final byte[] assetId = HEX.decode(this.assetId);
        byte[] data = VarInt.writeUnsignedVarInt(0x03);
        data = BinaryPacking.concat(assetId, data);
        data = BinaryPacking.concat(owner.pack(), data);
        data = concat(data, VarInt.writeUnsignedVarInt(nonces[index]));
        return data;
    }

    @Override
    int size() {
        return nonces.length;
    }

    private String buildSingleJson(int index) {
        return "{\"owner\":\"" + owner.getAddress() + "\",\"signature\":\"" +
               HEX.encode(signatures.get(index)) + "\"," +
               "\"asset_id\":\"" + assetId + "\",\"nonce\":" + nonces[index] + "}";
    }

    private void checkNonces() {
        if (nonces == null || nonces.length == 0)
            throw new IllegalArgumentException("Invalid nonce value");
    }
}
