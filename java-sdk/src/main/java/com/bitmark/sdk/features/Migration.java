package com.bitmark.sdk.features;

import com.bitmark.apiservice.params.IssuanceParams;
import com.bitmark.apiservice.params.query.BitmarkQueryBuilder;
import com.bitmark.apiservice.response.GetBitmarksResponse;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.callback.Callable1;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.apiservice.utils.record.BitmarkRecord;
import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.cryptography.error.ValidateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.bitmark.apiservice.utils.Awaitility.await;
import static com.bitmark.cryptography.utils.Validator.checkValid;
import static com.bitmark.sdk.utils.Version.TWENTY_FOUR;

/**
 * @author Hieu Pham
 * @since 11/5/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Migration {

    public static void migrate(String[] phraseWords,
                               Callback1<Pair<Account, List<String>>> callback) throws ValidateException {
        checkValid(() -> phraseWords != null && phraseWords.length == TWENTY_FOUR.getMnemonicWordsLength(), "Only support migrate from 24 recovery words ");
        final Account oldAccount = Account.fromRecoveryPhrase(phraseWords);
        final String oldAccountNumber = oldAccount.getAccountNumber();
        final Account newAccount = new Account();

        try {
            // Get all owned bitmarks
            final List<GetBitmarksResponse> bitmarksResponses =
                    getBitmarksResponses(oldAccountNumber);
            if (bitmarksResponses == null)
                callback.onSuccess(new Pair<>(newAccount, Collections.emptyList()));
            else {
                final List<String> bitmarkIds = new ArrayList<>();

                CompletableFuture.allOf(bitmarksResponses.stream().map(response -> {
                    List<BitmarkRecord> bitmarks = response.getBitmarks();
                    List<AssetRecord> assets = response.getAssets();
                    return internalMigrate(newAccount, bitmarks, assets).whenComplete((result,
                                                                                       throwable) -> {
                        if (throwable == null) bitmarkIds.addAll(result);

                    });
                }).toArray(CompletableFuture[]::new)).whenComplete((ignore, throwable) -> {
                    if (throwable != null) {
                        callback.onError(throwable.getCause());
                    } else {
                        callback.onSuccess(new Pair<>(newAccount, bitmarkIds));
                    }
                });
            }

        } catch (Throwable throwable) {
            // Error when collect all owned bitmark
            callback.onError(throwable);
        }
    }

    private static List<GetBitmarksResponse> getBitmarksResponses(String accountNumber) throws Throwable {
        final int limit = 100;
        final List<GetBitmarksResponse> result = new ArrayList<>();

        // Get the latest bitmark by offset
        List<BitmarkRecord> firstBitmarks =
                await((Callable1<GetBitmarksResponse>) internalCallback ->
                        Bitmark.list(new BitmarkQueryBuilder().ownedBy(accountNumber).pending(true).limit(1),
                                internalCallback)).getBitmarks();
        if (firstBitmarks == null || firstBitmarks.isEmpty()) return null;
        Long lastOffset = firstBitmarks.get(0).getOffset();
        while (lastOffset != null) {

            BitmarkQueryBuilder builder =
                    new BitmarkQueryBuilder().ownedBy(accountNumber).at(lastOffset).to("earlier").loadAsset(true).pending(true).limit(limit);
            GetBitmarksResponse response = await(internalCallback -> Bitmark.list(builder,
                    internalCallback));
            result.add(response);
            final List<BitmarkRecord> bitmarks = response.getBitmarks();
            final int size = bitmarks == null ? 0 : bitmarks.size();
            if (size == limit) {
                // Continue finding out more bitmarks by querying from the offset of last bitmark
                lastOffset = bitmarks.get(bitmarks.size() - 1).getOffset();
            } else lastOffset = null; // No more bitmarks will be found
        }
        return result;
    }

    private static CompletableFuture<List<String>> internalMigrate(Account owner,
                                                                   List<BitmarkRecord> bitmarks,
                                                                   List<AssetRecord> assets) {

        final Address ownerAddress = owner.toAddress();
        final KeyPair key = owner.getKey();

        // Build collection of IssuanceParams
        List<IssuanceParams> params = new ArrayList<>();
        assets.forEach(asset -> {
            String assetId = asset.getId();
            int quantity =
                    (int) bitmarks.stream().filter(bitmark -> bitmark.getAssetId().equals(assetId)).count();
            IssuanceParams param = new IssuanceParams(assetId, ownerAddress, quantity);
            param.sign(key);
            params.add(param);
        });

        // Execute from the above collection
        final List<String> bitmarkIds = new ArrayList<>();
        final CompletableFuture<List<String>> emitters = new CompletableFuture<>();
        CompletableFuture.allOf(params.stream().map(param -> issue(param).whenComplete((result,
                                                                                        throwable) -> {
            if (throwable == null) bitmarkIds.addAll(result);
        })).toArray(CompletableFuture[]::new)).whenComplete((result, throwable) -> {
            if (throwable != null) emitters.completeExceptionally(throwable.getCause());
            else emitters.complete(bitmarkIds);
        });

        return emitters;
    }

    private static CompletableFuture<List<String>> issue(IssuanceParams params) {
        final CompletableFuture<List<String>> emitter = new CompletableFuture<>();
        Bitmark.issue(params, new Callback1<List<String>>() {
            @Override
            public void onSuccess(List<String> data) {
                emitter.complete(data);
            }

            @Override
            public void onError(Throwable throwable) {
                emitter.completeExceptionally(throwable);
            }
        });
        return emitter;
    }
}
