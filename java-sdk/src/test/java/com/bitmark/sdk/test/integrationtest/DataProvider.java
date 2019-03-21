package com.bitmark.sdk.test.integrationtest;

import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.internal.SeedTwelve;

/**
 * @author Hieu Pham
 * @since 3/21/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class DataProvider {

    private DataProvider() {
    }

    public static final Account ACCOUNT1 = Account.fromSeed(SeedTwelve.fromEncodedSeed(
            "9J876mP7wDJ6g5P41eNMN8N3jo9fycDs2"));

    public static final KeyPair KEY1 = ACCOUNT1.getKeyPair();

    public static final Account ACCOUNT2 = Account.fromSeed(SeedTwelve.fromEncodedSeed(
            "9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH"));

    public static final KeyPair KEY2 = ACCOUNT2.getKeyPair();

    public static final Account ACCOUNT3 = Account.fromSeed(SeedTwelve.fromEncodedSeed(
            "9J878SbnM2GFqAELkkiZbqHJDkAj57fYK"));

    public static final KeyPair KEY3 = ACCOUNT3.getKeyPair();
}
