package com.snaps.mobile.activity.home.intent_strategies;

import android.app.Activity;

import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.TabStyleNativeScrollViewController;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.home.model.HomeIntentHandleData;
import com.snaps.mobile.activity.home.model.HomeUIControl;
import com.snaps.mobile.activity.home.ui_strategies.HomeUIHandler;

import errorhandle.LogUtil;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeIntentDataHandlerP0002 extends HomeIntentDataHandlerBase {
    public HomeIntentDataHandlerP0002(Activity activity, HomeIntentHandleData intentHandleData) {
        super(activity, intentHandleData);
    }

    @Override
    public void performGoToFunction() throws Exception {
        String index = getIntent().getExtras().getString("fullurl");
        index = StringUtil.getTitleAtUrl(index, "idx");

        HomeIntentHandleData handleData = getIntentHandleData();
        HomeUIHandler uiHandler = handleData.getHomeUIHandler();
        HomeUIControl homeUIControl = uiHandler.getHomeUIControl();
        TabStyleNativeScrollViewController wvController = homeUIControl.getWvController();

        if (!wvController.gotoTab(Integer.parseInt(index))) {
            loadMenuNotWork();
        }
    }

    private void loadMenuNotWork() {
        LogUtil.sendCrashlyticsLog(getActivity(), "HomeActivity onCreate mainRes", "loadMenuNotWork");
        MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getResources().getString(R.string.menu_didnot_load_message), new ICustomDialogListener() {

            @Override
            public void onClick(byte clickedOk) {
                getActivity().finish();
            }
        });
    }
}
