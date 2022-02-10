package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;

import com.snaps.common.utils.constant.Const_EKEY;
import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IPostingResult;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventStoryLinkHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventStoryLinkHandler.class.getSimpleName();
    public SnapsWebEventStoryLinkHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        sendStoryLink(urlData);

        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    /***
     * 카카오스토리 포스팅
     *
     * @param urlData
     * @return
     */
    boolean sendStoryLink(final HashMap<String, String> urlData) {
        String title = urlData.get(Const_EKEY.WEB_TITLE_KEY);
        String desc = urlData.get(Const_EKEY.WEB_DESCRIPTION);
        String imgUrl = urlData.get(Const_EKEY.WEB_IMAGE_URL);
        String url = urlData.get(Const_EKEY.WEB_LINKURL);

        try {
            if (title != null)
                title = URLDecoder.decode(title, "utf-8");
            if (desc != null)
                desc = URLDecoder.decode(desc, "utf-8");
            if (url != null)
                url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            desc = "";
        }

        if (title != null) {
            return kakao.sendPostingLink(activity, url, title, desc, imgUrl, "article");
        }
        kakao.sendPostingLink(url, desc, new IPostingResult() {
            @Override
            public void OnPostingComplate(boolean isSucess, String errMsg) {

            }
        });

        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class Name:" + getClass().getName());
    }
}
