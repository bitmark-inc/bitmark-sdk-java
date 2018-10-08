package apiservice.utils.callback;

/**
 * @author Hieu Pham
 * @since 9/25/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface Callable1<T> extends Callable {

    void call(Callback1<T> callback);
}
