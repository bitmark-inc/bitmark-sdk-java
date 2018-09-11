package features;

import service.ApiService;
import service.params.IssuanceParams;
import utils.callback.Callback1;

import java.util.List;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Bitmark {

    public static void issue(IssuanceParams params, Callback1<List<String>> callback){
        ApiService.getInstance().issueBitmark(params, callback);
    }

}
