package com.bitmark.apiservice;

import io.github.centrifugal.centrifuge.*;

/**
 * @author Hieu Pham
 * @since 7/6/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
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

    private boolean isSubscribed(String channel) {
        return client.getSubscription(channel) != null;
    }

    boolean isConnected() {
        return isConnected;
    }
}
