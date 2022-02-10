package com.snaps.mobile.activity.home.intent_strategies;

import android.app.Activity;

import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.model.HomeIntentHandleData;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeIntentDataHandlerP0010 extends HomeIntentDataHandlerBase {
    public HomeIntentDataHandlerP0010(Activity activity, HomeIntentHandleData intentHandleData) {
        super(activity, intentHandleData);
    }

    @Override
    public void performGoToFunction() throws Exception {
		SnapsMenuManager.gotoDiaryList(getActivity());
    }
}
