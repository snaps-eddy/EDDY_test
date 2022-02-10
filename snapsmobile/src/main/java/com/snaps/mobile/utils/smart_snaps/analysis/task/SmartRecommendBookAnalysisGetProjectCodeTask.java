package com.snaps.mobile.utils.smart_snaps.analysis.task;

import android.app.Activity;

import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderGetPROJCodeTaskImp;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.task.upload_task.handler.SnapsOrderTaskHandlerFactory;
import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisListener;

/**
 * Created by ysjeong on 2018. 4. 24..
 */

public class SmartRecommendBookAnalysisGetProjectCodeTask extends SmartRecommendBookAnalysisBaseTask {
    private static final String TAG = SmartRecommendBookAnalysisGetProjectCodeTask.class.getSimpleName();
    SmartRecommendBookAnalysisGetProjectCodeTask(Activity activity, SmartSnapsAnalysisListener analysisListener) {
        super(activity, analysisListener);
    }

    @Override
    public SmartSnapsConstants.eSmartSnapsAnalysisTaskType getTaskType() {
        return SmartSnapsConstants.eSmartSnapsAnalysisTaskType.GET_PROJECT_CODE;
    }

    @Override
    public void perform() {
        super.perform();

        startFetchProjectCode();
    }

    private void startFetchProjectCode() {
        try {
            if (isCanceled()) return;

            if (Config.isValidProjCode()) {
                sendComplete();
            } else {
                SnapsOrderGetPROJCodeTaskImp handler = createGetPROJCodeTask();
                handler.getProjectCode(new SnapsOrderResultListener() {
                    @Override
                    public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                        if (Config.isValidProjCode()) {
                            sendComplete();
                        } else {
                            sendFailed("failed is not ValidProjCode");
                        }
                    }

                    @Override
                    public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                        sendFailed("failed get product code.");
                    }
                });
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            sendException(e);
        }
    }

    private SnapsOrderGetPROJCodeTaskImp createGetPROJCodeTask() throws Exception {
        return SnapsOrderTaskHandlerFactory.createGetPROJCodeTaskWithAttribute(getActivity());
    }
}
