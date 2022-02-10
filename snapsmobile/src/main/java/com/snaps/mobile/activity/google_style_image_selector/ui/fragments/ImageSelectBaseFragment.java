package com.snaps.mobile.activity.google_style_image_selector.ui.fragments;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.CustomFragment;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectAlbumHandler;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectFragmentItemClickListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectListUpdateListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectPublicMethods;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectStateChangedListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.component.SnapsNoPrintDialog;

import font.FProgressDialog;
import font.FTextView;

/**
 * Created by ysjeong on 2016. 12. 2..
 */

public abstract class ImageSelectBaseFragment extends CustomFragment implements IImageSelectListUpdateListener, IImageSelectAlbumHandler, IImageSelectFragmentItemClickListener {
    protected IImageSelectStateChangedListener itemStateChangedListener = null;

    protected ImageSelectActivityV2 imageSelectActivityV2;

    protected FProgressDialog progress = null;

    //Network error or empty
    protected View lyErrorView = null;
    protected ImageView ivErrorImg = null;
    protected FTextView tvErrorText = null;
    protected FTextView tvErrorTextSub = null;
    protected FTextView tvErrorRetryBtn = null;

    public void setItemStateChangedListener(IImageSelectStateChangedListener itemStateChangedListener) {
        this.itemStateChangedListener = itemStateChangedListener;
    }

    /**
     * 대부분이 멀티도 아니고 싱글도 아님.
     * AI 추천 포토북이 멀티인듯 -> 정확히는 모르겠다
     *
     * @param vh
     */
    @Override
    public void onClickFragmentItem(final ImageSelectAdapterHolders.PhotoFragmentItemHolder vh) {
        if (vh == null || imageSelectActivityV2 == null) return;

        if (imageSelectActivityV2.isSingleChooseType()) {

            if (vh.isEnableMimeType() == false) {
                showNotAllowedMineTypeDialog(imageSelectActivityV2);
                imageSelectActivityV2.onItemUnSelectedListener(IImageSelectStateChangedListener.eCONTROL_TYPE.FRAGMENT, vh.getMapKey());
                return;
            }

            String singleChooseImageKey = Setting.getString(imageSelectActivityV2, ISnapsImageSelectConstants.SINGLE_CHOOSE_IMAGE_KEY);

            if (vh.isDisableClick()) {
                if (Config.TEST_TUTORIAL || !Setting.getBoolean(imageSelectActivityV2, "noprint")) {
                    SnapsNoPrintDialog dialog = new SnapsNoPrintDialog(imageSelectActivityV2);
                    dialog.show();
                }
                //return;
            }

            if (singleChooseImageKey.equalsIgnoreCase("")) {
                chooseSingleImage(vh);

            } else {
                if (ImageSelectUtils.isContainsInImageHolder(vh.getMapKey())) {
                    imageSelectActivityV2.onItemUnSelectedListener(IImageSelectStateChangedListener.eCONTROL_TYPE.FRAGMENT, vh.getMapKey());
                    Setting.set(imageSelectActivityV2, ISnapsImageSelectConstants.SINGLE_CHOOSE_IMAGE_KEY, "");

                } else if (ImageSelectUtils.isContainsInImageHolder(singleChooseImageKey)) {
                    imageSelectActivityV2.onItemUnSelectedListener(IImageSelectStateChangedListener.eCONTROL_TYPE.FRAGMENT, singleChooseImageKey);
                    chooseSingleImage(vh);
                }
            }
        } else if (imageSelectActivityV2.isMultiChooseType()) {
            if (ImageSelectUtils.isContainsInImageHolder(vh.getMapKey())) {
                imageSelectActivityV2.onItemUnSelectedListener(IImageSelectStateChangedListener.eCONTROL_TYPE.FRAGMENT, vh.getMapKey());
            } else {
                if (vh.isEnableMimeType() == false) {
                    showNotAllowedMineTypeDialog(imageSelectActivityV2);

                } else if (vh.isDisableClick()) {
                    if (Config.TEST_TUTORIAL || !Setting.getBoolean(imageSelectActivityV2, "noprint")) {
                        SnapsNoPrintDialog dialog = new SnapsNoPrintDialog(imageSelectActivityV2, () -> setAddImage(vh));
                        dialog.show();
                    } else {
                        setAddImage(vh);
                    }
                } else {
                    setAddImage(vh);
                }
            }
        } else {
            if (ImageSelectUtils.isContainsInImageHolder(vh.getMapKey())) {
                imageSelectActivityV2.onItemUnSelectedListener(IImageSelectStateChangedListener.eCONTROL_TYPE.FRAGMENT, vh.getMapKey());
            } else {

                if (vh.isEnableMimeType() == false) {
                    showNotAllowedMineTypeDialog(imageSelectActivityV2);

                } else if (vh.isDisableClick()) {
                    if (Config.TEST_TUTORIAL || !Setting.getBoolean(imageSelectActivityV2, "noprint")) {
                        SnapsNoPrintDialog dialog = new SnapsNoPrintDialog(imageSelectActivityV2, () -> setAddImage(vh));
                        dialog.show();
                    } else {
                        setAddImage(vh);
                    }
                } else {
                    setAddImage(vh);
                }
            }
        }
    }

    private void setAddImage(ImageSelectAdapterHolders.PhotoFragmentItemHolder vh) {
        if (imageSelectActivityV2.isAddableImage()) {
            if (itemStateChangedListener != null)
                itemStateChangedListener.onFragmentItemSelected(vh);
        } else {
            ImageSelectUtils.showDisableAddPhotoMsg(imageSelectActivityV2);
        }
    }

    protected void showProgress(boolean isShowMsg) {
        if (imageSelectActivityV2 == null) return;

        if (progress == null) {
            progress = new FProgressDialog(imageSelectActivityV2);
            if (isShowMsg)
                progress.setMessage(getString(R.string.please_wait));
            progress.setCancelable(false);
        }

        progress.show();
    }

    protected void chooseSingleImage(ImageSelectAdapterHolders.PhotoFragmentItemHolder holder) {
        imageSelectActivityV2.putSelectedImageData(holder.getMapKey(), holder.getImgData());
        Setting.set(imageSelectActivityV2, ISnapsImageSelectConstants.SINGLE_CHOOSE_IMAGE_KEY, holder.getMapKey());
    }

    protected void dismissedFDialog() {
        if (progress != null)
            progress.dismiss();
        progress = null;
    }

    protected void setEmptyUIState(IImageSelectPublicMethods.ePHOTO_LIST_ERR_TYPE errType) {
        if (errType == null || lyErrorView == null || ivErrorImg == null || tvErrorText == null || tvErrorTextSub == null || tvErrorRetryBtn == null)
            return;

        switch (errType) {
            case PHOTO_LIST_NETWORK_ERR:
                ivErrorImg.setImageResource(R.drawable.wifi);
                tvErrorText.setText(R.string.network_err_msg_title);
                tvErrorTextSub.setText(R.string.network_err_msg_sub);
                lyErrorView.setVisibility(View.VISIBLE);
                tvErrorRetryBtn.setVisibility(View.VISIBLE);
                break;
            case PHOTO_LIST_EMPTY:
                ivErrorImg.setImageResource(R.drawable.icon_photo_list_empty);
                tvErrorText.setText(R.string.not_photo);
                tvErrorTextSub.setText("");
                lyErrorView.setVisibility(View.VISIBLE);
                tvErrorRetryBtn.setVisibility(View.GONE);
                break;
            case NONE:
            default:
                lyErrorView.setVisibility(View.GONE);
                break;
        }
    }

    private void showNotAllowedMineTypeDialog(Activity activity) {
        MessageUtil.alert(activity, R.string.not_supported_file_format);
    }
}
