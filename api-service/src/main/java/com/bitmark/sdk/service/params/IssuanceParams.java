package com.bitmark.sdk.service.params;

import com.bitmark.sdk.crypto.encoder.VarInt;
import com.bitmark.sdk.error.ValidateException;
import com.bitmark.sdk.utils.Address;
import com.bitmark.sdk.utils.ArrayUtil;
import com.bitmark.sdk.utils.BinaryPacking;
import com.bitmark.sdk.utils.annotation.VisibleForTesting;

import static com.bitmark.sdk.crypto.Random.secureRandomInt;
import static com.bitmark.sdk.crypto.Random.secureRandomInts;
import static com.bitmark.sdk.crypto.encoder.Hex.HEX;
import static com.bitmark.sdk.utils.ArrayUtil.isDuplicate;
import static com.bitmark.sdk.utils.ArrayUtil.isPositive;
import static com.bitmark.sdk.utils.Validator.checkValid;
import static com.bitmark.sdk.utils.Validator.checkValidHex;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class IssuanceParams extends AbsMultipleParams {

    private static final int ASSET_ID_LENGTH = 64;

    private String assetId;

    private int[] nonces;

    private Address owner;

    public IssuanceParams(String assetId, Address owner) throws ValidateException {
        checkValidHex(assetId);
        checkValid(() -> HEX.decode(assetId).length <= ASSET_ID_LENGTH);
        checkValid(() -> owner != null && owner.isValid(), "Invalid Address");
        this.assetId = assetId;
        this.owner = owner;
        this.nonces = new int[]{secureRandomInt()};
    }

    public IssuanceParams(String assetId, Address owner, int[] nonces) throws ValidateException {
        this(assetId, owner);
        checkValid(() -> nonces != null && nonces.length > 0 && !isDuplicate(nonces) && isPositive(nonces));
        this.nonces = nonces;
    }

    public IssuanceParams(String assetId, Address owner, int quantity) throws ValidateException {
        this(assetId, owner);
        checkValid(() -> quantity > 0);
        int[] examinedNonce = secureRandomInts(quantity);
        checkValid(() -> !isDuplicate(examinedNonce) && isPositive(examinedNonce));
        nonces = examinedNonce;
    }

    @VisibleForTesting
    public int[] getNonces() {
        return nonces;
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
        data = ArrayUtil.concat(data, VarInt.writeUnsignedVarInt(nonces[index]));
        return data;
    }

    @Override
    int size() {
        return nonces.length;
    }

    private String buildSingleJson(int index) {
        return "{\"owner\":\"" + owner.getAddress() + "\",\"signature\":\"" + HEX.encode(signatures.get(index)) + "\"," +
                "\"asset_id\":\"" + assetId + "\",\"nonce\":" + nonces[index] + "}";
    }
}
