package com.bitmark.apiservice.params;

import com.bitmark.apiservice.utils.annotation.VisibleForTesting;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.BinaryPacking;
import com.bitmark.cryptography.crypto.encoder.VarInt;
import com.bitmark.cryptography.error.ValidateException;

import static com.bitmark.apiservice.utils.ArrayUtil.isDuplicate;
import static com.bitmark.cryptography.crypto.Random.secureRandomInt;
import static com.bitmark.cryptography.crypto.Random.secureRandomInts;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.utils.Validator.checkValid;
import static com.bitmark.cryptography.utils.Validator.checkValidHex;

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

    private com.bitmark.apiservice.utils.Address owner;

    public IssuanceParams(String assetId, com.bitmark.apiservice.utils.Address owner) throws ValidateException {
        checkValidHex(assetId);
        checkValid(() -> HEX.decode(assetId).length <= ASSET_ID_LENGTH);
        checkValid(() -> owner != null && owner.isValid(), "Invalid Address");
        this.assetId = assetId;
        this.owner = owner;
        this.nonces = new int[]{secureRandomInt()};
    }

    public IssuanceParams(String assetId, com.bitmark.apiservice.utils.Address owner, int[] nonces) throws ValidateException {
        this(assetId, owner);
        checkValid(() -> nonces != null && nonces.length > 0 && !com.bitmark.apiservice.utils.ArrayUtil.isDuplicate(nonces) && com.bitmark.apiservice.utils.ArrayUtil.isPositive(nonces));
        this.nonces = nonces;
    }

    public IssuanceParams(String assetId, Address owner, int quantity) throws ValidateException {
        this(assetId, owner);
        checkValid(() -> quantity > 0);
        int[] examinedNonce = secureRandomInts(quantity);
        checkValid(() -> !com.bitmark.apiservice.utils.ArrayUtil.isDuplicate(examinedNonce) && com.bitmark.apiservice.utils.ArrayUtil.isPositive(examinedNonce));
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
        data = com.bitmark.apiservice.utils.BinaryPacking.concat(assetId, data);
        data = BinaryPacking.concat(owner.pack(), data);
        data = com.bitmark.apiservice.utils.ArrayUtil.concat(data, VarInt.writeUnsignedVarInt(nonces[index]));
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
