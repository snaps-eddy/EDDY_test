package com.snaps.mobile.order.order_v2.task.upload_task.default_task;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.thread.ATask;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsOrderGetProjectCodeExceptionCreator;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.task.upload_task.SnapsOrderBaseTask;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderGetProjectCodeTask extends SnapsOrderBaseTask {
    private static final String TAG = SnapsOrderGetProjectCodeTask.class.getSimpleName();
    private static final int LIMIT_TRY_GET_PROJECT_CODE_COUNT = 3;

    private SnapsOrderGetProjectCodeTask(SnapsOrderAttribute attribute) {
        super(attribute);
    }

    public static SnapsOrderGetProjectCodeTask createInstanceWithAttribute(SnapsOrderAttribute attribute) {
        return new SnapsOrderGetProjectCodeTask(attribute);
    }

    public void getProjectCode(final SnapsOrderResultListener listener) throws Exception {
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            String responseProjectCode = null;
            @Override
            public void onPre() {
                Config.setPROJ_UTYPE("i");
            }

            @Override
            public boolean onBG() {
                responseProjectCode = requestGetProjectCodeSafety();
                if (Config.isValidProjCodeWithStringCode(responseProjectCode)) {
                    Dlog.d("getProjectCode() Project Code:" + responseProjectCode);
                    Config.setPROJ_CODE(responseProjectCode);
                    return true;
                } else {
                    Config.setPROJ_CODE("");
                    return false;
                }
            }

            //프로젝트 코드 요청 값이 false
            private String requestGetProjectCodeSafety() {
                int tryCount = 0;
                String projectCode = requestGetProjectCode();
                while (!Config.isValidProjCodeWithStringCode(projectCode)
                        && ++tryCount < LIMIT_TRY_GET_PROJECT_CODE_COUNT) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Dlog.e(TAG, e);
                    }
                    projectCode = requestGetProjectCode();
                }
                return projectCode;
            }

            private String requestGetProjectCode() {
                return GetParsedXml.getProjectCode(SnapsTPAppManager.getProjectCodeParams(getAttribute().getActivity()), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            }

            @Override
            public void onPost(boolean result) {
                if (result) {
                    listener.onSnapsOrderResultSucceed(null);

                    AutoSaveManager autoSaveManager = AutoSaveManager.getInstance();
                    if (autoSaveManager != null) {
                        autoSaveManager.exportProjectInfo();
                    }
                } else {
                    SnapsLogger.sendLogException("failed getProjectCode.", SnapsOrderGetProjectCodeExceptionCreator.createExceptionWithMessage("get project Code from snaps server : " + responseProjectCode + ", userId : " + SnapsLoginManager.getUUserNo(getAttribute().getActivity())));

                    listener.onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_GET_PROJECT_CODE);
                }
            }
        });
    }
}
