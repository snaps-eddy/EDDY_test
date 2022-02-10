package com.snaps.mobile.activity.google_style_image_selector.performs;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ysjeong on 2016. 12. 2..
 */

public class ImageSelectPerformForMultiChooseType extends BaseImageSelectPerformer implements IImageSelectProductPerform {
    public ImageSelectPerformForMultiChooseType(ImageSelectActivityV2 activity) {
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
            MessageUtil.toast(imageSelectActivity, R.string.select_nocount);
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
        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder != null) {
            ArrayList<MyPhotoSelectImageData> datas = holder.getNormalData();
            if (datas != null && datas.size() > 0) {
                if (Config.isSmartSnapsRecommendLayoutPhotoBook()) {
                    SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
                    smartSnapsManager.appendAddedAllImageListAndSortingDuplicatedPhoto(datas);
                }

                if (imageSelectActivity != null) {
                    imageSelectActivity.setResult(RESULT_OK);
                    imageSelectActivity.finish();
                }
            }
        }
    }
}
