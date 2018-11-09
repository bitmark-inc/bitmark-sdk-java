package com.bitmark.sdk.features;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import com.bitmark.apiservice.utils.ArrayUtil;
import com.bitmark.apiservice.utils.error.InvalidNetworkException;
import com.bitmark.cryptography.crypto.Sha3256;
import com.bitmark.cryptography.crypto.encoder.VarInt;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.utils.SequenceIterateByteArray;
import com.bitmark.sdk.utils.Version;
import com.bitmark.sdk.utils.error.InvalidChecksumException;
import com.bitmark.sdk.utils.error.InvalidSeedException;

import java.util.Arrays;

import static com.bitmark.apiservice.utils.ArrayUtil.concat;
import static com.bitmark.apiservice.utils.ArrayUtil.slice;
import static com.bitmark.cryptography.crypto.encoder.Base58.BASE_58;
import static com.bitmark.cryptography.utils.Validator.checkValid;
import static com.bitmark.cryptography.utils.Validator.checkValidLength;
import static com.bitmark.sdk.utils.SdkUtils.extractNetwork;
import static com.bitmark.sdk.utils.Version.TWELVE;
import static com.bitmark.sdk.utils.Version.TWENTY_FOUR;

/**
 * @author Hieu Pham
 * @since 8/24/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Seed {

    private static final int CHECKSUM_LENGTH = 4;

    private static final byte[] TWELVE_HEADER = {0x5A, (byte) 0xFE, 0x02};

    private static final byte[] TWENTY_FOUR_HEADER = {0x5A, (byte) 0xFE};

    private static final int VERSION = 0x01;

    private static final int NETWORK_LENGTH = 1;

    private byte[] seed;

    private Network network;

    public static Seed fromEncodedSeed(String encodedSeed) throws ValidateException {
        final Version version = Version.fromEncodedSeed(encodedSeed);
        final byte[] seedBytes = BASE_58.decode(encodedSeed);
        final int encodedSeedLength = version.getEncodedSeedLength();
        checkValidLength(seedBytes, encodedSeedLength);

        return version == TWELVE ? fromTwelveVerEncodedSeed(seedBytes) :
                fromTwentyFourVerEncodedSeed(seedBytes);
    }

    private static Seed fromTwelveVerEncodedSeed(byte[] seedBytes) {
        final int seedLength = TWELVE.getCoreLength();

        // Checksum is last 4 bits
        final int length = seedBytes.length;
        final byte[] checksum = slice(seedBytes,
                length - CHECKSUM_LENGTH, length);

        // Bytes not contains checksum
        final byte[] data = Arrays.copyOfRange(seedBytes, 0,
                length - CHECKSUM_LENGTH);

        // Verify checksum
        final byte[] dataHashed = Sha3256.hash(data);
        final byte[] checksumVerification = slice(dataHashed, 0, CHECKSUM_LENGTH);
        checkValid(() -> Arrays.equals(checksum, checksumVerification),
                new InvalidChecksumException(checksumVerification, checksum));

        // Get seed
        final byte[] seed = slice(data, TWELVE_HEADER.length, data.length);
        checkValidLength(seed, seedLength);
        return new Seed(seed);
    }

    private static Seed fromTwentyFourVerEncodedSeed(byte[] seedBytes) {
        final int seedLength = TWENTY_FOUR.getCoreLength();

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
        final byte[] magicNumber = data.next(TWENTY_FOUR_HEADER.length);
        checkValid(() -> Arrays.equals(magicNumber, TWENTY_FOUR_HEADER),
                new InvalidSeedException.InvalidMagicNumberException(magicNumber,
                        TWENTY_FOUR_HEADER));

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
        checkValidLength(seed, seedLength);
        return new Seed(seed, Network.valueOf(network));
    }

    public Seed(byte[] seed) throws ValidateException {
        checkValid(() -> seed != null && Version.matchesCore(seed));
        this.seed = seed;
        this.network = Version.fromCore(seed) == TWELVE ? extractNetwork(seed) :
                GlobalConfiguration.network();
    }

    public Seed(byte[] seed, Network network) throws ValidateException {
        checkValid(() -> seed != null && Version.matchesCore(seed) && network != null);
        final Version version = Version.fromCore(seed);
        if (version == TWELVE && extractNetwork(seed) != network)
            throw new ValidateException("Invalid network");
        this.seed = seed;
        this.network = network;
    }

    public String getEncodedSeed() {
        return Version.fromCore(seed) == TWELVE ? getTwelveVerEncodedSeed() :
                getTwentyFourVerEncodedSeed();
    }

    private String getTwelveVerEncodedSeed() {
        final byte[] data = concat(TWELVE_HEADER, seed);
        final byte[] checksum = slice(Sha3256.hash(data), 0, CHECKSUM_LENGTH);
        final byte[] seed = concat(data, checksum);
        return BASE_58.encode(seed);
    }

    private String getTwentyFourVerEncodedSeed() {
        final byte[] network = VarInt.writeUnsignedVarInt(this.network.value());
        final byte[] encodedSeedVersion = VarInt.writeUnsignedVarInt(VERSION);
        final byte[] data = concat(TWENTY_FOUR_HEADER, encodedSeedVersion, network, seed);
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
}
