package com.snaps.mobile.utils.smart_snaps.analysis.task;

import android.app.Activity;

import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_ThemePage;
import com.snaps.common.utils.thread.ATask;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisListener;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

/**
 * Created by ysjeong on 2018. 4. 24..
 */

public class SmartRecommendBookGetPageBGResTask extends SmartRecommendBookAnalysisBaseTask {
    private static final String TAG = SmartRecommendBookGetPageBGResTask.class.getSimpleName();
    public SmartRecommendBookGetPageBGResTask(Activity activity, SmartSnapsAnalysisListener analysisListener) {
        super(activity, analysisListener);
    }

    @Override
    public SmartSnapsConstants.eSmartSnapsAnalysisTaskType getTaskType() {
        return SmartSnapsConstants.eSmartSnapsAnalysisTaskType.GET_PAGE_BG_RES;
    }

    @Override
    public void perform() {
        super.perform();

        try {
            requestGetPageBGResList();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            sendException(e);
        }
    }

    private void requestGetPageBGResList() throws Exception {
        if (isAllTaskCompleted()) {
            sendComplete();
        } else {
            ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
                @Override
                public void onPre() {}

                @Override
                public boolean onBG() {
                    SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();

                    Xml_ThemePage pageXML = GetParsedXml.getSmartSnapsAnalysisPhotoBookPageBGResList(Config.getPROD_CODE(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                    smartSnapsManager.setPageBGResListOfAnalysisPhotoBook(pageXML);
                    return pageXML != null;
                }

                @Override
                public void onPost(boolean result) {
                    if (result) {
                        sendComplete();
                    } else {
                        sendFailed("failed page bg res list.");
                    }
                }
            });
        }
    }

    private boolean isAllTaskCompleted() {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        return smartSnapsManager.isExistPageBGResListOfAnalysisPhotoBook();
    }
}
