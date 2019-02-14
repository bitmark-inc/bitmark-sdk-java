package com.bitmark.sdk.authentication;

import javax.crypto.Cipher;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public interface Authenticator {

    void authenticate(Cipher cipher);
}
