package params;

/**
 * @author Hieu Pham
 * @since 8/30/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TransferResponseParams extends AbsParams {

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

    @Override
    public String toJson() {
        throw new UnsupportedOperationException("Unsupport right now");
    }
}
