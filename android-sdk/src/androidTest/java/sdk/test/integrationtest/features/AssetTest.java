package sdk.test.integrationtest.features;

import apiservice.params.RegistrationParams;
import apiservice.params.query.AssetQueryBuilder;
import apiservice.response.RegistrationResponse;
import apiservice.utils.Address;
import apiservice.utils.callback.Callable1;
import apiservice.utils.error.HttpException;
import apiservice.utils.record.AssetRecord;
import org.junit.Rule;
import org.junit.Test;
import sdk.features.Asset;
import sdk.test.utils.extensions.TemporaryFolderRule;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static apiservice.utils.Awaitility.await;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class AssetTest extends BaseFeatureTest {

    @Rule
    public TemporaryFolderRule temporaryFolderRule = new TemporaryFolderRule();

    @Test
    public void testRegisterAsset_NewAsset_CorrectResponseIsReturn() throws Throwable {
        File asset = temporaryFolderRule.newFile();
        Address registrant = ACCOUNT1.toAddress();
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from android sdk test");
        }};
        RegistrationParams params = new RegistrationParams(asset.getName(), metadata, registrant);
        params.generateFingerprint(asset);
        params.sign(KEY1);
        RegistrationResponse response =
                await((Callable1<RegistrationResponse>) callback -> Asset.register(params,
                        callback));
        List<RegistrationResponse.Asset> assets = response.getAssets();
        assertNotNull(assets.get(0).getId());
        assertFalse(assets.get(0).isDuplicate());
    }


    @Test
    public void testRegisterAsset_ExistedAsset_CorrectResponseIsReturn() throws Throwable {
        expectedException.expect(HttpException.class);
        expectedException.expectMessage(containsString("2009"));

        File asset = temporaryFolderRule.newFile("This is an existed file on Bitmark Block chain");
        Address registrant = ACCOUNT1.toAddress();
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from android sdk test");
        }};
        RegistrationParams params = new RegistrationParams(asset.getName(), metadata, registrant);
        params.generateFingerprint(asset);
        params.sign(KEY1);
        await((Callable1<RegistrationResponse>) callback -> Asset.register(params,
                callback));
    }


    @Test
    public void testQueryAssetById_ExistedAssetId_CorrectResponseIsReturn() throws Throwable {
        // Query existed assets
        AssetQueryBuilder builder =
                new AssetQueryBuilder().registrant(ACCOUNT1.getAccountNumber()).limit(1);
        List<AssetRecord> assets =
                await((Callable1<List<AssetRecord>>) callback -> Asset.list(builder, callback));
        assertFalse("This guy has not registered any assets", assets.isEmpty());

        // Get asset by id
        String id = assets.get(0).getId();
        AssetRecord asset = await((Callable1<AssetRecord>) callback -> Asset.get(id, callback));
        assertNotNull(asset);
        assertEquals(id, asset.getId());
    }

    @Test
    public void testQueryAssetById_NotExistedAssetId_ErrorIsThrow() throws Throwable {
        expectedException.expect(HttpException.class);
        expectedException.expectMessage(containsString(String.valueOf(HTTP_NOT_FOUND)));
        String id =
                "12345678901234567890123456789012345678901234567890123456789012341234567890123456789012345678901234567890123456789012345678901234";
        await((Callable1<AssetRecord>) callback -> Asset.get(id, callback));
    }

    @Test
    public void testQueryAssets_NoCondition_CorrectResponseIsReturn() throws Throwable {
        int limit = 1;
        String registrant = ACCOUNT1.getAccountNumber();
        AssetQueryBuilder builder = new AssetQueryBuilder().limit(limit).registrant(registrant);
        List<AssetRecord> assets =
                await((Callable1<List<AssetRecord>>) callback -> Asset.list(builder, callback));
        assertEquals(limit, assets.size());
        assets.forEach(asset -> assertEquals(registrant, asset.getRegistrant()));
    }

}
