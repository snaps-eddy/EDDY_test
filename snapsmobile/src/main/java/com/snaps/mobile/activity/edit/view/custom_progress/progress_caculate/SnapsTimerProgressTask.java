package com.snaps.mobile.activity.edit.view.custom_progress.progress_caculate;

import androidx.annotation.NonNull;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_views.SnapsProgressViewAPI;

/**
 * Created by ysjeong on 2017. 4. 12..
 */

public class SnapsTimerProgressTask {
    private static final String TAG = SnapsTimerProgressTask.class.getSimpleName();
    private SnapsProgressViewAPI.eTimerProgressTaskType progressTask;
    private int progressValue; //0~100;
    private long estimatedTime; // 1/1000 sec

    public SnapsTimerProgressTask(@NonNull SnapsProgressViewAPI.eTimerProgressTaskType task) {
        setProgressTask(task);
    }

    public SnapsProgressViewAPI.eTimerProgressTaskType getProgressTask() {
        return progressTask;
    }

    private void setProgressTask(SnapsProgressViewAPI.eTimerProgressTaskType progressTask) {
        this.progressTask = progressTask;
    }

    public int getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(int progressValue) {
        this.progressValue = progressValue;
    }

    public int getWeightAppliedProgressValue() {
        try {
            int weight = getProgressTask() != null ? getProgressTask().getWeight() : 0;
            int weightAppliedValue = (int) (weight * (getProgressValue() / 100.f));
            return Math.max(0, Math.min(100, weightAppliedValue));
        } catch (IllegalArgumentException e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

    public long getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(long estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}
