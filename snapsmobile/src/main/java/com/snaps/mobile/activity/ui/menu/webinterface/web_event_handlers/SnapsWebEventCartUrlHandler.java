package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.snaps.common.trackers.SnapsAppsFlyer;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.IPostingResult;
import com.snaps.common.utils.ui.IntentUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.VoidContainsBoolean;
import com.snaps.kakao.utils.share.SNSShareUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.DetailProductWebviewActivity;
import com.snaps.mobile.activity.webview.IUIWebViewActBridge;
import com.snaps.mobile.activity.webview.PopupWebviewActivity;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;
import com.snaps.mobile.activity.webview.WebviewActivity;
import com.snaps.mobile.activity.webview.ZoomProductWebviewActivity;
import com.snaps.mobile.utils.pref.PrefUtil;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Currency;
import java.util.HashMap;
import java.util.Hashtable;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ysjeong on 16. 8. 12..
 */

//TODO  대충 옮겨 놨으니까, 여유 있을때, 제대로 UIWebViewActivity와 분할 시키는 작업을 진행 하자..
public class SnapsWebEventCartUrlHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventCartUrlHandler.class.getSimpleName();
    //    private HashSet<ImpWebViewProcess> processes = null;
//    private ObserveScrollingWebView observerWebView = null;
    private String url;
    private WebView webview;
    //    private ISnapsWebViewLoadListener snapsWebViewLoadListener = null;
    private String DIALOG_CARDNM = "";
    private static final int DIALOG_ISP = 2;
    private static final int DIALOG_CARDAPP = 3;
    private AlertDialog alertIsp;
    private IUIWebViewActBridge uiWebViewActBridge = null;
    public static final int CART_REQUEST_CODE_PAYMENT = 900;

    public static SnapsWebEventCartUrlHandler createHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas, IUIWebViewActBridge uiWebViewActBridge) {
        SnapsWebEventCartUrlHandler webEventCartUrlHandler = new SnapsWebEventCartUrlHandler(activity, handleDatas);
        webEventCartUrlHandler.setUiWebViewActBridge(uiWebViewActBridge);
        return webEventCartUrlHandler;
    }

    public IUIWebViewActBridge getUiWebViewActBridge() {
        return uiWebViewActBridge;
    }

    public void setUiWebViewActBridge(IUIWebViewActBridge uiWebViewActBridge) {
        this.uiWebViewActBridge = uiWebViewActBridge;
    }

    private SnapsWebEventCartUrlHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
//        this.processes = handleDatas.getProcesses();
//        this.observerWebView = handleDatas.getObserveScrollingWebView();
        this.url = handleDatas.getUrl();
        this.webview = handleDatas.getWebview();
//        this.snapsWebViewLoadListener = handleDatas.getSnapsWebViewLoadListener();
    }

    @Override
    public boolean handleEvent() {
        // 주문완료 GA 전송.
        if (url.contains("/order/complete.jsp") && url.contains("orderCode")) {
            handleOrderCompleteUrl(url);
        }

        if (url.startsWith(SnapsWebEventBaseHandler.SCHMA)) {
            VoidContainsBoolean result = handleSnapsSchemeUrl(webview, url);
            if (result != null && result.getResult() != null) {
                switch (result.getResult()) {
                    case TRUE:
                        return true;
                    case FALSE:
                        return false;
                }
            }
        } else if (!SnapsTPAppManager.isThirdPartyApp(activity) && url.startsWith("snapskr")) {
            paymentResult(url);
            return true;
        } else if (Config.isSnapsSDK2(activity) && url.startsWith(Config.getPaymentScheme(activity))) {
            paymentResult(url);
            return true;
        } else if (Config.isSnapsBitween(activity) && url.startsWith(Config.getPaymentScheme(activity))) {
            paymentResult(url);
            return true;
        } else if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) { //URL별로 분기가 필요합니다. 어플리케이션을 로딩하는것과 WEB PAGE를 로딩하는것을 분리 하여 처리해야 합니다. 만일 가맹점 특정 어플 URL이 들어온다면 조건을 더 추가하여 처리해 주십시요.
            VoidContainsBoolean result = handleJavaScriptUrl(webview, url);
            if (result != null && result.getResult() != null) {
                switch (result.getResult()) {
                    case TRUE:
                        return true;
                    case FALSE:
                        return false;
                }
            }
            //            https://jp.snaps.com/payment/close?message=This payment has% been cancelled.
        } else if (url.contains("/payment/close?message=This%2Bpayment%2Bhas%2Bbeen%2Bcancelled.")) {
            //일본 엑심페이에서 취소 후 팝업이 닫힐 때, 호출되는 주소를 하드코딩.
            return false;

        } else {
            webview.loadUrl(url, SystemUtil.getWebviewVersionMapData(activity));
        }

        return true;
    }

    private void handleOrderCompleteUrl(String url) {
        sendActionEvent(activity.getString(R.string.order_complete));
//        SnapsAdbrix.orderComplete();
        String amount = new UrlQuerySanitizer(url).getValue("totalPrice");
        if (!StringUtil.isEmpty(amount)) {
            try {
                double dAmount = Double.parseDouble(amount.replaceAll(",", ""));
//                SnapsAdbrix.buyAmount(activity, url);
                SnapsAppsFlyer.setInappEvent(activity, url);
                int iAmount = Integer.parseInt(amount.replaceAll(",", ""));
                AdWordsConversionReporter.reportWithConversionId(activity.getApplicationContext(), "943955751", "enxKCJDK9GoQp76OwgM", amount, true);
                AppEventsLogger logger = AppEventsLogger.newLogger(activity);
                logger.logPurchase(BigDecimal.valueOf(dAmount), Currency.getInstance("KRW"));
            } catch (NumberFormatException e) {
                Dlog.e(TAG, e);
            }
        }
    }

    private VoidContainsBoolean handleSnapsSchemeUrl(final WebView view, String url) {
        VoidContainsBoolean result = new VoidContainsBoolean();
        HashMap<String, String> hashmap = Config.ExtractWebURL(url);

        if (hashmap == null)
            result.setResult(VoidContainsBoolean.eResult.FALSE);

        /**
         * TODO SnapsShouldOverideLoader와 통합하려고 했는데, 다르게 동작하는 부분이 많아서,
         * 현재 상태를 유지함.
         */
        String cmdKey = SnapsWebEventBaseHandler.findHost(url);
        if (cmdKey == null || cmdKey.length() < 1) {
            if (hashmap.containsKey(Const_EKEY.WEB_CMD_KEY)) {
                cmdKey = hashmap.get(Const_EKEY.WEB_CMD_KEY);
            }
        }

        if (cmdKey == null || cmdKey.length() < 1)
            result.setResult(VoidContainsBoolean.eResult.FALSE);

        if (cmdKey.equals("preview")) {
            SnapsWebEventCartProductLoadHandler cartProductLoadHandler = new SnapsWebEventCartProductLoadHandler(activity, handleDatas); //TODO .. 얘도 리펙토링 대상...
            cartProductLoadHandler.handleEvent();
        } else if ("presentPayment".equals(cmdKey)) {
            Intent intent = RenewalHomeActivity.getIntent(activity, activity.getString(R.string.payment_short), url);
            // 호출할 url를 만들어서 보낸다.
            String orderCode = hashmap.get(Const_EKEY.WEB_ORDER_CODE_KEY);
            String sttlemethod = hashmap.get(Const_EKEY.WEB_STTLEMETHOD_KEY);

            intent.putExtra(CALL_URL, createPaymentUrl(orderCode, sttlemethod));

            // 주문번호를 저장한다.
            Setting.set(activity, "ordercode", orderCode);

            activity.startActivityForResult(intent, CART_REQUEST_CODE_PAYMENT);
        } else if ("goDelivery".equals(cmdKey)) {  // 배송정보화면으로 이동하기...
            IntentUtil.sendToDeliveryActivity(activity);
            //주문 완료 후 배송 조회로 가면 액비비티를 닫고 간다.
            activity.finish();
            result.setResult(VoidContainsBoolean.eResult.TRUE);
        } else if ("cartCount".equals(cmdKey)) {
            String cartCount = hashmap.get(Const_EKEY.WEB_CARTCOUNT_KEY);

            if ("0".equals(cartCount)) {
                getUiWebViewActBridge().getBtnEdit().setVisibility(View.GONE);
                getUiWebViewActBridge().getBtnComplete().setVisibility(View.GONE);
            } else {
                getUiWebViewActBridge().getBtnEdit().setVisibility(View.GONE);
            }
            result.setResult(VoidContainsBoolean.eResult.TRUE);
        } else if ("count".equals(cmdKey)) {
            try {
                String cartCount = hashmap.get(Const_EKEY.WEB_CARTCOUNT_KEY);
                if (cartCount != null && cartCount.length() > 0)
                    PrefUtil.saveCartCount(activity, cartCount);

                String couponCount = hashmap.get(Const_EKEY.WEB_COUPON_COUNT_KEY);
                if (couponCount != null && couponCount.length() > 0)
                    Setting.set(activity, Const_VALUE.KEY_COUPON_COUNT, Integer.parseInt(couponCount));
            } catch (NumberFormatException e) {
                Dlog.e(TAG, e);
            }
            result.setResult(VoidContainsBoolean.eResult.TRUE);
        } else if ("appFinish".equals(cmdKey)) {
            Intent ittLogin = new Intent(activity, RenewalHomeActivity.class);
            ittLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PrefUtil.clearUserInfo(activity, true);// 모든정보 초기화
            activity.startActivity(ittLogin);
            activity.finish();
        } else if ("openBrowser".equals(cmdKey)) {
            SnapsWebEventOpenBrowserHandler handler = new SnapsWebEventOpenBrowserHandler(activity, handleDatas);
            handler.handleEvent();
        } else if (WebViewCmdGotoPage.gotoPage(activity, hashmap, url)) {
        } else if (!Config.isSnapsSDK2(activity) && getUiWebViewActBridge().getKakaoStoryPostingEventor().isStoryPostingEvent(hashmap, view, url)) {
        } else if (cmdKey.equals("snsShare")) {
            if (!SnapsTPAppManager.isThirdPartyApp(activity)) {
                getUiWebViewActBridge().setSnsShareCallBack(SNSShareUtil.sendSNSShareContents(activity, hashmap, new IPostingResult() {
                    @Override
                    public void OnPostingComplate(boolean isSucess, String errMsg) {
                        if (!SnapsTPAppManager.isThirdPartyApp(activity))
                            SNSShareUtil.loadSnsShareCallBack(isSucess, errMsg, getUiWebViewActBridge().getSnsShareCallBack(), webview);
                    }
                }));
            }
        } else if (cmdKey.equals("openAppPopup")) {// FIXME snapsWebviewProcess랑 합쳐야되는데..
            String openurl = hashmap.get("openUrl");

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
                String title = hashmap.get(Const_EKEY.WEB_TITLE_KEY);
                try {
                    if (title != null) {
                        title = URLDecoder.decode(title, "utf-8");
                    } else
                        title = "";
                } catch (UnsupportedEncodingException e) {
                    Dlog.e(TAG, e);
                }
                openurl = SnapsTPAppManager.getSnapsWebDomain(activity, openurl, SnapsTPAppManager.getBaseQuary(activity, false));
                Intent intent = ZoomProductWebviewActivity.getIntent(activity, title, openurl, true);
                intent.putExtra("orientation", hashmap.get("orientation"));
                activity.startActivity(intent);
                result.setResult(VoidContainsBoolean.eResult.TRUE);
            } else if (openurl.startsWith("http")) {
                Dlog.d("handleSnapsSchemeUrl() openUrl:" + openurl);
                String title = "";
                try {
                    String naviTitle = hashmap.get(Const_EKEY.WEB_NAVIBARTITLE_KEY);
                    if (naviTitle != null && naviTitle.length() > 0)
                        title = URLDecoder.decode(naviTitle, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    Dlog.e(TAG, e);
                }

                Intent intent = WebviewActivity.getIntent(activity, (title == null) ? "" : title, openurl);
                intent.putExtra("isEnableBack", true);
                activity.startActivity(intent);
                result.setResult(VoidContainsBoolean.eResult.TRUE);
            }
            openurl = SnapsTPAppManager.getSnapsWebDomain(activity, openurl, SnapsTPAppManager.getBaseQuary(activity, false));

            PopupWebviewActivity.IPopupDialogFragmentCallback callback = new PopupWebviewActivity.IPopupDialogFragmentCallback() {
                @Override
                public void onReceived(String msg) {
                    if (msg != null) {
                        if (msg.startsWith("snapsapp"))
                            getUiWebViewActBridge().shouldOverrideUrlLoading(view, msg);
                        else {
                            if (msg.startsWith(SnapsWebEventBaseHandler.DETAIL_PRODUCT_SCHEME) || Const_PRODUCT.checkDetailProduct(msg)) {
                                String titleStr = StringUtil.getTitleAtUrl(msg, "naviBarTitle");
                                if (titleStr == null || titleStr.length() < 1)
                                    titleStr = StringUtil.getTitleAtUrl(msg, "F_TMPL_NAME");

                                Intent intent = DetailProductWebviewActivity.getIntent(activity, titleStr != null && titleStr.length() > 0 ? titleStr : "디자인 상세",
//												UrlUtil.getTopMarginUrl(activity, false, msg), true, true, SnapsMenuManager.eHAMBURGER_ACTIVITY.PRODUCT_DETAIL_PAGE);
                                        msg, true, true, SnapsMenuManager.eHAMBURGER_ACTIVITY.PRODUCT_DETAIL_PAGE);
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                            } else
                                sendWebCommand(view, msg);
                        }
                    }
                }
            };

            Intent intent = PopupWebviewActivity.getIntent(activity, callback, openurl);
            if (intent != null)
                activity.startActivity(intent);
        }

        return result;
    }

    private VoidContainsBoolean handleJavaScriptUrl(WebView view, String url) {
        VoidContainsBoolean result = new VoidContainsBoolean();
        Intent intent = null;

        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            Dlog.d("handleJavaScriptUrl() intent.getDataString():" + intent.getDataString());

        } catch (URISyntaxException ex) {
            Dlog.e(TAG, "<INIPAYMOBILE> URI syntax error : " + url + ":" + ex.getMessage(), ex);
            result.setResult(VoidContainsBoolean.eResult.FALSE);
        }

        Uri uri = Uri.parse(intent.getDataString());
        intent = new Intent(Intent.ACTION_VIEW, uri);

        try {

            activity.startActivity(intent);

        } catch (ActivityNotFoundException e) {
            String errorMsg = "INIPAYMOBILE, ActivityNotFoundException INPUT >> " + url;
            errorMsg += "\n";
            errorMsg += "INIPAYMOBILE, uri.getScheme()" + intent.getDataString();
            Dlog.e(TAG, errorMsg, e);

            // ISP
            if (url.startsWith("ispmobile://")) {
                String ispUrl = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp";
                view.loadUrl(ispUrl, SystemUtil.getWebviewVersionMapData(activity));
                result.setResult(VoidContainsBoolean.eResult.FALSE);
            }

            // 현대앱카드
            else if (intent.getDataString().startsWith("hdcardappcardansimclick://")) {
                DIALOG_CARDNM = "HYUNDAE";
                Dlog.i(TAG, "handleJavaScriptUrl() 현대앱카드설치");
                showAlertDialog(DIALOG_CARDAPP);
                result.setResult(VoidContainsBoolean.eResult.TRUE);
            }

            // 신한앱카드
            else if (intent.getDataString().startsWith("shinhan-sr-ansimclick://")) {
                DIALOG_CARDNM = "SHINHAN";
                Dlog.i(TAG, "handleJavaScriptUrl() 신한카드앱설치");
                showAlertDialog(DIALOG_CARDAPP);
                result.setResult(VoidContainsBoolean.eResult.TRUE);
            }

            // 삼성앱카드
            else if (intent.getDataString().startsWith("mpocket.online.ansimclick://")) {
                DIALOG_CARDNM = "SAMSUNG";
                Dlog.i(TAG, "handleJavaScriptUrl() 삼성카드앱설치");
                showAlertDialog(DIALOG_CARDAPP);
                result.setResult(VoidContainsBoolean.eResult.TRUE);
            }

            // 롯데앱카드
            else if (intent.getDataString().startsWith("lottesmartpay://")) {
                DIALOG_CARDNM = "LOTTE";
                Dlog.i(TAG, "handleJavaScriptUrl() 롯데카드앱설치");
                showAlertDialog(DIALOG_CARDAPP);
                result.setResult(VoidContainsBoolean.eResult.TRUE);
            }

            // KB앱카드
            else if (intent.getDataString().startsWith("kb-acp://")) {
                DIALOG_CARDNM = "KB";
                Dlog.i(TAG, "handleJavaScriptUrl() KB카드앱설치");
                showAlertDialog(DIALOG_CARDAPP);
                result.setResult(VoidContainsBoolean.eResult.TRUE);
            }

            // 하나SK카드 통합안심클릭앱
            else if (intent.getDataString().startsWith("hanaansim://")) {
                DIALOG_CARDNM = "HANASK";
                Dlog.i(TAG, "handleJavaScriptUrl() 하나카드앱설치");
                showAlertDialog(DIALOG_CARDAPP);
                result.setResult(VoidContainsBoolean.eResult.TRUE);
            }
            // 씨티카드 보완... kikat(4.4.2)
            else if (intent.getDataString().startsWith("mvaccinestart://")) {
                Intent excepIntent = null;
                try {
                    excepIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException e1) {
                    Dlog.e(TAG, e1);
                }
                // 1.String m_szWebViewUrl 이 값을 통해 INITENT 정합성
                String packageNm = excepIntent.getPackage();
                // 2.해당 INITENT에서 패키지네임 추출
                excepIntent = new Intent(Intent.ACTION_VIEW);
                // 3.ACTION_VIEW로 옵션 설정
                excepIntent.setData(Uri.parse("market://search?q=" + packageNm));
                // 4.Uri.parse를 통해 마켓 스키마 설정
                activity.startActivity(excepIntent);
                result.setResult(VoidContainsBoolean.eResult.TRUE);
            } else if (url.startsWith("intent://")) {
                try {
                    Intent passedIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    String packageName = passedIntent.getPackage();
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("market://search?q=" + packageName));
                    activity.startActivity(sendIntent);
                } catch (URISyntaxException e1) {
                    Dlog.e(TAG, e1);
                }
            }
        }

        return result;
    }

    private void paymentResult(final String retValue) {
        ATask.executeVoid(new ATask.OnTask() {

            @Override
            public void onPre() {
            }

            @Override
            public void onPost() {

                //배지 카운터를 서버에서 조회하도록 요청한다.
                Setting.set(activity, Const_VALUE.KEY_SHOULD_REQUEST_BADGE_COUNT, true);

                if (getUiWebViewActBridge().isINISIS_Page()) {
                    Intent data = new Intent();
                    data.putExtra("returnValue", retValue);
                    activity.setResult(RESULT_OK, data);
                    activity.finish();
                } else {
                    getUiWebViewActBridge().setIsPaymentComplete(true);
                }
            }

            @Override
            public void onBG() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Dlog.e(TAG, e);
                }
            }
        });
    }

    //TODO  공통 메서드로 빼자...
    private void sendActionEvent(String message) {
        if (SnapsTPAppManager.isThirdPartyApp(activity)) return;

        Intent intent = new Intent(Const_VALUE.SNAPS_EVENT_TRACKER_ACTION);

        intent.putExtra("event_category", "전환");
        intent.putExtra("event_action", message);

        activity.sendBroadcast(intent);
    }

    void showAlertDialog(int id) {
        switch (id) {
            case DIALOG_ISP:

                alertIsp = new AlertDialog.Builder(activity).setIcon(android.R.drawable.ic_dialog_alert).setTitle("알림")
                        .setMessage("모바일 ISP 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.").setPositiveButton("설치", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String ispUrl = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp";
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(activity, "(-1)" + activity.getString(R.string.canceled_payment), Toast.LENGTH_SHORT).show();
                            }

                        }).create();
                alertIsp.show();
                break;

            case DIALOG_CARDAPP:
                alertIsp = getCardInstallAlertDialog(DIALOG_CARDNM);
                alertIsp.show();
                break;

        }// end switch
    }

    private AlertDialog getCardInstallAlertDialog(final String coCardNm) {
        final Hashtable<String, String> cardNm = new Hashtable<String, String>();
        cardNm.put("HYUNDAE", "현대 앱카드");
        cardNm.put("SAMSUNG", "삼성 앱카드");
        cardNm.put("LOTTE", "롯데 앱카드");
        cardNm.put("SHINHAN", "신한 앱카드");
        cardNm.put("KB", "국민 앱카드");
        cardNm.put("HANASK", "하나SK 통합안심클릭");

        final Hashtable<String, String> cardInstallUrl = new Hashtable<String, String>();
        cardInstallUrl.put("HYUNDAE", "market://details?id=com.hyundaicard.appcard");
        cardInstallUrl.put("SAMSUNG", "market://details?id=kr.co.samsungcard.mpocket");
        cardInstallUrl.put("LOTTE", "market://details?id=com.lotte.lottesmartpay");
        cardInstallUrl.put("SHINHAN", "market://details?id=com.shcard.smartpay");
        cardInstallUrl.put("KB", "market://details?id=com.kbcard.cxh.appcard");
        cardInstallUrl.put("HANASK", "market://details?id=com.ilk.visa3d");

        AlertDialog alertCardApp = new AlertDialog.Builder(activity).setIcon(android.R.drawable.ic_dialog_alert).setTitle("알림")
                .setMessage(cardNm.get(coCardNm) + " 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.").setPositiveButton("설치", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String installUrl = cardInstallUrl.get(coCardNm);
                        Uri uri = Uri.parse(installUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        Dlog.d("<INIPAYMOBILE> Call : " + uri.toString());
                        try {
                            activity.startActivity(intent);
                        } catch (ActivityNotFoundException anfe) {
                            Toast.makeText(activity, cardNm.get(coCardNm) + "설치 url이 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(activity, "(-1)" + activity.getString(R.string.canceled_payment), Toast.LENGTH_SHORT).show();
                    }
                }).create();

        return alertCardApp;

    }// end getCardInstallAlertDialog

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
