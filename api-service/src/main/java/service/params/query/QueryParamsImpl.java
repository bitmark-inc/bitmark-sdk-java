package service.params.query;

/**
 * @author Hieu Pham
 * @since 8/30/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class QueryParamsImpl implements QueryParams {

    private QueryBuilder builder;

    QueryParamsImpl(QueryBuilder builder) {
        this.builder = builder;
    }

    @Override
    public String toUrlQuery() {
        throw new UnsupportedOperationException("Unsupport right now");
    }
}
