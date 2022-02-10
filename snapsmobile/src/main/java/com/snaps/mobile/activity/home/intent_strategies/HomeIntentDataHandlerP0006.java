package com.snaps.mobile.activity.home.intent_strategies;

import android.app.Activity;

import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.model.HomeIntentHandleData;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;
import com.snaps.mobile.utils.ui.UrlUtil;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeIntentDataHandlerP0006 extends HomeIntentDataHandlerBase {
    public HomeIntentDataHandlerP0006(Activity activity, HomeIntentHandleData intentHandleData) {
        super(activity, intentHandleData);
    }

    @Override
    public void performGoToFunction() throws Exception {
		String index = UrlUtil.getIntentDataFromFullUrl(getIntent(), "idx", false);
        SnapsMenuManager.goToNoticeList(getActivity(), SnapsTPAppManager.getNoticeUrl(getActivity()), index);
    }
}
