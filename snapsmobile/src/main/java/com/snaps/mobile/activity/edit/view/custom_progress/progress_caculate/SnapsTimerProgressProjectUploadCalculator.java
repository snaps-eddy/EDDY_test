package com.snaps.mobile.activity.edit.view.custom_progress.progress_caculate;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_views.SnapsProgressViewAPI;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsImageUploadUtil;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsOrgImgBackgroundUploadExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ysjeong on 2017. 4. 12..
 */

/**
 * 프로젝트 업로드 상태를 바 모양 프로그래스로 표현하며, 남은 예상 시간을 계산하여 보여준다.
 */
public class SnapsTimerProgressProjectUploadCalculator extends SnapsTimerProgressBaseCalculator {
    private static final String TAG = SnapsTimerProgressProjectUploadCalculator.class.getSimpleName();
    private static final long BASE_ESTIMATED_TIME_FOR_MAIN_THUMBNAIL = 2000;
    private static final long BASE_ESTIMATED_TIME_FOR_UPLOAD_ORG_IMG = 700;
    private static final long BASE_ESTIMATED_TIME_FOR_UPLOAD_XML = 1000;

    private HashMap<SnapsProgressViewAPI.eTimerProgressTaskType, SnapsTimerProgressTask> progressTasks = null;

    private HashMap<MyPhotoSelectImageData, SnapsTimerProgressImgUploadFileUnit> imgUploadMeasureUnits = null;

    private SnapsTimerProgressImgUploadCalculator orgImgUploadCalculator = null;

    public static SnapsTimerProgressProjectUploadCalculator createUploadProgressTaskCalculator() {
        return new SnapsTimerProgressProjectUploadCalculator();
    }

    public static File getImageDataFile(MyPhotoSelectImageData imageData) {
        if (imageData == null) return null;
        File file = new File(imageData.PATH);
        return file.exists() ? file : null;
    }

    @Override
    public void releaseData() {
        try {
            if (progressTasks != null) {
                progressTasks.clear();
                progressTasks = null;
            }

            if (imgUploadMeasureUnits != null) {
                imgUploadMeasureUnits.clear();
                imgUploadMeasureUnits = null;
            }

            if (orgImgUploadCalculator != null) {
                orgImgUploadCalculator.releaseInstance();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private SnapsTimerProgressProjectUploadCalculator() {
        init();
    }

    private void init() {
        createProgressTask();

        initBaseEstimatedTime();

        imgUploadMeasureUnits = new HashMap<>();

        orgImgUploadCalculator = new SnapsTimerProgressImgUploadCalculator();
    }

    private void initBaseEstimatedTime() {
        try {
            updateEstimatedTime(SnapsProgressViewAPI.eTimerProgressTaskType.TASK_TYPE_UPLOAD_MAIN_THUMBNAIL, BASE_ESTIMATED_TIME_FOR_MAIN_THUMBNAIL);
            updateEstimatedTime(SnapsProgressViewAPI.eTimerProgressTaskType.TASK_TYPE_UPLOAD_ORG_IMG, BASE_ESTIMATED_TIME_FOR_UPLOAD_ORG_IMG);
            updateEstimatedTime(SnapsProgressViewAPI.eTimerProgressTaskType.TASK_TYPE_HANDLE_XML, BASE_ESTIMATED_TIME_FOR_UPLOAD_XML);
        } catch (NullPointerException e) {
            Dlog.e(TAG, e);
        }
    }

    private SnapsTimerProgressTask getProgressTaskByTaskType(SnapsProgressViewAPI.eTimerProgressTaskType type) {
        return progressTasks != null && progressTasks.containsKey(type) ? progressTasks.get(type) : null;
    }

    private void createProgressTask() {
        progressTasks = new HashMap<>();
        SnapsProgressViewAPI.eTimerProgressTaskType[] tasks = SnapsProgressViewAPI.eTimerProgressTaskType.values();
        for (SnapsProgressViewAPI.eTimerProgressTaskType task : tasks) {
            progressTasks.put(task, new SnapsTimerProgressTask(task));
        }
    }

    public void updateProgressValue(SnapsProgressViewAPI.eTimerProgressTaskType targetTask, int value) {
        SnapsTimerProgressTask progressTask = getProgressTaskByTaskType(targetTask);
        if (progressTask != null) progressTask.setProgressValue(value);
    }

    public void updateEstimatedTime(SnapsProgressViewAPI.eTimerProgressTaskType targetTask, long value) {
        SnapsTimerProgressTask progressTask = getProgressTaskByTaskType(targetTask);
        if (progressTask != null) progressTask.setEstimatedTime(value);
    }

    @Override
    public int getCurrentProgressValue() {
        try {
            SnapsProgressViewAPI.eTimerProgressTaskType[] tasks = SnapsProgressViewAPI.eTimerProgressTaskType.values();
            int progressValue = 0;
            for (SnapsProgressViewAPI.eTimerProgressTaskType task : tasks) {
                SnapsTimerProgressTask progressTask = getProgressTaskByTaskType(task);
                if (progressTask != null) progressValue += progressTask.getWeightAppliedProgressValue();
            }
            return progressValue;
        } catch (NullPointerException e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

    @Override
    public int getAllTaskRemainEstimatedTime() {
        try {
            SnapsProgressViewAPI.eTimerProgressTaskType[] tasks = SnapsProgressViewAPI.eTimerProgressTaskType.values();
            int estimatedTime = 0;
            for (SnapsProgressViewAPI.eTimerProgressTaskType task : tasks) {
                SnapsTimerProgressTask progressTask = getProgressTaskByTaskType(task);
                if (progressTask != null) estimatedTime += progressTask.getEstimatedTime();
            }
            return estimatedTime;
        } catch (NullPointerException e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

    public void initOrgImgUploadCalculator(ArrayList<MyPhotoSelectImageData> imgList) {
        try {
            orgImgUploadCalculator.initUploadImageList(imgList);

            int orgImgUploadEstimatedTime = (orgImgUploadCalculator.getLocalImgTotalCount() * (2000 / SnapsOrgImgBackgroundUploadExecutor.USE_WORK_THREAD_COUNT)); //대략적인 업로드 완료 시간을 측정한다.
            updateEstimatedTime(SnapsProgressViewAPI.eTimerProgressTaskType.TASK_TYPE_UPLOAD_ORG_IMG, orgImgUploadEstimatedTime);
        } catch (NullPointerException e) {
            Dlog.e(TAG, e);
        }
    }

    private ArrayList<MyPhotoSelectImageData> getUploadImageList() {
        return orgImgUploadCalculator != null ? orgImgUploadCalculator.getUploadImageList() : null;
    }

    private int getLocalImageTotalCount() {
        return orgImgUploadCalculator != null ? orgImgUploadCalculator.getLocalImgTotalCount() : 0;
    }

    private int getUploadedImageCount() {
        return orgImgUploadCalculator != null ? orgImgUploadCalculator.getUploadedImgCount() : 0;
    }

    public void addUploadedImageCount() {
        try {
            orgImgUploadCalculator.addUploadedImgCount();
        } catch (NullPointerException e) {
            Dlog.e(TAG, e);
        }
    }

    public void updateOrgImgUploadEstimateTime(MyPhotoSelectImageData imageData) {
        try {
            if (!imgUploadMeasureUnits.containsKey(imageData)) return;
            SnapsTimerProgressImgUploadFileUnit measureUnit = imgUploadMeasureUnits.remove(imageData);
            orgImgUploadCalculator.updateOrgImgUploadSpeed(measureUnit.updateUploadSpeed());
            long estimatedTime = getExpectedRemainTimeForOrgImgUpload(orgImgUploadCalculator.getMeasureAvgNetworkSpeed());

            if (estimatedTime > 0)
                updateEstimatedTime(SnapsProgressViewAPI.eTimerProgressTaskType.TASK_TYPE_UPLOAD_ORG_IMG, estimatedTime);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void updateOrgImgUploadProgressValue() {
        try {
            if (getUploadImageList() == null || getUploadImageList().isEmpty()) return;
            int progress = (int) ((getUploadedImageCount() / (float) getLocalImageTotalCount()) * 100);
            updateProgressValue(SnapsProgressViewAPI.eTimerProgressTaskType.TASK_TYPE_UPLOAD_ORG_IMG, progress);
        } catch (NullPointerException e) {
            Dlog.e(TAG, e);
        }
    }

    public void setOffsetForMeasureImageData(MyPhotoSelectImageData imageData) {
        if (SnapsImageUploadUtil.isOrgImgUploadedOnImageData(imageData)) return;
            try {
            SnapsTimerProgressImgUploadFileUnit measureUnit = new SnapsTimerProgressImgUploadFileUnit();
            measureUnit.setOffsetForMeasureImageData(imageData);

            imgUploadMeasureUnits.put(imageData, measureUnit);
        } catch (NullPointerException e) {
                Dlog.e(TAG, e);
            }
    }

    private long getExpectedRemainTimeForOrgImgUpload(float speed) {
        if (speed <= 0 || getUploadImageList() == null || getUploadImageList().isEmpty()) return -1;

        long totalFileSize = 0;
        for (MyPhotoSelectImageData imageData : getUploadImageList()) {
            if (SnapsImageUploadUtil.isOrgImgUploadedOnImageData(imageData)) continue;
            File file = getImageDataFile(imageData);
            if (file != null) {
                totalFileSize += file.length();
            }
        }

        if (totalFileSize > 0) {
            return (long) ((totalFileSize / speed) * 1000);
        }

        return -1;
    }
}
