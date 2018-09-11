package features;

import service.ApiService;
import service.params.RegistrationParams;
import service.response.RegistrationResponse;
import utils.callback.Callback1;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Asset {

    public static void register(RegistrationParams params,
                                Callback1<RegistrationResponse> callback) {
        ApiService.getInstance().registerAsset(params, callback);
    }

}
