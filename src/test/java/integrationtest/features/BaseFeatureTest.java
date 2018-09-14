package integrationtest.features;

import crypto.key.KeyPair;
import features.Account;
import integrationtest.BaseTest;
import utils.Seed;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public abstract class BaseFeatureTest extends BaseTest {

    static final int TIMEOUT = 20; // Second

    static final Account ACCOUNT;

    static final KeyPair KEY;

    static {
        ACCOUNT = Account.fromSeed(Seed.fromEncodedSeed(
                "5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QGF4U2yGb1DABXZsVeD"));
        KEY = ACCOUNT.getKey();
    }
}
