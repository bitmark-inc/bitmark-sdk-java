package com.bitmark.androidsdksample.samples;

import com.bitmark.apiservice.params.TransferOfferParams;
import com.bitmark.apiservice.params.TransferParams;
import com.bitmark.apiservice.params.TransferResponseParams;
import com.bitmark.apiservice.response.GetBitmarkResponse;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.record.OfferRecord;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.Bitmark;

import io.reactivex.Single;

import static com.bitmark.androidsdksample.samples.QuerySample.queryBitmarkById;

public class BitmarkTransferSample {
    public static Single<String> transferOneSignature(Account sender, String bitmarkId, String receiverAccountNumber) {
        Single<GetBitmarkResponse> bitmarkResponseSingle = queryBitmarkById(bitmarkId);

        Single<String> transferSingle = bitmarkResponseSingle.flatMap(bitmarkResponse -> {
            TransferParams params = new TransferParams(Address.fromAccountNumber(receiverAccountNumber));
            String link = bitmarkResponse.getBitmark().getHeadId();
            params.setLink(link);
            params.sign(sender.getKeyPair());

            return executeTransfer(params);
        });

        return transferSingle;
    }

    public static Single<String> sendTransferOffer(Account sender, String bitmarkId, String receiverAccountNumber) {
        Single<GetBitmarkResponse> bitmarkResponseSingle = queryBitmarkById(bitmarkId);

        Single<String> offerSingle = bitmarkResponseSingle.flatMap(bitmarkResponse -> {
            TransferOfferParams params = new TransferOfferParams(Address.fromAccountNumber(receiverAccountNumber));
            String link = bitmarkResponse.getBitmark().getHeadId();
            params.setLink(link);
            params.sign(sender.getKeyPair());

            return executeOffer(params);
        });

        return offerSingle;
    }

    public static Single<String> acceptTransferOffer(Account receiver, String bitmarkId) {
        Single<GetBitmarkResponse> bitmarkResponseSingle = queryBitmarkById(bitmarkId);

        Single<String> responseSingle = bitmarkResponseSingle.flatMap(bitmarkResponse -> {
            OfferRecord offerRecord = bitmarkResponse.getBitmark().getOffer();

            TransferResponseParams responseParams = TransferResponseParams.accept(offerRecord);
            responseParams.sign(receiver.getKeyPair());

            return executeResponse(responseParams);
        });

        return responseSingle;
    }

    public static Single<String> rejectTransferOffer(Account receiver, String bitmarkId) {
        Single<GetBitmarkResponse> bitmarkResponseSingle = queryBitmarkById(bitmarkId);

        Single<String> responseSingle = bitmarkResponseSingle.flatMap(bitmarkResponse -> {
            OfferRecord offerRecord = bitmarkResponse.getBitmark().getOffer();

            TransferResponseParams responseParams = TransferResponseParams.reject(offerRecord);
            responseParams.setSigningKey(receiver.getKeyPair());

            return executeResponse(responseParams);
        });

        return responseSingle;
    }

    public static Single<String> cancelTransferOffer(Account sender, String bitmarkId) {
        Single<GetBitmarkResponse> bitmarkResponseSingle = queryBitmarkById(bitmarkId);

        Single<String> responseSingle = bitmarkResponseSingle.flatMap(bitmarkResponse -> {
            OfferRecord offerRecord = bitmarkResponse.getBitmark().getOffer();

            TransferResponseParams responseParams = TransferResponseParams.cancel(offerRecord, bitmarkResponse.getBitmark().getOwner());
            responseParams.setSigningKey(sender.getKeyPair());

            return executeResponse(responseParams);
        });

        return responseSingle;
    }

    public static Single<String> executeTransfer(TransferParams params) {
        Single<String> single = Single.create(singleSubscriber -> {
            Bitmark.transfer(params, new Callback1<String>() {
                @Override
                public void onSuccess(String data) {
                    singleSubscriber.onSuccess(data);
                }

                @Override
                public void onError(Throwable throwable) {
                    singleSubscriber.onError(throwable);
                }
            });
        });

        return single;
    }

    public static Single<String> executeOffer(TransferOfferParams params) {
        Single<String> single = Single.create(singleSubscriber -> {
            Bitmark.offer(params, new Callback1<String>() {
                @Override
                public void onSuccess(String data) {
                    singleSubscriber.onSuccess(data);
                }

                @Override
                public void onError(Throwable throwable) {
                    singleSubscriber.onError(throwable);
                }
            });
        });

        return single;
    }

    public static Single<String> executeResponse(TransferResponseParams params) {
        Single<String> single = Single.create(singleSubscriber -> {
            Bitmark.respond(params, new Callback1<String>() {
                @Override
                public void onSuccess(String data) {
                    singleSubscriber.onSuccess(data);
                }

                @Override
                public void onError(Throwable throwable) {
                    singleSubscriber.onError(throwable);
                }
            });
        });

        return single;
    }
}
