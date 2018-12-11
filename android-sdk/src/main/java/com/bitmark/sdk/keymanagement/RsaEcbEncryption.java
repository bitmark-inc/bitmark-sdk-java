package com.bitmark.sdk.keymanagement;

import android.content.Context;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
class RsaEcbEncryption extends AbsEncryption {

    RsaEcbEncryption(Context context) {
        super(context);
    }

    @Override
    String getTransformation() {
        return "RSA/ECB/PKCS1Padding";
    }

    @Override
    public byte[] encrypt(byte[] input, Key key)
            throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException,
                   BadPaddingException, IllegalBlockSizeException {
        if (!(key instanceof PublicKey)) throw new InvalidKeyException("Only accept public key");
        return encrypt(input, getEncryptCipher(key));
    }

    @Override
    public byte[] encrypt(byte[] input, Cipher cipher)
            throws BadPaddingException, IllegalBlockSizeException {
        return cipher.doFinal(input);
    }

    @Override
    public byte[] decrypt(byte[] input, Key key)
            throws NoSuchAlgorithmException,
                   InvalidKeyException, NoSuchPaddingException, BadPaddingException,
                   IllegalBlockSizeException {
        if (!(key instanceof PrivateKey)) throw new InvalidKeyException("Only accept private key");
        return decrypt(input, getDecryptCipher(key));
    }

    @Override
    public byte[] decrypt(byte[] input, Cipher cipher)
            throws BadPaddingException, IllegalBlockSizeException {
        return cipher.doFinal(input);
    }

    @Override
    public Cipher getEncryptCipher(Key key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Cipher cipher = getCipher();
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher;
    }

    @Override
    public Cipher getDecryptCipher(Key key)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = getCipher();
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher;
    }
}
