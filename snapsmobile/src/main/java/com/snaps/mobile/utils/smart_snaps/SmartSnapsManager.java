package com.snaps.mobile.utils.smart_snaps;

import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.SmartSnapsAnimationListener;
import com.snaps.common.data.smart_snaps.interfacies.ISmartSnapImgDataAnimationState;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.bean.XML_BasePage;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeCover;
import com.snaps.common.utils.net.xml.bean.Xml_ThemePage;
import com.snaps.common.utils.ui.CustomizeDialog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.common.data.SnapsPageEditRequestInfo;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.utils.smart_snaps.analysis.data.SmartRecommendBookLayoutData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import errorhandle.logger.Logg;

import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.IS_USE_SMART_SNAPS_FUNCTION;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.LIMIT_OF_PAGE_DESIGN_LIST_SIZE;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.MAX_SMART_SNAPS_PAGING_WAIT_TIME;

/**
 * Created by ysjeong on 2017. 9. 8..
 */

public class SmartSnapsManager {
    private static final String TAG = SmartSnapsManager.class.getSimpleName();
    private volatile static SmartSnapsManager instance = null;

    private SmartSnapsConstants.eSmartSnapsImageSelectType smartSnapsImageSelectType = SmartSnapsConstants.eSmartSnapsImageSelectType.NONE;

    private SparseArray<ISmartSnapImgDataAnimationState> smartAnimationViewTargetListeners = null;
    private SparseArray<ISmartSnapImgDataAnimationState> smartAnimationThumbViewTargetListeners = null;

    private SparseArray<List<MyPhotoSelectImageData>> smartSnapsImageControls = null;

    private Map<ViewGroup, ProgressBar> smartSnapsSearchingProgressMap = null;

    private SmartSnapsAnimationHandler smartSnapsAnimationHandler = null;

    private CustomizeDialog smartSearchingCancelConfirmDialog = null;

    private int smartSnapsTaskTotalCount = 0;
    private int smartSnapsFinishTaskCount = 0;

    private int smartSnapsImgUploadCompleteCount = 0;

    private boolean isSmartAreaSearching = false;
    private boolean isScreenRotationLock = false;
    private boolean isCompleteMakeRecommendBook = false;

    private Object smartSnapsAnimationImageListHandleSyncLocker = new Object();
    private AtomicBoolean isSmartSnapsAnimationImageListHandleSyncLock = new AtomicBoolean(false);

    private SparseArray<PageCanvasImageLoadSyncLocker> pageCanvasImageLoadSyncLockers = new SparseArray<>();

    private SmartRecommendBookLayoutData photoBookLayoutData = null;

    private ArrayList<MyPhotoSelectImageData> tempPhotoImageDataList = null;
    private ArrayList<SnapsPage> tempPageList = null;
    private ArrayList<SnapsPage> tempCoverPageList = null;
    private String tempCoverTitle = null;

    private ArrayList<MyPhotoSelectImageData> addedAllImageList = null;
    private boolean isShownImageSelectTutorial = false;

    public static boolean shouldSmartSnapsSearchingOnActivityCreate() {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct() || AutoSaveManager.isAutoSaveRecoveryMode()) {
            return false;
        }
        DataTransManager dataTransManager = DataTransManager.getInstance();
        if (dataTransManager != null) {
            SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
            SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
            if (snapsTemplate != null && snapsTemplate.getPages() != null && !snapsTemplate.getPages().isEmpty()) {
                ArrayList<MyPhotoSelectImageData> imageList = dataTransManager.getPhotoImageDataList();
                if (imageList != null && !imageList.isEmpty()) {
                    for (MyPhotoSelectImageData imageData : imageList) {
                        if (imageData != null && imageData.IMAGE_ID != 0 && imageData.isSmartSnapsSupport()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return false;
    }

    public static boolean isSupportSmartSnapsProduct() {
        return Config.useKorean() && !Config.isSimpleMakingBook() && !Config.isSnapsPhotoPrint() && !Config.isSNSBook() && !SnapsDiaryDataManager.isAliveSnapsDiaryService() && !Const_PRODUCT.isFreeSizeProduct();
    }

    public static boolean shouldSelectSmartSnapsTypeProduct() {
        return Config.isSimplePhotoBook();
    }

    public static void createInstance() {
        if (instance == null) {
            synchronized (SmartSnapsManager.class) {
                if (instance == null) {
                    instance = new SmartSnapsManager();
                }
            }
        }
    }

    public static void finalizeInstance() {
        if (instance != null) {
            try {
                Config.setAliveRecommendBookActivity(false);

                clearAllInstanceInfo();

                if (instance.photoBookLayoutData != null) {
                    instance.photoBookLayoutData.releaseInstance();
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }

            instance = null;
        }
    }

    public static void clearAllInstanceInfo() {
        if (instance != null) {
            instance.isSmartAreaSearching = false;
            instance.smartSnapsImageSelectType = SmartSnapsConstants.eSmartSnapsImageSelectType.NONE;

            try {
                instance.initSmartSnapsTargets();

                if (instance.smartSnapsAnimationHandler != null) {
                    instance.smartSnapsAnimationHandler.suspendTasks();
                    instance.smartSnapsAnimationHandler = null;
                }

                if (instance.photoBookLayoutData != null) {
                    instance.photoBookLayoutData.clearTemplateInfo();
                }

                if (instance.tempPhotoImageDataList != null) {
                    if (!instance.tempPhotoImageDataList.isEmpty()) {
                        instance.tempPhotoImageDataList.clear();
                    }
                    instance.tempPhotoImageDataList = null;
                }

                if (instance.tempPageList != null) {
                    if (!instance.tempPageList.isEmpty()) {
                        instance.tempPageList.clear();
                    }
                    instance.tempPageList = null;
                }

                if (instance.tempCoverPageList != null) {
                    if (!instance.tempCoverPageList.isEmpty()) {
                        instance.tempCoverPageList.clear();
                    }
                    instance.tempCoverPageList = null;
                }

                if (!StringUtil.isEmpty(instance.tempCoverTitle)) {
                    instance.tempCoverTitle = null;
                }

                if (instance.addedAllImageList != null) {
                    if (!instance.addedAllImageList.isEmpty()) {
                        instance.addedAllImageList.clear();
                    }
                    instance.addedAllImageList = null;
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    public static void unlockAllSyncObjects() {
        try {
            SmartSnapsManager smartSnapsManager = getInstance();
            smartSnapsManager.notifySmartSnapsAnimationImageListHandling();

            if (smartSnapsManager.smartSnapsAnimationHandler != null) {
                SmartSnapsAnimationHandler animationHandler = smartSnapsManager.smartSnapsAnimationHandler;
                animationHandler.suspendTasks();
            }

            smartSnapsManager.clearPageCanvasImageLoadSyncLockers();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static SmartSnapsManager getInstance() {
        if (instance == null) {
            createInstance();
        }
        return instance;
    }

    private SmartSnapsManager() {
        smartAnimationViewTargetListeners = new SparseArray<>();
        smartAnimationThumbViewTargetListeners = new SparseArray<>();
        smartSnapsImageControls = new SparseArray<>();
        smartSnapsSearchingProgressMap = new HashMap<>();
        photoBookLayoutData = new SmartRecommendBookLayoutData();
        tempPhotoImageDataList = new ArrayList<>();
        addedAllImageList = new ArrayList<>();
    }

    public String getTempCoverTitle() {
        return tempCoverTitle;
    }

    public void setTempCoverTitle(String tempCoverTitle) {
        this.tempCoverTitle = tempCoverTitle;
    }

    public void fixRecommendCoverIndex(int coverIndex, ArrayList<SnapsPage> pageList) {
        ArrayList<SnapsPage> coverList = getCoverPageListOfAnalysisPhotoBook();
        if (coverList != null && coverList.size() > coverIndex) {
            SnapsPage recommendedCoverPage = coverList.remove(coverIndex);
            coverList.add(0, recommendedCoverPage);

            if (pageList != null && !pageList.isEmpty()) {
                if (isContainCoverPageOnPageList(pageList)) {
                    pageList.remove(0);
                }
                pageList.add(0, recommendedCoverPage);
            }
        }
    }

    public boolean isContainCoverPageOnPageList(ArrayList<SnapsPage> pageList) {
        if (pageList == null || pageList.isEmpty()) {
            return false;
        }
        SnapsPage snapsPage = pageList.get(0);
        return snapsPage != null && snapsPage.type != null && snapsPage.type.equalsIgnoreCase("cover");
    }

    private void initImageDataSortPriority() {
        synchronized (getAllAddedImageList()) {
            for (MyPhotoSelectImageData addedImage : addedAllImageList) {
                if (addedImage != null) {
                    addedImage.setSortPriority(0);
                }
            }
        }
    }

    //선택한 사진을 더하되 중복 선택된 사진은 앞쪽으로 Sorting 한다...
    public void appendAddedAllImageListAndSortingDuplicatedPhoto(ArrayList<MyPhotoSelectImageData> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        synchronized (getAllAddedImageList()) {

            initImageDataSortPriority();

            for (MyPhotoSelectImageData newImage : list) {
                if (newImage == null) {
                    return;
                }
                boolean isContained = false;
                for (MyPhotoSelectImageData addedImage : addedAllImageList) {
                    if (addedImage != null && addedImage.isSameImage(newImage)) {
                        addedImage.setSortPriority(1);
                        isContained = true;
                        break;
                    }
                }

                if (!isContained) {
                    addedAllImageList.add(0, newImage);
                }
            }

            Collections.sort(addedAllImageList, new Comparator<MyPhotoSelectImageData>() {
                @Override
                public int compare(MyPhotoSelectImageData lhs, MyPhotoSelectImageData rhs) {
                    return lhs.getSortPriority() > rhs.getSortPriority() ? -1 : (lhs.getSortPriority() < rhs.getSortPriority() ? 1 : 0);
                }
            });
        }
    }

    public void copyAddedAllImageList(ArrayList<MyPhotoSelectImageData> list) {
        synchronized (getAllAddedImageList()) {
            if (addedAllImageList != null && !addedAllImageList.isEmpty()) {
                addedAllImageList.clear();
            }

            if (list != null && !list.isEmpty()) {
                for (MyPhotoSelectImageData imgData : list) {
                    MyPhotoSelectImageData copyData = new MyPhotoSelectImageData();
                    copyData.set(imgData);
                    addedAllImageList.add(copyData);
                }
            }
        }
    }

    public ArrayList<MyPhotoSelectImageData> getAllAddedImageList() {
        if (addedAllImageList == null) {
            addedAllImageList = new ArrayList<>();
        }
        return addedAllImageList;
    }

    public void copyTempPhotoImageDataList(ArrayList<MyPhotoSelectImageData> list) {
        synchronized (getTempPhotoImageDataList()) {
            if (tempPhotoImageDataList != null && !tempPhotoImageDataList.isEmpty()) {
                tempPhotoImageDataList.clear();
            }

            if (list != null && !list.isEmpty()) {
                for (MyPhotoSelectImageData imgData : list) {
                    MyPhotoSelectImageData copyData = new MyPhotoSelectImageData();
                    copyData.set(imgData);
                    tempPhotoImageDataList.add(copyData);
                }
            }
        }
    }

    public void recoveryPrevImageList(ArrayList<MyPhotoSelectImageData> prevImageList) {
        synchronized (getAllAddedImageList()) {
            if (addedAllImageList != null && !addedAllImageList.isEmpty()) {
                if (prevImageList != null && !prevImageList.isEmpty()) {
                    for (MyPhotoSelectImageData prevImageData : prevImageList) {
                        for (MyPhotoSelectImageData currentImageData : addedAllImageList) {
                            if (currentImageData.IMAGE_ID == prevImageData.IMAGE_ID) {
                                currentImageData.set(prevImageData);
                            }
                        }
                    }
                }
            }
        }
    }

    public ArrayList<MyPhotoSelectImageData> getTempPhotoImageDataList() {
        if (tempPhotoImageDataList == null) {
            tempPhotoImageDataList = new ArrayList<>();
        }
        return tempPhotoImageDataList;
    }

    public ArrayList<SnapsPage> getTempPageList() {
        if (tempPageList == null) {
            tempPageList = new ArrayList<>();
        }
        return tempPageList;
    }

    public void copyTempPageList(ArrayList<SnapsPage> orgList) {
        synchronized (getTempPageList()) {
            if (tempPageList != null && !tempPageList.isEmpty()) {
                tempPageList.clear();
            }

            if (orgList != null && !orgList.isEmpty()) {
                for (SnapsPage snapsPage : orgList) {
                    SnapsPage copiedPage = snapsPage.copyPage(snapsPage.getPageID(), true);
                    tempPageList.add(copiedPage);
                }
            }
        }
    }

    public ArrayList<SnapsPage> getTempCoverPageList() {
        if (tempCoverPageList == null) {
            tempCoverPageList = new ArrayList<>();
        }
        return tempCoverPageList;
    }

    public void copyTempCoverPageList(ArrayList<SnapsPage> orgList) {
        synchronized (getTempCoverPageList()) {
            if (tempCoverPageList != null && !tempCoverPageList.isEmpty()) {
                tempCoverPageList.clear();
            }

            if (orgList != null && !orgList.isEmpty()) {
                for (SnapsPage snapsPage : orgList) {
                    SnapsPage copiedPage = snapsPage.copyPage(snapsPage.getPageID(), true);
                    tempCoverPageList.add(copiedPage);
                }
            }
        }
    }

    public static boolean isSmartImageSelectType() {
        return getInstance().getSmartSnapsImageSelectType() == SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_CHOICE;
    }

    public static boolean isSmartAreaSearching() {
        return IS_USE_SMART_SNAPS_FUNCTION && SmartSnapsManager.isSupportSmartSnapsProduct() && getInstance().isSmartAreaSearching;
    }

    public static boolean isFirstSmartAreaSearching() {
        return IS_USE_SMART_SNAPS_FUNCTION && SmartSnapsManager.isSupportSmartSnapsProduct() && getInstance().isSmartAreaSearching && getInstance().isFirstSmartSearching();
    }

    public static void setSmartAreaSearching(boolean smartAreaSearching) {
        getInstance().isSmartAreaSearching = smartAreaSearching;
    }

    public static void suspendSmartSnapsFaceSearching() throws Exception {
        setSmartAreaSearching(false);
        SmartSnapsAnimationHandler snapsAnimationHandler = getInstance().getSmartSnapsAnimationHandler();
        if (snapsAnimationHandler != null) {
            snapsAnimationHandler.suspendTasks();
        }

        getInstance().initSmartSnapsTargets();
    }

    public void initSmartSnapsTargets() throws Exception {
        if (instance != null) {
            instance.isSmartAreaSearching = false;
            if (instance.smartAnimationViewTargetListeners != null) {
                instance.smartAnimationViewTargetListeners.clear();
            }

            if (instance.smartAnimationThumbViewTargetListeners != null) {
                instance.smartAnimationThumbViewTargetListeners.clear();
            }

            if (instance.smartSnapsImageControls != null) {
                instance.smartSnapsImageControls.clear();
            }

            if (instance.smartSnapsSearchingProgressMap != null) {
                instance.smartSnapsSearchingProgressMap.clear();
            }

            if (instance.pageCanvasImageLoadSyncLockers != null) {
                instance.pageCanvasImageLoadSyncLockers.clear();
            }

            instance.smartSnapsTaskTotalCount = 0;
            instance.smartSnapsFinishTaskCount = 0;
        }
    }

    public synchronized static void startSmartSnapsAutoFitImage(SmartSnapsAnimationListener smartSnapsAnimationListener, SmartSnapsConstants.eSmartSnapsProgressType progressType, int startPageIndex) throws Exception {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct()) {
            return;
        }

        SmartSnapsManager smartSnapsManager = getInstance();
        SmartSnapsAnimationHandler smartSnapsAnimationHandler = SmartSnapsAnimationHandler.createSmartSnapsAnimationHandler(smartSnapsAnimationListener, progressType);
        smartSnapsManager.setSmartSnapsAnimationHandler(smartSnapsAnimationHandler);
        smartSnapsManager.initProgress(progressType);

        smartSnapsAnimationHandler.startSmartSnapsAutoFitImage(startPageIndex);
    }

    public void removeAllSmartSnapsSearchingProgress() throws Exception {
        if (smartSnapsSearchingProgressMap == null) {
            return;
        }
        Set<ViewGroup> viewGroupSet = smartSnapsSearchingProgressMap.keySet();
        if (viewGroupSet.isEmpty()) {
            return;
        }

        for (ViewGroup viewGroup : viewGroupSet) {
            if (viewGroup == null) {
                continue;
            }
            ProgressBar progressBar = smartSnapsSearchingProgressMap.get(viewGroup);
            if (progressBar == null) {
                continue;
            }
            viewGroup.removeView(progressBar);
        }

        smartSnapsSearchingProgressMap.clear();
    }

    public void putSmartSnapsSearchingProgress(ViewGroup viewGroup, ProgressBar progressBar) {
        if (smartSnapsSearchingProgressMap != null) {
            smartSnapsSearchingProgressMap.put(viewGroup, progressBar);
        }
    }

    public SmartSnapsConstants.eSmartSnapsImageSelectType getSmartSnapsImageSelectType() {
        return smartSnapsImageSelectType;
    }

    public void setSmartSnapsImageSelectType(SmartSnapsConstants.eSmartSnapsImageSelectType smartSnapsImageSelectType) {
        this.smartSnapsImageSelectType = smartSnapsImageSelectType;
    }

    public void addSmartAnimationViewTargetListener(MyPhotoSelectImageData imageData, ISmartSnapImgDataAnimationState listener) {
        if (this.smartAnimationViewTargetListeners == null) {
            return;
        }

        this.smartAnimationViewTargetListeners.put(imageData.IMG_IDX, listener);
    }

    public ISmartSnapImgDataAnimationState getSmartImgAnimationListener(MyPhotoSelectImageData imageData) {
        if (this.smartAnimationViewTargetListeners == null) {
            return null;
        }
        return this.smartAnimationViewTargetListeners.get(imageData.IMG_IDX);
    }

    public void addSmartAnimationThumbViewTargetListener(MyPhotoSelectImageData imageData, ISmartSnapImgDataAnimationState listener) {
        if (this.smartAnimationThumbViewTargetListeners == null) {
            return;
        }

        this.smartAnimationThumbViewTargetListeners.put(imageData.IMG_IDX, listener);
    }

    public ISmartSnapImgDataAnimationState getSmartImgAnimationThumbViewListener(MyPhotoSelectImageData imageData) {
        if (this.smartAnimationThumbViewTargetListeners == null) {
            return null;
        }
        return this.smartAnimationThumbViewTargetListeners.get(imageData.IMG_IDX);
    }

    public SparseArray<List<MyPhotoSelectImageData>> getSmartSnapsImageControls() {
        return smartSnapsImageControls;
    }

    public SmartSnapsAnimationHandler getSmartSnapsAnimationHandler() {
        return smartSnapsAnimationHandler;
    }

    public void initProgress(SmartSnapsConstants.eSmartSnapsProgressType progressType) {
        switch (progressType) {
            case FIST_LOAD:
                setSmartSnapsTaskTotalCount();
                break;
            default:
                break;
        }
    }

    public void setSmartSnapsAnimationHandler(SmartSnapsAnimationHandler smartSnapsAnimationHandler) {
        this.smartSnapsAnimationHandler = smartSnapsAnimationHandler;
    }

    public SmartSnapsConstants.eSmartSnapsProgressType getSmartSnapsProgressType() {
        return getSmartSnapsAnimationHandler() != null ? getSmartSnapsAnimationHandler().getProgressType() : null;
    }

    public boolean isFirstSmartSearching() {
        return getSmartSnapsProgressType() != null && getSmartSnapsProgressType() == SmartSnapsConstants.eSmartSnapsProgressType.FIST_LOAD;
    }

    public int getSmartSnapsTaskTotalCount() {
        return smartSnapsTaskTotalCount;
    }

    public void setSmartSnapsTaskTotalCount() {
        this.smartSnapsTaskTotalCount = getSmartSnapsImageDataCount();
        this.smartSnapsFinishTaskCount = 0;
    }

    public int getSmartSnapsFinishTaskCount() {
        return smartSnapsFinishTaskCount;
    }

    public void increaseSmartSnapsFinishTaskCount() {
        smartSnapsFinishTaskCount = Math.min(smartSnapsFinishTaskCount + 1, getSmartSnapsTaskTotalCount());
    }

    private int getSmartSnapsImageDataCount() {
        if (getSmartSnapsImageControls() == null) {
            return 0;
        }
        try {
            synchronized (getSmartSnapsImageControls()) {
                int result = 0;
                for (int ii = 0; ii < getSmartSnapsImageControls().size(); ii++) {
                    int index = getSmartSnapsImageControls().keyAt(ii);
                    List<MyPhotoSelectImageData> imageDataList = getSmartSnapsImageControls().get(index);
                    result += imageDataList.size();
                }

                return result;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return 0;
        }
    }

    public void setSmartSnapsImgInfoOnAllImageDataInTemplate(SnapsTemplate template) throws Exception {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct() || template == null || template.getPages() == null) {
            return;
        }

        SparseArray<List<MyPhotoSelectImageData>> imageControlMap = getSmartSnapsImageControls();
        List<MyPhotoSelectImageData> imgDataList = null;

        for (int pageIndex = 0; pageIndex < template.getPages().size(); pageIndex++) {
            SnapsPage page = template.getPages().get(pageIndex);

            imgDataList = new LinkedList<>();
            imageControlMap.put(pageIndex, imgDataList);
            for (int controlIndex = 0; controlIndex < page.getLayoutList().size(); controlIndex++) {
                SnapsLayoutControl layout = (SnapsLayoutControl) page.getLayoutList().get(controlIndex);
                if (layout.type.equalsIgnoreCase("browse_file")) {
                    MyPhotoSelectImageData imgData = layout.imgData;
                    if (imgData != null && imgData.isSmartSnapsSupport()) {
                        SmartSnapsUtil.setSmartSnapsImgInfoOnImageData(imgData, pageIndex);

                        imgDataList.add(imgData);
                    }
                }
            }
        }
    }

    public void setSingleSmartSnapsImageData(final MyPhotoSelectImageData imageData, int pageIndex) throws Exception {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct()) {
            return;
        }

        if (imageData == null || !imageData.isSmartSnapsSupport()) {
            return;
        }

        SmartSnapsUtil.setSmartSnapsImgInfoOnImageData(imageData, pageIndex);

        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        SparseArray<List<MyPhotoSelectImageData>> imageControlMap = smartSnapsManager.getSmartSnapsImageControls();
        if (imageControlMap != null) {
            imageControlMap.put(pageIndex, new LinkedList<MyPhotoSelectImageData>() {{
                add(imageData);
            }});
        }
    }

    public void setSmartSnapsAnimationReadyState(final MyPhotoSelectImageData imageData, int pageIndex) throws Exception {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct()) {
            return;
        }

        if (imageData == null || !imageData.isSmartSnapsSupport()) {
            return;
        }

        SmartSnapsUtil.changeSmartSnapsImgStateWithImageData(imageData, SmartSnapsConstants.eSmartSnapsImgState.READY);

        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        SparseArray<List<MyPhotoSelectImageData>> imageControlMap = smartSnapsManager.getSmartSnapsImageControls();
        if (imageControlMap != null) {
            imageControlMap.put(pageIndex, new LinkedList<MyPhotoSelectImageData>() {{
                add(imageData);
            }});
        }
    }

    public void handleSmartSnapsAnimationOnPage(int page) throws Exception {
        if (!isFirstSmartSearching()) {
            return;
        }

        SmartSnapsAnimationHandler smartSnapsAnimationHandler = getSmartSnapsAnimationHandler();
        if (smartSnapsAnimationHandler != null) {
            smartSnapsAnimationHandler.handleSmartSnapsAnimationOnPage(page);
        }
    }

    public void removeUploadReadyImageData(MyPhotoSelectImageData imageData) {
        SmartSnapsAnimationHandler smartSnapsAnimationHandler = getSmartSnapsAnimationHandler();
        if (smartSnapsAnimationHandler != null) {
            smartSnapsAnimationHandler.removeUploadReadyImageData(imageData);
        }
    }

    public List<MyPhotoSelectImageData> createSmartSnapsImageListWithPageIdx(int pageIdx) throws Exception {
        SparseArray<List<MyPhotoSelectImageData>> imageControlMap = getSmartSnapsImageControls();
        if (imageControlMap == null) {
            return new LinkedList<>();
        }
        List<MyPhotoSelectImageData> imgDataList = new LinkedList<>();
        imageControlMap.put(pageIdx, imgDataList);
        return imgDataList;
    }

    public void requestSmartImgAnimation(MyPhotoSelectImageData imageData) throws Exception {
        SmartSnapsManager snapsManager = SmartSnapsManager.getInstance();
        ISmartSnapImgDataAnimationState listener = snapsManager.getSmartImgAnimationListener(imageData);
        if (listener != null) {
            listener.onRequestedAnimation();
        }
    }

    public void requestSmartThumbImgAnimation(MyPhotoSelectImageData imageData) throws Exception {
        SmartSnapsManager snapsManager = SmartSnapsManager.getInstance();
        ISmartSnapImgDataAnimationState listener = snapsManager.getSmartImgAnimationThumbViewListener(imageData);
        if (listener != null) {
            listener.onRequestedAnimation();
        }
    }

    public int getSmartSnapsImgUploadCompleteCount() {
        return smartSnapsImgUploadCompleteCount;
    }

    public void setSmartSnapsImgUploadCompleteCount(int smartSnapsImgUploadCompleteCount) {
        this.smartSnapsImgUploadCompleteCount = smartSnapsImgUploadCompleteCount;
    }

    public void increaseSmartSnapsImgUploadCompleteCount() {
        this.smartSnapsImgUploadCompleteCount++;
    }

    public void dismissSmartSearchingCancelConfirmDialog() {
        if (smartSearchingCancelConfirmDialog == null || !smartSearchingCancelConfirmDialog.isShowing()) {
            return;
        }
        try {
            smartSearchingCancelConfirmDialog.cancel();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void setSmartSearchingCancelConfirmDialog(CustomizeDialog smartSearchingCancelConfirmDialog) {
        this.smartSearchingCancelConfirmDialog = smartSearchingCancelConfirmDialog;
    }

    public boolean isScreenRotationLock() {
        return isScreenRotationLock;
    }

    public void setScreenRotationLock(boolean screenRotationLock) {
        isScreenRotationLock = screenRotationLock;
    }

    public void waitIfSmartSnapsAnimationImageListHandling() {
        if (isSmartSnapsAnimationImageListHandleSyncLock()) {
            synchronized (getSmartSnapsAnimationImageListHandleSyncLocker()) {
                if (isSmartSnapsAnimationImageListHandleSyncLock()) {
                    try {
                        getSmartSnapsAnimationImageListHandleSyncLocker().wait(5000);
                    } catch (InterruptedException e) {
                        Dlog.e(TAG, e);
                    }
                }
            }
        }
    }

    public void lockSmartSnapsAnimationImageListHandling() {
        isSmartSnapsAnimationImageListHandleSyncLock.set(true);
    }

    public void notifySmartSnapsAnimationImageListHandling() {
        try {
            if (isSmartSnapsAnimationImageListHandleSyncLock()) {
                isSmartSnapsAnimationImageListHandleSyncLock.set(false);
                synchronized (getSmartSnapsAnimationImageListHandleSyncLocker()) {
                    getSmartSnapsAnimationImageListHandleSyncLocker().notify();
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public Object getSmartSnapsAnimationImageListHandleSyncLocker() {
        return smartSnapsAnimationImageListHandleSyncLocker;
    }

    public boolean isSmartSnapsAnimationImageListHandleSyncLock() {
        return isSmartSnapsAnimationImageListHandleSyncLock.get();
    }

    public void waitIfPageCanvasImageLoadSyncLocker(int page) {
        try {
            if (pageCanvasImageLoadSyncLockers == null) {
                return;
            }

            PageCanvasImageLoadSyncLocker loadSyncLocker = pageCanvasImageLoadSyncLockers.get(page);
            if (loadSyncLocker == null) {
                return;
            }

            if (loadSyncLocker.isPageCanvasImageLoadSyncLock()) {
                synchronized (loadSyncLocker.getPageCanvasImageLoadSyncLocker()) {
                    if (loadSyncLocker.isPageCanvasImageLoadSyncLock()) {
                        try {
                            loadSyncLocker.getPageCanvasImageLoadSyncLocker().wait(MAX_SMART_SNAPS_PAGING_WAIT_TIME);
                        } catch (InterruptedException e) {
                            Dlog.e(TAG, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void lockPageCanvasImageLoadSyncLock(int page) {
        pageCanvasImageLoadSyncLockers.put(page, PageCanvasImageLoadSyncLocker.createNewInstance());
    }

    public void clearPageCanvasImageLoadSyncLockers() {
        if (pageCanvasImageLoadSyncLockers == null) {
            return;
        }
        pageCanvasImageLoadSyncLockers.clear();
    }

    public void notifyPageCanvasImageLoadSyncLocker(int page) {
        try {
            if (pageCanvasImageLoadSyncLockers == null) {
                return;
            }

            PageCanvasImageLoadSyncLocker loadSyncLocker = pageCanvasImageLoadSyncLockers.get(page);
            if (loadSyncLocker == null) {
                return;
            }

            if (loadSyncLocker.isPageCanvasImageLoadSyncLock()) {
                loadSyncLocker.unLockPageCanvasImageLoadSyncLock();
                synchronized (loadSyncLocker.getPageCanvasImageLoadSyncLocker()) {
                    loadSyncLocker.getPageCanvasImageLoadSyncLocker().notify();
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public boolean isExistCoverDataOfAnalysisPhotoBook() {
        return getCoverPageListOfAnalysisPhotoBook() != null && !getCoverPageListOfAnalysisPhotoBook().isEmpty();
    }

    public boolean isExistCoverDesignListOfAnalysisPhotoBook() {
        return getCoverDesignListOfAnalysisPhotoBook() != null && getCoverDesignListOfAnalysisPhotoBook().bgList != null && !getCoverDesignListOfAnalysisPhotoBook().bgList.isEmpty();
    }

    public boolean isExistTitlePageDesignListOfAnalysisPhotoBook() {
        return getIndexDesignListOfAnalysisPhotoBook() != null && !getIndexDesignListOfAnalysisPhotoBook().isEmpty();
    }

    public boolean isExistInnerPageDesignListOfAnalysisPhotoBook() {
        return getPageDesignListOfAnalysisPhotoBook() != null && getPageDesignListOfAnalysisPhotoBook().bgList != null && !getPageDesignListOfAnalysisPhotoBook().bgList.isEmpty();
    }

    public boolean isExistPageBGResListOfAnalysisPhotoBook() {
        return getPageBGResListOfAnalysisPhotoBook() != null && getPageBGResListOfAnalysisPhotoBook().bgList != null && !getPageBGResListOfAnalysisPhotoBook().bgList.isEmpty();
    }

    public void setCoverTemplateOfAnalysisPhotoBook(SnapsTemplate coverTemplate) {
        if (photoBookLayoutData == null) {
            photoBookLayoutData = new SmartRecommendBookLayoutData();
        }
        photoBookLayoutData.setCoverTemplate(coverTemplate);
    }

    public void setCoverDesignListOfAnalysisPhotoBook(Xml_ThemeCover cover) {
        if (photoBookLayoutData == null) {
            photoBookLayoutData = new SmartRecommendBookLayoutData();
        }
        photoBookLayoutData.setCoverDesignList(cover);
    }

    public Xml_ThemeCover getCoverDesignListOfAnalysisPhotoBook() {
        return photoBookLayoutData != null ? photoBookLayoutData.getCoverDesignList() : null;
    }

    private Xml_ThemePage getPageBGResListOfAnalysisPhotoBook() {
        return photoBookLayoutData != null ? photoBookLayoutData.getPageBGResList() : null;
    }

    public void setIndexDesignListOfAnalysisPhotoBook(Xml_ThemePage page) {
        if (photoBookLayoutData == null) {
            photoBookLayoutData = new SmartRecommendBookLayoutData();
        }
        photoBookLayoutData.setIndexDesignList(page);
    }

    public ArrayList<XML_BasePage> getIndexDesignListOfAnalysisPhotoBook() {
        if (photoBookLayoutData == null) {
            return null;
        }

        Xml_ThemePage indexPageXml = photoBookLayoutData.getIndexDesignList();
        if (indexPageXml == null) {
            return null;
        }

        Xml_ThemePage.ThemePage recommendedPage = null;
        Xml_ThemePage.ThemePage selectedPage = null;

        String recommendMultiformId = SmartSnapsUtil.removeDesignIdPrefix(getOrgPageMultiformIdWithPageIndex(1));
        if (!StringUtil.isEmpty(recommendMultiformId)) {
            recommendedPage = findDesignFromRecommendTemplateWithMultiformId(indexPageXml.bgList, recommendMultiformId);
        }

        String selectedMultiformId = SmartSnapsUtil.removeDesignIdPrefix(getSelectedPageMultiformIdWithPageIndex(1));
        if (!StringUtil.isEmpty(recommendMultiformId)
                && !recommendMultiformId.equalsIgnoreCase(selectedMultiformId)
                && !StringUtil.isEmpty(selectedMultiformId)) {
            selectedPage = findDesignFromRecommendTemplateWithMultiformId(indexPageXml.bgList, selectedMultiformId);
        }

        ArrayList<XML_BasePage> indexPageLayoutDesignList = new ArrayList<>();
        insertPageLayoutDesignList(indexPageXml.bgList, indexPageLayoutDesignList, recommendedPage, selectedPage);
        return indexPageLayoutDesignList;
    }

    private ArrayList<Xml_ThemePage.ThemePage> getBgListWithTargetMaskCnt(ArrayList<Xml_ThemePage.ThemePage> bgList, int targetCnt) {
        if (bgList == null) {
            return null;
        }

        ArrayList<Xml_ThemePage.ThemePage> result = new ArrayList<>();
        for (Xml_ThemePage.ThemePage p : bgList) {
            if (p == null) {
                continue;
            }

            if (!StringUtil.isEmpty(p.F_MASK_CNT)) {
                int maskCnt = Integer.parseInt(p.F_MASK_CNT);
                if (maskCnt == targetCnt) {
                    result.add(p);
                }
            }
        }
        return result;
    }

    private void insertPageLayoutDesignList(ArrayList<Xml_ThemePage.ThemePage> bgList, ArrayList<XML_BasePage> pageLayoutDesignList, Xml_ThemePage.ThemePage recommendedPage, Xml_ThemePage.ThemePage selectedPage) {
        if (bgList == null || pageLayoutDesignList == null) {
            return;
        }

        if (recommendedPage != null) {
            pageLayoutDesignList.add(recommendedPage);
        }

        if (selectedPage != null && recommendedPage != selectedPage) {
            pageLayoutDesignList.add(selectedPage);
        }

        for (XML_BasePage page : bgList) {
            if (page == null || page == recommendedPage || page == selectedPage) {
                continue;
            }
            page.F_IS_SELECT = false;
            pageLayoutDesignList.add(page);
            if (pageLayoutDesignList.size() >= LIMIT_OF_PAGE_DESIGN_LIST_SIZE) {
                break;
            }
        }
    }

    private Xml_ThemePage.ThemePage findDesignFromRecommendTemplateWithMultiformId(ArrayList<Xml_ThemePage.ThemePage> bgList, String multiformId) {
        if (bgList == null || StringUtil.isEmpty(multiformId)) {
            return null;
        }

        for (Xml_ThemePage.ThemePage page : bgList) {
            if (page == null) {
                continue;
            }
            if (multiformId.equalsIgnoreCase(SmartSnapsUtil.removeDesignIdPrefix(page.F_TMPL_ID))) {
                page.F_IS_SELECT = false;
                page.F_IS_BASE_MULTIFORM = false;
                return page;
            }
        }
        return null;
    }

    private String getSelectedPageMultiformIdWithPageIndex(int index) {
        ArrayList<SnapsPage> pageList = getRecommendPageList();
        if (pageList == null || pageList.size() <= index) {
            return null;
        }
        SnapsPage snapsPage = pageList.get(index);
        if (snapsPage == null) {
            return null;
        }
        return SmartSnapsUtil.removeDesignIdPrefix(snapsPage.multiformId);
    }

    private String getOrgPageMultiformIdWithPageIndex(int index) {
        ArrayList<SnapsPage> pageList = getRecommendPageList();
        if (pageList == null || pageList.size() <= index) {
            return null;
        }
        SnapsPage snapsPage = pageList.get(index);
        if (snapsPage == null) {
            return null;
        }
        return SmartSnapsUtil.removeDesignIdPrefix(snapsPage.orgMultiformId);
    }

    private ArrayList<SnapsPage> getRecommendPageList() {
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
        if (snapsTemplate == null) {
            return null;
        }
        return snapsTemplate.getPages();
    }

    public void setPageDesignListOfAnalysisPhotoBook(Xml_ThemePage page) {
        if (photoBookLayoutData == null) {
            photoBookLayoutData = new SmartRecommendBookLayoutData();
        }
        photoBookLayoutData.setPageDesignList(page);
    }

    public void setPageBGResListOfAnalysisPhotoBook(Xml_ThemePage page) {
        if (photoBookLayoutData == null) {
            photoBookLayoutData = new SmartRecommendBookLayoutData();
        }
        photoBookLayoutData.setPageBGResList(page);
    }

    public void deleteTempEditInfo() {
        if (tempPhotoImageDataList != null) {
            tempPhotoImageDataList.clear();
            tempPhotoImageDataList = null;
        }

        if (tempPageList != null) {
            tempPageList.clear();
            tempPageList = null;
        }

        if (tempCoverPageList != null) {
            tempCoverPageList.clear();
            tempCoverPageList = null;
        }
    }

    public Xml_ThemePage getPageDesignListOfAnalysisPhotoBook() {
        return photoBookLayoutData.getPageDesignList();
    }

    public ArrayList<XML_BasePage> getPageDesignListOfAnalysisPhotoBook(SnapsPageEditRequestInfo requestInfo) {
        if (photoBookLayoutData == null || requestInfo == null) {
            return null;
        }

        int pageIndex = requestInfo.getPageIndex();
        int maskCnt = requestInfo.getMaskCount();

        Xml_ThemePage innerPageXml = photoBookLayoutData.getPageDesignList();
        if (innerPageXml == null) {
            return null;
        }

        Xml_ThemePage.ThemePage recommendedPage = null;
        Xml_ThemePage.ThemePage selectedPage = null;

        String recommendMultiformId = SmartSnapsUtil.removeDesignIdPrefix(getOrgPageMultiformIdWithPageIndex(pageIndex));
        if (!StringUtil.isEmpty(recommendMultiformId)) {
            recommendedPage = findDesignFromRecommendTemplateWithMultiformId(innerPageXml.bgList, recommendMultiformId);
        }

        String selectedMultiformId = SmartSnapsUtil.removeDesignIdPrefix(getSelectedPageMultiformIdWithPageIndex(pageIndex));
        if (!StringUtil.isEmpty(selectedMultiformId)) {
            selectedPage = findDesignFromRecommendTemplateWithMultiformId(innerPageXml.bgList, selectedMultiformId);
        }

        ArrayList<Xml_ThemePage.ThemePage> bgList = getBgListWithTargetMaskCnt(innerPageXml.bgList, maskCnt);

        ArrayList<XML_BasePage> innerPageLayoutDesignList = new ArrayList<>();
        insertPageLayoutDesignList(bgList, innerPageLayoutDesignList, recommendedPage, selectedPage);
        return innerPageLayoutDesignList;
    }

    public ArrayList<XML_BasePage> getPagBGResListOfAnalysisPhotoBook() {
        Xml_ThemePage bgXML = getPageBGResListOfAnalysisPhotoBook();
        if (bgXML == null || bgXML.bgList == null) {
            return null;
        }

        ArrayList<XML_BasePage> pageBgList = new ArrayList<>();
        for (XML_BasePage page : bgXML.bgList) {
            if (page == null) {
                continue;
            }
            page.F_IS_SELECT = false;
            pageBgList.add(page);
        }
        return pageBgList;
    }

    public void clearLayoutDataOfAnalysisPhotoBook() {
        if (photoBookLayoutData != null) {
            photoBookLayoutData.releaseInstance();
            photoBookLayoutData = null;
        }
    }

    public void clearTemplateInfo() {
        if (photoBookLayoutData != null) {
            photoBookLayoutData.clearTemplateInfo();
        }
    }

    public ArrayList<SnapsPage> getCoverPageListOfAnalysisPhotoBook() {
        if (getCoverTemplateOfAnalysisPhotoBook() == null) {
            return null;
        }
        return getCoverTemplateOfAnalysisPhotoBook().getPages();
    }

    private SnapsTemplate getCoverTemplateOfAnalysisPhotoBook() {
        if (photoBookLayoutData == null) {
            return null;
        }
        return photoBookLayoutData.getCoverTemplate();
    }

    public SmartRecommendBookLayoutData getPhotoBookLayoutData() {
        if (photoBookLayoutData == null) {
            photoBookLayoutData = new SmartRecommendBookLayoutData();
        }
        return photoBookLayoutData;
    }

    public boolean isShownImageSelectTutorial() {
        return isShownImageSelectTutorial;
    }

    public void setShownImageSelectTutorial(boolean shownImageSelectTutorial) {
        isShownImageSelectTutorial = shownImageSelectTutorial;
    }

    public void appendCoverPhotoMapKey(String key) {
        if (getPhotoBookLayoutData() != null && getPhotoBookLayoutData().getCoverPhotoKeySet() == null) {
            getPhotoBookLayoutData().setCoverPhotoKeySet(new HashSet<String>());
        }
        getPhotoBookLayoutData().getCoverPhotoKeySet().add(key);
    }

    public boolean removeCoverPhotoMapKey(String key) {
        if (!isCoverPhotoMapKey(key)) {
            return false;
        }
        getPhotoBookLayoutData().getCoverPhotoKeySet().remove(key);
        return true;
    }

    public boolean isCoverPhotoMapKey(String key) {
        return getPhotoBookLayoutData() != null && getPhotoBookLayoutData().getCoverPhotoKeySet() != null && getPhotoBookLayoutData().getCoverPhotoKeySet().contains(key);
    }

    public void clearCoverPhotoMapKeys() {
        if (getPhotoBookLayoutData() == null || getPhotoBookLayoutData().getCoverPhotoKeySet() == null) {
            return;
        }
        getPhotoBookLayoutData().getCoverPhotoKeySet().clear();
    }

    public boolean isContainCoverPhotoMapKey(String key) {
        return getPhotoBookLayoutData() != null && getPhotoBookLayoutData().getCoverPhotoKeySet() != null && getPhotoBookLayoutData().getCoverPhotoKeySet().contains(key);
    }

    public boolean isExistCoverPhotoMapKey() {
        return getPhotoBookLayoutData() != null && getPhotoBookLayoutData().getCoverPhotoKeySet() != null && !getPhotoBookLayoutData().getCoverPhotoKeySet().isEmpty();
    }

    public boolean isCompleteMakeRecommendBook() {
        return isCompleteMakeRecommendBook;
    }

    public void setCompleteMakeRecommendBook(boolean completeMakeRecommendBook) {
        isCompleteMakeRecommendBook = completeMakeRecommendBook;
    }

    public static class PageCanvasImageLoadSyncLocker {
        private Object pageCanvasImageLoadSyncLocker = null;
        private AtomicBoolean isPageCanvasImageLoadSyncLock = null;

        public static PageCanvasImageLoadSyncLocker createNewInstance() {
            PageCanvasImageLoadSyncLocker loadSyncLocker = new PageCanvasImageLoadSyncLocker();
            loadSyncLocker.isPageCanvasImageLoadSyncLock = new AtomicBoolean(true);
            loadSyncLocker.pageCanvasImageLoadSyncLocker = new Object();
            return loadSyncLocker;
        }

        private PageCanvasImageLoadSyncLocker() {
        }

        public Object getPageCanvasImageLoadSyncLocker() {
            return pageCanvasImageLoadSyncLocker;
        }

        public boolean isPageCanvasImageLoadSyncLock() {
            return isPageCanvasImageLoadSyncLock.get();
        }

        public void unLockPageCanvasImageLoadSyncLock() {
            isPageCanvasImageLoadSyncLock.set(false);
        }
    }
}
