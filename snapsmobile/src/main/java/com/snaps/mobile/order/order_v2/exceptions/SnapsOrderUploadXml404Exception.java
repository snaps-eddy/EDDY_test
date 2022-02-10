package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderUploadXml404Exception extends SnapsOrderException {
    public SnapsOrderUploadXml404Exception(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderUploadXml404Exception(String detailMessage) {
        super(detailMessage);
    }
}
