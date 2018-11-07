package sdk.features;

import apiservice.params.IssuanceParams;
import apiservice.params.query.BitmarkQueryBuilder;
import apiservice.response.GetBitmarksResponse;
import apiservice.utils.Address;
import apiservice.utils.Pair;
import apiservice.utils.callback.Callable1;
import apiservice.utils.callback.Callback1;
import apiservice.utils.record.AssetRecord;
import apiservice.utils.record.BitmarkRecord;
import cryptography.crypto.key.KeyPair;
import cryptography.error.ValidateException;
import io.reactivex.Single;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static apiservice.utils.Awaitility.await;
import static cryptography.utils.Validator.checkValid;
import static sdk.utils.Version.TWENTY_FOUR;

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
                final List<Single<List<String>>> sources = new ArrayList<>();

                for (GetBitmarksResponse response : bitmarksResponses) {
                    List<BitmarkRecord> bitmarks = response.getBitmarks();
                    List<AssetRecord> assets = response.getAssets();
                    sources.add(internalMigrate(newAccount, bitmarks, assets).doOnSuccess(bitmarkIds::addAll));
                }

                Single.merge(sources).toList().map(ignore -> bitmarkIds).subscribe((result,
                                                                                    throwable) -> {
                    if (throwable == null) callback.onSuccess(new Pair<>(newAccount, result));
                    else callback.onError(throwable);
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

    private static Single<List<String>> internalMigrate(Account owner,
                                                        List<BitmarkRecord> bitmarks,
                                                        List<AssetRecord> assets) {

        final Address ownerAddress = owner.toAddress();
        final KeyPair key = owner.getKey();

        // Build collection of IssuanceParams
        List<IssuanceParams> params = new ArrayList<>();
        for (AssetRecord asset : assets) {
            String assetId = asset.getId();
            int quantity = 0;

            for (BitmarkRecord bitmark : bitmarks) {
                if (bitmark.getAssetId().equals(assetId)) quantity++;
            }
            IssuanceParams param = new IssuanceParams(assetId, ownerAddress, quantity);
            param.sign(key);
            params.add(param);
        }

        // Execute from the above collection
        final List<String> bitmarkIds = new ArrayList<>();
        List<Single<List<String>>> sources = new ArrayList<>();
        for (IssuanceParams param : params) {
            sources.add(issue(param).doOnSuccess(bitmarkIds::addAll));
        }

        return Single.merge(sources).toList().map(ignore -> bitmarkIds);
    }

    private static Single<List<String>> issue(IssuanceParams params) {
        return Single.create(emitter -> Bitmark.issue(params, new Callback1<List<String>>() {
            @Override
            public void onSuccess(List<String> data) {
                emitter.onSuccess(data);
            }

            @Override
            public void onError(Throwable throwable) {
                emitter.onError(throwable);
            }
        }));
    }
}
