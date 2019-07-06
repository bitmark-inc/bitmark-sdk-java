package com.bitmark.apiservice.test.integrationtest;

import com.bitmark.apiservice.utils.Address;
import com.bitmark.cryptography.crypto.key.Ed25519KeyPair;
import com.bitmark.cryptography.crypto.key.KeyPair;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;

/**
 * @author Hieu Pham
 * @since 3/21/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class DataProvider {

    private DataProvider() {
    }

    public static final Address ADDRESS1 =
            Address.fromAccountNumber("fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs");

    public static final KeyPair KEY1 = Ed25519KeyPair.from(HEX.decode(
            "d1c177ef358e9d1f0d4b09328cc1213e8d3580703aee51ccf97e482be977f7bc"),
                                                           HEX.decode(
                                                                   "b0e77f0a27390a00e82c07d6d228999019dce17aa3fbc1958629a7a47bc1cf6dd1c177ef358e9d1f0d4b09328cc1213e8d3580703aee51ccf97e482be977f7bc"));

    public static final Address ADDRESS2 =
            Address.fromAccountNumber("f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy");

    public static final KeyPair KEY2 = Ed25519KeyPair.from(HEX.decode(
            "9bdf52f23deb941ea23cec982c24a5c811d321e71f6df56508bd511f66311e06"),
                                                           HEX.decode(
                                                                   "4534075cbcfc6ada1bb6b9e53d53f72341746031d9d17a3089a117766e7cda9e9bdf52f23deb941ea23cec982c24a5c811d321e71f6df56508bd511f66311e06"));

    public static final Address ADDRESS3 =
            Address.fromAccountNumber("eY7ddmQcwDSUtPybUsDXyZtMeDcKEnGq36yVkeFuMMmwP6NhcV");

    public static final KeyPair KEY3 = Ed25519KeyPair.from(HEX.decode(
            "4f6789400e2bff0a30bcfa6dcbe8e346be3f8676ec2eac49491072ca2ea90c92"),
                                                           HEX.decode(
                                                                   "413fe9a50366355c55174d7bab323f02669869cc674d10874d86dc0bd5dd61ee4f6789400e2bff0a30bcfa6dcbe8e346be3f8676ec2eac49491072ca2ea90c92"));
}
