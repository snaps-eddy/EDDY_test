package com.snaps.mobile.activity.home.intent_strategies;

import android.app.Activity;
import android.content.Intent;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.home.model.HomeIntentHandleData;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.webview.ZoomProductWebviewActivity;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;
import com.snaps.mobile.utils.ui.UrlUtil;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeIntentDataHandlerP0014 extends HomeIntentDataHandlerBase {
    public HomeIntentDataHandlerP0014(Activity activity, HomeIntentHandleData intentHandleData) {
        super(activity, intentHandleData);
    }

    @Override
    public void performGoToFunction() throws Exception {
        String url = UrlUtil.getIntentDataFromFullUrl(getIntent(), "url", true);
        String prmchnlcode = getIntent().getStringExtra("prmchnlcode");

        url = SnapsTPAppManager.getSnapsWebDomain(getActivity(), url, SnapsTPAppManager.getBaseQuary(getActivity(), false));

        String userNo = SnapsLoginManager.getUUserNo(getActivity());
        if (userNo != null && !StringUtil.isEmpty(userNo)) {
            url += "?";
            url += "&f_user_no=" + userNo;
            url += "&f_chnl_code=" + Config.getCHANNEL_CODE();
        }

        Intent intent = new Intent(getActivity(), RenewalHomeActivity.class);
        intent.putExtra("targetUrl", url);
        intent.putExtra("prmchnlcode", prmchnlcode);
        getActivity().startActivity(intent);

        SnapsMenuManager.requestFinishPrevActivity();
    }
}
