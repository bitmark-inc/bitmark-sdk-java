package com.bitmark.sdk.keymanagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.security.keystore.*;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.callback.Callback0;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.error.UnexpectedException;
import com.bitmark.sdk.authentication.AuthenticationCallback;
import com.bitmark.sdk.authentication.Authenticator;
import com.bitmark.sdk.authentication.AuthenticatorFactory;
import com.bitmark.sdk.authentication.KeyAuthenticationSpec;
import com.bitmark.sdk.authentication.error.AuthenticationException;
import com.bitmark.sdk.authentication.error.AuthenticationRequiredException;
import com.bitmark.sdk.authentication.error.HardwareNotSupportedException;
import com.bitmark.sdk.utils.SharedPreferenceApi;
import io.reactivex.annotations.Nullable;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import static com.bitmark.cryptography.crypto.encoder.Base58.BASE_58;
import static com.bitmark.cryptography.crypto.encoder.Raw.RAW;
import static com.bitmark.sdk.authentication.Provider.*;
import static com.bitmark.sdk.authentication.error.AuthenticationException.Type.*;
import static com.bitmark.sdk.utils.DeviceUtils.isAboveP;
import static com.bitmark.sdk.utils.FileUtils.read;
import static com.bitmark.sdk.utils.FileUtils.write;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class KeyManagerImpl implements KeyManager {

    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    private static final String AES_KEY_TRANSFORMATION = KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7;

    private static final String SEPARATE_CHARACTER = "/";

    private Activity activity;

    public KeyManagerImpl(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public void getKey(
            String alias,
            KeyAuthenticationSpec keyAuthSpec,
            Callback1<byte[]> callback
    ) {

        KeyAuthenticationSpec newKeyAuthSpec;

        try {

            if (!isEncKeyExisting(keyAuthSpec.getKeyAlias())) {
                callback.onError(
                        new IllegalArgumentException(
                                "Encryption key alias is not existing"));
                return;
            }

            final SecretKey encryptionKey =
                    (SecretKey) getLoadedAndroidKeyStore().getKey(
                            keyAuthSpec.getKeyAlias(),
                            null
                    );

            newKeyAuthSpec = rebuildKeyAuthSpec(
                    activity,
                    encryptionKey,
                    keyAuthSpec
            );

            try {
                Cipher cipher = getDecryptCipher(alias, encryptionKey);

                if (newKeyAuthSpec.needAuthenticateImmediately()) {

                    // The key authentication is required and user didn't set the validity time frame for it
                    authForGetKey(
                            activity,
                            alias,
                            newKeyAuthSpec,
                            cipher,
                            callback
                    );
                } else {

                    // Don't require for authentication
                    callback.onSuccess(getKey(alias, cipher));
                }
            } catch (UserNotAuthenticatedException e) {

                // The user has not authenticated within the specified time frame
                triggerDeviceAuthentication(activity, newKeyAuthSpec,
                        getAuthCallback(new Callback1<Cipher>() {
                            @Override
                            public void onSuccess(Cipher cipher) {
                                try {
                                    callback.onSuccess(
                                            getKey(
                                                    alias,
                                                    getDecryptCipher(
                                                            alias,
                                                            encryptionKey
                                                    )
                                            ));
                                } catch (IOException | BadPaddingException | IllegalBlockSizeException
                                        | NoSuchPaddingException | NoSuchAlgorithmException
                                        | InvalidAlgorithmParameterException | InvalidKeyException e1) {
                                    e1.printStackTrace();
                                    callback.onError(e1);
                                }
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                callback.onError(throwable);
                            }
                        })
                );
            }

        } catch (KeyStoreException | InvalidAlgorithmParameterException | CertificateException
                | IOException | NoSuchAlgorithmException | NoSuchProviderException
                | BadPaddingException | InvalidKeySpecException | IllegalBlockSizeException
                | NoSuchPaddingException e) {
            // Unexpected error
            e.printStackTrace();
            callback.onError(new UnexpectedException(e));
        } catch (UnrecoverableEntryException | InvalidKeyException e) {
            // Cannot get the key because if it might be broken
            e.printStackTrace();
            callback.onError(new InvalidKeyException(e));
        } catch (AuthenticationRequiredException | HardwareNotSupportedException e) {
            // the hardware is not supported or user did not set up
            e.printStackTrace();
            callback.onError(e);
        }
    }

    private void authForGetKey(
            Activity activity,
            String alias,
            KeyAuthenticationSpec spec,
            Cipher cipher,
            Callback1<byte[]> callback
    ) {

        try {

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

            final Authenticator authenticator =
                    detectAuthenticator(
                            activity.getApplicationContext(),
                            spec.useAlternativeAuthentication()
                    );
            authenticator.authenticate(
                    activity,
                    spec.getAuthenticationTitle(),
                    spec.getAuthenticationDescription(),
                    cipher,
                    authCallback
            );
        } catch (AuthenticationRequiredException | HardwareNotSupportedException e) {
            e.printStackTrace();
            callback.onError(e);
        }

    }

    private byte[] getKey(String alias, Cipher cipher)
            throws IOException, BadPaddingException, IllegalBlockSizeException {
        byte[] encryptedKey = getEncryptedKeyInfo(alias).first();
        return cipher.doFinal(encryptedKey);
    }

    private Cipher getDecryptCipher(String alias, SecretKey key)
            throws
            IOException,
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException {

        byte[] iv = getEncryptedKeyInfo(alias).second();
        Cipher cipher = Cipher.getInstance(AES_KEY_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher;
    }

    private Pair<byte[], byte[]> getEncryptedKeyInfo(String alias)
            throws IOException {
        File file = getEncryptedKeyFile(alias);
        if (!file.exists()) {
            throw new IOException("File is not existing");
        }
        String encryptedKeyString = RAW.encode(read(file));
        byte[] encryptedKey = BASE_58.decode(encryptedKeyString.split(
                SEPARATE_CHARACTER)[0]);
        byte[] iv = BASE_58.decode(encryptedKeyString.split(SEPARATE_CHARACTER)[1]);
        return new Pair<>(encryptedKey, iv);
    }

    @Override
    public void saveKey(
            String alias,
            KeyAuthenticationSpec keyAuthSpec,
            byte[] key,
            Callback0 callback
    ) {

        KeyAuthenticationSpec newKeyAuthSpec;

        try {

            final boolean isEncKeyExisting = isEncKeyExisting(keyAuthSpec.getKeyAlias());

            final SecretKey encryptionKey =
                    isEncKeyExisting
                    ? (SecretKey) getLoadedAndroidKeyStore()
                            .getKey(keyAuthSpec.getKeyAlias(), null)
                    : generateEncryptionKey(
                            activity.getApplicationContext(),
                            keyAuthSpec.getKeyAlias(),
                            keyAuthSpec.isAuthenticationRequired(),
                            keyAuthSpec.getAuthenticationValidityDuration(),
                            keyAuthSpec.useAlternativeAuthentication(),
                            isAboveP()
                    );

            // Regenerate key spec
            newKeyAuthSpec =
                    rebuildKeyAuthSpec(activity, encryptionKey, keyAuthSpec);

            try {
                Cipher cipher = getEncryptCipher(encryptionKey);

                if (newKeyAuthSpec.needAuthenticateImmediately()) {
                    // The key authentication is required and user didn't set the validity time frame for it
                    authForSaveKey(
                            activity,
                            alias,
                            newKeyAuthSpec,
                            key,
                            cipher,
                            callback
                    );
                } else {
                    // Do not require for authentication
                    saveKey(alias, key, cipher);
                    callback.onSuccess();
                }

            } catch (UserNotAuthenticatedException e) {

                // The user has not authenticated within the specified time frame
                triggerDeviceAuthentication(activity, newKeyAuthSpec,
                        getAuthCallback(new Callback1<Cipher>() {
                            @Override
                            public void onSuccess(Cipher cipher) {
                                try {
                                    saveKey(alias, key,
                                            getEncryptCipher(encryptionKey)
                                    );
                                    callback.onSuccess();
                                } catch (BadPaddingException | IllegalBlockSizeException | IOException
                                        | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e1) {
                                    e1.printStackTrace();
                                    callback.onError(e1);
                                }
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                callback.onError(throwable);
                            }
                        })
                );
            }

        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException
                | NoSuchProviderException | NoSuchPaddingException
                | BadPaddingException | InvalidKeySpecException | IllegalBlockSizeException e) {
            e.printStackTrace();
            callback.onError(new UnexpectedException(e));
        } catch (InvalidKeyException | UnrecoverableEntryException e) {
            // Cannot get the key because if it might be broken
            e.printStackTrace();
            callback.onError(new InvalidKeyException(e));
        } catch (AuthenticationRequiredException | HardwareNotSupportedException e) {
            // the hardware is not supported or user did not set up
            e.printStackTrace();
            callback.onError(e);
        }
    }

    private AuthenticationCallback getAuthCallback(Callback1<Cipher> callback) {
        return new AuthenticationCallback() {
            @Override
            public void onSucceeded(@Nullable Cipher cipher) {
                callback.onSuccess(cipher);
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
    }

    private void authForSaveKey(
            Activity activity,
            String alias,
            KeyAuthenticationSpec spec,
            byte[] key,
            Cipher cipher,
            Callback0 callback
    ) {

        try {
            final AuthenticationCallback authCallback = getAuthCallback(
                    new Callback1<Cipher>() {
                        @Override
                        public void onSuccess(Cipher cipher) {
                            try {
                                saveKey(alias, key, cipher);
                                callback.onSuccess();
                            } catch (BadPaddingException | IllegalBlockSizeException | IOException e) {
                                e.printStackTrace();
                                callback.onError(e);
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            callback.onError(throwable);
                        }
                    });
            final Authenticator authenticator =
                    detectAuthenticator(
                            activity.getApplicationContext(),
                            spec.useAlternativeAuthentication()
                    );
            authenticator.authenticate(
                    activity,
                    spec.getAuthenticationTitle(),
                    spec.getAuthenticationDescription(),
                    cipher,
                    authCallback
            );
        } catch (AuthenticationRequiredException | HardwareNotSupportedException e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    private void triggerDeviceAuthentication(
            Activity activity,
            KeyAuthenticationSpec spec,
            AuthenticationCallback authCallback
    )
            throws
            AuthenticationRequiredException,
            HardwareNotSupportedException {

        getDeviceAuthenticator(activity.getApplicationContext()).authenticate(
                activity,
                spec.getAuthenticationTitle(),
                spec.getAuthenticationDescription(),
                null,
                authCallback
        );
    }

    private void saveKey(String alias, byte[] key, Cipher cipher)
            throws BadPaddingException, IllegalBlockSizeException, IOException {

        byte[] encryptedKey = cipher.doFinal(key);
        byte[] iv = cipher.getIV();
        String result = BASE_58.encode(encryptedKey) + SEPARATE_CHARACTER + BASE_58
                .encode(iv);

        // Write encrypted key to local storage
        write(createEncryptedKeyFile(alias), RAW.decode(result));
    }

    private Cipher getEncryptCipher(SecretKey key)
            throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException {
        Cipher cipher = Cipher.getInstance(AES_KEY_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher;
    }

    private Authenticator detectAuthenticator(
            Context context,
            boolean useAlternativePossibleAuthenticator
    )
            throws
            AuthenticationRequiredException,
            HardwareNotSupportedException {
        try {
            final AuthenticatorFactory factory =
                    isAboveP()
                    ? AuthenticatorFactory.from(BIOMETRIC)
                    : AuthenticatorFactory
                            .from(FINGERPRINT);
            final Authenticator authenticator = factory.getAuthenticator(context);
            authenticator.checkAvailability();
            return authenticator;
        } catch (AuthenticationRequiredException | HardwareNotSupportedException e) {
            if (useAlternativePossibleAuthenticator) {
                return getDeviceAuthenticator(context);
            }
            throw e;
        }
    }

    private Authenticator getDeviceAuthenticator(Context context)
            throws
            AuthenticationRequiredException,
            HardwareNotSupportedException {
        final Authenticator authenticator =
                AuthenticatorFactory.from(DEVICE).getAuthenticator(context);
        authenticator.checkAvailability();
        return authenticator;
    }

    @Override
    public void removeKey(
            String alias,
            KeyAuthenticationSpec keyAuthSpec,
            Callback0 callback
    ) {

        if (!isEncKeyExisting(keyAuthSpec.getKeyAlias())) {
            callback.onError(
                    new IllegalArgumentException(
                            "Encryption key alias is not existing"));
            return;
        }

        KeyAuthenticationSpec newKeyAuthSpec = null;

        try {

            final SecretKey encryptionKey =
                    (SecretKey) getLoadedAndroidKeyStore()
                            .getKey(keyAuthSpec.getKeyAlias(), null);

            // Regenerate key spec
            newKeyAuthSpec = rebuildKeyAuthSpec(
                    activity,
                    encryptionKey,
                    keyAuthSpec
            );

            if (newKeyAuthSpec.needAuthenticateImmediately()) {
                // The key in keystore is required to be authenticated each time using it
                authForRemoveKey(activity, alias, newKeyAuthSpec, callback);
            } else {
                // Verify the key is valid on time frame
                getDecryptCipher(alias, encryptionKey);

                // The key is in time frame
                removeKey(alias, keyAuthSpec);
                callback.onSuccess();
            }

        } catch (UserNotAuthenticatedException e) {
            // Key is now out of time frame, need to authenticate again
            authForRemoveKey(activity, alias, newKeyAuthSpec, callback);

        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException
                | IOException | UnrecoverableKeyException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchProviderException
                | InvalidKeySpecException e) {
            // Unexpected error
            callback.onError(e);
        }

    }

    private void authForRemoveKey(
            Activity activity,
            String alias,
            KeyAuthenticationSpec keyAuthSpec,
            Callback0 callback
    ) {
        try {
            triggerDeviceAuthentication(activity, keyAuthSpec,
                    getAuthCallback(new Callback1<Cipher>() {
                        @Override
                        public void onSuccess(Cipher cipher) {
                            try {
                                removeKey(alias, keyAuthSpec);
                                callback.onSuccess();
                            } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
                                e.printStackTrace();
                                callback.onError(e);
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            callback.onError(throwable);
                        }
                    })
            );

        } catch (AuthenticationRequiredException | HardwareNotSupportedException e1) {
            e1.printStackTrace();
            callback.onError(e1);
        }
    }

    private void removeKey(String alias, KeyAuthenticationSpec keyAuthSpec)
            throws
            CertificateException,
            NoSuchAlgorithmException,
            KeyStoreException,
            IOException {
        if (TextUtils.isEmpty(alias) || TextUtils.isEmpty(keyAuthSpec.getKeyAlias())) {
            throw new IllegalArgumentException(
                    "Invalid key store alias or the key alias");
        }
        KeyStore keyStore = getLoadedAndroidKeyStore();
        keyStore.deleteEntry(keyAuthSpec.getKeyAlias());
        getEncryptedKeyFile(alias).delete();
    }


    @SuppressLint("NewApi")
    private SecretKey generateEncryptionKey(
            Context context, String keyAlias,
            boolean isAuthenticationRequired,
            int keyAuthValidityDuration,
            boolean useAlternativeAuth,
            boolean supportStrongBoxBacked
    )
            throws
            NoSuchProviderException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            AuthenticationRequiredException {
        try {
            final KeyGenerator keyGenerator = KeyGenerator
                    .getInstance(
                            KeyProperties.KEY_ALGORITHM_AES,
                            ANDROID_KEYSTORE
                    );

            final KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
            )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            if (isAuthenticationRequired) {
                builder.setUserAuthenticationRequired(true);
                if (keyAuthValidityDuration != -1) {
                    builder.setUserAuthenticationValidityDurationSeconds(
                            keyAuthValidityDuration);
                }
            }
            if (supportStrongBoxBacked) {
                builder.setIsStrongBoxBacked(true); // Enable hardware secure module
            }

            keyGenerator.init(builder.build());
            return keyGenerator.generateKey();
        } catch (InvalidAlgorithmParameterException e) {
            if (e.getCause() instanceof IllegalStateException) {
                if (AuthenticatorFactory.from(FINGERPRINT)
                        .getAuthenticator(context)
                        .isHardwareDetected()) {
                    throw new AuthenticationRequiredException(FINGERPRINT);

                } else if (useAlternativeAuth) {
                    final SharedPreferenceApi sharePrefApi = new SharedPreferenceApi(
                            context);
                    // save flag for detect device authentication is required
                    sharePrefApi.put(keyAlias + "_auth_required", true);
                    // save flag for detect using alternative auth
                    sharePrefApi.put(keyAlias + "_alternative_auth", true);
                    // using device authentication in case of device does not support fingerprint sensor
                    return generateEncryptionKey(
                            context,
                            keyAlias,
                            false,
                            keyAuthValidityDuration,
                            true,
                            false
                    );
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        } catch (StrongBoxUnavailableException e) {
            // We do not know how to check the device supports strongbox key master or not
            // So we need to catch StrongBoxUnavailableException to re-generate the key without strongbox support
            return generateEncryptionKey(
                    context,
                    keyAlias,
                    isAuthenticationRequired,
                    keyAuthValidityDuration,
                    useAlternativeAuth,
                    false
            );
        }
    }

    private boolean isEncKeyExisting(String keyAlias) {
        try {
            return getLoadedAndroidKeyStore().containsAlias(keyAlias);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    private KeyStore getLoadedAndroidKeyStore()
            throws
            KeyStoreException,
            CertificateException,
            NoSuchAlgorithmException,
            IOException {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        return keyStore;
    }

    private File createEncryptedKeyFile(String fileName) throws IOException {
        File file = getEncryptedKeyFile(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private File getEncryptedKeyFile(String fileName) {
        return new File(
                activity.getApplicationContext().getFilesDir(),
                fileName + ".key"
        );
    }

    private KeyAuthenticationSpec rebuildKeyAuthSpec(
            Activity activity,
            SecretKey encryptionKey,
            KeyAuthenticationSpec oldSpec
    )
            throws
            NoSuchProviderException,
            NoSuchAlgorithmException,
            InvalidKeySpecException {
        final Context context = activity.getApplicationContext();
        final SharedPreferenceApi sharePrefApi = new SharedPreferenceApi(context);

        final boolean authRequiredFlag =
                sharePrefApi.get(
                        oldSpec.getKeyAlias() + "_auth_required",
                        Boolean.class
                );
        final boolean useAlternativeAuth =
                sharePrefApi.get(
                        oldSpec.getKeyAlias() + "_alternative_auth",
                        Boolean.class
                );


        // Retrieve the key info of encryption key
        SecretKeyFactory factory =
                SecretKeyFactory.getInstance(
                        encryptionKey.getAlgorithm(),
                        ANDROID_KEYSTORE
                );
        KeyInfo info = (KeyInfo) factory.getKeySpec(
                encryptionKey,
                KeyInfo.class
        );

        // Regenerate key spec
        return oldSpec.newBuilder(activity)
                .setAuthenticationRequired(
                        info.isUserAuthenticationRequired() || authRequiredFlag)
                .setAuthenticationValidityDuration(
                        info.getUserAuthenticationValidityDurationSeconds())
                .setUseAlternativeAuthentication(
                        oldSpec.useAlternativeAuthentication() || useAlternativeAuth)
                .build();
    }
}
