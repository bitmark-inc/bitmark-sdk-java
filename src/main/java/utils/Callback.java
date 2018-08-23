package utils;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public interface Callback {

    void onSuccess();

    void onError(Throwable throwable);
}
