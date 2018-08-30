package params.query;

/**
 * @author Hieu Pham
 * @since 8/30/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class QueryParams {

    private QueryBuilder builder;

    QueryParams(QueryBuilder builder) {
        this.builder = builder;
    }

    String toUrlQuery() {
        throw new UnsupportedOperationException("Unsupport right now");
    }
}
