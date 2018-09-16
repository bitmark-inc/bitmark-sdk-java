package test.integrationtest.features;

import crypto.key.KeyPair;
import features.Account;
import test.integrationtest.BaseTest;
import utils.Seed;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public abstract class BaseFeatureTest extends BaseTest {

    static final int TIMEOUT = 20; // Second

    static final Account ACCOUNT1;

    static final KeyPair KEY1;

    static final Account ACCOUNT2;

    static final KeyPair KEY2;

    static final Account ACCOUNT3;

    static final KeyPair KEY3;

    static {
        ACCOUNT1 = Account.fromSeed(Seed.fromEncodedSeed(
                "5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QGF4U2yGb1DABXZsVeD"));
        KEY1 = ACCOUNT1.getKey();
        ACCOUNT2 = Account.fromSeed(Seed.fromEncodedSeed(
                "5XEECsXPYA9wDVXMtRMAVrtaWx7WSc5tG2hqj6b8iiz9rARjg2BgA9w"));
        KEY2 = ACCOUNT2.getKey();
        ACCOUNT3 = Account.fromSeed(Seed.fromEncodedSeed(
                "5XEECsavEHickBSYe6xMLcoGvxBQhY1ungWFD9Va4jLrFCL7TYk9Wfg"));
        KEY3 = ACCOUNT3.getKey();
    }
}
