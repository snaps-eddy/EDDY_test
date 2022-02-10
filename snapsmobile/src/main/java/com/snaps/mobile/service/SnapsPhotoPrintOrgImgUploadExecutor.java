package com.snaps.mobile.service;

import android.os.Looper;
import android.os.Process;
import android.util.SparseArray;

import com.snaps.common.data.img.ImageUploadSyncLocker;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.net.CustomMultiPartEntity;
import com.snaps.common.structure.SnapsDelImage;
import com.snaps.common.structure.photoprint.ImpUploadProject;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpReq;
import com.snaps.common.utils.net.xml.GetMultiPartMethod;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.photoprint.manager.PhotoPrintProject;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;
import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderException;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsImageUploadUtil;
import com.snaps.mobile.order.order_v2.util.org_image_upload.threadpool_util.PriorityThreadFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import errorhandle.SnapsAssert;
import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;

/**
 * Created by ysjeong on 16. 5. 31..
 *
 */
public class SnapsPhotoPrintOrgImgUploadExecutor extends ThreadPoolExecutor {
    private static final String TAG = SnapsPhotoPrintOrgImgUploadExecutor.class.getSimpleName();

    private static final int USE_WORK_THREAD_COUNT = 3;  //실제 쓰레드 가용 갯수

    private static final int MAX_THREAD_COUNT = 3;  //최대 쓰레드 가용 갯수 (요청 작업이 많으면 ThreadPoolExecutor가 알아서 최대 갯수까지 가용시키는 듯 하다.)

    private static final int DEFAULT_CORE_POOL_SIZE = Math.min(Runtime.getRuntime().availableProcessors(), USE_WORK_THREAD_COUNT);
    private static final int DEFAULT_MAXIMUM_POOL_SIZE = Math.min(Runtime.getRuntime().availableProcessors(), MAX_THREAD_COUNT); //최대 가용 갯수 제한 (너무 많은 쓰레드를 돌리면, 이미지 로딩이 느리다.)

    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.DAYS;
    private static final long DEFAULT_KEEP_ALIVE_TIME = 999; //사실 상 기다리는 시간 제한은 없앤다.

    private static final  BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(); //Queue pool을 사용함.

    private static final ThreadFactory backgroundPriorityThreadFactory = new PriorityThreadFactory(Process.THREAD_PRIORITY_DEFAULT); //우선 순위를 낮춘다. THREAD_PRIORITY_LOWEST가 더 낮나..?

    private SparseArray<SnapsPhotoPrintUploadImageData> backgroundUploadImgDataSparseArray = null;

    private SparseArray<PhotoPrintOrgImgUploadRunnable> workThreadRunnableSparseArray = null;

    private SnapsPhotoPrintOrgImgUploadListener snapsPhotoPrintOrgImgUploadListener = null;

    private Object workSyncObj = new Object();

    private SnapsPhotoPrintOrgImgUploadResultData orgImgUploadResultData = null;
    private Object orgImgUploadResultSyncLocker = new Object();
    private AtomicBoolean isOrgImgUploadResultHandling = new AtomicBoolean(false);

    private AtomicBoolean isOrgImgUploading = new AtomicBoolean(false);
    private ImageUploadSyncLocker orgImgUploadSyncLocker = new ImageUploadSyncLocker();

    private boolean isSuspend = false;

    SnapsPhotoPrintOrgImgUploadExecutor() {
        super(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAXIMUM_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME, DEFAULT_TIME_UNIT, sPoolWorkQueue, backgroundPriorityThreadFactory, new DiscardPolicy()); //Thread 가용 범위를 넘어선다면, 그냥 안 올리는 정책을 택 했다.
        this.backgroundUploadImgDataSparseArray = new SparseArray<>();
        this.workThreadRunnableSparseArray = new SparseArray<>();
    }

    void initWorkSet() {
        if (backgroundUploadImgDataSparseArray != null && backgroundUploadImgDataSparseArray.size() > 0)
            backgroundUploadImgDataSparseArray.clear();

        if (workThreadRunnableSparseArray != null && workThreadRunnableSparseArray.size() > 0)
            workThreadRunnableSparseArray.clear();

        orgImgUploadResultData = SnapsPhotoPrintOrgImgUploadResultData.createDefaultInstance();
    }

    void startUploadImages(ImpUploadProject project) throws Exception {
        initWorkSet();

        ArrayList<SnapsPhotoPrintUploadImageData> imageList = createUploadListWithProject(project);
        if (imageList == null || imageList.isEmpty()) {
            Dlog.d("startUploadImages() images is not exist.");
            return;
        }

        LinkedList<PhotoPrintOrgImgUploadRunnable> addWorkThreadRunnable = getAddedWorkThreadSet(imageList);
        if (checkExistUploadImage(addWorkThreadRunnable)) {
            startOrgImgUploadSyncLock();

            synchronized (getWorkSyncObj()) {
                setIsSuspend(false);

                while (!addWorkThreadRunnable.isEmpty()) {
                    PhotoPrintOrgImgUploadRunnable runnable = addWorkThreadRunnable.poll();
                    if (runnable == null) continue;
                    this.execute(runnable);
                }
            }

            waitIfOrgImgUploading();
        } else {
            Dlog.d("startUploadImages() isn't exist images to upload");
        }
    }

    private ArrayList<SnapsPhotoPrintUploadImageData> createUploadListWithProject(ImpUploadProject project) throws Exception {
        ArrayList<SnapsPhotoPrintUploadImageData> imageList = new ArrayList<>();
        for (int i = 0; i < project.getItemCount(); i++) {
            String fileName = project.getOriginalPathWithIndex(i);

            // 이미 올라간 사진인 경우 다시 올리지 않는다.
            int imageKind = project.getImageKindWithIndex(i);
            if (imageKind != Const_VALUES.SELECT_UPLOAD) {
                PhotoPrintData photoPrintData = project.getPhotoPrintDataWithIndex(i);
                if (photoPrintData != null) {
                    MyPhotoSelectImageData imageData = photoPrintData.getMyPhotoSelectImageData();
                    if (imageData != null) {
                        SnapsPhotoPrintUploadImageData uploadImageData = new SnapsPhotoPrintUploadImageData.Builder()
                                .setImageId((int)imageData.IMAGE_ID)
                                .setImageKind(imageKind)
                                .setFileName(fileName)
                                .setMyPhotoSelectImageData(imageData)
                                .setProject(project)
                                .create();

                        imageList.add(uploadImageData);
                    }
                }
            }
        }

        return getAddedImageList(imageList);
    }

    private Object getWorkSyncObj() {
        return workSyncObj;
    }

    void suspendUpload() {
        setIsSuspend(true);
    }

    private ArrayList<SnapsPhotoPrintUploadImageData> getAddedImageList(ArrayList<SnapsPhotoPrintUploadImageData> imageList) throws Exception {
        if (isUploading()) { //이미 업로드를 하고 있다면, 새로운 데이터만 추가해준다.
            return getAddedNewImageList(imageList);
        } else {
            initBackgroundUploadImageWithRunnable();
            return imageList;
        }
    }
    
    private boolean checkExistUploadImage(LinkedList workList) {
        if (workList != null && !workList.isEmpty()) {
            if (!isUploading()) {
                sendResultToActivity(null, SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult.START);
            }
            return true;
        }

        return false;
    }

    SnapsPhotoPrintOrgImgUploadListener getSnapsPhotoPrintOrgImgUploadListener() {
        return snapsPhotoPrintOrgImgUploadListener;
    }

    void setSnapsPhotoPrintOrgImgUploadListener(SnapsPhotoPrintOrgImgUploadListener snapsPhotoPrintOrgImgUploadListener) {
        this.snapsPhotoPrintOrgImgUploadListener = snapsPhotoPrintOrgImgUploadListener;
    }

    private LinkedList<PhotoPrintOrgImgUploadRunnable> getAddedWorkThreadSet(ArrayList<SnapsPhotoPrintUploadImageData> imageList) throws Exception {
        LinkedList<PhotoPrintOrgImgUploadRunnable> addedWorkList = new LinkedList<>();
        for (SnapsPhotoPrintUploadImageData newImageData : imageList) {
            if (isUploadedImageData(newImageData)) continue;

            addBackgroundUploadImageData(newImageData);
            PhotoPrintOrgImgUploadRunnable runnable = new PhotoPrintOrgImgUploadRunnable(newImageData);
            addedWorkList.add(runnable);

            int imageId = getImageDataIdInteger(newImageData);
            addWorkThreadRunnable(imageId, runnable);
        }
        return addedWorkList;
    }

    private boolean isUploadedImageData(SnapsPhotoPrintUploadImageData imageData) throws Exception {
        MyPhotoSelectImageData myPhotoSelectImageData = imageData.getMyPhotoSelectImageData();
        try {
            SnapsImageUploadUtil.fixInvalidUploadedOrgImageData(myPhotoSelectImageData);
        } catch (Exception e) { Dlog.e(TAG, e); }
        return !SnapsImageUploadUtil.shouldBeOrgImgUploadWithImageData(myPhotoSelectImageData) && !myPhotoSelectImageData.isUploadFailedOrgImage;
    }

    private ArrayList<SnapsPhotoPrintUploadImageData> getAddedNewImageList(ArrayList<SnapsPhotoPrintUploadImageData> imageList) throws Exception {
        if (getBackgroundUploadImgDataSet() == null || getBackgroundUploadImgDataSet().size() < 1) return imageList;
        if (imageList == null || imageList.isEmpty() || getWorkSyncObj() == null) return null;

        synchronized (getWorkSyncObj()) {
            ArrayList<SnapsPhotoPrintUploadImageData> addedNewImageList = new ArrayList<>();
            for (SnapsPhotoPrintUploadImageData newImageData : imageList) {
                if (newImageData == null) continue;
                addedNewImageList.add(newImageData);
            }
            return addedNewImageList;
        }
    }

    private SparseArray<SnapsPhotoPrintUploadImageData> getBackgroundUploadImgDataSet() {
        return backgroundUploadImgDataSparseArray;
    }

    private void initBackgroundUploadImageWithRunnable() throws Exception {
        synchronized (getWorkSyncObj()) {
            if (getBackgroundUploadImgDataSet() != null) {
                getBackgroundUploadImgDataSet().clear();
            }

            if (getWorkThreadRunnableSet() != null) {
                getBackgroundUploadImgDataSet().clear();
            }
        }
    }

    private void addBackgroundUploadImageData(SnapsPhotoPrintUploadImageData imageData) throws Exception {
        if (getBackgroundUploadImgDataSet() == null || imageData == null || getWorkSyncObj() == null) return;
        synchronized (getWorkSyncObj()) {
            int imageId = getImageDataIdInteger(imageData);
            getBackgroundUploadImgDataSet().put(imageId, imageData);
        }
    }

    private void addWorkThreadRunnable(int id, PhotoPrintOrgImgUploadRunnable runnable) {
        if (runnable == null || getWorkThreadRunnableSet() == null || getWorkSyncObj() == null) return;
        synchronized (getWorkSyncObj()) {
            getWorkThreadRunnableSet().put(id, runnable);
        }
    }

    private SparseArray<PhotoPrintOrgImgUploadRunnable> getWorkThreadRunnableSet() {
        return workThreadRunnableSparseArray;
    }

    private void sendResultToActivity(SnapsPhotoPrintOrgImgUploadResultData uploadResultData, SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult resultMsg) {
        setUploadResultData(uploadResultData);
        sendResultToActivity(resultMsg);
    }

    private void sendResultToActivity(SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult resultMsg) {
        if (getSnapsPhotoPrintOrgImgUploadListener() == null || getUploadResultData() == null) return;

        getSnapsPhotoPrintOrgImgUploadListener().onPhotoPrintOrgImgUploadResult(resultMsg, getUploadResultData());
    }

    private void startOrgImgUploadSyncLock() {
        isOrgImgUploading.set(true);
    }

    private void waitIfOrgImgUploading() {
        if (isOrgImgUploading.get()) {
            synchronized (getUploadSyncLocker()) {
                if (isOrgImgUploading.get()) {
                    try {
                        Dlog.d("waitIfOrgImgUploading() photo print org image uploader locked.");
                        getUploadSyncLocker().wait();
                    } catch (InterruptedException e) {
                        Dlog.e(TAG, e);
                    }
                }

                Dlog.d("waitIfOrgImgUploading() photo print org image uploader un-locked.");
            }
        }
    }

    void finishUploadSyncLock() {
        if (isOrgImgUploading.get()) {
            isOrgImgUploading.set(false);
            synchronized (getUploadSyncLocker()) {
                getUploadSyncLocker().notifyAll();
            }
        }
    }

    private ImageUploadSyncLocker getUploadSyncLocker() {
        return orgImgUploadSyncLocker;
    }

    private SnapsPhotoPrintOrgImgUploadResultData getUploadResultData() {
        return orgImgUploadResultData;
    }

    private void setUploadResultData(SnapsPhotoPrintOrgImgUploadResultData data) {
        this.orgImgUploadResultData = data;
    }

    public Object getOrgImgUploadResultSyncLocker() {
        return orgImgUploadResultSyncLocker;
    }

    private synchronized void waitIfUploadResultHandling(SnapsPhotoPrintOrgImgUploadResultData resultData) throws InterruptedException {
        if (isOrgImgUploadResultHandling.get()) {
            synchronized (getOrgImgUploadResultSyncLocker()) {
                if (isOrgImgUploadResultHandling.get()) {
                    Dlog.d("waitIfUploadResultHandling() wait");
                    getOrgImgUploadResultSyncLocker().wait();
                    Dlog.d("waitIfUploadResultHandling() notified");
                }
            }
        }

        startUploadResultHandle(resultData);
    }

    private void startUploadResultHandle(SnapsPhotoPrintOrgImgUploadResultData resultData) {
        isOrgImgUploadResultHandling.set(true);

        setUploadResultData(resultData);
    }

    private void completeUploadResultHandle() {
        if (isOrgImgUploadResultHandling.get()) {
            isOrgImgUploadResultHandling.set(false);
            synchronized (getOrgImgUploadResultSyncLocker()) {
                getOrgImgUploadResultSyncLocker().notifyAll();
            }
        }
    }

    private class PhotoPrintOrgImgUploadRunnable implements Runnable {
        private SnapsPhotoPrintUploadImageData imageData = null;
        private boolean isActive = false;

        private PhotoPrintOrgImgUploadRunnable(SnapsPhotoPrintUploadImageData imageData) {
            this.setImageData(imageData);
        }

        @Override
        public void run() {
            if (isSuspend() || getImageData() == null) {
                try {
                    Dlog.d("PhotoPrintOrgImgUploadRunnable : isSuspend() " + isSuspend() + ", getImageData() : " + getImageData());
                    processOrgImageUploadResult(createOrgImgUploadResultMsgData(getImageData()), SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult.SUSPENDED);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    SnapsAssert.assertException(e);
                }
                return;
            }

            setActive(true);
            synchronized (getImageData()) {
                try {
                    Thread.yield(); //될 수 있으면 MainThread에게 양보하기 위해..

                    uploadImageOnBackground();
                } catch (Exception e) {
                    try {
                        processOrgImageUploadResult(createOrgImgUploadResultMsgData(getImageData()), SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult.EXCEPTION);
                    } catch (Exception e1) {
                        Dlog.e(TAG, e1);
                        SnapsAssert.assertException(e);
                    }
                }
            }
        }

        public SnapsPhotoPrintUploadImageData getImageData() {
            return imageData;
        }

        public PhotoPrintOrgImgUploadRunnable setImageData(SnapsPhotoPrintUploadImageData imageData) {
            this.imageData = imageData;
            return this;
        }

        private void updateOrgImgUploadProgressValue() {
            if (getUploadResultData() == null) return;

            int totalImageCnt = getBackgroundUploadImgDataSet() != null ? getBackgroundUploadImgDataSet().size() : 0;
            int workRunnableCnt = totalImageCnt - (getWorkThreadRunnableSet() != null ? getWorkThreadRunnableSet().size() : 0);
            getUploadResultData().setTotalImgCnt(totalImageCnt);
            getUploadResultData().setFinishedCnt(workRunnableCnt);
        }

        private void uploadImageOnBackground() throws Exception {
            if (Looper.myLooper() == Looper.getMainLooper()) throw new SnapsOrderException("tried upload img on ui thread.");

            if (!checkValidWorkState()) {
                processOrgImageUploadResult(createOrgImgUploadResultMsgData(getImageData()), SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult.SUSPENDED);
                return;
            }

            // 이미 올라간 사진인 경우 다시 올리지 않는다.
            int imageKind = getImageData().getImageKind();
            if (imageKind == Const_VALUES.SELECT_UPLOAD) {
                processOrgImageUploadResult(createOrgImgUploadResultMsgData(getImageData()), SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult.IMG_KIND_IS_UPLOADED);
                return;
            }

            String fileName = getImageData().getFileName();
            ImpUploadProject project = getImageData().getProject();

            String message = requestOrgImgUpload(imageKind, fileName, null, project, getImageData().getImageId(),getImageData().getMyPhotoSelectImageData().mineType);

            if( StringUtil.isEmpty(message)) {
                processOrgImageUploadResult(createOrgImgUploadResultMsgData(getImageData()), SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult.RESULT_IS_EMPTY);
            } else if (SnapsImageUploadUtil.isThumbnailErrorMsg(message)) {
                Dlog.d("uploadImageOnBackground() retryOrgImageUploadWithThumbnail!");
                message = retryOrgImageUploadWithThumbnail(fileName, imageKind, fileName, project, getImageData().getImageId(), getImageData().getMyPhotoSelectImageData().mineType);
                Dlog.d("uploadImageOnBackground() retryOrgImageUploadWithThumbnail result:" + message);
            }

            if (message == null) {
                SnapsLogger.appendOrderLog("failed upload org img : message is null.");
                processOrgImageUploadResult(createOrgImgUploadResultMsgData(getImageData()), SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult.RESULT_IS_FAIL);
                return;
            }

            String[] returnValue = message.replace("||", "|").split("\\|");

            if (isUploadSuccess(returnValue)) {
                SnapsDelImage delImage = createDelImageByReturnValue(imageKind, returnValue);
                project.setItemImgSeqWithImageId(getImageData().getImageId(), delImage);

                if (isImageSizeInfoError(getImageData(), delImage)) {
                    processOrgImageUploadResult(createOrgImgUploadResultMsgData(getImageData()), SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult.RESULT_IS_FAIL);
                    return;
                }

                processOrgImageUploadResult(createOrgImgUploadResultMsgData(getImageData()), SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult.RESULT_IS_SUCCESS);
            } else {
                SnapsLogger.appendOrderLog("failed upload org img : " + message);
                processOrgImageUploadResult(createOrgImgUploadResultMsgData(getImageData()), SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult.RESULT_IS_FAIL);
            }
        }

        private boolean isImageSizeInfoError(SnapsPhotoPrintUploadImageData imageData, SnapsDelImage delImage) {
            if (imageData == null || delImage == null) return false;

            String uploadedImgSize = delImage.sizeOrgImg;
            if( !StringUtil.isEmpty(uploadedImgSize) && uploadedImgSize.contains(" ") ) {
                String[] sizeString = uploadedImgSize.split(" ");
                float imageW = Float.parseFloat(sizeString[0]);
                float imageH = Float.parseFloat(sizeString[1]);
                if (imageW <= 0 || imageH <= 0) {
                    MyPhotoSelectImageData myPhotoSelectImageData = imageData.getMyPhotoSelectImageData();
                    if (myPhotoSelectImageData != null) {
                        imageW = Float.parseFloat(myPhotoSelectImageData.F_IMG_WIDTH);
                        imageH = Float.parseFloat(myPhotoSelectImageData.F_IMG_HEIGHT);
                    }
                }

                return imageW <= 0 || imageH <= 0;
            }

            return false;
        }

        private SnapsPhotoPrintOrgImgUploadResultData createOrgImgUploadResultMsgData(SnapsPhotoPrintUploadImageData imageData) {
            return new SnapsPhotoPrintOrgImgUploadResultData.Builder().setImageData(imageData).create();
        }

        private boolean isUploadSuccess(String[] message) throws Exception {
            return message != null && message.length > 0 && message[0].contains("SUCCESS");
        }

        private SnapsDelImage createDelImageByReturnValue(int imageKind, String[] returnValue) throws Exception {
            boolean snsImage = imageKind != Const_VALUES.SELECT_PHONE;
            // Save Del Image 저장.
            SnapsDelImage delImg = new SnapsDelImage();
            if( snsImage ) {
                delImg.imgYear = returnValue[2].replace("/", "");
                delImg.imgSeq = returnValue[3].replace("/", "");
                delImg.uploadPath = returnValue[7];
                delImg.tinyPath = returnValue[9];
                delImg.oriPath = returnValue[10];
                delImg.sizeOrgImg = returnValue[4] + " " + returnValue[5];
                delImg.shootDate = returnValue[0];
                delImg.usedImgCnt = "0";
                delImg.thumbNailUrl = returnValue[8];
            } else {
                delImg.imgYear = returnValue[2].replace("/", "");
                delImg.imgSeq = returnValue[3].replace("/", "");
                delImg.uploadPath = returnValue[1];
                delImg.tinyPath = returnValue[9];
                delImg.oriPath = returnValue[7];
                delImg.sizeOrgImg = returnValue[4] + " " + returnValue[5];
                delImg.shootDate = returnValue[0];
                delImg.usedImgCnt = "0";
                delImg.thumbNailUrl = returnValue[8];
            }
            return delImg;
        }

        private String requestOrgImgUpload(int imageKind, String fileName, String thumbnailPath, ImpUploadProject project, int imageId,String mineType) throws IOException {
            String message = "";
            if( imageKind == Const_VALUES.SELECT_PHONE ) {
                // 로컬 사진만 체크를 한다.
                File file = new File(fileName);
                if (file == null || !file.exists()) {
                    return null;
                }

                HttpResponse response = GetMultiPartMethod.getOrgImageUploadForOldVersion(fileName, thumbnailPath, project.getProjectCode(), new CustomMultiPartEntity.ProgressListener() {
                    @Override
                    public void transferred(long num, long total) {

                    }
                }, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

                if (response != null) {
                    if (response.getStatusLine().getStatusCode() != 200) {
                        // 네트워크 오류...
                        SnapsLogger.appendOrderLog("snaps photo print org image upload result fail status code : " + response.getStatusLine().getStatusCode());
                        return null;
                    }

                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
                    message = FileUtil.convertStreamToString(is);
                }
            } else {
                String thumbPath = "";
                if( project instanceof PhotoPrintProject)
                    thumbPath = ( (PhotoPrintProject) project ).getThumbnailWithImageId( imageId );
                message = HttpReq.saveSNSImage(fileName, thumbPath, imageKind, project.getProjectCode(),mineType, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            }
            return message;
        }

        private String retryOrgImageUploadWithThumbnail(String orgFilePath, int imageKind, String fileName, ImpUploadProject project, int imageId, String mineType) throws Exception {
            String thumbnailFilePath = null;
            try {
                Dlog.d("retryOrgImageUploadWithThumbnail()");
                thumbnailFilePath = CropUtil.createThumbnailFile(orgFilePath);
                if (StringUtil.isEmpty(thumbnailFilePath)) return "";
                return requestOrgImgUpload(imageKind, fileName, thumbnailFilePath, project, imageId, mineType);
            } finally {
                if (!StringUtil.isEmpty(thumbnailFilePath)) {
                    FileUtil.deleteFile(thumbnailFilePath);
                    Dlog.d("retryOrgImageUploadWithThumbnail() delete temp thumbnail file.");
                }
            }
        }

        private void removeWorkThread() throws Exception {
            int imageId = getImageIdFromImgUploadResultData(getUploadResultData());
            if (imageId != -1) {
                if (getWorkThreadRunnableSet() != null) {
                    synchronized (getWorkThreadRunnableSet()) {
                        getWorkThreadRunnableSet().remove(imageId);
                    }
                }
            }
        }

        private void removeBackgroundWorkSetIfUploadFailed(SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult resultMsg) throws Exception {
            if (resultMsg == null || !isUploadFailedMsg(resultMsg)) return;

            int imageId = getImageIdFromImgUploadResultData(getUploadResultData());
            if (imageId != -1) {
                if (getBackgroundUploadImgDataSet() != null) {
                    synchronized (getBackgroundUploadImgDataSet()) {
                        getBackgroundUploadImgDataSet().remove(imageId);
                        Dlog.d("removeBackgroundWorkSetIfUploadFailed() removed photo print upload work set : " + imageId);
                    }
                }
            }
        }

        private boolean isUploadFailedMsg(SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult resultMsg) {
            if (resultMsg == null) return false;
            return resultMsg == SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult.RESULT_IS_FAIL ||  resultMsg == SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult.RESULT_IS_EMPTY;
        }

        private int getImageIdFromImgUploadResultData(SnapsPhotoPrintOrgImgUploadResultData orgImgUploadResultData) throws Exception {
            return  orgImgUploadResultData != null && orgImgUploadResultData.getImageData() != null ? getImageDataIdInteger(orgImgUploadResultData.getImageData()) : -1;
        }

        private synchronized void processOrgImageUploadResult(SnapsPhotoPrintOrgImgUploadResultData resultData, SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult resultMsg) throws Exception {
            if (!checkValidWorkState()) return;

            try {
                waitIfUploadResultHandling(resultData);

                removeWorkThread();

                removeBackgroundWorkSetIfUploadFailed(resultMsg);

                updateOrgImgUploadProgressValue(); //여기서 자르니까 이상해지네.

                sendResultToActivity(resultMsg);

                checkExecutorAllTaskFinished();

                setActive(false);
            } finally {
                completeUploadResultHandle();
            }
        }

        private boolean checkValidWorkState() {
            if (isSuspend() || getImageData() == null) {
                Dlog.d("checkValidWorkState() : isSuspend() " + isSuspend() + ", getImageData() : " + getImageData());
                sendResultToActivity(createOrgImgUploadResultMsgData(getImageData()), SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult.SUSPENDED);
                return false;
            }
            return true;
        }

        private void checkExecutorAllTaskFinished() {
            if (getUploadResultData() != null) {
                Dlog.d("checkExecutorAllTaskFinished() Background uploading -> total Image Count : " + getUploadResultData().getTotalImgCnt() + ", completed : " + getUploadResultData().getFinishedCnt() + " (active work thread count : " +  getActiveCount() + ")");
            }

            if (getWorkThreadRunnableSet() == null || getWorkThreadRunnableSet().size() < 1) {
                sendResultToActivity(getUploadResultData(), SnapsPhotoPrintOrgImgUploadListener.ePhotoPrintOrgImgUploadResult.COMPLETED);
            }
        }


        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }
    }

    private boolean isSuspend() {
        return isSuspend;
    }

    private void setIsSuspend(boolean isStop) {
        this.isSuspend = isStop;
    }

    public boolean isUploading() {
        return this.getActiveCount() > 0;
    }

    private int getImageDataIdInteger(SnapsPhotoPrintUploadImageData imageData) throws Exception {
        if (imageData == null || imageData.getImageId() > Integer.MAX_VALUE) throw new Exception();
        return imageData.getImageId();
    }
}
