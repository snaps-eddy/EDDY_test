package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.strategies;

import android.net.Uri;

import com.snaps.common.data.img.ImageSelectSNSImageData;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.instagram.utils.instagram.InstagramApp;
import com.snaps.instagram.utils.instagram.InstagramImageData;
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

public class ImageSelectSNSPhotoForInstagram extends ImageSelectSNSPhotoBase {
    private static final String TAG = ImageSelectSNSPhotoForInstagram.class.getSimpleName();
    private InstagramApp insta;

    public ImageSelectSNSPhotoForInstagram(ImageSelectActivityV2 activity) {
        super(activity);
    }

    @Override
    public void initialize(ImageSelectSNSData snsData, IImageSelectLoadPhotosListener listener) {
        super.initialize(snsData, listener);

        if (snsData == null) return;

        insta = snsData.getInstagram(); //널이면 답 없다..그냥 종료 시켜야 겠다.
    }

    @Override
    public String getMapKey(ImageSelectSNSImageData imageData) {
        if (imageData == null) return "";
        return getSNSTypeCode() + "_" + imageData.getId();
    }

    @Override
    public String getObjectId(String url) {
        if (url == null) return "";
        return Uri.parse(url).getLastPathSegment();
    }

    @Override
    public void loadImageIfExistCreateAlbumList(IImageSelectGetAlbumListListener albumListListener) {
        if (albumListListener != null) albumListListener.onCreatedAlbumList(null);
    }

    @Override
    public int getSNSTypeCode() {
        return Const_VALUES.SELECT_INSTAGRAM;
    }


    @Override public boolean isExistAlbumList() {
        return false;
    }

    @Override
    public int getTitleResId() {
        return R.string.instagram_photo;
    }

    @Override
    public void loadImage(ImageSelectNetworkPhotoAttribute attribute) {
        isFirstLoding = attribute.getPage() == 0;

        if (!isAliveNetwork()) {
            if (listener != null) {
                listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.NETWORK_ERROR);
            }
            return;
        }

        ATask.executeVoid(new ATask.OnTask() {
            ArrayList<InstagramImageData> imageData = null;
            @Override
            public void onPre() {
                if (isFirstLoding) {
                    if (listener != null)
                        listener.onLoadPhotoPreprare();
                }
            }

            @Override
            public void onPost() {
                if (isSuspended) return;

                if( (imageData == null || imageData.size() < 1) && (adapter != null && adapter.getItemCount() <= 1)) {
                    if (listener != null) {
                        listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.EMPTY);
                    }
                    return;
                }

                try {
                    InstagramImageData data;
                    ArrayList<ImageSelectSNSImageData> result = new ArrayList<>();

                    for (int i = 0; i < imageData.size(); i++) {
                        if (isSuspended) return;

                        data = imageData.get(i);
                        if (data == null) continue;

                        String imgWidth = "" + data.standardResSize[0];
                        String imgHeight = "" + data.standardResSize[1];

                        ImageSelectSNSImageData snsImageData = new ImageSelectSNSImageData();
                        snsImageData.setId(data.id);
                        snsImageData.setOrgImageUrl(data.standardUrl);
                        snsImageData.setThumbnailImageUrl(data.lowUrl);
                        snsImageData.setlCreateAt(data.createdTime);
                        snsImageData.setOrgImageWidth(imgWidth);
                        snsImageData.setOrgImageHeight(imgHeight);
                        result.add(snsImageData);
                    }

                    if (adapter != null && result.size() > 0)
                        adapter.addAll(result);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }

                if (isSuspended) return;

                if (listener != null)
                    listener.onFinishedLoadPhoto(isFirstLoding ? IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.FIRST_LOAD_COMPLATED : IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.MORE_LOAD_COMPLATE);
            }

            @Override
            public void onBG() {
                if (insta != null)
                    imageData = insta.getPhotoUrlList();
            }
        });
    }
}
