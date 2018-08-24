import features.Asset;
import features.Bitmark;
import features.SdkInterface;

/**
 * @author Hieu Pham
 * @since 8/22/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class BitmarkSDK {

    private SdkInterface sdkInterface = new SdkInterface();

    private static volatile BitmarkSDK INSTANCE;

    private BitmarkSDK() {
    }

    public static void init() {
        if (INSTANCE == null) {
            synchronized (BitmarkSDK.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BitmarkSDK();
                } else throw new UnsupportedOperationException("Sdk must be initialized once");
            }
        } else throw new UnsupportedOperationException("Sdk must be initialized once");
    }

    public static Asset asset() {
        validate();
        return INSTANCE.sdkInterface.getAsset();
    }

    public static Bitmark bitmark() {
        validate();
        return INSTANCE.sdkInterface.getBitmark();
    }

    private static void validate() {
        if (INSTANCE == null) throw new UnsupportedOperationException("You must call BitmarkSDK" +
                ".init() first");
    }

}

