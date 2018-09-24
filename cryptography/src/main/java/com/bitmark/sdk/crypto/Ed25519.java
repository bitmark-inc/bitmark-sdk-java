package com.bitmark.sdk.crypto;

import com.bitmark.sdk.crypto.key.KeyPair;
import com.bitmark.sdk.crypto.key.StandardKeyPair;
import com.bitmark.sdk.error.ValidateException;
import jnr.ffi.byref.LongLongByReference;

import static com.bitmark.sdk.crypto.encoder.Hex.HEX;
import static com.bitmark.sdk.crypto.libsodium.LibSodium.sodium;
import static com.bitmark.sdk.utils.Validator.checkValidHex;
import static com.bitmark.sdk.utils.Validator.checkValidLength;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Ed25519 {

    public static final int SEED_LENGTH = 32;

    public static final int PUBLIC_KEY_LENGTH = 32;

    public static final int PRIVATE_KEY_LENGTH = 64;

    public static final int SIG_LENGTH = 64;

    private Ed25519() {
    }

    public static KeyPair generateKeyPair() {
        final byte[] publicKey = new byte[PUBLIC_KEY_LENGTH];
        final byte[] privateKey = new byte[PRIVATE_KEY_LENGTH];
        sodium().crypto_sign_ed25519_keypair(publicKey, privateKey);
        return StandardKeyPair.from(publicKey, privateKey);
    }

    public static KeyPair generateKeyPairFromSeed(byte[] seed) throws ValidateException.InvalidLength {
        checkValidLength(seed, SEED_LENGTH);
        final byte[] publicKey = new byte[PUBLIC_KEY_LENGTH];
        final byte[] privateKey = new byte[PRIVATE_KEY_LENGTH];
        sodium().crypto_sign_ed25519_seed_keypair(publicKey, privateKey, seed);
        return StandardKeyPair.from(publicKey, privateKey);
    }

    public static KeyPair getKeyPair(byte[] privateKey) throws ValidateException.InvalidLength {
        checkValidLength(privateKey, PRIVATE_KEY_LENGTH);
        final byte[] publicKey = new byte[PUBLIC_KEY_LENGTH];
        sodium().crypto_sign_ed25519_sk_to_pk(publicKey, privateKey);
        return StandardKeyPair.from(publicKey, privateKey);
    }

    public static byte[] getSeed(byte[] privateKey) throws ValidateException.InvalidLength {
        checkValidLength(privateKey, PRIVATE_KEY_LENGTH);
        final byte[] seed = new byte[SEED_LENGTH];
        sodium().crypto_sign_ed25519_sk_to_seed(seed, privateKey);
        return seed;
    }

    public static String getSeed(String hexPrivateKey) throws ValidateException.InvalidHex {
        checkValidHex(hexPrivateKey);
        return HEX.encode(getSeed(HEX.decode(hexPrivateKey)));
    }

    public static byte[] sign(byte[] message, byte[] privateKey) throws ValidateException.InvalidLength {
        checkValidLength(privateKey, PRIVATE_KEY_LENGTH);
        final byte[] signature = new byte[SIG_LENGTH];
        sodium().crypto_sign_ed25519_detached(signature, new LongLongByReference(signature.length), message, message.length, privateKey);
        return signature;
    }

    public static String sign(String hexMessage, String hexPrivateKey) throws ValidateException.InvalidHex {
        checkValidHex(hexMessage);
        checkValidHex(hexPrivateKey);
        return HEX.encode(sign(HEX.decode(hexMessage), HEX.decode(hexPrivateKey)));
    }

    public static boolean verify(byte[] signature, byte[] message, byte[] publicKey) throws ValidateException.InvalidLength {
        checkValidLength(publicKey, PUBLIC_KEY_LENGTH);
        checkValidLength(signature, SIG_LENGTH);
        return sodium().crypto_sign_ed25519_verify_detached(signature, message, message.length, publicKey) == 0;
    }

    public static boolean verify(String hexSignature, String hexMessage, String hexPublicKey) throws ValidateException.InvalidHex {
        checkValidHex(hexSignature);
        checkValidHex(hexMessage);
        checkValidHex(hexPublicKey);
        return verify(HEX.decode(hexSignature), HEX.decode(hexMessage), HEX.decode(hexPublicKey));
    }


}
