package com.bitmark.sdk.test.integrationtest.features;

import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.internal.SeedTwelve;
import com.bitmark.sdk.test.integrationtest.BaseTest;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public abstract class BaseFeatureTest extends BaseTest {

    static final Account ACCOUNT1;

    static final KeyPair KEY1;

    static final Account ACCOUNT2;

    static final KeyPair KEY2;

    static final Account ACCOUNT3;

    static final KeyPair KEY3;

    static {
        ACCOUNT1 = Account.fromSeed(SeedTwelve.fromEncodedSeed(
                "9J876mP7wDJ6g5P41eNMN8N3jo9fycDs2"));
        KEY1 = ACCOUNT1.getKeyPair();
        ACCOUNT2 = Account.fromSeed(SeedTwelve.fromEncodedSeed(
                "9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH"));
        KEY2 = ACCOUNT2.getKeyPair();
        ACCOUNT3 = Account.fromSeed(SeedTwelve.fromEncodedSeed(
                "9J878SbnM2GFqAELkkiZbqHJDkAj57fYK"));
        KEY3 = ACCOUNT3.getKeyPair();
    }
}
