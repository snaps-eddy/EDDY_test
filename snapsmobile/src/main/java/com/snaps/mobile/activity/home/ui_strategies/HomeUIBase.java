package com.snaps.mobile.activity.home.ui_strategies;

import android.app.Activity;
import android.widget.Toast;

import com.snaps.mobile.activity.home.model.HomeUIControl;
import com.snaps.mobile.activity.home.model.HomeUIData;
import com.snaps.mobile.activity.home.utils.SnapsEventHandler;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public abstract class HomeUIBase implements HomeUIHandler {

    public interface ISnapsFinishCheckListener {
        void performSnapsFinish();
        void requestClearAppFinishCheckFlag();
    }

    private Activity activity = null;
    private HomeUIControl homeUIControl = null;
    private HomeUIData homeUIData = null;
    private SnapsEventHandler eventHandler = null;

    private Toast appFinishToast;
    private boolean appFinishCheckFlag = false;

    public HomeUIBase(Activity activity, SnapsEventHandler eventHandler) {
        this.homeUIControl = new HomeUIControl();
        this.homeUIData = new HomeUIData();
        this.activity = activity;
        this.eventHandler = eventHandler;
    }

    @Override
    public HomeUIControl getHomeUIControl() {
        return homeUIControl;
    }

    @Override
    public HomeUIData getHomeUIData() {
        return homeUIData;
    }

    public abstract void createHomeUIControls();

    public Activity getActivity() {
        return activity;
    }

    public SnapsEventHandler getEventHandler() {
        return eventHandler;
    }

    public Toast getAppFinishToast() {
        return appFinishToast;
    }

    public void setAppFinishToast(Toast appFinishToast) {
        this.appFinishToast = appFinishToast;
    }

    public boolean isAppFinishCheckFlag() {
        return appFinishCheckFlag;
    }

    public void setAppFinishCheckFlag(boolean appFinishCheckFlag) {
        this.appFinishCheckFlag = appFinishCheckFlag;
    }
}
