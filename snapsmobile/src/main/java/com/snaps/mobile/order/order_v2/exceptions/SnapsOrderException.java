package com.snaps.mobile.order.order_v2.exceptions;

import errorhandle.logger.SnapsLoggerAttribute;
import errorhandle.logger.model.SnapsLoggerBase;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderException extends SnapsLoggerBase {
    private static final long serialVersionUID = 383772304585822285L;

    public SnapsOrderException(SnapsLoggerAttribute attribute) {
        super(attribute);
    }

    public SnapsOrderException(String message) {
        super(message);
        appendLog(message);
    }

    @Override
    protected void handleLogAfterLogAppend() {
        super.handleLogAfterLogAppend();
    }
}
