package com.snaps.mobile.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.snaps.common.utils.constant.Config;

import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.net.URISyntaxException;
import java.util.Hashtable;

public class PaymentWebviewProcess implements ImpWebViewProcess {
    private static final String TAG = PaymentWebviewProcess.class.getSimpleName();
    private static final int DIALOG_ISP = 2;
    private static final int DIALOG_CARDAPP = 3;
    private static String DIALOG_CARDNM = "";

    private AlertDialog alertIsp;
    Activity activity = null;

    boolean returnVaule = false;

    OnPaymentListener paymentListener = null;

    public PaymentWebviewProcess(Activity activity) {
        this.activity = activity;
    }

    public void setPaymentListener(OnPaymentListener paymentListener) {
        this.paymentListener = paymentListener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // TODO Auto-generated method stub
        if (!SnapsTPAppManager.isThirdPartyApp(activity.getApplicationContext()) && url.startsWith("snapskr")) {
            if (paymentListener != null)
                paymentListener.onPaymentResult(url);
            returnVaule = true;
            return true;

        } else if (url.startsWith(Config.getPaymentScheme(activity.getApplicationContext()))) {
            if (paymentListener != null)
                paymentListener.onPaymentResult(url);
            returnVaule = true;
            return true;
        } else if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
            Intent intent;

            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                Dlog.d("shouldOverrideUrlLoading() intent.getDataString():" + intent.getDataString());
            } catch (URISyntaxException ex) {
                Dlog.e(TAG, "<INIPAYMOBILE> URI syntax error : " + url + ":" + ex.getMessage(), ex);
                returnVaule = false;
                return true;
            }

            Uri uri = Uri.parse(intent.getDataString());
            intent = new Intent(Intent.ACTION_VIEW, uri);

            try {

                activity.startActivity(intent);

                /*
                 * 가맹점의 사정에 따라 현재 화면을 종료하지 않아도 됩니다. 삼성카드 기타 안심클릭에서는 종료되면 안되기 때문에 조건을 걸어 종료하도록 하였습니다.
                 */
                if (url.startsWith("ispmobile://")) {
                }

            } catch (ActivityNotFoundException e) {
                String errorMsg = "INIPAYMOBILE, ActivityNotFoundException INPUT >> " + url;
                errorMsg += "\n";
                errorMsg += "INIPAYMOBILE, uri.getScheme()" + intent.getDataString();
                Dlog.e(TAG, errorMsg, e);

                // ISP
                if (url.startsWith("ispmobile://")) {
                    String ispUrl = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp";
                    view.loadUrl(ispUrl, SystemUtil.getWebviewVersionMapData(activity));
                    returnVaule = false;
                    return true;
                }

                // 현대앱카드
                else if (intent.getDataString().startsWith("hdcardappcardansimclick://")) {
                    DIALOG_CARDNM = "HYUNDAE";
                    Dlog.i(TAG, "handleJavaScriptUrl() 현대앱카드설치");
                    showAlertDialog(DIALOG_CARDAPP);
                    returnVaule = true;
                    return true;
                }

                // 신한앱카드
                else if (intent.getDataString().startsWith("shinhan-sr-ansimclick://")) {
                    DIALOG_CARDNM = "SHINHAN";
                    Dlog.i(TAG, "handleJavaScriptUrl() 신한카드앱설치");
                    showAlertDialog(DIALOG_CARDAPP);
                    returnVaule = true;
                    return true;
                }

                // 삼성앱카드
                else if (intent.getDataString().startsWith("mpocket.online.ansimclick://")) {
                    DIALOG_CARDNM = "SAMSUNG";
                    Dlog.i(TAG, "handleJavaScriptUrl() 삼성카드앱설치");
                    showAlertDialog(DIALOG_CARDAPP);
                    returnVaule = true;
                    return true;
                }

                // 롯데앱카드
                else if (intent.getDataString().startsWith("lottesmartpay://")) {
                    DIALOG_CARDNM = "LOTTE";
                    Dlog.i(TAG, "handleJavaScriptUrl() 롯데카드앱설치");
                    showAlertDialog(DIALOG_CARDAPP);
                    returnVaule = true;
                    return true;
                }

                // KB앱카드
                else if (intent.getDataString().startsWith("kb-acp://")) {
                    DIALOG_CARDNM = "KB";
                    Dlog.i(TAG, "handleJavaScriptUrl() KB카드앱설치");
                    showAlertDialog(DIALOG_CARDAPP);
                    returnVaule = true;
                    return true;
                }

                // 하나SK카드 통합안심클릭앱
                else if (intent.getDataString().startsWith("hanaansim://")) {
                    DIALOG_CARDNM = "HANASK";
                    Dlog.i(TAG, "handleJavaScriptUrl() 하나카드앱설치");
                    showAlertDialog(DIALOG_CARDAPP);
                    returnVaule = true;
                    return true;
                }

                // 씨티카드 보완... kikat(4.4.2)
                else if (intent.getDataString().startsWith("mvaccinestart://")) {
                    Intent excepIntent = null;
                    try {
                        excepIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    } catch (URISyntaxException e1) {
                        Dlog.e(TAG, e1);
                    }
                    // 1.String url 이 값을 통해 INITENT 정합성
                    String packageNm = excepIntent.getPackage();
                    // 2.해당 INITENT에서 패키지네임 추출
                    excepIntent = new Intent(Intent.ACTION_VIEW);
                    // 3.ACTION_VIEW로 옵션 설정
                    excepIntent.setData(Uri.parse("market://search?q=" + packageNm));
                    // 4.Uri.parse를 통해 마켓 스키마 설정
                    activity.startActivity(excepIntent);
                    returnVaule = true;
                    return true;

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
                    return true;
                }
            }
        }
        return false;
    }

    void showAlertDialog(int id) {
        switch (id) {
            case DIALOG_ISP:

                alertIsp = new AlertDialog.Builder(activity.getApplicationContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle("알림").setMessage("모바일 ISP 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.").setPositiveButton("설치", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String ispUrl = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp";
                    }
                }).setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(activity.getApplicationContext(), "(-1)" + activity.getString(R.string.canceled_payment), Toast.LENGTH_SHORT).show();
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

        AlertDialog alertCardApp = new AlertDialog.Builder(activity.getApplicationContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle("알림").setMessage(cardNm.get(coCardNm) + " 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.")
                .setPositiveButton("설치", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String installUrl = cardInstallUrl.get(coCardNm);
                        Uri uri = Uri.parse(installUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        Dlog.d("<INIPAYMOBILE> Call : " + uri.toString());
                        try {
                            activity.startActivity(intent);
                        } catch (ActivityNotFoundException anfe) {
                            Toast.makeText(activity.getApplicationContext(), cardNm.get(coCardNm) + "설치 url이 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(activity.getApplicationContext(), "(-1)" + activity.getString(R.string.canceled_payment), Toast.LENGTH_SHORT).show();
                    }
                }).create();

        return alertCardApp;

    }// end getCardInstallAlertDialog

    @Override
    public boolean getCheckProcess() {
        // TODO Auto-generated method stub
        return returnVaule;
    }
}
