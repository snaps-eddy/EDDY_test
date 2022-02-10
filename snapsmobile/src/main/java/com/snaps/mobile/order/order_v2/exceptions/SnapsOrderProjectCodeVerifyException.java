package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderProjectCodeVerifyException extends SnapsOrderException {
    public SnapsOrderProjectCodeVerifyException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderProjectCodeVerifyException(String detailMessage) {
        super(detailMessage);
    }
}
