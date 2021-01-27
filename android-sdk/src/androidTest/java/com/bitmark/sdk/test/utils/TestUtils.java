package com.bitmark.sdk.test.utils;

public class TestUtils {
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T assertThrows(String msg,
                                                       Class<T> expectedError,
                                                       Callable callable) {
        try {
            callable.call();
            throw new AssertionError(msg);
        } catch (Throwable e) {
            if (expectedError.isAssignableFrom(e.getClass()))
                return (T) e;
            throw e;
        }
    }

    public static <T> T assertDoesNotThrow(Callable1<T> callable) {
        try {
            return callable.call();
        } catch (Throwable e) {
            throw new AssertionError(e.getMessage());
        }
    }
}
