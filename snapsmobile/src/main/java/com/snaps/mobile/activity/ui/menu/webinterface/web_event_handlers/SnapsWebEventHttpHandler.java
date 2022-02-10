package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

import com.snaps.common.utils.constant.Const_PRODUCT;
import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.ui.menu.renewal.model.SubCategory;
import com.snaps.mobile.activity.ui.menu.webinterface.ISnapsWebViewLoadListener;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.DetailProductWebviewActivity;
import com.snaps.mobile.component.ImpWebViewProcess;
import com.snaps.mobile.component.ObserveScrollingWebView;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.util.HashSet;
import java.util.Iterator;

import errorhandle.CatchFragmentActivity;

/**
 * Created by ysjeong on 16. 8. 12..
 */

/**
 * 웹뷰에서 클릭 했을 때, 일반적인 URL타입의 처리 Handler.
 * (snapsApp://으로 시작하는 url이 아닌 경우...)
 */
public class SnapsWebEventHttpHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventHttpHandler.class.getSimpleName();
    private HashSet<ImpWebViewProcess> processes = null;
    private ObserveScrollingWebView observerWebView = null;
    private String url;
    private WebView webview;
    private ISnapsWebViewLoadListener snapsWebViewLoadListener = null;

    public SnapsWebEventHttpHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
        this.processes = handleDatas.getProcesses();
        this.observerWebView = handleDatas.getObserveScrollingWebView();
        this.url = handleDatas.getUrl();
        this.webview = handleDatas.getWebview();
        this.snapsWebViewLoadListener = handleDatas.getSnapsWebViewLoadListener();
    }

    @Override
    public boolean handleEvent() {
        if(processes != null) {
            Iterator<ImpWebViewProcess> it = processes.iterator();

            // boolean isRet = false;
            // boolean isProcess = false;

            while (it.hasNext()) {
                ImpWebViewProcess p = it.next();
                // true이면 다음처리 로직을 처리하지 않는다.
                if (observerWebView != null && p.shouldOverrideUrlLoading(observerWebView, url)) {
                    // 리턴을 해야할 값을 받는다.
                    return p.getCheckProcess();
                }
            }
        }

        sendPageEventTracker(activity, url);

        String titleStr = "";

        if( url.startsWith(DETAIL_PRODUCT_SCHEME) || Const_PRODUCT.checkDetailProduct(url) ) {
            titleStr = StringUtil.getTitleAtUrl(url, "naviBarTitle");
            if( titleStr == null || titleStr.length() < 1 ) titleStr = StringUtil.getTitleAtUrl( url, "F_TMPL_NAME" );

            int intentFlag = Const_PRODUCT.checkDetailProduct(url) ? Intent.FLAG_ACTIVITY_NEW_TASK : Intent.FLAG_ACTIVITY_CLEAR_TOP;

            SubCategory subCategory = MenuDataManager.findSubcategoryByUrl(url);
            if (subCategory!= null) {
                SnapsMenuManager menuManager = SnapsMenuManager.getInstance();
                menuManager.setSubCategory(subCategory);
            }

            Intent intent = DetailProductWebviewActivity.getIntent(
                    activity,
                    titleStr != null && titleStr.length() > 0 ? titleStr : "디자인 상세",
                    url,
                    true,
                    true,
                    intentFlag,
                    SnapsMenuManager.eHAMBURGER_ACTIVITY.PRODUCT_DETAIL_PAGE);

            activity.startActivity(intent);
            ( (Activity) activity ).overridePendingTransition( R.anim.slide_in_from_right, R.anim.slide_out_to_left );

            dismissPopupWebViewActivity();
        }
        else if (url.startsWith(THEMESCHMA)) {
            SubCategory subCategory = MenuDataManager.findSubcategoryByUrl(url);
            if (subCategory != null) {
                SnapsMenuManager menuManager = SnapsMenuManager.getInstance();
                menuManager.setSubCategory(subCategory);
            }

            SnapsMenuManager.dismissDialogFragment();

        } else {
            if (url.endsWith(".mp4")) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(url);
                i.setDataAndType(uri, "video/mp4");
                activity.startActivity(i);

            } else if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                activity.startActivity(intent);
            } else {
                if(webview != null) {
                    webview.loadUrl( url, SystemUtil.getWebviewVersionMapData(activity));
                }

                if(snapsWebViewLoadListener != null)
                    snapsWebViewLoadListener.onLoaded();
            }
        }

        return true;
    }

    private boolean checkQnaWritePage( Activity activity, String url ) {
        return ( !SnapsTPAppManager.isThirdPartyApp((CatchFragmentActivity)activity) && url.startsWith(QNA_WRITE_SCHEME) ) || ( SnapsTPAppManager.isThirdPartyApp((CatchFragmentActivity)activity) && url.startsWith(OLD_QNA_WRITE_SCHEME) );
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
