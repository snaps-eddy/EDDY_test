package com.snaps.mobile.utils.smart_snaps.analysis.task;

import android.app.Activity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;
import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisListener;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2018. 4. 24..
 */

public class SmartRecommendBookFaceFitCenterTask extends SmartRecommendBookAnalysisBaseTask {
    private static final String TAG = SmartRecommendBookFaceFitCenterTask.class.getSimpleName();
    public SmartRecommendBookFaceFitCenterTask(Activity activity, SmartSnapsAnalysisListener analysisListener) {
        super(activity, analysisListener);
    }

    @Override
    public SmartSnapsConstants.eSmartSnapsAnalysisTaskType getTaskType() {
        return SmartSnapsConstants.eSmartSnapsAnalysisTaskType.FIT_CENTER_FACE;
    }

    @Override
    public void perform() {
        super.perform();

        try {
            if (tryFaceFitCenter()) {
                sendComplete();
            } else {
                sendFailed("isExistEmptyImageLayerOnTemplate");
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            sendException(e);
        }
    }

    private boolean tryFaceFitCenter() throws Exception {
        SnapsTemplate snapsTemplate = getAnalysisTemplate();
        ArrayList<MyPhotoSelectImageData> uploadImageList = getAnalysisImageList();

        SmartSnapsUtil.insertImageListToTemplateByAnalysisKey(getActivity(), snapsTemplate, uploadImageList, false);

        if (SmartSnapsUtil.isExistEmptyImageLayerOnTemplate(snapsTemplate)) {
            return false;
        }

        SmartSnapsUtil.fixImageLayerAreaOnTemplateBySmartSnapsInfo(getActivity(), snapsTemplate);
        return true;
    }

    private SnapsTemplate getAnalysisTemplate() {
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        return snapsTemplateManager.getSnapsTemplate();
    }

    private ArrayList<MyPhotoSelectImageData> getAnalysisImageList() {
        DataTransManager dataTransManager = DataTransManager.getInstance();
        if (dataTransManager != null) {
            return dataTransManager.getPhotoImageDataList();
        }
        return null;
    }
}
