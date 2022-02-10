package com.snaps.mobile.order.order_v2.task.prepare_process;

import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;

/**
 * Created by ysjeong on 2017. 3. 31..
 */

public class SnapsOrderPrepareHandlerIdentifyPhoto extends SnapsOrderPrepareHandlerDefault {
    private SnapsOrderPrepareHandlerIdentifyPhoto(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        super(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderPrepareHandlerIdentifyPhoto createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        return new SnapsOrderPrepareHandlerIdentifyPhoto(attribute, snapsOrderActivityBridge);
    }

    @Override
    protected boolean isLackQuantity() throws Exception {
        return false;
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLowResolutionAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createSaveToBasketAlertNotPrintOnlyCancelBtnAttribute();
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLackQuantityAlertAttribute() {
        return null;
    }
}
