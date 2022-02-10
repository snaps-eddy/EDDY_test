package com.snaps.mobile.activity.home.intent_strategies;

import android.app.Activity;
import android.text.TextUtils;

import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.mobile.activity.home.model.HomeIntentHandleData;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeIntentDataHandlerP0012 extends HomeIntentDataHandlerBase {
    public HomeIntentDataHandlerP0012(Activity activity, HomeIntentHandleData intentHandleData) {
        super(activity, intentHandleData);
    }

    @Override
    public void performGoToFunction() throws Exception {
		String loginType="";
		if (TextUtils.isEmpty(SnapsLoginManager.getUUserNo(getActivity()))) {
			loginType = Const_VALUES.LOGIN_P_JOIN;
		}else{
			loginType = Const_VALUES.LOGIN_P_VERRIFY_POPUP;
		}
		SnapsLoginManager.startLogInProcess(getActivity(), loginType);
    }
}
