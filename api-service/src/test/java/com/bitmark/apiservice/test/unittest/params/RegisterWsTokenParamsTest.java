/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.test.unittest.params;

import com.bitmark.apiservice.params.RegisterWsTokenParams;
import com.bitmark.apiservice.test.integrationtest.BaseTest;
import com.bitmark.cryptography.crypto.key.BoxKeyPair;
import com.bitmark.cryptography.crypto.key.Ed25519KeyPair;
import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static com.bitmark.apiservice.test.integrationtest.DataProvider.*;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterWsTokenParamsTest extends BaseTest {

    @Test
    public void testConstructor__ValidInstanceOrErrorThrow() {
        assertThrows(
                ValidateException.class,
                () -> new RegisterWsTokenParams(null)
        );

        assertDoesNotThrow(() -> new RegisterWsTokenParams(ADDRESS1));
        assertDoesNotThrow(() -> new RegisterWsTokenParams(ADDRESS2));
    }

    @Test
    public void testGetter__ValidValueReturn() {
        RegisterWsTokenParams params = new RegisterWsTokenParams(ADDRESS1);
        params.sign(KEY1);
        assertNotNull(params.getSignature());
        assertTrue(Objects.deepEquals(ADDRESS1, params.getRequester()));
    }

    @Test
    public void testSign() {
        RegisterWsTokenParams params = new RegisterWsTokenParams(ADDRESS1);
        assertThrows(ValidateException.class, () -> params.sign(null));
        assertThrows(
                ValidateException.class,
                () -> params.sign(BoxKeyPair.from(new byte[32], new byte[64]))
        );
        assertThrows(
                ValidateException.class,
                () -> params.sign(Ed25519KeyPair.from(
                        new byte[32],
                        new byte[64]
                ))
        );
        assertThrows(ValidateException.class, () -> params.sign(KEY2));

        assertDoesNotThrow(() -> params.sign(KEY1));

        assertNotNull(params.getSignature());
    }

    @Test
    public void testBuildHeader() {
        RegisterWsTokenParams params = new RegisterWsTokenParams(ADDRESS1);
        assertThrows(UnsupportedOperationException.class, params::buildHeader);

        params.sign(KEY1);
        assertDoesNotThrow(params::buildHeader);
    }
}
