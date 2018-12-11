package com.bitmark.sdk.keymanagement;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public interface Encryption {

    byte[] encrypt(byte[] input, Key key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
                   BadPaddingException, IllegalBlockSizeException, IOException;

    byte[] encrypt(byte[] input, Cipher cipher)
            throws BadPaddingException, IllegalBlockSizeException, IOException;

    byte[] decrypt(byte[] input, Key key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
                   BadPaddingException, IllegalBlockSizeException,
                   InvalidAlgorithmParameterException;

    byte[] decrypt(byte[] input, Cipher cipher)
            throws BadPaddingException, IllegalBlockSizeException;

    Cipher getEncryptCipher(Key key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
                   InvalidAlgorithmParameterException, InvalidKeyException;

    Cipher getDecryptCipher(Key key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
                   InvalidAlgorithmParameterException, InvalidKeyException;
}
