package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;
import android.text.TextUtils;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ysjeong on 2016. 12. 2..
 */

public class ImageSelectPerformForTemplateFreeChoose extends BaseImageSelectPerformer implements IImageSelectProductPerform {
    public ImageSelectPerformForTemplateFreeChoose(ImageSelectActivityV2 activity) {
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

        if (ImageSelectUtils.getCurrentSelectedImageCount() < 1) {
            MessageUtil.alertnoTitle(imageSelectActivity, imageSelectActivity.getString(R.string.disable_add_photo_for_clear_photo), new ICustomDialogListener() {

                @Override
                public void onClick(byte clickedOk) {
                    if (clickedOk == ICustomDialogListener.OK)
                        moveNextActivity();
                    else
                        MessageUtil.toast(getApplicationContext(), imageSelectActivity.getString(R.string.cancel_msg));
                }
            });
            return;
        }
        moveNextActivity();
    }

    /**
     * 액티비티 이동
     */
    @Override
    public void moveNextActivity() {
        if(isSuccessSetSimpleDatas()) {
            Intent saveIntent = new Intent(imageSelectActivity, SnapsEditActivity.class);
            SnapsProductEditConstants.eSnapsProductKind productKind  = getProductKind();
            if(productKind == null) return;
            saveIntent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, productKind.ordinal());
            imageSelectActivity.startActivity(saveIntent);
            imageSelectActivity.finish();
        }
    }

    private SnapsProductEditConstants.eSnapsProductKind getProductKind() {
        if(Const_PRODUCT.isTransparencyPhotoCardProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.TRANSPARENCY_PHOTO_CARD;
        } else if(Const_PRODUCT.isPosterGroupProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.POSTER;
        } else if(Const_PRODUCT.isLongPhotoStickerProduct() || Const_PRODUCT.isStikerGroupProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.STICKER;
        } else if(Const_PRODUCT.isCardProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.CARD;
        } else if(Const_PRODUCT.isPhotoCardProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.PHOTO_CARD;
        } else if(Const_PRODUCT.isNewWalletProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.WALLET_PHOTO;
        }else {
            return null;
        }
    }

    @Override
    protected boolean isSuccessSetSimpleDatas() {
        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder != null) {
            ArrayList<MyPhotoSelectImageData> postImageList = holder.getNormalData();
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

}
