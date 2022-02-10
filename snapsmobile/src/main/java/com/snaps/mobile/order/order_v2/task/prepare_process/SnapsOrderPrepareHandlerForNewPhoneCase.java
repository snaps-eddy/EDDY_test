package com.snaps.mobile.order.order_v2.task.prepare_process;

import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;

public class SnapsOrderPrepareHandlerForNewPhoneCase extends SnapsOrderPrepareHandlerDefault {

    protected SnapsOrderPrepareHandlerForNewPhoneCase(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        super(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderPrepareHandlerForNewPhoneCase createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        return new SnapsOrderPrepareHandlerForNewPhoneCase(attribute, snapsOrderActivityBridge);
    }

    @Override
    public void setProductYN() {
        getAttribute().getSnapsTemplate().setF_PRO_YORN("Y");
    }
}
