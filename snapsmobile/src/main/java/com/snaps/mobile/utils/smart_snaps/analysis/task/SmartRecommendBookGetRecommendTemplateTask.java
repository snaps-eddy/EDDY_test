package com.snaps.mobile.utils.smart_snaps.analysis.task;

import android.app.Activity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;
import com.snaps.mobile.utils.smart_snaps.analysis.exception.SmartSnapsAnalysisException;
import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ysjeong on 2018. 4. 24..
 */

public class SmartRecommendBookGetRecommendTemplateTask extends SmartRecommendBookAnalysisBaseTask {
    private static final String TAG = SmartRecommendBookGetRecommendTemplateTask.class.getSimpleName();
    SmartRecommendBookGetRecommendTemplateTask(Activity activity, SmartSnapsAnalysisListener analysisListener) {
        super(activity, analysisListener);
    }

    @Override
    public SmartSnapsConstants.eSmartSnapsAnalysisTaskType getTaskType() {
        return SmartSnapsConstants.eSmartSnapsAnalysisTaskType.GET_RECOMMEND_TEMPLATE;
    }

    @Override
    public void perform() {
        super.perform();

        startFetchSmartAnalysisTemplate();
    }

    private void startFetchSmartAnalysisTemplate() {
        try {
            if (isCanceled()) return;

            if (!Config.isValidProjCode()) throw new SmartSnapsAnalysisException("is not validProjCode.");

            requestAnalysisImageList();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            sendException(e);
        }
    }

    private void requestAnalysisImageList() throws SmartSnapsAnalysisException {
        if (isCanceled()) return;

        DataTransManager dataTransManager = DataTransManager.getInstance();
        if (dataTransManager != null) {
            ArrayList<MyPhotoSelectImageData> imageList = dataTransManager.getPhotoImageDataList();

            setSmartAnalysisImageTypeOnImageData(imageList);

            SmartSnapsUtil.requestAnalysisImageList(imageList, new SnapsCommonResultListener<SnapsTemplate>() {
                @Override
                public void onPrepare() {
                    super.onPrepare();
                }

                @Override
                public void onResult(SnapsTemplate template) {
                    if (isCanceled()) return;
                    if (template != null) {
                        handleFetchedTemplate(template);

                        setBaseTemplateCodeOnTemplate(template);

                        sendComplete();
                    } else {
                        sendFailed("template is null.");
                    }
                }

                @Override
                public void onException(Exception e) {
                    super.onException(e);
                    sendException(e);
                }

                private void setBaseTemplateCodeOnTemplate(SnapsTemplate template) {
                    if (template == null || template.info == null) return;
                    SnapsTemplateInfo snapsTemplateInfo = template.info;
                    if (!StringUtil.isEmpty(snapsTemplateInfo.F_TMPL_CODE)) {
                        Config.setTMPL_CODE(snapsTemplateInfo.F_TMPL_CODE);
                    }
                }
            });
        }
    }

    private boolean setPageTypeAndReturnCoverExist(ArrayList<MyPhotoSelectImageData> imageList) {
        if (imageList == null) return false;
        boolean isExistCover = false;
        for (MyPhotoSelectImageData imageData : imageList) {
            if (imageData == null) continue;

            if (SmartSnapsManager.getInstance().isContainCoverPhotoMapKey(imageData.getImageSelectMapKey())) {
                imageData.setPageType(SmartSnapsConstants.eSmartSnapsAnalysisImagePageType.COVER);
                isExistCover = true;
            } else {
                imageData.setPageType(SmartSnapsConstants.eSmartSnapsAnalysisImagePageType.PAGE);
            }
        }
        return isExistCover;
    }

    private void setSmartAnalysisImageTypeOnImageData(ArrayList<MyPhotoSelectImageData> imageList) throws SmartSnapsAnalysisException {
        if (imageList == null) return;

        //우선 커버로 선택 된 사진을 찾는다 없으면, 에러.
        if (!setPageTypeAndReturnCoverExist(imageList)) {
            throw new SmartSnapsAnalysisException("is not selected cover.");
        }

        //얼굴이 젤 많이 들어있는 사진 중 가장 최근 사진을 선택해서 title 페이지에 넣어준다.
        List<MyPhotoSelectImageData> photoWithMostFacesList = new ArrayList<>();
        final int mostFacesCount = getMostFacesCountOnImageList(imageList);
        for (MyPhotoSelectImageData imageData : imageList) {
            if (imageData == null) continue;

            if (imageData.getPageType() != SmartSnapsConstants.eSmartSnapsAnalysisImagePageType.COVER) {
                if (imageData.getSmartSnapsSearchedFaceCount() == mostFacesCount) {
                    photoWithMostFacesList.add(imageData);
                }
            }
        }

        if (!photoWithMostFacesList.isEmpty()) {
            if (photoWithMostFacesList.size() > 1) {
                Collections.sort(photoWithMostFacesList, new Comparator<MyPhotoSelectImageData>() {
                    @Override
                    public int compare(MyPhotoSelectImageData lhs, MyPhotoSelectImageData rhs) {
                        return lhs.photoTakenDateTime > rhs.photoTakenDateTime ? -1 : (lhs.photoTakenDateTime < rhs.photoTakenDateTime ? 1 : 0);
                    }
                });
            }

            MyPhotoSelectImageData titlePageImage = photoWithMostFacesList.get(0);
            if (titlePageImage != null) {
                titlePageImage.setPageType(SmartSnapsConstants.eSmartSnapsAnalysisImagePageType.TITLE);
            }
        }
    }

    private int getMostFacesCountOnImageList(ArrayList<MyPhotoSelectImageData> imageList) {
        if (imageList == null) return 0;

        int mostFacesCount = 0;
        for (MyPhotoSelectImageData imageData : imageList) {
            if (imageData == null || imageData.getPageType() == SmartSnapsConstants.eSmartSnapsAnalysisImagePageType.COVER) continue;
            mostFacesCount = Math.max(mostFacesCount, imageData.getSmartSnapsSearchedFaceCount());
        }
        return mostFacesCount;
    }

    private void handleFetchedTemplate(SnapsTemplate template) {
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        snapsTemplateManager.setSnapsTemplate(template);
    }
}
