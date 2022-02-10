package com.snaps.mobile.activity.google_style_image_selector.datas;

/**
 * Created by ysjeong on 2016. 11. 29..
 */

public class ImageSelectTrayPageCountInfo {
    private int lastSnapsPageIdx = 2;  //페이지를 추가할 때, 스냅스 페이지의 IDX (커버, 속지 다음부터...)
    private int addedPageCount = 0;
    private int totalPageCount = 0;
    private int lastSelectCellId = 0;

    private int totalTemplateImageCount = 0;
    private int currentSelectedImageCount = 0;

    private boolean isAddedPage = false; //페이지가 추가 되었는지

    public int getTotalTemplateImageCount() {
        return totalTemplateImageCount;
    }

    public ImageSelectTrayPageCountInfo setTotalTemplateImageCount(int totalTemplateImageCount) {
        this.totalTemplateImageCount = totalTemplateImageCount;
        return this;
    }

    public int getCurrentSelectedImageCount() {
        return currentSelectedImageCount;
    }

    public ImageSelectTrayPageCountInfo setCurrentSelectedImageCount(int currentSelectedImageCount) {
        this.currentSelectedImageCount = currentSelectedImageCount;
        return this;
    }

    public int getLastSelectCellId() {
        return lastSelectCellId;
    }

    public ImageSelectTrayPageCountInfo setLastSelectCellId(int lastSelectCellId) {
        this.lastSelectCellId = lastSelectCellId;
        return this;
    }

    public int getLastSnapsPageIdx() {
        return lastSnapsPageIdx;
    }

    public void setLastSnapsPageIdx(int lastSnapsPageIdx) {
        this.lastSnapsPageIdx = lastSnapsPageIdx;
    }

    public void addLastSnapsPageIdx() {
        this.lastSnapsPageIdx++;
    }

    public int getAddedPageCount() {
        return addedPageCount;
    }

    public void setAddedPageCount(int addedPageCount) {
        this.addedPageCount = addedPageCount;
    }

    public void addAddedPageCount() {
        this.addedPageCount++;
    }

    public int getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(int totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public void addTotalPageCount() {
        this.totalPageCount++;
    }

    public boolean isAddedPage() {
        return isAddedPage;
    }

    public void setAddedPage(boolean addedPage) {
        isAddedPage = addedPage;
    }

    public boolean hasEmptyImageContainer() {
        return currentSelectedImageCount < totalTemplateImageCount;
    }

    public boolean hasEmptyImageContainerByTotalCount(int compareTotalSelectImageCount) {
        return currentSelectedImageCount < compareTotalSelectImageCount;
    }
}
