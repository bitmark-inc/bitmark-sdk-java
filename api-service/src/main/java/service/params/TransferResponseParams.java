package service.params;

import annotation.VisibleForTesting;
import config.SdkConfig;
import crypto.Ed25519;
import crypto.encoder.VarInt;
import crypto.key.KeyPair;
import utils.Address;
import utils.ArrayUtil;
import utils.BinaryPacking;
import utils.record.OfferRecord;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static crypto.encoder.Hex.HEX;
import static crypto.encoder.Raw.RAW;
import static utils.Validator.checkNonNull;
import static utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 8/30/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransferResponseParams extends AbsSingleParams {

    public enum Response {
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

    public TransferResponseParams(OfferRecord offer, Response response) {
        checkNonNull(offer);
        checkNonNull(response);
        this.offer = offer;
        this.response = response;
    }

    @Override
    byte[] pack() {
        byte[] data = VarInt.writeUnsignedVarInt(SdkConfig.Transfer.OFFER_TAG);
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
        String requester = offer.getOwner();
        String message = String.format("updateOffer|%s|%s|%s", offer.getId(),
                requester, String.valueOf(time));
        byte[] signature = Ed25519.sign(RAW.decode(message),
                key.privateKey().toBytes());
        headers.put("requester", requester);
        headers.put("timestamp", String.valueOf(time));
        headers.put("signature", HEX.encode(signature));
        return headers;
    }

    public boolean isAccept() {
        return response == Response.ACCEPT;
    }
}
