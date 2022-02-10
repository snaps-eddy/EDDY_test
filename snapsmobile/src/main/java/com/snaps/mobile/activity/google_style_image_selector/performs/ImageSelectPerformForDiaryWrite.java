package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.activities.SnapsDiaryConfirmEditableActivity;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryWriteInfo;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectTrayPageCountInfo;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2016. 12. 2..
 */

public class ImageSelectPerformForDiaryWrite extends BaseImageSelectPerformer implements IImageSelectProductPerform {
    public ImageSelectPerformForDiaryWrite(ImageSelectActivityV2 activity) {
        super(activity);
    }


    @Override
    public ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT performGetDefaultFragmentType() {
        return ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.SELECT_IMAGE_SRC;
    }

    /**
     * 완료 버튼을 눌렀을 때의 처리
     */
    @Override
    public void onClickedNextBtn() {
        if (imageSelectActivity == null) return;

        ImageSelectManager manager = ImageSelectManager.getInstance();
        if (manager == null) return;

        boolean isNotAllSelected = false; //템플릿이 있는데, 사진을 모두 선택하지 않은 형태

        ImageSelectTrayPageCountInfo pageCountInfo = manager.getPageCountInfo();
        if (pageCountInfo != null) {
//            if (pageCountInfo.getCurrentSelectedImageCount() < pageCountInfo.getTotalTemplateImageCount()) {
//                isNotAllSelected = true;
//            }
            isNotAllSelected = pageCountInfo.hasEmptyImageContainer();
        }

        if (isNotAllSelected) {
            String msg = String.format(imageSelectActivity.getString(R.string.select_some_photos), pageCountInfo.getTotalTemplateImageCount());
            MessageUtil.toast(imageSelectActivity, msg);
        } else {
            moveNextActivity();
        }
    }

    /**
     * 액티비티 이동
     */
    @Override
    public void moveNextActivity() {
        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder != null) {
            ArrayList<MyPhotoSelectImageData> dataList = holder.getNormalData();

            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
            ArrayList<MyPhotoSelectImageData> editingImageList = writeInfo.getPhotoImageDataList();
            boolean isUseEditingImageList = false;
            if (editingImageList != null && dataList != null && editingImageList.size() == dataList.size()) {
                boolean isSameItem = true;
                for (int ii = 0; ii < editingImageList.size(); ii++) {
                    MyPhotoSelectImageData editItem = editingImageList.get(ii);
                    MyPhotoSelectImageData returnItem = dataList.get(ii);
                    if (editItem == null || returnItem == null) continue;
                    if (editItem.IMAGE_ID != returnItem.IMAGE_ID) {
                        isSameItem = false;
                        break;
                    }
                }
                if (isSameItem) isUseEditingImageList = true;
            }

            if (!isUseEditingImageList) {
                dataManager.clearImageList();
                writeInfo.setPhotoImageDataList(dataList);
            }

            //뒤로가기로 돌아왔을 때 데이터를 복구하기 위해..
            DataTransManager dataTransManager = DataTransManager.getInstance();
            if (dataTransManager != null) {
                dataTransManager.cloneCurrentSelectedImageList();
            }

            Intent saveIntent = new Intent(imageSelectActivity, SnapsDiaryConfirmEditableActivity.class);
            imageSelectActivity.startActivityForResult(saveIntent, ISnapsImageSelectConstants.REQCODE_DIARY_WRITE);
        }
    }
}
