package apiservice.params;

import apiservice.utils.Address;
import apiservice.utils.ArrayUtil;
import apiservice.utils.BinaryPacking;
import apiservice.utils.annotation.VisibleForTesting;
import apiservice.utils.record.OfferRecord;
import cryptography.crypto.Ed25519;
import cryptography.crypto.encoder.VarInt;
import cryptography.crypto.key.KeyPair;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static apiservice.params.TransferResponseParams.Response.*;
import static cryptography.crypto.encoder.Hex.HEX;
import static cryptography.crypto.encoder.Raw.RAW;
import static cryptography.utils.Validator.*;

/**
 * @author Hieu Pham
 * @since 8/30/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransferResponseParams extends AbsSingleParams {

    enum Response {
        ACCEPT("accept"), REJECT("reject"), CANCEL("cancel");

        private String value;

        Response(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    private OfferRecord offer;

    private Response response;

    private KeyPair key;

    private String currentOwner;

    public static TransferResponseParams accept(OfferRecord offer) {
        return new TransferResponseParams(offer, ACCEPT);
    }

    public static TransferResponseParams reject(OfferRecord offer) {
        return new TransferResponseParams(offer, REJECT);
    }

    public static TransferResponseParams cancel(OfferRecord offer, String owner) {
        TransferResponseParams params = new TransferResponseParams(offer, CANCEL);
        params.setCurrentOwner(owner);
        return params;
    }

    private TransferResponseParams(OfferRecord offer, Response response) {
        checkNonNull(offer);
        checkNonNull(response);
        this.offer = offer;
        this.response = response;
    }

    @Override
    byte[] pack() {
        byte[] data = VarInt.writeUnsignedVarInt(0x05);
        data = BinaryPacking.concat(HEX.decode(offer.getLink()), data);
        data = ArrayUtil.concat(data, new byte[]{0x00});
        data = BinaryPacking.concat(Address.fromAccountNumber(offer.getOwner()).pack(), data);
        data = BinaryPacking.concat(HEX.decode(offer.getSignature()), data);
        return data;
    }

    @Override
    public byte[] sign(KeyPair key) {
        checkValid(this::isAccept, "Only accept response params need to be signed");
        setSigningKey(key);
        return super.sign(key);
    }

    public void setSigningKey(KeyPair key) {
        this.key = key;
    }

    @Override
    public String toJson() {
        if (isAccept()) checkSigned();
        return "{\"action\":\"" + response.value + (isAccept() ? "\",\"countersignature\":\""
                + HEX.encode(signature) : "") + "\",\"id\":\"" + offer.getId() + "\"}";
    }

    public Map<String, String> buildHeaders() {
        return buildHeaders(Calendar.getInstance().getTimeInMillis());
    }

    @VisibleForTesting
    public Map<String, String> buildHeaders(long time) {
        checkValid(() -> key != null && key.isValid(), "Invalid or missing key for signing");
        Map<String, String> headers = new HashMap<>();
        String requester = isCancel() ? currentOwner : offer.getOwner();
        String message = String.format("updateOffer|%s|%s|%s", offer.getId(),
                requester, String.valueOf(time));
        byte[] signature = Ed25519.sign(RAW.decode(message),
                key.privateKey().toBytes());
        headers.put("requester", requester);
        headers.put("timestamp", String.valueOf(time));
        headers.put("signature", HEX.encode(signature));
        return headers;
    }

    private void setCurrentOwner(String owner) {
        checkValidString(owner);
        this.currentOwner = owner;
    }

    public boolean isAccept() {
        return response == ACCEPT;
    }

    private boolean isCancel() {
        return response == Response.CANCEL;
    }
}
