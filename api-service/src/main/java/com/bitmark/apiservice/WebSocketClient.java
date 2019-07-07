package com.bitmark.apiservice;

import io.github.centrifugal.centrifuge.*;

import java.util.HashMap;
import java.util.Map;

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

    private final Map<String, Subscription> subscribedChannel = new HashMap<>();

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
        if (isSubscribed(channel)) return;

        final SubscriptionEventListener internalListener = new SubscriptionEventListener() {
            @Override
            public void onPublish(Subscription sub, PublishEvent event) {
                super.onPublish(sub, event);
                listener.onPublish(sub, event);
            }

            @Override
            public void onSubscribeSuccess(Subscription sub, SubscribeSuccessEvent event) {
                super.onSubscribeSuccess(sub, event);
                subscribedChannel.put(channel, sub);
                listener.onSubscribeSuccess(sub, event);
            }

            @Override
            public void onSubscribeError(Subscription sub, SubscribeErrorEvent event) {
                super.onSubscribeError(sub, event);
                listener.onSubscribeError(sub, event);
            }

            @Override
            public void onUnsubscribe(Subscription sub, UnsubscribeEvent event) {
                super.onUnsubscribe(sub, event);
                final String channel = sub.getChannel();
                subscribedChannel.remove(channel);
                listener.onUnsubscribe(sub, event);
            }
        };

        final Subscription subscription = client.newSubscription(channel, internalListener);
        subscription.subscribe();
    }

    void unsubscribe(String channel) {
        if (!isSubscribed(channel)) return;
        final Subscription subscription = subscribedChannel.get(channel);
        subscription.unsubscribe();
    }

    @Override
    public void onConnect(Client client, ConnectEvent event) {
        super.onConnect(client, event);
        isConnected = true;
        if (null != eventListener) eventListener.onConnect(client, event);
    }

    @Override
    public void onDisconnect(Client client, DisconnectEvent event) {
        super.onDisconnect(client, event);
        isConnected = false;
        if (null != eventListener) eventListener.onDisconnect(client, event);
    }

    private boolean isSubscribed(String channel) {
        return subscribedChannel.containsKey(channel);
    }

    boolean isConnected() {
        return isConnected;
    }
}
