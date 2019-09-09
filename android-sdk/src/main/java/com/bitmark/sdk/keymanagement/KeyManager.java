package com.bitmark.sdk.keymanagement;

import com.bitmark.apiservice.utils.callback.Callback0;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.sdk.authentication.KeyAuthenticationSpec;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public interface KeyManager {

    void getKey(
            String alias,
            KeyAuthenticationSpec keyAuthSpec,
            Callback1<byte[]> callback
    );

    void saveKey(
            String alias,
            KeyAuthenticationSpec keyAuthSpec,
            byte[] key,
            Callback0 callback
    );

    void removeKey(
            String alias,
            KeyAuthenticationSpec keyAuthSpec,
            Callback0 callback
    );
}
