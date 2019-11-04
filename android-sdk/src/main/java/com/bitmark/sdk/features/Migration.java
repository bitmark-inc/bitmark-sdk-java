/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.features;

import com.bitmark.apiservice.params.TransferOfferParams;
import com.bitmark.apiservice.params.TransferResponseParams;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.response.GetBitmarkResponse;
import com.bitmark.apiservice.response.GetBitmarksResponse;
import com.bitmark.apiservice.utils.Awaitility;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.record.BitmarkRecord;
import com.bitmark.apiservice.utils.record.OfferRecord;
import com.bitmark.sdk.utils.AwaitilityExecutorService;
import com.bitmark.sdk.utils.Task;

import java.util.ArrayList;
import java.util.List;

import static com.bitmark.apiservice.utils.Awaitility.await;
import static com.bitmark.cryptography.utils.Validator.checkValid;

public class Migration {

    public static void rekey(
            Account from,
            Account to,
            Callback1<List<String>> callback
    ) {
        checkValid(() -> from != null && to != null, "account is null");
        checkValid(
                () -> !from.getAccountNumber().equals(to.getAccountNumber()),
                "cannot use same account for rekey"
        );

        try {
            List<BitmarkRecord> bitmarks = getOwningBitmarks(from);
            List<OfferRecord> offer = offer(bitmarks, from, to);
            callback.onSuccess(respond(offer, to));
        } catch (Throwable e) {
            callback.onError(e);
        }
    }

    private static List<String> respond(
            List<OfferRecord> offers,
            Account receiver
    ) throws InterruptedException {
        if (offers.isEmpty()) {
            return new ArrayList<>();
        }

        final AwaitilityExecutorService executor = new AwaitilityExecutorService(
                5);
        List<Task<OfferRecord, String>> tasks = new ArrayList<>();

        for (OfferRecord offer : offers) {
            Task<OfferRecord, String> task = new Task<OfferRecord, String>(offer) {
                @Override
                public String run(OfferRecord... params) throws Throwable {
                    TransferResponseParams param = TransferResponseParams.accept(
                            params[0]);
                    param.sign(receiver.getAuthKeyPair());
                    return await(callback -> Bitmark.respond(
                            param,
                            callback
                    ));
                }
            };

            tasks.add(task);
        }

        return executor.execute(tasks, 120, true);
    }

    private static List<OfferRecord> offer(
            List<BitmarkRecord> bitmarks,
            Account sender,
            Account receiver
    ) throws InterruptedException {
        if (bitmarks.isEmpty()) {
            return new ArrayList<>();
        }

        final AwaitilityExecutorService executor = new AwaitilityExecutorService(
                5);
        List<Task<String, OfferRecord>> tasks = new ArrayList<>();
        for (BitmarkRecord bm : bitmarks) {
            Task<String, OfferRecord> task = new Task<String, OfferRecord>(bm.getId()) {
                @Override
                public OfferRecord run(String... params) throws Throwable {
                    TransferOfferParams param = new TransferOfferParams(
                            receiver.toAddress(),
                            bm.getHeadId()
                    );
                    param.sign(sender.getAuthKeyPair());
                    Awaitility.<String>await(callback -> Bitmark
                            .offer(param, callback));
                    GetBitmarkResponse res = await(callback -> Bitmark
                            .get(params[0], callback));
                    return res.getBitmark().getOffer();
                }
            };
            tasks.add(task);
        }

        return executor.execute(tasks, 120, true);
    }

    private static List<BitmarkRecord> getOwningBitmarks(Account owner)
            throws InterruptedException {
        final AwaitilityExecutorService executor = new AwaitilityExecutorService(
                1);
        return executor.execute(new Task<Void, List<BitmarkRecord>>() {
            @Override
            public List<BitmarkRecord> run(Void... params) throws Throwable {
                final List<BitmarkRecord> bitmarks = new ArrayList<>();
                boolean existing = true;
                Long offset = null;
                while (existing) {
                    BitmarkQueryBuilder builder = new BitmarkQueryBuilder().ownedBy(
                            owner.getAccountNumber())
                            .to("earlier")
                            .limit(100)
                            .pending(false);
                    if (offset != null) {
                        builder.at(offset);
                    }
                    GetBitmarksResponse res = await(callback -> Bitmark.list(
                            builder,
                            callback
                    ));
                    List<BitmarkRecord> bms = res.getBitmarks();
                    existing = bms.size() == 100;
                    if (existing) {
                        offset = bms.get(bms.size() - 1).getOffset();
                    }
                    bitmarks.addAll(bms);
                }
                return bitmarks;
            }
        }, 120, true);
    }
}
