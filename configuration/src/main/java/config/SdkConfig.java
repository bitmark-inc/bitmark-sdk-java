package config;

import crypto.Ed25519;
import crypto.Sha3256;

import static crypto.encoder.Hex.HEX;

/**
 * @author Hieu Pham
 * @since 8/27/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class SdkConfig {

    public static final int KEY_TYPE = 0x01; // Ed25519

    public static final int CHECKSUM_LENGTH = 4;

    public static final class Seed {

        public static final int ENCODED_LENGTH = 40;

        public static final int LENGTH = Ed25519.SEED_LENGTH;

        public static final int VERSION = 0x01;

        public static final byte[] MAGIC_NUMBER = HEX.decode("5AFE");

        public static final int NETWORK_LENGTH = 1;
    }

    public static final class Issue {

        public static final int TAG = 0x03;

        public static final int ASSET_ID_LENGTH = 64;
    }

    public static final class Asset {

        public static final int TAG = 0x02;

        public static final int MAX_FINGER_PRINT = 1024;

        public static final String PREFIX_FINGERPRINT = "01";
    }

    public static final class Transfer {

        public static final int TRANSACTION_ID_LENGTH = Sha3256.HASH_LENGTH;

        public static final int LINK_LENGTH = Sha3256.HASH_LENGTH;

        public static final int TRANSFER_TAG = 0x04;

        public static final int OFFER_TAG = 0x05;
    }

    public static final class ApiServer {

        public static final String LIVE_NET_ENDPOINT = "https://api.bitmark.com";

        public static final String TEST_NET_ENDPOINT = "https://api.test.bitmark.com";

        public static final String VERSION = "v3";

    }

    public enum KeyPart {

        PRIVATE_KEY(0x00), PUBLIC_KEY(0x01);

        private int value;

        KeyPart(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }
}
