package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

import static com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment.DIALOG_TYPE_ACCESSORY_SAVE_COMPLETE;
import static com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment.DIALOG_TYPE_ACCESSORY_SAVE_FAIL;

/**
 * Created by kimduckwon on 2018. 2. 12..
 */

public class SnapsSelectProductJunctionForAccessory implements ISnapsProductLauncher {
    private static final String TAG = SnapsSelectProductJunctionForAccessory.class.getSimpleName();

    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;

        String userNo = Setting.getString(activity, Const_VALUE.KEY_SNAPS_USER_NO);
        if (TextUtils.isEmpty(userNo)) {
            SnapsLoginManager.startLogInProcess(activity, Const_VALUES.LOGIN_P_LOGIN);
            return false;
        } else {
            HashMap<String, String> urlData = attribute.getUrlData();
            if (urlData == null) return false;
            //카운트 들어가면 받아야 하는것
            String count = urlData.get("projectCount");
            Config.setCARD_QUANTITY(TextUtils.isEmpty(count) ? "1" : count);

            String productCode = urlData.get("productCode");
            Config.setPROD_CODE(productCode);

            String templateCode = urlData.get(Const_EKEY.WEB_TEMPLE_KEY);
            String templateOptionCode = urlData.get("prmOptionTmplCode");
            String templateOptionName = urlData.get("prmOptionTmplName");
            addTocart(activity, productCode, templateCode, templateOptionCode, templateOptionName, userNo, count);
            return true;
        }


    }


    private void addTocart(final Activity activity, final String productCode, final String templateCode, final String templateOptionCode, final String templateOptionName, final String userNo, final String count) {
        AsyncTask<Void, Void, Void> aTask = new AsyncTask<Void, Void, Void>() {
            String resultMsg;

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    List<NameValuePair> getParameters = new ArrayList<NameValuePair>();

                    getParameters.add(new BasicNameValuePair("part", "mobile.SetData"));
                    getParameters.add(new BasicNameValuePair("cmd", "postCartByAccessory"));
                    getParameters.add(new BasicNameValuePair("channelCode", Config.getCHANNEL_CODE()));
                    getParameters.add(new BasicNameValuePair("productCode", productCode));
                    getParameters.add(new BasicNameValuePair("templateCode", templateCode));
                    getParameters.add(new BasicNameValuePair("templateOptionCode", templateOptionCode));
                    getParameters.add(new BasicNameValuePair("templateOptionName", templateOptionName));
                    getParameters.add(new BasicNameValuePair("projectCount", count));
                    getParameters.add(new BasicNameValuePair("userNo", userNo));
                    getParameters.add(new BasicNameValuePair("deviceCode", "190002"));
                    getParameters.add(new BasicNameValuePair("applicationVersion", Config.getAPP_VERSION()));

                    resultMsg = HttpUtil.connectPost(SnapsAPI.DOMAIN() + "/servlet/Command.do", getParameters, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                    int status = 0;
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                int status = 0;
                try {
                    JSONObject object = new JSONObject(resultMsg);
                    status = object.getInt("status");
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                } finally {
                    if (status == 200) {
                        completeDialog(activity);
                    } else {
                        failDialog(activity);
                    }
                }
            }
        }.execute();
    }

    private void completeDialog(final Activity activity) {
        DialogConfirmFragment.IDialogConfirmClickListener confirmClickListener = new DialogConfirmFragment.IDialogConfirmClickListener() {
            @Override
            public void onClick(boolean isOk) {
                if (isOk) {
                    Intent intent = new Intent(activity, RenewalHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("goToCart", true);
                    activity.startActivity(intent);
                }
            }

            @Override
            public void onCanceled() {

            }
        };
        try {
            DialogConfirmFragment.newInstance(DIALOG_TYPE_ACCESSORY_SAVE_COMPLETE, confirmClickListener).show(((FragmentActivity) activity).getSupportFragmentManager(), "dialog");
        } catch (IllegalStateException e) {
            Dlog.e(TAG, e);
        }
    }

    private void failDialog(final Activity activity) {

        DialogConfirmFragment.IDialogConfirmClickListener confirmClickListener = new DialogConfirmFragment.IDialogConfirmClickListener() {
            @Override
            public void onClick(boolean isOk) {

            }

            @Override
            public void onCanceled() {

            }
        };

        try {
            DialogConfirmFragment.newInstance(DIALOG_TYPE_ACCESSORY_SAVE_FAIL, confirmClickListener).show(((FragmentActivity) activity).getSupportFragmentManager(), "dialog");
        } catch (IllegalStateException e) {
            Dlog.e(TAG, e);
        }
    }

}
