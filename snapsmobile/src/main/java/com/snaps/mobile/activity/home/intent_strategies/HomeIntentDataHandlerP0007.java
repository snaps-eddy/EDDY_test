package com.snaps.mobile.activity.home.intent_strategies;

import android.app.Activity;
import android.content.Intent;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.home.model.HomeIntentHandleData;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.webview.PopupWebviewActivity;
import com.snaps.mobile.utils.ui.UrlUtil;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeIntentDataHandlerP0007 extends HomeIntentDataHandlerBase {
    public HomeIntentDataHandlerP0007(Activity activity, HomeIntentHandleData intentHandleData) {
        super(activity, intentHandleData);
    }

    @Override
    public void performGoToFunction() throws Exception {
		String url = UrlUtil.getIntentDataFromFullUrl(getIntent(), "url", false);
        String userNo = SnapsLoginManager.getUUserNo(getActivity());
		if (userNo != null && !StringUtil.isEmpty(userNo)) {
			url += "?";
			url += "&f_user_no=" + userNo;
			url += "&f_chnl_code=" + Config.getCHANNEL_CODE();
		}

		goToEvent(url);
    }

    public void goToEvent(String url) {
        Intent intent = PopupWebviewActivity.getIntent(getActivity(), url);
        if (intent != null)
            getActivity().startActivity(intent);
    }
}
