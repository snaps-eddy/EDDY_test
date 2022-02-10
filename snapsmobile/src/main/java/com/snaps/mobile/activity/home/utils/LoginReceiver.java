package com.snaps.mobile.activity.home.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.mobile.activity.home.ui_strategies.HomeUIHandler;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class LoginReceiver extends BroadcastReceiver {
    HomeUIHandler homeUIHandler;
    //로그인이 완료가 되었을때 호출이 된다
    public LoginReceiver(HomeUIHandler homeUIHandler) {
        this.homeUIHandler = homeUIHandler;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action =intent.getAction();
        if(action.equals(Const_VALUE.LOGIN_ACTION)) {
            //homeUIHandler.getHomeUIControl().getWvController().refresh();
            // TODO : WEBVIEW 갱신해야할 경우 코드 변경
        } else if(action.equals(Const_VALUE.WEBVIEW_START)) {
            if(Config.useKorean()) {
                homeUIHandler.getHomeUIControl().getLinearLayoutWebViewProgress().setVisibility(View.VISIBLE);
                homeUIHandler.getHomeUIControl().getImageViewWebViewProgress().setVisibility(View.VISIBLE);
            }
        } else if(action.equals(Const_VALUE.WEBVIEW_END)) {
            homeUIHandler.getHomeUIControl().getLinearLayoutWebViewProgress().setVisibility(View.GONE);
            homeUIHandler.getHomeUIControl().getImageViewWebViewProgress().setVisibility(View.GONE);
        } else if(action.equals(Const_VALUE.WEBVIEW_FAIL)){
            homeUIHandler.getHomeUIControl().getLinearLayoutWebViewProgress().setVisibility(View.GONE);
            homeUIHandler.getHomeUIControl().getImageViewWebViewProgress().setVisibility(View.GONE);
        }
    }
}
