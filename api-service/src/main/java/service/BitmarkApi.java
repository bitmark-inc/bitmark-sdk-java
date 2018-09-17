package service;

import service.params.*;
import service.params.query.QueryParams;
import service.response.GetBitmarkResponse;
import service.response.GetBitmarksResponse;
import service.response.IssueResponse;
import service.response.RegistrationResponse;
import utils.callback.Callback1;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface BitmarkApi {

    void issueBitmark(IssuanceParams params, Callback1<IssueResponse> callback);

    void registerAsset(RegistrationParams params, Callback1<RegistrationResponse> callback);

    void transferBitmark(TransferParams params, Callback1<String> callback);

    void offerBitmark(TransferOfferParams params, Callback1<String> callback);

    void respondBitmarkOffer(TransferResponseParams params, Callback1<String> callback);

    void get(String bitmarkId, boolean includeAsset, Callback1<GetBitmarkResponse> callback);

    void list(QueryParams params, Callback1<GetBitmarksResponse> callback);

}
