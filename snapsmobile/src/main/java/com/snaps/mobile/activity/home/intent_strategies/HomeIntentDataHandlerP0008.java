package com.snaps.mobile.activity.home.intent_strategies;

import android.app.Activity;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.model.HomeIntentHandleData;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.utils.ui.UrlUtil;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeIntentDataHandlerP0008 extends HomeIntentDataHandlerBase {
    public HomeIntentDataHandlerP0008(Activity activity, HomeIntentHandleData intentHandleData) {
        super(activity, intentHandleData);
    }

    @Override
    public void performGoToFunction() throws Exception {
		String url = UrlUtil.getIntentDataFromFullUrl(getIntent(), "optionUrl", true);
		String title = UrlUtil.getIntentDataFromFullUrl(getIntent(), "title", true);
        String userNo = SnapsLoginManager.getUUserNo(getActivity());

		if (url != null && userNo != null && !StringUtil.isEmpty(userNo)) {
			url += url.contains("?") ? "&" : "?";
			url += "&f_user_no=" + userNo;
			url += "&f_chnl_code=" + Config.getCHANNEL_CODE();
		}

		SnapsMenuManager.gotoKakaoEventPage(getActivity(), url, title);
    }
}
