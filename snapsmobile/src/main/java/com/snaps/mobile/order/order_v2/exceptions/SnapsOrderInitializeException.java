package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderInitializeException extends SnapsOrderException {
    public SnapsOrderInitializeException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderInitializeException(String detailMessage) {
        super(detailMessage);
    }
}
