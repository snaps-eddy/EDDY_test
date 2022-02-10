package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;

import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

/**
 * Created by ysjeong on 2016. 12. 2..
 */

public class ImageSelectPerformForIdentifyPhotoPrint extends BaseImageSelectPerformer implements IImageSelectProductPerform {
    public ImageSelectPerformForIdentifyPhotoPrint(ImageSelectActivityV2 activity) {
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
        if(isSuccessSetSimpleDatas()) {
            Intent saveIntent = new Intent(imageSelectActivity, SnapsEditActivity.class);
            saveIntent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.IDENTIFY_PHOTO.ordinal());
            saveIntent.putExtra("templete", BaseImageSelectPerformer.TEMPLATE_PATH);
            imageSelectActivity.startActivity(saveIntent);
            imageSelectActivity.finish();
        }
    }
}
