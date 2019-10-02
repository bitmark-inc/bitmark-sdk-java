package com.bitmark.sdk.features;

import com.bitmark.apiservice.params.TransferOfferParams;
import com.bitmark.apiservice.params.TransferResponseParams;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.response.GetBitmarkResponse;
import com.bitmark.apiservice.response.GetBitmarksResponse;
import com.bitmark.apiservice.utils.Awaitility;
import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.record.BitmarkRecord;
import com.bitmark.apiservice.utils.record.OfferRecord;
import java8.util.concurrent.CompletionException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.bitmark.apiservice.utils.Awaitility.await;
import static com.bitmark.cryptography.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 11/5/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

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
        getOwningBitmarks(from).thenCompose(bitmarks -> offer(
                bitmarks,
                from,
                to
        )).thenCompose(offers -> respond(offers, to)).whenComplete(
                (txIds, throwable) -> {
                    if (throwable != null) {
                        callback.onError(throwable);
                    } else {
                        callback.onSuccess(txIds);
                    }
                });
    }

    private static CompletableFuture<List<String>> respond(
            List<OfferRecord> offers,
            Account receiver
    ) {
        if (offers.isEmpty()) {
            return CompletableFuture.supplyAsync(ArrayList::new);
        }

        return CompletableFuture.supplyAsync(() -> offers.stream()
                .map(offer -> {
                    TransferResponseParams param = TransferResponseParams.accept(
                            offer);
                    param.sign(receiver.getAuthKeyPair());
                    return param;
                })
                .map(param -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return Awaitility.<String>await(callback -> Bitmark.respond(
                                param,
                                callback
                        ));
                    } catch (Throwable e) {
                        throw new CompletionException(e);
                    }
                }))
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));
    }

    private static CompletableFuture<List<OfferRecord>> offer(
            List<BitmarkRecord> bitmarks,
            Account sender,
            Account receiver
    ) {
        if (bitmarks.isEmpty()) {
            return CompletableFuture.supplyAsync(ArrayList::new);
        }

        return CompletableFuture.supplyAsync(() -> {
            List<Pair<String, TransferOfferParams>> params = bitmarks.stream()
                    .map(bm -> {
                        TransferOfferParams param = new TransferOfferParams(
                                receiver.toAddress(),
                                bm.getHeadId()
                        );
                        param.sign(sender.getAuthKeyPair());
                        return new Pair<>(bm.getId(), param);
                    })
                    .collect(Collectors.toList());

            return params.stream()
                    .map(p -> CompletableFuture.supplyAsync(
                            () -> {
                                try {
                                    String bitmarkId = p.first();
                                    TransferOfferParams param = p.second();
                                    Awaitility.<String>await(callback -> Bitmark
                                            .offer(param, callback));
                                    GetBitmarkResponse res = await(callback -> Bitmark
                                            .get(bitmarkId, callback));
                                    return res.getBitmark().getOffer();
                                } catch (Throwable e) {
                                    throw new CompletionException(e);
                                }
                            }))
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
        });
    }

    private static CompletableFuture<List<BitmarkRecord>> getOwningBitmarks(
            Account owner
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
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
            } catch (Throwable e) {
                throw new CompletionException(e);
            }
        });
    }
}
