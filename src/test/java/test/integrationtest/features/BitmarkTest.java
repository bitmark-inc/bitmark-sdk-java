package test.integrationtest.features;

import features.Asset;
import features.Bitmark;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import service.params.IssuanceParams;
import service.params.RegistrationParams;
import service.response.IssueResponse;
import service.response.RegistrationResponse;
import test.utils.Callable;
import test.utils.extensions.TemporaryFolderExtension;
import utils.Address;
import utils.error.HttpException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.*;
import static test.utils.CommonUtils.await;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

@ExtendWith({TemporaryFolderExtension.class})
public class BitmarkTest extends BaseFeatureTest {

    @DisplayName("Verify function Bitmark.issue(IssuanceParams, Callback1<>) works well with " +
                         "owned multiple bitmark ")
    @Test
    public void testIssueBitmark_OwnedMultipleBitmark_CorrectSuccessResponseIsReturn(File asset) {
        // Register asset
        Address owner = ACCOUNT1.toAddress();
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from java sdk test");
        }};
        RegistrationParams registrationParams = new RegistrationParams(asset.getName(), metadata,
                owner);
        registrationParams.generateFingerprint(asset);
        registrationParams.sign(KEY1);
        RegistrationResponse registrationResponse =
                await(callback -> Asset.register(registrationParams,
                        callback));
        List<RegistrationResponse.Asset> assets = registrationResponse.getAssets();
        String assetId = assets.get(0).getId();

        // Issue bitmarks
        final int quantity = 5;
        IssuanceParams issuanceParams = new IssuanceParams(assetId, owner, quantity);
        issuanceParams.sign(KEY1);
        IssueResponse issueResponse = await(callback -> Bitmark.issue(issuanceParams, callback));
        assertEquals(issueResponse.getBitmarks().size(), quantity);
        assertFalse(issueResponse.getBitmarks().get(0).getId().isEmpty());
    }

    @DisplayName("Verify function Bitmark.issue(IssuanceParams, Callback1<>) works well with " +
                         "owned single bitmark")
    @Test
    public void testIssueBitmark_OwnedSingleBitmark_CorrectSuccessResponseIsReturn(File asset) {
        // Register asset
        Address owner = ACCOUNT1.toAddress();
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from java sdk test");
        }};
        RegistrationParams registrationParams = new RegistrationParams(asset.getName(), metadata,
                owner);
        registrationParams.generateFingerprint(asset);
        registrationParams.sign(KEY1);
        RegistrationResponse registrationResponse =
                await(callback -> Asset.register(registrationParams,
                        callback));
        List<RegistrationResponse.Asset> assets = registrationResponse.getAssets();
        String assetId = assets.get(0).getId();

        // Issue bitmarks
        IssuanceParams issuanceParams = new IssuanceParams(assetId, owner);
        issuanceParams.sign(KEY1);
        IssueResponse issueResponse = await(callback -> Bitmark.issue(issuanceParams, callback));
        assertEquals(issueResponse.getBitmarks().size(), 1);
        assertFalse(issueResponse.getBitmarks().get(0).getId().isEmpty());
    }

    @DisplayName("Verify function Bitmark.issue(IssuanceParams, Callback1<>) works well with " +
                         "not owned bitmarks")
    @Test
    public void testIssueBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn() {
        Address issuer = Address.fromAccountNumber(ACCOUNT1.getAccountNumber());
        IssuanceParams issuanceParams = new IssuanceParams(
                "bda050d2235402751ed09e73486c2cced34424c35d4d799eaa37ab73c3dba663", issuer);
        issuanceParams.sign(KEY1);
        HttpException exception = (HttpException) assertThrows(CompletionException.class, () ->
                await((Callable<IssueResponse>) callback -> Bitmark.issue(issuanceParams,
                        callback))).getCause();
        assertEquals(1000, exception.getErrorCode());
        assertEquals(HTTP_BAD_REQUEST, exception.getStatusCode());
    }

    @DisplayName("Verify function Bitmark.transfer(TransferParams, Callback1<>) works well with " +
                         "owned bitmarks")
    public void testTransferBitmark_OwnedBitmark_CorrectSuccessResponseIsReturn(File asset) {
    }

    @DisplayName("Verify function Bitmark.transfer(TransferParams, Callback1<>) works well with " +
                         "not owned bitmarks")
    public void testTransferBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn() {
    }

    @DisplayName("Verify function Bitmark.offer(TransferOfferParams, Callback1<>) works well with" +
                         " owned bitmarks")
    public void testOfferBitmark_OwnedBitmark_CorrectSuccessResponseIsReturn() {

    }

    @DisplayName("Verify function Bitmark.offer(TransferOfferParams, Callback1<>) works well with" +
                         " owned bitmarks")
    public void testOfferBitmark_NotOwnedBitmark_CorrectErrorResponseIsReturn() {

    }

    @DisplayName("Verify function Bitmark.respond(TransferResponseParams, Callback1<>) works well" +
                         " with accept response")
    public void testReplyOffer_AcceptOffer_CorrectSuccessResponseIsReturn() {

    }

    @DisplayName("Verify function Bitmark.respond(TransferResponseParams, Callback1<>) works well" +
                         " with cancel response")
    public void testReplyOffer_CancelOffer_CorrectSuccessResponseIsReturn() {

    }

    @DisplayName("Verify function Bitmark.respond(TransferResponseParams, Callback1<>) works well" +
                         " with reject response")
    public void testReplyOffer_RejectOffer_CorrectSuccessResponseIsReturn() {

    }


}
