package service.params;

import annotation.VisibleForTesting;
import config.SdkConfig;
import crypto.Ed25519;
import crypto.encoder.VarInt;
import crypto.key.KeyPair;
import utils.ArrayUtil;
import utils.BinaryPacking;
import utils.record.TransferOffer;

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

    private TransferOffer transferOffer;

    private Response response;

    private KeyPair offeredOwnerKey;

    public TransferResponseParams(TransferOffer transferOffer, Response response) {
        checkNonNull(transferOffer);
        checkNonNull(response);
        this.transferOffer = transferOffer;
        this.response = response;
    }

    @Override
    byte[] pack() {
        byte[] data = VarInt.writeUnsignedVarInt(SdkConfig.Transfer.OFFER_TAG);
        data = BinaryPacking.concat(HEX.decode(transferOffer.getLink()), data);
        data = ArrayUtil.concat(data, new byte[]{0x00});
        data = BinaryPacking.concat(transferOffer.getOfferedOwner().pack(), data);
        data = BinaryPacking.concat(transferOffer.getSignature(), data);
        return data;
    }

    @Override
    public byte[] sign(KeyPair key) {
        checkValid(this::isAccept, "Only accept need to be signed");
        this.offeredOwnerKey = key;
        return super.sign(key);
    }

    @Override
    public String toJson() {
        if (isAccept()) checkSigned();
        return "{\"action\":\"" + response.value + (isAccept() ? "\",\"countersignature\":\""
                + HEX.encode(signature) : "") + "\",\"id\":\"" + transferOffer.getOfferId() + "\"}";
    }

    public Map<String, String> buildHeaders() {
        return buildHeaders(Calendar.getInstance().getTimeInMillis());
    }

    @VisibleForTesting
    public Map<String, String> buildHeaders(long time) {
        checkSigned();
        Map<String, String> headers = new HashMap<>();
        String requester = transferOffer.getOfferedOwner().getAddress();
        String message = String.format("updateOffer|%s|%s|%s", transferOffer.getOfferId(),
                requester, String.valueOf(time));
        byte[] signature = Ed25519.sign(RAW.decode(message),
                offeredOwnerKey.privateKey().toBytes());
        headers.put("requester", requester);
        headers.put("timestamp", String.valueOf(time));
        headers.put("signature", HEX.encode(signature));
        return headers;
    }

    private boolean isAccept() {
        return response == Response.ACCEPT;
    }
}
