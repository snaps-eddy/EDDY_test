package com.snaps.mobile.order.order_v2.util.handler;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;

/**
 * Created by ysjeong on 2017. 12. 21..
 */

public class SnapsOrderHandlerFactory {
    public static SnapsOrderBaseHandler createSnapsOrderHandler(@NonNull Activity activity, @NonNull SnapsOrderActivityBridge snapsOrderActivityBridge) throws Exception {
        if (SnapsDiaryDataManager.isAliveSnapsDiaryService())
            return SnapsOrderDiaryHandler.createInstance(activity, snapsOrderActivityBridge);

        return SnapsOrderDefaultHandler.createInstance(activity, snapsOrderActivityBridge);
    }
}
