package com.snaps.mobile.service.ai;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Base64;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snaps.common.utils.log.Dlog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * REST 통신
 */
public class NetClient {
    private static final String TAG = NetClient.class.getSimpleName();
    public static final String EXIF_SYNC_TYPE_INIT = "INIT";
    public static final String EXIF_SYNC_TYPE_UPDATE = "UPDATE";
    public static final String EXIF_SYNC_TYPE_NOT_AVAILABLE = "NA";
    private static final String DIR_NAME_THUMB = "ai_sync_thumb";
    private static final int MAX_RETRY_COUNT = 3;
    private static final int MAX_THREAD_COUNT = 3;

    private String mThumbImgCacheDir;
    private volatile boolean mIsForceStop;  //한번 true로 변경하면 reset하는 기능 없음
    private volatile boolean mIsForceStopNetwork;
    private volatile Call<ResponseBody> mCurrentCall;    //주의 멑티 스레드로 호출 되는 상황이면 문제 발생
    private Map<MethodName, Integer> mRetryCountMap;
    private SyncPhotoServerAPI mSyncPhotoServerAPI;
    private DeviceManager mDeviceManager;
    private final ReceiveUploadImageListResult mReceiveUploadImageListResultWait;
    private final ConfirmSentExifZipFileResult mConfirmSentExifZipFileResultWait;
    private volatile CopyOnWriteArrayList<Call<ResponseBody>> mPostThumbImgCallList;
    private volatile ExecutorService mPostThumbImgListExecutorService;
    private ImageUtils mImageUtils;
    private String mUserNo;
    private String mDeviceId;

    public enum Result {
        Success,    //전송 성공
        Fail,       //최종 전송 실패
        Retry,      //재시도
        Wait        //현재 전송이 불가능하므로 대기
    }

    public enum SyncStatus {
        OK,
        Fail,
        Processing
    }

    public enum ThumbType {
        Recommend,
        Story
    }

    //하드 코딩
    //TODO::메소드가 추가되거나 이름이 변경되면 수정 해야 함
    private enum MethodName {
        confirmSentExifZipFile,
        sendExifZipFile,
        receiveUploadImageList,
        uploadImages,
    }


    public NetClient(Context context, String domain, String userNo, String deviceId, DeviceManager deviceManager) {
        mUserNo = userNo;
        mDeviceId = deviceId;
        mDeviceManager = deviceManager;

        mReceiveUploadImageListResultWait =
                new ReceiveUploadImageListResult(Result.Wait, new ArrayList<List<UploadImage>>());
        mConfirmSentExifZipFileResultWait =
                new ConfirmSentExifZipFileResult(Result.Wait, SyncStatus.Fail);

        mIsForceStop = false;
        mCurrentCall = null;
        mRetryCountMap = new HashMap<MethodName, Integer>();
        mPostThumbImgCallList = new CopyOnWriteArrayList<Call<ResponseBody>>();
        mImageUtils = new ImageUtils();
        mThumbImgCacheDir = context.getExternalCacheDir().getAbsolutePath() + File.separator + DIR_NAME_THUMB;
        mPostThumbImgListExecutorService = null;

        ServiceGenerator serviceGenerator = new ServiceGenerator();
        mSyncPhotoServerAPI = serviceGenerator.createService(domain, SyncPhotoServerAPI.class);

        mIsForceStopNetwork = false;
    }

    /**
     *
     */
    public void clearAllRetryCount() {
        mRetryCountMap.clear();
    }

    /**
     * 제시도 횟수 초기화
     * @param methodName
     */
    private void clearRetryCount(MethodName methodName) {
        if (mRetryCountMap.containsKey(methodName) == false) {
            return;
        }

        mRetryCountMap.put(methodName, 0);
    }

    /**
     * 재시도 횟수 증가
     * @param methodName
     * @return
     */
    private int addRetryCount(MethodName methodName) {
        if (mRetryCountMap.containsKey(methodName) == false) {
            mRetryCountMap.put(methodName, 0);
        }

        int currentCount = mRetryCountMap.get(methodName);
        currentCount++;
        mRetryCountMap.put(methodName, currentCount);

        return currentCount;
    }

    /**
     *
     * @param methodName
     * @return
     */
    private boolean isMaxRetryCount(MethodName methodName) {
        if (mRetryCountMap.containsKey(methodName) == false) {
            return false;
        }

        int currentCount = mRetryCountMap.get(methodName);
        return (currentCount >= MAX_RETRY_COUNT);
    }

    /**
     * 결과에 따라 재시도 횟수를 계산해서 최종 실패인지 구한다. (버그가 있다. 실패 했다가 한참 후에 시도해서 또 실패하면 카운터가 올라간다. 이제 맞는 건가?)
     * @param methodName
     * @param isSuccess
     * @return
     */
    private Result processResult(MethodName methodName, boolean isSuccess) {
        Result result;
        if (isSuccess) {
            clearRetryCount(methodName);
            result = Result.Success;
        }
        else {
            int count = addRetryCount(methodName);
            if (count >= MAX_RETRY_COUNT) {
                result = Result.Fail;
            }
            else {
                result = Result.Retry;
            }
        }
        return result;
    }


    public void resetIsForceStopNetwork() {
        mIsForceStopNetwork = false;
    }

    /**
     * 당장 전송 중지
     */
    public void forceStopNetwork() {
        mIsForceStopNetwork = true;

        if (mCurrentCall != null) {
            if (mCurrentCall.isCanceled() == false) {
                try {
                    mCurrentCall.cancel();
                    mCurrentCall = null;
                }catch (Exception e) {
                    Loggg.e(TAG, e);
                }
            }
        }

        if (mPostThumbImgListExecutorService != null) {
            try {
                if (mPostThumbImgListExecutorService.isTerminated() == false) {
                    mPostThumbImgListExecutorService.shutdown();
                }
            }catch (Exception e) {
                Loggg.e(TAG, e);
            }finally {
                mPostThumbImgListExecutorService = null;
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean isForceStop() {
        return mIsForceStop;
    }

    /**
     * 강제 정지
     */
    public void forceStop() {
        Loggg.d(TAG, "forceStop");
        mIsForceStop = true;

        forceStopNetwork();
    }

    /**
     * 현재 전송 가능 상태인지 구한다.
     * @return
     */
    private boolean isWaitState() {
        //대기 상황 검사
        if (AppConfigClone.getInstance().isAllowUploadMobileNetwork() == false) {
            if (mDeviceManager.isWiFiConnected() == false) return true;
        }

        return false;
    }


    static class ConfirmSentExifZipFileResult {
        public final Result mResult;
        public final SyncStatus mSyncStatus;

        ConfirmSentExifZipFileResult(Result result, SyncStatus syncStatus) {
            mResult = result;
            mSyncStatus = syncStatus;
        }
    }

    /**
     * Exif 전송 후 서버에서 처리가 완료되었는지 확인한다.
     * @param txId
     * @return
     */
    public ConfirmSentExifZipFileResult confirmSentExifZipFile(String txId) {
        boolean isValidTxId = (txId != null && txId.length() > 0);
        if (isValidTxId == false) {
            Loggg.e(TAG, "confirmSentExifZipFile() params is invalid");
            return new ConfirmSentExifZipFileResult(Result.Fail, SyncStatus.Fail);
        }

        MethodName thisMethodName = MethodName.confirmSentExifZipFile;
        if (isWaitState()) {
            clearRetryCount(thisMethodName);    //연속 3회하고 가정하므로 clear
            return mConfirmSentExifZipFileResultWait;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("deviceId", mDeviceId);
        paramMap.put("txId", txId);
        mCurrentCall = mSyncPhotoServerAPI.getConfirmSentExifZipFile(mUserNo, paramMap);

        SyncStatus syncStatus = SyncStatus.Fail;
        boolean isSuccess = false;
        try {
            Response<ResponseBody> response = mCurrentCall.execute();
            if (response.isSuccessful()) {
                isSuccess = true;
                String jsonString = response.body().string();
                try {
                    JsonObject jsonObj = new JsonParser().parse(jsonString).getAsJsonObject();
                    String status = jsonObj.get("syncStatus").getAsString();
                    if (status.equals("01")) {
                        syncStatus = SyncStatus.Processing;
                    }
                    else if (status.equals("02")) {
                        syncStatus = SyncStatus.OK;
                    }
                    else if (status.equals("03")) {
                        syncStatus = SyncStatus.Fail;
                    }
                }catch (Exception e) {
                    Loggg.e(TAG, e);
                }
            }
            else {
                Loggg.e(TAG, thisMethodName + " : " + response.toString());
            }
        }catch (Exception e) {
            Loggg.e(TAG, e);
        } finally {
            mCurrentCall = null;
        }

        Result result = processResult(thisMethodName, isSuccess);
        return new ConfirmSentExifZipFileResult(result, syncStatus);
    }


    /**
     * Exif 전제 정보를 서버에 전송한다.
     * @param filePath
     * @param syncType
     * @return
     */
    public Result sendExifZipFile(String filePath, String syncType) {
        boolean isValidFilePath = (filePath != null && filePath.length() > 0);
        boolean isValidSyncType = (syncType != null && syncType.length() > 0);
        if (isValidFilePath == false || isValidSyncType == false) {
            Loggg.e(TAG, "sendExifZipFile() params is invalid");
            return Result.Fail;
        }

        //대기 상황 검사
        //String thisMethodName = new Object(){}.getClass().getEnclosingMethod().getName();  // 오버헤드!!!
        MethodName thisMethodName = MethodName.sendExifZipFile;
        if (isWaitState()) {
            clearRetryCount(thisMethodName);    //연속 3회하고 가정하므로 clear
            return Result.Wait;
        }

        File file = new File(filePath);
        long fileSizeNumber = file.length();

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("metaFile", file.getName(), requestFile);

        RequestBody fileSize = RequestBody.create(MediaType.parse("multipart/form-data"), Long.toString(fileSizeNumber));
        mCurrentCall = mSyncPhotoServerAPI.postExifZipFile(mUserNo, syncType, body, fileSize);

        boolean isSuccess = false;
        try {
            Response<ResponseBody> response = mCurrentCall.execute();
            if (response.isSuccessful()) {
                isSuccess = true;
            }
            else {
                Loggg.e(TAG, thisMethodName + " : " + response.toString());
            }
        }catch (Exception e) {
            Loggg.e(TAG, e);
        } finally {
            mCurrentCall = null;
        }

        //서버 정상 전송 후 응답에러가 발생하는 경우가 존재해서
        isSuccess = true;   //무조건 성공으로 처리
        Result result = processResult(thisMethodName, isSuccess);
        return result;
    }

    static class ReceiveUploadImageListResult {
        public final Result mResult;
        public final List<List<UploadImage>> mImageListList;

        ReceiveUploadImageListResult(Result result, List<List<UploadImage>> imageListList) {
            mResult = result;
            mImageListList = new ArrayList<List<UploadImage>>(imageListList);
        }
    }

    static class UploadImagesInfo {
        private final String mTxId;
        private List<List<UploadImage>> mImageListList;

        UploadImagesInfo(String txId, List<List<UploadImage>> imageListList) {
            mTxId = txId;
            mImageListList = imageListList;
        }

        public String getTxId() {
            return mTxId;
        }

        public List<List<UploadImage>> getImageListList() {
            return mImageListList;
        }

        public void setImageListList(List<List<UploadImage>> imageListList) {
            mImageListList = imageListList;
        }
    }

    static class UploadImage {
        private final ThumbType mThumbType;
        private final int mRank;
        private final String mImgPath;
        private PhotoInfo mPhotoInfo;

        UploadImage(ThumbType thumbType, int rank, String imgPath) {
            mThumbType = thumbType;
            mRank = rank;
            mImgPath = imgPath;
            mPhotoInfo = null;
        }

        public ThumbType getThumbType() {
            return mThumbType;
        }

        public int getRank() {
            return mRank;
        }

        public String getImgPath() {
            return mImgPath;
        }

        public void setPhotoInfo(PhotoInfo photoInfo) {
            mPhotoInfo = photoInfo;
        }

        public PhotoInfo getPhotoInfo() {
            return mPhotoInfo;
        }
    }

    /**
     * 서버에 업로드해야하는 이미지 리스트 정보를 요청한다.
     * @return
     */
    public ReceiveUploadImageListResult receiveUploadImageList() {
        MethodName thisMethodName = MethodName.receiveUploadImageList;
        if (isWaitState()) {
            clearRetryCount(thisMethodName);    //연속 3회하고 가정하므로 clear
            return mReceiveUploadImageListResultWait;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appType", "android");
        paramMap.put("deviceId", mDeviceId);
        mCurrentCall = mSyncPhotoServerAPI.getUploadThumbImgList(mUserNo, paramMap);

        List<List<UploadImage>> uploadImageListList = null;

        boolean isSuccess = false;
        try {
            Response<ResponseBody> response = mCurrentCall.execute();
            if (response.isSuccessful()) {
                String jsonString = response.body().string();
                uploadImageListList = extractUploadImageList(jsonString);
                if (uploadImageListList != null) {
                    isSuccess = true;
                }
            }
            else {
                Loggg.e(TAG, thisMethodName + " : " + response.toString());
            }
        }catch (Exception e) {
            Loggg.e(TAG, e);
        } finally {
            mCurrentCall = null;
        }

        Result result = processResult(thisMethodName, isSuccess);

        if (isSuccess) {
            return new ReceiveUploadImageListResult(result, uploadImageListList);
        }
        return new ReceiveUploadImageListResult(result, new ArrayList<List<UploadImage>>());
    }



    /**
     * 서버에서 전송받은 업로드 해야하는 이미지 리스트 결과를 파싱해서 이미지 목록을 구한다.
     * @param jsonString
     * @return
     */
    private List<List<UploadImage>> extractUploadImageList(String jsonString) {
        if (jsonString == null || jsonString.length() == 0) {
            return null;
        }

        List<List<UploadImage>> uploadImageInfoListList = new ArrayList<List<UploadImage>>();

        try {
            JsonObject jsonObj = new JsonParser().parse(jsonString).getAsJsonObject();
            int statusCode = jsonObj.get("statusCode").getAsInt();
            if (statusCode != 200) {
                Loggg.e(TAG, "statusCode:" + statusCode);
                return null;
            }

            List<UploadImage> uploadImageList = getRecommendUploadImageList(jsonObj);
            if (uploadImageList.size() > 0) {
                uploadImageInfoListList.add(uploadImageList);
            }

            uploadImageInfoListList.addAll(getStoryUploadImageList(jsonObj));
        }catch (Exception e) {
            Loggg.e(TAG, e);
            return null;
        }

        return uploadImageInfoListList;
    }

    /**
     * 스토리 대표 썸네일 이미지 업로드 목록을 구한다.
     * @param jsonObj
     * @return
     */
    private List<List<UploadImage>> getStoryUploadImageList(JsonObject jsonObj) {
        List<List<UploadImage>> uploadImageListList = new ArrayList<List<UploadImage>>();

        int storyTotCnt = jsonObj.get("storyTotCnt").getAsInt();
        Loggg.d(TAG, "storyTotCnt:" + storyTotCnt);

        JsonArray storyJsonArray = jsonObj.getAsJsonArray("story");
        for(int i = 0; i < storyTotCnt; i++) {
            JsonObject storyJsonObj = storyJsonArray.get(i).getAsJsonObject();
            int imagesLength = storyJsonObj.get("imagesLength").getAsInt();
            Loggg.d(TAG, "story.imagesLength:" + imagesLength);
            if (imagesLength == 0) {
                continue;
            }

            int rank = storyJsonObj.get("rank").getAsInt();
            Loggg.d(TAG, "story.rank:" + rank);

            List<UploadImage> uploadImageList = new ArrayList<UploadImage>();

            JsonArray storyImagesJsonArray = storyJsonObj.getAsJsonArray("images");
            for (int j = 0; j < imagesLength; j++) {
                String imagePath = storyImagesJsonArray.get(j).getAsString();
                UploadImage uploadImage = new UploadImage(ThumbType.Story, rank, imagePath);
                uploadImageList.add(uploadImage);
            }

            uploadImageListList.add(uploadImageList);
        }

        return uploadImageListList;
    }

    /**
     * 추천 썸네일 이미지 목록을 구한다.
     * @param jsonObj
     * @return
     */
    private List<UploadImage> getRecommendUploadImageList(JsonObject jsonObj) {
        List<UploadImage> uploadImageList =  new ArrayList<UploadImage>();

        JsonObject recommendJsonObj = jsonObj.get("recommend").getAsJsonObject();
        int imagesLength = recommendJsonObj.get("imagesLength").getAsInt();
        Loggg.d(TAG, "recommend.imagesLength:" + imagesLength);
        if (imagesLength == 0) {
            return uploadImageList;
        }

        int rank = recommendJsonObj.get("rank").getAsInt();
        Loggg.d(TAG, "recommend.rank:" + rank);

        JsonArray imagesJsonArray = recommendJsonObj.getAsJsonArray("images");
        for (int i = 0; i < imagesLength; i++) {
            String imagePath = imagesJsonArray.get(i).getAsString();
            UploadImage uploadImage = new UploadImage(ThumbType.Recommend, rank, imagePath);
            uploadImageList.add(uploadImage);
        }

        return uploadImageList;
    }

    static class UploadImagesInfoResult {
        public final Result mResult;
        public final String mTxId;
        public final List<List<UploadImage>> mImageListList;

        UploadImagesInfoResult(Result result, String txId, List<List<UploadImage>> imageListList) {
            mTxId = txId;
            mResult = result;
            mImageListList = imageListList;
        }
    }

    /**
     * 전체 썸네일 업로드한다.
     * @param uploadImagesInfo
     * @return
     */
    public UploadImagesInfoResult uploadImages(UploadImagesInfo uploadImagesInfo) {
        boolean isValidUploadImagesInfo = (uploadImagesInfo != null);
        if (isValidUploadImagesInfo == false) {
            Loggg.e(TAG, "uploadImages() params is invalid");
            return new UploadImagesInfoResult(Result.Fail, "", new ArrayList<List<UploadImage>>());
        }

        String txId = uploadImagesInfo.getTxId();
        List<List<UploadImage>> uploadImageInfoListList = uploadImagesInfo.getImageListList();

        //대기 상황 검사
        //String thisMethodName = new Object(){}.getClass().getEnclosingMethod().getName();  //오버헤드!!!
        MethodName thisMethodName = MethodName.uploadImages;
        if (isWaitState()) {
            clearRetryCount(thisMethodName);    //연속 3회라고 가정하므로 clear
            return new UploadImagesInfoResult(Result.Wait, txId, uploadImageInfoListList);
        }

        FileUtils.mkdirs(mThumbImgCacheDir);

        int totalUploadImgCount = 0;
        for(List<UploadImage> uploadImageList : uploadImageInfoListList) {
            totalUploadImgCount += uploadImageList.size();
        }
        OverallProgress.getInstance().setTotal(OverallProgress.Part.UPLOAD_IMG_THUMB, totalUploadImgCount);

        List<List<UploadImage>> notUploadImageInfoListList = new ArrayList<List<UploadImage>>(uploadImageInfoListList);

        for(List<UploadImage> uploadImageList : uploadImageInfoListList) {
            if (mIsForceStopNetwork) {
                return new UploadImagesInfoResult(Result.Fail, "", new ArrayList<List<UploadImage>>());
            }
            boolean isSuccess = uploadImageInfoList(txId, uploadImageList);
            if (isSuccess) {
                //전부 전송 완료
                notUploadImageInfoListList.remove(uploadImageList);
            }
            else {
                //하나라도 전송 실패가 되면 추후 전부 다시 보낸다.
                Result result = processResult(thisMethodName,false);
                UploadImagesInfoResult uploadImagesInfoResult = new UploadImagesInfoResult(result, txId, notUploadImageInfoListList);
                return uploadImagesInfoResult;
            }
        }

        return new UploadImagesInfoResult(Result.Success, txId, new ArrayList<List<UploadImage>>());
    }


    /**
     * 전체 썸네일 중에서 rank 단위에 해당하는 썸네일을 업로드한다.
     * @param txId
     * @param uploadImageList
     * @return
     */
    private boolean uploadImageInfoList(String txId, List<UploadImage> uploadImageList) {
        int size = uploadImageList.size();

        //멀티 스레드로 전송하다가 마지막 것 전송할때 last flag를 변경해야 한다.
        //그래서 제일 마지막 것을 제외하고 멀티로 전송하도 이게 끝나면 남은 거 last flag 변경해서 전송한다.
        UploadImage lastUploadImage;
        if (size == 1) {
            //하나면 멀티가 아니고 그냥 전송
            lastUploadImage = uploadImageList.get(0);
        }
        else {
            //마지막 것 하나 따로 챙겨둔다.
            lastUploadImage = uploadImageList.get(size - 1);

            List<UploadImage> copyImageInfoListList = new ArrayList<UploadImage>(uploadImageList);

            copyImageInfoListList.remove(size - 1);  //마지막 것 빼두기

            boolean isSuccess = uploadImagesUseThreadPool(txId, copyImageInfoListList);
            if (isSuccess == false) {
                return false;
            }
        }

        //AI 팀 요청으로 이미지 업로드 할때 마지막은 5초 쉬고 보내라고 요청(단 이미지가 하나일때는 즉시 보냄)
        if (size > 1) {
            final int totalSleepTime = 5000;
            final int unit = 100;
            final int loopCount = totalSleepTime / unit;
            for(int i = 0; i < loopCount; i++) {
                if (mIsForceStopNetwork) {
                    return false;
                }
                try {
                    Thread.sleep(unit);
                }catch (InterruptedException e) {
                    Dlog.e(TAG, e);
                }
            }
        }

        //남은거 하나 전송
        ThumbType thumbType = lastUploadImage.getThumbType();
        int rank = lastUploadImage.getRank();
        PhotoInfo photoInfo = lastUploadImage.getPhotoInfo();
        Result result = uploadImage(txId, thumbType, rank, photoInfo,true,false);

        return (result == Result.Success);
    }


    /**
     * Thread pool을 이용해서 멀티로 업로드한다.
     * @param txId
     * @param uploadImageList
     * @return
     */
    private boolean uploadImagesUseThreadPool(String txId, List<UploadImage> uploadImageList) {
        //자세한 설명은 아래 링크 참
        //https://www.callicoder.com/java-callable-and-future-tutorial/
        mPostThumbImgListExecutorService = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

        List<Callable<Result>> uploadImageTaskList = new ArrayList<Callable<Result>>();
        for(UploadImage uploadImageInfo : uploadImageList) {
            UploadImageTask uploadImageTask = new UploadImageTask(txId, uploadImageInfo);
            uploadImageTaskList.add(uploadImageTask);
        }

        try {
            List<Future<Result>> futures = mPostThumbImgListExecutorService.invokeAll(uploadImageTaskList);
            //모든 스레드가 종료될때 까지 blocking
            for(Future<Result> future: futures) {
                Result result = future.get();
                if (result != Result.Success) {
                    return false;
                }
            }
        }catch (Exception e) {
            Loggg.e(TAG, e);
        }finally {
            if (mPostThumbImgListExecutorService != null) {
                try {
                    mPostThumbImgListExecutorService.shutdown();
                } catch (Exception e) {
                    Loggg.e(TAG, e);
                }
                mPostThumbImgListExecutorService = null;
            }
        }

        return true;
    }

    private void stopUploadImagesUseThreadPool() {
        if (mPostThumbImgListExecutorService == null) {
            return;
        }

        if (mPostThumbImgListExecutorService.isTerminated()) {
            return;

        }
        try {
            mPostThumbImgListExecutorService.shutdown();
        }catch (Exception e) {
            Loggg.e(TAG, e);
        }
    }

    /**
     * 이미지 하나를 서버에 전송한다.
     * @param txId
     * @param photoInfo
     * @param isLast
     * @param isRunThreadPool  긴급 정지를 위해서..
     * @return
     */
    private Result uploadImage(
            String txId,
            ThumbType thumbType,
            int rank,
            PhotoInfo photoInfo,
            boolean isLast,
            boolean isRunThreadPool)
    {
        if (isWaitState()) {
            return Result.Wait;
        }

        Bitmap bitmap = mImageUtils.createResizedBitmap(photoInfo.getFilePath(), ImageUtils.THUMBNAIL_PIXEL_SIZE);
        if (bitmap == null) {
            //이 경우 사용자 실시간으로 삭제한 경우에 해당 될수 있다.
            //이 상태면 업로드 전부를 중지 해야한다.
            stopUploadImagesUseThreadPool();
            return Result.Fail;
        }

        String photoFilePath = photoInfo.getFilePath();
        //File imageFile = new File(photoFilePath);
        //String fileName = imageFile.getName();
        //String photoID = Long.toString(photoInfo.getID());
        String thumbImgPath = getThumbImgFullPath(photoInfo);
        if (mImageUtils.saveBitmap(bitmap, thumbImgPath) == false) {
            bitmap.recycle();
            //이 상태면 업로드 전부를 중지 해야한다.
            stopUploadImagesUseThreadPool();
            return Result.Fail;
        }
        bitmap.recycle();

        int imgW = photoInfo.getWidth();
        int imgH = photoInfo.getHeight();
        Point point = mImageUtils.getResizeWidthAndHeight(imgW, imgH, ImageUtils.THUMBNAIL_PIXEL_SIZE);
        String thumbW = Integer.toString(point.x);
        String thumbH = Integer.toString(point.y);

        ////////////////////////////////////////////////////////////////////////////

        MediaType mediaType = MediaType.parse("multipart/form-data");

        File file = new File(thumbImgPath);
        RequestBody requestFile = RequestBody.create(mediaType, file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("middleFile", file.getName(), requestFile);

        String rcmdType = (thumbType == ThumbType.Recommend ? "10" : "20");

        Map<String, RequestBody> partMap = new HashMap<>();
        partMap.put("appType", RequestBody.create(mediaType, "android"));
        partMap.put("txId", RequestBody.create(mediaType, txId));
        partMap.put("deviceId", RequestBody.create(mediaType, mDeviceId));
        partMap.put("rcmdType", RequestBody.create(mediaType, rcmdType));
        partMap.put("uuid", RequestBody.create(mediaType, photoInfo.getUUID()));
        partMap.put("rank", RequestBody.create(mediaType, Integer.toString(rank)));
        partMap.put("imageKey", RequestBody.create(mediaType, Long.toString(photoInfo.getID())));
        partMap.put("imageOriFile", RequestBody.create(mediaType, photoFilePath));
        partMap.put("oripqW", RequestBody.create(mediaType, Integer.toString(imgW)));
        partMap.put("oripqH", RequestBody.create(mediaType, Integer.toString(imgH)));
        partMap.put("thumbW", RequestBody.create(mediaType, thumbW));
        partMap.put("thumbH", RequestBody.create(mediaType, thumbH));
        partMap.put("ot", RequestBody.create(mediaType, Integer.toString(photoInfo.getExifOrientation())));
        partMap.put("fileSize", RequestBody.create(mediaType, Long.toString(file.length())));

        float latitude = photoInfo.getExifLatitude();
        float longitude = photoInfo.getExifLongitude();
        if (latitude != PhotoInfo.NOT_EXIST_GPS_VALUE && longitude != PhotoInfo.NOT_EXIST_GPS_VALUE) {
            partMap.put("gps", RequestBody.create(mediaType, "" + latitude + "," + longitude));
        }

        Loggg.d(TAG, "upload img -> type:" + rcmdType + ", rank:" + rank + ", imageFile:" + photoFilePath);

        ///////////////////////////////////////////////////////////////////////////

        boolean isSuccess = false;
        String lastFileYN = (isLast ? "Y" : "N");
        Call<ResponseBody> call = mSyncPhotoServerAPI.postThumbImg(mUserNo, lastFileYN, body, partMap);
        if (isRunThreadPool == false) {
            mCurrentCall = call;
        }
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                isSuccess = true;
            }
            else {
                Loggg.e(TAG, "postThumbImg : " + response.toString());
            }
        }catch (Exception e) {
            Loggg.e(TAG, e);
        }finally {
            if (isRunThreadPool == false) {
                mCurrentCall = null;
            }
            //파일 꼭 지우기
            if (file.delete() == false) {
                Loggg.e(TAG, "Delete " + file.getAbsolutePath() + " is Fail!");
            }
        }

        //프로그래스바 처리
        if (isSuccess) {
            OverallProgress.getInstance().setIncrement(OverallProgress.Part.UPLOAD_IMG_THUMB, 1);
        }

        return (isSuccess ? Result.Success : Result.Fail);
    }

    private String getThumbImgFullPath(PhotoInfo photoInfo) {
        String photoFilePath = photoInfo.getFilePath();
        File imageFile = new File(photoFilePath);
        String name = imageFile.getName();
        String fileName = name;
        String fileExt = "";
        if (name.endsWith(".") == false) {
            int index = name.lastIndexOf(".");
            if (index > 0) {
                fileName = name.substring(0, index);
                fileExt = name.substring(index);
                fileName = Base64.encodeToString(fileName.getBytes(), Base64.NO_WRAP | Base64.NO_PADDING);
            }
        }

        Loggg.d(TAG, "File Name Base64:" + name + " -> " + fileName + fileExt);

        String photoID = Long.toString(photoInfo.getID());
        String thumbImgPath = mThumbImgCacheDir + File.separator + photoID + "_" + fileName + fileExt;
        return thumbImgPath;
    }


    class UploadImageTask implements Callable<Result> {
        private String mTxId;
        private UploadImage mUploadImage;

        public UploadImageTask(String txId, UploadImage uploadImage) {
            mTxId = txId;
            mUploadImage = uploadImage;
        }

        public Result call() {
            try {
                Result result = uploadImage(
                        mTxId,
                        mUploadImage.getThumbType(),
                        mUploadImage.getRank(),
                        mUploadImage.getPhotoInfo(),
                        false, true);
                return result;
            }catch (Exception e) {
                Loggg.e(TAG, e);
                return Result.Fail;
            }
        }
    }
}
