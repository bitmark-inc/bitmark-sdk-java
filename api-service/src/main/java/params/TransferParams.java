package params;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransferParams extends AbsParams {

    private String receiverAccountNumber;

    private String prevTxId;

    public TransferParams(String receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public void setPrevTxId(String prevTxId) {
        this.prevTxId = prevTxId;
    }

    @Override
    public String toJson() {
        throw new UnsupportedOperationException("Unsupport right now");
    }
}
