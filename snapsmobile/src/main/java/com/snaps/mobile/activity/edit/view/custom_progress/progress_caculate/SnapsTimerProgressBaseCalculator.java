package com.snaps.mobile.activity.edit.view.custom_progress.progress_caculate;

/**
 * Created by ysjeong on 2017. 4. 12..
 */

public abstract class SnapsTimerProgressBaseCalculator {
    private long uploadRemainExpectSec = 0;

    public abstract int getAllTaskRemainEstimatedTime();

    public abstract int getCurrentProgressValue();

    public abstract void releaseData();

    public long getUploadRemainExpectTime() {
        return uploadRemainExpectSec;
    }

    public void setUploadRemainExpectSec(long uploadRemainExpectSec) {
        this.uploadRemainExpectSec = uploadRemainExpectSec;
    }
}
