package apiservice.configuration;

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

    public static Network valueOf(int value) {
        switch (value) {
            case 0x00:
                return LIVE_NET;
            case 0x01:
                return TEST_NET;
            default:
                throw new IllegalArgumentException("Invalid network");
        }
    }

    public static boolean isValid(int value) {
        return value == LIVE_NET.value || value == TEST_NET.value;
    }
}
