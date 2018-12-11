package com.bitmark.sdk.keymanagement;

import com.bitmark.apiservice.utils.callback.Callback0;
import com.bitmark.apiservice.utils.callback.Callback1;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public interface KeyManager {

    void getKey(String alias, Callback1<byte[]> callback);

    void saveKey(String alias, byte[] key, boolean isAuthenticationRequired, Callback0 callback);

    void removeKey(String alias, Callback0 callback);
}
