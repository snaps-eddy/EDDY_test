package com.snaps.mobile.order.order_v2.task.upload_task.diary_task;

import com.snaps.common.data.net.CustomMultiPartEntity;
import com.snaps.common.structure.SnapsMakeXML;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.xml.GetMultiPartMethod;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.json.SnapsDiaryBaseResultJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryGsonUtil;
import com.snaps.mobile.activity.diary.json.SnapsDiaryUploadResultJson;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUploadSeqInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUserInfo;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.task.upload_task.SnapsOrderBaseTask;

import java.io.File;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderUploadDiaryXMLTask extends SnapsOrderBaseTask {
    private static final String TAG = SnapsOrderUploadDiaryXMLTask.class.getSimpleName();
    private File saveXML;

    private String exceptionWhere = null;

    private SnapsOrderUploadDiaryXMLTask(SnapsOrderAttribute attribute) {
        super(attribute);
    }

    public static SnapsOrderUploadDiaryXMLTask createInstanceWithAttribute(SnapsOrderAttribute attribute) {
        return new SnapsOrderUploadDiaryXMLTask(attribute);
    }

    public void performMakeXML(final SnapsOrderResultListener listener) throws Exception {

        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {

            @Override
            public void onPre() {}

            @Override
            public boolean onBG() {
                try {
                    SnapsMakeXML makeXML = new SnapsMakeXML(getTemplate(), SystemUtil.getAppVersion(getActivity()));

                    makeSaveXML(makeXML);

                    return saveXML != null && saveXML.length() > 0;
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                return false;
            }

            @Override
            public void onPost(boolean result) {
                if (result) {
                    listener.onSnapsOrderResultSucceed(null);
                } else {
                    listener.onSnapsOrderResultFailed(exceptionWhere, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_MAKE_XML);
                }
            }
        });
    }

    public void performUploadXML(final SnapsOrderResultListener listener) throws Exception {
        if (!CNetStatus.getInstance().isAliveNetwork(ContextUtil.getContext())) {
            listener.onSnapsOrderResultFailed(SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_SUPPORT_NETWORK_STATE, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_XML);
            return;
        }

        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {

            boolean isIssuedInk = false;
            String uploadResultMsg = "";

            @Override
            public void onPre() {}

            @Override
            public boolean onBG() {
                try {
                    SnapsLogger.appendOrderLog("diary saveXML make point C : saveXML length : " + ((saveXML != null && saveXML.exists()) ? saveXML.length() : "saveXML is empty"));
                    uploadResultMsg = GetMultiPartMethod.getDiaryProejctUpload(saveXML, new CustomMultiPartEntity.ProgressListener() {
                        @Override
                        public void transferred(long num, long total) {}
                    }, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

                    if (uploadResultMsg != null) {
                        SnapsLogger.appendOrderLog("diary saveXML upload msg : " + uploadResultMsg);

                        SnapsDiaryBaseResultJson result = SnapsDiaryGsonUtil.getParsedGsonData(uploadResultMsg, SnapsDiaryUploadResultJson.class);
                        if (result != null && result.isSuccess()) {
                            SnapsDiaryUploadResultJson uploadResultJson = (SnapsDiaryUploadResultJson) result;
                            isIssuedInk = uploadResultJson.isIssuedInk();

                            if(uploadResultJson.getMissionInfo() != null) {
                                SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();

                                SnapsDiaryUserInfo prevUserInfo = dataManager.getSnapsDiaryUserInfo();

                                //사용자의 미션 정보 갱신.
                                SnapsDiaryUserInfo userInfo = new SnapsDiaryUserInfo();
                                userInfo.set(uploadResultJson.getMissionInfo());
                                if(prevUserInfo != null) {
                                    userInfo.setThumbnailCache(prevUserInfo.getThumbnailCache());
                                    userInfo.setThumbnailPath(prevUserInfo.getThumbnailPath());
                                    userInfo.setIsMissionValildCheckResult(true);
                                }

                                dataManager.setSnapsDiaryUserInfo(userInfo);

                                SnapsDiaryUploadSeqInfo uploadSeqInfo = dataManager.getUploadInfo();
                                uploadSeqInfo.setDiaryNo("");
//								Logg.y("user's ink cnt : " + uploadResultJson.getMissionInfo().getInkCnts() + " / " + uploadResultJson.getMissionInfo().getTotalInkCnt());
                            }
                            return true;
                        } else {
                            if (result != null) {
                                String errorLog = "result msg : " + result.getMessage();
                                errorLog += "\nerrCode : " + result.getErrMsg();
                                errorLog += "\ngetStatus : " + result.getStatus();
                                SnapsLogger.appendOrderLog("diary saveXML upload result : " +errorLog);
                            } else {
                                SnapsLogger.appendOrderLog("diary saveXML upload result : result obj is null");
                            }
                        }
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    if (GetMultiPartMethod.isUnknownHostExceptionWithException(e) || GetMultiPartMethod.isHttpHostConnectException(e)) {
                        uploadResultMsg = SnapsOrderConstants.EXCEPTION_MSG_UNKNOWN_HOST_ERROR;
                    } else
                        SnapsLogger.appendOrderLog("diary saveXML upload Exception : " + e.toString());
                }

                return false;
            }

            @Override
            public void onPost(boolean result) {
                if (result) {
                    listener.onSnapsOrderResultSucceed(isIssuedInk);
                } else {
                    //업로드 실패시 확인을 위한 로그
                    if (uploadResultMsg != null && uploadResultMsg.equalsIgnoreCase(SnapsOrderConstants.EXCEPTION_MSG_UNKNOWN_HOST_ERROR)) { //Unkown Host exception이 발생하면 Wifi연결이 안 되어 있을 수도 있기 때문에 별도로 오류 처리를 한다
                        listener.onSnapsOrderResultFailed(uploadResultMsg, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_XML);
                    } else {
                        listener.onSnapsOrderResultFailed(exceptionWhere, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_XML);
                    }
                }
            }
        });
    }

    /**
     * Save XML 생성.
     */
    private File makeSaveXMLString(SnapsMakeXML xml) {
        return xml.saveXmlFile();
    }

    private void makeSaveXML(SnapsMakeXML makeXML) throws Exception {
        exceptionWhere = "make saveXML err";
        saveXML = makeSaveXMLString(makeXML);
    }
}
