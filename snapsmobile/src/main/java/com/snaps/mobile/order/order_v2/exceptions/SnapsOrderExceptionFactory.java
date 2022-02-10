package com.snaps.mobile.order.order_v2.exceptions;

import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsOrderGetProjectCodeExceptionCreator;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsOrderMakeXmlExceptionCreator;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsOrderImageUploadExceptionCreator;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsOrderUploadXml404ExceptionCreator;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsOrderUploadXmlExceptionCreator;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsOrderUploadXmlExceptionFileNotFoundCreator;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsOrderUploadXmlExceptionHostCreator;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsOrderUploadXmlExceptionJsonSyntaxCreator;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsOrderUploadXmlExceptionPhotoPrintCreator;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsOrderUploadXmlExceptionSocketCreator;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;

/**
 * Created by ysjeong on 2017. 6. 22..
 */

public class SnapsOrderExceptionFactory {
    public static SnapsOrderException createSnapsOrderExceptionWithDetailMsg(SnapsOrderConstants.eSnapsOrderType orderType, String msg) {
        if (orderType == null) {
            return new SnapsOrderException(msg);
        }

        SnapsOrderException exception = null;
        switch (orderType) {
            case ORDER_TYPE_GET_PROJECT_CODE :
            case ORDER_TYPE_VERIFY_PROJECT_CODE :
                exception = SnapsOrderGetProjectCodeExceptionCreator.createExceptionWithMessage(msg);
                break;
            case ORDER_TYPE_UPLOAD_ORG_IMAGE :
            case ORDER_TYPE_UPLOAD_THUMB_IMAGE :
                exception = SnapsOrderImageUploadExceptionCreator.createExceptionWithMessage((msg));
                break;
            case ORDER_TYPE_MAKE_XML :
                exception = SnapsOrderMakeXmlExceptionCreator.createExceptionWithMessage((msg));
                break;
            case ORDER_TYPE_UPLOAD_XML :
                exception = createUploadXmlDetailException((msg));
                break;
            default :
                exception = new SnapsOrderException(msg);
                break;
        }
        return exception;
    }

    private static SnapsOrderException createUploadXmlDetailException(String msg) {
        if (StringUtil.isEmpty(msg)) return SnapsOrderUploadXmlExceptionCreator.createExceptionWithMessage((msg));

        if (msg.contains("404 Not Found")) {
            return SnapsOrderUploadXml404ExceptionCreator.createExceptionWithMessage(msg);
        } else if (msg.contains("SocketException")) {
            return SnapsOrderUploadXmlExceptionSocketCreator.createExceptionWithMessage(msg);
        } else if (msg.contains("HttpHostConnectException") || msg.contains("refused") || msg.contains("UnknownHostException")) {
            return SnapsOrderUploadXmlExceptionHostCreator.createExceptionWithMessage(msg);
        } else if (msg.contains("JsonSyntaxException")) {
            return SnapsOrderUploadXmlExceptionJsonSyntaxCreator.createExceptionWithMessage(msg);
        } else if (msg.contains("FileNotFoundException")) {
            return SnapsOrderUploadXmlExceptionFileNotFoundCreator.createExceptionWithMessage(msg);
        } else if (msg.contains("snaps photo print upload error")) {
            return SnapsOrderUploadXmlExceptionPhotoPrintCreator.createExceptionWithMessage(msg);
        }

        return SnapsOrderUploadXmlExceptionCreator.createExceptionWithMessage((msg));
    }

}
