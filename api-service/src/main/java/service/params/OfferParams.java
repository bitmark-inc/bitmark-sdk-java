package service.params;

/**
 * @author Hieu Pham
 * @since 8/30/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class OfferParams extends AbsSingleParams {

    private String receiverAccountNumber;

    private String prevTxId;

    public OfferParams(String receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public void setPrevTxId(String prevTxId) {
        this.prevTxId = prevTxId;
    }

    @Override
    public String toJson() {
        throw new UnsupportedOperationException("Unsupport right now");
    }

    @Override
    byte[] pack() {
        return new byte[0];
    }
}
