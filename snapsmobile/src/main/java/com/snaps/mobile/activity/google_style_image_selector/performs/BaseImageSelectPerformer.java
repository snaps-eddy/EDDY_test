package com.snaps.mobile.activity.google_style_image_selector.performs;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.ui.CustomizeDialog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2016. 12. 2..
 */

public class BaseImageSelectPerformer {
    public static final String TEMPLATE_PATH = "/cache/template/template.xml";

    protected ImageSelectActivityV2 imageSelectActivity = null;

    private CustomizeDialog cellularDataConfirmDialog = null;

    public BaseImageSelectPerformer(ImageSelectActivityV2 activity) {
        this.imageSelectActivity = activity;
    }

    protected boolean isSuccessSetSimpleDatas() {
        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder != null) {
            // 이미지 데이터와 템플릿 path를 넘긴다.
            ArrayList<MyPhotoSelectImageData> postImageList = holder.getSimpleData(getSelectedImageKeysFromTrayCells());
            DataTransManager dataTransManager = DataTransManager.getInstance();
            if (dataTransManager != null) {
                dataTransManager.releaseAllData();
                dataTransManager.setPhotoImageDataList(postImageList);
            } else {
                DataTransManager.notifyAppFinish(imageSelectActivity);
                return false;
            }
        }

        return true;
    }

    private ArrayList<String> getSelectedImageKeysFromTrayCells() {
        if (imageSelectActivity == null) return null;

        ArrayList<String> idxArr = new ArrayList<String>();
        ImageSelectUIProcessor uiProcessor = imageSelectActivity.getUIProcessor();
        if (uiProcessor == null) return null;

        ImageSelectTrayBaseAdapter baseAdapter = uiProcessor.getTrayAdapter();
        if (baseAdapter != null) {
            ArrayList<ImageSelectTrayCellItem> arCellList = baseAdapter.getTrayCellItemList();
            if (arCellList != null) {
                for (ImageSelectTrayCellItem cellItem : arCellList) {
                    if (cellItem == null) continue;
                    String key = cellItem.getImageKey();
                    idxArr.add(key != null ? key : "");
                }
            }
        }
        return idxArr;
    }

    public CustomizeDialog getCellularDataConfirmDialog() {
        return cellularDataConfirmDialog;
    }

    public void setCellularDataConfirmDialog(CustomizeDialog cellularDataConfirmDialog) {
        this.cellularDataConfirmDialog = cellularDataConfirmDialog;
    }
}
