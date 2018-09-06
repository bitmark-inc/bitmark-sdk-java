package utils;

import config.Network;
import crypto.Ed25519;
import crypto.Sha3256;
import crypto.encoder.VarInt;
import crypto.key.PublicKey;

import static config.SdkConfig.CHECKSUM_LENGTH;
import static config.SdkConfig.KEY_TYPE;
import static config.SdkConfig.KeyPart.PUBLIC_KEY;
import static crypto.encoder.Base58.BASE_58;
import static utils.ArrayUtil.concat;
import static utils.ArrayUtil.slice;

/**
 * @author Hieu Pham
 * @since 9/4/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Address implements Validation {

    private PublicKey key;

    private Network network;

    public Address(PublicKey key, Network network) {
        this.key = key;
        this.network = network;
    }

    public byte[] pack() {
        return concat(getPrefix(), key.toBytes());
    }

    @Override
    public boolean isValid() {
        return key != null && key.size() == Ed25519.PUBLIC_KEY_LENGTH && network != null;
    }

    public String getAddress() {
        final byte[] keyVariantVarInt = getPrefix();
        final byte[] publicKeyBytes = key.toBytes();
        final byte[] preChecksum = concat(keyVariantVarInt, publicKeyBytes);
        final byte[] checksum = slice(Sha3256.hash(preChecksum), 0, CHECKSUM_LENGTH);
        final byte[] address = concat(keyVariantVarInt, publicKeyBytes, checksum);
        return BASE_58.encode(address);
    }

    private byte[] getPrefix() {
        int keyVariantValue = KEY_TYPE << 4;
        keyVariantValue |= PUBLIC_KEY.value();
        keyVariantValue |= (network.value() << 1);
        return VarInt.writeUnsignedVarInt(keyVariantValue);
    }
}
