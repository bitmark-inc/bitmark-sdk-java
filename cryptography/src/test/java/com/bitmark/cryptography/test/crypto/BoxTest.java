/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.test.crypto;

import com.bitmark.cryptography.crypto.Box;
import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.bitmark.cryptography.crypto.Random.secureRandomBytes;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.crypto.encoder.Raw.RAW;
import static com.bitmark.cryptography.test.utils.TestUtils.assertNotZeroBytes;
import static com.bitmark.cryptography.test.utils.TestVectors.*;
import static org.junit.jupiter.api.Assertions.*;

public class BoxTest extends BaseCryptoTest {

    private static final String MESSAGE = "This is a message";

    @RepeatedTest(3)
    public void testBox_ValidParams_CorrectCipherReturn() {
        byte[] nonce = secureRandomBytes(Box.NONCE_BYTE_LENGTH);
        byte[] message = RAW.decode(MESSAGE);

        byte[] cipher = Box.box(
                message,
                nonce,
                HEX.decode(ALICE_PUBLIC_KEY),
                HEX.decode(BOB_PRIVATE_KEY)
        );
        assertNotNull(cipher);
        assertNotZeroBytes(cipher);

        byte[] actualMessage = Box.unbox(
                cipher,
                nonce,
                HEX.decode(BOB_PUBLIC_KEY),
                HEX.decode(ALICE_PRIVATE_KEY)
        );
        assertNotNull(actualMessage);
        assertNotZeroBytes(actualMessage);
        assertTrue(Arrays.equals(message, actualMessage));

    }

    @ParameterizedTest
    @MethodSource("createInvalidParamsForBox")
    public void testBox_InvalidParams_ErrorThrown(
            byte[] message, byte[] nonce, byte[] publicKey,
            byte[] privateKey
    ) {
        assertThrows(
                ValidateException.class,
                () -> Box.box(message, nonce, publicKey, privateKey)
        );
    }

    @ParameterizedTest
    @MethodSource("createInvalidParamsForBox")
    public void testUnbox_InvalidParams_ErrorThrown(
            byte[] cipher, byte[] nonce, byte[] publicKey,
            byte[] privateKey
    ) {
        assertThrows(
                ValidateException.class,
                () -> Box.box(cipher, nonce, publicKey, privateKey)
        );
    }

    @RepeatedTest(3)
    public void testGenKeyPairWithPrivateKey_ValidPrivateKey_CorrectKeyPairReturn() {
        byte[] privateKey = secureRandomBytes(Box.PRIVATE_KEY_BYTE_LENGTH);
        KeyPair keyPair = Box.generateKeyPair(privateKey);
        assertTrue(keyPair.isValid());
        assertEquals(keyPair.publicKey().size(), Box.PUB_KEY_BYTE_LENGTH);
    }

    @ParameterizedTest
    @MethodSource("createInvalidKey")
    public void testGenKeyPairWithPrivateKey_InvalidPrivateKey_CorrectKeyPairReturn(
            byte[] privateKey
    ) {
        assertThrows(
                ValidateException.class,
                () -> Box.generateKeyPair(privateKey)
        );
    }

    @RepeatedTest(3)
    public void testGenKeyPair_CorrectKeyPairReturn() {
        KeyPair keyPair = Box.generateKeyPair();
        assertTrue(keyPair.isValid());
        assertEquals(keyPair.publicKey().size(), Box.PUB_KEY_BYTE_LENGTH);
        assertEquals(keyPair.privateKey().size(), Box.PRIVATE_KEY_BYTE_LENGTH);
    }

    @RepeatedTest(3)
    public void testBoxCurve25519_ValidParams_CorrectCipherReturn() {
        byte[] nonce = secureRandomBytes(Box.NONCE_BYTE_LENGTH);
        byte[] message = RAW.decode(MESSAGE);

        byte[] cipher = Box.boxCurve25519XSalsa20Poly1305(
                message,
                nonce,
                HEX.decode(ALICE_PUBLIC_KEY),
                HEX.decode(BOB_PRIVATE_KEY)
        );
        assertNotNull(cipher);
        assertNotZeroBytes(cipher);

        byte[] actualMessage = Box.unboxCurve25519XSalsa20Poly1305(
                cipher,
                nonce,
                HEX.decode(BOB_PUBLIC_KEY),
                HEX.decode(ALICE_PRIVATE_KEY)
        );
        assertNotNull(actualMessage);
        assertNotZeroBytes(actualMessage);
        assertTrue(Arrays.equals(message, actualMessage));

    }

    @ParameterizedTest
    @MethodSource("createInvalidParamsForBox")
    public void testBoxCurve25519_InvalidParams_ErrorThrown(
            byte[] message, byte[] nonce,
            byte[] publicKey,
            byte[] privateKey
    ) {
        assertThrows(
                ValidateException.class,
                () -> Box.boxCurve25519XSalsa20Poly1305(
                        message,
                        nonce,
                        publicKey,
                        privateKey
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createInvalidParamsForBox")
    public void testUnboxCurve25519_InvalidParams_ErrorThrown(
            byte[] cipher, byte[] nonce,
            byte[] publicKey,
            byte[] privateKey
    ) {
        assertThrows(
                ValidateException.class,
                () -> Box.unboxCurve25519XSalsa20Poly1305(
                        cipher,
                        nonce,
                        publicKey,
                        privateKey
                )
        );
    }

    private static Stream<byte[]> createInvalidKey() {
        return Stream.of(null, new byte[]{}, new byte[]{0, 1, 2, 3});
    }

    private static Stream<Arguments> createInvalidParamsForBox() {
        return Stream.of(
                Arguments.of(null, null, null, null),
                Arguments.of(null, new byte[]{}, new byte[]{}, new byte[]{}),
                Arguments.of(
                        new byte[15],
                        new byte[1],
                        new byte[Box.PUB_KEY_BYTE_LENGTH],
                        new byte[Box.PRIVATE_KEY_BYTE_LENGTH]
                )
        );
    }
}
