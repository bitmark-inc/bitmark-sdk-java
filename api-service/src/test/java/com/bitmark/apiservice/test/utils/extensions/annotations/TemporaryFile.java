package com.bitmark.apiservice.test.utils.extensions.annotations;

import java.lang.annotation.*;

/**
 * @author Hieu Pham
 * @since 3/21/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface TemporaryFile {
    String value();
}
