package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderNetworkException extends SnapsOrderException {
    public SnapsOrderNetworkException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderNetworkException(String detailMessage) {
        super(detailMessage);
    }
}
