package com.snaps.mobile.order.order_v2.exceptions.factories;

import com.snaps.mobile.order.order_v2.exceptions.SnapsInvalidImageDataException;
import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderException;

/**
 * Created by ysjeong on 2017. 11. 22..
 */

public class SnapsInvalidImageDataExceptionCreator {
    public static SnapsOrderException createExceptionWithMessage(String detailMessage) {
        return new SnapsInvalidImageDataException(detailMessage);
    }
}
