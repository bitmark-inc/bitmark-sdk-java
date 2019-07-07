package com.bitmark.apiservice;

import com.bitmark.apiservice.utils.Address;

/**
 * @author Hieu Pham
 * @since 7/6/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public interface BitmarkWebSocket extends WebSocket {

    void subscribeNewBlock(NewBlockEvent event);

    void unsubscribeNewBlock();

    void subscribeBitmarkChanged(Address owner, BitmarkChangedEvent event);

    void unsubscribeBitmarkChanged(Address owner);

    void subscribeTransferOffer(Address requester, TransferOfferEvent event);

    void unsubscribeTransferOffer(Address requester);

    void subscribeNewPendingIssuance(Address owner, NewPendingIssuanceEvent event);

    void unsubscribeNewPendingIssuance(Address owner);

    void subscribeNewPendingTx(Address stakeHolder, NewPendingTxEvent event);

    void unsubscribeNewPendingTx(Address stakeHolder);

    abstract class NewBlockEvent extends SubscribeEvent {

        public abstract void onNewBlock(long blockNumber);
    }

    abstract class BitmarkChangedEvent extends SubscribeEvent {

        public abstract void onChanged(String bitmarkId, String txId, boolean presence);
    }

    abstract class TransferOfferEvent extends SubscribeEvent {

        public abstract void onReceived(String bitmarkId);
    }

    abstract class NewPendingIssuanceEvent extends SubscribeEvent {

        public abstract void onNewPendingIssuance(String bitmarkId);
    }

    abstract class NewPendingTxEvent extends SubscribeEvent {

        public abstract void onNewPendingIx(String txId, String owner, String prevTxId,
                                            String prevOwner);
    }

}
