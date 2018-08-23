package features;

import config.GlobalConfiguration;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class SdkInterface {

    private Asset asset;

    private Bitmark bitmark;

    public SdkInterface(GlobalConfiguration configuration) {

    }

    public Asset getAsset() {
        return asset;
    }

    public Bitmark getBitmark() {
        return bitmark;
    }
}
