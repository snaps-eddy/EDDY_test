package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsIOException extends SnapsOrderException {
    public SnapsIOException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsIOException(String message) {
        super(message);
    }
}
