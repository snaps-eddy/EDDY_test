package com.snaps.mobile.activity.home.utils.push_handlers;

import android.app.Activity;

import com.snaps.common.structure.SnapsHandler;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.RenewalHomeActivity;

/**
 * Created by ysjeong on 2017. 8. 24..
 */

public class SnapsPushHandlerForGoCart extends SnapsBasePushHandler {

    public SnapsPushHandlerForGoCart(Activity activity, SnapsPushHandleData pushHandleData) {
        super(activity, pushHandleData);
    }

    @Override
    public boolean performPushDataHandle() {
        // 장바구니로 보내기...
        boolean isGoToCart = getPushHandleData().getIntent().getBooleanExtra("goToCart", false);
        if (isGoToCart) {
            SnapsHandler handler = getPushHandleData().getSnapsHandler();
            if (handler != null)
                handler.sendEmptyMessage(RenewalHomeActivity.HANDLE_MSG_GOTO_CART);
            //SnapsMenuManager.goToCartList(getActivity(), getPushHandleData().getHomeUIHandler());
            return true;
        }
        return false;
    }
}
