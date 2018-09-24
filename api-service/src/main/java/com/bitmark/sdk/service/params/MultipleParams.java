package com.bitmark.sdk.service.params;

import com.bitmark.sdk.crypto.key.KeyPair;

import java.util.List;

/**
 * @author Hieu Pham
 * @since 9/4/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface MultipleParams extends Params {

    List<byte[]> sign(KeyPair key);
}
