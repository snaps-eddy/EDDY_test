package com.snaps.mobile.utils.smart_snaps.analysis.task;

import android.app.Activity;

import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisListener;
import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisTaskImp;

/**
 * Created by ysjeong on 2018. 4. 24..
 */

public abstract class SmartRecommendBookAnalysisBaseTask implements SmartSnapsAnalysisTaskImp {
    private Activity activity;
    private boolean isCanceled = false;
    private SmartSnapsAnalysisListener analysisListener = null;

    public SmartRecommendBookAnalysisBaseTask(Activity activity, SmartSnapsAnalysisListener analysisListener) {
        setActivity(activity);

        setAnalysisListener(analysisListener);
    }

    public Activity getActivity() {
        return activity;
    }

    private void setActivity(Activity activity) {
        this.activity = activity;
    }

    public SmartSnapsAnalysisListener getAnalysisListener() {
        return analysisListener;
    }

    private void setAnalysisListener(SmartSnapsAnalysisListener analysisListener) {
        this.analysisListener = analysisListener;
    }

    boolean isCanceled() {
        return isCanceled;
    }

    @Override
    public void perform() {
        isCanceled = false;
    }

    @Override
    public void cancel() {
        isCanceled = true;
    }

    protected void sendException(Exception e) {
        if (getAnalysisListener() != null)
            getAnalysisListener().onException(getTaskType(), e);
    }

    protected void sendFailed(String msg) {
        if (getAnalysisListener() != null)
            getAnalysisListener().onFailed(getTaskType(), msg);
    }

    protected void sendProgress(int total, int complete) {
        if (getAnalysisListener() != null)
            getAnalysisListener().onProgress(getTaskType(), total, complete);
    }

    protected void sendComplete() {
        if (isCanceled()) return;

        if (getAnalysisListener() != null) {
            getAnalysisListener().onCompleteTask(getTaskType());
        }
    }
}
