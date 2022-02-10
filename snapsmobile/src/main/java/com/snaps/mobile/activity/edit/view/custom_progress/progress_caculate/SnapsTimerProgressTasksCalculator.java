package com.snaps.mobile.activity.edit.view.custom_progress.progress_caculate;

/**
 * Created by ysjeong on 2017. 4. 12..
 */

public class SnapsTimerProgressTasksCalculator extends SnapsTimerProgressBaseCalculator {
    private float progressValue = 0;

    public static SnapsTimerProgressTasksCalculator createTasksCalculator() {
        return new SnapsTimerProgressTasksCalculator();
    }

    @Override
    public int getAllTaskRemainEstimatedTime() {
        return 0;
    }

    @Override
    public int getCurrentProgressValue() {
        return (int) Math.max(0, Math.min(100, progressValue));
    }

    public void updateProgressValue(float progress) {
        this.progressValue = progress;
    }

    @Override
    public void releaseData() {}
}
