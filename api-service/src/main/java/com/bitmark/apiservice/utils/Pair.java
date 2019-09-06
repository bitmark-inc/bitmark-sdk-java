package com.bitmark.apiservice.utils;

import java.util.Objects;

/**
 * @author Hieu Pham
 * @since 10/19/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Pair<T1, T2> {

    private T1 first;

    private T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public T1 first() {
        return first;
    }

    public T2 second() {
        return second;
    }

    @Override
    public String toString() {
        return first + "=" + second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
