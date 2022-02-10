package com.snaps.mobile.order.order_v2.task.upload_task.default_task;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetMultiPartMethod;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.exceptions.SnapsIOException;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.task.upload_task.SnapsOrderBaseTask;

import java.io.File;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderUploadMainThumbnailTask extends SnapsOrderBaseTask {
    private static final String TAG = SnapsOrderUploadMainThumbnailTask.class.getSimpleName();

    private SnapsOrderUploadMainThumbnailTask(SnapsOrderAttribute attribute) {
        super(attribute);
    }

    public static SnapsOrderUploadMainThumbnailTask createInstanceWithAttribute(SnapsOrderAttribute attribute) {
        return new SnapsOrderUploadMainThumbnailTask(attribute);
    }

    private String getImgSeqStr(String value) {
        if (StringUtil.isEmpty(value)) return value;

        if (value.contains("/")) {
            String[] arValue = value.split("/");
            if (arValue.length > 0)
                return arValue[0];
        }

        return value;
    }

    public void uploadMainThumbnail(final SnapsOrderResultListener listener) throws Exception {
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            @Override
            public void onPre() {}

            @Override
            public boolean onBG() {

                String message = uploadThumbnailImage();
                if (message == null) return false;

                String[] returnValue = message.replace("||", "|").split("\\|");

                if (returnValue[0].indexOf("SUCCESS") >= 0) {
                    String yearValue = getImgSeqStr(returnValue[2]);
                    Config.setYEAR_KEY(yearValue);

                    String sqnValue = getImgSeqStr(returnValue[3]);
                    Config.setSQNC_KEY(sqnValue);
                    return true;
                }

                return false;
            }

            private String getThumbnailPath(String thumbName) {
                String fileName = "";
                try {
                    File file = Config.getTHUMB_PATH(thumbName);
                    if (file == null) throw new SnapsIOException("failed make thumbnail");
                    if (!file.exists()) return null;
                    fileName = file.getAbsolutePath();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    return null;
                }
                return fileName;
            }

            private String uploadThumbnailImage() {
                String thumbFileName = getThumbnailPath("thumb.jpg");
                String fullThumbFileName = PhotobookCommonUtils.shouldUploadFullSizeThumbnailProduct() ? getThumbnailPath("fullSizeThumb.jpg") : null;

                GetMultiPartMethod.SnapsImageUploadRequestData requestData = new GetMultiPartMethod.SnapsImageUploadRequestData.Builder()
                        .setUserId(SnapsLoginManager.getUUserNo(getActivity()))
                        .setFileName(thumbFileName)
                        .setFullSizeThumbnail(!StringUtil.isEmpty(fullThumbFileName))
                        .setFullSizeFileName(fullThumbFileName)
                        .setListener(null)
                        .setAlbumType("P")
                        .setPrjCode(Config.getPROJ_CODE())
                        .setInterfaceLogListener(SnapsInterfaceLogDefaultHandler.createDefaultHandler()).create();

                return GetMultiPartMethod.getPageThumbImageUpload(requestData);
            }

            @Override
            public void onPost(boolean result) {
                if (result) {
                    listener.onSnapsOrderResultSucceed(null);
                } else {
                    listener.onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_MAIN_THUMBNAIL);
                }
            }
        });
    }
}
