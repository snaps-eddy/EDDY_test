package com.snaps.mobile.utils.smart_snaps.analysis.task;

import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisListener;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class SmartRecommendBookAnalysisTaskChecker implements SmartSnapsAnalysisListener {
    private Queue<SmartSnapsConstants.eSmartSnapsAnalysisTaskType> taskList = new LinkedList<>();
    private SnapsCommonResultListener<Boolean> resultListener = null;

    public static SmartRecommendBookAnalysisTaskChecker createChecker(SmartSnapsConstants.eSmartSnapsAnalysisTaskType... taskTypes) {
        SmartRecommendBookAnalysisTaskChecker checker = new SmartRecommendBookAnalysisTaskChecker();
        if (taskTypes != null) {
            checker.taskList.addAll(Arrays.asList(taskTypes));
        }
        return checker;
    }

    public void setResultListener(SnapsCommonResultListener<Boolean> resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    public void onProgress(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType, int total, int complete) {}

    @Override
    public void onCompleteTask(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType) {
        removeTask(taskType);
        if (taskList.isEmpty()) {
            postResult(true);
        }
    }

    @Override
    public void onException(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType, Exception e) {
        removeTask(taskType);
        if (taskList.isEmpty()) {
            postResult(false);
        }
    }

    @Override
    public void onFailed(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType, String msg) {
        removeTask(taskType);
        if (taskList.isEmpty()) {
            postResult(false);
        }
    }

    private void removeTask(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType) {
        if (taskList == null) return;

        if (taskList.contains(taskType)) {
            taskList.remove(taskType);
        }
    }

    private void postResult(boolean result) {
        if (resultListener != null) {
            resultListener.onResult(result);
        }
    }
}