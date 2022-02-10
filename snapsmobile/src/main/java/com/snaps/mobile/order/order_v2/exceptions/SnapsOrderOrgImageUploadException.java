package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderOrgImageUploadException extends SnapsOrderException {
    public SnapsOrderOrgImageUploadException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderOrgImageUploadException(String detailMessage) {
        super(detailMessage);
    }
}
