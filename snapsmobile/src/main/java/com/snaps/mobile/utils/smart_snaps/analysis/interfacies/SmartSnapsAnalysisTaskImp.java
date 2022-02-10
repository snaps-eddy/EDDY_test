package com.snaps.mobile.utils.smart_snaps.analysis.interfacies;

import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;

/**
 * Created by ysjeong on 2018. 4. 24..
 */

public interface SmartSnapsAnalysisTaskImp {
    SmartSnapsConstants.eSmartSnapsAnalysisTaskType getTaskType();
    void perform();
    void cancel();
}
