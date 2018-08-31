package service.params;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class IssuanceParams extends AbsParams {

    private String assetId;

    private int[] nonce;

    public IssuanceParams(String assetId) {
        this.assetId = assetId;
    }

    @Override
    public String toJson() {
        throw new UnsupportedOperationException("Unsupport right now");
    }

    public void setNonce(int... nonce) {
        this.nonce = nonce;
    }
}
