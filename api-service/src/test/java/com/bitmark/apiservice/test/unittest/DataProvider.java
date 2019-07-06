package com.bitmark.apiservice.test.unittest;

import com.bitmark.apiservice.utils.Address;
import com.bitmark.cryptography.crypto.key.Ed25519KeyPair;
import com.bitmark.cryptography.crypto.key.KeyPair;

import java.util.HashMap;
import java.util.Map;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;

/**
 * @author Hieu Pham
 * @since 3/11/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class DataProvider {

    private DataProvider() {
    }

    public static final String PRIVATE_KEY_1 =
            "0246a917d422e596168185cea9943459c09751532c52fe4ddc27b06e2893ef2258760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f";

    public static final String PUBLIC_KEY_1 =
            "58760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f";

    public static final KeyPair KEY_PAIR_1 = Ed25519KeyPair.from(HEX.decode(PUBLIC_KEY_1),
                                                                 HEX.decode(PRIVATE_KEY_1));

    public static final String PRIVATE_KEY_2 =
            "02732ac92fd70ea402393f28163c3b45f5da6f9d61dcf1254e0f2cec805fe737807f4d123c944e0c3ecc95d9bde89916ced6341a8c8cedeb8caafef8f35654e7";

    public static final String PUBLIC_KEY_2 =
            "807f4d123c944e0c3ecc95d9bde89916ced6341a8c8cedeb8caafef8f35654e7";

    public static final KeyPair KEY_PAIR_2 = Ed25519KeyPair.from(HEX.decode(PUBLIC_KEY_2),
                                                                 HEX.decode(PRIVATE_KEY_2));

    public static final String PRIVATE_KEY_3 =
            "b0e77f0a27390a00e82c07d6d228999019dce17aa3fbc1958629a7a47bc1cf6dd1c177ef358e9d1f0d4b09328cc1213e8d3580703aee51ccf97e482be977f7bc";

    public static final String PUBLIC_KEY_3 =
            "d1c177ef358e9d1f0d4b09328cc1213e8d3580703aee51ccf97e482be977f7bc";

    public static final KeyPair KEY_PAIR_3 = Ed25519KeyPair.from(HEX.decode(PUBLIC_KEY_3),
                                                                 HEX.decode(PRIVATE_KEY_3));

    public static final String PRIVATE_KEY_4 =
            "4534075cbcfc6ada1bb6b9e53d53f72341746031d9d17a3089a117766e7cda9e9bdf52f23deb941ea23cec982c24a5c811d321e71f6df56508bd511f66311e06";

    public static final String PUBLIC_KEY_4 =
            "9bdf52f23deb941ea23cec982c24a5c811d321e71f6df56508bd511f66311e06";

    public static final KeyPair KEY_PAIR_4 = Ed25519KeyPair.from(HEX.decode(PUBLIC_KEY_4),
                                                                 HEX.decode(PRIVATE_KEY_4));

    public static final Address ADDRESS1 = Address.fromAccountNumber(
            "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva");

    public static final Address ADDRESS2 = Address.fromAccountNumber(
            "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs");

    public static final String ASSET_ID =
            "f5ad8d9b58e122d2d229f86eaa5d276496a5a3da19d53c887a23f81955a3d07266b50a896d332abc1d1845850311e50570cb56ee507b89ec18bc91edc34c1059";

    public static final Map<String, String> METADATA = new HashMap<String, String>() {{
        put("name", "name");
        put("des", "des");
    }};

    public static final String ASSET_NAME = "name";

    public static final String TX_ID =
            "03a70acb35ef1eca10634137967ea28b8f2ca90c5aaf77dbb53ee54ab04fc8b6";
}
