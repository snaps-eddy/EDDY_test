package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsInvalidImageDataException extends SnapsOrderException {
    public SnapsInvalidImageDataException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsInvalidImageDataException(String message) {
        super(message);
    }
}
