package sdk.test.integrationtest.features;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import sdk.test.integrationtest.BaseTest;
import sdk.features.Account;
import sdk.utils.Seed;
import cryptography.crypto.key.KeyPair;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public abstract class BaseFeatureTest extends BaseTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    static final Account ACCOUNT1;

    static final KeyPair KEY1;

    static final Account ACCOUNT2;

    static final KeyPair KEY2;

    static final Account ACCOUNT3;

    static final KeyPair KEY3;

    static {
        ACCOUNT1 = Account.fromSeed(Seed.fromEncodedSeed(
                "9J876mP7wDJ6g5P41eNMN8N3jo9fycDs2"));
        KEY1 = ACCOUNT1.getKey();
        ACCOUNT2 = Account.fromSeed(Seed.fromEncodedSeed(
                "9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH"));
        KEY2 = ACCOUNT2.getKey();
        ACCOUNT3 = Account.fromSeed(Seed.fromEncodedSeed(
                "9J878SbnM2GFqAELkkiZbqHJDkAj57fYK"));
        KEY3 = ACCOUNT3.getKey();
    }
}
