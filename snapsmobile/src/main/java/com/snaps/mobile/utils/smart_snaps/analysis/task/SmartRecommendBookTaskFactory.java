package com.snaps.mobile.utils.smart_snaps.analysis.task;

import android.app.Activity;

import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisListener;

/**
 * Created by ysjeong on 2018. 4. 24..
 */

public class SmartRecommendBookTaskFactory {
    public static SmartRecommendBookAnalysisBaseTask createTask(Activity activity, SmartSnapsAnalysisListener analysisListener, SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType) {
        switch (taskType) {
            case GET_PROJECT_CODE:
                return new SmartRecommendBookAnalysisGetProjectCodeTask(activity, analysisListener);
            case GET_RECOMMEND_TEMPLATE:
                return new SmartRecommendBookGetRecommendTemplateTask(activity, analysisListener);
            case UPLOAD_THUMBNAILS:
                return new SmartRecommendBookAnalysisThumbUploadTask(activity, analysisListener);
            case FIT_CENTER_FACE:
                return new SmartRecommendBookFaceFitCenterTask(activity, analysisListener);
            case GET_COVER_TEMPLATE:
                return new SmartRecommendBookGetCoverTemplateTask(activity, analysisListener);
            case GET_PAGE_TEMPLATE:
                return new SmartRecommendBookGetPageLayoutTask(activity, analysisListener);
            case GET_PAGE_BG_RES:
                return new SmartRecommendBookGetPageBGResTask(activity, analysisListener);
        }

        return null;
    }
}
