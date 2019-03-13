package com.bitmark.sdk.test.integrationtest.features;

import com.bitmark.apiservice.params.RegistrationParams;
import com.bitmark.apiservice.params.query.AssetQueryBuilder;
import com.bitmark.apiservice.response.RegistrationResponse;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.callback.Callable1;
import com.bitmark.apiservice.utils.error.HttpException;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.sdk.features.Asset;
import com.bitmark.sdk.test.utils.extensions.TemporaryFolderExtension;
import com.bitmark.sdk.test.utils.extensions.annotations.TemporaryFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bitmark.apiservice.utils.Awaitility.await;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

@ExtendWith({TemporaryFolderExtension.class})
public class AssetTest extends BaseFeatureTest {

    @Test
    public void testRegisterAsset_NewAsset_CorrectResponseIsReturn(File asset) throws Throwable {
        Address registrant = ACCOUNT1.toAddress();
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from java sdk test");
        }};
        RegistrationParams params = new RegistrationParams(asset.getName(), metadata, registrant);
        params.generateFingerprint(asset);
        params.sign(KEY1);
        RegistrationResponse response = await(callback -> Asset.register(params, callback));
        List<RegistrationResponse.Asset> assets = response.getAssets();
        assertNotNull(assets.get(0).getId());
        assertFalse(assets.get(0).isDuplicate());
    }

    @Test
    public void testRegisterAsset_ExistedAsset_CorrectResponseIsReturn(@TemporaryFile("This is an existed file on Bitmark Block chain") File asset) {
        Address registrant = ACCOUNT1.toAddress();
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from java sdk test");
        }};
        RegistrationParams params = new RegistrationParams(asset.getName(), metadata, registrant);
        params.generateFingerprint(asset);
        params.sign(KEY1);
        HttpException exception = assertThrows(HttpException.class,
                () -> await((Callable1<RegistrationResponse>) callback -> Asset.register(params,
                        callback)));
        assertEquals(HTTP_FORBIDDEN, exception.getStatusCode());
        assertEquals(2009, exception.getErrorCode());
    }


    @Test
    public void testQueryAssetById_ExistedAssetId_CorrectResponseIsReturn() throws Throwable {
        // Query existed assets
        AssetQueryBuilder builder =
                new AssetQueryBuilder().registrant(ACCOUNT1.getAccountNumber()).limit(1);
        List<AssetRecord> assets = await(callback -> Asset.list(builder, callback));
        assertFalse(assets.isEmpty(), "This guy has not registered any assets");

        // Get asset by id
        String id = assets.get(0).getId();
        AssetRecord asset = await(callback -> Asset.get(id, callback));
        assertNotNull(asset);
        assertEquals(id, asset.getId());
    }

    @Test
    public void testQueryAssetById_NotExistedAssetId_ErrorIsThrow() {
        String id =
                "12345678901234567890123456789012345678901234567890123456789012341234567890123456789012345678901234567890123456789012345678901234";
        HttpException exception = assertThrows(HttpException.class,
                () -> await((Callable1<AssetRecord>) callback -> Asset.get(id, callback)));
        assertEquals(HTTP_NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void testQueryAssets_NoCondition_CorrectResponseIsReturn() throws Throwable {
        int limit = 1;
        String registrant = ACCOUNT1.getAccountNumber();
        AssetQueryBuilder builder = new AssetQueryBuilder().limit(limit).registrant(registrant);
        List<AssetRecord> assets = await(callback -> Asset.list(builder, callback));
        assertEquals(limit, assets.size());
        assets.forEach(asset -> assertEquals(registrant, asset.getRegistrant()));
    }

}
