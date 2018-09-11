package service;

import config.GlobalConfiguration;
import config.SdkConfig;
import service.params.IssuanceParams;
import service.params.RegistrationParams;
import service.response.RegistrationResponse;
import utils.callback.Callback1;

import java.util.List;

import static service.middleware.Converter.toRegistrationResponse;
import static service.middleware.Converter.toTxIds;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class ApiService extends AbsApiService implements BitmarkApi {

    private static ApiService INSTANCE;

    public static ApiService getInstance() {
        if (INSTANCE == null) {
            synchronized (ApiService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApiService(GlobalConfiguration.apiToken());
                }
            }
        }
        return INSTANCE;
    }

    private ApiService(String apiToken) {
        super(apiToken);
    }

    @Override
    public void issueBitmark(IssuanceParams params, Callback1<List<String>> callback) {
        final String path = String.format("/%s/issue", SdkConfig.ApiServer.VERSION);
        postAsync(path, params, toTxIds(callback));
    }

    @Override
    public void registerAsset(RegistrationParams params, Callback1<RegistrationResponse> callback) {
        final String path = String.format("/%s/registerAsset", SdkConfig.ApiServer.VERSION);
        postAsync(path, params, toRegistrationResponse(callback));
    }
}
