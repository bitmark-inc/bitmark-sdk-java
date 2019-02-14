package com.bitmark.sdk.authentication;

import android.content.Context;
import com.bitmark.sdk.BuildConfig;
import com.bitmark.sdk.R;

/**
 * @author Hieu Pham
 * @since 2/11/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class KeyAuthenticationSpec {

    private Builder builder;

    private KeyAuthenticationSpec() {
    }

    private KeyAuthenticationSpec(Builder builder) {
        this();
        this.builder = builder;
    }

    public Builder newBuilder(Context context) {
        return new Builder(context).setKeyAlias(getKeyAlias())
                                   .setAuthenticationRequired(isAuthenticationRequired())
                                   .setAuthenticationValidityDuration(
                                           getAuthenticationValidityDuration())
                                   .setAuthenticationDescription(
                                           getAuthenticationDescriptionResId())
                                   .setAuthenticationTitle(getAuthenticationTitleResId());
    }

    public boolean isAuthenticationRequired() {
        return builder.isAuthenticationRequired;
    }

    public int getAuthenticationValidityDuration() {
        return builder.authenticationValidityDuration;
    }

    public String getAuthenticationTitleResId() {
        return builder.authenticationTitle;
    }

    public String getAuthenticationDescriptionResId() {
        return builder.authenticationDescription;
    }

    public String getKeyAlias() {
        return builder.keyAlias;
    }

    public boolean willInvalidateInTimeFrame() {
        return getAuthenticationValidityDuration() != -1;
    }

    public static final class Builder {

        private String keyAlias;

        private boolean isAuthenticationRequired;

        private int authenticationValidityDuration = -1;

        private String authenticationTitle;

        private String authenticationDescription;

        public Builder(Context context) {
            initDefault(context);
        }

        private void initDefault(Context context) {
            keyAlias = BuildConfig.APPLICATION_ID + "encryption_key";
            isAuthenticationRequired = false;
            authenticationTitle = context.getString(R.string.authentication);
            authenticationDescription = context.getString(R.string.please_authenticate_to_unlock);

        }

        public Builder setAuthenticationRequired(boolean authenticationRequired) {
            isAuthenticationRequired = authenticationRequired;
            return this;
        }

        public Builder setAuthenticationValidityDuration(
                int authenticationValidityDuration) {
            this.authenticationValidityDuration = authenticationValidityDuration;
            return this;
        }

        public Builder setAuthenticationTitle(String authenticationTitle) {
            this.authenticationTitle = authenticationTitle;
            return this;
        }

        public Builder setAuthenticationDescription(String authenticationDescription) {
            this.authenticationDescription = authenticationDescription;
            return this;
        }

        public Builder setKeyAlias(String keyAlias) {
            this.keyAlias = keyAlias;
            return this;
        }

        public KeyAuthenticationSpec build() {
            return new KeyAuthenticationSpec(this);
        }
    }
}
