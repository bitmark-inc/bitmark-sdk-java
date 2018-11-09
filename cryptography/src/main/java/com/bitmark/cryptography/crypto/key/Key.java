package com.bitmark.cryptography.crypto.key;

import com.bitmark.cryptography.utils.Validation;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface Key extends Validation {

    byte[] toBytes();

    int size();
}
