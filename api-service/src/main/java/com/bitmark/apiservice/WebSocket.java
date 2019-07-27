package com.bitmark.apiservice;

import com.bitmark.cryptography.crypto.key.KeyPair;
import io.github.centrifugal.centrifuge.SubscribeErrorEvent;
import io.github.centrifugal.centrifuge.SubscribeSuccessEvent;
import io.github.centrifugal.centrifuge.UnsubscribeEvent;

/**
 * @author Hieu Pham
 * @since 7/7/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public interface WebSocket {

    void connect(KeyPair keyPair);

    void disconnect();

    abstract class SubscribeEvent {

        public void onSubscribeSuccess(SubscribeSuccessEvent event) {
        }

        public void onSubscribeError(SubscribeErrorEvent event) {
        }

        public void onUnsubscribe(UnsubscribeEvent event) {
        }
    }

    interface ConnectionEvent {

        void onConnected();

        void onDisconnected();

        void onConnectionError(Throwable e);
    }
}
