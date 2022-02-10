package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderMakeXmlException extends SnapsOrderException {
    public SnapsOrderMakeXmlException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderMakeXmlException(String detailMessage) {
        super(detailMessage);
    }
}
