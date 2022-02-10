package com.snaps.mobile.tutorial.custom_tutorial;

import com.snaps.common.data.model.SnapsCommonResultListener;

public interface CustomTutorialInterface {
    int getContentViewLayoutId();
    void showTutorialView();
    void initTutorialView();
    long getTutorialAutoCloseTime();
    void setCloseTutorialListener(SnapsCommonResultListener<Void> listener);
}
