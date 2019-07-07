package com.bitmark.apiservice;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import com.bitmark.apiservice.params.RegisterWsTokenParams;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.error.UnexpectedException;
import com.bitmark.cryptography.crypto.key.KeyPair;
import io.github.centrifugal.centrifuge.*;

import java.nio.charset.Charset;
import java.util.Map;

import static com.bitmark.apiservice.utils.HttpUtils.jsonToMap;

/**
 * @author Hieu Pham
 * @since 7/6/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class BitmarkWebSocketService implements BitmarkWebSocket {

    private static final String WS_ENDPOINT_TEST_NET =
            "wss://subscription.api.test.bitmark.com/connection/websocket?format=protobuf";

    private static final String WS_ENDPOINT_LIVE_NET =
            "wss://subscription.api.bitmark.com/connection/websocket?format=protobuf";

    private WebSocketClient client;

    @Override
    public void connect(KeyPair keyPair, ConnectionEvent connEvent) {
        // disconnect before if needed
        if (isConnected()) disconnect();

        // register token
        Network network = GlobalConfiguration.network();
        Address requester = Address.getDefault(keyPair.publicKey(), network);
        RegisterWsTokenParams params = new RegisterWsTokenParams(requester);
        params.sign(keyPair);
        new ApiService().registerWsToken(params, new Callback1<String>() {
            @Override
            public void onSuccess(String token) {
                connect(token, connEvent);
            }

            @Override
            public void onError(Throwable throwable) {
                connEvent.onConnectionError(throwable);
            }
        });

    }

    private void connect(String token, ConnectionEvent connEvent) {
        final String endpoint = GlobalConfiguration.network() ==
                                Network.TEST_NET ? WS_ENDPOINT_TEST_NET : WS_ENDPOINT_LIVE_NET;
        client = new WebSocketClient(endpoint, token);
        client.setEventListener(new EventListener() {
            @Override
            public void onConnect(Client client, ConnectEvent ev) {
                super.onConnect(client, ev);
                connEvent.onConnected();
            }

            @Override
            public void onDisconnect(Client client, DisconnectEvent ev) {
                super.onDisconnect(client, ev);
                connEvent.onDisconnected();
            }

            @Override
            public void onError(Client client, ErrorEvent ev) {
                super.onError(client, ev);
                connEvent.onConnectionError(new UnexpectedException("cannot connect to ws server"));
            }
        });
        client.connect();
    }

    @Override
    public void disconnect() {
        if (null == client) return;
        client.disconnect();
    }

    @Override
    public void subscribeNewBlock(NewBlockEvent event) {
        if (null == client) return;
        client.subscribe("blockchain:new-block", new SubscriptionEventListener() {
            @Override
            public void onSubscribeError(Subscription sub, SubscribeErrorEvent ev) {
                super.onSubscribeError(sub, ev);
                event.onSubscribeError(ev);
            }

            @Override
            public void onSubscribeSuccess(Subscription sub, SubscribeSuccessEvent ev) {
                super.onSubscribeSuccess(sub, ev);
                event.onSubscribeSuccess(ev);
            }

            @Override
            public void onUnsubscribe(Subscription sub, UnsubscribeEvent ev) {
                super.onUnsubscribe(sub, ev);
                event.onUnsubscribe(ev);
            }

            @Override
            public void onPublish(Subscription sub, PublishEvent ev) {
                super.onPublish(sub, ev);

                try {
                    Map<String, Object> data = processPublishEvent(ev);
                    event.onNewBlock(
                            (long) Double.parseDouble(String.valueOf(data.get("block_number"))));
                } catch (Throwable ignore) {
                }

            }
        });
    }

    @Override
    public void unsubscribeNewBlock() {
        if (null == client) return;
        client.unsubscribe("blockchain:new-block");
    }

    @Override
    public void subscribeBitmarkChanged(Address owner, BitmarkChangedEvent event) {
        if (null == client) return;
        String channel = String.format("bitmark_changed:%s", owner.getAddress());
        client.subscribe(channel, new SubscriptionEventListener() {

            @Override
            public void onSubscribeError(Subscription sub, SubscribeErrorEvent ev) {
                super.onSubscribeError(sub, ev);
                event.onSubscribeError(ev);
            }

            @Override
            public void onSubscribeSuccess(Subscription sub, SubscribeSuccessEvent ev) {
                super.onSubscribeSuccess(sub, ev);
                event.onSubscribeSuccess(ev);
            }

            @Override
            public void onUnsubscribe(Subscription sub, UnsubscribeEvent ev) {
                super.onUnsubscribe(sub, ev);
                event.onUnsubscribe(ev);
            }

            @Override
            public void onPublish(Subscription sub, PublishEvent ev) {
                super.onPublish(sub, ev);
                try {
                    Map<String, Object> data = processPublishEvent(ev);
                    event.onChanged(String.valueOf(data.get("bitmark_id")),
                                    String.valueOf(data.get("tx_id")),
                                    Boolean.parseBoolean(String.valueOf(data.get("presence"))));
                } catch (Throwable ignore) {
                }
            }
        });
    }

    @Override
    public void unsubscribeBitmarkChanged(Address owner) {
        if (null == client) return;
        String channel = String.format("bitmark_changed:%s", owner.getAddress());
        client.unsubscribe(channel);
    }

    @Override
    public void subscribeTransferOffer(Address requester, TransferOfferEvent event) {
        if (null == client) return;
        String channel = String.format("tx_offer#%s", requester.getAddress());
        client.subscribe(channel, new SubscriptionEventListener() {

            @Override
            public void onSubscribeError(Subscription sub, SubscribeErrorEvent ev) {
                super.onSubscribeError(sub, ev);
                event.onSubscribeError(ev);
            }

            @Override
            public void onSubscribeSuccess(Subscription sub, SubscribeSuccessEvent ev) {
                super.onSubscribeSuccess(sub, ev);
                event.onSubscribeSuccess(ev);
            }

            @Override
            public void onUnsubscribe(Subscription sub, UnsubscribeEvent ev) {
                super.onUnsubscribe(sub, ev);
                event.onUnsubscribe(ev);
            }

            @Override
            public void onPublish(Subscription sub, PublishEvent ev) {
                super.onPublish(sub, ev);
                try {
                    Map<String, Object> data = processPublishEvent(ev);
                    event.onReceived(String.valueOf(data.get("bitmark_id")));
                } catch (Throwable ignore) {
                }
            }

        });
    }

    @Override
    public void unsubscribeTransferOffer(Address requester) {
        if (null == client) return;
        String channel = String.format("tx_offer#%s", requester.getAddress());
        client.unsubscribe(channel);
    }

    @Override
    public void subscribeNewPendingIssuance(Address owner, NewPendingIssuanceEvent event) {
        if (null == client) return;

        String channel = String.format("bitmark_pending_change:%s", owner.getAddress());
        client.subscribe(channel, new SubscriptionEventListener() {
            @Override
            public void onPublish(Subscription sub, PublishEvent ev) {
                super.onPublish(sub, ev);
                try {
                    Map<String, Object> data = processPublishEvent(ev);
                    event.onNewPendingIssuance(String.valueOf(data.get("bitmark_id")));
                } catch (Throwable ignore) {
                }
            }

            @Override
            public void onSubscribeSuccess(Subscription sub, SubscribeSuccessEvent ev) {
                super.onSubscribeSuccess(sub, ev);
                event.onSubscribeSuccess(ev);
            }

            @Override
            public void onSubscribeError(Subscription sub, SubscribeErrorEvent ev) {
                super.onSubscribeError(sub, ev);
                event.onSubscribeError(ev);
            }

            @Override
            public void onUnsubscribe(Subscription sub, UnsubscribeEvent ev) {
                super.onUnsubscribe(sub, ev);
                event.onUnsubscribe(ev);
            }
        });
    }

    @Override
    public void unsubscribeNewPendingIssuance(Address owner) {
        if (null == client) return;

        String channel = String.format("bitmark_pending_change:%s", owner.getAddress());
        client.unsubscribe(channel);
    }

    @Override
    public void subscribeNewPendingTx(Address stakeHolder, NewPendingTxEvent event) {
        if (null == client) return;

        String channel = String.format("tx_pending_change:%s", stakeHolder.getAddress());
        client.subscribe(channel, new SubscriptionEventListener() {
            @Override
            public void onPublish(Subscription sub, PublishEvent ev) {
                super.onPublish(sub, ev);
                try {
                    Map<String, Object> data = processPublishEvent(ev);
                    String txId = String.valueOf(data.get("tx_id"));
                    String owner = String.valueOf(data.get("owner"));
                    String prevTxId = String.valueOf(data.get("previous_tx_id"));
                    String prevOwner = String.valueOf(data.get("previous_owner"));
                    event.onNewPendingIx(txId, owner, prevTxId, prevOwner);
                } catch (Throwable ignore) {
                }
            }

            @Override
            public void onSubscribeSuccess(Subscription sub, SubscribeSuccessEvent ev) {
                super.onSubscribeSuccess(sub, ev);
                event.onSubscribeSuccess(ev);
            }

            @Override
            public void onSubscribeError(Subscription sub, SubscribeErrorEvent ev) {
                super.onSubscribeError(sub, ev);
                event.onSubscribeError(ev);
            }

            @Override
            public void onUnsubscribe(Subscription sub, UnsubscribeEvent ev) {
                super.onUnsubscribe(sub, ev);
                event.onUnsubscribe(ev);
            }
        });
    }

    @Override
    public void unsubscribeNewPendingTx(Address stakeHolder) {
        if (null == client) return;

        String channel = String.format("tx_pending_change:%s", stakeHolder.getAddress());
        client.unsubscribe(channel);
    }

    private Map<String, Object> processPublishEvent(PublishEvent ev) {
        String data = new String(ev.getData(), Charset.forName("UTF-8"));
        return jsonToMap(data);
    }

    private boolean isConnected() {
        return null != client && client.isConnected();
    }
}
