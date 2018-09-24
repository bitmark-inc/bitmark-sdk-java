package com.bitmark.sdk.crypto.libsodium;

import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;
import jnr.ffi.byref.LongLongByReference;
import jnr.ffi.types.u_int64_t;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface Sodium {

    int sodium_init();

    String sodium_version_string();

    void randombytes(@Out byte[] buffer, @In @u_int64_t int size);

    int crypto_secretbox(@Out byte[] ct, @In byte[] msg, @In @u_int64_t int length,
                         @In byte[] nonce, @In byte[] key);

    int crypto_secretbox_xsalsa20poly1305(
            @Out byte[] ct, @In byte[] msg, @In @u_int64_t int length,
            @In byte[] nonce, @In byte[] key);

    int crypto_secretbox_xsalsa20poly1305_open(
            @Out byte[] message, @In byte[] ct, @In @u_int64_t int length,
            @In byte[] nonce, @In byte[] key);

    int crypto_auth_hmacsha512256(
            @Out byte[] mac, @In byte[] message, @In @u_int64_t int sizeof,
            @In byte[] key);

    int crypto_auth_hmacsha512256_verify(
            @In byte[] mac, @In byte[] message, @In @u_int64_t int sizeof,
            @In byte[] key);

    int crypto_aead_chacha20poly1305_encrypt(
            @Out byte[] ct, @Out LongLongByReference ctLength,
            @In byte[] message, @In @u_int64_t int messageLength,
            @In byte[] additionalData, @In @u_int64_t int adLength,
            @In byte[] nsec, @In byte[] npub, @In byte[] key);

    int crypto_aead_chacha20poly1305_decrypt(
            @Out byte[] message, @Out LongLongByReference messageLength,
            @In byte[] nsec, @In byte[] ct, @In @u_int64_t int ctLength,
            @In byte[] additionalData, @In @u_int64_t int adLength,
            @In byte[] npub, @In byte[] key);

    int crypto_scalarmult_base(
            @Out byte[] publicKey, @In byte[] secretKey);

    int crypto_box_curve25519xsalsa20poly1305_keypair(
            @Out byte[] publicKey, @Out byte[] secretKey);

    int crypto_box_curve25519xsalsa20poly1305_beforenm(
            @Out byte[] sharedkey, @In byte[] publicKey,
            @In byte[] privateKey);

    int crypto_box_curve25519xsalsa20poly1305(
            @Out byte[] ct, @In byte[] msg, @In @u_int64_t int length,
            @In byte[] nonce, @In byte[] publicKey, @In byte[] privateKey);

    int crypto_box_curve25519xsalsa20poly1305_afternm(
            @Out byte[] ct, @In byte[] msg, @In @u_int64_t int length,
            @In byte[] nonce, @In byte[] shared);

    int crypto_box_curve25519xsalsa20poly1305_open(
            @Out byte[] message, @In byte[] ct, @In @u_int64_t int length,
            @In byte[] nonce, @In byte[] publicKey, @In byte[] privateKey);

    int crypto_box_curve25519xsalsa20poly1305_open_afternm(
            @Out byte[] message, @In byte[] ct, @In @u_int64_t int length,
            @In byte[] nonce, @In byte[] shared);

    int crypto_sign_ed25519_keypair(@Out byte[] publicKey, @Out byte[] privateKey);

    int crypto_sign_ed25519_sk_to_seed(@Out byte[] seed, @In byte[] privateKey);

    int crypto_sign_ed25519_sk_to_pk(@Out byte[] publicKey, @In byte[] privateKey);

    int crypto_sign_ed25519_seed_keypair(
            @Out byte[] publicKey, @Out byte[] secretKey, @In byte[] seed);

    int crypto_sign_ed25519(
            @Out byte[] buffer, @Out LongLongByReference bufferLen,
            @In byte[] message, @In @u_int64_t int length,
            @In byte[] secretKey);

    int crypto_sign_ed25519_open(
            @Out byte[] buffer, @Out LongLongByReference bufferLen,
            @In byte[] sigAndMsg, @In @u_int64_t int length,
            @In byte[] key);

    int crypto_sign_ed25519_detached(@Out byte[] sig, @Out LongLongByReference sigLen,
                                     @In byte[] message, @In @u_int64_t int length,
                                     @In byte[] secretKey);

    int crypto_sign_ed25519_verify_detached(@In byte[] sig,
                                            @In byte[] message, @In @u_int64_t int length,
                                            @In byte[] key);

}
