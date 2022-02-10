package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;

import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IPostingResult;
import com.snaps.kakao.utils.share.SNSShareUtil;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventSNSShareHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventSNSShareHandler.class.getSimpleName();
    String snsShareCallBack = "";

    public SnapsWebEventSNSShareHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        snsShareCallBack = SNSShareUtil.sendSNSShareContents(activity, urlData, new IPostingResult() {
            @Override
            public void OnPostingComplate(boolean isSucess, String errMsg) {
                SNSShareUtil.loadSnsShareCallBack(isSucess, errMsg, snsShareCallBack, handleDatas.getWebview());
            }
        });

        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class Name:" + getClass().getName());
    }
}
