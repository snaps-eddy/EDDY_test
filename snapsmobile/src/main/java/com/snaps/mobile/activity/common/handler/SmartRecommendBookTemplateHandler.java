package com.snaps.mobile.activity.common.handler;

import android.app.Activity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.img.SmartSnapsImageAreaInfo;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.common.SmartRecommendBookMainActivity;
import com.snaps.mobile.activity.common.data.SmartRecommendBookHandlerInstance;
import com.snaps.mobile.activity.common.data.SnapsProductEditInfo;
import com.snaps.mobile.activity.common.interfacies.SnapsEditActExternalConnectionBridge;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;
import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisTaskImp;
import com.snaps.mobile.utils.smart_snaps.analysis.task.SmartRecommendBookAnalysisTaskChecker;
import com.snaps.mobile.utils.smart_snaps.analysis.task.SmartRecommendBookTaskFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eSmartSnapsAnalysisTaskType.GET_COVER_TEMPLATE;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eSmartSnapsAnalysisTaskType.GET_PAGE_BG_RES;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eSmartSnapsAnalysisTaskType.GET_PAGE_TEMPLATE;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eSmartSnapsAnalysisTaskType.GET_RECOMMEND_TEMPLATE;

public class SmartRecommendBookTemplateHandler {
    private static final String TAG = SmartRecommendBookTemplateHandler.class.getSimpleName();

    public interface SmartRecommendBookTemplateHandleResultListener {
        void onLoadedTemplate();
        void onFailedGetTemplate();
    }

    public static SmartRecommendBookTemplateHandler createHandlerWithInstance(SmartRecommendBookHandlerInstance instance) {
        SmartRecommendBookTemplateHandler templateHandler = new SmartRecommendBookTemplateHandler();
        templateHandler.actExternalConnectionBridge = instance.getExternalConnectionBridge();
        templateHandler.snapsProductEditInfo = instance.getSnapsProductEditInfo();
        return templateHandler;
    }

    private SmartRecommendBookTemplateHandler() {}

    private SnapsEditActExternalConnectionBridge actExternalConnectionBridge = null;
    private SnapsProductEditInfo snapsProductEditInfo = null;

    private SnapsTemplate mainTemplate = null;

    private SmartRecommendBookTemplateHandleResultListener handleResultListener = null;

    public void loadTemplate(SmartRecommendBookTemplateHandleResultListener templateHandleResultListener) {
        this.handleResultListener = templateHandleResultListener;

        if(Config.getAI_IS_RECOMMENDAI()) {
            loadTemplateAfterDownloadTemplates();
        } else {

            if (Config.isFromCart()) {
                loadTemplateAfterDownloadTemplates();
            } else {
                mainTemplate = SnapsTemplateManager.getInstance().getSnapsTemplate();
                onCompleteLoadTemplate();
            }
        }
    }

    private void onCompleteLoadTemplate() {
        if (mainTemplate == null) {
            onFailLoadTemplate();
            return;
        }

        initEditInfoOnLoadTemplate();
        if (handleResultListener != null)
            handleResultListener.onLoadedTemplate();
    }

    private void onFailLoadTemplate() {
        if (handleResultListener != null)
            handleResultListener.onFailedGetTemplate();
    }

    private void processMatchingAIImageKey(SnapsTemplate snapsTemplate) {
        ArrayList<MyPhotoSelectImageData> list = new ArrayList<>();
        String faceInfo = ((SmartRecommendBookMainActivity)getActivity()).getFaceDetectionInfo();
        JSONArray jsonImgArray = null;

        Map<String, String> analysisMap = SmartSnapsUtil.recommendAIGetAnalysisKeyList(snapsTemplate);

        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> allPhoneList = ImageSelectManager.getInstance().getPhonePhotoFragmentDatas().getPhonePhotoData().getPhotoListByAlbumId(String.valueOf(ISnapsImageSelectConstants.PHONE_ALL_PHOTO_CURSOR_ID));

        Map<String, Integer> faceInfoMap = new HashMap<>();     // 빠른 검색을 위해 imageKey-index 맵을 만든다.

        try {
            JSONObject jsonFaceInfo = new JSONObject(faceInfo);
            if(jsonFaceInfo != null) {
                jsonImgArray = jsonFaceInfo.getJSONArray("images");
                if(jsonImgArray != null && jsonImgArray.length() != 0) {
                    for(int i = 0; i < jsonImgArray.length(); i++) {
                        faceInfoMap.put(Const_VALUES.SELECT_PHONE + "_" + jsonImgArray.getJSONObject(i).getString("imageKey"), i);
                    }
                }
            }

        } catch(Exception e) {
            Dlog.e(TAG, e);
        }

        String coverMapKey = null;
        if(!StringUtil.isEmpty(snapsTemplate.info.F_COVER_IMAGE_KEY))
            coverMapKey = Const_VALUES.SELECT_PHONE + "_" + snapsTemplate.info.F_COVER_IMAGE_KEY;

        for(GalleryCursorRecord.PhonePhotoFragmentItem photo : allPhoneList) {
            if(analysisMap.containsKey(photo.getImageKey())) {

                if(faceInfo != null) {
                    list.add(SmartSnapsUtil.recommendAIApplyFaceDetectionArea(photo, SmartSnapsUtil.recommendAIGetFaceInfoByImageKey(jsonImgArray, faceInfoMap, photo.getImageKey())));
                } else {
                    list.add(photo.getImgData());
                }
            }

            if(coverMapKey != null && coverMapKey.equalsIgnoreCase(photo.getImageKey())) {
                SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
                smartSnapsManager.appendCoverPhotoMapKey(coverMapKey);
            }

        }

        mainTemplate.myphotoImageList = list;

        DataTransManager dataTransManager = DataTransManager.getInstance();
        dataTransManager.setPhotoImageDataList(list);

    }


    private void loadTemplateAfterDownloadTemplates() {
        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            SmartRecommendBookAnalysisTaskChecker smartSnapsAnalysisTaskChecker = null;

            @Override
            public void onPre() {
                if (actExternalConnectionBridge != null)
                    actExternalConnectionBridge.showPageProgress();

                //커버랑 디자인 리스트가 다 받아졌는 지 체크하는 리스너.
                smartSnapsAnalysisTaskChecker = SmartRecommendBookAnalysisTaskChecker.createChecker(GET_COVER_TEMPLATE, GET_PAGE_TEMPLATE, GET_PAGE_BG_RES);
                smartSnapsAnalysisTaskChecker.setResultListener(new SnapsCommonResultListener<Boolean>() {
                    @Override
                    public void onResult(Boolean result) {
                        if (result) {
                            onCompleteLoadTemplate();
                        } else {
                            onFailLoadTemplate();
                        }
                    }
                });
            }

            @Override
            public void onBG() {
                if (snapsProductEditInfo != null) {
                    if(Config.getAI_IS_RECOMMENDAI()) {
                        mainTemplate = GetParsedXml.getRecommendProduct(SystemUtil.getDeviceId(getActivity()), SnapsLoginManager.getUUserNo(getActivity()), Config.getPROD_CODE(), Config.getPROJ_CODE(), Config.getAI_RECOMMENDREQ(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                        processMatchingAIImageKey(mainTemplate);
                        try {
                            SmartSnapsUtil.insertImageListToTemplateByAnalysisKey(getActivity(), mainTemplate, mainTemplate.myphotoImageList, true);
                            SmartSnapsUtil.fixImageLayerAreaOnTemplateBySmartSnapsInfo(getActivity(), mainTemplate);
                        } catch(Exception e) {
                            Dlog.e(TAG, e);
                        }

                    } else {
                        mainTemplate = GetTemplateLoad.getThemeBookTemplate(snapsProductEditInfo.getTemplateUrl(), snapsProductEditInfo.IS_EDIT_MODE(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                        //장바구니에서 편집한 경우 책등 제목을 타이틀로 다시 설정해준다
                        if (actExternalConnectionBridge != null)
                            ((SmartRecommendBookMainActivity)actExternalConnectionBridge.getActivity()).requestChangeTitleText();
                    }
                    SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
                    snapsTemplateManager.setSnapsTemplate(mainTemplate);
                }
            }

            @Override
            public void onPost() {
                if (actExternalConnectionBridge != null)
                    actExternalConnectionBridge.pageProgressUnload();

                if (mainTemplate != null && mainTemplate.info != null) {
                    loadRecommendPhotoBookInfoFromTemplate();

                    performTask(GET_COVER_TEMPLATE);
                    performTask(GET_PAGE_TEMPLATE);
                    performTask(GET_PAGE_BG_RES);
                } else {
                    onFailLoadTemplate();
                }
            }

            private void loadRecommendPhotoBookInfoFromTemplate() {
                if (actExternalConnectionBridge == null) return;
                SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();

                DataTransManager dataTransManager = DataTransManager.getInstance();
                dataTransManager.setPhotoImageDataList(mainTemplate.myphotoImageList);

                if(Config.getAI_IS_RECOMMENDAI()) {     // 추천AI에서는 onBG processMatchingAIImageKey()에서 이미 커버 이미지 처리
                    return;
                }

                ArrayList<SnapsPage> pages = getPageList();
                if (pages != null && !pages.isEmpty()) {
                    SnapsPage coverPage = pages.get(0);
                    if (coverPage != null) {
                        List<MyPhotoSelectImageData> imageDataList = coverPage.getImageDataListOnPage();
                        if (imageDataList != null && !imageDataList.isEmpty()) {
                            MyPhotoSelectImageData coverImage = imageDataList.get(0);
                            if (coverImage != null) {
                                smartSnapsManager.appendCoverPhotoMapKey(coverImage.getImageSelectMapKey());
                            }
                        }
                    }
                }
            }

            private void performTask(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType) {
                SmartSnapsAnalysisTaskImp projectCodeTask = SmartRecommendBookTaskFactory.createTask(getActivity(), smartSnapsAnalysisTaskChecker, taskType);
                if (projectCodeTask != null) {
                    projectCodeTask.perform();
                }
            }
        });
    }

    private Activity getActivity() {
        return actExternalConnectionBridge != null ? actExternalConnectionBridge.getActivity() : null;
    }

    public SnapsTemplate getMainTemplate() {
        return mainTemplate;
    }

    private void initEditInfoOnLoadTemplate() {
        SnapsAssert.assertNotNull(actExternalConnectionBridge);
        SnapsAssert.assertNotNull(getTemplate());
        SnapsAssert.assertNotNull(snapsProductEditInfo);

        fixRecommendCoverIndex();

        initLoadedTemplate();

        setTemplateBaseInfo();

        initSmartSnapsAnimationState();

        snapsProductEditInfo.setPageList(getTemplate().getPages());

        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        smartSnapsManager.setCompleteMakeRecommendBook(true);
    }

    private void fixRecommendCoverIndex() {
		SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();

        String currentCoverPageId = getCurrentCoverPageId();
        if (currentCoverPageId != null) {
            smartSnapsManager.fixRecommendCoverIndex(findCurrentCoverPageIndexOnCoverList(currentCoverPageId), getPageList());
        } else {
            smartSnapsManager.fixRecommendCoverIndex(0, getPageList());
        }
	}

    private int findCurrentCoverPageIndexOnCoverList(String currentCoverId) {
        if (!StringUtil.isEmpty(currentCoverId)) {
            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            ArrayList<SnapsPage> coverList = smartSnapsManager.getCoverPageListOfAnalysisPhotoBook();
            if (coverList != null) {
                for (int ii = 0; ii < coverList.size(); ii++) {
                    SnapsPage coverPage = coverList.get(ii);
                    if (coverPage == null) continue;
                    if (currentCoverId.equalsIgnoreCase(coverPage.multiformId)) return ii;
                }
            }
        }
        return 0;
    }

    private String getCurrentCoverPageId() {
        ArrayList<SnapsPage> currentPageList = getPageList();
        if (currentPageList != null && !currentPageList.isEmpty()) {
            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            if (smartSnapsManager.isContainCoverPageOnPageList(currentPageList)) {
                SnapsPage currentCoverPage = currentPageList.get(0);
                return currentCoverPage.multiformId;
            }
        }
        return null;
    }

    private void initLoadedTemplate() {
        try {
            getTemplate().clonePage();

            PhotobookCommonUtils.initPaperInfoOnLoadedTemplate(getTemplate());

            if (snapsProductEditInfo.IS_EDIT_MODE()) {
                PhotobookCommonUtils.imageRange2(getTemplate());
            }

            snapsProductEditInfo.initGalleryListFromDataTransManager(getActivity());

            getTemplate().info.F_ACTIVITY = "Product Code (" + Config.getPROD_CODE() + ")";
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void setTemplateBaseInfo() {
        SnapsAssert.assertNotNull(getTemplate());
        try {
            PhotobookCommonUtils.initBaseTemplateBaseInfo(getActivity(), getTemplate());

            PhotobookCommonUtils.initPaperInfoOnLoadedTemplate(getTemplate());

            PhotobookCommonUtils.refreshPagesId(getPageList());

            getTemplate().addQRcode(PhotobookCommonUtils.getPhotoBookQRCodeRect(getTemplate()));

            PhotobookCommonUtils.imageRange(getTemplate());
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
        }
    }

    private void initSmartSnapsAnimationState() {
        try {
            SmartSnapsManager.unlockAllSyncObjects();

            ArrayList<MyPhotoSelectImageData> imageList = getGalleryList();
            if (imageList == null) return;

            for (MyPhotoSelectImageData imageData : imageList) {
                if (imageData == null) continue;

                if (imageData.isFindSmartSnapsFaceArea()) {
                    SmartSnapsUtil.changeSmartSnapsImgStateWithImageData(imageData, SmartSnapsConstants.eSmartSnapsImgState.FINISH_ANIMATION);
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
        }
    }


    private SnapsTemplate getTemplate() {
        return actExternalConnectionBridge != null ? actExternalConnectionBridge.getTemplate() : null;
    }

    private ArrayList<SnapsPage> getPageList() {
        return actExternalConnectionBridge != null ? actExternalConnectionBridge.getPageList() : null;
    }

    private ArrayList<MyPhotoSelectImageData> getGalleryList() {
        return actExternalConnectionBridge != null ? actExternalConnectionBridge.getGalleryList() : null;
    }
}
