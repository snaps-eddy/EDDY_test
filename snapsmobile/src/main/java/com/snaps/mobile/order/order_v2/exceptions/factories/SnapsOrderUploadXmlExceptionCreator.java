package com.snaps.mobile.order.order_v2.exceptions.factories;

import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderException;
import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderUploadXmlException;

/**
 * Created by ysjeong on 2017. 11. 22..
 */

public class SnapsOrderUploadXmlExceptionCreator {
    public static SnapsOrderException createExceptionWithMessage(String detailMessage) {
        return new SnapsOrderUploadXmlException(detailMessage);
    }
}
