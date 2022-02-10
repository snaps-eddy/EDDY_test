package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderUploadXmlJsonSyntaxException extends SnapsOrderException {
    public SnapsOrderUploadXmlJsonSyntaxException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderUploadXmlJsonSyntaxException(String detailMessage) {
        super(detailMessage);
    }
}
