package config;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public enum Network {

    LIVE_NET(0x00), TEST_NET(0x01);

    private int value;

    Network(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
