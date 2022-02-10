package com.snaps.mobile.order.order_v2.util.org_image_upload.uploader_stratigies;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.img.SmartSnapsImageAreaInfo;
import com.snaps.common.data.smart_snaps.SmartSnapsImgInfo;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.http.HttpReq;
import com.snaps.common.utils.net.xml.GetMultiPartMethod;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsImageUploadUtil;
import com.snaps.mobile.order.order_v2.util.thumb_image_upload.SnapsThumbnailMaker;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;

import java.io.File;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;

/**
 * Created by ysjeong on 2017. 4. 14..
 */

public class SnapsThumbImgUploadHandler extends SnapsImageBaseUploadHandler {
	private static final String TAG = SnapsThumbImgUploadHandler.class.getSimpleName();

	public SnapsThumbImgUploadHandler(MyPhotoSelectImageData imageData) {
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
			message = HttpReq.saveSNSThumbImage(filePath, thumbPath, uploadImageData.KIND, Config.getPROJ_CODE(), SmartSnapsManager.isSupportSmartSnapsProduct(), uploadImageData.mineType, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
		} else {
			if (checkValidThumbCache()) {
				message = GetMultiPartMethod.getThumbImageUpload(getImageData(), Config.getPROJ_CODE(), null, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
			} else {
				return "failed thumbnail create";
			}
		}

		return message;
	}

	private boolean checkValidThumbCache() {
		boolean isValidThumbCacheFile = false;
		try {
			File thumbnailCacheFile = SnapsThumbnailMaker.getThumbnailCacheFileWithImageData(ContextUtil.getContext(), getImageData());
			isValidThumbCacheFile = thumbnailCacheFile != null && thumbnailCacheFile.exists() && thumbnailCacheFile.length() > 0 && BitmapUtil.isValidThumbnailImage(thumbnailCacheFile.getAbsolutePath());
			if (!isValidThumbCacheFile) {
				thumbnailCacheFile = SnapsThumbnailMaker.createThumbnailCacheWithImageData(ContextUtil.getContext(), getImageData());
				if (thumbnailCacheFile != null && thumbnailCacheFile.exists() && thumbnailCacheFile.length() > 0) {
					if (BitmapUtil.isValidThumbnailImage(thumbnailCacheFile.getAbsolutePath())) {
						isValidThumbCacheFile = true;
					} else {
						FileUtil.deleteFile(thumbnailCacheFile.getAbsolutePath());
					}
				}
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return isValidThumbCacheFile;
	}

//    private String retryOrgImageUploadWithThumbnail(String orgFilePath) throws Exception {
//        String thumbnailFilePath = null;
//        try {
//            Logg.y("retryOrgImageUploadWithThumbnail");
//            thumbnailFilePath = CropUtil.createThumbnailFile(orgFilePath);
//            if (StringUtil.isEmpty(thumbnailFilePath)) return "";
//            return GetMultiPartMethod.getOrgImageUpload(orgFilePath, thumbnailFilePath, Config.getPROJ_CODE(), null, SnapsInterfaceLogDefaultHandler.createDefaultHandler() );
//        } finally {
//            if (!StringUtil.isEmpty(thumbnailFilePath)) {
//                FileUtil.deleteFile(thumbnailFilePath);
//                Logg.y("delete temp thumbnail file.");
//            }
//        }
//    }

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

		final String IMAGE_NAME = uploadImageData.F_IMG_NAME;

		if (StringUtil.isEmpty(message)) {
			sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_RETURN_VALUE_ERR, IMAGE_NAME), orgImgUploadListener);
			return;
		}

		String[] returnValue = message.replace("||", "|").split("\\|");
		if (returnValue.length < 1) {
			sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_RETURN_VALUE_ERR, IMAGE_NAME), orgImgUploadListener);
			return;
		}

		if (returnValue[0].contains("SUCCESS")) {
			SnapsImageUploadUtil.setUploadedThumbImageInfoByUploadResultValue(uploadImageData, returnValue);

			//이미지 업로드를 했는데, 아래 정보가 누락되는 케이스가 있어서 넣은 코드
			if (!SnapsImageUploadUtil.isValidUploadedThumbImageData(uploadImageData)) {

				//업로드 정보를 초기화 시켜 버린다.
				SnapsImageUploadUtil.initThumbImgUploadInfo(uploadImageData);

				sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_RETURN_VALUE_ERR, IMAGE_NAME), orgImgUploadListener);
				return;
			}

			if (SmartSnapsManager.isSupportSmartSnapsProduct() && uploadImageData.isSmartSnapsSupport()) {
				SmartSnapsImageAreaInfo smartSnapsImageAreaInfo = SmartSnapsUtil.createSmartSnapsImageAreaInfoByUploadResultStrArr(returnValue);
				if (SmartSnapsUtil.isValidSmartImageAreaInfo(smartSnapsImageAreaInfo, uploadImageData)) {
					Dlog.d("handleAnalyzeUploadResultMsg() smartSnapsImageAreaInfo:" + smartSnapsImageAreaInfo);
					uploadImageData.setSmartSnapsImageAreaInfo(smartSnapsImageAreaInfo);
				} else if (Config.useDrawSmartSnapsImageArea()) {
					String errMsg = returnValue.length > 12 ? (returnValue[12] + " (" + returnValue[7] + ")") : "";
					Dlog.w(TAG,  "handleAnalyzeUploadResultMsg() invalid smart image area info : " + errMsg);
					SmartSnapsImgInfo smartSnapsImgInfo = uploadImageData.getSmartSnapsImgInfo();
					if (smartSnapsImgInfo != null) {
						smartSnapsImgInfo.setSearchFaceFailedMsg(errMsg);
						smartSnapsImgInfo.setFailedSearchFace(true);
					}

					Dlog.w(TAG, "handleAnalyzeUploadResultMsg() invalid smart image area info : image dimens : w "
							+ uploadImageData.F_IMG_WIDTH + ", h" + uploadImageData.F_IMG_HEIGHT + ", r : " + uploadImageData.ROTATE_ANGLE);
				}
			}

			sendResultWithImageDataSyncUnLock(true, SnapsImageUploadUtil.createOrgImgSuccessData(uploadImageData, null), orgImgUploadListener);
		} else {
			Dlog.e(TAG,  "handleAnalyzeUploadResultMsg() failed to Org Image upload : " + message);
			SnapsLogger.appendOrderLog("failed to Org Image upload : " + message);
			sendResultWithImageDataSyncUnLock(false, SnapsImageUploadUtil.createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_RETURN_VALUE_ERR, IMAGE_NAME), orgImgUploadListener);
		}
	}
}
