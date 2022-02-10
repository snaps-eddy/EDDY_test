package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.content.Intent;

import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.DetailProductWebviewActivity;
import com.snaps.mobile.activity.webview.PopupWebviewActivity;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;
import com.snaps.mobile.activity.webview.WebviewActivity;
import com.snaps.mobile.activity.webview.ZoomProductWebviewActivity;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventOpenAppPopupHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventOpenAppPopupHandler.class.getSimpleName();

    public SnapsWebEventOpenAppPopupHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        String openurl = urlData.get("openUrl");

        if (openurl != null) {
            try {
                openurl = URLDecoder.decode(openurl, "utf-8");
            } catch (UnsupportedEncodingException e) {
                Dlog.e(TAG, e);
            }
        }

        // 확대보기에는 orientation 값이 포함이 되서 들어온다.
        if (url.contains("orientation")) {
            // 확대보기..
            String title = urlData.get(Const_EKEY.WEB_TITLE_KEY);
            try {
                if (title != null)
                    title = URLDecoder.decode(title, "utf-8");
                else
                    title = "";
            } catch (UnsupportedEncodingException e) {
                Dlog.e(TAG, e);
            }

            openurl = SnapsTPAppManager.getSnapsWebDomain(activity, openurl, SnapsTPAppManager.getBaseQuary(activity, false));

            Intent intent = ZoomProductWebviewActivity.getIntent(activity, title, openurl, true);
            intent.putExtra("orientation", urlData.get("orientation"));
            activity.startActivity(intent);
            return true;
        } else if (openurl != null && openurl.startsWith("http")) {
            Dlog.e(TAG, "handleEvent() openUrl:" + openurl);
            String title = "";
            try {
                String naviTitle = urlData.get(Const_EKEY.WEB_NAVIBARTITLE_KEY);
                if(naviTitle != null)
                    title = URLDecoder.decode(naviTitle, "utf-8");
            } catch (UnsupportedEncodingException e) {
                Dlog.e(TAG, e);
            }
            String urlType = null;
            if(urlData != null && urlData.containsKey(Const_EKEY.WEB_URL_TYPE_KEY)) {
                urlType = urlData.get(Const_EKEY.WEB_URL_TYPE_KEY);
            }

            boolean isDeliveryUrl = urlType != null && urlType.equals(Const_EKEY.URL_TYPE_DELIVERY);

            Intent intent = WebviewActivity.getIntent(activity, (title == null) ? "" : title, openurl);
            intent.putExtra("isEnableBack", isDeliveryUrl ? false : true);
            intent.putExtra("urlType", urlType);
            activity.startActivity(intent);
            return true;
        }

        openurl = SnapsTPAppManager.getSnapsWebDomain(activity, openurl, SnapsTPAppManager.getBaseQuary(activity, false));

        PopupWebviewActivity.IPopupDialogFragmentCallback callback = new PopupWebviewActivity.IPopupDialogFragmentCallback() {
            @Override
            public void onReceived(String msg) {
                if(msg != null) {
                    if(msg.startsWith("snapsapp")) {
                        SnapsShouldOverrideUrlLoader urlLoader = new SnapsShouldOverrideUrlLoader(activity, facebook, kakao);
                        urlLoader.shouldOverrideUrlLoading(handleDatas.getWebview(), msg, this);
                    }
                    else {
                        if( msg.startsWith(SnapsWebEventBaseHandler.DETAIL_PRODUCT_SCHEME) || Const_PRODUCT.checkDetailProduct(msg) ) {
                            String titleStr = StringUtil.getTitleAtUrl(msg, "naviBarTitle");
                            if( titleStr == null || titleStr.length() < 1 ) titleStr = StringUtil.getTitleAtUrl( msg, "F_TMPL_NAME" );

                            Intent intent = DetailProductWebviewActivity.getIntent(activity,
                                    titleStr != null && titleStr.length() > 0 ? titleStr : "디자인 상세",
                                    msg, true, true, SnapsMenuManager.eHAMBURGER_ACTIVITY.PRODUCT_DETAIL_PAGE);
                            activity.startActivity(intent);
                            activity.overridePendingTransition( R.anim.slide_in_from_right, R.anim.slide_out_to_left );
                        } else
                            sendWebCommand(handleDatas.getWebview(), msg);
                    }
                }
            }
        };

        Intent intent = PopupWebviewActivity.getIntent(activity, callback, openurl);
        if (intent != null)
            activity.startActivity(intent);

        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
