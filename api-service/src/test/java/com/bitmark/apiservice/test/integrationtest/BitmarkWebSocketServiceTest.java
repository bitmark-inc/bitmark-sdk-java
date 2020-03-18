/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
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

import static com.bitmark.apiservice.test.integrationtest.DataProvider.ADDRESS1;
import static com.bitmark.apiservice.test.integrationtest.DataProvider.KEY1;
import static org.junit.jupiter.api.Assertions.*;

public class BitmarkWebSocketServiceTest extends BaseTest {

    private BitmarkWebSocket ws;

    @BeforeEach
    public void setup() {

    }

    @AfterEach
    public void teardown() {
        if (ws != null) {
            ws.disconnect();
        }
        ws = null;
    }

    @Test
    public void testConnect() throws InterruptedException {
        final boolean expectedConnected = true;
        final AtomicBoolean actualConnected = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        ws = new BitmarkWebSocketService(new WebSocket.ConnectionEvent() {
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
        ws.connect(KEY1);

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
        ws = new BitmarkWebSocketService(new WebSocket.ConnectionEvent() {
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
        ws.connect(KEY1);

        while (!done.get()) {
            Thread.sleep(200);
        }
        assertEquals(expectedConnected, actualConnected.get());
    }

    @Test
    public void testSubscribe() throws Throwable {

        final AtomicBoolean connectDone = new AtomicBoolean();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        ws = new BitmarkWebSocketService(new WebSocket.ConnectionEvent() {
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
        ws.connect(KEY1);

        while (!connectDone.get()) {
            Thread.sleep(200);
        }

        if (null != error.get()) {
            throw error.get();
        }

        testSubscribeNewBlock();
        testSubscribeBitmarkChanged();
        testSubscribeTransferOffer();
        testSubscribeNewPendingIssuance();
        testSubscribeNewPendingTx();
    }

    @Test
    public void testUnsubscribe() throws Throwable {

        final AtomicBoolean connectDone = new AtomicBoolean();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        ws = new BitmarkWebSocketService(new WebSocket.ConnectionEvent() {
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
        ws.connect(KEY1);

        while (!connectDone.get()) {
            Thread.sleep(200);
        }

        if (null != error.get()) {
            throw error.get();
        }

        testUnsubscribeNewBlock();
        testUnsubscribeBitmarkChanged();
        testUnsubscribeTransferOffer();
        testUnsubscribeNewPendingIssuance();
        testUnsubscribeNewPendingTx();
    }

    private void testSubscribeNewBlock() throws InterruptedException {
        final boolean expectedSubscribed = true;
        final AtomicBoolean actualSubscribed = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
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

    private void testUnsubscribeNewBlock() throws Throwable {
        final boolean expectedUnsubscribed = true;
        final AtomicBoolean actualUnsubscribed = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        final AtomicReference<Throwable> error = new AtomicReference<>();

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

    private void testSubscribeBitmarkChanged() throws Throwable {
        final boolean expectedSubscribed = true;
        final AtomicBoolean actualSubscribed = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();

        ws.subscribeBitmarkChanged(
                ADDRESS1,
                new BitmarkWebSocket.BitmarkChangedEvent() {

                    @Override
                    public void onChanged(
                            String bitmarkId,
                            String txId,
                            boolean presence
                    ) {

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
                }
        );

        while (!done.get()) {
            Thread.sleep(200);
        }

        ws.unsubscribeBitmarkChanged(ADDRESS1);

        assertEquals(expectedSubscribed, actualSubscribed.get());
    }

    private void testUnsubscribeBitmarkChanged() throws Throwable {
        final boolean expectedUnsubscribed = true;
        final AtomicBoolean actualUnsubscribed = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        final AtomicReference<Throwable> error = new AtomicReference<>();

        ws.subscribeBitmarkChanged(
                ADDRESS1,
                new BitmarkWebSocket.BitmarkChangedEvent() {

                    @Override
                    public void onChanged(
                            String bitmarkId,
                            String txId,
                            boolean presence
                    ) {

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
                        ws.unsubscribeBitmarkChanged(ADDRESS1);
                    }

                    @Override
                    public void onSubscribeError(SubscribeErrorEvent event) {
                        super.onSubscribeError(event);
                        error.set(new RuntimeException("subscribe failed"));
                        done.set(true);
                    }
                }
        );

        while (!done.get()) {
            Thread.sleep(200);
        }

        if (null != error.get()) {
            throw error.get();
        }

        assertEquals(expectedUnsubscribed, actualUnsubscribed.get());

    }

    private void testSubscribeTransferOffer() throws Throwable {
        final boolean expectedSubscribed = true;
        final AtomicBoolean actualSubscribed = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();

        ws.subscribeTransferOffer(
                ADDRESS1,
                new BitmarkWebSocket.TransferOfferEvent() {

                    @Override
                    public void onReceived(String bitmarkId) {

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
                }
        );

        while (!done.get()) {
            Thread.sleep(200);
        }

        ws.unsubscribeTransferOffer(ADDRESS1);

        assertEquals(expectedSubscribed, actualSubscribed.get());
    }

    private void testUnsubscribeTransferOffer() throws Throwable {
        final boolean expectedUnsubscribed = true;
        final AtomicBoolean actualUnsubscribed = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        final AtomicReference<Throwable> error = new AtomicReference<>();

        ws.subscribeTransferOffer(
                ADDRESS1,
                new BitmarkWebSocket.TransferOfferEvent() {

                    @Override
                    public void onReceived(String bitmarkId) {

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
                        ws.unsubscribeTransferOffer(ADDRESS1);
                    }

                    @Override
                    public void onSubscribeError(SubscribeErrorEvent event) {
                        super.onSubscribeError(event);
                        error.set(new RuntimeException("subscribe failed"));
                        done.set(true);
                    }
                }
        );

        while (!done.get()) {
            Thread.sleep(200);
        }

        if (null != error.get()) {
            throw error.get();
        }

        assertEquals(expectedUnsubscribed, actualUnsubscribed.get());

    }

    private void testSubscribeNewPendingIssuance() throws Throwable {
        final boolean expectedSubscribed = true;
        final AtomicBoolean actualSubscribed = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();

        ws.subscribeNewPendingIssuance(
                ADDRESS1,
                new BitmarkWebSocket.NewPendingIssuanceEvent() {

                    @Override
                    public void onNewPendingIssuance(String bitmarkId) {

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
                }
        );

        while (!done.get()) {
            Thread.sleep(200);
        }

        ws.unsubscribeNewPendingIssuance(ADDRESS1);

        assertEquals(expectedSubscribed, actualSubscribed.get());
    }

    private void testUnsubscribeNewPendingIssuance() throws Throwable {
        final boolean expectedUnsubscribed = true;
        final AtomicBoolean actualUnsubscribed = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        final AtomicReference<Throwable> error = new AtomicReference<>();

        ws.subscribeNewPendingIssuance(
                ADDRESS1,
                new BitmarkWebSocket.NewPendingIssuanceEvent() {

                    @Override
                    public void onNewPendingIssuance(String bitmarkId) {

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
                        ws.unsubscribeNewPendingIssuance(ADDRESS1);
                    }

                    @Override
                    public void onSubscribeError(SubscribeErrorEvent event) {
                        super.onSubscribeError(event);
                        error.set(new RuntimeException("subscribe failed"));
                        done.set(true);
                    }
                }
        );

        while (!done.get()) {
            Thread.sleep(200);
        }

        if (null != error.get()) {
            throw error.get();
        }

        assertEquals(expectedUnsubscribed, actualUnsubscribed.get());

    }

    private void testSubscribeNewPendingTx() throws Throwable {
        final boolean expectedSubscribed = true;
        final AtomicBoolean actualSubscribed = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();

        ws.subscribeNewPendingTx(
                ADDRESS1,
                new BitmarkWebSocket.NewPendingTxEvent() {

                    @Override
                    public void onNewPendingIx(
                            String txId, String owner, String prevTxId,
                            String prevOwner
                    ) {

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
                }
        );

        while (!done.get()) {
            Thread.sleep(200);
        }

        ws.unsubscribeNewPendingTx(ADDRESS1);

        assertEquals(expectedSubscribed, actualSubscribed.get());
    }

    private void testUnsubscribeNewPendingTx() throws Throwable {
        final boolean expectedUnsubscribed = true;
        final AtomicBoolean actualUnsubscribed = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        final AtomicReference<Throwable> error = new AtomicReference<>();

        ws.subscribeNewPendingTx(
                ADDRESS1,
                new BitmarkWebSocket.NewPendingTxEvent() {

                    @Override
                    public void onNewPendingIx(
                            String txId, String owner, String prevTxId,
                            String prevOwner
                    ) {

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
                        ws.unsubscribeNewPendingTx(ADDRESS1);
                    }

                    @Override
                    public void onSubscribeError(SubscribeErrorEvent event) {
                        super.onSubscribeError(event);
                        error.set(new RuntimeException("subscribe failed"));
                        done.set(true);
                    }
                }
        );

        while (!done.get()) {
            Thread.sleep(200);
        }

        if (null != error.get()) {
            throw error.get();
        }

        assertEquals(expectedUnsubscribed, actualUnsubscribed.get());

    }

    @Test
    public void testAlreadySubscribed() throws Throwable {
        final AtomicBoolean done = new AtomicBoolean();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        ws = new BitmarkWebSocketService(new WebSocket.ConnectionEvent() {
            @Override
            public void onConnected() {
                done.set(true);
            }

            @Override
            public void onDisconnected() {
                error.set(new RuntimeException("failed to connect"));
                done.set(true);
            }

            @Override
            public void onConnectionError(Throwable e) {
                error.set(new RuntimeException("failed to connect"));
                done.set(true);
            }
        });
        ws.connect(KEY1);

        while (!done.get()) {
            Thread.sleep(200);
        }

        if (null != error.get()) {
            throw error.get();
        }

        done.set(false);
        error.set(null);

        ws.subscribeNewBlock(new BitmarkWebSocket.NewBlockEvent() {
            @Override
            public void onNewBlock(long blockNumber) {

            }

            @Override
            public void onSubscribeSuccess(SubscribeSuccessEvent event) {
                super.onSubscribeSuccess(event);
                ws.subscribeNewBlock(new BitmarkWebSocket.NewBlockEvent() {
                    @Override
                    public void onNewBlock(long blockNumber) {

                    }

                    @Override
                    public void onSubscribeSuccess(SubscribeSuccessEvent event) {
                        super.onSubscribeSuccess(event);
                        error.set(new Throwable(
                                "expect subscribe event failed because of duplication but it's still success"));
                        done.set(true);
                    }

                    @Override
                    public void onSubscribeError(SubscribeErrorEvent event) {
                        super.onSubscribeError(event);
                        error.set(new Throwable(event.getMessage()));
                        done.set(true);
                    }
                });
            }

            @Override
            public void onSubscribeError(SubscribeErrorEvent event) {
                super.onSubscribeError(event);
                error.set(new Throwable(event.getMessage()));
                done.set(true);
            }

        });

        while (!done.get()) {
            Thread.sleep(200);
        }

        assertNotNull(error.get());
        assertEquals("already subscribed", error.get().getMessage());
    }
}
