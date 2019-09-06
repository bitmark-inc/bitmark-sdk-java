package com.bitmark.sdk.features.internal;

import com.bitmark.apiservice.configuration.Network;
import com.bitmark.apiservice.utils.ArrayUtil;
import com.bitmark.apiservice.utils.error.InvalidNetworkException;
import com.bitmark.cryptography.crypto.Box;
import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.crypto.Sha3256;
import com.bitmark.cryptography.crypto.encoder.VarInt;
import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.sdk.utils.SequenceIterateByteArray;
import com.bitmark.sdk.utils.error.InvalidChecksumException;
import com.bitmark.sdk.utils.error.InvalidSeedException;

import java.util.Arrays;

import static com.bitmark.apiservice.utils.ArrayUtil.concat;
import static com.bitmark.apiservice.utils.ArrayUtil.slice;
import static com.bitmark.cryptography.crypto.Random.randomBytes;
import static com.bitmark.cryptography.crypto.SecretBox.box;
import static com.bitmark.cryptography.crypto.encoder.Base58.BASE_58;
import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.utils.Validator.checkValid;
import static com.bitmark.cryptography.utils.Validator.checkValidLength;


/**
 * @author Hieu Pham
 * @since 1/4/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class SeedTwentyFour extends AbsSeed {

    public static final int SEED_BYTE_LENGTH = 32;

    public static int ENCODED_SEED_LENGTH = 40;

    private static final int NONCE_LENGTH = 24;

    private static final byte[] KEY_INDEX = HEX.decode(
            "000000000000000000000000000003E7");

    private static final byte[] ENC_KEY_INDEX = HEX.decode(
            "000000000000000000000000000003E8");

    private static final int VERSION = 0x01;

    private static final byte[] HEADER = {0x5A, (byte) 0xFE};

    private static final int NETWORK_LENGTH = 1;

    private Network network;

    public static Seed fromEncodedSeed(String encodedSeed) {

        final byte[] seedBytes = BASE_58.decode(encodedSeed);
        checkValidLength(seedBytes, ENCODED_SEED_LENGTH);

        // Checksum is last 4 bits
        final int length = seedBytes.length;
        final byte[] checksum = slice(
                seedBytes,
                length - CHECKSUM_LENGTH,
                length
        );

        // Bytes not contains checksum
        final SequenceIterateByteArray data =
                new SequenceIterateByteArray(Arrays.copyOfRange(seedBytes, 0,
                        length - CHECKSUM_LENGTH
                ));

        // Verify checksum
        final byte[] dataHashed = Sha3256.hash(data.getBytes());
        final byte[] checksumVerification = slice(
                dataHashed,
                0,
                CHECKSUM_LENGTH
        );
        checkValid(
                () -> Arrays.equals(checksum, checksumVerification),
                new InvalidChecksumException(checksumVerification, checksum)
        );

        // Verify magic number
        final byte[] magicNumber = data.next(HEADER.length);
        checkValid(
                () -> Arrays.equals(magicNumber, HEADER),
                new InvalidSeedException.InvalidMagicNumberException(
                        magicNumber,
                        HEADER
                )
        );

        // Verify version
        final byte[] encodedSeedVersion = VarInt.writeUnsignedVarInt(VERSION);
        final byte[] version = data.next(encodedSeedVersion.length);
        checkValid(
                () -> Arrays.equals(version, encodedSeedVersion),
                new InvalidSeedException.InvalidVersionException(
                        version,
                        encodedSeedVersion
                )
        );

        // Verify network
        final int network = ArrayUtil.toPrimitiveInteger(data.next(
                NETWORK_LENGTH));
        checkValid(
                () -> Network.isValid(network),
                new InvalidNetworkException(network)
        );

        // Get seed
        final byte[] seed = data.next();
        checkValidLength(seed, SEED_BYTE_LENGTH);
        return new SeedTwentyFour(seed, Network.valueOf(network));
    }

    public SeedTwentyFour(Network network) {
        this(randomBytes(SEED_BYTE_LENGTH), network);
    }

    public SeedTwentyFour(byte[] seedBytes, Network network) {
        super(seedBytes);
        this.network = network;
    }

    @Override
    public String getEncodedSeed() {
        final byte[] network = VarInt.writeUnsignedVarInt(this.network.value());
        final byte[] encodedSeedVersion = VarInt.writeUnsignedVarInt(VERSION);
        final byte[] data = concat(
                HEADER,
                encodedSeedVersion,
                network,
                seedBytes
        );
        final byte[] checksum = slice(Sha3256.hash(data), 0, CHECKSUM_LENGTH);
        final byte[] seed = concat(data, checksum);
        return BASE_58.encode(seed);
    }

    @Override
    public KeyPair getAuthKeyPair() {
        return Ed25519.generateKeyPairFromSeed(box(
                KEY_INDEX,
                new byte[NONCE_LENGTH],
                seedBytes
        ));
    }

    @Override
    public KeyPair getEncKeyPair() {
        byte[] privateKey = box(
                ENC_KEY_INDEX,
                new byte[NONCE_LENGTH],
                seedBytes
        );
        return Box.generateKeyPair(privateKey);
    }

    @Override
    public Version getVersion() {
        return Version.TWENTY_FOUR;
    }

    @Override
    public int length() {
        return SEED_BYTE_LENGTH;
    }

    @Override
    public Network getNetwork() {
        return network;
    }
}
