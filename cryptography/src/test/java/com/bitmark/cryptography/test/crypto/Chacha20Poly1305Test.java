package com.bitmark.cryptography.test.crypto;

import com.bitmark.cryptography.crypto.Chacha20Poly1305;
import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.bitmark.cryptography.crypto.Chacha20Poly1305.IETF_KEY_BYTE_LENGTH;
import static com.bitmark.cryptography.crypto.Random.secureRandomBytes;
import static com.bitmark.cryptography.crypto.encoder.Raw.RAW;
import static com.bitmark.cryptography.test.utils.TestUtils.assertNotZeroBytes;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hieu Pham
 * @since 12/12/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public class Chacha20Poly1305Test extends BaseCryptoTest {

    private static final byte[] MESSAGE = RAW.decode("Bitmark SDK Test");

    private static final byte[] ADDITIONAL_DATA = RAW.decode("Additional Data");

    @Test
    public void testGenIetfKey_NoCondition_ValidKeyReturn() {
        byte[] key = Chacha20Poly1305.generateIetfKey();
        assertNotNull(key);
        assertEquals(IETF_KEY_BYTE_LENGTH, key.length);
        assertNotZeroBytes(key);
    }

    @Test
    public void testEncryptDecryptIetf_WithAdditionalData_ValidResultReturn() {
        // Random key and nonce
        byte[] nonce = secureRandomBytes(Chacha20Poly1305.IETF_NPUBBYTE_LENGTH);
        byte[] key = secureRandomBytes(Chacha20Poly1305.IETF_KEY_BYTE_LENGTH);

        byte[] cipherBytes = Chacha20Poly1305.aeadIetfEncrypt(MESSAGE, ADDITIONAL_DATA, nonce, key);
        assertNotZeroBytes(cipherBytes);

        byte[] message = Chacha20Poly1305.aeadIetfDecrypt(cipherBytes, ADDITIONAL_DATA, nonce, key);
        assertTrue(Arrays.equals(MESSAGE, message));
    }

    @Test
    public void testEncryptDecryptIetf_WithoutAdditionalData_ValidResultReturn() {
        // Random key and nonce
        byte[] nonce = secureRandomBytes(Chacha20Poly1305.IETF_NPUBBYTE_LENGTH);
        byte[] key = secureRandomBytes(Chacha20Poly1305.IETF_KEY_BYTE_LENGTH);

        byte[] cipherBytes = Chacha20Poly1305.aeadIetfEncrypt(MESSAGE, null, nonce, key);
        assertNotZeroBytes(cipherBytes);

        byte[] message = Chacha20Poly1305.aeadIetfDecrypt(cipherBytes, null, nonce, key);
        assertTrue(Arrays.equals(MESSAGE, message));
    }

    @ParameterizedTest
    @MethodSource("createInvalidKey")
    public void testEncryptIetf_InvalidKey_ErrorIsThrown(byte[] key) {
        byte[] nonce = secureRandomBytes(Chacha20Poly1305.IETF_NPUBBYTE_LENGTH);
        assertThrows(ValidateException.class,
                     () -> Chacha20Poly1305.aeadIetfEncrypt(MESSAGE, null, nonce, key));
    }

    @ParameterizedTest
    @MethodSource("createInvalidKey")
    public void testDecryptIetf_InvalidKey_ErrorIsThrown(byte[] key) {
        byte[] nonce = secureRandomBytes(Chacha20Poly1305.IETF_NPUBBYTE_LENGTH);
        assertThrows(ValidateException.class,
                     () -> Chacha20Poly1305.aeadIetfDecrypt(MESSAGE, null, nonce, key));
    }

    private static Stream<byte[]> createInvalidKey() {
        return Stream.of(null, new byte[12], new byte[]{1, 2, 3, 4, 5});
    }

}
