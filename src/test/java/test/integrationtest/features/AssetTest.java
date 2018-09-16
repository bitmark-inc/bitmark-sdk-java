package test.integrationtest.features;

import features.Asset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import service.params.RegistrationParams;
import service.response.RegistrationResponse;
import test.utils.Callable;
import test.utils.extensions.TemporaryFolderExtension;
import test.utils.extensions.annotations.TemporaryFile;
import utils.Address;
import utils.error.HttpException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static org.junit.jupiter.api.Assertions.*;
import static test.utils.CommonUtils.await;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

@ExtendWith({TemporaryFolderExtension.class})
public class AssetTest extends BaseFeatureTest {

    @DisplayName("Verify function Asset.register(RegistrationParams, Callback1<>) works well with" +
                         " a new asset")
    @Test
    public void testRegisterAsset_NewAsset_CorrectResponseIsReturn(File asset) {
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

    @DisplayName("Verify function Asset.register(RegistrationParams, Callback1<>) works well with" +
                         " an existed asset")
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
        HttpException exception = (HttpException) assertThrows(CompletionException.class,
                () -> await((Callable<RegistrationResponse>) callback -> Asset.register(params,
                        callback))).getCause();
        assertEquals(HTTP_FORBIDDEN, exception.getStatusCode());
        assertEquals(2009, exception.getErrorCode());
    }
}
