package com.snaps.mobile.activity.diary.recoder;

import com.snaps.common.data.between.BaseResponse;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.json.SnapsDiaryListItemJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ysjeong on 16. 3. 16..
 */
public class SnapsDiaryListInfo {
    private static final String TAG = SnapsDiaryListInfo.class.getSimpleName();

    private int currentPageNo = 1;

    private int pageSize = 0;

    private int totalCount;

    private int androidCount;

    private int iosCount;

    private ArrayList<SnapsDiaryListItem> arrDiaryList = null;

    public ArrayList<SnapsDiaryListItem> getArrDiaryList() {
        if(arrDiaryList == null)
            arrDiaryList = new ArrayList<>();
        return arrDiaryList;
    }

    public boolean isExistOtherOsContents() {
        return iosCount > 0;
    }

    public boolean removeDiaryItem(String diaryNo) {
        List<SnapsDiaryListItem> list = getArrDiaryList();
        for(SnapsDiaryListItem item : list) {
            if(item == null) continue;
            if(item.getDiaryNo().equalsIgnoreCase(diaryNo)) {
                list.remove(item);
                return true;
            }
        }
        return false;
    }

    public SnapsDiaryListItem getDiaryItem(String diaryNo) {
        if(diaryNo == null) return null;
        List<SnapsDiaryListItem> list = getArrDiaryList();
        for(SnapsDiaryListItem item : list) {
            if(item == null) continue;
            if(item.getDiaryNo().equalsIgnoreCase(diaryNo)) {
                return item;
            }
        }
        return null;
    }

    public boolean isEmptyDiaryList() {
        ArrayList<SnapsDiaryListItem> arrayList = getArrDiaryList();
        return arrayList == null || arrayList.isEmpty();
    }

    public void clearDiaryList() {
        if(arrDiaryList == null || arrDiaryList.isEmpty()) return;
        arrDiaryList.clear();
    }

    public void updateItem(SnapsDiaryListItemJson jsonData) {
        if(jsonData == null || jsonData.getDiaryNo() == null) return;

        if(arrDiaryList == null)
            arrDiaryList = new ArrayList<>();

        for(SnapsDiaryListItem originItem : arrDiaryList) {
            if (originItem.getDiaryNo().equals(jsonData.getDiaryNo())) {
                SnapsDiaryListItem item = new SnapsDiaryListItem();
                item.setContents(getDiaryContents(jsonData.getDiaryContents()));
                item.setDate(jsonData.getDate());
                item.setFilePath(jsonData.getFilePath());
                item.setDiaryNo(jsonData.getDiaryNo());
                item.setThumbnail(jsonData.getThumbnailImg());
                item.setWeather(jsonData.getWeatherCode());
                item.setFeels(jsonData.getFeelingCode());
                item.setRegisteredDate(jsonData.getSaveDate());
                item.setOsType(jsonData.getOsType());
                item.setIsForceMoreText(BaseResponse.parseBool(jsonData.getForceMoreText()));
                originItem.set(item);
                break;
            }
        }
    }

    //bug fix
    //??????????????? ????????? ????????? url.encoding ?????? ?????? ??????.
    //????????? url.encoding ?????? ?????? ???????????? %??? ???????????? ?????????????????? ????????? url.decoding????????? ?????? ??????
    //CS ?????? ?????? ?????? ???????????? "200% ??????"????????? ?????? ????????? ????????? ?????? url.decoding ????????? ?????? ????????? ????????? ?????? ?????? ??????
    private String getDiaryContents(String data) {
        if (data == null || data.length() == 0) {
            return "";
        }

        try {
            String contents = StringUtil.getURLDecode(data, "utf-8"); //??????! ?????? ??????.. ?????? ?????? ????????? ????????? ???.....
            return contents;
        }catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return data;
    }

    private boolean isExcludeData(SnapsDiaryListItemJson data, Set<String> setExclude) {
        if(setExclude == null || setExclude.isEmpty()) return false;

        for (String diaryNo : setExclude) {
            if(diaryNo == null || data.getDiaryNo() == null) continue;
            if(data.getDiaryNo().equals(diaryNo)) {
                return true;
            }
        }

        return false;
    }

    public void addDiaryList(List<SnapsDiaryListItemJson> list) {
        addDiaryList(list, null);
    }

    public void addDiaryList(List<SnapsDiaryListItemJson> list, Set<String> setExclude) {

        if(arrDiaryList == null)
            arrDiaryList = new ArrayList<>();

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryListInfo listInfo = dataManager.getListInfo();

        synchronized(listInfo.getArrDiaryList()) {
            if(list != null && !list.isEmpty()) {
                for(SnapsDiaryListItemJson jsonData : list) {
                    if(jsonData == null) continue;

                    if(isExcludeData(jsonData, setExclude)) continue;

                    if(!isDuplicate(jsonData.getDiaryNo())) {
                        SnapsDiaryListItem item = new SnapsDiaryListItem();
                        item.setContents(getDiaryContents(jsonData.getDiaryContents()));
                        item.setDate(jsonData.getDate());
                        item.setFilePath(jsonData.getFilePath());
                        item.setDiaryNo(jsonData.getDiaryNo());
                        item.setThumbnail(jsonData.getThumbnailImg());
                        item.setWeather(jsonData.getWeatherCode());
                        item.setFeels(jsonData.getFeelingCode());
                        item.setRegisteredDate(jsonData.getSaveDate());
                        item.setIsForceMoreText(BaseResponse.parseBool(jsonData.getForceMoreText()));
                        item.setOsType(jsonData.getOsType());

                        arrDiaryList.add(item);
                    }
                }
            }
        }
    }

    private boolean isDuplicate(String diaryNo) {
        if(diaryNo == null || diaryNo.length() < 1 || arrDiaryList == null) return true; //????????? ?????????..

        for(SnapsDiaryListItem item : arrDiaryList) {
            if(item == null) continue;
            if(item.getDiaryNo().equals(diaryNo))
                return true;
        }
        return false;
    }

    public int getCurrentPageNo() {
        return currentPageNo;
    }

    public void setCurrentPageNo(int currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public void setCurrentPageNo(String pageNo) {
        if(pageNo == null || pageNo.length() < 1) return;
        try {
            setCurrentPageNo(Integer.parseInt(pageNo));
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageSize(String size) {
        if(size == null || size.length() < 1) return;
        try {
            this.pageSize = Integer.parseInt(size);
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setTotalCount(String totalCount) {
        if(totalCount == null || totalCount.length() < 1) return;
        try {
            this.totalCount = Integer.parseInt(totalCount);
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }
    }

    public boolean isMoreNextPage() {
        if (getTotalCount() < 1) return false;
        return getCurrentPageNo() * getPageSize() < getTotalCount();
    }

    public int getAndroidCount() {
        return androidCount;
    }

    public void setAndroidCount(String androidCount) {
        if(androidCount == null || androidCount.length() < 1) return;
        try {
            this.androidCount = Integer.parseInt(androidCount);
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }
    }

    public void setAndroidCount(int androidCount) {
        this.androidCount = androidCount;
    }

    public int getIosCount() {
        return iosCount;
    }

    public void setIosCount(String iosCount) {
        if(iosCount == null || iosCount.length() < 1) return;
        try {
            this.iosCount = Integer.parseInt(iosCount);
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }
    }

    public void setIosCount(int iosCount) {
        this.iosCount = iosCount;
    }
}
