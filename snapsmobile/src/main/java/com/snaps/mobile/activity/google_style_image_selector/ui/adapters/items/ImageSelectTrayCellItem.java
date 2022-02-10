package com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items;

import android.content.Context;

import com.snaps.common.data.parser.GetTemplateXMLHandler;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;

/**
 * Created by ysjeong on 2016. 11. 25..
 */

public class ImageSelectTrayCellItem implements ISnapsImageSelectConstants {
    private ImageSelectAdapterHolders.TrayThumbnailItemHolder holder = null;
    private Context context = null;
    private SnapsPage snapsPage = null;
    private String label = ""; //트레이에 표시할 텍스트
    private int lastIdx = 0;
    private int cellId = -1;
    private String imageKey = ""; //앨범에서 선택한 사진의 키
    private boolean isDrawHalfLine = false;
    private boolean isSelected = false;
    private boolean isPrevDelete = false;
    private boolean isNoPrint = false;
    private boolean isCoverPhoto = false;

    private eTRAY_CELL_STATE cellState = eTRAY_CELL_STATE.EMPTY;

    public ImageSelectTrayCellItem(Context context, int cellId, eTRAY_CELL_STATE cellState) {
        this.context = context;
        this.cellId = cellId;
        this.cellState = cellState;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public boolean isEqualsItem(ImageSelectTrayCellItem item) {
        return item != null && (this.imageKey != null && this.imageKey.equalsIgnoreCase(item.imageKey)) &&
                this.isSelected == item.isSelected;
    }

    public ImageSelectAdapterHolders.TrayThumbnailItemHolder getHolder() {
        return holder;
    }

    public void setHolder(ImageSelectAdapterHolders.TrayThumbnailItemHolder holder) {
        this.holder = holder;
    }

    public int getLastIdx() {
        return lastIdx;
    }

    public boolean isNoPrint() {
        return isNoPrint;
    }

    public void setNoPrint(boolean noPrint) {
        isNoPrint = noPrint;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isPrevDelete() {
        return isPrevDelete;
    }

    public void setPrevDelete(boolean prevDelete) {
        isPrevDelete = prevDelete;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isDrawHalfLine() {
        return isDrawHalfLine;
    }

    public boolean isPlusBtn() {
        return getCellState() == eTRAY_CELL_STATE.PLUS_BUTTON;
    }

    public boolean isDummyItem() {
        return getCellState() == eTRAY_CELL_STATE.EMPTY_DUMMY;
    }

    public eTRAY_CELL_STATE getCellState() {
        return cellState;
    }

    public void setCellState(eTRAY_CELL_STATE cellState) {
        this.cellState = cellState;
    }

    public int getCellId() {
        return cellId;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public SnapsPage getSnapsPage() {
        return snapsPage;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public boolean isCoverPhoto() {
        return isCoverPhoto;
    }

    public void setCoverPhoto(boolean coverPhoto) {
        isCoverPhoto = coverPhoto;
    }

    public void setSnapsPage(SnapsPage snapsPage, int firstIdx, int lastIdx, int pageCount) {
        this.snapsPage = snapsPage;
        this.lastIdx = lastIdx;

        // 중간라인을 그리지 않는 상품들
        if (Const_PRODUCT.isSinglePageProduct() || Const_PRODUCT.isPackageProduct() || Const_PRODUCT.isCardProduct() || SnapsDiaryDataManager.isAliveSnapsDiaryService() || Config.isSimpleMakingBook())
            this.isDrawHalfLine = false;
        else
            this.isDrawHalfLine = true;

        if (Config.isCalendar()) {
            if (snapsPage.type.compareTo("cover") == 0) {
                label = context.getString(R.string.cover);
            } else {
                int nStartYear = GetTemplateXMLHandler.getStartYear();
                int nStartMonth = GetTemplateXMLHandler.getStartMonth();

                int month = 0;

                String pageIdx = pageCount + "";

                int nPage = Integer.parseInt(pageIdx);

                if ((nStartMonth + nPage) > 12) {
                    month = ((nStartMonth + nPage) % 12);
                    nStartYear++;
                } else {
                    month = nStartMonth + nPage;
                }

                String labelText = String.format("%d년 %d월", nStartYear, month);
                if (!Config.useKorean()) {
                    labelText = String.format("%d.%d", nStartYear, month);
                }

                label = labelText;
                ;
            }
        } else if (Config.isSimplePhotoBook()) {
            if (snapsPage.type.compareTo("cover") == 0) {
                label = context.getString(R.string.cover);
            } else if (snapsPage.type.compareTo("title") == 0) {
                label = context.getString(R.string.inner_title_page);
            } else {
                int pageNum = firstIdx - 2;
                label = (Integer.toString(pageNum) + "." + Integer.toString(pageNum + 1) + "p");
            }
        } else if (Config.isSimpleMakingBook()) {
            if (snapsPage.type.compareTo("cover") == 0) {
                label = context.getString(R.string.cover);
            } else if (snapsPage.type.compareTo("title") == 0) {
                label = context.getString(R.string.inner_title_page);
            } else {
                int pageNum = (pageCount - 1) * 2 + 1;
                label = (pageNum + "." + (pageNum + 1) + "p");
            }
        } else if (Const_PRODUCT.isAccordionCardProduct()) {
//            if(pageCount == 0 && lastIdx == 5) {
//                label = context.getString(R.string.front_page);
//            } else if(pageCount == 1 && lastIdx == 5){
//                label = context.getString(R.string.back_page);
//            } else {
//                label = "";
//            }
            if (pageCount == 0) {
                label = context.getString(R.string.front_page);
            } else if (pageCount == 1) {
                label = context.getString(R.string.back_page);
            }
        } else if (Const_PRODUCT.isNewWalletProduct()) {
            if ((pageCount % 2) == 0) {
                label = context.getString(R.string.front_page);
            } else {
                label = context.getString(R.string.back_page);
            }
        } else { // 액자군은 텍스트를 넣지 않는다.
            label = "";
        }
    }
}
