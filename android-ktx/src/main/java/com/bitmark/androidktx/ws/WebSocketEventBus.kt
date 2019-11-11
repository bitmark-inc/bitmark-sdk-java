/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.androidktx.ws

import android.os.Handler
import android.os.Looper
import com.bitmark.apiservice.BitmarkWebSocket
import com.bitmark.apiservice.BitmarkWebSocketService
import com.bitmark.apiservice.WebSocket
import com.bitmark.apiservice.utils.Address
import com.bitmark.cryptography.crypto.key.KeyPair
import io.github.centrifugal.centrifuge.SubscribeErrorEvent
import io.github.centrifugal.centrifuge.SubscribeSuccessEvent
import io.reactivex.Completable
import io.reactivex.subjects.PublishSubject

class WebSocketEventBus : Bus(), WebSocket.ConnectionEvent {

    private val handler = Handler(Looper.getMainLooper())

    private var connectListener: ((Throwable?) -> Unit)? = null

    private var disconnectListener: (() -> Unit)? = null

    private val webSocketService = BitmarkWebSocketService(this)

    val newPendingTxPublisher =
        Publisher(PublishSubject.create<Map<String, String>>())

    val bitmarkChangedPublisher =
        Publisher(PublishSubject.create<Map<String, Any>>())

    val newPendingIssuancePublisher = Publisher(PublishSubject.create<String>())

    val newBlockPublisher = Publisher(PublishSubject.create<Long>())

    val newTransferOfferPublisher = Publisher(PublishSubject.create<String>())

    fun setConnectListener(listener: ((Throwable?) -> Unit)?) {
        this.connectListener = listener
    }

    fun setDisconnectListener(listener: (() -> Unit)?) {
        this.disconnectListener = listener
    }

    fun connect(keyPair: KeyPair) {
        webSocketService.connect(keyPair)
    }

    fun disconnect() {
        webSocketService.disconnect()
    }

    fun subscribeNewBlock() = Completable.create { emt ->
        webSocketService.subscribeNewBlock(object :
                                               BitmarkWebSocket.NewBlockEvent() {
            override fun onNewBlock(blockNumber: Long) {
                newBlockPublisher.publisher.onNext(blockNumber)
            }

            override fun onSubscribeSuccess(event: SubscribeSuccessEvent?) {
                super.onSubscribeSuccess(event)
                emt.onComplete()
            }

            override fun onSubscribeError(event: SubscribeErrorEvent?) {
                super.onSubscribeError(event)
                emt.onError(SubscribeEventException(event!!.code, "new-block#${event.message}"))
            }
        })
    }

    fun unsubscribeNewBlock() = Completable.fromCallable {
        webSocketService.unsubscribeNewBlock()
    }

    fun subscribeNewTransferOffer(requester: String) = Completable.create { emt ->
        webSocketService.subscribeTransferOffer(
            Address.fromAccountNumber(requester),
            object : BitmarkWebSocket.TransferOfferEvent() {
                override fun onReceived(bitmarkId: String?) {
                    newTransferOfferPublisher.publisher.onNext(bitmarkId!!)
                }

                override fun onSubscribeSuccess(event: SubscribeSuccessEvent?) {
                    super.onSubscribeSuccess(event)
                    emt.onComplete()
                }

                override fun onSubscribeError(event: SubscribeErrorEvent?) {
                    super.onSubscribeError(event)
                    emt.onError(SubscribeEventException(event!!.code,
                                                        "new-transfer-offer#${event.message}"
                    )
                    )
                }
            })
    }

    fun unsubscribeNewTransferOffer(requester: String) = Completable.fromCallable {
        webSocketService.unsubscribeTransferOffer(
            Address.fromAccountNumber(requester)
        )
    }

    fun subscribeBitmarkChanged(owner: String) = Completable.create { emt ->
        webSocketService.subscribeBitmarkChanged(
            Address.fromAccountNumber(owner),
            object : BitmarkWebSocket.BitmarkChangedEvent() {
                override fun onChanged(
                        bitmarkId: String?,
                        txId: String?,
                        presence: Boolean
                ) {
                    bitmarkChangedPublisher.publisher.onNext(
                        mapOf(
                            "bitmark_id" to bitmarkId!!,
                            "tx_id" to txId!!,
                            "presence" to presence
                        )
                    )
                }

                override fun onSubscribeSuccess(event: SubscribeSuccessEvent?) {
                    super.onSubscribeSuccess(event)
                    emt.onComplete()
                }

                override fun onSubscribeError(event: SubscribeErrorEvent?) {
                    super.onSubscribeError(event)
                    emt.onError(SubscribeEventException(event!!.code,
                                                        "bitmark-changed#${event.message}"
                    )
                    )
                }

            })
    }

    fun unsubscribeBitmarkChanged(owner: String) = Completable.fromCallable {
        webSocketService.unsubscribeBitmarkChanged(
            Address.fromAccountNumber(owner)
        )
    }

    fun subscribeNewPendingTx(stakeholder: String) = Completable.create { emt ->
        webSocketService.subscribeNewPendingTx(
            Address.fromAccountNumber(
                stakeholder
            ), object : BitmarkWebSocket.NewPendingTxEvent() {
                override fun onNewPendingIx(
                        txId: String?,
                        owner: String?,
                        prevTxId: String?,
                        prevOwner: String?
                ) {
                    newPendingTxPublisher.publisher.onNext(
                        mapOf(
                            "tx_id" to txId!!,
                            "owner" to owner!!,
                            "prev_tx_id" to prevTxId!!,
                            "prev_owner" to prevOwner!!
                        )
                    )
                }

                override fun onSubscribeSuccess(event: SubscribeSuccessEvent?) {
                    super.onSubscribeSuccess(event)
                    emt.onComplete()
                }

                override fun onSubscribeError(event: SubscribeErrorEvent?) {
                    super.onSubscribeError(event)
                    emt.onError(SubscribeEventException(event!!.code,
                                                        "new-pending-tx#${event.message}"
                    )
                    )
                }

            })
    }

    fun unsubscribeNewPendingTx(stakeholder: String) = Completable.fromCallable {
        webSocketService.unsubscribeNewPendingTx(
            Address.fromAccountNumber(stakeholder)
        )
    }

    fun subscribeNewPendingIssuance(issuer: String
    ) = Completable.create { emt ->
        webSocketService.subscribeNewPendingIssuance(
            Address.fromAccountNumber(
                issuer
            ), object : BitmarkWebSocket.NewPendingIssuanceEvent() {
                override fun onNewPendingIssuance(bitmarkId: String?) {
                    newPendingIssuancePublisher.publisher.onNext(
                        bitmarkId ?: return
                    )
                }

                override fun onSubscribeSuccess(event: SubscribeSuccessEvent?) {
                    super.onSubscribeSuccess(event)
                    emt.onComplete()
                }

                override fun onSubscribeError(event: SubscribeErrorEvent?) {
                    super.onSubscribeError(event)
                    emt.onError(SubscribeEventException(event!!.code,
                                                        "new-pending-issuance#${event.message}"
                    )
                    )
                }

            })
    }

    fun unsubscribeNewPendingIssuance(issuer: String) = Completable.fromCallable {
        webSocketService.unsubscribeNewPendingIssuance(
            Address.fromAccountNumber(issuer)
        )
    }

    override fun onConnected() {
        handler.post {
            connectListener?.invoke(null)
        }
    }

    override fun onConnectionError(e: Throwable?) {
        handler.post {
            connectListener?.invoke(e)
        }
    }

    override fun onDisconnected() {
        handler.post {
            disconnectListener?.invoke()
        }
    }
}