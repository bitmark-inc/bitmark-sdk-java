package com.bitmark.apiservice.utils;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import com.bitmark.apiservice.utils.error.InvalidAddressException;
import com.bitmark.apiservice.utils.error.InvalidNetworkException;
import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.crypto.Sha3256;
import com.bitmark.cryptography.crypto.encoder.VarInt;
import com.bitmark.cryptography.crypto.key.PublicKey;
import com.bitmark.cryptography.error.ValidateException;

import java.util.Arrays;

import static com.bitmark.apiservice.configuration.KeyPart.PUBLIC_KEY;
import static com.bitmark.cryptography.crypto.encoder.Base58.BASE_58;
import static com.bitmark.cryptography.utils.Validator.checkNonNull;
import static com.bitmark.cryptography.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 9/4/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Address {

    public static final int CHECKSUM_LENGTH = 4;

    private PublicKey key;

    private Network network;

    public static Address fromAccountNumber(String accountNumber) {
        final byte[] addressBytes = BASE_58.decode(accountNumber);
        int keyVariant = VarInt.readUnsignedVarInt(addressBytes);
        final int keyVariantLength = ArrayUtil.toByteArray(keyVariant).length;

        // Verify address length
        int addressLength = keyVariantLength + Ed25519.PUBLIC_KEY_LENGTH + CHECKSUM_LENGTH;
        if (addressLength != addressBytes.length) throw new InvalidAddressException("Address " +
                                                                                    "length is invalid. The expected is " +
                                                                                    addressLength +
                                                                                    " but actual is " +
                                                                                    addressBytes.length);

        // Verify checksum
        final byte[] checksumData = ArrayUtil.slice(addressBytes, 0,
                                                    keyVariantLength + Ed25519.PUBLIC_KEY_LENGTH);
        final byte[] checksum = ArrayUtil.slice(Sha3256.hash(checksumData), 0, CHECKSUM_LENGTH);
        final byte[] checksumFromAddress = ArrayUtil.slice(addressBytes,
                                                           addressLength - CHECKSUM_LENGTH,
                                                           addressLength);
        if (!ArrayUtil.equals(checksumFromAddress, checksum)) throw new InvalidAddressException(
                "Invalid checksum. The expected is " + Arrays.toString(checksum) + " but actual " +
                "is " + Arrays.toString(checksumFromAddress));

        // Check for whether it's an address
        if ((keyVariant & 0x01) != PUBLIC_KEY.value())
            throw new InvalidAddressException();

        // Verify network value
        int networkValue = (keyVariant >> 1) & 0x01;
        final Network network = Network.valueOf(networkValue);
        if (!Network.isValid(networkValue) || GlobalConfiguration.network() != network)
            throw new InvalidNetworkException(networkValue);

        final byte[] publicKey = ArrayUtil.slice(addressBytes, keyVariantLength,
                                                 addressLength - CHECKSUM_LENGTH);
        return new Address(PublicKey.from(publicKey), network);

    }

    public static boolean isValidAccountNumber(String accountNumber) {
        try {
            fromAccountNumber(accountNumber);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static Address getDefault(PublicKey key, Network network) throws ValidateException {
        checkValid(() -> null != key && Ed25519.PUBLIC_KEY_LENGTH == key.size(),
                   "invalid public key");
        checkNonNull(network);
        return new Address(key, network);
    }

    private Address() {
    }

    private Address(PublicKey key, Network network) {
        this();
        this.key = key;
        this.network = network;
    }

    public byte[] pack() {
        return ArrayUtil.concat(getPrefix(), key.toBytes());
    }

    public Network getNetwork() {
        return network;
    }

    public PublicKey getKey() {
        return key;
    }

    public String getAddress() {
        final byte[] keyVariantVarInt = getPrefix();
        final byte[] publicKeyBytes = key.toBytes();
        final byte[] preChecksum = ArrayUtil.concat(keyVariantVarInt, publicKeyBytes);
        final byte[] checksum = ArrayUtil.slice(Sha3256.hash(preChecksum), 0, CHECKSUM_LENGTH);
        final byte[] address = ArrayUtil.concat(keyVariantVarInt, publicKeyBytes, checksum);
        return BASE_58.encode(address);
    }

    public byte[] getPrefix() {
        int keyVariantValue = 0x01 << 4;
        keyVariantValue |= PUBLIC_KEY.value();
        keyVariantValue |= (network.value() << 1);
        return VarInt.writeUnsignedVarInt(keyVariantValue);
    }
}
