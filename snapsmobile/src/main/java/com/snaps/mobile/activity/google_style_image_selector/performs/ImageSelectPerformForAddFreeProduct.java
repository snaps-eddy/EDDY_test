package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;

import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectTrayPageCountInfo;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants.EXTRA_NAME_ADD_PAGE_INDEX_LIST;

/**
 * Created by ysjeong on 2016. 12. 2..
 */

public class ImageSelectPerformForAddFreeProduct extends BaseImageSelectPerformer implements IImageSelectProductPerform {
    public ImageSelectPerformForAddFreeProduct(ImageSelectActivityV2 activity) {
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
        if (isSuccessSetSimpleDatas()) {
            Intent saveIntent = new Intent(imageSelectActivity, SnapsEditActivity.class);
            SnapsProductEditConstants.eSnapsProductKind productKind = getProductKind();
            if (productKind == null) return;
            saveIntent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, productKind.ordinal());
            saveIntent.putExtra("templete", TEMPLATE_PATH);

            ArrayList<Integer> addPageIndexList = ImageSelectUtils.getAddPageIdxs();
            if (addPageIndexList != null && !addPageIndexList.isEmpty()) {
                saveIntent.putExtra(EXTRA_NAME_ADD_PAGE_INDEX_LIST, addPageIndexList);
            }

            imageSelectActivity.startActivity(saveIntent);
            imageSelectActivity.finish();
        }
    }

    private SnapsProductEditConstants.eSnapsProductKind getProductKind() {
        if (Const_PRODUCT.isDIYStickerProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.DIY_STICKER;
        } else if (Const_PRODUCT.isTransparencyPhotoCardProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.TRANSPARENCY_PHOTO_CARD;
        } else if (Const_PRODUCT.isPosterGroupProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.POSTER;
        } else if (Const_PRODUCT.isLongPhotoStickerProduct() || Const_PRODUCT.isStikerGroupProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.STICKER;
        } else if (Const_PRODUCT.isCardProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.CARD;
        } else if (Const_PRODUCT.isPhotoCardProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.PHOTO_CARD;
        } else if (Const_PRODUCT.isNewWalletProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.WALLET_PHOTO;
        } else if (Const_PRODUCT.isAccordionCardProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.ACCORDION_CARD;
        } else if (Const_PRODUCT.isSealStickerProduct()) {
            return SnapsProductEditConstants.eSnapsProductKind.SEAL_STICKER;
        } else {
            return null;
        }
    }
}
