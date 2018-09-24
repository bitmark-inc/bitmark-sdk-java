package com.bitmark.sdk.config;

import com.bitmark.sdk.annotation.VisibleForTesting;

import static com.bitmark.sdk.utils.Validator.checkNonNull;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class GlobalConfiguration {

    private Builder builder;

    private static volatile GlobalConfiguration INSTANCE;

    public static Builder builder() {
        return new Builder();
    }

    public static void createInstance(String apiToken) {
        createInstance(new Builder(apiToken));
    }

    public static void createInstance(Builder builder) {
        checkNonNull(builder);
        builder.validate();
        if (INSTANCE == null) {
            synchronized (GlobalConfiguration.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GlobalConfiguration(builder);
                }
            }
        } else
            throw new UnsupportedOperationException("GlobalConfiguration must be initialize once");
    }

    public static boolean isInitialized() {
        return INSTANCE != null;
    }

    @VisibleForTesting
    public static void destroy(){
        INSTANCE = null;
    }

    private GlobalConfiguration(Builder builder) {
        this.builder = builder;
    }

    public static Network network() {
        validate();
        return INSTANCE.builder.network;
    }

    public static String apiToken() {
        validate();
        return INSTANCE.builder.apiToken;
    }

    public static int connectionTimeout() {
        validate();
        return INSTANCE.builder.connectionTimeout;
    }

    private static void validate() {
        if (INSTANCE == null) throw new UnsupportedOperationException("You must init " +
                "Configuration before");
    }


    public static final class Builder {

        private Network network = Network.TEST_NET;

        private String apiToken;

        private int connectionTimeout = 30; // 30 seconds

        Builder() {
        }

        Builder(String apiToken) {
            this.apiToken = apiToken;
            validate();
        }

        public Builder withNetwork(Network network) {
            this.network = network;
            return this;
        }

        public Builder withApiToken(String apiToken) {
            this.apiToken = apiToken;
            return this;
        }

        public Builder withConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public void build() {
            GlobalConfiguration.createInstance(this);
        }

        private void validate() {
            if (apiToken == null || apiToken.isEmpty())
                throw new IllegalArgumentException("Api token is required");

        }

    }
}
