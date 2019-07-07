package com.bitmark.apiservice.test.integrationtest;

import com.bitmark.apiservice.BitmarkWebSocket;
import com.bitmark.apiservice.BitmarkWebSocketService;
import com.bitmark.apiservice.WebSocket;
import io.github.centrifugal.centrifuge.SubscribeErrorEvent;
import io.github.centrifugal.centrifuge.SubscribeSuccessEvent;
import io.github.centrifugal.centrifuge.UnsubscribeEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.bitmark.apiservice.test.integrationtest.DataProvider.KEY1;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Hieu Pham
 * @since 7/7/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class BitmarkWebSocketServiceTest extends BaseTest {

    private BitmarkWebSocket ws;

    @BeforeEach
    public void setup() {
        ws = new BitmarkWebSocketService();
    }

    @AfterEach
    public void teardown() {
        ws.disconnect();
        ws = null;
    }

    @Test
    public void testConnect() throws InterruptedException {
        final boolean expectedConnected = true;
        final AtomicBoolean actualConnected = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        ws.connect(KEY1, new WebSocket.ConnectionEvent() {
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
        });

        while (!done.get()) {
            Thread.sleep(200);
        }
        assertEquals(expectedConnected, actualConnected.get());
    }

    @Test
    public void testDisconnect() throws InterruptedException {
        final boolean expectedConnected = false;
        final AtomicBoolean actualConnected = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        ws.connect(KEY1, new WebSocket.ConnectionEvent() {
            @Override
            public void onConnected() {
                actualConnected.set(true);
                ws.disconnect();
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
        });

        while (!done.get()) {
            Thread.sleep(200);
        }
        assertEquals(expectedConnected, actualConnected.get());
    }

    @Test
    public void testSubscribe() throws Throwable {
        final boolean expectedSubscribed = true;
        final AtomicBoolean actualSubscribed = new AtomicBoolean();
        final AtomicBoolean connectDone = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        ws.connect(KEY1, new WebSocket.ConnectionEvent() {
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
        });

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
    }

    @Test
    public void testUnsubscribe() throws Throwable {
        final boolean expectedUnsubscribed = true;
        final AtomicBoolean actualUnsubscribed = new AtomicBoolean();
        final AtomicBoolean connectDone = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        ws.connect(KEY1, new WebSocket.ConnectionEvent() {
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
        });

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
    }
}
