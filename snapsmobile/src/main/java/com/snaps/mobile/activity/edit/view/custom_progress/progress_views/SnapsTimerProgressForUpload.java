package com.snaps.mobile.activity.edit.view.custom_progress.progress_views;

import android.app.Activity;

import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_caculate.SnapsTimerProgressProjectUploadCalculator;

/**
 * Created by ysjeong on 2017. 4. 12..
 */

public class SnapsTimerProgressForUpload extends SnapsTimerProgressBase {

    public SnapsTimerProgressForUpload(Activity activity, SnapsTimerProgressViewFactory.eTimerProgressType progressType) {
        super(activity, progressType);
    }

    @Override
    protected void initHook() {
        setProgressCalculator(SnapsTimerProgressProjectUploadCalculator.createUploadProgressTaskCalculator());
    }

    @Override
    protected void showProgressHook() {
        setProgressBarShape(eProgressBarShape.BAR);
    }

    @Override
    protected void updateProgressBar() throws Exception {
        updateProgressView();
    }

    @Override
    protected void updateProgressText() throws Exception {
//        updateRemainTimeText();
    }

    public SnapsTimerProgressProjectUploadCalculator getProgressCalculator() {
        return (SnapsTimerProgressProjectUploadCalculator) super.getProgressCalculator();
    }
}
