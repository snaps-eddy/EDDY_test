package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderUploadXmlHostException extends SnapsOrderException {
    public SnapsOrderUploadXmlHostException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderUploadXmlHostException(String detailMessage) {
        super(detailMessage);
    }
}
