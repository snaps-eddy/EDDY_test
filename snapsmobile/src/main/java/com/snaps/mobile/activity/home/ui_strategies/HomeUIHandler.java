package com.snaps.mobile.activity.home.ui_strategies;

import com.snaps.mobile.activity.home.model.HomeUIControl;
import com.snaps.mobile.activity.home.model.HomeUIData;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public interface HomeUIHandler {

    void initialize();

    HomeUIControl getHomeUIControl();

    HomeUIData getHomeUIData();

    void checkInnerPopup() throws Exception;

    void refreshDataAndUI();

    void handleOnResume();

    void handleOnBackPressed(HomeUIBase.ISnapsFinishCheckListener snapsFinishCheckListener);

    void clearAppFinishCheckFlag();
}
