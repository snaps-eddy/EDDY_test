package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderUploadXmlPhotoPrintException extends SnapsOrderException {
    public SnapsOrderUploadXmlPhotoPrintException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderUploadXmlPhotoPrintException(String detailMessage) {
        super(detailMessage);
    }
}
