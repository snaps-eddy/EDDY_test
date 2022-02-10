package com.snaps.common.utils.net.xml;

import android.content.Context;
import android.util.Log;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.net.CustomMultiPartEntity;
import com.snaps.common.data.net.CustomMultiPartEntity.ProgressListener;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.log.SnapsInterfaceLogListener;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUploadSeqInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUserInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryWriteInfo;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.exceptions.SnapsInvalidImageDataException;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.util.thumb_image_upload.SnapsThumbnailMaker;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;

public class GetMultiPartMethod {
    private static final String TAG = GetMultiPartMethod.class.getSimpleName();

    public static String sendCustomLogFileWithUserId(String userId) throws Exception {
        File customLogFile = SnapsLogger.writeStandardInfoToCustomLogFile(ContextUtil.getContext());

        HttpClient client = new DefaultHttpClient();
        String url = SnapsAPI.DOMAIN() + "/servlet/CommandMultipart.do";
        HttpPost post = new HttpPost(url);

        CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null);
        reqEntity.addPart("part", new StringBody("mall.smartphotolite.SmartPhotoLiteInterface"));
        reqEntity.addPart("cmd", new StringBody("saveMobileLogFile"));

        String customCode = String.valueOf(System.currentTimeMillis());
        reqEntity.addPart("prmProjCode", new StringBody(customCode));
        reqEntity.addPart("userId", new StringBody(userId));

        if (customLogFile != null && customLogFile.exists()) {
            reqEntity.addPart("customLog", new FileBody(customLogFile));
        }

        reqEntity.setTotalCount(reqEntity.getContentLength());

        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);

        String returnValue = null;
        if (response != null) {
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            returnValue = FileUtil.convertStreamToString(is);
        }

        return returnValue;
    }

    public static String getPageThumbImageUpload(SnapsImageUploadRequestData requestData) {
        String returnValue = "";
        try {
            HttpResponse response = getPageThumbImageUpload2(requestData);

            if (response != null) {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                returnValue = FileUtil.convertStreamToString(is);

                if (requestData.getInterfaceLogListener() != null) {
                    requestData.getInterfaceLogListener().onSnapsInterfaceResult(HttpUtil.getHttpResponseStatusCode(response), returnValue);
                }
            }
        } catch (Exception e) {
            SnapsLogger.appendOrderLog("exception snaps photo print at getPageThumbImageUpload : " + e.toString());
            Dlog.e(TAG, e);
            if (requestData.getInterfaceLogListener() != null) {
                requestData.getInterfaceLogListener().onSnapsInterfaceException(e);
            }
        }

        return returnValue;
    }

    /***
     * 파일 첨부
     */
    public static String getAttachFileUpload(File file, ProgressListener listener, SnapsInterfaceLogListener interfaceLogListener) throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(SnapsAPI.DOMAIN() + "/servlet/CommandMultipart.do");
        ContentBody bin = new FileBody(file);

        CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, listener);

        reqEntity.addPart("part", new StringBody("mobile.SetData"));
        reqEntity.addPart("cmd", new StringBody("uploadTemporary"));
        reqEntity.addPart("nextPage", new StringBody("uploadTemporary"));
        reqEntity.addPart("prmChnlCode", new StringBody(Config.getCHANNEL_CODE()));
        reqEntity.addPart("mFile", bin);

        if (interfaceLogListener != null)
            interfaceLogListener.onSnapsInterfacePreRequest("getAttachFileUpload/uploadTemporary");

        reqEntity.setTotalCount(reqEntity.getContentLength());

        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);

        String returnValue = "";

        if (response != null) {
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            returnValue = FileUtil.convertStreamToString(is);

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceResult(HttpUtil.getHttpResponseStatusCode(response), returnValue);
        }

        return returnValue;
    }

    //이게 장바구니 썸네일 이미지 업로드
    public static HttpResponse getPageThumbImageUpload2(SnapsImageUploadRequestData requestData) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(SnapsAPI.DOMAIN() + "/servlet/CommandMultipart.do");

        CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, requestData.getListener());

        reqEntity.addPart("part", new StringBody("mall.smartphotolite.SmartPhotoLiteInterface"));
        reqEntity.addPart("cmd", new StringBody("smartPhotoFlexThumbUploadProc"));
        reqEntity.addPart("nextPage", new StringBody("smartPhotoFlexThumbUploadProc"));
        reqEntity.addPart("userInfo", new StringBody(String.format("userNo=%s&username=%s&useremail=%s", requestData.getUserId(), "", "").trim()));
        reqEntity.addPart("prmprojcode", new StringBody(requestData.getPrjCode()));
        reqEntity.addPart("prmchnlcode", new StringBody(Config.getCHANNEL_CODE()));
        reqEntity.addPart("albmType", new StringBody(requestData.getAlbumType()));
        reqEntity.addPart("fileName", new StringBody("SmartPhotoThumbUploadProcCart"));
        File file = new File(requestData.getFileName());
        ContentBody bin = new FileBody(file);
        reqEntity.addPart("tmbImg", bin);

        String uploadCount = "1";
        if (requestData.isFullSizeThumbnail()) {
            uploadCount = "2";

            File fullSizeFile = new File(requestData.getFullSizeFileName());
            ContentBody fullSizeFileBin = new FileBody(fullSizeFile);
            reqEntity.addPart("midImg", fullSizeFileBin);
        }

        reqEntity.addPart("ThumbCnt", new StringBody(uploadCount));

        reqEntity.setTotalCount(reqEntity.getContentLength());

        if (requestData.getInterfaceLogListener() != null)
            requestData.getInterfaceLogListener().onSnapsInterfacePreRequest("smartPhotoFlexThumbUploadProc");

        post.setEntity(reqEntity);

        HttpResponse response = client.execute(post);

        logServerInfo("getPageThumbImageUpload2()", response);

        return response;
    }

    /**
     * 페이지 리스트 이미지 업로드.
     *
     * @param userid
     * @param pageList
     * @param context
     * @param listener
     * @return
     */
    public static String getPageImageUpload(String userid, ArrayList<String> pageList, Context context, ProgressListener listener, SnapsInterfaceLogListener interfaceLogListener) {
        return getPageImageUpload(userid, pageList, context, "P", listener, interfaceLogListener);
    }

    /**
     * 페이지 리스트 이미지 업로드.
     *
     * @param userid
     * @param pageList
     * @param context
     * @return
     */
    public static String getPageImageUpload(String userid, ArrayList<String> pageList, Context context, String albmType, ProgressListener listener, SnapsInterfaceLogListener interfaceLogListener) {
        return getPageImageUpload(userid, pageList, context, albmType, Config.getPROJ_CODE(), listener, interfaceLogListener);
    }

    public static String getPageImageUpload(String userid, ArrayList<String> pageList, Context context, String albmType, String prjCode, ProgressListener listener, SnapsInterfaceLogListener interfaceLogListener) {
        String returnValue = "";

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(SnapsAPI.DOMAIN() + "/servlet/CommandMultipart.do");
            CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, listener);

            reqEntity.addPart("part", new StringBody("mall.smartphotolite.SmartPhotoLiteInterface"));
            reqEntity.addPart("cmd", new StringBody("smartPhotoFlexThumbUploadProc"));
            reqEntity.addPart("nextPage", new StringBody("smartPhotoFlexThumbUploadProc"));
            reqEntity.addPart("userInfo", new StringBody(String.format("userNo=%s&username=%s&useremail=%s", userid, "", "").trim()));
            reqEntity.addPart("prmprojcode", new StringBody(prjCode));
            reqEntity.addPart("prmchnlcode", new StringBody(Config.getCHANNEL_CODE()));
            reqEntity.addPart("albmType", new StringBody(albmType));
            reqEntity.addPart("ThumbCnt", new StringBody(Integer.toString(pageList.size())));

            for (int i = 0; i < pageList.size(); i++) {
                String imgPath = pageList.get(i);
                File file = new File(imgPath);
                ContentBody bin = new FileBody(file);
                reqEntity.addPart("midImg", bin);
            }

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfacePreRequest("getPageImageUpload/smartPhotoFlexThumbUploadProc");

            reqEntity.setTotalCount(reqEntity.getContentLength());

            post.setEntity(reqEntity);
            HttpResponse response = client.execute(post);

            if (response != null) {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                returnValue = FileUtil.convertStreamToString(is);

                if (interfaceLogListener != null)
                    interfaceLogListener.onSnapsInterfaceResult(HttpUtil.getHttpResponseStatusCode(response), returnValue);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceException(e);
        }

        return returnValue;
    }

    /**
     * 오리지널 이미지를 업로드.
     *
     * @return
     */
    public static String getDiaryUserProfileImageUplad(String userNo, String imgUri, boolean isDelete, ProgressListener listener, SnapsInterfaceLogListener interfaceLogListener) {
        String returnValue = "";
        try {
            HttpResponse response = null;

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(SnapsAPI.DOMAIN() + "/servlet/CommandMultipart.do");

            CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, listener);

            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();

            reqEntity.addPart("part", new StringBody("mall.applInterface.DiaryInterface"));
            reqEntity.addPart("cmd", new StringBody("profileImgUpload"));
            reqEntity.addPart("userNo", new StringBody(userNo));
            reqEntity.addPart("isDel", new StringBody(isDelete ? "true" : "false"));

            if (!isDelete) {
                File file = new File(imgUri);
                ContentBody bin = new FileBody(file);
                reqEntity.addPart("file", bin);
            }

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfacePreRequest("getDiaryUserProfileImageUplad/profileImgUpload");

            reqEntity.setTotalCount(reqEntity.getContentLength());

            post.setEntity(reqEntity);
            response = client.execute(post);

            if (response != null) {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                returnValue = FileUtil.convertStreamToString(is);

                if (interfaceLogListener != null)
                    interfaceLogListener.onSnapsInterfaceResult(HttpUtil.getHttpResponseStatusCode(response), returnValue);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceException(e);
        }

        return returnValue;
    }

    public static String getDiaryOrgImageUplad(String imgUri, boolean isThumb, ProgressListener listener, SnapsInterfaceLogListener interfaceLogListener) {
        return getDiaryOrgImageUplad(imgUri, null, isThumb, listener, interfaceLogListener);
    }

    public static String getDiaryOrgImageUplad(String imgUri, String thumbnailPath, boolean isThumb, ProgressListener listener, SnapsInterfaceLogListener interfaceLogListener) {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryUploadSeqInfo uploadInfo = dataManager.getUploadInfo();
        if (uploadInfo == null || StringUtil.isEmpty(uploadInfo.getSeqUserNo()) || StringUtil.isEmpty(uploadInfo.getSeqDiaryNo())) {
            return "";
        }

        String returnValue = "";
        try {
            HttpResponse response = null;
            File file = new File(imgUri);

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(SnapsAPI.DOMAIN() + "/servlet/CommandMultipart.do");
            ContentBody bin = new FileBody(file);

            ContentBody thumbImgFileBin = null;    //FIXME 서버에서 작업이 어렵다고 해서 보류..
            if (!StringUtil.isEmpty(thumbnailPath)) {
                File thumbFile = new File(thumbnailPath);
                thumbImgFileBin = new FileBody(thumbFile);
            }

            CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, listener);

            reqEntity.addPart("part", new StringBody("mall.applInterface.DiaryInterface"));
            reqEntity.addPart("cmd", new StringBody("imgUpload"));
            reqEntity.addPart("userNo", new StringBody(uploadInfo.getSeqUserNo()));
            reqEntity.addPart("diaryNo", new StringBody(uploadInfo.getSeqDiaryNo()));
            reqEntity.addPart("isThum", new StringBody(isThumb ? "true" : "false"));

            reqEntity.addPart("file", bin);

            if (thumbImgFileBin != null) {
                reqEntity.addPart("midfile", thumbImgFileBin);
            }

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfacePreRequest("getDiaryOrgImageUplad/imgUpload");

            reqEntity.setTotalCount(reqEntity.getContentLength());

            post.setEntity(reqEntity);
            response = client.execute(post);

            if (response != null) {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                returnValue = FileUtil.convertStreamToString(is);

                if (interfaceLogListener != null)
                    interfaceLogListener.onSnapsInterfaceResult(HttpUtil.getHttpResponseStatusCode(response), returnValue);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceException(e);
        }

        return returnValue;
    }

    public static String getDiaryProejctUpload(File save, ProgressListener listener, SnapsInterfaceLogListener interfaceLogListener) throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient();
        String url = SnapsAPI.DOMAIN() + "/servlet/CommandMultipart.do";
        Dlog.d("getDiaryProejctUpload() url:" + url);
        HttpPost post = new HttpPost(url);

        CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, listener);
        reqEntity.addPart("part", new StringBody("mall.applInterface.DiaryInterface"));
        reqEntity.addPart("cmd", new StringBody("saveDiaryInfo"));

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryUploadSeqInfo uploadInfo = dataManager.getUploadInfo();
        if (uploadInfo != null) {
            reqEntity.addPart("userNo", new StringBody(uploadInfo.getSeqUserNo()));
            reqEntity.addPart("diaryNo", new StringBody(uploadInfo.getSeqDiaryNo()));

            SnapsLogger.appendOrderLog("diary saveXML uploadInfo.getSeqUserNo() : " + uploadInfo.getSeqUserNo() + ", diaryNo : " + uploadInfo.getSeqDiaryNo());
        }

        SnapsDiaryUserInfo userInfo = dataManager.getSnapsDiaryUserInfo();
        if (userInfo != null) {
            /**
             * 미션 넘버는 미션 진행 중일 때만 보낸다.(미션 번호가 없으면 자동 재 시작 됨.)
             */
            String missionNo = "";
            if (userInfo.getMissionStateEnum().equals(SnapsDiaryConstants.eMissionState.ING))
                missionNo = userInfo.getMissionNo();

            reqEntity.addPart("missionNo", new StringBody(missionNo != null ? missionNo : ""));

            String failStr = "false";
            if (dataManager.getWriteMode() == SnapsDiaryConstants.EDIT_MODE_NEW_WRITE) {
                failStr = userInfo.isMissionValildCheckResult() ? "false" : "true";
            }

            reqEntity.addPart("isFail", new StringBody(failStr)); //true로 던지면 미션이 재시작된다..
        }

        SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
        if (writeInfo != null) {
            reqEntity.addPart("diaryDate", new StringBody(writeInfo.getDateNumber()));
            reqEntity.addPart("feelingCode", new StringBody(writeInfo.getFeels().getCode()));
            reqEntity.addPart("weatherCode", new StringBody(writeInfo.getWeather().getCode()));

            String postContents = writeInfo.getPostContents();
            String originContents = writeInfo.getContents();
            writeInfo.setIsForceMoreText(postContents != null && originContents != null && !originContents.equals(postContents));

            reqEntity.addPart("diaryContents", new StringBody((postContents != null ? URLEncoder.encode(postContents, "utf-8") : "")));
            reqEntity.addPart("moreYorn", new StringBody(writeInfo.isForceMoreText() ? "Y" : "N"));
        }

        reqEntity.addPart("isUpdate", new StringBody(dataManager.getWriteMode() == SnapsDiaryConstants.EDIT_MODE_MODIFY ? "true" : "false"));

        reqEntity.addPart("file", new FileBody(save));

        if (interfaceLogListener != null)
            interfaceLogListener.onSnapsInterfacePreRequest("getDiaryProejctUpload/saveDiaryInfo");

        reqEntity.setTotalCount(reqEntity.getContentLength());

        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);

        /**
         * For Debugging
         */

        String returnValue = null;
        if (response != null) {
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            returnValue = FileUtil.convertStreamToString(is);

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceResult(HttpUtil.getHttpResponseStatusCode(response), returnValue);
        }

        return returnValue;
    }

    private static int getHttpStatusCode(HttpResponse response) {
        if (response == null || response.getStatusLine() == null) return -1;

        StatusLine statusLine = response.getStatusLine();
        if (statusLine != null) {
            return statusLine.getStatusCode();
        }
        return -1;
    }

    public static String getOrgImageUpload(MyPhotoSelectImageData imageData, String prjCode, ProgressListener listener, SnapsInterfaceLogListener interfaceLogListener) {
        return getOrgImageUpload(imageData, null, prjCode, listener, interfaceLogListener);
    }

    public static String getOrgImageUpload(MyPhotoSelectImageData imageData, String thumbFilePath, String prjCode, ProgressListener listener, SnapsInterfaceLogListener interfaceLogListener) {
        String returnValue = "";
        try {
            HttpResponse response = getOrgImageUpload2(imageData, thumbFilePath, prjCode, listener, interfaceLogListener);
            if (response != null) {
                int statusCode = getHttpStatusCode(response);
                if (statusCode != 200 && !CNetStatus.getInstance().isAliveNetwork(ContextUtil.getContext())) {
                    if (interfaceLogListener != null)
                        interfaceLogListener.onSnapsInterfaceResult(statusCode, "");
                    return SnapsOrderConstants.EXCEPTION_MSG_NETWORK_ERROR;
                }

                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                returnValue = FileUtil.convertStreamToString(is);

                if (interfaceLogListener != null)
                    interfaceLogListener.onSnapsInterfaceResult(statusCode, returnValue);

                if (!StringUtil.isEmpty(thumbFilePath)) { //TODO  for test 삭제 해도 무방 함.
                    Dlog.d("getOrgImageUpload() returnValue:" + returnValue);
                }
            } else {
                return SnapsOrderConstants.EXCEPTION_MSG_NETWORK_ERROR;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            returnValue = "getOrgImageUpload exception : " + e.toString();
            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceException(e);
        }

        return returnValue;
    }

    /***
     * 서비스 용...
     *
     * @param prjCode
     * @param listener
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */

    public static HttpResponse getOrgImageUpload2(MyPhotoSelectImageData imageData, String thumbFilePath, String prjCode, ProgressListener listener, SnapsInterfaceLogListener interfaceLogListener) throws ClientProtocolException, IOException, OutOfMemoryError {
        HttpClient client = new DefaultHttpClient();

//		String domain = SmartSnapsManager.isSupportSmartSnapsProduct() && !Config.isRealServer() ? "https://stg-bo.snaps.com" : SnapsAPI.DOMAIN();
        String domain = SnapsAPI.DOMAIN();

        HttpPost post = new HttpPost(domain + "/servlet/CommandMultipart.do");

        String orgFilePath = imageData != null ? imageData.PATH : "";

        File orgFile = new File(orgFilePath);
        if (!orgFile.exists())
            throw new FileNotFoundException("getOrgImageUpload2 orgFile is not exist : " + orgFilePath);

        ContentBody orgImgFileBin = new FileBody(orgFile);

        ContentBody thumbImgFileBin = null;
        if (!StringUtil.isEmpty(thumbFilePath)) {
            if (!thumbFilePath.startsWith("http") && !thumbFilePath.startsWith("/Upload")) {
                File thumbFile = new File(thumbFilePath);
                if (thumbFile.exists())
                    thumbImgFileBin = new FileBody(thumbFile);
            }
        }

        CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, listener);

        reqEntity.addPart("part", new StringBody("mall.smartphotolite.SmartPhotoLiteInterface"));
        String cmd = SmartSnapsManager.isSupportSmartSnapsProduct() ? "smartPhotoOriginalUpload" : "smartPhotoFlexOrgImgUploadProc2";
        reqEntity.addPart("cmd", new StringBody(cmd));
        reqEntity.addPart("prmprojcode", new StringBody(prjCode));
        reqEntity.addPart("prmchnlcode", new StringBody(Config.getCHANNEL_CODE()));
        reqEntity.addPart("imgsize", new StringBody("1024"));
        reqEntity.addPart("file", orgImgFileBin);

        if (SmartSnapsManager.isSupportSmartSnapsProduct() && imageData != null) {
            reqEntity.addPart("prmImgYear", new StringBody(imageData.F_IMG_YEAR));
            reqEntity.addPart("prmImgSqnc", new StringBody(imageData.F_IMG_SQNC));
        }

        reqEntity.addPart("analysisYN", new StringBody("N"));

        if (thumbImgFileBin != null) {
            reqEntity.addPart("midfile", thumbImgFileBin);
        }

        reqEntity.addPart("realFileName", new StringBody(orgFilePath));

        if (interfaceLogListener != null)
            interfaceLogListener.onSnapsInterfacePreRequest("getOrgImageUpload2/" + cmd);

        reqEntity.setTotalCount(reqEntity.getContentLength());

        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);

        logServerInfo("getOrgImageUpload2()", response);

        return response;
    }

    public static HttpResponse getOrgImageUploadForOldVersion(String orgFilePath, String thumbFilePath, String prjCode, ProgressListener listener, SnapsInterfaceLogListener interfaceLogListener) throws ClientProtocolException, IOException, OutOfMemoryError {
        HttpClient client = new DefaultHttpClient();

        String domain = SnapsAPI.DOMAIN();

        HttpPost post = new HttpPost(domain + "/servlet/CommandMultipart.do");

        File orgFile = new File(orgFilePath);
        if (!orgFile.exists())
            throw new FileNotFoundException("getOrgImageUpload2 orgFile is not exist : " + orgFilePath);

        ContentBody orgImgFileBin = new FileBody(orgFile);

        ContentBody thumbImgFileBin = null;
        if (!StringUtil.isEmpty(thumbFilePath)) {
            File thumbFile = new File(thumbFilePath);
            if (thumbFile.exists())
                thumbImgFileBin = new FileBody(thumbFile);
        }

        CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, listener);

        reqEntity.addPart("part", new StringBody("mall.smartphotolite.SmartPhotoLiteInterface"));
        reqEntity.addPart("cmd", new StringBody("smartPhotoFlexOrgImgUploadProc2"));
        reqEntity.addPart("prmprojcode", new StringBody(prjCode));
        reqEntity.addPart("prmchnlcode", new StringBody(Config.getCHANNEL_CODE()));
        reqEntity.addPart("imgsize", new StringBody("1024"));
        reqEntity.addPart("file", orgImgFileBin);

        if (thumbImgFileBin != null) {
            reqEntity.addPart("midfile", thumbImgFileBin);
        }

        reqEntity.addPart("realFileName", new StringBody(orgFilePath));

        if (interfaceLogListener != null)
            interfaceLogListener.onSnapsInterfacePreRequest("getOrgImageUploadForOldVersion/" + "smartPhotoFlexOrgImgUploadProc2");

        reqEntity.setTotalCount(reqEntity.getContentLength());

        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);

        logServerInfo("getOrgImageUploadForOldVersion()", response);

        return response;
    }

    public static String getThumbImageUpload(MyPhotoSelectImageData imageData, String prjCode, ProgressListener listener, SnapsInterfaceLogListener interfaceLogListener) {

        String returnValue = "";
        try {
            HttpResponse response = getThumbImageUpload2(imageData, prjCode, listener, interfaceLogListener);
            if (response != null) {
                int statusCode = getHttpStatusCode(response);
                if (statusCode != 200 && !CNetStatus.getInstance().isAliveNetwork(ContextUtil.getContext())) {
                    if (interfaceLogListener != null)
                        interfaceLogListener.onSnapsInterfaceResult(statusCode, "");
                    return SnapsOrderConstants.EXCEPTION_MSG_NETWORK_ERROR;
                }

                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                returnValue = FileUtil.convertStreamToString(is);

                if (interfaceLogListener != null)
                    interfaceLogListener.onSnapsInterfaceResult(statusCode, returnValue);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            returnValue = "getThumbImageUpload exception : " + e.toString();
            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceException(e);
        }

        return returnValue;
    }

    /***
     * 서비스 용...
     *
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResponse getThumbImageUpload2(MyPhotoSelectImageData imageData, String prjCode, ProgressListener listener, SnapsInterfaceLogListener interfaceLogListener) throws SnapsInvalidImageDataException, ClientProtocolException, IOException, OutOfMemoryError, JSONException {
        HttpClient client = new DefaultHttpClient();

        Map<String, String> imageUploadRequestDataMap = createImageUploadRequestDataMap(imageData, prjCode);
        String url = SnapsAPI.DOMAIN() + "/servlet/CommandMultipart.do";

        HttpPost post = new HttpPost(url);

        CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, listener);

        JSONObject jsonObjectForWebLog = new JSONObject();
        for (Map.Entry<String, String> entry : imageUploadRequestDataMap.entrySet()) {
            if (entry == null) continue;

            reqEntity.addPart(entry.getKey(), new StringBody(entry.getValue()));
            jsonObjectForWebLog.put(entry.getKey(), entry.getValue());
        }

        File thumbnailCacheFile = null;
        try {
            thumbnailCacheFile = SnapsThumbnailMaker.getThumbnailCacheFileWithImageData(ContextUtil.getContext(), imageData);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        boolean isValidThumbCacheFile = thumbnailCacheFile != null && thumbnailCacheFile.exists() && thumbnailCacheFile.length() > 0 && BitmapUtil.isValidThumbnailImage(thumbnailCacheFile.getAbsolutePath());
        if (isValidThumbCacheFile) {
            ContentBody thumbImgFileBin = new FileBody(thumbnailCacheFile);
            reqEntity.addPart("file", thumbImgFileBin);
        } else {
            try {
                SnapsOrderManager snapsOrderManager = SnapsOrderManager.getInstance();
                if (snapsOrderManager.isInitialized()) {
                    SnapsOrderManager.removeBackgroundUploadThumbImageData(imageData);
                }

                thumbnailCacheFile = SnapsThumbnailMaker.createThumbnailCacheWithImageData(ContextUtil.getContext(), imageData);
                if (thumbnailCacheFile != null && thumbnailCacheFile.exists() && thumbnailCacheFile.length() > 0) {
                    if (BitmapUtil.isValidThumbnailImage(thumbnailCacheFile.getAbsolutePath())) {
                        ContentBody thumbImgFileBin = new FileBody(thumbnailCacheFile);
                        reqEntity.addPart("file", thumbImgFileBin);
                        isValidThumbCacheFile = true;
                    } else {
                        FileUtil.deleteFile(thumbnailCacheFile.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        if (!isValidThumbCacheFile) {
            return null;
        }

        if (interfaceLogListener != null)
            interfaceLogListener.onSnapsInterfacePreRequest("getThumbImageUpload2");

        reqEntity.setTotalCount(reqEntity.getContentLength());

        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);

        logServerInfo("getThumbImageUpload2()", response);

        return response;
    }

    private static Map<String, String> createImageUploadRequestDataMap(MyPhotoSelectImageData imageData, String prjCode) {
        Map<String, String> requestDataMap = new HashMap<>();
        requestDataMap.put("part", "mall.smartphotolite.SmartPhotoLiteInterface");
        requestDataMap.put("cmd", "smartPhotoThumbnailUpload");
        requestDataMap.put("prmprojcode", prjCode);
        requestDataMap.put("prmchnlcode", Config.getCHANNEL_CODE());
        requestDataMap.put("imgsize", "1024");
        requestDataMap.put("analysisYN", SmartSnapsManager.isSupportSmartSnapsProduct() ? "Y" : "N");

        try {
            String orgFilePath = imageData.PATH;
            int orientation = CropUtil.getExifOrientationTag(orgFilePath);
            requestDataMap.put("orientation", String.valueOf(orientation));
            requestDataMap.put("realFileName", orgFilePath);
        } catch (IOException e) {
            Dlog.e(TAG, e);
        }

        return requestDataMap;
    }

    /**
     * 프로젝트 저장 , 장바구니.
     *
     * @param userid
     * @param ordr
     * @param save
     * @param order
     * @param option
     * @return
     */
    public static String getProejctUpload(String userid, String ordr, File save, File order, File option, ProgressListener listener, boolean isKakao, SnapsInterfaceLogListener interfaceLogListener) {
        return getProejctUpload(userid, ordr, "P", save, order, option, listener, isKakao, interfaceLogListener);
    }

    public static String getProejctUpload(String userid, String ordr, String albmType, File save, File order, File option, ProgressListener listener, boolean isKakao, SnapsInterfaceLogListener interfaceLogListener) {
        return getProejctUpload(userid, ordr, albmType, Config.getPROJ_CODE(), save, order, option, listener, isKakao, interfaceLogListener);
    }

    public static String getProejctUpload(String userid, String ordr, String albmType, String prjCode, File save, File order, File option, ProgressListener listener, boolean isKakao, SnapsInterfaceLogListener interfaceLogListener) {
        return getProejctUpload(userid, ordr, albmType, prjCode, save, order, option, Config.getPROJ_UTYPE(), listener, isKakao, interfaceLogListener);
    }

    public static String getProejctUpload(String userid, String ordr, String albmType, String prjCode, File save, File order, File option, String utype, ProgressListener listener, boolean isKakao, SnapsInterfaceLogListener interfaceLogListener) {
        String returnValue = "";

        try {
            HttpResponse response = getProejctUpload2(userid, ordr, albmType, prjCode, save, order, option, utype, listener, isKakao, interfaceLogListener);

            if (response != null) {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                returnValue = FileUtil.convertStreamToString(is);

                SnapsLogger.appendOrderLog("xml file upload response value : " + returnValue);
                if (interfaceLogListener != null)
                    interfaceLogListener.onSnapsInterfaceResult(HttpUtil.getHttpResponseStatusCode(response), returnValue);
            }

        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsLogger.appendOrderLog("getProjectUpload exception  :  " + e.toString());
            if (isUnknownHostExceptionWithException(e) || isHttpHostConnectException(e)) {
                returnValue = SnapsOrderConstants.EXCEPTION_MSG_UNKNOWN_HOST_ERROR;
            }

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceException(e);
        }

        return returnValue;

        /*
         * List<NameValuePair> params = new ArrayList<NameValuePair>();
         *
         * params.add( new BasicNameValuePair( "userNo" , userid.trim() )); params.add( new BasicNameValuePair( "prmprojcode" , Config.PROJ_CODE )); params.add( new BasicNameValuePair( "prmchnlcode" ,
         * Config.CHANNEL_CODE )); params.add( new BasicNameValuePair( "prmalbmType" , "P" )); params.add( new BasicNameValuePair( "uType" , Config.PROJ_UTYPE )); params.add( new BasicNameValuePair(
         * "appver" , "mobile" )); params.add( new BasicNameValuePair( "attr2" , "kakao" )); params.add( new BasicNameValuePair( "ordrStat" , ordr )); params.add( new BasicNameValuePair( "save" ,
         * save.replace("&amp;", "&") )); params.add( new BasicNameValuePair( "order" , order )); params.add( new BasicNameValuePair( "option" , option ));
         *
         * String result = HttpUtil.connectPost( SnapsAPI.POST_API_SAVE_ORDER_PROJECT , params ); return result;
         */
    }

    public static boolean isUnknownHostExceptionWithException(Exception e) {
        return e != null && e instanceof UnknownHostException;
    }

    public static boolean isHttpHostConnectException(Exception e) {
        return e != null && e instanceof HttpHostConnectException;
    }

//			.UnknownHostException: Unable to resolve host "m.snaps.kr": No address associated with hostname

    public static HttpResponse getProejctUpload2(String userid, String ordr, String albmType, String prjCode, File save, File order, File option, String uType, ProgressListener listener,
                                                 boolean isKakao, SnapsInterfaceLogListener interfaceLogListener) throws ClientProtocolException, IOException {

        SnapsLogger.appendOrderLog("start xml file upload");

        HttpClient client = new DefaultHttpClient();
        String url = SnapsAPI.DOMAIN() + "/servlet/CommandMultipart.do";
        Dlog.d("getProejctUpload2() url:" + url);
        Dlog.d(Dlog.CS_TOOL_TOY, "PROJECT_CODE:" + prjCode);
        HttpPost post = new HttpPost(url);

        CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, listener);
        reqEntity.addPart("part", new StringBody("mall.smartphotolite.SmartPhotoLiteInterface"));
        reqEntity.addPart("cmd", new StringBody("liteUploadXmlProcMobile"));
        reqEntity.addPart("nextPage", new StringBody("liteUploadXmlProcMobile"));

        reqEntity.addPart("userNo", new StringBody(userid.trim()));
        reqEntity.addPart("prmprojcode", new StringBody(prjCode));
        reqEntity.addPart("prmchnlcode", new StringBody(Config.getCHANNEL_CODE()));
        reqEntity.addPart("prmalbmType", new StringBody(albmType));
        reqEntity.addPart("uType", new StringBody(uType));
        // 사진인화 os버젼 android
        reqEntity.addPart("osVer", new StringBody("190002_" + Config.getAPP_VERSION()));
        reqEntity.addPart("appver", new StringBody("mobile"));
        if (isKakao)
            reqEntity.addPart("attr2", new StringBody("kakao"));
        reqEntity.addPart("save", new FileBody(save));
        reqEntity.addPart("order", new FileBody(order));
        reqEntity.addPart("option", new FileBody(option));

        SnapsLogger.appendOrderLog("xml file upload userId : " + userid);
        SnapsLogger.appendOrderLog("xml file upload prjCode : " + prjCode);
        SnapsLogger.appendOrderLog("xml file upload uType : " + uType);
        SnapsLogger.appendOrderLog("xml file upload chnCode : " + Config.getCHANNEL_CODE());

        SnapsLogger.appendOrderLog("xml file upload save exist : " + save.exists() + ", length : " + save.length());
        SnapsLogger.appendOrderLog("xml file upload order exist : " + order.exists() + ", length : " + order.length());
        SnapsLogger.appendOrderLog("xml file upload option exist : " + option.exists() + ", length : " + option.length());

        //xml 파일 오류 검사
        //일단 오류 발생시 로그만 남긴다.
        //근본적 현재 단계에서 오류를 찾으면 안되고 이전 단계 어느 시점에서 찾아야 한다. 하지만 처음부터 오류를 만들지 말아야지!!
        checkXmlFile("xml file upload save [check xml]", save);
        checkXmlFile("xml file upload order [check xml]", order);
        checkXmlFile("xml file upload option [check xml]", option);

        if (interfaceLogListener != null)
            interfaceLogListener.onSnapsInterfacePreRequest("getProejctUpload2/liteUploadXmlProcMobile");

        reqEntity.setTotalCount(reqEntity.getContentLength());

        post.setEntity(reqEntity);
        HttpResponse response = client.execute(post);

        SnapsLogger.appendOrderLog("xml file upload response : " + (response != null ? response.getStatusLine() : "null"));

        logServerInfo("getProejctUpload2()", response);

        return response;
    }

    private static void logServerInfo(String log, HttpResponse response) {
        if (response == null) return;
        if (response.getStatusLine() == null) return;
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) return;

        try {
            Header[] headers = response.getAllHeaders();
            if (headers == null) return;
            for (Header header : headers) {
                SnapsLogger.appendOrderLog(log + " HttpResponse " + header.getName() + " : " + header.getValue());
            }
        }catch (Exception e) {
            //Dlog.e(TAG, e);
        }
    }

    private static void checkXmlFile(String logTag, File xmlFile) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            dBuilder.parse(xmlFile);
        }catch (Throwable tr) {
            //콜 스택을 추가할까 말까 고민하다가 소스가 복잡하니 콜 스택이라도 남겨야 찾을수 있을 것 같아서 콜 스택 추가.
            if (tr instanceof SAXParseException) {
                try {
                    SAXParseException saxParseException = (SAXParseException) tr;
                    int line = saxParseException.getLineNumber();
                    int column = saxParseException.getColumnNumber();
                    SnapsLogger.appendOrderLog(logTag + " line:" + line + ", column:" + column); //서버에 업로드된 원본 파일 경로를 쉽게 찾기 어려운데 에러 위치를 남기는것이 의미가...
                }catch (Exception e) {
                    //Dlog.e(TAG, e);
                }
            }
            String callStack = Log.getStackTraceString(tr);
            SnapsLogger.appendOrderLog(logTag + " : " + callStack);
        }
    }

    public static class SnapsImageUploadRequestData {
        private String userId;
        private String fileName;
        private String fullSizeFileName;
        private String albumType;
        private String prjCode;
        private CustomMultiPartEntity.ProgressListener listener;
        private SnapsInterfaceLogListener interfaceLogListener;
        private boolean isFullSizeThumbnail;

        private SnapsImageUploadRequestData(Builder builder) {
            this.userId = builder.userId;
            this.fileName = builder.fileName;
            this.fullSizeFileName = builder.fullSizeFileName;
            this.albumType = builder.albumType;
            this.prjCode = builder.prjCode;
            this.listener = builder.listener;
            this.interfaceLogListener = builder.interfaceLogListener;
            this.isFullSizeThumbnail = builder.isFullSizeThumbnail;
        }

        public String getFullSizeFileName() {
            return fullSizeFileName;
        }

        public boolean isFullSizeThumbnail() {
            return isFullSizeThumbnail;
        }

        public String getUserId() {
            return userId;
        }

        public String getFileName() {
            return fileName;
        }

        public String getAlbumType() {
            return albumType;
        }

        public String getPrjCode() {
            return prjCode;
        }

        public CustomMultiPartEntity.ProgressListener getListener() {
            return listener;
        }

        public SnapsInterfaceLogListener getInterfaceLogListener() {
            return interfaceLogListener;
        }

        public static class Builder {
            private String userId;
            private String fileName;
            private String fullSizeFileName;
            private String albumType;
            private String prjCode;
            private CustomMultiPartEntity.ProgressListener listener;
            private SnapsInterfaceLogListener interfaceLogListener;
            private boolean isFullSizeThumbnail;

            public Builder setFullSizeFileName(String fullSizeFileName) {
                this.fullSizeFileName = fullSizeFileName;
                return this;
            }

            public Builder setFullSizeThumbnail(boolean fullSizeThumbnail) {
                isFullSizeThumbnail = fullSizeThumbnail;
                return this;
            }

            public Builder setUserId(String userId) {
                this.userId = userId;
                return this;
            }

            public Builder setFileName(String fileName) {
                this.fileName = fileName;
                return this;
            }

            public Builder setAlbumType(String albumType) {
                this.albumType = albumType;
                return this;
            }

            public Builder setPrjCode(String prjCode) {
                this.prjCode = prjCode;
                return this;
            }

            public Builder setListener(CustomMultiPartEntity.ProgressListener listener) {
                this.listener = listener;
                return this;
            }

            public Builder setInterfaceLogListener(SnapsInterfaceLogListener interfaceLogListener) {
                this.interfaceLogListener = interfaceLogListener;
                return this;
            }

            public SnapsImageUploadRequestData create() {
                return new SnapsImageUploadRequestData(this);
            }
        }
    }
}
