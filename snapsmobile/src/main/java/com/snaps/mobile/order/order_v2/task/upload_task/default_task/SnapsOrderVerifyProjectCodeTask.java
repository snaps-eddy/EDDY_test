package com.snaps.mobile.order.order_v2.task.upload_task.default_task;

import android.app.Activity;
import android.content.Intent;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.webview.DetailProductWebviewActivity;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.task.upload_task.SnapsOrderBaseTask;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderVerifyProjectCodeTask extends SnapsOrderBaseTask {
    private static final String TAG = SnapsOrderVerifyProjectCodeTask.class.getSimpleName();

    private SnapsOrderVerifyProjectCodeTask(SnapsOrderAttribute attribute) {
        super(attribute);
    }

    public static SnapsOrderVerifyProjectCodeTask createInstanceWithAttribute(SnapsOrderAttribute attribute) {
        return new SnapsOrderVerifyProjectCodeTask(attribute);
    }

    private boolean isDuplicatedVerifyProjectCode = false;

    public void verifyProjectCode(final SnapsOrderResultListener listener) throws Exception {
        ATask.executeBoolean(new ATask.OnTaskResult() {
            @Override
            public void onPre() {
                isDuplicatedVerifyProjectCode = false;
            }

            @Override
            public void onPost(boolean result) {
                if (result) {
                    if (isEditMode()) {
                        Config.setPROJ_UTYPE("m");
                        listener.onSnapsOrderResultSucceed(null);
                    } else {
                        listener.onSnapsOrderResultSucceed(null);
                    }
                } else {
                    if (isDuplicatedVerifyProjectCode) {
                        MessageUtil.alertnoTitleOneBtn(((Activity) getActivity()), getActivity().getResources().getString(R.string.failed_order_because_already_complated_order), new ICustomDialogListener() {
                            @Override
                            public void onClick(byte clickedOk) {
                                String url = SnapsAPI.WEB_DOMAIN(SnapsAPI.ORDER_URL(), SnapsLoginManager.getUUserNo(getActivity()), "");
                                Intent intent = DetailProductWebviewActivity.getIntent(getActivity(), getActivity().getString(R.string.order_and_delivery), url, true, SnapsMenuManager.eHAMBURGER_ACTIVITY.ORDER);
                                getActivity().startActivity(intent);
                                ((Activity) getActivity()).finish();
                            }
                        });
                    } else {
                        listener.onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_VERIFY_PROJECT_CODE);
                    }
                }
            }

            @Override
            public boolean onBG() {
                String result = null;

                try {
                    result = GetParsedXml.getResultVerifyProjectCode(Config.getPROJ_CODE(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                } catch (Exception e) { Dlog.e(TAG, e); }

                if (result == null) {
                    isDuplicatedVerifyProjectCode = false;
                    return false;
                }

                if (result.equalsIgnoreCase("SUCCESS")) {
                    return true;
                } else {
                    isDuplicatedVerifyProjectCode = true;
                    SnapsLogger.appendOrderLog("verify project code exception : " + result);
                    return false;
                }
            }
        });
    }
}
