package crypto;

import java.util.Arrays;

import static crypto.libsodium.LibSodium.sodium;
import static utils.Validator.checkValidLength;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class SecretBox {

    private static final int MESSAGE_LENGTH = 16;

    private SecretBox() {
    }

    public static byte[] generateSecretBox(byte[] msg, byte[] nonce, byte[] key) {
        checkValidLength(msg, MESSAGE_LENGTH);

        final byte[] m = new byte[key.length + MESSAGE_LENGTH];
        final byte[] c = new byte[m.length];

        System.arraycopy(msg, 0, m, m.length - MESSAGE_LENGTH, msg.length);

        sodium().crypto_secretbox(c, m, m.length, nonce, key);
        return Arrays.copyOfRange(c, MESSAGE_LENGTH, c.length - 1);
    }

}
