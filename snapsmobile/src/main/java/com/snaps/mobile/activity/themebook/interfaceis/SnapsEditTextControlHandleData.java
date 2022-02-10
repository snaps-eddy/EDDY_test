package com.snaps.mobile.activity.themebook.interfaceis;

import android.app.Activity;
import android.view.View;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.mobile.activity.themebook.adapter.PopoverView;

public class SnapsEditTextControlHandleData {
    private Activity activity;
    private PopoverView popoverView;
    private int tempViewId = -1;
    private SnapsTemplate snapsTemplate;
    private View rootView;
    private ISnapsEditTextControlHandleListener handleListener;
    private boolean isAppliedBlurActivity;
    private boolean isCoverTitleEdit;
    private boolean shouldBeBlurBackground;
    private int activityRequestCode;
    private View titleLayout;

    public View getTitleLayout() {
        return titleLayout;
    }

    public Activity getActivity() {
        return activity;
    }

    public boolean isAppliedBlurActivity() {
        return isAppliedBlurActivity;
    }

    public PopoverView getPopoverView() {
        return popoverView;
    }

    public int getTempViewId() {
        return tempViewId;
    }

    public SnapsTemplate getSnapsTemplate() {
        return snapsTemplate;
    }

    public View getRootView() {
        return rootView;
    }

    public boolean isCoverTitleEdit() {
        return isCoverTitleEdit;
    }

    public ISnapsEditTextControlHandleListener getHandleListener() {
        return handleListener;
    }

    public boolean shouldBeBlurBackground() {
        return shouldBeBlurBackground;
    }

    public int getActivityRequestCode() {
        return activityRequestCode;
    }

    private SnapsEditTextControlHandleData(Builder builder) {
        this.activity = builder.activity;
        this.popoverView = builder.popoverView;
        this.tempViewId = builder.tempViewId;
        this.snapsTemplate = builder.snapsTemplate;
        this.rootView = builder.rootView;
        this.isAppliedBlurActivity = builder.isAppliedBlurActivity;
        this.handleListener = builder.handleListener;
        this.isCoverTitleEdit = builder.isCoverTitleEdit;
        this.shouldBeBlurBackground = builder.shouldBeBlurBackground;
        this.activityRequestCode = builder.activityRequestCode;
        this.titleLayout = builder.titleLayout;
    }

    public static class Builder {
        private Activity activity;
        private PopoverView popoverView;
        private int tempViewId = -1;
        private SnapsTemplate snapsTemplate;
        private View rootView;
        private boolean isAppliedBlurActivity;
        private boolean isCoverTitleEdit;
        private ISnapsEditTextControlHandleListener handleListener;
        private boolean shouldBeBlurBackground;
        private int activityRequestCode;
        private View titleLayout;

        public Builder setTitleLayout(View titleLayout) {
            this.titleLayout = titleLayout;
            return this;
        }

        public Builder setActivityRequestCode(int activityRequestCode) {
            this.activityRequestCode = activityRequestCode;
            return this;
        }

        public Builder setShouldBeBlurBackground(boolean shouldBeBlurBackground) {
            this.shouldBeBlurBackground = shouldBeBlurBackground;
            return this;
        }

        public Builder setCoverTitleEdit(boolean coverTitleEdit) {
            isCoverTitleEdit = coverTitleEdit;
            return this;
        }

        public Builder setAppliedBlurActivity(boolean appliedBlurActivity) {
            isAppliedBlurActivity = appliedBlurActivity;
            return this;
        }

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setPopoverView(PopoverView popoverView) {
            this.popoverView = popoverView;
            return this;
        }

        public Builder setTempViewId(int tempViewId) {
            this.tempViewId = tempViewId;
            return this;
        }

        public Builder setSnapsTemplate(SnapsTemplate snapsTemplate) {
            this.snapsTemplate = snapsTemplate;
            return this;
        }

        public Builder setRootView(View rootView) {
            this.rootView = rootView;
            return this;
        }

        public Builder setHandleListener(ISnapsEditTextControlHandleListener handleListener) {
            this.handleListener = handleListener;
            return this;
        }

        public SnapsEditTextControlHandleData create() {
            return new SnapsEditTextControlHandleData(this);
        }
    }
}
