package com.snaps.mobile.order.order_v2.task.prepare_process;

import androidx.fragment.app.FragmentActivity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 3. 31..
 */

public class SnapsOrderPrepareHandlerForDiary extends SnapsOrderPrepareBaseHandler {
    private static final String TAG = SnapsOrderPrepareHandlerForDiary.class.getSimpleName();

    private SnapsOrderPrepareHandlerForDiary(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        super(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderPrepareHandlerForDiary createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        return new SnapsOrderPrepareHandlerForDiary(attribute, snapsOrderActivityBridge);
    }

    @Override
    public int performInspectOrderOptionAndGetResultCode() {
        int resultCode = checkBaseOrderOptionAndGetResultCode();
        if (resultCode != ORDER_PREPARE_INSPECT_RESULT_OK) return resultCode;

        if (!SnapsDiaryDataManager.isExistDiarySeqNo()) resultCode = ORDER_PREPARE_INSPECT_RESULT_NOT_EXIST_DIARY_SEQ;
        else if (!isExistImageData())  resultCode = ORDER_PREPARE_INSPECT_RESULT_NOT_EXIST_DIARY_IMAGE;
        return resultCode;
    }

    //일기 이미지는 최소한 1장 이상이어야 한다.
    private boolean isExistImageData() {
        if (getSnapsOrderActivityBridge() == null) return false;
        ArrayList<MyPhotoSelectImageData> imageList = getSnapsOrderActivityBridge().getUploadImageList();
        return imageList != null && !imageList.isEmpty();
    }

    @Override
    public void setOrderBaseInfo(SnapsOrderResultListener baseInfoListener) throws Exception {
        setKeepScreenState(true);

        baseInfoListener.onSnapsOrderResultSucceed(null);

        //재저장이면 m으로 넣어주어야 한다.
        initProjUType();

        //업로드 실패한 원본 이미지 리스트 초기화
        SnapsUploadFailedImageDataCollector.clearHistory(SnapsDiaryDataManager.getDiarySeq());
    }

    @Override
    public void showSaveToBasketAlert(SnapsOrderSaveToBasketAlertAttribute alertAttribute, DialogInputNameFragment.IDialogInputNameClickListener dialogInputNameClickListener) throws Exception {
        if (SnapsOrderManager.isUploadingProject()) return;

        //일기는 팝업같은 게 없다 바로 업로드 시작...
        if (dialogInputNameClickListener!= null)
            dialogInputNameClickListener.onClick(true);
    }

    @Override
    public void showCompleteUploadPopup(DialogConfirmFragment.IDialogConfirmClickListener dialogConfirmClickListener) throws Exception {
            setDiagConfirm(DialogConfirmFragment.newInstance(DialogConfirmFragment.DIALOG_TYPE_ORDER_COMPLETE, dialogConfirmClickListener));
        try {
            getDiagConfirm().show(((FragmentActivity) getAttribute().getActivity()).getSupportFragmentManager(), "dialog");
        } catch (IllegalStateException e) { Dlog.e(TAG, e); }
    }

    @Override
    protected void setTextControlBaseText() throws Exception {}

    @Override
    protected boolean isLackQuantity() throws Exception {
        return false;
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLowResolutionAlertAttribute() {
        return null;
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLackQuantityAlertAttribute() {
        return null;
    }
}
