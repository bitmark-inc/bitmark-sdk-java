/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.authentication;

import android.content.Context;
import com.bitmark.sdk.BuildConfig;
import com.bitmark.sdk.R;

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
                .setAuthenticationDescription(getAuthenticationDescription())
                .setAuthenticationTitle(getAuthenticationTitle())
                .setUseAlternativeAuthentication(useAlternativeAuthentication());
    }

    public boolean isAuthenticationRequired() {
        return builder.isAuthenticationRequired;
    }

    public int getAuthenticationValidityDuration() {
        return builder.authenticationValidityDuration;
    }

    public String getAuthenticationTitle() {
        return builder.authenticationTitle;
    }

    public String getAuthenticationDescription() {
        return builder.authenticationDescription;
    }

    public String getKeyAlias() {
        return builder.keyAlias;
    }

    public boolean useAlternativeAuthentication() {
        return builder.useAlternativeAuthentication;
    }

    public boolean willInvalidateInTimeFrame() {
        return getAuthenticationValidityDuration() != -1;
    }

    public boolean needAuthenticateImmediately() {
        return isAuthenticationRequired() && !willInvalidateInTimeFrame();
    }

    public static final class Builder {

        private String keyAlias;

        private boolean isAuthenticationRequired;

        private int authenticationValidityDuration = -1;

        private String authenticationTitle;

        private String authenticationDescription;

        private boolean useAlternativeAuthentication;

        public Builder(Context context) {
            initDefault(context);
        }

        private void initDefault(Context context) {
            keyAlias = BuildConfig.APPLICATION_ID + "encryption_key";
            isAuthenticationRequired = false;
            useAlternativeAuthentication = false;
            authenticationTitle = context.getString(R.string.authentication);
            authenticationDescription = context.getString(R.string.please_authenticate_to_unlock);

        }

        public Builder setAuthenticationRequired(boolean authenticationRequired) {
            isAuthenticationRequired = authenticationRequired;
            return this;
        }

        public Builder setAuthenticationValidityDuration(
                int authenticationValidityDuration
        ) {
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

        public Builder setUseAlternativeAuthentication(boolean use) {
            this.useAlternativeAuthentication = use;
            return this;
        }

        public KeyAuthenticationSpec build() {
            return new KeyAuthenticationSpec(this);
        }
    }
}
