package com.bitmark.cryptography.crypto.key;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface Key {

    byte[] toBytes();

    int size();
}
