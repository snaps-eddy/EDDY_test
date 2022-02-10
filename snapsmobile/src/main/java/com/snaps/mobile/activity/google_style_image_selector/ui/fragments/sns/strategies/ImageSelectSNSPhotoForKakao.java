package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.strategies;

import com.snaps.common.data.img.ImageSelectSNSImageData;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectNetworkPhotoAttribute;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectSNSData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectGetAlbumListListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectLoadPhotosListener;

import java.util.ArrayList;


/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class ImageSelectSNSPhotoForKakao extends ImageSelectSNSPhotoBase implements IKakao.ISNSPhotoHttpHandler {

    private IKakao kakao = null;

    public ImageSelectSNSPhotoForKakao(ImageSelectActivityV2 activity) {
        super(activity);
    }

    @Override
    public void initialize(ImageSelectSNSData snsData, IImageSelectLoadPhotosListener listener) {
        super.initialize(snsData, listener);

        if (snsData != null && snsData.getKakao() != null) {
            kakao = snsData.getKakao();
        } else {
            kakao = SnsFactory.getInstance().queryIntefaceKakao();
            if (snsData != null)
                snsData.setKakao(kakao);
        }
    }

    @Override
    public String getMapKey(ImageSelectSNSImageData imageData) {
        if (imageData == null) return "";
        return getSNSTypeCode() + "_" + imageData.getId();
    }

    @Override
    public String getObjectId(String url) {
        if (url == null) return "";
        return Integer.toString(url.hashCode());
    }

    @Override
    public void loadImageIfExistCreateAlbumList(IImageSelectGetAlbumListListener albumListListener) {
        if (albumListListener != null) albumListListener.onCreatedAlbumList(null);
    }

    @Override
    public int getSNSTypeCode() {
        return Const_VALUES.SELECT_KAKAO;
    }


    @Override
    public boolean isExistAlbumList() {
        return false;
    }

    @Override
    public int getTitleResId() {
        return R.string.kakaostory_photo;
    }

    @Override
    public void loadImage(ImageSelectNetworkPhotoAttribute attribute) {
        if (attribute == null || kakao == null) return;

        if (!isAliveNetwork()) {
            if (listener != null) {
                listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.NETWORK_ERROR);
            }
            return;
        }

        this.attribute = attribute;

        isFirstLoding = attribute.getPage() == 0;

        attribute.setNextKey(isFirstLoding ? "" : kakao.getLastID());

        if (isFirstLoding) {
            if (listener != null)
                listener.onLoadPhotoPreprare();
        }

        ArrayList<ImageSelectSNSImageData> dataList = new ArrayList<>();
        kakao.getRequestKakao(attribute.getNextKey(), 0, this, dataList);
    }

    @Override
    public void onSNSPhotoHttpResult(ArrayList<ImageSelectSNSImageData> result) {
        if (isSuspended) return;

        if (isFirstLoding && ((result == null || result.size() <= 0) && (adapter != null && adapter.getItemCount() <= 1))) {
            if (listener != null) {
                listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.EMPTY);
            }
            return;
        }

        if (result != null && result.size() > 0) {
            if (adapter != null)
                adapter.addAll(result);
            if (this.attribute != null) {
                attribute.setNextKey(kakao.getLastID());
            }
        } else {
            if (this.attribute != null) {
                this.attribute.setNextKey(null);
            }
        }

        if (listener != null)
            listener.onFinishedLoadPhoto(isFirstLoding ? IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.FIRST_LOAD_COMPLATED : IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.MORE_LOAD_COMPLATE);
    }

    @Override
    public void onSNSPhotoError(int errorCode, String errorMessage) {
        listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.EMPTY);
    }

}
