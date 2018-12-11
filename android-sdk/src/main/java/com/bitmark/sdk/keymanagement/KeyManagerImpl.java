package com.bitmark.sdk.keymanagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import com.bitmark.apiservice.utils.callback.Callback0;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.sdk.BuildConfig;
import com.bitmark.sdk.authentication.Authentication;
import com.bitmark.sdk.authentication.AuthenticationCallback;
import com.bitmark.sdk.authentication.AuthenticationManager;
import com.bitmark.sdk.authentication.error.AuthenticationException;
import com.bitmark.sdk.authentication.error.BiometricException;
import com.bitmark.sdk.authentication.error.FingerprintException;
import com.bitmark.sdk.authentication.error.KeyGuardRequiredException;
import com.bitmark.sdk.utils.SharedPreferenceApi;

import javax.crypto.*;
import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Calendar;

import static com.bitmark.sdk.authentication.error.AuthenticationException.Type.*;
import static com.bitmark.sdk.utils.DeviceUtils.*;
import static com.bitmark.sdk.utils.FileUtils.read;
import static com.bitmark.sdk.utils.FileUtils.write;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public class KeyManagerImpl implements KeyManager {

    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    private static final String KEY_ALIAS = BuildConfig.APPLICATION_ID + ".wrapperkey";

    private static final String AUTH_FLAG = "auth_flag";

    private Activity activity;

    private final SharedPreferenceApi sharedPrefApi;

    public KeyManagerImpl(@NonNull Activity activity) {
        this.activity = activity;
        this.sharedPrefApi = new SharedPreferenceApi(activity);
    }

    @Override
    public void getKey(String alias, Callback1<byte[]> callback) {

        try {

            final Object wrapperKeyObj = getWrapperKey(true);
            final Key wrapperKey = getDecryptionWrapperKey(wrapperKeyObj);

            if (isWeakSecureDevices(activity.getApplicationContext()) &&
                isAuthenticationRequired()) {
                // Need manually to be authenticate before using the key
                authForGetKey(alias, wrapperKey, callback);
            } else {

                try {
                    callback.onSuccess(getKey(alias, wrapperKey));
                } catch (IllegalBlockSizeException e) {

                    // The key need to authenticate before
                    Throwable cause = e.getCause();
                    if (cause instanceof KeyStoreException || cause.getClass().getName()
                                                                   .equalsIgnoreCase(
                                                                           "android.security.KeyStoreException")) {
                        authForGetKey(alias, wrapperKey, callback);

                    } else callback.onError(e);

                }
            }

        } catch (KeyStoreException | InvalidAlgorithmParameterException | CertificateException
                | IOException | NoSuchAlgorithmException | NoSuchProviderException
                | KeyGuardRequiredException | BadPaddingException | NoSuchPaddingException e) {
            e.printStackTrace();
            callback.onError(e);
        } catch (UnrecoverableEntryException | InvalidKeyException e) {
            // Cannot get the key because if it might be broken
            e.printStackTrace();
            callback.onError(new InvalidKeyException(e));
        }
    }

    private void authForGetKey(String alias, Key wrapperKey, Callback1<byte[]> callback)
            throws InvalidKeyException {

        final AuthenticationCallback authCallback = new AuthenticationCallback() {
            @Override
            public void onSucceeded(@NonNull Cipher cipher) {
                try {
                    callback.onSuccess(getKey(alias, cipher));
                } catch (IllegalBlockSizeException
                        | IOException | BadPaddingException e) {
                    e.printStackTrace();
                    callback.onError(e);
                }
            }

            @Override
            public void onFailed() {
                callback.onError(new AuthenticationException(FAILED));
            }

            @Override
            public void onError(String error) {
                callback.onError(new AuthenticationException(ERROR, error));
            }

            @Override
            public void onCancelled() {
                callback.onError(new AuthenticationException(CANCELLED));
            }
        };

        try {
            final Encryption encryption = getEncryption();
            final Authentication authentication = getAuthentication(authCallback);
            authentication.authenticate(encryption.getDecryptCipher(wrapperKey));
        } catch (KeyGuardRequiredException | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | FingerprintException | BiometricException e) {
            e.printStackTrace();
            callback.onError(e);
        }

    }

    private byte[] getKey(String alias, Key wrapperKey)
            throws InvalidAlgorithmParameterException,
                   NoSuchAlgorithmException, IOException, IllegalBlockSizeException,
                   InvalidKeyException, BadPaddingException, NoSuchPaddingException {

        File file = getEncryptedKeyFile(alias);
        if (!file.exists()) return null;
        byte[] encryptedKey = read(file);
        Encryption encryption = getEncryption();
        return encryption.decrypt(encryptedKey, wrapperKey);

    }

    private byte[] getKey(String alias, Cipher cipher)
            throws IOException, BadPaddingException, IllegalBlockSizeException {
        File file = getEncryptedKeyFile(alias);
        if (!file.exists()) return null;
        byte[] encryptedKey = read(file);
        Encryption encryption = getEncryption();
        return encryption.decrypt(encryptedKey, cipher);
    }

    @Override
    public void saveKey(String alias, byte[] key, boolean isAuthenticationRequired,
                        Callback0 callback) {


        try {

            final Object wrapperKeyObj = getWrapperKey(isAuthenticationRequired);
            final Key wrapperKey = getEncryptionWrapperKey(wrapperKeyObj);

            if (isWeakSecureDevices(activity.getApplicationContext())) {

                // Save auth flag to determine when to force to authenticate for weak secure devices
                saveAuthFlag(isAuthenticationRequired);
                if (isAuthenticationRequired) authForSaveKey(alias, key, wrapperKey, callback);

            } else {

                try {
                    saveKey(alias, key, getEncryption().getEncryptCipher(wrapperKey));
                    callback.onSuccess();

                } catch (IllegalBlockSizeException e) {

                    // The key need to authenticate before, happens from Android M
                    final Throwable cause = e.getCause();
                    if (cause instanceof KeyStoreException || cause.getClass().getName()
                                                                   .equalsIgnoreCase(
                                                                           "android.security.KeyStoreException")) {

                        authForSaveKey(alias, key, wrapperKey, callback);

                    } else callback.onError(e);
                }
            }

        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException
                | NoSuchProviderException | NoSuchPaddingException | KeyGuardRequiredException
                | BadPaddingException e) {
            e.printStackTrace();
            callback.onError(e);
        } catch (InvalidKeyException | UnrecoverableEntryException e) {
            // Cannot get the key because if it might be broken
            e.printStackTrace();
            callback.onError(new InvalidKeyException(e));
        }
    }

    private void authForSaveKey(String alias, byte[] key, Key wrapperKey, Callback0 callback)
            throws InvalidKeyException {

        final AuthenticationCallback authCallback = new AuthenticationCallback() {
            @Override
            public void onSucceeded(@NonNull Cipher cipher) {
                try {
                    saveKey(alias, key, cipher);
                    callback.onSuccess();
                } catch (BadPaddingException | IllegalBlockSizeException | IOException e) {
                    e.printStackTrace();
                    callback.onError(e);
                }
            }

            @Override
            public void onFailed() {
                callback.onError(new AuthenticationException(FAILED));
            }

            @Override
            public void onError(String error) {
                callback.onError(new AuthenticationException(ERROR, error));
            }

            @Override
            public void onCancelled() {
                callback.onError(new AuthenticationException(CANCELLED));
            }
        };

        try {
            getAuthentication(authCallback)
                    .authenticate(getEncryption().getEncryptCipher(wrapperKey));
        } catch (KeyGuardRequiredException | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | FingerprintException | BiometricException e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    private void saveKey(String alias, byte[] key, Cipher cipher)
            throws BadPaddingException, IllegalBlockSizeException, IOException {

        byte[] encryptedKey;
        if (isAboveM()) {
            encryptedKey = getEncryption().encrypt(key, cipher);
        } else {
            encryptedKey = getEncryption().encrypt(key, cipher);
        }

        // Write encrypted key to local storage
        write(createEncryptedKeyFile(alias), encryptedKey);
    }

    private Key getEncryptionWrapperKey(Object key) {
        return isAboveM() ? (SecretKey) key : ((KeyPair) key).getPublic();
    }

    private Key getDecryptionWrapperKey(Object key) {
        return isAboveM() ? (SecretKey) key : ((KeyPair) key).getPrivate();
    }

    @Override
    public void removeKey(String alias, Callback0 callback) {
        if (!isAboveM()) clearAuthFlag();
        try {
            KeyStore keyStore = getLoadedAndroidKeyStore();
            keyStore.deleteEntry(alias);
            getEncryptedKeyFile(alias).delete();
            AesGcmEncryption.deleteSaltKeyFile(activity.getApplicationContext());
            callback.onSuccess();
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    private Authentication getAuthentication(AuthenticationCallback callback)
            throws KeyGuardRequiredException, FingerprintException, BiometricException {
        return new AuthenticationManager(activity, callback).getAuthentication();
    }

    private Encryption getEncryption() {
        final Context context = activity.getApplicationContext();
        return isAboveM() ? new AesGcmEncryption(context) : new RsaEcbEncryption(context);
    }

    private Object getWrapperKey(boolean isAuthenticationRequired)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException,
                   UnrecoverableEntryException, NoSuchProviderException,
                   InvalidAlgorithmParameterException, KeyGuardRequiredException {
        if (isWrapperKeyExisted()) {

            KeyStore keyStore = getLoadedAndroidKeyStore();

            if (isAboveM()) {
                KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore
                        .getEntry(KEY_ALIAS, null);
                return entry.getSecretKey();

            } else {
                KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) keyStore
                        .getEntry(KEY_ALIAS, null);
                return new KeyPair(entry.getCertificate().getPublicKey(), entry.getPrivateKey());
            }

        } else {
            return generateWrapperKey(isAuthenticationRequired);
        }
    }

    @SuppressLint("NewApi")
    private Object generateWrapperKey(boolean isAuthenticationRequired)
            throws NoSuchProviderException, NoSuchAlgorithmException,
                   InvalidAlgorithmParameterException, KeyGuardRequiredException {
        if (isAboveM()) {

            final KeyGenerator keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);

            final KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE);

            if (isAuthenticationRequired && !isWeakSecureDevices(activity.getApplicationContext()))
                builder.setUserAuthenticationRequired(true);
            if (isAboveP()) builder.setIsStrongBoxBacked(true); // Enable hardware secure module
            if (isAboveN()) builder.setInvalidatedByBiometricEnrollment(
                    false); // Avoid invalidate the key when user setup new device security

            keyGenerator.init(builder.build());
            return keyGenerator.generateKey();
        } else {
            try {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 50);
                KeyPairGeneratorSpec.Builder builder = new KeyPairGeneratorSpec
                        .Builder(activity.getApplicationContext())
                        .setAlias(KEY_ALIAS)
                        .setSubject(new X500Principal("CN=Bitmark Inc , O=Bitmark Inc C=Taiwan"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime());
                if (isAuthenticationRequired) builder.setEncryptionRequired();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", ANDROID_KEYSTORE);

                generator.initialize(builder.build());
                return generator.generateKeyPair();
            } catch (IllegalStateException e) {
                // Not set the device lock
                throw new KeyGuardRequiredException();
            }
        }
    }

    private boolean isWrapperKeyExisted() {
        try {
            return getLoadedAndroidKeyStore().containsAlias(KEY_ALIAS);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    private KeyStore getLoadedAndroidKeyStore()
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        return keyStore;
    }

    private File createEncryptedKeyFile(String fileName) throws IOException {
        File file = getEncryptedKeyFile(fileName);
        if (!file.exists()) file.createNewFile();
        return file;
    }

    private File getEncryptedKeyFile(String fileName) {
        return new File(activity.getApplicationContext().getFilesDir(), fileName + ".key");
    }

    private boolean isWeakSecureDevices(Context context) {
        return !isAboveM() || !AuthenticationManager.isHardwareDetected(context);
    }

    private void saveAuthFlag(boolean isAuthenticationRequired) {
        sharedPrefApi.put(AUTH_FLAG, isAuthenticationRequired);
    }

    private void clearAuthFlag() {
        sharedPrefApi.remove(AUTH_FLAG);
    }

    private boolean isAuthenticationRequired() {
        return sharedPrefApi.get(AUTH_FLAG, Boolean.class);
    }
}
