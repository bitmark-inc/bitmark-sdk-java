package utils.record;

import utils.Address;

import java.util.Map;

import static config.SdkConfig.Transfer.LINK_LENGTH;
import static crypto.encoder.Hex.HEX;
import static utils.Validator.checkValid;
import static utils.Validator.checkValidHex;

/**
 * @author Hieu Pham
 * @since 9/11/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransferOffer implements Record {

    private String offerId;

    private Address offeredOwner;

    private String link;

    private Map<String, String> extraInfo;

    private byte[] signature;

    public TransferOffer(String offerId, Address offeredOwner, String link, String hexSignature) {
        checkValidHex(hexSignature);
        checkValid(() -> offerId != null && !offerId.isEmpty() && offeredOwner != null && offeredOwner.isValid() && link != null && HEX.decode(link).length == LINK_LENGTH, "Invalid params");
        this.offerId = offerId;
        this.offeredOwner = offeredOwner;
        this.link = link;
        this.signature = HEX.decode(hexSignature);
    }

    public void setExtraInfo(Map<String, String> extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getOfferId() {
        return offerId;
    }

    public Address getOfferedOwner() {
        return offeredOwner;
    }

    public String getLink() {
        return link;
    }

    public Map<String, String> getExtraInfo() {
        return extraInfo;
    }

    public byte[] getSignature() {
        return signature;
    }
}
