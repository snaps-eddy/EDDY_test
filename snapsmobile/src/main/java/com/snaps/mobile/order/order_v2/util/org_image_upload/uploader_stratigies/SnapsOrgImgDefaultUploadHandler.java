package com.snaps.mobile.order.order_v2.util.org_image_upload.uploader_stratigies;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.img.SmartSnapsImageAreaInfo;
import com.snaps.common.structure.SnapsDelImage;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.http.HttpReq;
import com.snaps.common.utils.net.xml.GetMultiPartMethod;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsImageUploadUtil;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;

/**
 * Created by ysjeong on 2017. 4. 14..
 */

public class SnapsOrgImgDefaultUploadHandler extends SnapsImageBaseUploadHandler {
	private static final String TAG = SnapsOrgImgDefaultUploadHandler.class.getSimpleName();
	public SnapsOrgImgDefaultUploadHandler(MyPhotoSelectImageData imageData) {
		super(imageData);
	}

	@Override
	public String requestImageUpload() throws Exception {
		MyPhotoSelectImageData uploadImageData = getImageData();
		if (uploadImageData == null) {
			return null;
		}

		String filePath = uploadImageData.PATH;
		String thumbPath = uploadImageData.getSafetyThumbnailPath();
		if (StringUtil.isEmpty(thumbPath)) {
			thumbPath = filePath;
		}

		String message;
		if (Config.isSNSPhoto(uploadImageData.KIND)) {
			if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
				message = HttpReq.saveSNSOrgImgForSmartSnaps(uploadImageData, Config.getPROJ_CODE(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
			} else {
				message = HttpReq.saveSNSImage(filePath, thumbPath, uploadImageData.KIND, Config.getPROJ_CODE(), uploadImageData.mineType, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
			}
		} else {
			message = GetMultiPartMethod.getOrgImageUpload(getImageData(), Config.getPROJ_CODE(), null, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
		}

		if (SnapsImageUploadUtil.isThumbnailErrorMsg(message)) {
			message = retryOrgImageUploadWithThumbnail(getImageData());
		}

		return message;
	}

	private String retryOrgImageUploadWithThumbnail(MyPhotoSelectImageData imageData) throws Exception {
		if (imageData == null) {
			return "";
		}

		String thumbnailFilePath = null;
		try {
			Dlog.d("retryOrgImageUploadWithThumbnail()");
			thumbnailFilePath = CropUtil.createThumbnailFile(imageData.PATH);
			if (StringUtil.isEmpty(thumbnailFilePath)) {
				return "";
			}
			return GetMultiPartMethod.getOrgImageUpload(imageData, thumbnailFilePath, Config.getPROJ_CODE(), null, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
		} finally {
			if (!StringUtil.isEmpty(thumbnailFilePath)) {
				FileUtil.deleteFile(thumbnailFilePath);
				Dlog.d("retryOrgImageUploadWithThumbnail() delete temp thumbnail file.");
			}
		}
	}

	private boolean isNetworkErrorMsg(String message) {
		return message != null && message.equalsIgnoreCase(SnapsOrderConstants.EXCEPTION_MSG_NETWORK_ERROR);
	}

	@Override
	public void handleAnalyzeUploadResultMsg(String message, SnapsImageUploadListener orgImgUploadListener) throws Exception {
		MyPhotoSelectImageData uploadImageData = getImageData();

		if (uploadImageData == null) { //이럴 가능성은 없다고 생각 되지만...혹시나~
			sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(null, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_ORG_IMAGE_DATA_IS_NULL), orgImgUploadListener);
			return;
		}

		if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
			SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
			smartSnapsManager.waitIfSmartSnapsAnimationImageListHandling();
		}

		if (!CNetStatus.getInstance().isAliveNetwork(ContextUtil.getContext()) || isNetworkErrorMsg(message)) {
			sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_SUPPORT_NETWORK_STATE), orgImgUploadListener);
			return;
		}

		if (StringUtil.isEmpty(message)) {
			sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_RETURN_VALUE_ERR), orgImgUploadListener);
			return;
		}

		String[] returnValue = message.replace("||", "|").split("\\|");
		if (returnValue.length < 1) {
			sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_RETURN_VALUE_ERR), orgImgUploadListener);
			return;
		}

		if (returnValue[0].contains("SUCCESS")) {
			SnapsImageUploadUtil.setUploadedOrgImageInfoByUploadResultValue(uploadImageData, returnValue);

			//이미지 업로드를 했는데, 아래 정보가 누락되는 케이스가 있어서 넣은 코드
			if (!SnapsImageUploadUtil.isValidUploadedOrgImageData(uploadImageData)) {

				//업로드 정보를 초기화 시켜 버린다.
				SnapsImageUploadUtil.initOrgImgUploadInfo(uploadImageData);

				sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_RETURN_VALUE_ERR), orgImgUploadListener);
				return;
			}

			SnapsDelImage delImg = SnapsImageUploadUtil.createSnapsDelImageByUploadResultValue(uploadImageData, returnValue);
			if (delImg != null) {
				SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
				SnapsImageUploadUtil.addDelImgDataInTemplate(snapsTemplateManager.getSnapsTemplate(), delImg);
			}

			if (SmartSnapsManager.isSupportSmartSnapsProduct() && uploadImageData.isSmartSnapsSupport()) {
				SmartSnapsImageAreaInfo smartSnapsImageAreaInfo = SmartSnapsUtil.createSmartSnapsImageAreaInfoByUploadResultStrArr(returnValue);
				if (SmartSnapsUtil.isValidSmartImageAreaInfo(smartSnapsImageAreaInfo, uploadImageData)) {
					uploadImageData.setSmartSnapsImageAreaInfo(smartSnapsImageAreaInfo);
				}
//                else if (Config.useDrawSmartSnapsImageArea()) { //얼굴 정보는 썸네일 업로딩 시에 처리 한다...
//                    String errMsg = returnValue.length > 1 ? (returnValue[1]) : "";
//                    Logg.y("############# invalid smart image area info : " + errMsg);
//                    SmartSnapsImgInfo smartSnapsImgInfo = uploadImageData.getSmartSnapsImgInfo();
//                    if (smartSnapsImgInfo != null) {
//                        smartSnapsImgInfo.setSearchFaceFailedMsg(errMsg);
//                        smartSnapsImgInfo.setFailedSearchFace(true);
//                    }
//
//                    Logg.y(">>> invalid smart image area info : image dimens : w " + uploadImageData.F_IMG_WIDTH + ", h" + uploadImageData.F_IMG_HEIGHT + ", r : " + uploadImageData.ROTATE_ANGLE);
//                }
			}

			sendResultWithImageDataSyncUnLock(true, SnapsImageUploadUtil.createOrgImgSuccessData(uploadImageData, delImg), orgImgUploadListener);
		} else {
			SnapsLogger.appendOrderLog("failed to Org Image upload : " + message);
			sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_RETURN_VALUE_ERR), orgImgUploadListener);
		}
	}
}
