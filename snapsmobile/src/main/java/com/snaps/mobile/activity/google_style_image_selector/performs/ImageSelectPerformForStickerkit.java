package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.EditActivity;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

import static com.snaps.common.utils.constant.Config.getTMPL_CODE;

/**
 * Created by ysjeong on 2016. 12. 2..
 */

public class ImageSelectPerformForStickerkit extends BaseImageSelectPerformer implements IImageSelectProductPerform {
    public ImageSelectPerformForStickerkit(ImageSelectActivityV2 activity) {
        super(activity);
    }

    @Override
    public ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT performGetDefaultFragmentType() {
        Config.setPROD_CODE(Config.PRODUCT_STICKER);
        ImageSelectIntentData intentData = imageSelectActivity.getIntentData();
        if (intentData != null) {
            Config.setPAPER_CODE(intentData.getHomeSelectPaperType());
            String selectKind = intentData.getHomeSelectKind();

            if (selectKind.equals("6")) {
                Config.setTMPL_CODE(Config.TEMPLATE_STICKER_6);
            } else if (selectKind.equals("2")) {
                Config.setTMPL_CODE(Config.TEMPLATE_STICKER_2);
            } else if (selectKind.equals("1")) {
                Config.setTMPL_CODE(Config.TEMPLATE_STICKER_1);
            }
        }

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
            if (getTMPL_CODE().equals(Config.TEMPLATE_STICKER_6)) {
                MessageUtil.toast(imageSelectActivity, R.string.select_sticker_6_nocount);
            } else if (getTMPL_CODE().equals(Config.TEMPLATE_STICKER_2)) {
                MessageUtil.toast(imageSelectActivity, R.string.select_sticker_2_nocount);
            } else if (getTMPL_CODE().equals(Config.TEMPLATE_STICKER_1)) {
                MessageUtil.toast(imageSelectActivity, R.string.select_sticker_1_nocount);
            }
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
        Intent saveIntent = new Intent(imageSelectActivity, EditActivity.class);
        imageSelectActivity.startActivity(saveIntent);
        imageSelectActivity.finish(); //그냥 종료 시켜 버리자..
    }
}
