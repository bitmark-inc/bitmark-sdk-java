package features.impl;

import config.GlobalConfiguration;
import config.KeyPart;
import config.Network;
import crypto.Ed25519;
import crypto.SecretBox;
import crypto.Sha3256;
import crypto.encoder.VarInt;
import crypto.key.KeyPair;
import crypto.key.PublicKey;
import org.bouncycastle.util.Arrays;
import utils.RecoveryPhrase;
import utils.Seed;
import utils.callback.Callback2;

import static crypto.Random.random;
import static crypto.encoder.Base58.BASE_58;
import static crypto.encoder.Hex.HEX;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Account {

    private static final byte[] KEY_INDEX = HEX.decode("000000000000000000000000000003E7"); // 999

    private static final int ENTROPY_LENGTH = 32;

    private static final int NONCE_LENGTH = 24;

    private String accountNumber;

    private KeyPair key;

    public static Account fromSeed(String seed) {
        validate();
        throw new UnsupportedOperationException();
    }

    public static Account fromSeed(Seed seed) {
        validate();
        throw new UnsupportedOperationException();
    }

    public static Account fromRecoveryPhrase(RecoveryPhrase recoveryPhrase) {
        validate();
        throw new UnsupportedOperationException();
    }

    public static Account fromRecoveryPhrase(String... mnemonicWords) {
        validate();
        throw new UnsupportedOperationException();
    }

    public Account() {
        key = generateKey();
        accountNumber = generateAccountNumber(key.publicKey());
    }

    public RecoveryPhrase getRecoveryPhrase() {
        throw new UnsupportedOperationException();
    }

    public byte[] getSeed() {
        throw new UnsupportedOperationException();
    }

    public static boolean isValidAccountNumber(String accountNumber) {
        throw new UnsupportedOperationException();
    }

    public static void parseAccountNumber(String accountNumber,
                                          Callback2<Network, PublicKey> callback) {
        throw new UnsupportedOperationException();
    }

    private static void validate() {
    }

    private KeyPair generateKey() {
        final byte[] entropy = random(ENTROPY_LENGTH);
        final byte[] seed = SecretBox.generateSecretBox(KEY_INDEX, new byte[NONCE_LENGTH], entropy);
        return Ed25519.generateKeyPairFromSeed(seed);
    }

    private String generateAccountNumber(PublicKey key) {
        final Network network = GlobalConfiguration.network();
        final int keyType = Ed25519.TYPE;
        int keyVariantValue = keyType << 4;
        keyVariantValue |= KeyPart.PUBLIC_KEY.value();
        keyVariantValue |= (network.value() << 1);

        final byte[] keyVariantVarInt = VarInt.writeUnsignedVarInt(keyVariantValue);
        final byte[] publicKeyBytes = key.toBytes();
        final byte[] preChecksum = Arrays.concatenate(keyVariantVarInt, publicKeyBytes);
        final byte[] checksum = Arrays.copyOfRange(Sha3256.hash(preChecksum), 0, 4);
        final byte[] address = Arrays.concatenate(keyVariantVarInt, publicKeyBytes, checksum);
        return BASE_58.encode(address);
    }

}
