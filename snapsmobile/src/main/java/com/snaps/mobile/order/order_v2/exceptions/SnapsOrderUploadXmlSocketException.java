package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderUploadXmlSocketException extends SnapsOrderException {
    public SnapsOrderUploadXmlSocketException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderUploadXmlSocketException(String detailMessage) {
        super(detailMessage);
    }
}
