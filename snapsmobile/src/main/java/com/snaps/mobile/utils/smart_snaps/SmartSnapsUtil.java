package com.snaps.mobile.utils.smart_snaps;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import android.util.TypedValue;
import android.widget.ImageView;

import com.snaps.common.data.img.BRect;
import com.snaps.common.data.img.BSize;
import com.snaps.common.data.img.ExifUtil;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.img.SmartSnapsImageAreaInfo;
import com.snaps.common.data.img.SmartSnapsLayoutControlInfo;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.data.smart_snaps.SmartSnapsImgInfo;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.snaps_image_proccesor.image_coordinate_processor.ImageCoordinateCalculator;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.image.ResolutionUtil;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.imageloader.SnapsImageDownloader;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.imageloader.recoders.CropInfo;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.common.data.SnapsProductEditControls;
import com.snaps.mobile.activity.edit.view.SnapsClippingDimLayout;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;
import com.snaps.mobile.order.order_v2.util.thumb_image_upload.SnapsThumbnailMaker;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.SMART_RECOMMEND_BOOK_GLOSSY_TYPE;

/**
 * Created by ysjeong on 2018. 1. 17..
 */

public class SmartSnapsUtil {
    private static final String TAG = SmartSnapsUtil.class.getSimpleName();

    public static void deleteSmartRecommendBookTempEditInfo() {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        smartSnapsManager.deleteTempEditInfo();
    }

    public static void saveSmartRecommendBookCurrentEditInfo(ArrayList<MyPhotoSelectImageData> imageList, ArrayList<SnapsPage> pageList) {
        try {
            //편집 취소할 경우를 생각해서 임시 저장 해 놓고 쓴다...
            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            smartSnapsManager.copyTempPhotoImageDataList(imageList);
            smartSnapsManager.copyTempPageList(pageList);
            smartSnapsManager.copyTempCoverPageList(smartSnapsManager.getCoverPageListOfAnalysisPhotoBook());
            smartSnapsManager.setTempCoverTitle(Config.getPROJ_NAME());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static MyPhotoSelectImageData getCoverImageData(ArrayList<MyPhotoSelectImageData> photoList) {
        if (photoList == null) {
            return null;
        }

        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        for (MyPhotoSelectImageData imageData : photoList) {
            if (imageData != null && smartSnapsManager.isContainCoverPhotoMapKey(imageData.getImageSelectMapKey())) {
                return imageData;
            }
        }

        return null;
    }

    public static void requestAnalysisImageList(final ArrayList<MyPhotoSelectImageData> imgList, final SnapsCommonResultListener<SnapsTemplate> resultListener) {
        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            SnapsTemplate snapsTemplate = null;

            @Override
            public void onPre() {
                if (resultListener != null) {
                    resultListener.onPrepare();
                }
            }

            @Override
            public void onBG() {
                try {
                    SmartSnapsImageAnalysisRequestValue analysisRequestValue = SmartSnapsImageAnalysisRequestValue.createRequestValueWithImageList(imgList);
                    if (analysisRequestValue.checkErrorRequestValue()) {
                        SnapsAssert.assertTrue(false);
                        return;
                    }

                    analysisRequestValue.setProjCode(Config.getPROJ_CODE());
                    analysisRequestValue.setProdCode(Config.getPROD_CODE());

                    snapsTemplate = GetParsedXml.requestGetTemplateWithAnalysisInfo(
                            createImageAnalysisParamsWithRequestValue(analysisRequestValue)
                            , SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    if (resultListener != null) {
                        resultListener.onException(e);
                    }
                }
            }

            @Override
            public void onPost() {
                if (resultListener != null) {
                    resultListener.onResult(snapsTemplate);
                }
            }
        });
    }

    private static List<NameValuePair> createImageAnalysisParamsWithRequestValue(SmartSnapsImageAnalysisRequestValue requestValue) {
        Map<String, String> imageUploadRequestDataMap = createImageAnalysisParamsWithRequestDataMap(requestValue);

        List<NameValuePair> parameters = new ArrayList<>();
        JSONObject jsonObjectForWebLog = new JSONObject();

        for (Map.Entry<String, String> entry : imageUploadRequestDataMap.entrySet()) {
            if (entry == null) {
                continue;
            }
            try {
                jsonObjectForWebLog.put(entry.getKey(), entry.getValue());

                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            } catch (JSONException e) {
                Dlog.e(TAG, e);
            }
        }

        SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_complete_processLayout_REQ)
                .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE())
                .appendPayload(WebLogConstants.eWebLogPayloadType.REQUEST_CONTENTS, jsonObjectForWebLog.toString()));

        return parameters;
    }

    private static Map<String, String> createImageAnalysisParamsWithRequestDataMap(SmartSnapsImageAnalysisRequestValue requestValue) {
        Map<String, String> requestDataMap = new HashMap<>();
        requestDataMap.put("prmAppType", "android");
        requestDataMap.put("prmChnlCode", Config.getCHANNEL_CODE());
        requestDataMap.put("prmProjCode", requestValue.getProjCode());
        requestDataMap.put("prmProdCode", requestValue.getProdCode());
        requestDataMap.put("prmKeys", requestValue.getImageKey());
        requestDataMap.put("prmWidths", requestValue.getImageWidth());
        requestDataMap.put("prmHeights", requestValue.getImageHeight());
        requestDataMap.put("prmOts", requestValue.getImageOt());
        requestDataMap.put("prmExifDates", requestValue.getImageExifDate());
        requestDataMap.put("prmSysDates", requestValue.getImageSysDate());
        requestDataMap.put("prmGps", requestValue.getImageGps());
        requestDataMap.put("prmImageNames", requestValue.getImageName());
        requestDataMap.put("prmImageTypes", requestValue.getImageTypes());
        requestDataMap.put("prmThumbnailPaths", requestValue.getThumbPaths());
        requestDataMap.put("prmGlossyType", StringUtil.getSafeStrIfNotValidReturnSubStr(Config.getGLOSSY_TYPE(), SMART_RECOMMEND_BOOK_GLOSSY_TYPE));
        requestDataMap.put("prmFdThumbnails", requestValue.getFdThumbnails());
        return requestDataMap;
    }

    public static boolean isOmitDimUIProduct() { //딤 처리를 하지 않는 제품 (shadow나 스킨때문에 못한다..) -> 편집기 진입 시 얼굴 맞추기 기능 실행될때 어두워 지게 할것인가 아닌가 셋팅하는 것.
        return Const_PRODUCT.isLegacyPhoneCaseProduct() || Const_PRODUCT.isPrintPhoneCaseProduct() || Const_PRODUCT.isUvPhoneCaseProduct()
                || (!Config.isCalendar() && Const_PRODUCT.isFrameProduct()) || Const_PRODUCT.isTransparencyPhotoCardProduct();
    }

    public static void setSmartSnapsProgressClipArea(Activity activity, SnapsProductEditControls editControls) throws Exception {
        if (editControls == null || isOmitDimUIProduct()) {
            return;
        }
        SnapsClippingDimLayout dimLayout = editControls.getSmartSnapsSearchProgressDimLayout();
        if (dimLayout != null) {
            dimLayout.setDimmedAreaRect(activity);
        }
    }

    //템플릿에 속해 있는 모든 이미지의 얼굴 영역을 맞춰 준다.
    public static void fixImageLayerAreaOnTemplateBySmartSnapsInfo(Activity activity, SnapsTemplate snapsTemplate) throws Exception {
        if (snapsTemplate == null) {
            return;
        }

        for (int index = 0; index < snapsTemplate.getPages().size(); index++) {
            SnapsPage page = snapsTemplate.getPages().get(index);
            for (int i = 0; i < page.getLayoutList().size(); i++) {
                SnapsLayoutControl layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                if (layout == null || layout.imgData == null) {
                    continue;
                }

                SmartSnapsUtil.fixLayoutControlCropAreaBySmartSnapsAreaInfo(activity, layout);
            }
        }
    }

    //분석 서버에서 받은 템플릿에 analysisImageKey를 매칭하여 선택한 이미지를 하나씩 넣어준다.
    public static void insertImageListToTemplateByAnalysisKey(Activity activity, SnapsTemplate snapsTemplate, ArrayList<MyPhotoSelectImageData> imageDataList, boolean onlyImageID) throws Exception {
        if (activity == null || snapsTemplate == null || imageDataList == null) {
            return;
        }

        List<MyPhotoSelectImageData> cloneList = (List<MyPhotoSelectImageData>) imageDataList.clone();

        for (int index = 0; index < snapsTemplate.getPages().size(); index++) {
            if (cloneList.isEmpty()) {
                break;
            }

            SnapsPage page = snapsTemplate.getPages().get(index);
            for (int i = 0; i < page.getLayoutList().size(); i++) {
                if (cloneList.isEmpty()) {
                    break;
                }

                SnapsLayoutControl layout = (SnapsLayoutControl) page.getLayoutList().get(i);

                String analysisImageKey = layout.analysisImageKey;
                if (StringUtil.isEmpty(analysisImageKey)) {
                    Dlog.d("insertImageListToTemplateByAnalysisKey() analysisImageKey is Empty:" + layout.getPageIndex());
                    continue;
                }

                MyPhotoSelectImageData findImageData = findImageDataByKey(cloneList, analysisImageKey, onlyImageID);
                Dlog.d("insertImageListToTemplateByAnalysisKey()"
                        + "analysisImageKey:" + analysisImageKey + ", findImageData:" + findImageData);
                if (findImageData != null) {
                    layout.imgData = findImageData;
                    SmartSnapsUtil.initPageFullPositionBySmartImageAreaInfo(activity, layout.imgData, layout);

                    try {
                        layout.isNoPrintImage = ResolutionUtil.isEnableResolution(Float.parseFloat(snapsTemplate.info.F_PAGE_MM_WIDTH), Integer.parseInt(snapsTemplate.info.F_PAGE_PIXEL_WIDTH), layout);
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
            }
        }
    }

    //빠진 사진이 있는지 체크
    public static boolean isExistEmptyImageLayerOnTemplate(SnapsTemplate snapsTemplate) throws Exception {
        if (snapsTemplate == null) {
            return false;
        }

        for (int index = 0; index < snapsTemplate.getPages().size(); index++) {
            SnapsPage page = snapsTemplate.getPages().get(index);
            for (int i = 0; i < page.getLayoutList().size(); i++) {
                SnapsLayoutControl layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                if (layout == null || layout.type == null || !layout.type.equalsIgnoreCase("browse_file")) {
                    continue;
                }

                if (layout.imgData == null || StringUtil.isEmpty(layout.imgData.PATH)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static MyPhotoSelectImageData findImageDataByKey(List<MyPhotoSelectImageData> imageDataList, String analysisImageKey, boolean onlyImageID) throws Exception {
        if (imageDataList == null || StringUtil.isEmpty(analysisImageKey)) {
            return null;
        }

        for (MyPhotoSelectImageData imageData : imageDataList) {
            if (imageData == null) {
                continue;
            }
            if (analysisImageKey.equalsIgnoreCase(onlyImageID ? imageData.IMAGE_ID + "" : String.valueOf(imageData.getImageSelectMapKey()))) {
                imageDataList.remove(imageData);
                return imageData;
            }
        }

        return null;
    }

    private static MyPhotoSelectImageData findWithImageDataType(List<MyPhotoSelectImageData> imageDataList, SmartSnapsConstants.eSmartSnapsAnalysisImagePageType pageType) throws Exception {
        if (imageDataList == null) {
            return null;
        }
        for (MyPhotoSelectImageData imageData : imageDataList) {
            if (imageData == null) {
                continue;
            }
            if (imageData.getPageType() == pageType) {
                return imageData;
            }
        }

        return null;
    }

    public static String removeDesignIdPrefix(String multiTemplateId) {
        if (multiTemplateId == null) {
            return null;
        }

        final String prefixSS_ = "SS_";
        if (multiTemplateId.startsWith(prefixSS_)) {
            multiTemplateId = multiTemplateId.substring(prefixSS_.length());
        }

        return multiTemplateId;
    }

    public static void refreshSmartSnapsImgInfoOnNewLayoutWithImgList(Activity activity, SnapsTemplate snapsTemplate, List<MyPhotoSelectImageData> imageDataList, int pageIdx) throws Exception {
        if (activity == null || snapsTemplate == null || imageDataList == null) {
            return;
        }

        for (MyPhotoSelectImageData imageData : imageDataList) {
            if (imageData == null || !imageData.isSmartSnapsSupport() || imageData.getSmartSnapsImgInfo() == null || !imageData.isFindSmartSnapsFaceArea()) {
                continue;
            }
            try {
                SmartSnapsUtil.fixLayoutControlCropAreaBySmartSnapsAreaInfo(activity, snapsTemplate, imageData);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }

            SmartSnapsUtil.setSmartImgDataStateReadyOnChangeLayout(imageData, pageIdx);
            imageData.requestSmartSnapsAnimation();
        }
    }

    public static void setSmartSnapsImgInfoOnImageData(MyPhotoSelectImageData imageData, int pageIndex, SmartSnapsConstants.eSmartSnapsImgState state) throws Exception {
        if (imageData == null || pageIndex < 0) {
            return;
        }
        SmartSnapsImgInfo smartSnapsImgInfo = SmartSnapsImgInfo.createImgInfo(pageIndex);
        imageData.setSmartSnapsImgInfo(smartSnapsImgInfo);

        changeSmartSnapsImgStateWithImageData(imageData, state);
    }

    public static void setSmartSnapsImgInfoOnImageData(MyPhotoSelectImageData imageData, int pageIndex) throws Exception {
        setSmartSnapsImgInfoOnImageData(imageData, pageIndex, SmartSnapsConstants.eSmartSnapsImgState.READY);
    }

    public static void setSmartSnapsImgInfoWithImageList(ArrayList<MyPhotoSelectImageData> imageList) throws Exception {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct() || imageList == null) {
            return;
        }

        for (MyPhotoSelectImageData imageData : imageList) {
            setSmartSnapsImgInfoOnImageData(imageData, 0, SmartSnapsConstants.eSmartSnapsImgState.NONE);
        }
    }

    public static void setSmartImgDataStateReadyOnChangeLayout(MyPhotoSelectImageData imageData, int index) throws Exception {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct()) {
            return;
        }

        if (SmartSnapsUtil.shouldRestartSmartSnapsAnimationOnChangeDesign(imageData)) {
            SmartSnapsUtil.changeSmartSnapsImgStateWithImageData(imageData, SmartSnapsConstants.eSmartSnapsImgState.RECEIVE_SMART_SNAPS_INFO);
        } else {
            SmartSnapsUtil.setSmartSnapsImgInfoOnImageData(imageData, index);
        }
    }

    public static boolean shouldRestartSmartSnapsAnimationOnChangeDesign(MyPhotoSelectImageData imageData) {
        if (imageData == null || imageData.getSmartSnapsImgInfo() == null || imageData.isEditedImage()) {
            return false;
        }
        return imageData.getSmartSnapsImgInfo().getSmartSnapsImgState() == SmartSnapsConstants.eSmartSnapsImgState.RECEIVE_SMART_SNAPS_INFO
                || imageData.getSmartSnapsImgInfo().getSmartSnapsImgState() == SmartSnapsConstants.eSmartSnapsImgState.FINISH_ANIMATION;
    }

    public static void changeSmartSnapsImgStateWithImageData(MyPhotoSelectImageData imageData, SmartSnapsConstants.eSmartSnapsImgState state) throws Exception {
        if (imageData == null || state == null) {
            return;
        }
        SmartSnapsImgInfo smartSnapsImgInfo = imageData.getSmartSnapsImgInfo();
        if (smartSnapsImgInfo == null) {
            return;
        }

        synchronized (imageData.getSmartSnapsImgInfo()) {
            smartSnapsImgInfo.setSmartSnapsImgState(state);
        }
    }

    public static boolean isValidSmartImageAreaInfo(SmartSnapsImageAreaInfo smartSnapsImageAreaInfo, MyPhotoSelectImageData uploadImageData) {
        if (smartSnapsImageAreaInfo == null || uploadImageData == null) {
            return false;
        }

        try {
            boolean isExistAllSmartImageAreaInfo = smartSnapsImageAreaInfo.isExistAllInfoInSmartImageArea();
            boolean isValidRotateInfo = isValidRotateOfSmartImageAreaInfo(smartSnapsImageAreaInfo, uploadImageData);
            boolean isValidImageRatio = isValidImageRatioOfSmartImageAreaInfo(smartSnapsImageAreaInfo, uploadImageData);
            //TODO  가끔 서버에서 w, h 가 바뀌어서 들어와서 isValidImageRatio가 false로 떨어지는 경우가 있다. exif 자체 정보 오류라고 한다.

            return isExistAllSmartImageAreaInfo && isValidRotateInfo && isValidImageRatio;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return false;
    }

    public static SmartSnapsImageAreaInfo createSmartSnapsImageAreaInfoByUploadResultStrArr(String[] uploadResultStrArr) {
        if (uploadResultStrArr == null || uploadResultStrArr.length < 13) {
            return null;
        }
        String imageAreaInfoJson = uploadResultStrArr[12];

        return parseSmartSnapsImageAreaInfoJson(imageAreaInfoJson);
    }

    private static boolean isValidImageRatioOfSmartImageAreaInfo(SmartSnapsImageAreaInfo smartSnapsImageAreaInfo, MyPhotoSelectImageData uploadImageData) {
        try {
            BSize uploadedThumbnailImageSize = smartSnapsImageAreaInfo.getUploadedImageThumbnailSize();
            float uploadedImageWidth = uploadedThumbnailImageSize.getWidth();
            float uploadedImageHeight = uploadedThumbnailImageSize.getHeight();

            float imageWidth = Float.parseFloat(uploadImageData.F_IMG_WIDTH);
            float imageHeight = Float.parseFloat(uploadImageData.F_IMG_HEIGHT);

            float uploadedImageRatio = uploadedImageWidth / uploadedImageHeight;
            float localImageRatio = imageWidth / imageHeight;

            Dlog.d("isValidImageRatioOfSmartImageAreaInfo() uploadedImageOrientation:"
                    + smartSnapsImageAreaInfo.getUploadedImageOrientation() + ", uploadedImageRatio:" + uploadedImageRatio + ", local ori:"
                    + uploadImageData.ROTATE_ANGLE + ", localImageRatio:" + localImageRatio);
            return uploadedImageRatio != 0 && localImageRatio != 0 && Math.abs(uploadedImageRatio - localImageRatio) < .2;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    private static boolean isValidRotateOfSmartImageAreaInfo(SmartSnapsImageAreaInfo smartSnapsImageAreaInfo, MyPhotoSelectImageData uploadImageData) throws Exception {
        return uploadImageData.ROTATE_ANGLE == smartSnapsImageAreaInfo.getUploadedImageOrientation();
    }

    private static SmartSnapsImageAreaInfo parseSmartSnapsImageAreaInfoJson(String json) {
        if (StringUtil.isEmpty(json)) {
            return null;
        }
        Dlog.d("parseSmartSnapsImageAreaInfoJson() json:" + json);

        SmartSnapsImageAreaInfo imageAreaInfo = null;
        JSONObject jObj = null;
        try {
//            {"img":{"meta":{"dt":"","ot":0,"w":-1,"h":-1,"tw":446,"th":800},"fd_thum":{"x":-1,"y":-1,"xw":-1,"yh":-1,"w":0,"h":0,"fn":-1}}}
            jObj = new JSONObject(json);
            JSONObject imgObj = (JSONObject) jObj.get("img");
            if (imgObj != null) {
                imageAreaInfo = new SmartSnapsImageAreaInfo();

                if (!imgObj.isNull("fd_thum")) {
                    JSONObject fdObj = (JSONObject) imgObj.get("fd_thum");
                    Integer fn = (Integer) fdObj.get("fn");
                    Integer x = (Integer) fdObj.get("x");
                    Integer xw = (Integer) fdObj.get("xw");
                    Integer y = (Integer) fdObj.get("y");
                    Integer yh = (Integer) fdObj.get("yh");

                    imageAreaInfo.setSearchedAreaCount(fn);

                    BRect searchAreaRect = new BRect();
                    searchAreaRect.set(x, y, xw, yh);
                    imageAreaInfo.setSearchedAreaRect(searchAreaRect);

                    imageAreaInfo.setJsonStrFromServer(fdObj.toString());
                }

                if (!imgObj.isNull("meta")) {
                    JSONObject metaObj = (JSONObject) imgObj.get("meta");
                    Integer w = (Integer) metaObj.get("w");
                    Integer h = (Integer) metaObj.get("h");
                    Integer ot = (Integer) metaObj.get("ot");
                    String dt = (String) metaObj.get("dt");
                    Integer tw = (Integer) metaObj.get("tw");
                    Integer th = (Integer) metaObj.get("th");

                    imageAreaInfo.setUploadedImageSize(new BSize(w, h));
                    imageAreaInfo.setUploadedImageThumbnailSize(new BSize(tw, th));
                    imageAreaInfo.setUploadedImageOrientationTag(ot);
                    imageAreaInfo.setUploadedImageOrientation(SmartSnapsImgInfo.convertOtToAngle(ot));
                    imageAreaInfo.setDateInfo(dt);
                }
            }
        } catch (JSONException e) {
            Dlog.e(TAG, e);
        }

        return imageAreaInfo;
    }

    public static SmartSnapsImageAreaInfo parseRecommendAIFaceDetectionImageAreaInfoJson(JSONObject imgObj) {

        SmartSnapsImageAreaInfo imageAreaInfo = null;
        try {
            if (imgObj != null) {
                imageAreaInfo = new SmartSnapsImageAreaInfo();

                if (!imgObj.isNull("fd_thum")) {

                    JSONObject fdObj = (JSONObject) imgObj.get("fd_thum");
                    Integer fn = (Integer) fdObj.get("fn");
                    Integer x = (Integer) fdObj.get("x");
                    Integer xw = (Integer) fdObj.get("xw");
                    Integer y = (Integer) fdObj.get("y");
                    Integer yh = (Integer) fdObj.get("yh");

                    imageAreaInfo.setSearchedAreaCount(fn);

                    BRect searchAreaRect = new BRect();
                    searchAreaRect.set(x, y, xw, yh);
                    imageAreaInfo.setSearchedAreaRect(searchAreaRect);

                    imageAreaInfo.setJsonStrFromServer(fdObj.toString());
                }

                if (!imgObj.isNull("meta")) {
                    JSONObject metaObj = (JSONObject) imgObj.get("meta");
                    Integer w = (Integer) metaObj.get("w");
                    Integer h = (Integer) metaObj.get("h");
                    Integer ot = (Integer) metaObj.get("ot");
                    String dt = (String) metaObj.get("dt");
                    Integer tw = (Integer) metaObj.get("tw");
                    Integer th = (Integer) metaObj.get("th");

                    // 원본 사이즈로 전달되는 경우 썸네일 크기로 계산해준다
                    if (tw > SnapsThumbnailMaker.THUMBNAIL_SIZE_OFFSET || th > SnapsThumbnailMaker.THUMBNAIL_SIZE_OFFSET) {
                        float ratio = 0.0f;
                        if (tw > th) {
                            ratio = (float) SnapsThumbnailMaker.THUMBNAIL_SIZE_OFFSET / (float) tw;
                            tw = SnapsThumbnailMaker.THUMBNAIL_SIZE_OFFSET;
                            th = (int) (ratio * th);
                        } else {
                            ratio = (float) SnapsThumbnailMaker.THUMBNAIL_SIZE_OFFSET / (float) th;
                            th = SnapsThumbnailMaker.THUMBNAIL_SIZE_OFFSET;
                            tw = (int) (ratio * tw);
                        }
                    }

                    imageAreaInfo.setUploadedImageSize(new BSize(w, h));
                    imageAreaInfo.setUploadedImageThumbnailSize(new BSize(tw, th));
                    imageAreaInfo.setUploadedImageOrientationTag(ot);
                    imageAreaInfo.setUploadedImageOrientation(SmartSnapsImgInfo.convertOtToAngle(ot));
                    imageAreaInfo.setDateInfo(dt);
                }
            }
        } catch (JSONException e) {
            Dlog.e(TAG, e);
        }

        return imageAreaInfo;
    }

    private static boolean shouldCalculateLayoutControlCoordinate(BSize imageAreaSize) throws Exception {
        return imageAreaSize.getWidth() == 0 || imageAreaSize.getHeight() == 0;
    }

    private static void calculateLayoutControlCoordinate(Context context, SnapsLayoutControl layoutControl, MyPhotoSelectImageData data) throws Exception {
        ImageCoordinateCalculator.setLayoutControlCoordinateInfo(context, layoutControl);

        checkImageRatioErr(context, layoutControl, data);
    }

    private static void checkImageRatioErr(Context context, SnapsLayoutControl layoutControl, MyPhotoSelectImageData data) throws Exception {
        String[] imageRc = layoutControl.getRc().replace(" ", "|").split("\\|");
        String[] imageRcClip = layoutControl.getRcClip().replace(" ", "|").split("\\|");
        imageRc = BitmapUtil.checkImageRatio(data, imageRc, imageRcClip);
        boolean isWrongRatio = false;
        if (imageRc != null && imageRc.length >= 4) {
            try {
                float rectW = Float.parseFloat(imageRc[2]);
                float rectH = Float.parseFloat(imageRc[3]);
                float imgW = Float.parseFloat(data.F_IMG_WIDTH);
                float imgH = Float.parseFloat(data.F_IMG_HEIGHT);
                isWrongRatio = (imgW > imgH && rectW < rectH) || (imgW < imgH && rectW > rectH);
                if (!isWrongRatio) {
                    if ((imageRc[2] != null && imageRc[2].trim().equals("0"))
                            || (imageRc[3] != null && imageRc[3].trim().equals("0"))) {
                        Dlog.d("checkImageRatioErr() imageRc[2] == 0 || imageRc[3] == 0");
                        isWrongRatio = true;
                    }
                } else {
                    Dlog.d("checkImageRatioErr() isWrongRatio is true");
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
                isWrongRatio = true;
            }
        } else {
            Dlog.d("checkImageRatioErr() [imageRc.length >= 4]  <- false");
            isWrongRatio = true;
        }

        if (isWrongRatio) {
            Dlog.w(TAG, "checkImageRatioErr() isWrongRatio");
            layoutControl.imgData = null;
            layoutControl.srcTargetType = Const_VALUE.USERIMAGE_TYPE;

            layoutControl.srcTarget = "";
            layoutControl.resourceURL = "";

            SnapsAssert.assertTrue(false);
        }
    }

    public static void fixLayoutControlCropAreaBySmartSnapsAreaInfo(@NonNull Activity activity, @NonNull SnapsTemplate template, @NonNull MyPhotoSelectImageData uploadedImageData) throws Exception {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct() || !SmartSnapsManager.isSmartAreaSearching()) {
            return;
        }
        SnapsLayoutControl layoutControl = findLayoutControlWithImageData(template, uploadedImageData);
        if (layoutControl != null) {
            fixLayoutControlCropAreaBySmartSnapsAreaInfo(activity, layoutControl);
        } else {
            Dlog.d("fixLayoutControlCropAreaBySmartSnapsAreaInfo() can't not find layoutControl.");
        }
    }

    private static SnapsLayoutControl findLayoutControlWithImageData(@NonNull SnapsTemplate template, MyPhotoSelectImageData targetImageData) throws Exception {
        for (int index = 0; index < template.getPages().size(); index++) {
            SnapsPage page = template.getPages().get(index);

            for (int i = 0; i < page.getLayoutList().size(); i++) {
                SnapsLayoutControl layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                if (layout.imgData != null && layout.imgData == targetImageData) {
                    return layout;
                }
            }
        }
        return null;
    }

    private static boolean checkValidSmartSnapsAreaInfo(MyPhotoSelectImageData imgData) {
        if (imgData == null) {
            Dlog.d("checkValidSmartSnapsAreaInfo() image data is null.");
            return false;
        }

//        AdjustableCropInfo adjustableCropInfo = imgData.ADJ_CROP_INFO;
        SmartSnapsImageAreaInfo imageAreaInfo = imgData.getSmartSnapsImageAreaInfo();
        if (imgData.getSmartSnapsImageAreaInfo() == null) {
            Dlog.d("checkValidSmartSnapsAreaInfo() Don't created smart snaps image area info : " + imgData.F_IMG_NAME);
            return false;
        }

        if (imgData.isEditedImage()) {  //TODO  만약 사용자가 이미 편집한 흔적이 있다면 보정하지 않는 코드다 기획적으로 확정 된 내용이 없다
            Dlog.d("checkValidSmartSnapsAreaInfo() already edited image : " + imgData.F_IMG_NAME);
            return false;
        }

        boolean isFailedFindSmartArea = imageAreaInfo == null || imgData.getSmartSnapsImgInfo() == null || imgData.getSmartSnapsImgInfo().isFailedSearchFace();
        if (isFailedFindSmartArea) {
            Dlog.d("checkValidSmartSnapsAreaInfo() smart snaps failed search area.");
            if (Config.useDrawSmartSnapsImageArea()) {
                imgData.setSmartSnapsImageAreaInfo(imageAreaInfo);
            }
            return false;
        }

        return true;
    }

//    private static BSize getFixedSmartSnapsImageSizeByImage(Activity activity, MyPhotoSelectImageData imgData, SnapsLayoutControl layoutControl) throws Exception {
//        BSize imageAreaSize = getBSizeFromArrayStr(layoutControl.getRc());
////        if (imgData.ROTATE_ANGLE == 90 || imgData.ROTATE_ANGLE == 270) {
////            int temp = (int) imageAreaSize.getUserSelectWidth();
////            imageAreaSize.setWidth(imageAreaSize.getHeight());
////            imageAreaSize.setHeight(temp);
////        }
//
//        if (shouldCalculateLayoutControlCoordinate(imageAreaSize)) {
//            calculateLayoutControlCoordinate(activity, layoutControl, imgData);
//            imageAreaSize = getBSizeFromArrayStr(layoutControl.getRc());
//        }
//
//        if (imgData.ROTATE_ANGLE == 90 || imgData.ROTATE_ANGLE == 270) {
//            int temp = (int) imageAreaSize.getUserSelectWidth();
//            imageAreaSize.setWidth(imageAreaSize.getHeight());
//            imageAreaSize.setHeight(temp);
//        }
//
//        return imageAreaSize;
//    }

    private static BSize getFixedSmartSnapsImageSizeByImage(Activity activity, MyPhotoSelectImageData imgData, SnapsLayoutControl layoutControl) throws Exception {
        BSize imageAreaSize = StringUtil.getBSizeFromArrayStr(layoutControl.getRc());

        if (shouldCalculateLayoutControlCoordinate(imageAreaSize)) {
            calculateLayoutControlCoordinate(activity, layoutControl, imgData);
            imageAreaSize = StringUtil.getBSizeFromArrayStr(layoutControl.getRc());

//            float imageWidth = 0, imageHeight = 0;
//            try {
//                imageWidth = (int) Float.parseFloat(layoutControl.imgData.F_IMG_WIDTH);
//                imageHeight = (int) Float.parseFloat(layoutControl.imgData.F_IMG_HEIGHT);
//            } catch (NumberFormatException e) { Dlog.e(TAG, e); }
//
//            imageAreaSize = new BSize(imageWidth, imageHeight);
        }

        if (imgData.ROTATE_ANGLE == 90 || imgData.ROTATE_ANGLE == 270) {
            int temp = (int) imageAreaSize.getWidth();
            imageAreaSize.setWidth(imageAreaSize.getHeight());
            imageAreaSize.setHeight(temp);
        }

        return imageAreaSize;
    }

    public static void initPageFullPositionBySmartImageAreaInfo(Activity activity, MyPhotoSelectImageData imgData, SnapsLayoutControl layoutControl) throws Exception {
        if (imgData == null) {
            return;
        }
        SmartSnapsImageAreaInfo imageAreaInfo = imgData.getSmartSnapsImageAreaInfo();
        if (imageAreaInfo == null) {
            return;
        }

        SmartSnapsLayoutControlInfo snapsLayoutControlInfo = SmartSnapsUtil.createSmartSnapsLayoutControlInfo(activity, imgData, layoutControl);
        imageAreaInfo.calculateCropOrientation(snapsLayoutControlInfo);

        int layoutControlClipRectWidth = imageAreaInfo.getClipRectWidthInt(snapsLayoutControlInfo);
        int layoutControlClipRectHeight = imageAreaInfo.getClipRectHeightInt(snapsLayoutControlInfo);
        int layoutControlImageRectWidth = imageAreaInfo.getImageWidthInt(snapsLayoutControlInfo);
        int layoutControlImageRectHeight = imageAreaInfo.getImageHeightInt(snapsLayoutControlInfo);

        BitmapUtil.getPageFullPosition(layoutControlClipRectWidth, layoutControlClipRectHeight, layoutControlImageRectWidth, layoutControlImageRectHeight, imgData);
    }

    private static SmartSnapsLayoutControlInfo createSmartSnapsLayoutControlInfo(Activity activity, MyPhotoSelectImageData imgData, SnapsLayoutControl layoutControl) throws Exception {
        BSize imageAreaSize = getFixedSmartSnapsImageSizeByImage(activity, imgData, layoutControl);

        return new SmartSnapsLayoutControlInfo.Builder()
                .setImageSize(imageAreaSize)
                .setClipRectInfo(layoutControl.getRcClip())
                .setImageData(imgData)
                .create();
    }

    private static void setPageFullPositionWithSmartSnapsAreaInfo(@NonNull SmartSnapsImageAreaInfo imageAreaInfo,
                                                                  @NonNull SmartSnapsLayoutControlInfo snapsLayoutControlInfo,
                                                                  @NonNull MyPhotoSelectImageData imgData) throws Exception {
        int layoutControlClipRectWidth = imageAreaInfo.getClipRectWidthInt(snapsLayoutControlInfo);
        int layoutControlClipRectHeight = imageAreaInfo.getClipRectHeightInt(snapsLayoutControlInfo);
        int layoutControlImageRectWidth = imageAreaInfo.getImageWidthInt(snapsLayoutControlInfo);
        int layoutControlImageRectHeight = imageAreaInfo.getImageHeightInt(snapsLayoutControlInfo);

        String pageFullPosition = BitmapUtil.getPageFullPosition(layoutControlClipRectWidth, layoutControlClipRectHeight, layoutControlImageRectWidth, layoutControlImageRectHeight, imgData);

        if (!checkValidAreaWithPageFullPosition(pageFullPosition, snapsLayoutControlInfo, imgData)) {
            Dlog.d("setPageFullPositionWithSmartSnapsAreaInfo() is not valid PageFullPosition");
            //초기화 시킴
            imgData.isAdjustableCropMode = false;
            imgData.ADJ_CROP_INFO = new AdjustableCropInfo();
            imgData.FREE_ANGLE = 0;
            imgData.RESTORE_ANGLE = SnapsImageDownloader.INVALID_ANGLE;
            BitmapUtil.getPageFullPosition(layoutControlClipRectWidth, layoutControlClipRectHeight, layoutControlImageRectWidth, layoutControlImageRectHeight, imgData);
        }
    }

    private static boolean checkValidAreaWithPageFullPosition(String pageFullPosition, SmartSnapsLayoutControlInfo snapsLayoutControlInfo, MyPhotoSelectImageData imageData) {
        if (pageFullPosition == null || snapsLayoutControlInfo == null || snapsLayoutControlInfo.getClipRect() == null || imageData == null) {
            return false;
        }
        BRect imageRect = BRect.createBRectWithRcStr(pageFullPosition);
        BRect clipRect = snapsLayoutControlInfo.getClipRect();
        if (!isValidClipRect(imageRect) || !isValidClipRect(clipRect)) {
            return false;
        }

        int angle = imageData.ROTATE_ANGLE;
        int fixedX = clipRect.left + imageRect.left;
        int fixedY = clipRect.top + imageRect.top;

        int imageCenterX = fixedX + (imageRect.width() / 2);
        int imageCenterY = fixedY + (imageRect.height() / 2);

        PointF pivot = new PointF(imageCenterX, imageCenterY);
        PointF imagePt = new PointF(fixedX, fixedY);

        BRect rotatedRect = getRotatedRect(imagePt, imageRect, pivot, angle);
        return isValidImageRectAreaWithClipRect(rotatedRect, clipRect);
    }

    private static boolean isValidImageRectAreaWithClipRect(BRect imageRect, BRect clipRect) {
        if (imageRect == null || clipRect == null) {
            return false;
        }

        int diff = imageRect.left - clipRect.left;
        int ALLOW_ERROR_PIXEL_FOR_IMAGE_RECT_AREA = 3;
        if (diff > ALLOW_ERROR_PIXEL_FOR_IMAGE_RECT_AREA) {
            Dlog.d("isValidImageRectAreaWithClipRect() is failed [imageRect.left - clipRect.left]");
            return false;
        }

        diff = clipRect.right - imageRect.right;
        if (diff > ALLOW_ERROR_PIXEL_FOR_IMAGE_RECT_AREA) {
            Dlog.d("isValidImageRectAreaWithClipRect() is failed [clipRect.right - imageRect.right]");
            return false;
        }

        diff = imageRect.top - clipRect.top;
        if (diff > ALLOW_ERROR_PIXEL_FOR_IMAGE_RECT_AREA) {
            Dlog.d("isValidImageRectAreaWithClipRect() is failed [imageRect.top - clipRect.top]");
            return false;
        }

        diff = clipRect.bottom - imageRect.bottom;
        if (diff > ALLOW_ERROR_PIXEL_FOR_IMAGE_RECT_AREA) {
            Dlog.d("isValidImageRectAreaWithClipRect() is failed [clipRect.bottom - imageRect.bottom]");
            return false;
        }

        return true;
    }

    private static BRect getRotatedRect(PointF imagePoint, BRect imgRect, PointF pivotPoint, int degree) {
        if (imagePoint == null || imgRect == null || pivotPoint == null
                || (degree != 0 && degree != 90 && degree != 180 && degree != 270)) {
            return new BRect();
        }

        int rotatedImageWidth = imgRect.width();
        int rotatedImageHeight = imgRect.height();
        int convertedPointX = (int) imagePoint.x;
        int convertedPointY = (int) imagePoint.y;

        switch (degree) {
            case 0:
                break;
            case 90:
                convertedPointY += imgRect.height();

                rotatedImageWidth = imgRect.height();
                rotatedImageHeight = imgRect.width();
                break;
            case 180:
                convertedPointX += imgRect.width();
                convertedPointY += imgRect.height();
                break;
            case 270:
                convertedPointX += imgRect.width();

                rotatedImageWidth = imgRect.height();
                rotatedImageHeight = imgRect.width();
                break;
            default:
                SnapsAssert.assertTrue(true); //이 외의 각도는 지원 못한다.
                break;
        }

        PointF convertedPoint = new PointF(convertedPointX, convertedPointY);

        double x, y;
        double rad = Math.toRadians(degree);
        // 이동 후 좌표(x) = 현재 옮기고자 하는 좌표(pivotPoint.x) - x축의 중점(convertedPoint.x)
        x = ((convertedPoint.x - pivotPoint.x) * Math.cos(rad) - (convertedPoint.y - pivotPoint.y) * Math.sin(rad)) + pivotPoint.x;
        y = ((convertedPoint.x - pivotPoint.x) * Math.sin(rad) + (convertedPoint.y - pivotPoint.y) * Math.cos(rad)) + pivotPoint.y;

        return new BRect((int) x, (int) y, (int) x + rotatedImageWidth, (int) y + rotatedImageHeight);
    }

    private static boolean isValidClipRect(BRect clipRect) {
        return clipRect != null && clipRect.width() > 0 && clipRect.height() > 0;
    }

    public static void fixLayoutControlCropAreaBySmartSnapsAreaInfo(@NonNull Activity activity, @NonNull SnapsLayoutControl layoutControl) throws Exception {
        MyPhotoSelectImageData imgData = layoutControl.imgData;

        if (!checkValidSmartSnapsAreaInfo(imgData)) {
            return;
        }

        Dlog.d("fixLayoutControlCropAreaBySmartSnapsAreaInfo() find! smart snaps photo! : " + imgData.PATH);

        try {
            AdjustableCropInfo adjustableCropInfo = imgData.ADJ_CROP_INFO;

            SmartSnapsImageAreaInfo imageAreaInfo = imgData.getSmartSnapsImageAreaInfo();

            SmartSnapsLayoutControlInfo snapsLayoutControlInfo = createSmartSnapsLayoutControlInfo(activity, imgData, layoutControl);

            imageAreaInfo.calculateCropOrientation(snapsLayoutControlInfo);

            adjustableCropInfo.setClipRect(createClipRectFromImageAreaInfo(imageAreaInfo, snapsLayoutControlInfo));
            adjustableCropInfo.setImgRect(createImageRectFromImageAreaInfo(imageAreaInfo, snapsLayoutControlInfo));

            imgData.isAdjustableCropMode = true;

            setPageFullPositionWithSmartSnapsAreaInfo(imageAreaInfo, snapsLayoutControlInfo, imgData);
        } catch (Exception e) {
            SnapsAssert.assertException(activity, e);
            Dlog.e(TAG, e);
            imgData.ADJ_CROP_INFO = new AdjustableCropInfo();
            imgData.isAdjustableCropMode = false;
        }
    }

    private static AdjustableCropInfo.CropImageRect createClipRectFromImageAreaInfo(SmartSnapsImageAreaInfo imageAreaInfo, SmartSnapsLayoutControlInfo snapsLayoutControlInfo) throws Exception {
        AdjustableCropInfo.CropImageRect clipRect = new AdjustableCropInfo.CropImageRect();
        clipRect.width = imageAreaInfo.getClipRectWidthInt(snapsLayoutControlInfo);
        clipRect.height = imageAreaInfo.getClipRectHeightInt(snapsLayoutControlInfo);
        return clipRect;
    }

    private static AdjustableCropInfo.CropImageRect createImageRectFromImageAreaInfo(SmartSnapsImageAreaInfo imageAreaInfo, SmartSnapsLayoutControlInfo snapsLayoutControlInfo) throws Exception {
        AdjustableCropInfo.CropImageRect imgRect = new AdjustableCropInfo.CropImageRect();
        imgRect.width = imageAreaInfo.getImageWidthInt(snapsLayoutControlInfo);
        imgRect.height = imageAreaInfo.getImageHeightInt(snapsLayoutControlInfo);

        Dlog.d("createImageRectFromImageAreaInfo()imageAreaInfo dimension : "
                + imgRect.width + ", " + imgRect.height + ", rotate : " + imageAreaInfo.getUploadedImageOrientation());
        imgRect.scaleX = 1.f;
        imgRect.scaleY = 1.f;
        imgRect.rotate = imageAreaInfo.getImageRotation(snapsLayoutControlInfo);

        if (imageAreaInfo.getCropOrientation() == CropInfo.CORP_ORIENT.WIDTH) {
            imgRect.movedX = getMoveXOffsetFromImgCenter(imageAreaInfo, snapsLayoutControlInfo);
            imgRect.movedY = 0;
        } else if (imageAreaInfo.getCropOrientation() == CropInfo.CORP_ORIENT.HEIGHT) {
            imgRect.movedX = 0;
            imgRect.movedY = getMoveYOffsetFromImgCenter(imageAreaInfo, snapsLayoutControlInfo);
        }

        return imgRect;
    }

    private static int getMoveXOffsetFromImgCenter(SmartSnapsImageAreaInfo imageAreaInfo, SmartSnapsLayoutControlInfo snapsLayoutControlInfo) throws Exception {
        int layoutControlImageRectWidth = imageAreaInfo.getImageWidthInt(snapsLayoutControlInfo);
        int layoutControlClipRectWidth = imageAreaInfo.getClipRectWidthInt(snapsLayoutControlInfo);

        BSize uploadedImageSize = imageAreaInfo.getFixedUploadedThumbnailImageSizeByOrientation();
        BRect searchedAreaRect = imageAreaInfo.getSearchedAreaRect();

        if (uploadedImageSize != null && searchedAreaRect != null) {
            float imageCenterX = (uploadedImageSize.getWidth() / 2);
            float moveDistanceX = searchedAreaRect.centerX() - imageCenterX;
            float moveXPercent = moveDistanceX / uploadedImageSize.getWidth();
            float moveXOffsetFromImgCenter = layoutControlImageRectWidth * moveXPercent;

            int minMoveX = (int) ((layoutControlImageRectWidth / 2) - (layoutControlClipRectWidth / 2)) - 1;
            if (moveXOffsetFromImgCenter > 0) {
                moveXOffsetFromImgCenter = Math.min(moveXOffsetFromImgCenter, minMoveX);
                if (moveXOffsetFromImgCenter < 0) {
                    moveXOffsetFromImgCenter = Math.max(moveXOffsetFromImgCenter, -minMoveX);
                }
            } else if (moveXOffsetFromImgCenter < 0) {
                moveXOffsetFromImgCenter = Math.max(moveXOffsetFromImgCenter, -minMoveX);
                if (moveXOffsetFromImgCenter > 0) {
                    moveXOffsetFromImgCenter = Math.min(moveXOffsetFromImgCenter, minMoveX);
                }
            }

            return (int) -moveXOffsetFromImgCenter;
        }

        return 0;
    }

    private static int getMoveYOffsetFromImgCenter(SmartSnapsImageAreaInfo imageAreaInfo, SmartSnapsLayoutControlInfo snapsLayoutControlInfo) throws Exception {
        int layoutControlImageRectHeight = imageAreaInfo.getImageHeightInt(snapsLayoutControlInfo);
        int layoutControlClipRectHeight = imageAreaInfo.getClipRectHeightInt(snapsLayoutControlInfo);

        BSize uploadedImageSize = imageAreaInfo.getFixedUploadedThumbnailImageSizeByOrientation();
        BRect searchedAreaRect = imageAreaInfo.getSearchedAreaRect();

        if (uploadedImageSize != null && searchedAreaRect != null) {
            float imageCenterY = (uploadedImageSize.getHeight() / 2);
            float moveDistanceY = searchedAreaRect.centerY() - imageCenterY;
            float moveYPercent = moveDistanceY / uploadedImageSize.getHeight();
            float moveYOffsetFromImgCenter = layoutControlImageRectHeight * moveYPercent;

            int minMoveY = (int) ((layoutControlImageRectHeight / 2) - (layoutControlClipRectHeight / 2)) - 1;
            if (moveYOffsetFromImgCenter > 0) {
                moveYOffsetFromImgCenter = Math.min(moveYOffsetFromImgCenter, minMoveY);
                if (moveYOffsetFromImgCenter < 0) {
                    moveYOffsetFromImgCenter = Math.max(moveYOffsetFromImgCenter, -minMoveY);
                }
            } else if (moveYOffsetFromImgCenter < 0) {
                moveYOffsetFromImgCenter = Math.max(moveYOffsetFromImgCenter, -minMoveY);
                if (moveYOffsetFromImgCenter > 0) {
                    moveYOffsetFromImgCenter = Math.min(moveYOffsetFromImgCenter, minMoveY);
                }
            }

            return (int) -moveYOffsetFromImgCenter;
        }
        return 0;
    }

    public static Bitmap drawSmartSnapsImageArea(Bitmap bitmap, MyPhotoSelectImageData imageData) {
        if (!Config.useDrawSmartSnapsImageArea() || bitmap == null || bitmap.isRecycled() || imageData == null || imageData.getSmartSnapsImgInfo() == null) {
//            Logg.y("bitmap " + (bitmap == null) + ", bitmap is recycled ? "  + bitmap.isRecycled() + ", cropInfo is null? " + (cropInfo == null));
            return bitmap;
        }

        SmartSnapsImgInfo smartSnapsImgInfo = imageData.getSmartSnapsImgInfo();

        if (smartSnapsImgInfo.isFailedSearchFace()) {
//            Logg.y("!!!! SmartSnapsImageAreaInfo is null on snaps bitmap displayer");
            Bitmap copiedBitmap = CropUtil.getInSampledBitmapCopy(bitmap, Bitmap.Config.ARGB_8888);
            try {
                Paint paint = new Paint();
                paint.setColor(Color.argb(80, 150, 0, 0));
                Canvas canvas = new Canvas(copiedBitmap);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, ContextUtil.getContext().getResources().getDisplayMetrics());
                paint.setStrokeWidth(pixel);
                canvas.drawRect(0, 0, copiedBitmap.getWidth(), copiedBitmap.getHeight(), paint);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
            return copiedBitmap;
        }

        AdjustableCropInfo cropInfo = imageData.ADJ_CROP_INFO;
        if (cropInfo == null) {
            return bitmap;
        }

        SmartSnapsImageAreaInfo imageAreaInfo = smartSnapsImgInfo.getSmartSnapsImageAreaInfo();
        if (imageAreaInfo == null) {
            return bitmap;
        }

        Bitmap copiedBitmap = CropUtil.getInSampledBitmapCopy(bitmap, Bitmap.Config.ARGB_8888);
        try {
            Paint fillPaint = new Paint();
            Paint outlinePaint = new Paint();
            fillPaint.setColor(Color.argb(130, 0, 200, 0));
            outlinePaint.setColor(Color.argb(255, 0, 255, 0));
            Canvas canvas = new Canvas(copiedBitmap);

            BSize uploadedImageSize = imageAreaInfo.getFixedUploadedThumbnailImageSizeByOrientation();

            float ratioWidth = copiedBitmap.getWidth() / (float) uploadedImageSize.getWidth();
            float ratioHeight = copiedBitmap.getHeight() / (float) uploadedImageSize.getHeight();

            BRect searchedAreaRect = imageAreaInfo.getSearchedAreaRect();

            int left = (int) (searchedAreaRect.left * ratioWidth);
            int right = (int) (searchedAreaRect.right * ratioWidth);
            int top = (int) (searchedAreaRect.top * ratioHeight);
            int bottom = (int) (searchedAreaRect.bottom * ratioHeight);

            fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            outlinePaint.setStyle(Paint.Style.STROKE);
            int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, ContextUtil.getContext().getResources().getDisplayMetrics());
            outlinePaint.setStrokeWidth(pixel);
            canvas.drawRect(left, top, right, bottom, fillPaint);
            canvas.drawRect(left, top, right, bottom, outlinePaint);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return copiedBitmap;
    }

    public static Bitmap getAppliedDrawSmartSnapsSearchedArea(ImageView imageView, MyPhotoSelectImageData imageData) {
        try {
            if (!Config.useDrawSmartSnapsImageArea()) {
                return null;
            }

            Drawable drawable = imageView.getDrawable();
            if (drawable == null || !(drawable instanceof BitmapDrawable)) {
                return null;
            }

            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
            return SmartSnapsUtil.drawSmartSnapsImageArea(bitmapDrawable.getBitmap(), imageData);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    public static class SmartSnapsImageAnalysisRequestValue {
        private String projCode;
        private String prodCode;
        private String themeCode;
        private String imageKey;
        private String imageWidth;
        private String imageHeight;
        private String imageOt;
        private String imageExifDate;
        private String imageSysDate;
        private String imageGps;
        private String imageName;
        private String imageTypes;
        private String thumbPaths;
        private String fdThumbnails;

        public boolean checkErrorRequestValue() {
            if (imageTypes == null || !imageTypes.contains("cover") || !imageTypes.contains("title")) {
                return true; //빅데이터팀에서 간혹 cover가 빠져서 들어오는 경우가 있다고 해서 추가한 코드.
            }
            return false;
        }

        public String getImageTypes() {
            return imageTypes;
        }

        public String getThemeCode() {
            return themeCode;
        }

        public void setThemeCode(String themeCode) {
            this.themeCode = themeCode;
        }

        public String getProjCode() {
            return projCode;
        }

        public void setProjCode(String projCode) {
            this.projCode = projCode;
        }

        public String getProdCode() {
            return prodCode;
        }

        public void setProdCode(String prodCode) {
            this.prodCode = prodCode;
        }

        public String getImageKey() {
            return imageKey;
        }

        public String getImageWidth() {
            return imageWidth;
        }

        public String getImageHeight() {
            return imageHeight;
        }

        public String getImageOt() {
            return imageOt;
        }

        public String getImageExifDate() {
            return imageExifDate;
        }

        public String getImageSysDate() {
            return imageSysDate;
        }

        public String getImageGps() {
            return imageGps;
        }

        public String getImageName() {
            return imageName;
        }

        public String getThumbPaths() {
            return thumbPaths;
        }

        public String getFdThumbnails() {
            return fdThumbnails;
        }

        static SmartSnapsImageAnalysisRequestValue createRequestValueWithImageList(ArrayList<MyPhotoSelectImageData> imgList) {
            SmartSnapsImageAnalysisRequestValue requestValue = new SmartSnapsImageAnalysisRequestValue();
            StringBuilder imageKeyBuilder = new StringBuilder();
            StringBuilder imageWidthBuilder = new StringBuilder();
            StringBuilder imageHeightBuilder = new StringBuilder();
            StringBuilder imageOtBuilder = new StringBuilder();
            StringBuilder imageExifDateBuilder = new StringBuilder();
            StringBuilder imageSysDateBuilder = new StringBuilder();
            StringBuilder imageGpsBuilder = new StringBuilder();
            StringBuilder imageNameBuilder = new StringBuilder();
            StringBuilder imagePageTypeBuilder = new StringBuilder();
            StringBuilder imageThumbPathsBuilder = new StringBuilder();
            StringBuilder imageFdThumbJsonStrBuilder = new StringBuilder();

            final String SMART_SNAPS_PRM_SEPARATOR = "#";

            boolean isFirst = true;
            for (MyPhotoSelectImageData imageData : imgList) {
                if (imageData == null) {
                    continue;
                }

                if (!isFirst) {
                    imageKeyBuilder.append(SMART_SNAPS_PRM_SEPARATOR);
                    imageWidthBuilder.append(SMART_SNAPS_PRM_SEPARATOR);
                    imageHeightBuilder.append(SMART_SNAPS_PRM_SEPARATOR);
                    imageOtBuilder.append(SMART_SNAPS_PRM_SEPARATOR);
                    imageExifDateBuilder.append(SMART_SNAPS_PRM_SEPARATOR);
                    imageSysDateBuilder.append(SMART_SNAPS_PRM_SEPARATOR);
                    imageGpsBuilder.append(SMART_SNAPS_PRM_SEPARATOR);
                    imageNameBuilder.append(SMART_SNAPS_PRM_SEPARATOR);
                    imagePageTypeBuilder.append(SMART_SNAPS_PRM_SEPARATOR);
                    imageThumbPathsBuilder.append(SMART_SNAPS_PRM_SEPARATOR);
                    imageFdThumbJsonStrBuilder.append(SMART_SNAPS_PRM_SEPARATOR);
                }

                imageKeyBuilder.append(String.valueOf(imageData.getImageSelectMapKey()));
                imageWidthBuilder.append(imageData.F_IMG_WIDTH);
                imageHeightBuilder.append(imageData.F_IMG_HEIGHT);

                imageSysDateBuilder.append(StringUtil.getSafeStrIfNotValidReturnSubStr(imageData.getImageSystemDateTime(),
                        StringUtil.convertLongTimeToSmartAnalysisFormat(System.currentTimeMillis()))); //필수라서 오늘 날짜라도 넣어 준다.

                try {
                    imageNameBuilder.append(URLEncoder.encode(imageData.F_IMG_NAME, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    Dlog.e(TAG, e);
                    imageNameBuilder.append("");
                }

//                try {
//                    imageThumbPathsBuilder.append(URLEncoder.encode(imageData.THUMBNAIL_PATH, "utf-8"));
                imageThumbPathsBuilder.append(imageData.THUMBNAIL_PATH);
//                } catch (UnsupportedEncodingException e) {
//                    Dlog.e(TAG, e);
//                    imageThumbPathsBuilder.append("");
//                }

                imagePageTypeBuilder.append(StringUtil.getSafeStrIfNotValidReturnSubStr(imageData.getPageType().getTypeStr(), "page"));

                ExifUtil.SnapsExifInfo snapsExifInfo = imageData.getExifInfo();
                if (snapsExifInfo != null) {
                    imageOtBuilder.append(StringUtil.getSafeStrIfNotValidReturnSubStr(snapsExifInfo.getOrientationTag(), "0"));
                    imageExifDateBuilder.append(StringUtil.getSafeStrIfNotValidReturnSubStr(snapsExifInfo.getDate(), ""));
                    imageGpsBuilder.append(StringUtil.getSafeStrIfNotValidReturnSubStr(snapsExifInfo.getLocationStr(), ""));
                } else {
                    imageOtBuilder.append("0");
                    imageExifDateBuilder.append("");
                    imageGpsBuilder.append("");
                }

                imageFdThumbJsonStrBuilder.append(StringUtil.getSafeStrIfNotValidReturnSubStr(imageData.getFdThumbnailJsonStr(), ""));

                isFirst = false;
            }

            requestValue.imageKey = imageKeyBuilder.toString();
            requestValue.imageWidth = imageWidthBuilder.toString();
            requestValue.imageHeight = imageHeightBuilder.toString();
            requestValue.imageOt = imageOtBuilder.toString();
            requestValue.imageExifDate = imageExifDateBuilder.toString();
            requestValue.imageSysDate = imageSysDateBuilder.toString();
            requestValue.imageGps = imageGpsBuilder.toString();
            requestValue.imageName = imageNameBuilder.toString();
            requestValue.imageTypes = imagePageTypeBuilder.toString();
            requestValue.thumbPaths = imageThumbPathsBuilder.toString();
            requestValue.fdThumbnails = imageFdThumbJsonStrBuilder.toString();
            return requestValue;
        }
    }


    public static Map<String, String> recommendAIGetAnalysisKeyList(SnapsTemplate snapsTemplate) {

        Map<String, String> result = new HashMap<>();

        for (int index = 0; index < snapsTemplate.getPages().size(); index++) {

            SnapsPage page = snapsTemplate.getPages().get(index);
            for (int i = 0; i < page.getLayoutList().size(); i++) {
                SnapsLayoutControl layout = (SnapsLayoutControl) page.getLayoutList().get(i);

                String analysisImageKey = layout.analysisImageKey;
                if (StringUtil.isEmpty(analysisImageKey)) {
                    continue;
                }

                String mapKey = Const_VALUES.SELECT_PHONE + "_" + analysisImageKey;

                if (!result.containsKey(mapKey))
                    result.put(mapKey, mapKey);
            }
        }

        if (!StringUtil.isEmpty(snapsTemplate.info.F_COVER_IMAGE_KEY)) {
            String mapKey = Const_VALUES.SELECT_PHONE + "_" + snapsTemplate.info.F_COVER_IMAGE_KEY;
            if (!result.containsKey(mapKey))
                result.put(mapKey, mapKey);
        }

        return result;
    }

    public static MyPhotoSelectImageData recommendAIApplyFaceDetectionArea(GalleryCursorRecord.PhonePhotoFragmentItem photo, JSONObject jsonFaceInfo) {

        MyPhotoSelectImageData imgData = photo.getImgData();

        try {
            setSmartSnapsImgInfoOnImageData(imgData, 0, SmartSnapsConstants.eSmartSnapsImgState.NONE);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        if (jsonFaceInfo != null) {
            SmartSnapsImageAreaInfo imgAreaInfo = SmartSnapsUtil.parseRecommendAIFaceDetectionImageAreaInfoJson(jsonFaceInfo);
            if (SmartSnapsUtil.isValidSmartImageAreaInfo(imgAreaInfo, imgData))
                imgData.setSmartSnapsImageAreaInfo(imgAreaInfo);
        }

        return imgData;
    }

    public static JSONObject recommendAIGetFaceInfoByImageKey(JSONArray jsonArray, Map<String, Integer> faceInfoMap, String imageKey) {
        JSONObject resultObject = null;

        if (faceInfoMap != null && jsonArray != null) {

            int index = faceInfoMap.get(imageKey).intValue();
            try {
                resultObject = jsonArray.getJSONObject(index);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        return resultObject;
    }
}
