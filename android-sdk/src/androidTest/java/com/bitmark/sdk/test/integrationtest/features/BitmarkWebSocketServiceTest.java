package com.bitmark.sdk.test.integrationtest.features;

import com.bitmark.apiservice.BitmarkWebSocket;
import com.bitmark.apiservice.BitmarkWebSocketService;
import com.bitmark.apiservice.WebSocket;
import com.bitmark.sdk.test.integrationtest.BaseTest;
import io.github.centrifugal.centrifuge.SubscribeErrorEvent;
import io.github.centrifugal.centrifuge.SubscribeSuccessEvent;
import io.github.centrifugal.centrifuge.UnsubscribeEvent;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.bitmark.sdk.test.integrationtest.DataProvider.KEY1;
import static org.junit.Assert.assertEquals;


/**
 * @author Hieu Pham
 * @since 7/7/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class BitmarkWebSocketServiceTest extends BaseTest {

    @Test
    public void testConnect() throws InterruptedException {
        final boolean expectedConnected = true;
        final AtomicBoolean actualConnected = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        final WebSocket.ConnectionEvent connectionEvent = new WebSocket.ConnectionEvent() {
            @Override
            public void onConnected() {
                actualConnected.set(true);
                done.set(true);
            }

            @Override
            public void onDisconnected() {
                actualConnected.set(false);
                done.set(true);
            }

            @Override
            public void onConnectionError(Throwable e) {
                actualConnected.set(false);
                done.set(true);
            }
        };
        final WebSocket ws = new BitmarkWebSocketService(connectionEvent);
        ws.connect(KEY1);

        while (!done.get()) {
            Thread.sleep(200);
        }
        assertEquals(expectedConnected, actualConnected.get());
        ws.disconnect();
    }

    @Test
    public void testDisconnect() throws InterruptedException {
        final boolean expectedConnected = false;
        final AtomicBoolean actualConnected = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        AtomicReference<BitmarkWebSocket> wsRef = new AtomicReference<>();
        final WebSocket.ConnectionEvent connectionEvent = new WebSocket.ConnectionEvent() {
            @Override
            public void onConnected() {
                actualConnected.set(true);
                wsRef.get().disconnect();
            }

            @Override
            public void onDisconnected() {
                actualConnected.set(false);
                done.set(true);
            }

            @Override
            public void onConnectionError(Throwable e) {
                actualConnected.set(true);
                done.set(true);
            }
        };
        BitmarkWebSocket ws = new BitmarkWebSocketService(connectionEvent);
        wsRef.set(ws);
        ws.connect(KEY1);

        while (!done.get()) {
            Thread.sleep(200);
        }
        assertEquals(expectedConnected, actualConnected.get());
        ws.disconnect();
    }

    @Test
    public void testSubscribe() throws Throwable {
        final boolean expectedSubscribed = true;
        final AtomicBoolean actualSubscribed = new AtomicBoolean();
        final AtomicBoolean connectDone = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        final WebSocket.ConnectionEvent connectionEvent = new WebSocket.ConnectionEvent() {
            @Override
            public void onConnected() {
                connectDone.set(true);
            }

            @Override
            public void onDisconnected() {
                error.set(new RuntimeException("failed to connect"));
                connectDone.set(true);
            }

            @Override
            public void onConnectionError(Throwable e) {
                error.set(new RuntimeException("failed to connect"));
                connectDone.set(true);
            }
        };
        BitmarkWebSocket ws = new BitmarkWebSocketService(connectionEvent);
        ws.connect(KEY1);

        while (!connectDone.get()) {
            Thread.sleep(200);
        }

        if (null != error.get()) {
            throw error.get();
        }

        ws.subscribeNewBlock(new BitmarkWebSocket.NewBlockEvent() {

            @Override
            public void onNewBlock(long blockNumber) {

            }

            @Override
            public void onSubscribeSuccess(SubscribeSuccessEvent event) {
                super.onSubscribeSuccess(event);
                actualSubscribed.set(true);
                done.set(true);
            }

            @Override
            public void onSubscribeError(SubscribeErrorEvent event) {
                super.onSubscribeError(event);
                actualSubscribed.set(false);
                done.set(true);
            }
        });

        while (!done.get()) {
            Thread.sleep(200);
        }

        ws.unsubscribeNewBlock();

        assertEquals(expectedSubscribed, actualSubscribed.get());

        ws.disconnect();
    }

    @Test
    public void testUnsubscribe() throws Throwable {
        final boolean expectedUnsubscribed = true;
        final AtomicBoolean actualUnsubscribed = new AtomicBoolean();
        final AtomicBoolean connectDone = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        final WebSocket.ConnectionEvent connectionEvent = new WebSocket.ConnectionEvent() {
            @Override
            public void onConnected() {
                connectDone.set(true);
            }

            @Override
            public void onDisconnected() {
                error.set(new RuntimeException("failed to connect"));
                connectDone.set(true);
            }

            @Override
            public void onConnectionError(Throwable e) {
                error.set(new RuntimeException("failed to connect"));
                connectDone.set(true);
            }
        };
        BitmarkWebSocket ws = new BitmarkWebSocketService(connectionEvent);
        ws.connect(KEY1);

        while (!connectDone.get()) {
            Thread.sleep(200);
        }

        if (null != error.get()) {
            throw error.get();
        }

        ws.subscribeNewBlock(new BitmarkWebSocket.NewBlockEvent() {

            @Override
            public void onNewBlock(long blockNumber) {

            }

            @Override
            public void onUnsubscribe(UnsubscribeEvent event) {
                super.onUnsubscribe(event);
                actualUnsubscribed.set(true);
                done.set(true);
            }

            @Override
            public void onSubscribeSuccess(SubscribeSuccessEvent event) {
                super.onSubscribeSuccess(event);
                ws.unsubscribeNewBlock();
            }

            @Override
            public void onSubscribeError(SubscribeErrorEvent event) {
                super.onSubscribeError(event);
                error.set(new RuntimeException("subscribe failed"));
                done.set(true);
            }
        });

        while (!done.get()) {
            Thread.sleep(200);
        }

        if (null != error.get()) {
            throw error.get();
        }

        assertEquals(expectedUnsubscribed, actualUnsubscribed.get());
        ws.disconnect();
    }
}
