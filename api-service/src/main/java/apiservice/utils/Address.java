package apiservice.utils;

import apiservice.configuration.GlobalConfiguration;
import apiservice.configuration.Network;
import apiservice.utils.error.InvalidAddressException;
import apiservice.utils.error.InvalidNetworkException;
import cryptography.crypto.Ed25519;
import cryptography.crypto.Sha3256;
import cryptography.crypto.encoder.VarInt;
import cryptography.crypto.key.PublicKey;
import cryptography.error.ValidateException;

import java.util.Arrays;

import static apiservice.configuration.KeyPart.PUBLIC_KEY;
import static apiservice.utils.ArrayUtil.*;
import static cryptography.crypto.encoder.Base58.BASE_58;
import static cryptography.utils.Validator.checkNonNull;

/**
 * @author Hieu Pham
 * @since 9/4/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Address implements Validation {

    public static final int CHECKSUM_LENGTH = 4;

    private PublicKey key;

    private Network network;

    public static Address fromAccountNumber(String accountNumber) {
        final byte[] addressBytes = BASE_58.decode(accountNumber);
        int keyVariant = VarInt.readUnsignedVarInt(addressBytes);
        final int keyVariantLength = toByteArray(keyVariant).length;

        // Verify address length
        int addressLength = keyVariantLength + Ed25519.PUBLIC_KEY_LENGTH + CHECKSUM_LENGTH;
        if (addressLength != addressBytes.length) throw new InvalidAddressException("Address " +
                "length is invalid. The expected is " + addressLength + " but actual is " + addressBytes.length);

        // Verify checksum
        final byte[] checksumData = slice(addressBytes, 0,
                keyVariantLength + Ed25519.PUBLIC_KEY_LENGTH);
        final byte[] checksum = slice(Sha3256.hash(checksumData), 0, CHECKSUM_LENGTH);
        final byte[] checksumFromAddress = slice(addressBytes,
                addressLength - CHECKSUM_LENGTH, addressLength);
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

        final byte[] publicKey = slice(addressBytes, keyVariantLength,
                addressLength - CHECKSUM_LENGTH);
        return new Address(PublicKey.from(publicKey), network);

    }

    public static Address getDefault(PublicKey key, Network network) throws ValidateException {
        checkNonNull(key);
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
        return concat(getPrefix(), key.toBytes());
    }

    @Override
    public boolean isValid() {
        return key != null && key.size() == Ed25519.PUBLIC_KEY_LENGTH && network != null;
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
        final byte[] preChecksum = concat(keyVariantVarInt, publicKeyBytes);
        final byte[] checksum = slice(Sha3256.hash(preChecksum), 0, CHECKSUM_LENGTH);
        final byte[] address = concat(keyVariantVarInt, publicKeyBytes, checksum);
        return BASE_58.encode(address);
    }

    public byte[] getPrefix() {
        int keyVariantValue = 0x01 << 4;
        keyVariantValue |= PUBLIC_KEY.value();
        keyVariantValue |= (network.value() << 1);
        return VarInt.writeUnsignedVarInt(keyVariantValue);
    }
}
