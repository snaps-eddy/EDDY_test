package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ysjeong on 2016. 12. 2..
 */

public class ImageSelectPerformForSingleChoose extends BaseImageSelectPerformer implements IImageSelectProductPerform {
    public ImageSelectPerformForSingleChoose(ImageSelectActivityV2 activity) {
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

        boolean isSelected = false;
        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder != null) {
            ArrayList<MyPhotoSelectImageData> imageDatas = holder.getNormalData();
            if (imageDatas != null && imageDatas.size() > 0) {
                MyPhotoSelectImageData imgData = imageDatas.get(0);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("imgData", imgData);
                intent.putExtras(bundle);
                imageSelectActivity.setResult(RESULT_OK, intent);
                imageSelectActivity.finish();
                isSelected = true;
            }
        }

        if (!isSelected) {
            MessageUtil.toast(imageSelectActivity, R.string.select_nocount);
        }
    }

    /**
     * 액티비티 이동
     */
    @Override
    public void moveNextActivity() {}
}
