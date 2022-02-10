package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderGetProjectCodeException extends SnapsOrderException {
    public SnapsOrderGetProjectCodeException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderGetProjectCodeException(String detailMessage) {
        super(detailMessage);
    }
}
