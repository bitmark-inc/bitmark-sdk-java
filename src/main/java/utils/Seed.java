package utils;

import config.GlobalConfiguration;
import config.Network;
import config.SdkConfig;
import crypto.Sha3256;
import crypto.encoder.VarInt;
import error.ValidateException;
import utils.error.InvalidChecksumException;
import utils.error.InvalidNetworkException;
import utils.error.InvalidSeedException;

import java.util.Arrays;

import static config.SdkConfig.CHECKSUM_LENGTH;
import static config.SdkConfig.Seed.*;
import static crypto.encoder.Base58.BASE_58;
import static utils.ArrayUtil.concat;
import static utils.ArrayUtil.slice;
import static utils.Validator.checkValid;
import static utils.Validator.checkValidLength;

/**
 * @author Hieu Pham
 * @since 8/24/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Seed {

    private byte[] seed;

    private Network network;

    private int version;

    public static Seed fromEncodedSeed(String encodedSeed) throws ValidateException {
        final byte[] seedBytes = BASE_58.decode(encodedSeed);
        checkValidLength(seedBytes, ENCODED_LENGTH);

        // Checksum is last 4 bits
        final int length = seedBytes.length;
        final byte[] checksum = slice(seedBytes,
                length - CHECKSUM_LENGTH, length);

        // Bytes not contains checksum
        final SequenceIterateByteArray data =
                new SequenceIterateByteArray(Arrays.copyOfRange(seedBytes, 0,
                        length - CHECKSUM_LENGTH));

        // Verify checksum
        final byte[] dataHashed = Sha3256.hash(data.getBytes());
        final byte[] checksumVerification = slice(dataHashed, 0, CHECKSUM_LENGTH);
        checkValid(() -> Arrays.equals(checksum, checksumVerification),
                new InvalidChecksumException(checksumVerification, checksum));

        // Verify magic number
        final byte[] magicNumber = data.next(MAGIC_NUMBER.length);
        checkValid(() -> Arrays.equals(magicNumber, MAGIC_NUMBER),
                new InvalidSeedException.InvalidMagicNumberException(magicNumber, MAGIC_NUMBER));

        // Verify version
        final byte[] encodedSeedVersion = VarInt.writeUnsignedVarInt(VERSION);
        final byte[] version = data.next(encodedSeedVersion.length);
        checkValid(() -> Arrays.equals(version, encodedSeedVersion),
                new InvalidSeedException.InvalidVersionException(version, encodedSeedVersion));

        // Verify network
        final int network = ArrayUtil.toPrimitiveInteger(data.next(NETWORK_LENGTH));
        checkValid(() -> Network.isValid(network), new InvalidNetworkException(network));

        // Get seed
        final byte[] seed = data.next();
        checkValidLength(seed, LENGTH);
        return new Seed(seed, Network.valueOf(network), ArrayUtil.toPrimitiveInteger(version));
    }

    public Seed(byte[] seed) {
        this(seed, GlobalConfiguration.network());
    }

    public Seed(byte[] seed, Network network) {
        this(seed, network, VERSION);
    }

    public Seed(byte[] seed, Network network, int version) {
        checkValid(() -> seed != null && seed.length == SdkConfig.Seed.LENGTH && network != null && version > 0);
        this.seed = seed;
        this.network = network;
        this.version = version;
    }

    public String getEncodedSeed() {
        final byte[] network = VarInt.writeUnsignedVarInt(this.network.value());
        final byte[] encodedSeedVersion = VarInt.writeUnsignedVarInt(version);
        final byte[] data = concat(MAGIC_NUMBER, encodedSeedVersion, network, seed);
        final byte[] checksum = slice(Sha3256.hash(data), 0, CHECKSUM_LENGTH);
        final byte[] seed = concat(data, checksum);
        return BASE_58.encode(seed);
    }

    public byte[] getSeed() {
        return seed;
    }

    public Network getNetwork() {
        return network;
    }

    public int getVersion() {
        return version;
    }
}
