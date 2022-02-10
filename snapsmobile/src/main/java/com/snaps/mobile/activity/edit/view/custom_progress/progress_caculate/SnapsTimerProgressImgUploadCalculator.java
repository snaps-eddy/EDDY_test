package com.snaps.mobile.activity.edit.view.custom_progress.progress_caculate;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsImageUploadUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by ysjeong on 2017. 4. 13..
 */

public class SnapsTimerProgressImgUploadCalculator {
    private static final int UPLOAD_PROGRESS_MEASURE_COUNT = 7;

    private ArrayList<MyPhotoSelectImageData> uploadImageList = null;
    private int localImgTotalCount = 0;
    private int uploadedImgCount = 0;

    private LinkedList<Float> queUploadSpeeds = null;

    public void initUploadImageList(ArrayList<MyPhotoSelectImageData> imgList) {
        this.uploadImageList = imgList;

        setLocalImgTotalCount();

        uploadedImgCount = 0;
    }

    public void releaseInstance() {
        if (queUploadSpeeds != null) {
            queUploadSpeeds.clear();
            queUploadSpeeds = null;
        }
    }

    private void setLocalImgTotalCount() {
        localImgTotalCount = 0;
        if (uploadImageList == null || uploadImageList.isEmpty()) return;
        for (MyPhotoSelectImageData imageData : uploadImageList)
            if (SnapsImageUploadUtil.shouldBeOrgImgUploadWithImageData(imageData)) localImgTotalCount++;
    }

    public void updateOrgImgUploadSpeed(float downloadPerSec) {
        if (queUploadSpeeds == null)
            queUploadSpeeds = new LinkedList<>();

        if(queUploadSpeeds.size() > UPLOAD_PROGRESS_MEASURE_COUNT)
            queUploadSpeeds.poll();

        queUploadSpeeds.offer(downloadPerSec);
    }

    public float getMeasureAvgNetworkSpeed() throws Exception {
        if(queUploadSpeeds == null || queUploadSpeeds.size() < UPLOAD_PROGRESS_MEASURE_COUNT) return 0;

        Float maxValue = Collections.max(queUploadSpeeds);
        Float minValue = Collections.min(queUploadSpeeds);
        queUploadSpeeds.remove(maxValue);
        queUploadSpeeds.remove(minValue);

        float total = 0.f;
        for(float spd : queUploadSpeeds)
            total += spd;

        return (total > 0 ? total / queUploadSpeeds.size() : 0);
    }

    public int getUploadedImgCount() {
        return uploadedImgCount;
    }

    public void addUploadedImgCount() {
        this.uploadedImgCount++;
    }

    public int getLocalImgTotalCount() {
        return localImgTotalCount;
    }

    public ArrayList<MyPhotoSelectImageData> getUploadImageList() {
        return uploadImageList;
    }
}
