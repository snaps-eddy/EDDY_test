package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderUnknownHostException extends SnapsOrderException {
    public SnapsOrderUnknownHostException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderUnknownHostException(String detailMessage) {
        super(detailMessage);
    }
}
