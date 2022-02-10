package com.snaps.mobile.order.order_v2.task.upload_task.handler;

import android.app.Activity;

import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderGetPROJCodeTaskImp;

/**
 * Created by ysjeong on 2017. 4. 14..
 */

public class SnapsOrderTaskHandlerFactory {
    public static SnapsOrderTaskBaseHandler createOrderTaskHandlerWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) throws Exception {
        if (SnapsDiaryDataManager.isAliveSnapsDiaryService())
            return SnapsOrderTaskDiaryHandler.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);

        return SnapsOrderTaskDefaultHandler.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderGetPROJCodeTaskImp createGetPROJCodeTaskWithAttribute(Activity activity) throws Exception {
        return SnapsOrderTaskDefaultHandler.createInstanceForGetPROJCodeWithAttribute(new SnapsOrderAttribute.Builder().setActivity(activity).create());
    }
}
