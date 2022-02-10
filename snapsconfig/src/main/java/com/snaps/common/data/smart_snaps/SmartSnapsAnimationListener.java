package com.snaps.common.data.smart_snaps;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;

/**
 * Created by ysjeong on 2018. 1. 17..
 */

public interface SmartSnapsAnimationListener {
    void onSmartSnapsAnimationStart(SmartSnapsConstants.eSmartSnapsProgressType progressType);
    void onSmartSnapsAnimationFinish(SmartSnapsConstants.eSmartSnapsProgressType progressType);
    void onSmartSnapsAnimationUpdateProgress(SmartSnapsConstants.eSmartSnapsProgressType progressType, int totalCount, int finishCount);

    void requestSmartAnimationWithPage(int page);
    void requestRefreshPageThumbnail(int page);
    void requestAnimation(MyPhotoSelectImageData imageData);
    void onOccurredException(Exception e);
}
