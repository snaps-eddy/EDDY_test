package com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryRecyclerCustomAdapter;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.datas.TrayAdapterInsertParam;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;

import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.eTRAY_CELL_STATE.PHOTO_THUMBNAIL;
import static com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils.getSelectImageHolder;

public class ImageSelectTrayKTBookAdapter extends ImageSelectTraySmartSnapsSelectAdapter implements ISnapsDiaryRecyclerCustomAdapter {

    public ImageSelectTrayKTBookAdapter(ImageSelectActivityV2 imageSelectActivityV2) {
        super(imageSelectActivityV2);
    }

    @Override
    public boolean checkExcessMaxPhoto() {
        ImageSelectImgDataHolder holder = getSelectImageHolder();
        if (holder == null) {
            return false;
        }
        int maxCount = imageSelectPublicMethods != null && imageSelectPublicMethods.getUIProcessor() != null ? imageSelectPublicMethods.getUIProcessor().getMaxImageCount() : 0;
        return holder.getMapSize() >= maxCount;
    }

    @Override
    public boolean insertPhotoThumbnailOnTrayItem(TrayAdapterInsertParam trayAdapterInsertParam) {
        if (trayAdapterInsertParam == null) return false;

        String key = trayAdapterInsertParam.getImageMapKey();
        MyPhotoSelectImageData imageData = trayAdapterInsertParam.getImageData();

        if (hasNotEmptySpace()) {
            imageSelectPublicMethods.removeSelectedImageData(key);
            return false;
        }

        if (isPageSyncLock()) return false;
        setPageSyncLock(true);

        int cellItemId = createNewCell();
        if (cellItemId >= 0) {
            ImageSelectTrayCellItem cellItem = getTrayCellItemById(cellItemId);
            if (cellItem != null) {
                cellItem.setImageKey(key);
                cellItem.setCellState(PHOTO_THUMBNAIL);
                cellItem.setNoPrint(imageData.isNoPrint);
                if (trayAdapterInsertParam.isArrayInsert()) {
                    if (trayAdapterInsertParam.isArrayInsertAndLastItem()) {
                        notifyDataSetChanged();
                        scrollToCenterTrayView(cellItem.getCellId());
                    }
                } else {
                    notifyItemChanged(cellItem.getCellId());
                    scrollToCenterTrayView(cellItem.getCellId());
                }
            }

            setSelectedImageInfo(key);

            imageSelectPublicMethods.putSelectedImageData(key, imageData);

            refreshCounterInfo();
        }

        createThumbnailCacheWithImageData(imageData);

        setPageSyncLock(false);
        return true;
    }

    private boolean hasNotEmptySpace() {
        ImageSelectImgDataHolder holder = getSelectImageHolder();
        if (holder == null) {
            return false;
        }
        int maxCount = imageSelectPublicMethods != null && imageSelectPublicMethods.getUIProcessor() != null ? imageSelectPublicMethods.getUIProcessor().getMaxImageCount() : 0;
        return holder.getMapSize() > maxCount;
    }
}
