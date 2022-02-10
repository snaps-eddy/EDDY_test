package com.snaps.mobile.activity.edit.view.custom_progress.progress_views;

import android.app.Activity;

import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;

/**
 * Created by ysjeong on 2017. 4. 12..
 */

public class SnapsTimerProgressForLoading extends SnapsTimerProgressBase {

    public SnapsTimerProgressForLoading(Activity activity, SnapsTimerProgressViewFactory.eTimerProgressType progressType) {
        super(activity, progressType);
    }

    @Override
    protected void initHook() {}

    @Override
    protected void showProgressHook() {
        setProgressBarShape(eProgressBarShape.CIRCLE);
    }

    @Override
    protected void updateProgressBar() {
        /** 단순히 동그리가 도는 형태라 별도로 처리할 게 없다. **/
    }

    @Override
    protected void updateProgressText() throws Exception {
        /** 단순히 동그리가 도는 형태라 별도로 처리할 게 없다. **/
    }
}
