package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.ImageEdgeValidation;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.NoticeUnqualifiedImageDialog;
import com.snaps.common.utils.ui.SwordMan;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

import java.util.ArrayList;

import font.FProgressDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

import static android.app.Activity.RESULT_OK;

public class ImageSelectPerformForAcrylicProduct extends BaseImageSelectPerformer implements IImageSelectProductPerform {

    private static final String TAG = ImageSelectPerformForAcrylicProduct.class.getSimpleName();

    public ImageSelectPerformForAcrylicProduct(ImageSelectActivityV2 activity) {
        super(activity);
        // preference에 SINGLE_CHOOSE_IMAGE_KEY 값을 넣는데, ImageSelectActivity 가 정상종료 되지 않을 경우 초기화 해주는 코드가 없어서 버그 발생
        Setting.set(activity, ISnapsImageSelectConstants.SINGLE_CHOOSE_IMAGE_KEY, "");
    }

    @Override
    public ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT performGetDefaultFragmentType() {
        return ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.SELECT_IMAGE_SRC;
    }

    @Override
    public void onClickedNextBtn() {
        if (imageSelectActivity == null) return;

        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder == null) {
            MessageUtil.toast(imageSelectActivity, R.string.select_nocount);
            return;
        }

        ArrayList<MyPhotoSelectImageData> imageDatas = holder.getNormalData();
        if (imageDatas == null || imageDatas.size() <= 0) {
            MessageUtil.toast(imageSelectActivity, R.string.select_nocount);
            return;
        }

        FProgressDialog progressDialog = new FProgressDialog(imageSelectActivity);
        progressDialog.setCancelable(false);
        progressDialog.show();

        MyPhotoSelectImageData imgData = imageDatas.get(0);
        SwordMan swordMan = new SwordMan();

        Disposable disposable = swordMan.validateMakingAcrylicKeyring(imageSelectActivity, imgData.PATH)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEvent((aBoolean, throwable) -> progressDialog.dismiss())
                .subscribeWith(new DisposableSingleObserver<ImageEdgeValidation>() {
                    @Override
                    public void onSuccess(ImageEdgeValidation imageEdgeValidation) {

                        if (imageEdgeValidation.isJpeg()) {
                            if (imageEdgeValidation.isOverMinimumSize()) {
                                showAlertMakingJpeg(imgData);
                            } else {
                                showAlertDisableMakingMinimumSize();
                            }
                            return;
                        }

                        if (!imageEdgeValidation.isOnlyOneEdge()) {
                            NoticeUnqualifiedImageDialog dialog = new NoticeUnqualifiedImageDialog(imageSelectActivity);
                            dialog.setNoticeImage(imageEdgeValidation);
                            dialog.setCancelable(true);
                            dialog.show();
                            return;
                        }

                        if (!imageEdgeValidation.isOverMinimumSize()) {
                            showAlertDisableMakingMinimumSize();
                            return;
                        }

                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("imgData", imgData);
                        intent.putExtras(bundle);

                        imageSelectActivity.setResult(RESULT_OK, intent);
                        imageSelectActivity.finish();
                        dispose();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Dlog.e(TAG, e);
                        dispose();
                    }
                });
        Dlog.d("Disposeable : " + disposable.isDisposed());
    }

    private void showAlertMakingJpeg(MyPhotoSelectImageData imgData) {
        MessageUtil.alert(imageSelectActivity, R.string.making_keyring_jpeg_alert, clickedOk -> {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("imgData", imgData);
            intent.putExtras(bundle);

            imageSelectActivity.setResult(RESULT_OK, intent);
            imageSelectActivity.finish();
        });
    }

    private void showAlertDisableMakingMinimumSize() {
        MessageUtil.alert(imageSelectActivity,
                Const_PRODUCT.isAcrylicKeyringProduct() ?
                        R.string.making_keyring_minimum_size_alert :
                        R.string.making_acrylic_stand_minimum_size_alert);
    }

    @Override
    public void moveNextActivity() {
    }
}
