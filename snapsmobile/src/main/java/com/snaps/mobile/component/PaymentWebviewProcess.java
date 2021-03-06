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
                 * ???????????? ????????? ?????? ?????? ????????? ???????????? ????????? ?????????. ???????????? ?????? ????????????????????? ???????????? ????????? ????????? ????????? ?????? ??????????????? ???????????????.
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

                // ???????????????
                else if (intent.getDataString().startsWith("hdcardappcardansimclick://")) {
                    DIALOG_CARDNM = "HYUNDAE";
                    Dlog.i(TAG, "handleJavaScriptUrl() ?????????????????????");
                    showAlertDialog(DIALOG_CARDAPP);
                    returnVaule = true;
                    return true;
                }

                // ???????????????
                else if (intent.getDataString().startsWith("shinhan-sr-ansimclick://")) {
                    DIALOG_CARDNM = "SHINHAN";
                    Dlog.i(TAG, "handleJavaScriptUrl() ?????????????????????");
                    showAlertDialog(DIALOG_CARDAPP);
                    returnVaule = true;
                    return true;
                }

                // ???????????????
                else if (intent.getDataString().startsWith("mpocket.online.ansimclick://")) {
                    DIALOG_CARDNM = "SAMSUNG";
                    Dlog.i(TAG, "handleJavaScriptUrl() ?????????????????????");
                    showAlertDialog(DIALOG_CARDAPP);
                    returnVaule = true;
                    return true;
                }

                // ???????????????
                else if (intent.getDataString().startsWith("lottesmartpay://")) {
                    DIALOG_CARDNM = "LOTTE";
                    Dlog.i(TAG, "handleJavaScriptUrl() ?????????????????????");
                    showAlertDialog(DIALOG_CARDAPP);
                    returnVaule = true;
                    return true;
                }

                // KB?????????
                else if (intent.getDataString().startsWith("kb-acp://")) {
                    DIALOG_CARDNM = "KB";
                    Dlog.i(TAG, "handleJavaScriptUrl() KB???????????????");
                    showAlertDialog(DIALOG_CARDAPP);
                    returnVaule = true;
                    return true;
                }

                // ??????SK?????? ?????????????????????
                else if (intent.getDataString().startsWith("hanaansim://")) {
                    DIALOG_CARDNM = "HANASK";
                    Dlog.i(TAG, "handleJavaScriptUrl() ?????????????????????");
                    showAlertDialog(DIALOG_CARDAPP);
                    returnVaule = true;
                    return true;
                }

                // ???????????? ??????... kikat(4.4.2)
                else if (intent.getDataString().startsWith("mvaccinestart://")) {
                    Intent excepIntent = null;
                    try {
                        excepIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    } catch (URISyntaxException e1) {
                        Dlog.e(TAG, e1);
                    }
                    // 1.String url ??? ?????? ?????? INITENT ?????????
                    String packageNm = excepIntent.getPackage();
                    // 2.?????? INITENT?????? ??????????????? ??????
                    excepIntent = new Intent(Intent.ACTION_VIEW);
                    // 3.ACTION_VIEW??? ?????? ??????
                    excepIntent.setData(Uri.parse("market://search?q=" + packageNm));
                    // 4.Uri.parse??? ?????? ?????? ????????? ??????
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

                alertIsp = new AlertDialog.Builder(activity.getApplicationContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle("??????").setMessage("????????? ISP ????????????????????? ???????????? ?????? ????????????. \n????????? ?????? ?????? ??? ????????????.\n????????? ????????? ????????? ?????? ?????????.").setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
        cardNm.put("HYUNDAE", "?????? ?????????");
        cardNm.put("SAMSUNG", "?????? ?????????");
        cardNm.put("LOTTE", "?????? ?????????");
        cardNm.put("SHINHAN", "?????? ?????????");
        cardNm.put("KB", "?????? ?????????");
        cardNm.put("HANASK", "??????SK ??????????????????");

        final Hashtable<String, String> cardInstallUrl = new Hashtable<String, String>();
        cardInstallUrl.put("HYUNDAE", "market://details?id=com.hyundaicard.appcard");
        cardInstallUrl.put("SAMSUNG", "market://details?id=kr.co.samsungcard.mpocket");
        cardInstallUrl.put("LOTTE", "market://details?id=com.lotte.lottesmartpay");
        cardInstallUrl.put("SHINHAN", "market://details?id=com.shcard.smartpay");
        cardInstallUrl.put("KB", "market://details?id=com.kbcard.cxh.appcard");
        cardInstallUrl.put("HANASK", "market://details?id=com.ilk.visa3d");

        AlertDialog alertCardApp = new AlertDialog.Builder(activity.getApplicationContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle("??????").setMessage(cardNm.get(coCardNm) + " ????????????????????? ???????????? ?????? ????????????. \n????????? ?????? ?????? ??? ????????????.\n????????? ????????? ????????? ?????? ?????????.")
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String installUrl = cardInstallUrl.get(coCardNm);
                        Uri uri = Uri.parse(installUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        Dlog.d("<INIPAYMOBILE> Call : " + uri.toString());
                        try {
                            activity.startActivity(intent);
                        } catch (ActivityNotFoundException anfe) {
                            Toast.makeText(activity.getApplicationContext(), cardNm.get(coCardNm) + "?????? url??? ???????????? ????????????", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
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
