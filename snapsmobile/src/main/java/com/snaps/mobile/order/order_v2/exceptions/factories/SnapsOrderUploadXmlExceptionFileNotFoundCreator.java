package com.snaps.mobile.order.order_v2.exceptions.factories;

import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderException;
import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderUploadXmlFileNotFoundException;

/**
 * Created by ysjeong on 2017. 11. 22..
 */

public class SnapsOrderUploadXmlExceptionFileNotFoundCreator {
    public static SnapsOrderException createExceptionWithMessage(String detailMessage) {
        return new SnapsOrderUploadXmlFileNotFoundException(detailMessage);
    }
}
