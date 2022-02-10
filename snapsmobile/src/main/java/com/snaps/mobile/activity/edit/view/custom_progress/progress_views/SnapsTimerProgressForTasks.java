package com.snaps.mobile.activity.edit.view.custom_progress.progress_views;

import android.app.Activity;

import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_caculate.SnapsTimerProgressTasksCalculator;

/**
 * Created by ysjeong on 2017. 4. 12..
 */

/**
 * 총 작업량 증가량을 바 모양 프로그래스로 표현하며, 남은 예상 시간을 계산하여 보여준다.
 */
public class SnapsTimerProgressForTasks extends SnapsTimerProgressBase {

    public SnapsTimerProgressForTasks(Activity activity, SnapsTimerProgressViewFactory.eTimerProgressType progressType) {
        super(activity, progressType);
    }

    @Override
    protected void initHook() {
        setProgressCalculator(SnapsTimerProgressTasksCalculator.createTasksCalculator());
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
        updateRemainTimeText();
    }

    public SnapsTimerProgressTasksCalculator getProgressCalculator() {
        return (SnapsTimerProgressTasksCalculator) super.getProgressCalculator();
    }
}
