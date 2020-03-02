/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.test.integrationtest.features;

import com.annimon.stream.Stream;
import com.bitmark.apiservice.params.RegistrationParams;
import com.bitmark.apiservice.params.query.AssetQueryBuilder;
import com.bitmark.apiservice.response.RegistrationResponse;
import com.bitmark.apiservice.utils.callback.Callable1;
import com.bitmark.apiservice.utils.error.HttpException;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.sdk.features.Asset;
import com.bitmark.sdk.test.integrationtest.BaseTest;
import com.bitmark.sdk.test.integrationtest.utils.extensions.TemporaryFolderRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bitmark.apiservice.utils.Awaitility.await;
import static com.bitmark.sdk.test.integrationtest.DataProvider.ACCOUNT1;
import static com.bitmark.sdk.test.integrationtest.DataProvider.KEY1;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class AssetTest extends BaseTest {

    @Rule
    public TemporaryFolderRule temporaryFolderRule = new TemporaryFolderRule();

    @Test
    public void testRegisterAsset_NewAsset_CorrectResponseIsReturn()
            throws Throwable {
        File asset = temporaryFolderRule.newFile();
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from android sdk test");
        }};
        RegistrationParams params = new RegistrationParams(
                asset.getName(),
                metadata
        );
        params.setFingerprintFromFile(asset);
        params.sign(KEY1);
        RegistrationResponse response = await(callback -> Asset.register(
                params,
                callback
        ));
        List<AssetRecord> assets = response.getAssets();
        assertNotNull(assets.get(0).getId());
    }


    @Test
    public void testRegisterAsset_ExistedAsset_CorrectResponseIsReturn()
            throws Throwable {
        expectedException.expect(HttpException.class);
        expectedException.expectMessage(containsString("1000"));

        File asset = temporaryFolderRule.newFile(
                "This is an existed file on Bitmark Block chain");
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from android sdk test");
        }};
        RegistrationParams params = new RegistrationParams(
                asset.getName(),
                metadata
        );
        params.setFingerprintFromFile(asset);
        params.sign(KEY1);
        await((Callable1<RegistrationResponse>) callback -> Asset.register(
                params,
                callback
        ));
    }


    @Test
    public void testQueryAssetById_ExistedAssetId_CorrectResponseIsReturn()
            throws Throwable {
        // Query existed assets
        AssetQueryBuilder builder =
                new AssetQueryBuilder().registeredBy(ACCOUNT1.getAccountNumber())
                        .limit(1);
        List<AssetRecord> assets = await(callback -> Asset.list(
                builder,
                callback
        ));
        assertFalse("This guy has not registered any assets", assets.isEmpty());

        // Get asset by id
        String id = assets.get(0).getId();
        AssetRecord asset = await(callback -> Asset.get(id, callback));
        assertNotNull(asset);
        assertEquals(id, asset.getId());
    }

    @Test
    public void testQueryAssetById_NotExistedAssetId_ErrorIsThrow()
            throws Throwable {
        expectedException.expect(HttpException.class);
        expectedException.expectMessage(containsString(String.valueOf(
                HTTP_NOT_FOUND)));
        String id =
                "12345678901234567890123456789012345678901234567890123456789012341234567890123456789012345678901234567890123456789012345678901234";
        await((Callable1<AssetRecord>) callback -> Asset.get(id, callback));
    }

    @Test
    public void testQueryAssets_NoCondition_CorrectResponseIsReturn()
            throws Throwable {
        int limit = 1;
        String registrant = ACCOUNT1.getAccountNumber();
        AssetQueryBuilder builder = new AssetQueryBuilder().limit(limit)
                .registeredBy(registrant);
        List<AssetRecord> assets = await(callback -> Asset.list(
                builder,
                callback
        ));
        assertEquals(limit, assets.size());
        Stream.of(assets)
                .forEach(asset -> assertEquals(
                        registrant,
                        asset.getRegistrant()
                ));
    }

}
