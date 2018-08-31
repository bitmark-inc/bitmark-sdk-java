package service.params;

import crypto.key.KeyPair;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface Params {

    void sign(KeyPair key);

    String toJson();
}
