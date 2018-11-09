package com.bitmark.apiservice.utils.record;

import com.bitmark.apiservice.utils.Address;
import com.bitmark.cryptography.crypto.Sha3256;
import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Objects;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.utils.Validator.checkValid;
import static com.bitmark.cryptography.utils.Validator.checkValidHex;

/**
 * @author Hieu Pham
 * @since 9/17/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class OfferRecord implements Record {

    private String id;

    private String from;

    private String to;

    private Record record;

    @SerializedName("extra_info")
    private Map<String, Object> extraInfo;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("open")
    private boolean isOpen;

    public OfferRecord(String id, String owner, String link, String signature) {
        checkValidHex(signature);
        checkValid(() -> id != null && !id.isEmpty() && owner != null && Address.fromAccountNumber(owner).isValid() && link != null && HEX.decode(link).length == Sha3256.HASH_LENGTH, "Invalid params");
        this.id = id;
        record = new Record();
        record.link = link;
        record.signature = signature;
        record.owner = owner;
    }

    public String getId() {
        return id;
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

    public boolean isOpen() {
        return isOpen;
    }

    public String getLink() {
        return record == null ? null : record.link;
    }

    public String getSignature() {
        return record == null ? null : record.signature;
    }

    public String getOwner() {
        return record != null && record.owner != null ? record.owner : to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfferRecord offer = (OfferRecord) o;
        return isOpen == offer.isOpen &&
                Objects.equals(id, offer.id) &&
                Objects.equals(from, offer.from) &&
                Objects.equals(to, offer.to) &&
                Objects.equals(record, offer.record) &&
                Objects.equals(extraInfo, offer.extraInfo) &&
                Objects.equals(createdAt, offer.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, record, extraInfo, createdAt, isOpen);
    }

    public static final class Record {

        String link;

        String owner;

        String signature;

        public String getLink() {
            return link;
        }

        public String getOwner() {
            return owner;
        }

        public String getSignature() {
            return signature;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Record record = (Record) o;
            return Objects.equals(link, record.link) &&
                    Objects.equals(owner, record.owner) &&
                    Objects.equals(signature, record.signature);
        }

        @Override
        public int hashCode() {
            return Objects.hash(link, owner, signature);
        }
    }
}
