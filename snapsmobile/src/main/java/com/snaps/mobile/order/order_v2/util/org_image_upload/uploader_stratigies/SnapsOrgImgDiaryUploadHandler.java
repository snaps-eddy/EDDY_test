package com.snaps.mobile.order.order_v2.util.org_image_upload.uploader_stratigies;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsDelImage;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.http.HttpReq;
import com.snaps.common.utils.net.xml.GetMultiPartMethod;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.diary.json.SnapsDiaryBaseResultJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryGsonUtil;
import com.snaps.mobile.activity.diary.json.SnapsDiaryImgUploadResultJson;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsImageUploadUtil;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

/**
 * Created by ysjeong on 2017. 4. 14..
 */

public class SnapsOrgImgDiaryUploadHandler extends SnapsImageBaseUploadHandler {
    public SnapsOrgImgDiaryUploadHandler(MyPhotoSelectImageData imageData) {
        super(imageData);
    }

    @Override
    public String requestImageUpload() throws Exception {
        MyPhotoSelectImageData uploadImageData = getImageData();
        if (uploadImageData == null) return null;

        String filePath = uploadImageData.PATH;
        String thumbPath = uploadImageData.getSafetyThumbnailPath();
        if( StringUtil.isEmpty(thumbPath) ) thumbPath = filePath;

        String message;
        // 일기는 아직 필요없지만 나중에 일기에 SNS 이미지 추가할 경우를 대비해 같이 넣어둠.
        if( uploadImageData.KIND == Const_VALUES.SELECT_PHONE ) message = GetMultiPartMethod.getDiaryOrgImageUplad(filePath, false, null, SnapsInterfaceLogDefaultHandler.createDefaultHandler() );
        else message = HttpReq.saveSNSImage(filePath, thumbPath, uploadImageData.KIND, SnapsOrderManager.getProjectCode(),uploadImageData.mineType, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

        return message;
    }

    @Override
    public void handleAnalyzeUploadResultMsg(String message, SnapsImageUploadListener orgImgUploadListener) throws Exception {
        if (!CNetStatus.getInstance().isAliveNetwork(ContextUtil.getContext())) {
            sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(null, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_SUPPORT_NETWORK_STATE), orgImgUploadListener);
            return;
        }

        MyPhotoSelectImageData uploadImageData = getImageData();
        if (uploadImageData == null) {
            sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(null, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_ORG_IMAGE_DATA_IS_NULL), orgImgUploadListener);
            return;
        }

        if (StringUtil.isEmpty(message)) {
            sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_RETURN_VALUE_ERR), orgImgUploadListener);
            return;
        }

        SnapsDiaryBaseResultJson result = SnapsDiaryGsonUtil.getParsedGsonData(message, SnapsDiaryImgUploadResultJson.class);
        if (result != null && result.isSuccess()) {
            SnapsDiaryImgUploadResultJson imgUploadResult = (SnapsDiaryImgUploadResultJson) result;
            uploadImageData.F_IMG_YEAR = imgUploadResult.getImgYear();
            uploadImageData.F_IMG_SQNC = imgUploadResult.getImgSqnc();
            uploadImageData.F_UPLOAD_PATH = imgUploadResult.getRealFilePath();
            uploadImageData.THUMBNAIL_PATH = imgUploadResult.getMidFilePath();
            uploadImageData.ORIGINAL_PATH = imgUploadResult.getOrgFilePath();

            if(!SnapsImageUploadUtil.isValidUploadedOrgImageData(uploadImageData)) {
                //업로드 정보를 초기화 시켜 버린다.
                SnapsImageUploadUtil.initOrgImgUploadInfo(uploadImageData);

                sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_RETURN_VALUE_ERR), orgImgUploadListener);
                return;
            }

            SnapsDelImage delImg = SnapsImageUploadUtil.createSnapsDelImageByUploadResultValue(uploadImageData, imgUploadResult);

            sendResultWithImageDataSyncUnLock(true, SnapsImageUploadUtil.createOrgImgSuccessData(uploadImageData, delImg), orgImgUploadListener);
        } else {
            sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_RETURN_VALUE_ERR), orgImgUploadListener);
        }
    }
}
