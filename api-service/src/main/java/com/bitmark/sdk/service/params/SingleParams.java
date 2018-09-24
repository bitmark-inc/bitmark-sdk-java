package com.bitmark.sdk.service.params;

import com.bitmark.sdk.crypto.key.KeyPair;
import com.bitmark.sdk.error.ValidateException;

/**
 * @author Hieu Pham
 * @since 9/4/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface SingleParams extends Params {

    byte[] sign(KeyPair key) throws ValidateException;
}
