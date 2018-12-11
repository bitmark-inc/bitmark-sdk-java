package com.bitmark.sdk.keymanagement;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.bitmark.sdk.utils.FileUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import static com.bitmark.sdk.utils.FileUtils.read;
import static com.bitmark.sdk.utils.FileUtils.write;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
class AesGcmEncryption extends AbsEncryption {

    private static final String IV_FILE_NAME = "salt.key";

    private byte[] iv;

    AesGcmEncryption(Context context) {
        super(context);
        try {
            this.iv = getExistedIv(context);
        } catch (IOException e) {
            throw new RuntimeException("Cannot get existed iv");
        }
    }

    @Override
    String getTransformation() {
        return "AES/GCM/NoPadding";
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public byte[] encrypt(byte[] input, Key key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
                   BadPaddingException, IllegalBlockSizeException, IOException {
        return encrypt(input, getEncryptCipher(key));
    }

    @Override
    public byte[] encrypt(byte[] input, Cipher cipher)
            throws BadPaddingException, IllegalBlockSizeException, IOException {
        iv = cipher.getIV();
        byte[] encryptedBytes = cipher.doFinal(input);
        saveIv(context, iv);
        return encryptedBytes;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public byte[] decrypt(byte[] input, Key key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
                   InvalidAlgorithmParameterException, BadPaddingException,
                   IllegalBlockSizeException {
        return decrypt(input, getDecryptCipher(key));
    }

    @Override
    public byte[] decrypt(byte[] input, Cipher cipher)
            throws BadPaddingException, IllegalBlockSizeException {
        return cipher.doFinal(input);
    }

    @Override
    public Cipher getDecryptCipher(Key key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
                   InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = getCipher();
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return cipher;
    }

    @Override
    public Cipher getEncryptCipher(Key key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Cipher cipher = getCipher();
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher;
    }

    static boolean deleteSaltKeyFile(Context context) {
        File file = getSaltKeyFile(context);
        if (!file.exists()) return false;
        return file.delete();
    }

    private static void saveIv(Context context, byte[] iv) throws IOException {
        File saltFile = getSaltKeyFile(context);
        if (!saltFile.exists()) saltFile.createNewFile();
        write(saltFile, iv);
    }

    private static byte[] getExistedIv(Context context) throws IOException {
        File file = getSaltKeyFile(context);
        if (!FileUtils.isValid(file)) return null;
        return read(getSaltKeyFile(context));
    }

    private static File getSaltKeyFile(Context context) {
        return new File(context.getFilesDir(), IV_FILE_NAME);
    }
}
