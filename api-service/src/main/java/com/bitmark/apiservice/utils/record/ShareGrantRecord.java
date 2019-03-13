package com.bitmark.apiservice.utils.record;

import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.Validation;
import com.bitmark.cryptography.crypto.Sha3256;
import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Objects;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;

/**
 * @author Hieu Pham
 * @since 1/15/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class ShareGrantRecord implements Record, Validation {

    private String id;

    @SerializedName("share_id")
    private String shareId;

    private String from;

    private String to;

    private String status;

    private String txId;

    private Record record;

    @SerializedName("extra_info")
    private Map<String, Object> extraInfo;

    @SerializedName("created_at")
    private String createdAt;

    public String getId() {
        return id;
    }

    public String getShareId() {
        return shareId;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Record getRecord() {
        return record;
    }

    public Map<String, Object> getExtraInfo() {
        return extraInfo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public String getTxId() {
        return txId;
    }

    public int getQuantity() {
        return record != null ? record.quantity : -1;
    }

    public String getReceiver() {
        return record != null ? record.receiver : null;
    }

    public String getOwner() {
        return record != null ? record.owner : null;
    }

    public int getBeforeBlock() {
        return record != null ? record.beforeBlock : -1;
    }

    public String getSignature() {
        return record != null ? record.signature : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShareGrantRecord that = (ShareGrantRecord) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(shareId, that.shareId) &&
               Objects.equals(from, that.from) &&
               Objects.equals(to, that.to) &&
               Objects.equals(status, that.status) &&
               Objects.equals(txId, that.txId) &&
               Objects.equals(record, that.record) &&
               Objects.equals(extraInfo, that.extraInfo) &&
               Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shareId, from, to, status, txId, record, extraInfo, createdAt);
    }

    @Override
    public boolean isValid() {
        return id != null && !id.isEmpty() && shareId != null &&
               !shareId.isEmpty() &&
               HEX.decode(shareId).length == Sha3256.HASH_BYTE_LENGTH &&
               Address.isValidAccountNumber(from) && Address.isValidAccountNumber(to) &&
               record != null && record.isValid() && createdAt != null && !createdAt.isEmpty() &&
               status != null && !status.isEmpty();
    }

    public static class Record implements Validation {

        private String shareId;

        private int quantity;

        private String owner;

        @SerializedName("recipient")
        private String receiver;

        private int beforeBlock;

        private String signature;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Record record = (Record) o;
            return quantity == record.quantity &&
                   beforeBlock == record.beforeBlock &&
                   Objects.equals(shareId, record.shareId) &&
                   Objects.equals(owner, record.owner) &&
                   Objects.equals(receiver, record.receiver) &&
                   Objects.equals(signature, record.signature);
        }

        @Override
        public int hashCode() {
            return Objects.hash(shareId, quantity, owner, receiver, beforeBlock, signature);
        }

        @Override
        public boolean isValid() {
            return quantity >= 0 &&
                   owner != null &&
                   !owner.isEmpty() && Address.isValidAccountNumber(owner) && receiver != null &&
                   !receiver.isEmpty() && Address.isValidAccountNumber(receiver) &&
                   beforeBlock > 0 &&
                   signature != null && !signature.isEmpty();
        }
    }

}
