package com.bitmark.apiservice.params;

import com.bitmark.cryptography.crypto.key.KeyPair;

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
