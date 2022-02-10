package com.snaps.mobile.activity.diary;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryUploadOpserver;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryUploadSubject;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryFontInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListItem;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryPageInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUploadSeqInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUserInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryWriteInfo;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by ysjeong on 16. 3. 9..
 */
public class SnapsDiaryDataManager implements ISnapsDiaryUploadSubject {
    private static final String TAG = SnapsDiaryDataManager.class.getSimpleName();
    private static volatile SnapsDiaryDataManager gInstance = null;

    private SnapsDiaryWriteInfo writeInfo = null;

    private SnapsDiaryListInfo listInfo = null;

    private SnapsDiaryListInfo publishListInfo = null;

    private SnapsDiaryUploadSeqInfo uploadInfo = null;

    private SnapsDiaryUserInfo snapsDiaryUserInfo = null;

    private SnapsDiaryFontInfo snapsDiaryFont = null;

    private SnapsDiaryPageInfo snapsDiaryPageInfo = null;

    private String templateFilePath = "";

    private String layoutTemplateCachePath = "";

    private String productCode, paperCode, templateId, projectTitle, startDate, endDate;

    private boolean isWritingDiary = false; //스냅스 일기쪽에 머물러 있는 지.

    private byte writeMode = SnapsDiaryConstants.EDIT_MODE_NEW_WRITE;

    private Set<ISnapsDiaryUploadOpserver> setDiaryWriteActivities = null;

    public static void createInstance() {
        if(gInstance ==  null) {
            synchronized(SnapsDiaryDataManager.class) {
                gInstance = new SnapsDiaryDataManager();
            }
        }

        gInstance.releaseAllData();

        DataTransManager.releaseCloneImageSelectDataHolder();
    }

    public static SnapsDiaryDataManager getInstance() {
        if(gInstance ==  null) createInstance();
        return gInstance;
    }

    public static void finalizeInstance() {
        if(gInstance != null)  {
            gInstance.setIsWritingDiary(false);
            gInstance.releaseAllData();
            gInstance = null;
        }
        DataTransManager.releaseCloneImageSelectDataHolder();
    }

    public static boolean isAliveSnapsDiaryService() {
        return getInstance().isWritingDiary();
    }

    public static String getDiarySeq() {
        if (!SnapsDiaryDataManager.isAliveSnapsDiaryService()) return "";
        SnapsDiaryDataManager diaryDataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryUploadSeqInfo uploadSeqInfo = diaryDataManager.getUploadInfo();
        return uploadSeqInfo != null ? uploadSeqInfo.getSeqDiaryNo() : "";
    }

    /**
     * 일기를 삭제 할 때, 자동으로 미션 실패되는 것을 체크 함.
     * @param item
     * @return
     */
    public static boolean isWarningMissionFailedWhenDelete(SnapsDiaryListItem item) {
        if (item == null || item.getDate() == null || item.getDate().length() != 8) return false;

        Calendar current = Calendar.getInstance();
        SimpleDateFormat originformat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date diaryDate = originformat.parse(item.getDate());
            Calendar diaryCal = Calendar.getInstance();
            diaryCal.setTime(diaryDate);
            if (current.get(Calendar.YEAR) == diaryCal.get(Calendar.YEAR)
                    && current.get(Calendar.MONTH) == diaryCal.get(Calendar.MONTH)
                    && current.get(Calendar.DAY_OF_MONTH) == diaryCal.get(Calendar.DAY_OF_MONTH)) return false;
        } catch (ParseException e) {
            Dlog.e(TAG, e);
        }

        SnapsDiaryUserInfo userInfo = getInstance().getSnapsDiaryUserInfo();
        return (userInfo.getNeedInkCount() > 0) && (userInfo.getNeedInkCount() + 1) > (userInfo.getRemainDaysInt() + 1);
    }

    public String getLayoutTemplateCachePath() {
        return layoutTemplateCachePath;
    }

    public void setLayoutTemplateCachePath(String layoutTemplateCachePath) {
        this.layoutTemplateCachePath = layoutTemplateCachePath;
    }

    public String getTemplateFilePath() {
        return templateFilePath;
    }

    public void setTemplateFilePath(String templateFilePath) {
        this.templateFilePath = templateFilePath;
    }

    @Override
    public void registDiaryUploadObserver(ISnapsDiaryUploadOpserver ob) {
        if(setDiaryWriteActivities == null) setDiaryWriteActivities = new LinkedHashSet<>();
        setDiaryWriteActivities.add(ob);
    }

    @Override
    public void removeAllDiaryUploadObserver() {
        if(setDiaryWriteActivities != null && !setDiaryWriteActivities.isEmpty())
            setDiaryWriteActivities.clear();
        setDiaryWriteActivities = null;
    }

    @Override
    public void removeDiaryUploadObserver(ISnapsDiaryUploadOpserver ob) {
        if(setDiaryWriteActivities != null && setDiaryWriteActivities.contains(ob))
            setDiaryWriteActivities.remove(ob);
    }

    @Override
    public void notifyDiaryUploadOpservers(boolean isIssuedInk, boolean isNewWrite) {
        if(getDiaryOpservers() == null) return;
        synchronized (getDiaryOpservers()) {
            for (ISnapsDiaryUploadOpserver opserver : getDiaryOpservers()) {
                if(opserver != null) opserver.onFinishDiaryUpload(isIssuedInk, isNewWrite);
            }
        }
    }

    private Set<ISnapsDiaryUploadOpserver> getDiaryOpservers() {
        return setDiaryWriteActivities;
    }

    public void init( String productCode, String paperCode, String templateId, String projectTitle, String startDate, String endDate ) {
        this.productCode = productCode;
        this.paperCode = paperCode;
        this.templateId = templateId;
        this.projectTitle = projectTitle;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setCoverTitle( String title ) { this.projectTitle = title; }
    public String getCoverTitle() { return this.projectTitle; }
    public String getPaperCode() { return this.paperCode; }
    public String getTemplateId() { return this.templateId; }
    public String getProductCode() { return this.productCode; }
    public String getStartDate() { return this.startDate; }
    public String getEndDate() { return this.endDate; }
    public void setStartDate( String startDate ) { this.startDate = startDate; }
    public void setEndDate( String endDate) { this.endDate = endDate; }

    public SnapsDiaryPageInfo getPageInfo( boolean initialize ) {
        if( snapsDiaryPageInfo == null || initialize ) createPageInfo();
        return snapsDiaryPageInfo;
    }

    public void createPageInfo() {
        snapsDiaryPageInfo = new SnapsDiaryPageInfo();
        snapsDiaryPageInfo.setStartDate(startDate);
        snapsDiaryPageInfo.setEndDate( endDate );
    }

    public SnapsDiaryWriteInfo getWriteInfo() {
        if(writeInfo == null) {
            writeInfo = new SnapsDiaryWriteInfo();
        }

        return writeInfo;
    }

    public void setWriteInfo(SnapsDiaryWriteInfo writeInfo) {
        this.writeInfo = writeInfo;
    }

    public SnapsDiaryListInfo getPublishListInfo() {
        if(publishListInfo == null)
            publishListInfo = new SnapsDiaryListInfo();
        return publishListInfo;
    }

    public void setPublishListInfo(SnapsDiaryListInfo publishListInfo) {
        this.publishListInfo = publishListInfo;
    }

    public SnapsDiaryListInfo getListInfo() {
        if(listInfo == null)
            listInfo = new SnapsDiaryListInfo();
        return listInfo;
    }

    public void setListInfo(SnapsDiaryListInfo listInfo) {
        this.listInfo = listInfo;
    }

    public SnapsDiaryUploadSeqInfo getUploadInfo() {
        return uploadInfo;
    }

    public void setUploadInfo(SnapsDiaryUploadSeqInfo uploadInfo) {
        this.uploadInfo = uploadInfo;
    }

    public SnapsDiaryUserInfo getSnapsDiaryUserInfo() {
        return snapsDiaryUserInfo;
    }

    public SnapsDiaryFontInfo getSnapsDiaryFont() {
        return snapsDiaryFont;
    }

    public void setSnapsDiaryFont(SnapsDiaryFontInfo snapsDiaryFont) {
        this.snapsDiaryFont = snapsDiaryFont;
    }

    public void setSnapsDiaryUserInfo(SnapsDiaryUserInfo snapsDiaryUserInfo) {
        this.snapsDiaryUserInfo = snapsDiaryUserInfo;
    }

    public static boolean isExistDiarySeqNo() {
        String diaryNo = getInstance().getUploadInfo() != null ? getInstance().getUploadInfo().getSeqDiaryNo() : null;
        if (diaryNo != null) diaryNo = diaryNo.trim();
        return !StringUtil.isEmpty(diaryNo);
    }

    public boolean isExistUserTumbnail() {
        if (snapsDiaryUserInfo == null) return false;
        return snapsDiaryUserInfo.getThumbnailPath() != null && snapsDiaryUserInfo.getThumbnailPath().length() > 0;
    }

    public void clearUploadInfo() {
        if(gInstance != null && gInstance.uploadInfo != null) {
            uploadInfo = null;
        }
    }

    public void clearImageList() {
        if(gInstance != null) {
            if (getWriteInfo() != null) {
                getWriteInfo().clearImageList();
            }
        }
    }

    public void clearUserInfo() {
        if(gInstance != null) {
            SnapsDiaryUserInfo userInfo = getSnapsDiaryUserInfo();
            if (userInfo != null) {
                userInfo.releaseCache();
            }
            userInfo = null;
        }
    }

    public void deleteTemplateCache() {
        if(gInstance != null) {
            try {
                File templateFile = new File(gInstance.getTemplateFilePath());
                if(templateFile.exists()) {
                    templateFile.delete();
                }

                File layoutCacheFile = new File(gInstance.getLayoutTemplateCachePath());
                if(layoutCacheFile.exists()) {
                    layoutCacheFile.delete();
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    public void clearFontInfo() {
        if(gInstance != null) {
            gInstance.setSnapsDiaryFont(null);
        }
    }

    public void clearWriteInfo() {
        if(gInstance != null) {
            gInstance.setWriteInfo(null);
        }
    }

    public void clearListInfo() {
        if(gInstance != null) {
            gInstance.setListInfo(null);
        }
    }

    public void clearPublicListInfo() {
        if(gInstance != null) {
            gInstance.setPublishListInfo(null);
        }
    }

    public void clearUploadSeqInfo() {
        if(gInstance != null) {
            gInstance.setUploadInfo(null);
        }
    }

    public boolean isWritingDiary() {
        return isWritingDiary;
    }

    public void setIsWritingDiary(boolean isDiaryWriting) {
        this.isWritingDiary = isDiaryWriting;
    }

    public byte getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(byte writeMode) {
        this.writeMode = writeMode;
    }

    private void releaseAllData() {
        if(gInstance ==  null) return;

        try {
            removeAllDiaryUploadObserver();

            clearUploadInfo();

            clearImageList();

            clearFontInfo();

            clearWriteInfo();

            clearListInfo();

            clearPublicListInfo();

            clearUploadSeqInfo();

            clearUserInfo();

            deleteTemplateCache();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
