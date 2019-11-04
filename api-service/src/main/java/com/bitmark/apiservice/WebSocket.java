/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice;

import com.bitmark.cryptography.crypto.key.KeyPair;
import io.github.centrifugal.centrifuge.SubscribeErrorEvent;
import io.github.centrifugal.centrifuge.SubscribeSuccessEvent;
import io.github.centrifugal.centrifuge.UnsubscribeEvent;

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
