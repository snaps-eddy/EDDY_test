package com.snaps.mobile.activity.home.intent_strategies;

import android.app.Activity;

import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.model.HomeIntentHandleData;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeIntentDataHandlerP0005 extends HomeIntentDataHandlerBase {
    public HomeIntentDataHandlerP0005(Activity activity, HomeIntentHandleData intentHandleData) {
        super(activity, intentHandleData);
    }

    @Override
    public void performGoToFunction() throws Exception {
        if (!SnapsLoginManager.isLogOn(getActivity()))  {
            SnapsLoginManager.startLogInProcess(getActivity(), Const_VALUES.LOGIN_P_LOGIN);
            return;
        }

        SnapsMenuManager.gotoCouponAct(getActivity(), SnapsTPAppManager.getCouponUrl(getActivity()));
    }
}
