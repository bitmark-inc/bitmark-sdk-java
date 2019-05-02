package com.bitmark.sdk.sample.samples;

import com.bitmark.apiservice.params.IssuanceParams;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.Bitmark;

import java.util.List;

import static com.bitmark.apiservice.utils.Awaitility.await;

public class BitmarkIssuanceSample {
    public static List<String> issueBitmarks(Account issuer, String assetId, int quantity) throws Throwable {
        IssuanceParams issuanceParams = new IssuanceParams(assetId, issuer.toAddress(), quantity);
        issuanceParams.sign(issuer.getKeyPair());
        List<String> bitmarkIds = await(callback -> Bitmark.issue(issuanceParams, callback));

        return bitmarkIds;
    }
}
