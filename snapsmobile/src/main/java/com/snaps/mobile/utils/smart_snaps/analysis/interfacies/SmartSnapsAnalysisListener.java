package com.snaps.mobile.utils.smart_snaps.analysis.interfacies;

import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;

/**
 * Created by ysjeong on 2018. 4. 24..
 */

public interface SmartSnapsAnalysisListener {
    void onProgress(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType, int total, int complete);
    void onCompleteTask(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType);
    void onException(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType, Exception e);
    void onFailed(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType, String msg);
}
