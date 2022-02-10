package com.snaps.mobile.tutorial.custom_tutorial;

import android.app.Dialog;

import com.snaps.common.data.model.SnapsCommonResultListener;

public abstract class CustomTutorialBaseStrategy implements CustomTutorialInterface {

    private SnapsCommonResultListener<Void> closeListener = null;
    private Dialog dialogContext;

    public CustomTutorialBaseStrategy(Dialog dialog) {
        dialogContext = dialog;
    }

    @Override
    public void setCloseTutorialListener(SnapsCommonResultListener<Void> listener) {
        this.closeListener = listener;
    }

    public SnapsCommonResultListener<Void> getCloseListener() {
        return closeListener;
    }

    public Dialog getDialogContext() {
        return dialogContext;
    }

    protected void sendCloseTutorialMsg() {
        if (getCloseListener() == null) return;

        getCloseListener().onResult(null);
    }
}
