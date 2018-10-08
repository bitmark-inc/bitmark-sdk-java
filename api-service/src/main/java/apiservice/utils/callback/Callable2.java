package apiservice.utils.callback;

/**
 * @author Hieu Pham
 * @since 9/25/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface Callable2<T1, T2> extends Callable {

    void call(Callback2<T1, T2> callback);
}
