package com.snaps.mobile.order.order_v2.task.upload_task.diary_task;

import android.app.Activity;

import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.SnapsDiaryInterfaceUtil;
import com.snaps.mobile.activity.diary.customview.SnapsDiaryDialog;
import com.snaps.mobile.activity.diary.json.SnapsDiaryBaseResultJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryGsonUtil;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUploadSeqInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUserInfo;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.task.upload_task.SnapsOrderBaseTask;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderDiaryMissionStateCheckTask extends SnapsOrderBaseTask {
    private SnapsOrderDiaryMissionStateCheckTask(SnapsOrderAttribute attribute) {
        super(attribute);
    }

    public static SnapsOrderDiaryMissionStateCheckTask createInstanceWithAttribute(SnapsOrderAttribute attribute) {
        return new SnapsOrderDiaryMissionStateCheckTask(attribute);
    }

    public void getDiaryMissionState(final SnapsOrderResultListener listener) throws Exception {
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            private boolean isMissionValidCheckResult = false;
            @Override
            public void onPre() {
                isMissionValidCheckResult = false;
            }

            @Override
            public boolean onBG() {
                SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                SnapsDiaryUserInfo userInfo = dataManager.getSnapsDiaryUserInfo();
                if (userInfo == null) {
                    return false;
                }

                boolean isSuccess = false;

                if (dataManager.getWriteMode() == SnapsDiaryConstants.EDIT_MODE_NEW_WRITE) {
                    SnapsDiaryUploadSeqInfo uploadInfo = dataManager.getUploadInfo();

                    String resultJson = SnapsDiaryInterfaceUtil.requestCheckMissionValid(getActivity(), uploadInfo.getSeqDiaryNo());
                    if (resultJson != null) {
                        SnapsDiaryBaseResultJson result = SnapsDiaryGsonUtil.getParsedGsonData(resultJson, SnapsDiaryBaseResultJson.class);
                        if (result != null) {
                            if (result.isSuccess()){ //성공
                                userInfo.setIsMissionValildCheckResult(true);
                                isMissionValidCheckResult = true;
                                isSuccess = true;
                            } else if (result.isDiaryReStartCode()) { //잉크 충족 조건이 실패 되었거나, 기간 만료로 인해 도전 자동 재 시작
                                userInfo.setIsMissionValildCheckResult(false);
                                isMissionValidCheckResult = false;
                                isSuccess = true;
                            } else { //네트워크 또는 서버 에러
                                isSuccess = false;
                            }
                        }
                    }
                } else {
                    //수정하기에서는 인증할 필요가 없다.
                    userInfo.setIsMissionValildCheckResult(true);
                    isMissionValidCheckResult = true;
                    isSuccess = true;
                }

                return isSuccess;
            }

            @Override
            public void onPost(boolean result) {
                if (result) {
                    if (isMissionValidCheckResult) {
                        if (listener != null) listener.onSnapsOrderResultSucceed(null);
                    } else {
                        //일기를 쓰는 도중 도전 기간이 지나면 아래의 메시지가 나온다.
                        SnapsDiaryDialog.showDialogOneBtn((Activity) getActivity(),
                                getActivity().getString(R.string.mission_auto_retry_title),
                                getActivity().getString(R.string.mission_auto_retry_msg), new ICustomDialogListener() {
                                    @Override
                                    public void onClick(byte clickedOk) {
                                        if (listener != null) listener.onSnapsOrderResultSucceed(null);
                                    }
                                });
                    }
                } else {
                    initDiarySeq();

                    if (listener != null) listener.onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_CHECK_DIARY_MISSION_STATE);
                }
            }

            private void initDiarySeq() {
                SnapsDiaryDataManager diaryDataManager = SnapsDiaryDataManager.getInstance();
                SnapsDiaryUploadSeqInfo uploadSeqInfo = diaryDataManager.getUploadInfo();
                uploadSeqInfo.setDiaryNo("");
            }
        });
    }
}
