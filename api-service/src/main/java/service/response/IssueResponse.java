package service.response;

import annotation.VisibleForTesting;

import java.util.List;
import java.util.Objects;

/**
 * @author Hieu Pham
 * @since 9/14/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class IssueResponse implements Response {

    private List<Bitmark> bitmarks;

    @VisibleForTesting
    public IssueResponse(List<Bitmark> bitmarks) {
        this.bitmarks = bitmarks;
    }

    public List<Bitmark> getBitmarks() {
        return bitmarks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssueResponse that = (IssueResponse) o;
        return Objects.equals(bitmarks, that.bitmarks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitmarks);
    }

    public static final class Bitmark {

        String id;

        @VisibleForTesting
        public Bitmark(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Bitmark bitmark = (Bitmark) o;
            return Objects.equals(id, bitmark.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
