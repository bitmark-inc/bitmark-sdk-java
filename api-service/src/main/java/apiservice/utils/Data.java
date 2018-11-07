package apiservice.utils;

/**
 * @author Hieu Pham
 * @since 11/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Data<T> {

    private volatile T value;

    public synchronized void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
