package com.snaps.mobile.service.ai;

import android.content.Context;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 실제 사진 업로드 관련 작업을 하는 클래스
 */
class SyncWorkingThread extends Thread {
    private static final String TAG = SyncWorkingThread.class.getSimpleName();
    private static final String TXID_DATE_FORMAT = "yyyyMMddHHmmss";
    private static final int SLEEP_TIME_MILLISECOND = 250;  //주의! 반드시 1000의 약수로 설정 할 것
    private static final int WAIT_SHORT_TIME_SECOND = 10;
    private static final int WAIT_MEDIUM_TIME_SECOND = 30;
    private static final int WAIT_LONG_TIME_SECOND = 60;
    public static final String SYNC_ROOT_DIR_NAME = "ai_sync_photo";
    public static final String DB_DIR_NAME = "databases";
    public static final String EXIF_DIR_NAME = "exif";

    private Context mContext;
    private DeviceManager mDeviceManager;
    private int mSleepUnitCount;
    private String mExifDirName;
    private String mDeviceId;
    private volatile boolean mIsRunning;
    private NetClient.UploadImagesInfo mUploadImagesInfo;
    private SyncPhotoDB.ChangePhotoInfo mChangePhotoInfo;
    private PhotoInfosCreator mPhotoInfosCreator;
    private SyncPhotoDB mSyncPhotoDB;
    private NetClient mNetClient;
    private EventListener mEventListener;
    private int mNetFailCount;
    private String mUserNo;
    private volatile WorkStatus mWorkStatus;

    public interface EventListener {
        void onSentExifZipFile(boolean isInit, String userNo, String deviceId); //프로그래스 바를 그리기 위해서 필요
        void onChangeWorkStatus(WorkStatus workStatus);
        void onStop();
        void onUncatchedException(String msg, Throwable throwable);
    }

    public enum WorkStatus {
        NONE,
        INIT,
        CONFIRM_SEND_EXIF_INFO,
        CHECK_CHANGE_PHOTO,
        CREATE_EXIF_INFO,
        SEND_EXIF_INFO,
        RECEIVE_UPLOAD_PHOTO_LIST,
        UPLOAD_PHOTO_THUMB_FILES,
        WAIT_SHORT,
        WAIT_MEDIUM,
        WAIT_LONG
    }

    public SyncWorkingThread(
            Context context,
            String AISyncPhotoDomain,
            String userNo,
            String deviceId,
            EventListener eventListener)
    {
        mContext = context;
        mUserNo = userNo;

        String externalFilesDirPath = mContext.getExternalFilesDir(null).getAbsolutePath();
        String syncRoot = externalFilesDirPath + File.separator + SYNC_ROOT_DIR_NAME;
        FileUtils.mkdirs(syncRoot);

        //사용자 번호로 디렉토리를 만든다.
        String userRoot = syncRoot + File.separator + mUserNo;
        FileUtils.mkdirs(userRoot);

        mExifDirName = userRoot + File.separator + EXIF_DIR_NAME;
        FileUtils.mkdirs(mExifDirName);

        String databaseDir = userRoot + File.separator + DB_DIR_NAME;
        FileUtils.mkdirs(databaseDir);

        mEventListener = eventListener;
        mDeviceId = deviceId;
        mIsRunning = true;
        mPhotoInfosCreator = new PhotoInfosCreator();
        mDeviceManager = new DeviceManager(mContext);
        mNetClient = new NetClient(mContext, AISyncPhotoDomain, mUserNo, mDeviceId, mDeviceManager);
        mNetFailCount = 0;

        mSyncPhotoDB = new SyncPhotoDB(mContext, syncRoot, databaseDir);
    }

    /**
     * 긴급 정지
     */
    public void forceStop() {
        Loggg.d(TAG, "forceStop");
        mIsRunning = false;
        mPhotoInfosCreator.forceStop();
        mSyncPhotoDB.forceStop();
        mNetClient.forceStop();
    }

    public boolean isForceStop() {
        if (mPhotoInfosCreator.isForceStop()) return true;
        if (mSyncPhotoDB.isForceStop()) return true;
        if (mNetClient.isForceStop()) return true;

        return false;
    }

    /**
     * 네트워크 전송 중지
     */
    public void forceStopNetwork() {
        Loggg.d(TAG, "forceStopNetwork");
        mNetClient.forceStopNetwork();
        mWorkStatus = WorkStatus.INIT;
    }

    @Override
    public void run() {
        Loggg.d(TAG, "start run()");

        boolean isInit = (mSyncPhotoDB.getRowCount() == 0);
        if (isInit) {
            //최초 사진 동기화될때 까지 소요되는 시간을 모니터링 하는 것 시작
            Monitoring.getInstance().startSyncInitCompleateCheck();
        }

        try {
            process();
        }catch (Throwable t) {
            Loggg.e(TAG, t);
            String report = ErrorReport.getInstance().create(mContext, mUserNo);
            if (mEventListener != null) {
                mEventListener.onUncatchedException(report, t);
            }
        }

        if (mEventListener != null) {
            mEventListener.onStop(); //바인드 된 클라이언트가 있어서 종료 안되고 없으면 종료 된다.
        }

        //최초 사진 동기화될때 까지 소요되는 시간을 모니터링 하는 것 종
        Monitoring.getInstance().stopSyncInitCompleateCheck();

        Loggg.d(TAG, "stop run()");
    }

    private void process() {
        Loggg.d(TAG, "start process()");

        mSleepUnitCount = 0;

        mWorkStatus = WorkStatus.INIT;
        WorkStatus preWorkStatus = WorkStatus.NONE;

        while(isInterrupted() == false) {
            try {
                Thread.sleep(SLEEP_TIME_MILLISECOND);
            } catch (InterruptedException e) {
                break;
            }

            //긴급 정지
            if (mIsRunning == false) {
                Loggg.d(TAG, "forceStop!");
                break;
            }

            //상태 변화
            if (mWorkStatus != preWorkStatus) {
                Loggg.d(TAG, "process() Status:" + preWorkStatus + " -> " + mWorkStatus);
                Monitoring.getInstance().setInfo("WorkStatus", preWorkStatus + " -> " + mWorkStatus);
                if (mEventListener != null) {
                    mEventListener.onChangeWorkStatus(mWorkStatus);
                }
            }
            preWorkStatus = mWorkStatus;

            //외장 메모리 접근 권한 (사진 파일 때문에)
            if (mDeviceManager.isGantedPermissionReadExternalStorage() == false) {
                continue;
            }

            work();
        }

        Loggg.d(TAG, "stop process()");
    }


    private void work() {
        switch(mWorkStatus) {
            case NONE:
                break;

            case INIT:
                workInit();
                break;

            case CONFIRM_SEND_EXIF_INFO:
                workConfirmSendExifInfo();
                break;

            case CHECK_CHANGE_PHOTO:
                workCheckChangePhoto();
                break;

            case CREATE_EXIF_INFO:
                workCreateExifInfo();
                break;

            case SEND_EXIF_INFO:
                workSendExifInfo();
                break;

            case RECEIVE_UPLOAD_PHOTO_LIST:
                workReceiveUploadPhotoList();
                break;

            case UPLOAD_PHOTO_THUMB_FILES:
                workUploadPhotoThumbFiles();
                break;

            case WAIT_SHORT:
                workWait(WAIT_SHORT_TIME_SECOND);
                break;

            case WAIT_MEDIUM:
                workWait(WAIT_MEDIUM_TIME_SECOND);
                break;

            case WAIT_LONG:
                workWait(WAIT_LONG_TIME_SECOND);
                break;
        }
    }

    private void workWait(int waitSecond) {
        int millisecond = SLEEP_TIME_MILLISECOND * mSleepUnitCount;
        if (millisecond % 1000 == 0) {
            int second = millisecond / 1000;    //초 계산이 좀 복잡한데...
            if (second >= waitSecond) {
                mSleepUnitCount = 0;
                mWorkStatus = WorkStatus.INIT;
            }
            else {
                int countDown = waitSecond - second;
                Loggg.d(TAG, "" + mWorkStatus + ":" + countDown);
            }
        }

        mSleepUnitCount++;
    }

    private void workInit() {
        mNetClient.resetIsForceStopNetwork();
        mNetClient.clearAllRetryCount();  //전송 재시도 횟수를 초기화 한다.
        mWorkStatus = WorkStatus.CONFIRM_SEND_EXIF_INFO;
    }

    /**
     * 과거 또는 이전 단계에서 전체 사진 Exif 정보를 서버에 전송했는지 확인하고 전송을 했다면 서버의 처리 단계를 확인한다.
     */
    private void workConfirmSendExifInfo() {
        SyncPhotoDB.MustSendExifFileInfo mustSendExifFileInfo = mSyncPhotoDB.getMustSendExifFileInfo();
        if (mustSendExifFileInfo == null) {
            //이전 작업에서 전송하지 못한 Exif 정보 파일이 없으면 사진 변화를 확인하는 단계로 전환
            Loggg.d(TAG, "mustSendExifFileInfo is not exist");
            mWorkStatus = WorkStatus.CHECK_CHANGE_PHOTO;
            return;
        }

        Loggg.d(TAG, "mustSendExifFileInfo : " + mustSendExifFileInfo);
        NetClient.ConfirmSentExifZipFileResult result = mNetClient.confirmSentExifZipFile(mustSendExifFileInfo.mTxId);
        switch (result.mResult) {
            case Success:
                Loggg.d(TAG, "confirmSentExifZipFile : " + result.mSyncStatus);
                processConfirmSentExifZipFileResult(result.mSyncStatus);
                break;

            case Fail:
                Loggg.e(TAG, "confirmSentExifZipFile():" + result.mResult);
                mNetFailCount++;
                mWorkStatus = WorkStatus.WAIT_SHORT;  //실패하면 처음부터 다시 시작
                break;

            case Retry:
            case Wait:
                //상태를 변경하지 않는다. 아무것도 하지 않는다.
                //필요하면 로그 정도 추가한다.
                break;
        }
    }

    /**
     * 서버의 전체 사진 Exif 정보 처리 상태를 확인한다.
     * @param syncStatus
     */
    private void processConfirmSentExifZipFileResult(NetClient.SyncStatus syncStatus) {
        switch (syncStatus) {
            case OK:
                //과거에 이미 전송한 경우
                //또는 현재 전송 후 서버 처리가 끝난 경우
                mSyncPhotoDB.deleteMustSendExifInfoFile(); //전송이 성공하면 전송해야하는 Exif 파일 정보를 DB에서 지운다.
                FileUtils.deleteAllInDirectory(mExifDirName); // 전송이 성공하면 파일을 지운다.
                OverallProgress.getInstance().setPercent(OverallProgress.Part.UPLOAD_EXIF_INFO, 100);  //프로그래스바인데 의미 없
                mWorkStatus = WorkStatus.CHECK_CHANGE_PHOTO;
                break;

            case Fail:
                //과거에 전송 한 것을 서버가 수신하지 못한 경우
                mWorkStatus = WorkStatus.SEND_EXIF_INFO;
                break;

            case Processing:
                //서버가 수신했고 처리 중인 경우
                mWorkStatus = WorkStatus.WAIT_MEDIUM;
                break;
        }
    }

    /**
     * 사진 Sync DB와 미디어 스토어(파일 시스템상의 사진 파일)를 비교한다.
     */
    private void workCheckChangePhoto() {
        if (mSyncPhotoDB.getRowCount() == 0) {
            //최초 사진 동기화
            int photoCount = mPhotoInfosCreator.getPhotoCount(mContext);
            Loggg.d(TAG, "Target Photo Count:" + photoCount);
            Monitoring.getInstance().setInfo("Target Photo Count", photoCount);
        }

        final String TAG_CREATE_EXIF_INFO = "Create Exif info";
        PerformanceMeasurementTool.measure(TAG_CREATE_EXIF_INFO);

        //미디어 스토어에서 대상 사진 목록을 구한다.
        Map<String, PhotoInfo> photoInfoMap = mPhotoInfosCreator.createPhotoInfoMapExceptForExif(mContext);

        //미디어 스토어에서 구한 사진 파일과 동기화 DB를 비교한다.
        mChangePhotoInfo = mSyncPhotoDB.compare(photoInfoMap);
        if (mChangePhotoInfo == null) {
            throw new RuntimeException("Failed to compare photo info and database info");
        }

        PerformanceMeasurementTool.measure(TAG_CREATE_EXIF_INFO);

        Loggg.d(TAG, "Change Photo Info => " + mChangePhotoInfo.getSummaryText());
        Monitoring.getInstance().setInfo("Change Photo Info", mChangePhotoInfo.getSummaryText());

        if (mChangePhotoInfo.isChanged()) {
            //사진이 변경
            mWorkStatus = WorkStatus.CREATE_EXIF_INFO;
        }
        else {
            if (mSyncPhotoDB.getRowCount() == 0) {
                //최초 동기화인데 사진이 없는 경우
                if (Monitoring.getInstance().isSentNotAvailablePhoto()) {
                    mWorkStatus = WorkStatus.WAIT_LONG;
                }
                else {
                    mWorkStatus = WorkStatus.CREATE_EXIF_INFO;
                }
            }
            else {
                //서버와 단말의 사진 정보 동기화 후 업로드해야 하는 썸네일 정보를 서버에서 수신 받아야 한다.
                mWorkStatus = WorkStatus.RECEIVE_UPLOAD_PHOTO_LIST;
            }
        }
    }

    /**
     * 변경된 사진 정보를 서버에 전송하기 위해서 Exif 정보를 만든다.
     */
    private void workCreateExifInfo() {
        String syncType;
        if (mSyncPhotoDB.getRowCount() == 0) {
            if (mChangePhotoInfo.isChanged()) {
                syncType = NetClient.EXIF_SYNC_TYPE_INIT;
            }
            else {
                syncType = NetClient.EXIF_SYNC_TYPE_NOT_AVAILABLE;
                Monitoring.getInstance().setSentNotAvailablePhoto(true);
            }
        }
        else {
            syncType = NetClient.EXIF_SYNC_TYPE_UPDATE;
        }

        SimpleDateFormat sf = new SimpleDateFormat(TXID_DATE_FORMAT);
        String txId = sf.format(new Date());

        //변경된 사진 정보를 Json으로 만든다.
        PhotoInfoToJsonCreator photoInfoToJsonCreator = new PhotoInfoToJsonCreator();
        JSONObject allPhotoInfoJsonObj = photoInfoToJsonCreator.create(syncType, mDeviceId, txId, mChangePhotoInfo);
        if (allPhotoInfoJsonObj == null) {
            throw new RuntimeException("Failed to convert photo information to json object");
        }

        String allPhotoInfoString = allPhotoInfoJsonObj.toString();
        allPhotoInfoJsonObj = null; //명시적 GC 대상 지정. 이게 도움이 될까...

        String zipFilePath = createZipFile(txId, allPhotoInfoString);
        if (zipFilePath == null || zipFilePath.length() == 0) {
            throw new RuntimeException("exif info zip file creation failed");
        }

        //DB에 변경된 정보 반영, 반영 할때 위에서 생성한 exif zip file도 같이 등록한다.
        //같이 등록한 이유는 동기화 문제 때문에
        //exif zip file은 생성되었는데 DB에 변경 정보 반영하다가 중지된 경우 동기화가 깨진다.
        mSyncPhotoDB.sync(mChangePhotoInfo, zipFilePath, txId, syncType);
        mChangePhotoInfo.clear();

        //파일 전송으로 상태 변경
        mWorkStatus = WorkStatus.SEND_EXIF_INFO;
    }

    /**
     * Json String을 파일로 만들고 압축한다.
     * @param fileName
     * @param exifInfoString
     * @return
     */
    private String createZipFile(String fileName, String exifInfoString) {
        String fullPath = mExifDirName + File.separator + fileName;
        String fileFullPathTxt = fullPath + ".txt";
        String fileFullPathZip = fileFullPathTxt + ".zip";

        //확인용으로 디렉토리 생성(굳이 할 필요는 없는데...)
        if (FileUtils.mkdirs(mExifDirName) == false) {
            Loggg.e(TAG, "mkdirs error:" + mExifDirName);
            return "";
        }

        //텍스트 파일을 만든다.
        if (FileUtils.write(fileFullPathTxt, exifInfoString) == false) {
            Loggg.e(TAG, "write file error:" + fullPath);
            return "";
        }

        //for log
        File txtFile = new File(fileFullPathTxt);
        Loggg.d(TAG, "exif file size:" + PerformanceMeasurementTool.humanReadableByteCount(txtFile.length()));

        //생성한 텍스트 파일을 압축 파일로 만든다.
        boolean isSuccess = FileUtils.compress(fileFullPathTxt, fileFullPathZip);
        if (txtFile.delete() == false) {
            Loggg.e(TAG, "delete file error:" + fileFullPathTxt);
        }
        if (isSuccess == false) {
            Loggg.e(TAG, "compress file error:" + fileFullPathTxt + " -> " + fileFullPathZip);
            return "";
        }

        //for log
        File zipFile = new File(fileFullPathZip);
        Loggg.d(TAG, "exif zip file size:" + PerformanceMeasurementTool.humanReadableByteCount(zipFile.length()));

        return fileFullPathZip;
    }

    /**
     * 사진 정보 Exif 파일을 서버에 전송한다.
     */
    private void workSendExifInfo() {
        SyncPhotoDB.MustSendExifFileInfo mustSendExifFileInfo = mSyncPhotoDB.getMustSendExifFileInfo();
        if (mustSendExifFileInfo == null) {
            throw new RuntimeException("Exif information file information error that needs to be transferred");
        }

        String filePath = mustSendExifFileInfo.mFilePath;
        String syncType = mustSendExifFileInfo.mSyncType;

        NetClient.Result result = mNetClient.sendExifZipFile(filePath, syncType);
        switch (result) {
            case Success:
                if (mEventListener != null) {
                    boolean isInit = syncType.equals(NetClient.EXIF_SYNC_TYPE_INIT);
                    mEventListener.onSentExifZipFile(isInit, mUserNo, mDeviceId);  //웹에서 보여주는 프로그래스바 처리
                }
                //여기 주의!!
                //전송 완료 후 업로드 해야하는 썸네일 목록을 곧바로 요청하지 않는다.
                //이유는 서버가 Exif 정보를 분석할 시간이 필요하기 때문이다.
                //그래서 작업 상태를 Wait로 변경한다.
                //Wait가 끝나면 작업은 처음부터 다시 시작되고, 작업의 시작은 이전에 보낸 Exif정보를 서버가 정상 수신했는지 확인하는 단계이다.
                mWorkStatus = WorkStatus.WAIT_MEDIUM;
                break;

            case Fail:
                Loggg.e(TAG, "sendExifZipFile():" + result);
                mNetFailCount++;
                mWorkStatus = WorkStatus.WAIT_SHORT;  //처음부터 다시 시작
                break;

            case Retry:
            case Wait:
                //상태를 변경하지 않는다.
                break;
        }
    }

    /**
     * 업로드 해야하는 썸네일 목록을 서버에 요청한다.
     */
    private void workReceiveUploadPhotoList() {
        NetClient.ReceiveUploadImageListResult result = mNetClient.receiveUploadImageList();
        switch(result.mResult) {
            case Success:
                int size = result.mImageListList.size();
                if (size == 0) {
                    //더 이상 할 것이 없다.
                    Monitoring.getInstance().stopSyncInitCompleateCheck();  // 모니터링 종료

                    OverallProgress.getInstance().setPercent(OverallProgress.Part.UPLOAD_IMG_THUMB, 100);
                    mWorkStatus = WorkStatus.WAIT_LONG; // 더 이상 할것이 없어도 종료하지 않고 일단 대기 상태로 변경한다.
                    return;
                }

                Monitoring.getInstance().addInfo("receive upload photo count", size);

                if (setPhotoInfoInTheList(result.mImageListList)) {
                    SimpleDateFormat sf = new SimpleDateFormat(TXID_DATE_FORMAT);
                    String txId = sf.format(new Date());
                    mUploadImagesInfo =
                            new NetClient.UploadImagesInfo(txId, result.mImageListList);
                    mWorkStatus = WorkStatus.UPLOAD_PHOTO_THUMB_FILES;
                }
                else {
                    //파일이 하나라도 없으면 업로드 할 수 없으므로 싱크를 다시 맞춘다.
                    mWorkStatus = WorkStatus.CHECK_CHANGE_PHOTO;
                }
                break;

            case Fail:
                Loggg.e(TAG, "receiveUploadImageList():" + result.mResult);
                mNetFailCount++;
                mWorkStatus = WorkStatus.WAIT_SHORT;  //처음부터 다시 시작
                break;

            case Wait:
            case Retry:
                //상태를 변경하지 않는다.
                break;
        }
    }

    /**
     * 이미지 path에 해당하는 PhotoInfo를 찾아서 설정한다.
     * (메소드가 구조가 좀 이상한데..)
     * @param uploadImageInfoListList
     * @return
     */
    private boolean setPhotoInfoInTheList(List<List<NetClient.UploadImage>> uploadImageInfoListList) {
        boolean isSuccess = true;

        //업로드 요청한 파일 목록을 리스트로 변환
        List<String> imgPathList = new ArrayList<String>();
        for(List<NetClient.UploadImage> uploadImageList : uploadImageInfoListList) {
            for (NetClient.UploadImage uploadImage : uploadImageList) {
                imgPathList.add(uploadImage.getImgPath());
            }
        }

        PerformanceMeasurementTool.measure("selectPhotoInfoList - upload thumb img");
        //DB에 업로드 요청한 파일의 정보를 구한다.
        List<PhotoInfo> photoInfoList = mSyncPhotoDB.selectPhotoInfoList(SyncPhotoDB.COLUMN_NAME_FILE_PATH, imgPathList);
        PerformanceMeasurementTool.measure("selectPhotoInfoList - upload thumb img");

        //업로드 요청한 목록의 파일이 단말에 존재하는지 검사한다.
        if (photoInfoList.size() != imgPathList.size()) {
            Loggg.e(TAG, "The photo you requested to upload does not exist."
                    + " request:" + imgPathList.size()
                    + " exist:" + photoInfoList.size());
            return false;
        }

        int index = 0;
        for(List<NetClient.UploadImage> uploadImageList : uploadImageInfoListList) {
            for (NetClient.UploadImage uploadImage : uploadImageList) {
                PhotoInfo photoInfo = photoInfoList.get(index);
                index++;
                uploadImage.setPhotoInfo(photoInfo);
            }
        }

        return isSuccess;
    }

    /**
     * 썸네일 이미지를 서버에 업로드 한다.
     */
    private void workUploadPhotoThumbFiles() {
        NetClient.UploadImagesInfoResult result = mNetClient.uploadImages(mUploadImagesInfo);
        switch(result.mResult) {
            case Success:
                Monitoring.getInstance().stopSyncInitCompleateCheck();  // 모니터링 종료
                mWorkStatus = WorkStatus.WAIT_SHORT;  //업로드 전체 성공하면 잠깐 쉰다. 핸드폰 배터리 발열 문제가 발생 할 수 있으므로
                break;

            case Fail:
                mNetFailCount++;
                Loggg.e(TAG, "uploadImages():" + result.mResult);
                mWorkStatus = WorkStatus.WAIT_SHORT; //처음부터 다시 시작
                break;

            case Wait:
                //상태를 변경하지 않는다.
                break;

            case Retry:
                //전송되지 않고 남은 사진 파일로 업로드해야하는 리스트를 갱신한다.  <- 속도 향상 목적
                mUploadImagesInfo.setImageListList(result.mImageListList);
                break;
        }
    }
}
