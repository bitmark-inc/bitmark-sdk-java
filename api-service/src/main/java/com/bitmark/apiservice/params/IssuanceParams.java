package com.bitmark.apiservice.params;

import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.ArrayUtil;
import com.bitmark.apiservice.utils.BinaryPacking;
import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.error.UnexpectedException;
import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.crypto.encoder.VarInt;
import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.cryptography.error.ValidateException;

import java.util.ArrayList;
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

    private Address owner;

    private Boolean containsGenesisBitmark;

    // Hold the nonces for both case of issuance : contains and not contains genesis Bitmark
    private Pair<int[], int[]> noncesPair;

    // Hold the nonces for both case of issuance : contains and not contains genesis Bitmark
    private Pair<List<byte[]>, List<byte[]>> signaturePair;

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
        generateNonces(quantity);
    }

    private void generateNonces(int quantity)
            throws ValidateException {
        checkValid(() -> quantity > 0, "Invalid params");
        final int[] genesisNonces = quantity == 1 ? new int[]{0} : concat(new int[]{0},
                                                                          secureRandomInts(
                                                                                  quantity - 1));
        final int[] nonGenesisNonces = secureRandomInts(quantity);
        checkNonces(genesisNonces);
        checkNonces(nonGenesisNonces);
        noncesPair = new Pair<>(genesisNonces, nonGenesisNonces);
    }

    private void checkNonces(int[] nonces) {
        if (!ArrayUtil.isDuplicate(nonces) && ArrayUtil.isPositive(nonces)) return;
        throw new UnexpectedException(
                "Invalid generate nonce. The generated nonce cannot be duplicated and positive");
    }

    public Pair<int[], int[]> getNoncesPair() {
        return noncesPair;
    }

    public String getAssetId() {
        return assetId;
    }

    public Address getOwner() {
        return owner;
    }

    public void setContainsGenesisBitmark(boolean containsGenesisBitmark) {
        this.containsGenesisBitmark = containsGenesisBitmark;
    }

    @Override
    public List<byte[]> sign(KeyPair key) {
        List<byte[]> containGenesisBitmarkSig = sign(key, true);
        List<byte[]> notContainsGenesisBitmarkSig = sign(key, false);
        signaturePair = new Pair<>(containGenesisBitmarkSig, notContainsGenesisBitmarkSig);
        return concat(containGenesisBitmarkSig, notContainsGenesisBitmarkSig);
    }

    @Override
    public String toJson() {
        checkContainsGenesisBitmarkExisted();
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

    private List<byte[]> sign(KeyPair key, boolean containsGenesisBitmark) {
        checkValid(() -> key != null && key.isValid(), "Invalid key pair");
        List<byte[]> signatures = new ArrayList<>(size());
        for (int i = 0; i < size(); i++) {
            signatures
                    .add(Ed25519.sign(pack(i, containsGenesisBitmark), key.privateKey().toBytes()));
        }
        return signatures;
    }

    private byte[] pack(int index, boolean containsGenesisBitmark) {
        final byte[] assetId = HEX.decode(this.assetId);
        byte[] data = VarInt.writeUnsignedVarInt(0x03);
        data = BinaryPacking.concat(assetId, data);
        data = BinaryPacking.concat(owner.pack(), data);
        data = concat(data, VarInt.writeUnsignedVarInt(getNonces(containsGenesisBitmark)[index]));
        return data;
    }

    @Override
    byte[] pack(int index) {
        throw new UnsupportedOperationException("Do not support this function");
    }

    @Override
    int size() {
        // Use arbitrary value containsGenesisBitmark to get the size of nonces
        return getNonces(true).length;
    }

    @Override
    public List<byte[]> getSignatures() {
        checkContainsGenesisBitmarkExisted();
        return getSignatures(containsGenesisBitmark);
    }

    @Override
    public boolean isSigned() {
        return signaturePair != null;
    }

    private String buildSingleJson(int index) {
        return "{\"owner\":\"" + owner.getAddress() + "\",\"signature\":\"" +
               HEX.encode(getSignatures(containsGenesisBitmark).get(index)) + "\"," +
               "\"asset_id\":\"" + assetId + "\",\"nonce\":" +
               getNonces(containsGenesisBitmark)[index] + "}";
    }

    private List<byte[]> getSignatures(boolean containsGenesisBitmark) {
        return containsGenesisBitmark ? signaturePair.first() : signaturePair.second();
    }

    private int[] getNonces(boolean containsGenesisBitmark) {
        return containsGenesisBitmark ? noncesPair.first() : noncesPair.second();
    }

    private void checkContainsGenesisBitmarkExisted() {
        if (containsGenesisBitmark == null)
            throw new IllegalArgumentException(
                    "Need to mark this params is contains genesis bitmark or not");
    }
}
