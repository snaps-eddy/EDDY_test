package com.snaps.mobile.order.order_v2.task.upload_task.diary_task;

import com.snaps.common.utils.thread.ATask;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.SnapsDiaryInterfaceUtil;
import com.snaps.mobile.activity.diary.json.SnapsDiaryBaseResultJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryGsonUtil;
import com.snaps.mobile.activity.diary.json.SnapsDiarySeqResultJson;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUploadSeqInfo;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.task.upload_task.SnapsOrderBaseTask;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderGetDiarySequenceTask extends SnapsOrderBaseTask {
    private SnapsOrderGetDiarySequenceTask(SnapsOrderAttribute attribute) {
        super(attribute);
    }

    public static SnapsOrderGetDiarySequenceTask createInstanceWithAttribute(SnapsOrderAttribute attribute) {
        return new SnapsOrderGetDiarySequenceTask(attribute);
    }

    public void getDiarySequenceCode(final SnapsOrderResultListener listener) throws Exception {
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            @Override
            public void onPre() {
            }

            @Override
            public boolean onBG() {
                boolean isSuccess = false;

                SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                if (dataManager.getWriteMode() == SnapsDiaryConstants.EDIT_MODE_NEW_WRITE) {
                    String resultJson = SnapsDiaryInterfaceUtil.getDiarySequence(getActivity());
                    if (resultJson != null) {
                        SnapsDiaryBaseResultJson result = SnapsDiaryGsonUtil.getParsedGsonData(resultJson, SnapsDiarySeqResultJson.class);
                        if (result != null && result.isSuccess()) {
                            dataManager.clearUploadInfo();

                            SnapsDiarySeqResultJson seqResult = (SnapsDiarySeqResultJson) result;
                            SnapsDiaryUploadSeqInfo uploadInfo = new SnapsDiaryUploadSeqInfo();
                            uploadInfo.setDiaryNo(seqResult.getDiaryNo());
                            uploadInfo.setSeqUserNo(SnapsLoginManager.getUUserNo(getActivity()));
                            dataManager.setUploadInfo(uploadInfo);
                            isSuccess = true;
                        }
                    }
                } else {
                    isSuccess = true;
                }

                return isSuccess;
            }

            @Override
            public void onPost(boolean result) {
                if (result) {

                    if (listener != null) listener.onSnapsOrderResultSucceed(null);
                } else {
                    if (listener != null) listener.onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_GET_DIARY_SEQ_CODE);
                }
            }
        });
    }
}
