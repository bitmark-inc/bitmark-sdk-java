package integrationtest.features;

import features.Asset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import service.params.RegistrationParams;
import service.response.RegistrationResponse;
import utils.Address;
import utils.extensions.TemporaryFile;
import utils.extensions.TemporaryFolderExtension;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static utils.TestCommonUtils.await;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

@ExtendWith(TemporaryFolderExtension.class)
public class AssetTest extends BaseFeatureTest {

    @Test
    public void testRegisterAsset_NewAsset_CorrectResponseIsReturn(@TemporaryFile("This is the test file") File asset) {
        Address registrant = ACCOUNT.toAddress();
        Map<String, String> metadata = new HashMap<String, String>() {{
            put("name", asset.getName());
            put("description", "Temporary File create from java sdk test");
        }};
        RegistrationParams params = new RegistrationParams(asset.getName(), metadata, registrant);
        params.generateFingerprint(asset);
        params.sign(KEY);
        RegistrationResponse response = await(callback -> Asset.register(params, callback));
        assertNotNull(response.getId());
        assertFalse(response.isDuplicate());
    }

    @Test
    public void testRegisterAsset_ExistedAsset_CorrectResponseIsReturn() {

    }
}
