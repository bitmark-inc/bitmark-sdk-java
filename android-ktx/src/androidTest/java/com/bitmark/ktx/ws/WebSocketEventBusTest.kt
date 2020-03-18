/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.ktx.ws

import com.bitmark.ktx.BaseTest
import com.bitmark.ktx.util.ext.TemporaryFolderRule
import com.bitmark.apiservice.params.IssuanceParams
import com.bitmark.apiservice.params.RegistrationParams
import com.bitmark.apiservice.response.RegistrationResponse
import com.bitmark.apiservice.utils.Awaitility.await
import com.bitmark.apiservice.utils.Data
import com.bitmark.sdk.features.Account
import com.bitmark.sdk.features.Asset
import com.bitmark.sdk.features.Bitmark
import io.reactivex.Completable
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean

class WebSocketEventBusTest : BaseTest() {

    @Rule
    @JvmField
    val temporaryFolderRule = TemporaryFolderRule()

    @Test
    fun testSubscribeMultiEventParallel() {
        val account = Account()
        val accountNumber = account.accountNumber
        val ws = WebSocketEventBus()
        val done = AtomicBoolean(false)
        val passed = AtomicBoolean(false)
        val capturedThrowable = Data<Throwable>()
        ws.setConnectListener { throwable ->
            if (throwable == null) {
                Completable.mergeArray(ws.subscribeNewPendingIssuance(accountNumber),
                                       ws.subscribeNewPendingTx(accountNumber),
                                       ws.subscribeBitmarkChanged(accountNumber),
                                       ws.subscribeNewBlock(),
                                       ws.subscribeNewTransferOffer(accountNumber)
                ).doAfterTerminate {
                    done.set(true)
                }.subscribe({
                                passed.set(true)
                            }, { e ->
                                capturedThrowable.value = e
                                passed.set(false)
                            })
            }
            else {
                capturedThrowable.value = throwable
                passed.set(false)
                done.set(true)
            }

        }
        ws.connect(account.authKeyPair)
        while (!done.get()) {
            Thread.sleep(100)
        }

        ws.disconnect()
        assertTrue(capturedThrowable.value?.message, passed.get())
    }

    @Test
    fun testUnsubscribeMultiEventParallel() {
        val account = Account()
        val accountNumber = account.accountNumber
        val ws = WebSocketEventBus()
        val done = AtomicBoolean(false)
        val passed = AtomicBoolean(false)
        val capturedThrowable = Data<Throwable>()
        ws.setConnectListener { throwable ->
            if (throwable == null) {
                Completable.mergeArray(ws.subscribeBitmarkChanged(accountNumber),
                                       ws.subscribeNewBlock(),
                                       ws.subscribeNewPendingIssuance(accountNumber)
                ).doAfterTerminate {
                    done.set(true)
                }.subscribe({}, { e ->
                    capturedThrowable.value = e
                })
            }
            else {
                capturedThrowable.value = throwable
                done.set(true)
            }

        }
        ws.connect(account.authKeyPair)
        while (!done.get()) {
            Thread.sleep(100)
        }

        done.set(false)
        assertNull(capturedThrowable.value)
        Completable.mergeArray(ws.unsubscribeBitmarkChanged(accountNumber),
                               ws.unsubscribeNewBlock(),
                               ws.unsubscribeNewPendingIssuance(accountNumber)
        ).doAfterTerminate {
            done.set(true)
        }.subscribe({
                        passed.set(true)
                    }, { e ->
                        capturedThrowable.value = e
                        passed.set(false)
                    })

        while (!done.get()) {
            Thread.sleep(100)
        }
        ws.disconnect()
        assertTrue(capturedThrowable.value?.message, passed.get())
    }

    @Test
    fun testWebSocketEvent() {
        val account = Account()
        val ws = WebSocketEventBus()
        val done = AtomicBoolean(false)
        val capturedThrowable = Data<Throwable>()

        // connect socket and start subscribe event
        ws.setConnectListener { throwable ->
            if (throwable == null) {
                ws.subscribeNewPendingIssuance(account.accountNumber).subscribe()
            }
            else {
                capturedThrowable.value = throwable
            }
            done.set(true)
        }
        ws.connect(account.authKeyPair)

        while (!done.get()) {
            Thread.sleep(100)
        }

        done.set(false)
        assertTrue(capturedThrowable.value?.message, capturedThrowable.value == null)

        // listen event value trigger
        var actualBitmarkId = ""
        ws.newPendingIssuancePublisher.subscribe(this) { id ->
            actualBitmarkId = id
            done.set(true)
        }

        // register asset
        val asset = temporaryFolderRule.newFile()
        val owner = account.toAddress()
        val metadata = mapOf("description" to "test from android ktx")
        val registrationParams = RegistrationParams(asset.name, metadata)
        registrationParams.setFingerprintFromFile(asset)
        registrationParams.sign(account.authKeyPair)
        val registrationResponse =
            await<RegistrationResponse> { callback -> Asset.register(registrationParams, callback) }
        val assetId = registrationResponse.assets[0].id

        // issue bitmark
        val issuanceParams = IssuanceParams(assetId, owner)
        issuanceParams.sign(account.authKeyPair)
        val bitmarkIds = await<List<String>> { callback -> Bitmark.issue(issuanceParams, callback) }
        assertFalse(bitmarkIds.isEmpty())
        val expectedBitmarkId = bitmarkIds[0]

        // wait until receiving socket event or timeout
        while (!done.get()) {
            Thread.sleep(100)
        }

        ws.disconnect()
        assertEquals(expectedBitmarkId, actualBitmarkId)
    }

}