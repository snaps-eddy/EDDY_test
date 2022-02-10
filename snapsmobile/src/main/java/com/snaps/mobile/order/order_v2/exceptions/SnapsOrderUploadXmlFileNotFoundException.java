package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderUploadXmlFileNotFoundException extends SnapsOrderException {
    public SnapsOrderUploadXmlFileNotFoundException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderUploadXmlFileNotFoundException(String detailMessage) {
        super(detailMessage);
    }
}
