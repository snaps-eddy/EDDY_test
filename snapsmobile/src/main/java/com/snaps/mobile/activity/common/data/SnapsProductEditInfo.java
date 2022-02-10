package com.snaps.mobile.activity.common.data;

import android.app.Activity;
import androidx.fragment.app.Fragment;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.mobile.activity.card.SnapsTextOptions;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public class SnapsProductEditInfo {
    private int currentPageIndex = 0;
    private String templateUrl = "";
    private boolean isFirstLoad = true;
    private int pageAddIndex = 2;

    private int m_iInitedCanvasIdx = 0;

    private boolean IS_EDIT_MODE = false; //재 편집여부

    private int tempImageViewID = -1;

    private long m_lPrevAddPageClickedTime = 0l;
    private int m_iTouchDownX = 0;

    private ArrayList<Fragment> canvasList = null;

    private Queue<Integer> pageLoadQueue = new LinkedBlockingQueue<Integer>();

    private ArrayList<SnapsPage> pageList = null;   //new ArrayList<>();

    private SnapsTemplate snapsTemplate = null;

    private ArrayList<MyPhotoSelectImageData> galleryList = new ArrayList<MyPhotoSelectImageData>();

    private int loadCompleteCount = 0;

    private SnapsProductEditInfo() {
        init();
    }

    public static SnapsProductEditInfo createInstance() {
        return new SnapsProductEditInfo();
    }

    private void init() {
        setFirstLoad(true);
        setTemplateUrl("");
        setIS_EDIT_MODE(false);
        setTempImageViewID(-1);
        setPrevAddPageClickedTime(0);
        setTouchDownX(0);
        setPageAddIndex(2);
        setPageLoadQueue(new LinkedBlockingQueue<Integer>());
        setCanvasList(null);
        setLoadCompleteCount(0);
        setPageList(new ArrayList<SnapsPage>());
        setGalleryList(new ArrayList<MyPhotoSelectImageData>());
        setSnapsTemplate(null);
    }

    public ArrayList<Fragment> getCanvasList() {
        return canvasList;
    }

    public void setCanvasList(ArrayList<Fragment> canvasList) {
        this.canvasList = canvasList;
    }

    public Queue<Integer> getPageLoadQueue() {
        return pageLoadQueue;
    }

    public void setPageLoadQueue(Queue<Integer> pageLoadQueue) {
        this.pageLoadQueue = pageLoadQueue;
    }

    public long getPrevAddPageClickedTime() {
        return m_lPrevAddPageClickedTime;
    }

    public void setPrevAddPageClickedTime(long m_lPrevAddPageClickedTime) {
        this.m_lPrevAddPageClickedTime = m_lPrevAddPageClickedTime;
    }

    public int getTouchDownX() {
        return m_iTouchDownX;
    }

    public void setTouchDownX(int m_iTouchDownX) {
        this.m_iTouchDownX = m_iTouchDownX;
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public void setCurrentPageIndex(int currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public boolean isFirstLoad() {
        return isFirstLoad;
    }

    public void setFirstLoad(boolean firstLoad) {
        isFirstLoad = firstLoad;
    }

    public int getPageAddIndex() {
        return pageAddIndex;
    }

    public void setPageAddIndex(int pageAddIndex) {
        this.pageAddIndex = pageAddIndex;
    }

    public void increasePageAddIndex() {
        pageAddIndex++;
    }

    public void decreasePageAddIndex() {
        pageAddIndex--;
    }

    public int getInitedCanvasIdx() {
        return m_iInitedCanvasIdx;
    }

    public void setInitedCanvasIdx(int m_iInitedCanvasIdx) {
        this.m_iInitedCanvasIdx = m_iInitedCanvasIdx;
    }

    public void initTemplateUrl() throws Exception {
        if (AutoSaveManager.isAutoSaveRecoveryMode()) return;

        setTemplateUrl("");

        if (!Config.getPROJ_CODE().equalsIgnoreCase("")) { // 파일 경로를 가지고 파싱작업..
            setTemplateUrl(SnapsAPI.GET_API_SAVE_XML() + "&prmProjCode=" + Config.getPROJ_CODE()); // save.xml를 읽어서 화면을 구성한다.
        } else {
            if (SnapsTemplateManager.getInstance().getSnapsTemplate() == null)
                setTemplateUrl(SnapsTemplate.getTemplateUrl());
        }
    }

    public void initTemplateUrl(ArrayList<String> templeteCodes) throws Exception {
        if (AutoSaveManager.isAutoSaveRecoveryMode()) return;

        setTemplateUrl("");

        if (!Config.getPROJ_CODE().equalsIgnoreCase("")) { // 파일 경로를 가지고 파싱작업..
            setTemplateUrl(SnapsAPI.GET_API_SAVE_XML() + "&prmProjCode=" + Config.getPROJ_CODE()); // save.xml를 읽어서 화면을 구성한다.
        } else {
            if (SnapsTemplateManager.getInstance().getSnapsTemplate() == null)
                setTemplateUrl(SnapsTemplate.getTemplateNewYearsCardUrl(templeteCodes));
        }
    }

    public void offerQueue(int start, int end) throws Exception {
        Queue<Integer> pageLoadQueue = getPageLoadQueue();
        if (pageLoadQueue != null) {
            int idx = start;
            for (int i = 0; i < (end + 1 - start); i++) {
                if (!pageLoadQueue.contains(idx))
                    pageLoadQueue.offer(idx++);
            }
        }
    }

    public boolean IS_EDIT_MODE() {
        return IS_EDIT_MODE;
    }

    public void setIS_EDIT_MODE(boolean IS_EDIT_MODE) {
        this.IS_EDIT_MODE = IS_EDIT_MODE;
    }

    public int getTempImageViewID() {
        return tempImageViewID;
    }

    public void setTempImageViewID(int tempImageViewID) {
        this.tempImageViewID = tempImageViewID;
    }

    public ArrayList<SnapsPage> getPageList() {
        return pageList;
    }

    public void setPageList(ArrayList<SnapsPage> pageList) {
        this.pageList = pageList;
    }

    public SnapsTemplate getSnapsTemplate() {
        return snapsTemplate;
    }

    public void setSnapsTemplate(SnapsTemplate snapsTemplate) {
        this.snapsTemplate = snapsTemplate;
    }

    public ArrayList<MyPhotoSelectImageData> getGalleryList() {
        return galleryList;
    }

    public void setGalleryList(ArrayList<MyPhotoSelectImageData> galleryList) {
        this.galleryList = galleryList;
    }

    public void initGalleryListFromDataTransManager(Activity activity) throws Exception {
        if (AutoSaveManager.isAutoSaveRecoveryMode()) return;

//        if (Config.getPROJ_CODE().equalsIgnoreCase("")) {
            DataTransManager dataTransManager = DataTransManager.getInstance();
            if (dataTransManager == null) {
                DataTransManager.notifyAppFinish(activity);
                return;
            }

            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            smartSnapsManager.copyAddedAllImageList(dataTransManager.getPhotoImageDataList());

            setGalleryList(smartSnapsManager.getAllAddedImageList());
//        }
    }

    public int getLoadCompleteCount() {
        return loadCompleteCount;
    }

    public void setLoadCompleteCount(int loadCompleteCount) {
        this.loadCompleteCount = loadCompleteCount;
    }

    public ArrayList<SnapsPage> getBackPageList() {
        if(getSnapsTemplate() == null) return null;
        return getSnapsTemplate()._backPageList;
    }

    public ArrayList<SnapsPage> getHiddenPageList() {
        if(getSnapsTemplate() == null) return null;
        return getSnapsTemplate()._hiddenPageList;
    }

    public SnapsTextOptions getTextOptions() {
        if(getSnapsTemplate() == null || getSnapsTemplate().info == null) return null;
        return getSnapsTemplate().info.snapsTextOption;
    }
}
