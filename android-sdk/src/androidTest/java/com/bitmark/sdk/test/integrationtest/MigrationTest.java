/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.test.integrationtest;

import com.bitmark.apiservice.params.IssuanceParams;
import com.bitmark.apiservice.params.RegistrationParams;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.response.GetBitmarksResponse;
import com.bitmark.apiservice.response.RegistrationResponse;
import com.bitmark.apiservice.utils.Awaitility;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.apiservice.utils.record.BitmarkRecord;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.Asset;
import com.bitmark.sdk.features.Bitmark;
import com.bitmark.sdk.features.Migration;
import com.bitmark.sdk.test.BaseTest;
import com.bitmark.sdk.test.utils.extensions.TemporaryFolderRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.bitmark.apiservice.utils.Awaitility.await;
import static org.junit.Assert.*;

public class MigrationTest extends BaseTest {

    private static final long TIMEOUT = 50000;

    @Rule
    public TemporaryFolderRule temporaryFolderRule = new TemporaryFolderRule();

    @Test
    public void testRekey_ValidAccount_CorrectValuesReturn()
            throws Throwable {
        // Register asset
        File asset = temporaryFolderRule.newFile();
        Account owner = new Account();
        RegistrationParams registrationParams = new RegistrationParams(
                asset.getName(),
                null
        );
        registrationParams.setFingerprintFromFile(asset);
        registrationParams.sign(owner.getAuthKeyPair());
        RegistrationResponse registrationResponse =
                await(callback -> Asset.register(registrationParams, callback));
        List<AssetRecord> assets = registrationResponse.getAssets();
        String assetId = assets.get(0).getId();

        // Issue bitmarks
        IssuanceParams issuanceParams = new IssuanceParams(
                assetId,
                owner.toAddress(),
                10
        );
        issuanceParams.sign(owner.getAuthKeyPair());
        List<BitmarkRecord> bitmarks = await(callback -> Bitmark.issue(
                issuanceParams,
                callback
        ));
        List<String> txIds = new ArrayList<>();
        for (BitmarkRecord bitmark : bitmarks) {
            txIds.add(bitmark.getId());
        }

        assertEquals(txIds.size(), 10);
        assertFalse(txIds.get(0).isEmpty());

        // Loop for waiting bitmark to be confirmed
        while (true) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(15));
            BitmarkQueryBuilder builder = new BitmarkQueryBuilder().ownedBy(
                    owner.getAccountNumber()).pending(true);
            GetBitmarksResponse res = await(callback -> Bitmark.list(
                    builder,
                    callback
            ));
            if (res.getBitmarks()
                    .stream()
                    .noneMatch(bm -> bm.getStatus() == BitmarkRecord.Status.ISSUING)) {
                break;
            }
        }

        txIds = await(callback -> Migration.rekey(
                owner,
                new Account(),
                callback
        ), TIMEOUT);
        assertFalse(txIds.isEmpty());
        assertEquals(10, txIds.size());
    }

    @Test
    public void testRekey_EmptyBitmark_NothingBeRekey() throws Throwable {
        List<String> txIds = await(callback -> Migration.rekey(
                new Account(),
                new Account(),
                callback
        ), TIMEOUT);
        assertTrue(txIds.isEmpty());
    }

    @Test
    public void testRekey_InvalidAccount_ErrorThrow() throws Throwable {

        expectedException.expect(ValidateException.class);
        Awaitility.<List<String>>await(callback -> Migration.rekey(
                null,
                new Account(),
                callback
        ));

        Awaitility.<List<String>>await(callback -> Migration.rekey(
                new Account(),
                null,
                callback
        ));

        Account account = new Account();
        Awaitility.<List<String>>await(callback -> Migration.rekey(
                account,
                account,
                callback
        ));
    }

}
