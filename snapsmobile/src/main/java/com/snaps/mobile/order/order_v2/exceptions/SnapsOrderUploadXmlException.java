package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderUploadXmlException extends SnapsOrderException {
    public SnapsOrderUploadXmlException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderUploadXmlException(String detailMessage) {
        super(detailMessage);
    }
}
