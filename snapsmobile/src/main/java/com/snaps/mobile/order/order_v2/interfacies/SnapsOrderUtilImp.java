package com.snaps.mobile.order.order_v2.interfacies;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public interface SnapsOrderUtilImp {
    void getProjectCode(final SnapsOrderResultListener listener) throws Exception;

    void performUploadOrgImgAtBackground(final ArrayList<MyPhotoSelectImageData> imageList, final SnapsImageUploadListener listener)
            throws Exception;

    void performUploadThumbImgListAtBackground(final ArrayList<MyPhotoSelectImageData> imageList, final SnapsImageUploadListener listener)
            throws Exception;

    void performUploadThumbImgAtBackground(MyPhotoSelectImageData imageData, final SnapsImageUploadListener listener)
            throws Exception;

    void suspendBackgroundImgUpload() throws Exception;

    void cancelThumbnailImgUploadExecutor() throws Exception;

    void cancelOrgImgUploadExecutor() throws Exception;

    void performInspectRequiredOptions(final SnapsOrderResultListener listener) throws Exception;

    void removeBackgroundUploadingOrgImageDataList(List<MyPhotoSelectImageData> removeList) throws Exception;

    void removeBackgroundUploadingOrgImageData(MyPhotoSelectImageData removeData) throws Exception;

    void removeBackgroundUploadingThumbImageData(MyPhotoSelectImageData removeData) throws Exception;

    void showSaveToBasketAlert(SnapsOrderSaveToBasketAlertAttribute alertAttribute, DialogInputNameFragment.IDialogInputNameClickListener dialogInputNameClickListener) throws Exception;

    void showCompleteUploadPopup(DialogConfirmFragment.IDialogConfirmClickListener confirmClickListener) throws Exception;

    void prepareProjectUpload(SnapsOrderResultListener orderResultListener) throws Exception;
}
