package com.snaps.mobile.activity.edit.view.custom_progress.progress_caculate;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import java.io.File;

/**
 * Created by ysjeong on 2017. 4. 12..
 */

public class SnapsTimerProgressImgUploadFileUnit {
    private long uploadMeasureTime = 0;

    private File measureTargetFile = null;

    public void setOffsetForMeasureImageData(MyPhotoSelectImageData imageData) {
        File targetFile = SnapsTimerProgressProjectUploadCalculator.getImageDataFile(imageData);
        if (targetFile != null) {
            measureTargetFile = targetFile;
            uploadMeasureTime = System.currentTimeMillis();
        } else {
            measureTargetFile = null;
            uploadMeasureTime = 0;
        }
    }

    public float updateUploadSpeed() {
        long uploadedTimeSec = (System.currentTimeMillis() - uploadMeasureTime) / 1000;
        if (uploadedTimeSec > 60 || uploadedTimeSec <= 0 || measureTargetFile == null || measureTargetFile.length() < 1) return 0;

        return measureTargetFile.length() / uploadedTimeSec;
    }
}
