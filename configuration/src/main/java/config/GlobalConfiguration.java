package config;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class GlobalConfiguration {

    private Builder builder;

    private GlobalConfiguration(Builder builder) {
        this.builder = builder;
    }

    public Network getNetwork() {
        return builder.network;
    }

    public String getApiToken() {
        return builder.apiToken;
    }

    public int getConnectionTimeout() {
        return builder.connectionTimeout;
    }


    public static final class Builder {

        private Network network = Network.TEST_NET;

        private String apiToken;

        private int connectionTimeout = 30;

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

        public GlobalConfiguration build() {
            validate();
            return new GlobalConfiguration(this);
        }

        private void validate() {
            if (apiToken == null || apiToken.isEmpty())
                throw new IllegalArgumentException("Api token is required");

        }

    }
}
