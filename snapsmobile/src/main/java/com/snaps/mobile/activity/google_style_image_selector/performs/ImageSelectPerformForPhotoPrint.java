package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;

import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.photoprint.NewPhotoPrintListActivity;

/**
 * Created by ysjeong on 2016. 12. 2..
 */

public class ImageSelectPerformForPhotoPrint extends BaseImageSelectPerformer implements IImageSelectProductPerform {
    public ImageSelectPerformForPhotoPrint(ImageSelectActivityV2 activity) {
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

        boolean isMoveNext = true;
        if (ImageSelectUtils.getCurrentSelectedImageCount() < 1) {
            MessageUtil.toast(imageSelectActivity, R.string.select_photo_nocount);
            isMoveNext = false;
        }

        if (isMoveNext) {
            moveNextActivity();
        }
    }

    /**
     * 액티비티 이동
     */
    @Override
    public void moveNextActivity() {
        Intent saveIntent = new Intent(imageSelectActivity, NewPhotoPrintListActivity.class);
        String prodCode = imageSelectActivity.getIntentData() != null ? imageSelectActivity.getIntentData().getHomeSelectProductCode() : "";
        if( !StringUtil.isEmpty(prodCode) )
            saveIntent.putExtra( Const_EKEY.HOME_SELECT_PRODUCT_CODE, prodCode );
        DataTransManager dataTransManager = DataTransManager.getInstance();
        if (dataTransManager != null) {
            ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
            if (holder != null) {
                dataTransManager.releaseAllData();
                dataTransManager.setPhotoImageDataList(holder.getNormalData());
            }
        } else {
            DataTransManager.notifyAppFinish(imageSelectActivity);
            return;
        }
        imageSelectActivity.startActivityForResult( saveIntent, ISnapsImageSelectConstants.REQCODE_PHOTOPRINT );
    }
}
