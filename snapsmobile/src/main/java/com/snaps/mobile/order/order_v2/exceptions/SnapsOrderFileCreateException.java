package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderFileCreateException extends SnapsOrderException {
    public SnapsOrderFileCreateException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderFileCreateException(String detailMessage) {
        super(detailMessage);
    }
}
