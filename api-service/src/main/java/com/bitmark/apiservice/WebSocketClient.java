/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice;

import io.github.centrifugal.centrifuge.*;

class WebSocketClient extends EventListener {

    private Client client;

    private EventListener eventListener;

    private boolean isConnected;

    WebSocketClient(String address, String token) {
        client = new Client(address, new Options(), this);
        client.setToken(token);
    }

    void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    void connect() {
        client.connect();
    }

    void disconnect() {
        client.disconnect();
    }

    void subscribe(String channel, SubscriptionEventListener listener) {
        if (isSubscribed(channel)) {
            return;
        }

        Subscription subscription = client.getSubscription(channel);
        if (subscription == null) {
            subscription = client.newSubscription(channel, listener);
        }
        subscription.subscribe();
    }

    void unsubscribe(String channel) {
        if (!isSubscribed(channel)) {
            return;
        }
        final Subscription subscription = client.getSubscription(channel);
        client.removeSubscription(subscription);
    }

    @Override
    public void onConnect(Client client, ConnectEvent event) {
        super.onConnect(client, event);
        isConnected = true;
        if (null != eventListener) {
            eventListener.onConnect(client, event);
        }
    }

    @Override
    public void onDisconnect(Client client, DisconnectEvent event) {
        super.onDisconnect(client, event);
        isConnected = false;
        if (null != eventListener) {
            eventListener.onDisconnect(client, event);
        }
    }

    @Override
    public void onError(
            Client client, ErrorEvent event
    ) {
        super.onError(client, event);
        if (null != eventListener) {
            eventListener.onError(client, event);
        }
    }

    private boolean isSubscribed(String channel) {
        return client.getSubscription(channel) != null;
    }

    boolean isConnected() {
        return isConnected;
    }
}
