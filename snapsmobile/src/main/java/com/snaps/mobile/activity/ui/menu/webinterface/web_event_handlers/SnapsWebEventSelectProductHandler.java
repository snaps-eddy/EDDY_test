package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.SnapsSelectProductJunctionFactory;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForAccessory;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionGotoPage;

import font.FProgressDialog;

import static com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment.DIALOG_TYPE_PRODUCT_MATCH_FAIL;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventSelectProductHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventSelectProductHandler.class.getSimpleName();

    public SnapsWebEventSelectProductHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);

    }

    @Override
    public boolean handleEvent() {

        String oldProductCode = Config.getPROD_CODE();
        if (prodKey.equals(oldProductCode)) {
            Dlog.i(TAG, "Will be opend overlapped editor");
            return true;
        }

        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            FProgressDialog pd = null;

            @Override
            public void onPre() {
                SnapsTemplateManager templeteManager = SnapsTemplateManager.getInstance();
                if (templeteManager != null && templeteManager.isActivityFinishing()) {
                    pd = new FProgressDialog(activity);
                    pd.setCancelable(false);
                    pd.show();
                }
            }

            @Override
            public boolean onBG() {
                SnapsTemplateManager.waitIfEditActivityFinishing();
                return true;
            }

            @Override
            public void onPost(boolean result) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                    pd = null;
                }

                if (result) {
                    Config.cleanProductInfo();
//                    prodKey = "00800800220001";
                    ISnapsProductLauncher launcher = SnapsSelectProductJunctionFactory.getInstance().createProductLauncher(prodKey);
                    if (launcher instanceof SnapsSelectProductJunctionGotoPage) {
                        DialogConfirmFragment.IDialogConfirmClickListener confirmClickListener = new DialogConfirmFragment.IDialogConfirmClickListener() {
                            @Override
                            public void onClick(boolean isOk) {

                            }

                            @Override
                            public void onCanceled() {

                            }
                        };
                        DialogConfirmFragment.newInstance(DIALOG_TYPE_PRODUCT_MATCH_FAIL, confirmClickListener).show(((FragmentActivity) activity).getSupportFragmentManager(), "dialog");
                    } else {
                        SnapsProductAttribute attribute = new SnapsProductAttribute.Builder()
                                .setProdKey(prodKey)
                                .setUrlData(urlData)
                                .setPhotoPrintDataList(mData)
                                .setKakao(kakao)
                                .setFacebook(facebook)
                                .setHandleDatas(handleDatas)
                                .create();

                        if (launcher != null)
                            launcher.startMakeProduct(activity, attribute);

                        if (launcher instanceof SnapsSelectProductJunctionForAccessory) {
                            AppEventsLogger logger = AppEventsLogger.newLogger(SnapsWebEventSelectProductHandler.this.activity);
                            Bundle params = new Bundle();
                            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, Config.getPROD_NAME());
                            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, Config.getPROD_CODE());
                            logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, 0, params);
                        } else {
                            AppEventsLogger logger = AppEventsLogger.newLogger(SnapsWebEventSelectProductHandler.this.activity);
                            Bundle params = new Bundle();
                            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, Config.getPROD_CODE());
                            logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, 0, params);
                        }
                    }
                } else {
                    Dlog.d("handleEvent() result:false");
                }
            }
        });

        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class Name:" + getClass().getName());
    }
}
