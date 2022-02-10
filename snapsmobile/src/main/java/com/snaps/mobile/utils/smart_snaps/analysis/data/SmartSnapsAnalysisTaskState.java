package com.snaps.mobile.utils.smart_snaps.analysis.data;

import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisTaskImp;

/**
 * Created by ysjeong on 2018. 4. 25..
 */

public class SmartSnapsAnalysisTaskState {
    private SmartSnapsAnalysisTaskImp task;
    private int progress;
    private boolean isCompleteTask;

    public static SmartSnapsAnalysisTaskState createNewInstance() {
        return new SmartSnapsAnalysisTaskState();
    }

    public SmartSnapsAnalysisTaskImp getTask() {
        return task;
    }

    public void setTask(SmartSnapsAnalysisTaskImp task) {
        this.task = task;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isCompleteTask() {
        return isCompleteTask;
    }

    public void setCompleteTask(boolean completeTask) {
        isCompleteTask = completeTask;
    }
}
