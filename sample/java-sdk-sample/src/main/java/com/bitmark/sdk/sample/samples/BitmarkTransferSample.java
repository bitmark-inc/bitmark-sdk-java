/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.sample.samples;

import com.bitmark.apiservice.params.TransferOfferParams;
import com.bitmark.apiservice.params.TransferParams;
import com.bitmark.apiservice.params.TransferResponseParams;
import com.bitmark.apiservice.response.GetBitmarkResponse;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.record.OfferRecord;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.Bitmark;

import static com.bitmark.apiservice.utils.Awaitility.await;
import static com.bitmark.sdk.sample.samples.QuerySample.queryBitmarkById;

public class BitmarkTransferSample {
    public static String transferOneSignature(Account sender, String bitmarkId, String receiverAccountNumber) throws Throwable {
        GetBitmarkResponse bitmarkResponse = queryBitmarkById(bitmarkId);

        TransferParams params = new TransferParams(Address.fromAccountNumber(receiverAccountNumber));
        String link =  bitmarkResponse.getBitmark().getHeadId();
        params.setLink(link);
        params.sign(sender.getAuthKeyPair());
        String txId = await(callback -> Bitmark.transfer(params, callback));

        return txId;
    }

    public static String sendTransferOffer(Account sender, String bitmarkId, String receiverAccountNumber) throws Throwable {
        GetBitmarkResponse bitmarkResponse = queryBitmarkById(bitmarkId);

        TransferOfferParams params = new TransferOfferParams(Address.fromAccountNumber(receiverAccountNumber));
        String link = bitmarkResponse.getBitmark().getHeadId();
        params.setLink(link);
        params.sign(sender.getAuthKeyPair());
        String offerId = await(callback -> Bitmark.offer(params, callback));

        return offerId;
    }

    public static String acceptTransferOffer(Account receiver, String bitmarkId) throws Throwable {
        GetBitmarkResponse bitmarkResponse = queryBitmarkById(bitmarkId);
        OfferRecord offerRecord = bitmarkResponse.getBitmark().getOffer();

        TransferResponseParams responseParams = TransferResponseParams.accept(offerRecord);
        responseParams.sign(receiver.getAuthKeyPair());
        String txId = await(callback -> Bitmark.respond(responseParams, callback));

        return txId;
    }

    public static String rejectTransferOffer(Account receiver, String bitmarkId) throws Throwable {
        GetBitmarkResponse bitmarkResponse = queryBitmarkById(bitmarkId);
        OfferRecord offerRecord = bitmarkResponse.getBitmark().getOffer();

        TransferResponseParams responseParams = TransferResponseParams.reject(offerRecord);
        responseParams.setSigningKey(receiver.getAuthKeyPair());
        String txId = await(callback -> Bitmark.respond(responseParams, callback));

        return txId;
    }

    public static String cancelTransferOffer(Account sender, String bitmarkId) throws Throwable {
        GetBitmarkResponse bitmarkResponse = queryBitmarkById(bitmarkId);
        OfferRecord offerRecord = bitmarkResponse.getBitmark().getOffer();

        TransferResponseParams responseParams = TransferResponseParams.cancel(offerRecord, bitmarkResponse.getBitmark().getOwner());
        responseParams.setSigningKey(sender.getAuthKeyPair());
        String txId = await(callback -> Bitmark.respond(responseParams, callback));

        return txId;
    }
}
