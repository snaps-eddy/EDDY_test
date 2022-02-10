package com.snaps.mobile.order.order_v2.util.org_image_upload;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.SparseArray;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;
import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderException;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.util.org_image_upload.threadpool_util.PriorityThreadFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import errorhandle.SnapsAssert;
import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 16. 5. 31..
 *
 */
    public abstract class SnapsBaseImgBackgroundUploadExecutor extends ThreadPoolExecutor {
    private static final String TAG = SnapsBaseImgBackgroundUploadExecutor.class.getSimpleName();

    public static final int USE_WORK_THREAD_COUNT = 3;  //실제 쓰레드 가용 갯수  (요청 작업이 많으면 ThreadPoolExecutor가 알아서 최대 갯수까지 가용시키는 듯 하다.)

    private static final int DEFAULT_CORE_POOL_SIZE = Math.min(Runtime.getRuntime().availableProcessors(), USE_WORK_THREAD_COUNT);
    private static final int DEFAULT_MAXIMUM_POOL_SIZE = Math.min(Runtime.getRuntime().availableProcessors(), USE_WORK_THREAD_COUNT); //최대 가용 갯수 제한 (너무 많은 쓰레드를 돌리면, 이미지 로딩이 느리다.)

    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.DAYS;
    private static final long DEFAULT_KEEP_ALIVE_TIME = 999; //사실 상 기다리는 시간 제한은 없앤다.

    private static final  BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(); //Queue pool을 사용함.

    private static final ThreadFactory backgroundPriorityThreadFactory = new PriorityThreadFactory(Process.THREAD_PRIORITY_DEFAULT); //우선 순위를 낮춘다. THREAD_PRIORITY_LOWEST가 더 낮나..?

    private static final ThreadFactory backgroundHighPriorityThreadFactory = new PriorityThreadFactory(1); //우선 순위를 낮춘다. THREAD_PRIORITY_LOWEST가 더 낮나..?

    private SparseArray<MyPhotoSelectImageData> backgroundUploadImgDataSparseArray = null;

    private SparseArray<ImageUploadRunnable> workThreadRunnableSparseArray = null;

    private SnapsImageUploadListener imageUploadListener = null;

    private Object workSyncObj = new Object();

    private Activity activity = null;

    private boolean isSuspend = false;

    protected abstract boolean isUploadedImageData(MyPhotoSelectImageData orgData);

    protected abstract void handleUploadImageOnBackground(MyPhotoSelectImageData imageData, SnapsImageUploadListener listener) throws Exception;

    SnapsBaseImgBackgroundUploadExecutor(Activity activity, RejectedExecutionHandler rejectedExecutionHandler) {
        super(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAXIMUM_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME, DEFAULT_TIME_UNIT, sPoolWorkQueue, backgroundPriorityThreadFactory, rejectedExecutionHandler);
        this.activity = activity;
        this.backgroundUploadImgDataSparseArray = new SparseArray<>();
        this.workThreadRunnableSparseArray = new SparseArray<>();
    }

    public void startUploadImages(ArrayList<MyPhotoSelectImageData> imageList) throws Exception {
        ArrayList<MyPhotoSelectImageData> addImageList = getAddedImageList(imageList);
        if (addImageList == null || addImageList.isEmpty()) {
            Dlog.d("startUploadImages() addImageList == null || addImageList.isEmpty()");
            return;
        }

        sortImageList(addImageList);

        LinkedList<ImageUploadRunnable> addWorkThreadRunnable = getAddedWorkThreadSet(addImageList);
        if (checkExistUploadImage(addWorkThreadRunnable)) {
            synchronized (getWorkSyncObj()) {
                setIsSuspend(false);

                while (!addWorkThreadRunnable.isEmpty()) {
                    ImageUploadRunnable runnable = addWorkThreadRunnable.poll();
                    if (runnable == null) continue;
                    this.execute(runnable);
                }
            }
        } else {
            Dlog.d("startUploadImages() checkExistUploadImage(addWorkThreadRunnable) == false");
        }
    }

    void sortImageList(ArrayList<MyPhotoSelectImageData> addImageList) {
        /** Hook */
    }

    private Object getWorkSyncObj() {
        return workSyncObj;
    }

    public void removeUploadImgDataList(List<MyPhotoSelectImageData> removeList) throws Exception {
        if (removeList == null || removeList.isEmpty()) return;
        for (MyPhotoSelectImageData removeData : removeList) {
            removeUploadImgData(removeData);
        }
    }

    public void removeUploadImgData(MyPhotoSelectImageData removeData) throws Exception {
        if (removeData == null) return;

        synchronized (getWorkSyncObj()) {
            int imageId = getImageDataIdInteger(removeData);
            if (getBackgroundUploadImgDataSet() != null) {
                getBackgroundUploadImgDataSet().remove(imageId);
                Dlog.d("removeUploadImgData() remove image set!");
            }

            if (getWorkThreadRunnableSet() != null) {
                ImageUploadRunnable runnable = getWorkThreadRunnableSet().get(imageId);
                if (runnable != null && !runnable.isActive()) {
                    getWorkThreadRunnableSet().remove(imageId);
                    Dlog.d("removeUploadImgData() remove runnable!");
                }
            }
        }
    }

    public void suspendUpload() {
        setIsSuspend(true);
    }

    private ArrayList<MyPhotoSelectImageData> getAddedImageList(ArrayList<MyPhotoSelectImageData> imageList) throws Exception {
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
                sendResultToActivity(SnapsOrderConstants.ORG_IMG_UPLOAD_START, null);
            }
            return true;
        }

        return false;
    }

    private SnapsImageUploadListener getImageUploadListener() {
        return imageUploadListener;
    }

    public void setImageUploadListener(SnapsImageUploadListener imageUploadListener) {
        this.imageUploadListener = imageUploadListener;
    }

    private LinkedList<ImageUploadRunnable> getAddedWorkThreadSet(ArrayList<MyPhotoSelectImageData> imageList) throws Exception {
        LinkedList<ImageUploadRunnable> addedWorkList = new LinkedList<>();
        for (MyPhotoSelectImageData newImageData : imageList) {
            if (isUploadedImageData(newImageData)) continue; //이미 올렸거나, 단말기 사진이 아니거나..

            addBackgroundUploadImageData(newImageData);
            ImageUploadRunnable runnable = new ImageUploadRunnable(newImageData);
            addedWorkList.add(runnable);

            int imageId = getImageDataIdInteger(newImageData);
            addWorkThreadRunnable(imageId, runnable);
        }
        return addedWorkList;
    }
    
    private ArrayList<MyPhotoSelectImageData> getAddedNewImageList(ArrayList<MyPhotoSelectImageData> imageList) throws Exception {
        if (getBackgroundUploadImgDataSet() == null || getBackgroundUploadImgDataSet().size() < 1) return imageList;
        if (imageList == null || imageList.isEmpty() || getWorkSyncObj() == null) return null;

        synchronized (getWorkSyncObj()) {
            ArrayList<MyPhotoSelectImageData> addedNewImageList = new ArrayList<>();
            for (MyPhotoSelectImageData newImageData : imageList) {
                if (newImageData == null) continue;
                addedNewImageList.add(newImageData);
            }
            return addedNewImageList;
        }
    }

    public boolean isContainedUploadingImageData(MyPhotoSelectImageData imageData) throws Exception {
        synchronized (getWorkSyncObj()) {
            if (getWorkThreadRunnableSet() != null) {
                SparseArray<ImageUploadRunnable> uploadRunnableSparseArray = getWorkThreadRunnableSet();
                for (int ii=0; ii<uploadRunnableSparseArray.size(); ii++) {
                    ImageUploadRunnable uploadRunnable = uploadRunnableSparseArray.valueAt(ii);
                    if (uploadRunnable == null) continue;
                    MyPhotoSelectImageData uploadingData = uploadRunnable.getImageData();
                    if (uploadingData == imageData) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private SparseArray<MyPhotoSelectImageData> getBackgroundUploadImgDataSet() {
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

    private void addBackgroundUploadImageData(MyPhotoSelectImageData imageData) throws Exception {
        if (getBackgroundUploadImgDataSet() == null || imageData == null || getWorkSyncObj() == null) return;
        synchronized (getWorkSyncObj()) {
            int imageId = getImageDataIdInteger(imageData);
            getBackgroundUploadImgDataSet().put(imageId, imageData);
        }
    }

    private void addWorkThreadRunnable(int id, ImageUploadRunnable runnable) {
        if (runnable == null || getWorkThreadRunnableSet() == null || getWorkSyncObj() == null) return;
        synchronized (getWorkSyncObj()) {
            getWorkThreadRunnableSet().put(id, runnable);
        }
    }

    private SparseArray<ImageUploadRunnable> getWorkThreadRunnableSet() {
        return workThreadRunnableSparseArray;
    }

    private void sendResultToActivity(final int resultType, final SnapsImageUploadResultData uploadResultData) {
        try {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (getImageUploadListener() != null) {
                        switch (resultType) {
                            case SnapsOrderConstants.ORG_IMG_UPLOAD_START:
                                getImageUploadListener().onImageUploadStart();
                                break;
                            case SnapsOrderConstants.ORG_IMG_UPLOAD_RESULT_TYPE_ALL_TASK_FINISHED :
                                getImageUploadListener().onImageUploadAllBackgroundTaskFinished();
                                break;
                            case SnapsOrderConstants.ORG_IMG_UPLOAD_RESULT_TYPE_SUCCESS :
                                getImageUploadListener().onImageUploadSucceed(uploadResultData);
                                break;
                            case SnapsOrderConstants.ORG_IMG_UPLOAD_RESULT_TYPE_FAIL :
                                getImageUploadListener().onImageUploadFailed(uploadResultData);
                                break;
                        }
                    }
                }
            });
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    private class ImageUploadRunnable implements Runnable {

        private MyPhotoSelectImageData imageData = null;
        private boolean isActive = false;

        public ImageUploadRunnable(MyPhotoSelectImageData imageData) {
            this.setImageData(imageData);
        }

        @Override
        public void run() {
            if (isSuspend() || getImageData() == null) return;
            setActive(true);
            synchronized (getImageData()) {
                try {
                    Thread.yield(); //될 수 있으면 MainThread에게 양보하기 위해..

                    uploadImageOnBackground();
                } catch (Exception e) {
                    SnapsAssert.assertException(activity, e);
                    try {
                        processOrgImageUploadResult(SnapsImageUploadUtil.createImageUploadResultMsgData(null, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_EXCEPTION), SnapsOrderConstants.ORG_IMG_UPLOAD_RESULT_TYPE_FAIL);
                    } catch (Exception e1) {
                        Dlog.e(TAG, e1);
                    }
                }
            }
        }

        private void updateImageUploadProgressValue(SnapsImageUploadResultData resultData) {
            if (resultData == null) return;

            int totalImageCnt = getBackgroundUploadImgDataSet() != null ? getBackgroundUploadImgDataSet().size() : 0;
            int workRunnableCnt = totalImageCnt - (getWorkThreadRunnableSet() != null ? getWorkThreadRunnableSet().size() : 0);
            resultData.setTotalImgCnt(totalImageCnt);
            resultData.setFinishedCnt(workRunnableCnt);
        }

        private void uploadImageOnBackground() throws Exception {
            if (isSuspend() || getImageData() == null) return;

            if (Looper.myLooper() == Looper.getMainLooper()) throw new SnapsOrderException("tried upload img on ui thread.");

            handleUploadImageOnBackground(getImageData(), new SnapsImageUploadListener() {
                @Override
                public void onImageUploadStart() { /** 결과 안 들어옴 **/ }

                @Override
                public void onImageUploadAllBackgroundTaskFinished() { /** 결과 안 들어옴 **/ }

                @Override
                public void onImageUploadSucceed(SnapsImageUploadResultData uploadResultData) {
                    try {
                        processOrgImageUploadResult(uploadResultData, SnapsOrderConstants.ORG_IMG_UPLOAD_RESULT_TYPE_SUCCESS);
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }

                @Override
                public void onImageUploadFailed(SnapsImageUploadResultData uploadResultData) {
                    try {
                        processOrgImageUploadResult(uploadResultData, SnapsOrderConstants.ORG_IMG_UPLOAD_RESULT_TYPE_FAIL);
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
            });
        }

        private void removeWorkThread(SnapsImageUploadResultData orgImgUploadResultData) throws Exception {
            int imageId = getImageIdFromImgUploadResultData(orgImgUploadResultData);
            if (imageId != -1) {
                if (getWorkThreadRunnableSet() != null) {
                    synchronized (getWorkThreadRunnableSet()) {
                        getWorkThreadRunnableSet().remove(imageId);
                    }
                }
            }
        }

        private int getImageIdFromImgUploadResultData(SnapsImageUploadResultData orgImgUploadResultData) throws Exception {
            return  orgImgUploadResultData != null && orgImgUploadResultData.getImageData() != null ? getImageDataIdInteger(orgImgUploadResultData.getImageData()) : -1;
        }

        private void processOrgImageUploadResult(SnapsImageUploadResultData resultData, int resultType) throws Exception {
            if (!checkValidWorkState(resultData)) return;

            removeWorkThread(resultData);

            updateImageUploadProgressValue(resultData);

            sendResultToActivity(resultType, resultData);

            checkExecutorAllTaskFinished(resultData);

            setActive(false);
        }

        private boolean checkValidWorkState(SnapsImageUploadResultData uploadResultData) {
            if (isSuspend() || getImageData() == null) {
                if (uploadResultData != null)
                    uploadResultData.setUploadResultMsg(SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_SUSPENDED);

                sendResultToActivity(SnapsOrderConstants.ORG_IMG_UPLOAD_RESULT_TYPE_FAIL, uploadResultData);
                return false;
            }
            return true;
        }

        private void checkExecutorAllTaskFinished(SnapsImageUploadResultData resultData) {
            if (resultData != null) {
                Dlog.d("checkExecutorAllTaskFinished() Background uploading"
                        + " total Image Count:" + resultData.getTotalImgCnt()
                        + ", completed:" + resultData.getFinishedCnt()
                        + ", active work thread count:" +  getActiveCount());
            }

            if (getWorkThreadRunnableSet() == null || getWorkThreadRunnableSet().size() < 1) {
                sendResultToActivity(SnapsOrderConstants.ORG_IMG_UPLOAD_RESULT_TYPE_ALL_TASK_FINISHED, null);
            }
        }

        public MyPhotoSelectImageData getImageData() {
            return imageData;
        }

        public void setImageData(MyPhotoSelectImageData imageData) {
            this.imageData = imageData;
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

    private int getImageDataIdInteger(MyPhotoSelectImageData imageData) throws Exception {
        if (imageData == null || imageData.IMAGE_ID > Integer.MAX_VALUE) throw new Exception();
        return (int) imageData.IMAGE_ID;
    }
}
