package com.bitmark.cryptography.crypto.encoder;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface Encoder {

    byte[] decode(String data);

    String encode(byte[] data);

}
