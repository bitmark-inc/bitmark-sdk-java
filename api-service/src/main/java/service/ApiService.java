package service;

import config.GlobalConfiguration;
import config.SdkConfig;
import okhttp3.Headers;
import service.params.*;
import service.response.IssueResponse;
import service.response.RegistrationResponse;
import utils.callback.Callback1;

import static service.middleware.Converter.*;

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
    public void issueBitmark(IssuanceParams params, Callback1<IssueResponse> callback) {
        final String path = String.format("/%s/issue", SdkConfig.ApiServer.VERSION);
        postAsync(path, params, toIssueResponse(callback));
    }

    @Override
    public void registerAsset(RegistrationParams params, Callback1<RegistrationResponse> callback) {
        final String path = String.format("/%s/register-asset", SdkConfig.ApiServer.VERSION);
        postAsync(path, params, toRegistrationResponse(callback));
    }

    @Override
    public void transferBitmark(TransferParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", SdkConfig.ApiServer.VERSION);
        postAsync(path, params, toTxId(callback));
    }

    @Override
    public void offerBitmark(TransferOfferParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", SdkConfig.ApiServer.VERSION);
        postAsync(path, params, toOfferId(callback));
    }

    @Override
    public void respondBitmarkOffer(TransferResponseParams params, Callback1<String> callback) {
        final String path = String.format("/%s/transfer", SdkConfig.ApiServer.VERSION);
        Headers headers = Headers.of(params.buildHeaders());
        patchAsync(path, headers, params, toTxId(callback));
    }
}
