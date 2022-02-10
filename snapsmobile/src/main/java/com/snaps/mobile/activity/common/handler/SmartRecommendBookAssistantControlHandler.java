package com.snaps.mobile.activity.common.handler;

import com.snaps.common.utils.ui.CustomizeDialog;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.themebook.adapter.PopoverView;

public class SmartRecommendBookAssistantControlHandler {
    private PopoverView popupMenuView;
    private CustomizeDialog confirmDialog;
    private DialogDefaultProgress defaultProgress = null;

    public static SmartRecommendBookAssistantControlHandler createHandlerWithInstance() {
        return new SmartRecommendBookAssistantControlHandler();
    }

    private SmartRecommendBookAssistantControlHandler() {}

    public PopoverView getPopupMenuView() {
        return popupMenuView;
    }

    public void setPopupMenuView(PopoverView popupMenuView) {
        this.popupMenuView = popupMenuView;
    }

    public CustomizeDialog getConfirmDialog() {
        return confirmDialog;
    }

    public void setConfirmDialog(CustomizeDialog confirmDialog) {
        this.confirmDialog = confirmDialog;
    }

    public DialogDefaultProgress getDefaultProgress() {
        return defaultProgress;
    }

    public void setDefaultProgress(DialogDefaultProgress defaultProgress) {
        this.defaultProgress = defaultProgress;
    }
}
