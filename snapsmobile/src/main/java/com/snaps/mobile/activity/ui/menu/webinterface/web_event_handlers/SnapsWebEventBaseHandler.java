package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.webkit.WebView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.photoprint.PhotoPrintProductInfo;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.PopupWebviewActivity;
import com.snaps.mobile.component.OnBadgeCountChangeListener;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import errorhandle.CatchFragmentActivity;
import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public abstract class SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventBaseHandler.class.getSimpleName();
    public static final String SCHMA = "snapsapp://";
    public static final String THEMESCHMA = SnapsAPI.WEB_DOMAIN() + "/mw/v3/store/product";
    public static final String CALL_URL = "callurl";
    public static final String CART_PRODUCT_LOAD_URL = "cart_product_load_url";
    public static final String OLD_QNA_WRITE_SCHEME = SnapsAPI.WEB_DOMAIN() + "/mw/site/snaps/qna";
    public static final String QNA_WRITE_SCHEME = SnapsAPI.WEB_DOMAIN() + "/mw/cs";
    public static final String DETAIL_PRODUCT_SCHEME = SnapsAPI.WEB_DOMAIN() + "/mw/v3/store/product/detail";
    public static final String SEARCH_PRODUCT_SCHEME = SnapsAPI.WEB_DOMAIN() + "/mw/v3/store/product/search";
    public static final String SIZE_PRODUCT_SCHEME = SnapsAPI.WEB_DOMAIN() + "/mw/v3/store/product/submenu";

    public static final String ORDER_HISTORY_SCHEME = SnapsAPI.WEB_DOMAIN() + SnapsAPI.ORDER_URL() + "/?f_chnl_code=KOR0031";

    public static final String SUB_CATEGORY = "subCategory";
    public static final String PRODUCT_SUB_LIST = "isProductSubList";
    public static final String PRODUCT_SUB_LIST_PARAM = "&isProductSubList=true";
    public static final String CLSS_CODE = "classCode";
    public static final String PRODUCT_CODE = "productCode";
    public static final String TEMPLATE_CODE = "tmplCode";
    public static final String SCLSS_CODE = "sclssCode";
    public static final String MCLSS_CODE = "F_MCLSS_CODE";
    public static final String F_CLSS_CODE = "F_CLSS_CODE";
    public static final String F_TMPL_CODE = "F_TMPL_CODE";
    public static final String F_PROD_CODE = "F_PROD_CODE";
    public static final String F_OUTER_YORN = "F_OUTER_YORN";

    private static final String SNAPS_HOME_MENU_CLICK_ANALYSIS_PARAM_SOURCE = "utm_source";
    private static final String SNAPS_HOME_MENU_CLICK_ANALYSIS_PARAM_MEDIUM = "utm_medium";
    private static final String SNAPS_HOME_MENU_CLICK_ANALYSIS_PARAM_CAMPAIGN = "utm_campaign";
    private static final String SNAPS_HOME_MENU_CLICK_ANALYSIS_PARAM_TERM = "utm_term";

    public static final int TYPE_LIST = 0;
    public static final int TYPE_DETAIL = 1;
    public static final int TYPE_SIZE = 2;
    public static final int TYPE_ALERT = 3;

    protected final String PRODUCT_DATA = "product_data";

    protected String prodKey = "";
    protected HashMap<String, String> urlData;
    protected Activity activity;
    protected ArrayList<PhotoPrintProductInfo> mData;
    protected IKakao kakao;
    protected IFacebook facebook;

    protected int currentHome = 0;
    protected String url;

    protected SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas = null;

    protected OnBadgeCountChangeListener onBadgeCountListener = null;

    public SnapsWebEventBaseHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        this.activity = activity;
        this.handleDatas = handleDatas;

        if (handleDatas == null) return;

        url = handleDatas.getUrl();

        urlData = Config.ExtractWebURL(url);
        if (urlData == null) return;

        String tempProdKey = urlData.get(Const_EKEY.WEB_PRODCODE_KEY);
        prodKey = tempProdKey == null ? "" : tempProdKey;

        kakao = handleDatas.getKakao();
        if (kakao == null)
            kakao = SnsFactory.getInstance().queryIntefaceKakao();

        facebook = handleDatas.getFacebook();
        if (facebook == null)
            facebook = SnsFactory.getInstance().queryInteface();

//        REQUEST_CODE_PAYMENT = handleDatas.getREQUEST_CODE_PAYMENT();

        printClassName();
    }

    public static String findHost(String url) {
        if (url == null || !url.contains("?") || !url.startsWith(SnapsWebEventBaseHandler.SCHMA)) return null;
        return url.substring(SnapsWebEventBaseHandler.SCHMA.length(), url.indexOf("?"));
    }

    public void setOnBadgeCountListener(OnBadgeCountChangeListener onBadgeCountListener) {
        this.onBadgeCountListener = onBadgeCountListener;
    }

    public OnBadgeCountChangeListener getOnBadgeCountListener() {
        return onBadgeCountListener;
    }

    public void sendHomeMenuClickAnalysis() {
        if (urlData == null || activity == null) return;
        try {
            String[] arParams = { SNAPS_HOME_MENU_CLICK_ANALYSIS_PARAM_SOURCE, SNAPS_HOME_MENU_CLICK_ANALYSIS_PARAM_MEDIUM, SNAPS_HOME_MENU_CLICK_ANALYSIS_PARAM_CAMPAIGN, SNAPS_HOME_MENU_CLICK_ANALYSIS_PARAM_TERM };
            for (String param : arParams) {
                if (!urlData.containsKey(param)) return;
                String value = urlData.get(param);
                if (StringUtil.isEmpty(value)) return;
            }

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(activity);
            Tracker tracker = analytics.newTracker("UA-62310709-2");
            tracker.send(new HitBuilders.EventBuilder().setCampaignParamsFromUrl(url).build());
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    public abstract boolean handleEvent();

    public abstract void printClassName();

    public static void sendPageEventTracker( Activity activity, String url ) {
        if( !(activity instanceof CatchFragmentActivity) || SnapsTPAppManager.isThirdPartyApp(activity) ) return;

        int titleResId = -1;

        HashMap<String, String> urlData = parseUrl( url );
//        Set<String> keySet = urlData.keySet();
//        Object[] ary = keySet.toArray();

        if( urlData.containsKey("subCategory") ) {
            if( "KOR0031006001000".equals(urlData.get("subCategory")) ) titleResId = R.string.item_phone_case;
            else if( "KOR0031005001000".equals(urlData.get("subCategory")) ) titleResId = R.string.item_wood_frame;
            else if( "KOR0031005003000".equals(urlData.get("subCategory")) ) titleResId = R.string.item_metal_frame;
            else if( "KOR0031005004000".equals(urlData.get("subCategory")) ) titleResId = R.string.item_interior_frame;
            else if( "KOR0031005002000".equals(urlData.get("subCategory")) ) titleResId = R.string.item_marble_frame;
            else if( "KOR0031005005000".equals(urlData.get("subCategory")) ) titleResId = R.string.item_hanging_frame;
            else if( "KOR0031004008000".equals(urlData.get("subCategory")) ) titleResId = R.string.item_christmas_card;
            else if( "KOR0031004005000".equals(urlData.get("subCategory")) ) titleResId = R.string.item_new_year_card;
            else if( "KOR0031004009000".equals(urlData.get("subCategory")) ) titleResId = R.string.item_wedding_invitation_card;
            else if( "KOR0031004002000".equals(urlData.get("subCategory")) ) titleResId = R.string.item_thanks_card;
            else if( "KOR0031004003000".equals(urlData.get("subCategory")) ) titleResId = R.string.item_congraturation_card;
            else if( "KOR0031004001000".equals(urlData.get("subCategory")) ) titleResId = R.string.item_invitation_card;
            else if( "KOR0031004006000".equals(urlData.get("subCategory")) ) titleResId = R.string.item_special_day_card;
            else if( "KOR0031007001000".equals(urlData.get("subCategory")) ) titleResId = R.string.item_calendar_table;
        }
        else if( url.contains("/mw/v3/store/product") || (urlData.containsKey("address") && urlData.get("address").contains("detail")) ) {
            String /*P_CODE, S_CODE, */M_CODE; // 다른 코드들도 필요하면 추가.
            M_CODE = urlData.containsKey("F_MCLSS_CODE") ? urlData.get("F_MCLSS_CODE") : urlData.get( "mclssCode") ;
            if( "001001006008".equals(M_CODE) ) titleResId = R.string.item_photobook;
            else if( "001001016002".equals(M_CODE) ) titleResId = R.string.item_facebookphotobook;
            else if( "001001012002".equals(M_CODE) ) titleResId = R.string.item_kakaostorybook;
            else if( "001001007002".equals(M_CODE) ) titleResId = R.string.item_theme_photobook;
            else if( "001002001001".equals(M_CODE) ) titleResId = R.string.item_normal_print;
            else if( "001002002001".equals(M_CODE) ) titleResId = R.string.item_wallet_picture;
            else if( "001002002005".equals(M_CODE) ) titleResId = R.string.item_polaroid;
            else if( "001003009001".equals(M_CODE) ) titleResId = R.string.item_square_printpackage;
            else if( "001007005006".equals(M_CODE) ) titleResId = R.string.item_phone_case;
            else if( "001005002001".equals(M_CODE) ) titleResId = R.string.item_photo_stickerkit;
            else if( "001007006001".equals(M_CODE) ) titleResId = R.string.item_photo_mugcup;
            else if( "001007008001".equals(M_CODE) ) titleResId = R.string.item_photo_tumbler;
            else if( "001007001001".equals(M_CODE) ) titleResId = R.string.item_design_note;
            else if( "001007007001".equals(M_CODE) ) titleResId = R.string.item_mouse_pad;
            else if( "001004002003".equals(M_CODE) ) titleResId = R.string.item_calendar_wall_hanging;
            else if( "001004003002".equals(M_CODE) ) titleResId = R.string.item_calendar_scheduler;
        }
        else if( urlData != null && urlData.get("scheme") != null && urlData.get("scheme").contains("snapsapp") ) {
            if( "00800100010001".equals(urlData.get("productCode")) ) titleResId = R.string.item_3x5;
            else if( "00800100010003".equals(urlData.get("productCode")) ) titleResId = R.string.item_4x6;
            else if( "00800100010005".equals(urlData.get("productCode")) ) titleResId = R.string.item_5x7;
            else if( "00800100010006".equals(urlData.get("productCode")) ) titleResId = R.string.item_8x10;
            else if( "00800100010009".equals(urlData.get("productCode")) ) titleResId = R.string.item_11x14;
            else if( "00800500020001".equals(urlData.get("productCode")) ) titleResId = R.string.item_wallet_picture;
            else if( "00800500080001".equals(urlData.get("productCode")) ) titleResId = R.string.item_polaroid;
            else if( "00800900150001".equals(urlData.get("productCode")) ) titleResId = R.string.item_woodblock_print;
            else if( "00800900160001".equals(urlData.get("productCode")) ) titleResId = R.string.item_ddebooji_kit;
            else if( "00800900170001".equals(urlData.get("productCode")) ) titleResId = R.string.item_postcard_package;
            else if( "00800900190001".equals(urlData.get("productCode")) ) titleResId = R.string.item_polaroid_print_package;
            else if( "00800900180001".equals(urlData.get("productCode")) ) titleResId = R.string.item_4x4;
            else if( "00800900180002".equals(urlData.get("productCode")) ) titleResId = R.string.item_5x5;
            else if( "00800700030003".equals(urlData.get("productCode")) ) titleResId = R.string.item_calendar_original_horizontal;
            else if( "00800700030005".equals(urlData.get("productCode")) ) titleResId = R.string.item_calendar_original_vertical;
            else if( "00800700050003".equals(urlData.get("productCode")) ) titleResId = R.string.item_calendar_mini;
            else if( "00800700030004".equals(urlData.get("productCode")) ) titleResId = R.string.item_calendar_large;
            else if( "00800700060002".equals(urlData.get("productCode")) ) titleResId = R.string.item_calendar_small_horizontal;
            else if( "00800700050004".equals(urlData.get("productCode")) ) titleResId = R.string.item_calendar_small_vertical;
        }

        if( titleResId > 0 ) {
            String trackerTitle = activity.getString( titleResId );
            if( trackerTitle.length() > 0 ) ( (CatchFragmentActivity)activity ).sendPageEvent( trackerTitle );
        }

        saveMenuClickHistoryBySnapsLogger(url);

        if (urlData.containsKey("utm_source") && urlData.containsKey("utm_term")) {
            String source = urlData.get("utm_source");
            String term = urlData.get("utm_term");
            if (!StringUtil.isEmpty(source) && source.contains("home") && !StringUtil.isEmpty(term) && term.contains("aiphotobook")) {
                SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.home_event_annie_click));
            }
        }
    }

    private static void saveMenuClickHistoryBySnapsLogger(String url) {
        SnapsLogger snapsLogger = SnapsLogger.getInstance();
        snapsLogger.saveMenuClickHistoryBySnapsLogger(url);
    }

    private static HashMap<String, String> parseUrl( String url ) {
        HashMap<String, String> params = new HashMap<String, String>();

        int index = url.indexOf("://");
        if( index > 0 ) {
            String scheme = url.substring(0, url.indexOf("://"));
            params.put( "scheme", scheme );

            if( url.length() > index + 3 ) {
                url = url.substring( url.indexOf("://") + 3, url.length() );
                index = url.indexOf( "?" );
                String paramStr = "";
                if( index > 0 ) {
                    params.put( "address", url.substring(0, index) );
                    paramStr = url.substring( index + 1, url.length() );
                }
                else paramStr = url;

                String[] temp = paramStr.split( "&" );
                for( int i = 0; i < temp.length; ++i ) {
                    index = temp[i].indexOf( "=" );
                    if( index > 0 && index + 1 < temp[i].length() ) {
                        params.put( temp[i].substring(0, index), temp[i].substring(index + 1, temp[i].length()) );
                    }
                }
            }
        }

        return params;
    }

    void sendWebCommand(WebView webView, String cmdText) {
        if(webView != null)
            webView.loadUrl(cmdText, SystemUtil.getWebviewVersionMapData(activity));
    }

    protected String createPaymentUrl(String orderCode, String sttleMethod) {
        List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
        getParameters.add(new BasicNameValuePair("f_order_code", orderCode));
        getParameters.add(new BasicNameValuePair("f_sttl_mthd", sttleMethod));

        String userNo = Setting.getString(activity, Const_VALUE.KEY_SNAPS_USER_NO);
        String billingUrl = "";
        if (!SnapsTPAppManager.isThirdPartyApp(activity))
            billingUrl = SnapsAPI.WEB_DOMAIN(SnapsAPI.NEW_BILLING_KR_INIPAY_URL, userNo, "") + "&" + URLEncodedUtils.format(getParameters, "utf-8") + "&F_CHNL_CODE=" + Config.getCHANNEL_CODE()
                    + "&scheme=" + Config.getPaymentScheme(activity);
        else
            billingUrl = SnapsAPI.WEB_DOMAIN(SnapsAPI.NEW_BILLING_KR_INIPAY_URL, "", Config.getFF_UUSERID()) + "&" + URLEncodedUtils.format(getParameters, "utf-8") + "&scheme="
                    + Config.getPaymentScheme(activity);

        Dlog.d("createPaymentUrl() billingUrl:" + billingUrl);
        return billingUrl;
    }

    public void initPhotoPrint() {
        mData = PhotoPrintProductInfo.getPhotoPrintTemplate(activity);
    }

    protected void dismissPopupWebViewActivity() {
        SnapsMenuManager menuManager = SnapsMenuManager.getInstance();
        if (menuManager != null) {
            PopupWebviewActivity popupWebviewActivity = menuManager.getPopupWebViewActivity();
            if (popupWebviewActivity != null && !popupWebviewActivity.isFinished()) {
                popupWebviewActivity.finish();
            }
        }
    }
}
