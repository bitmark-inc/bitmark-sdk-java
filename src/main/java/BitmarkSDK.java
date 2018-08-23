import config.GlobalConfiguration;
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

    private GlobalConfiguration configuration;

    private SdkInterface sdkInterface;

    private static volatile BitmarkSDK INSTANCE;

    private BitmarkSDK(GlobalConfiguration configuration) {
        this.configuration = configuration;
        sdkInterface = new SdkInterface(configuration);
    }

    public static void init(GlobalConfiguration configuration) {
        if (INSTANCE == null) {
            synchronized (BitmarkSDK.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BitmarkSDK(configuration);
                } else throw new UnsupportedOperationException("Sdk must be initialized once");
            }
        } else throw new UnsupportedOperationException("Sdk must be initialized once");
    }

    public Asset asset() {
        return sdkInterface.getAsset();
    }

    public Bitmark bitmark() {
        return sdkInterface.getBitmark();
    }

}

