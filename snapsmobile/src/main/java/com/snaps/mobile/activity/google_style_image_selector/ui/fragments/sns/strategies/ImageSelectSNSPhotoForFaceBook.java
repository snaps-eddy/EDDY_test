package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.strategies;

import android.net.Uri;

import com.snaps.common.data.img.ImageSelectSNSImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectNetworkPhotoAttribute;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectSNSData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectGetAlbumListListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectLoadPhotosListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class ImageSelectSNSPhotoForFaceBook extends ImageSelectSNSPhotoBase implements IFacebook.OnPaging {
    private static final String TAG = ImageSelectSNSPhotoForFaceBook.class.getSimpleName();
    private static final int MAX_FACEBOOK_IMG_COUNT = 32;// 페이스북에서 한번에 읽을 갯수

    private IFacebook facebook = null;

    public ImageSelectSNSPhotoForFaceBook(ImageSelectActivityV2 activity) {
        super(activity);
    }

    @Override
    public void initialize(ImageSelectSNSData snsData, IImageSelectLoadPhotosListener listener) {
        super.initialize(snsData, listener);

        if (snsData != null && snsData.getFacebook() != null) {
            facebook = snsData.getFacebook();
        } else {
            facebook = SnsFactory.getInstance().queryInteface();
            facebook.init(activity);

            if (snsData != null)
                snsData.setFacebook(facebook);
        }
    }


    @Override
    public boolean isExistAlbumList() {
        return false;
    }

    @Override
    public String getMapKey(ImageSelectSNSImageData imageData) {
        if (imageData == null) return "";
        return getSNSTypeCode() + "_" + imageData.getId();
    }

    @Override
    public void loadImageIfExistCreateAlbumList(IImageSelectGetAlbumListListener albumListListener) {
        if (albumListListener != null) albumListListener.onCreatedAlbumList(null);
    }

    @Override
    public int getSNSTypeCode() {
        return Const_VALUES.SELECT_FACEBOOK;
    }

    @Override
    public String getObjectId(String url) {
        if (url == null) return null;
        return Uri.parse(url).getLastPathSegment();
    }

    @Override
    public int getTitleResId() {
        return R.string.facebook_photo;
    }

    @Override
    public void loadImage(ImageSelectNetworkPhotoAttribute attribute) {
        if (!Config.isFacebookService() || attribute == null) return;

        if (!isAliveNetwork()) {
            if (listener != null) {
                listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.NETWORK_ERROR);
            }
            return;
        }

        this.attribute = attribute;
        isFirstLoding = attribute.getPage() == 0;

        if (isFirstLoding) {
            if (listener != null)
                listener.onLoadPhotoPreprare();
        }

        if (facebook != null)
            facebook.facebookGetPhotos(activity, attribute.getNextKey(), MAX_FACEBOOK_IMG_COUNT, this);
    }

    @Override
    public void onPagingComplete(JSONObject jsonObj) {
        if (isSuspended) return;

        if (jsonObj == null) {
            if (listener != null) {
                if (adapter != null && adapter.getItemCount() == 0)
                    listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.EMPTY);
                else
                    listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.NO_MORE);
            }
            return;
        }

        try {

            ArrayList<ImageSelectSNSImageData> snsImageList = new ArrayList<>();

            JSONArray dataArrays = jsonObj.optJSONArray("data");

            if (isSuspended) return;

            if ((dataArrays == null || dataArrays.length() == 0)) {
                if (listener != null) {
                    if (adapter != null && adapter.getItemCount() == 0)
                        listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.EMPTY);
                    else
                        listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.NO_MORE);
                    return;
                }
            }

            if (attribute != null)
                attribute.setNextKey(null);

            if (!jsonObj.isNull("paging")) {
                JSONObject pagingObj = (JSONObject) jsonObj.get("paging");
                if (pagingObj != null && !pagingObj.isNull("next")) {
                    Object nextObj = pagingObj.get("next");
                    if (nextObj != null) {
                        JSONObject cursorsObj = (JSONObject) pagingObj.get("cursors");
                        if (cursorsObj != null) {
                            Object nextKey = cursorsObj.get("after");
                            if (nextKey != null) {
                                if (attribute != null)
                                    attribute.setNextKey((String) nextKey);
                            }
                        }
                    }
                }
            }

            boolean isMoreImg = attribute != null && (attribute.getNextKey() != null) && (attribute.getNextKey().length() > 0);

            JSONObject arrayObject = null;
            for (int i = 0; i < dataArrays.length(); i++) {
                ImageSelectSNSImageData image = new ImageSelectSNSImageData();
                arrayObject = dataArrays.optJSONObject(i);

                image.setId(arrayObject.optString("id", ""));
                image.setlCreateAt(StringUtil.getFBDatetoLong(activity, arrayObject.optString("created_time"))); // 2015-05-13T01:40:31+0000

                JSONArray imgArrays = arrayObject.optJSONArray("images");
                if (imgArrays != null && imgArrays.length() > 0) {
                    JSONObject orgImgObj = imgArrays.optJSONObject(0);
                    JSONObject thumbImgObj = imgArrays.optJSONObject(imgArrays.length() - 1);

                    if (orgImgObj != null) {
                        image.setOrgImageUrl(orgImgObj.getString("source"));
                        image.setOrgImageWidth(orgImgObj.optString("width", ""));
                        image.setOrgImageHeight(orgImgObj.optString("height", ""));
                    }

                    if (thumbImgObj != null) {
                        image.setThumbnailImageUrl(thumbImgObj.getString("source"));
                    }
                }

                snsImageList.add(image);

                loadedCount++;
            }

            if (isMoreImg && loadedCount < MAX_FACEBOOK_IMG_COUNT) {
                if (listener != null)
                    listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.REQUEST_MORE_LOAD);
                return;
            }


            if (adapter != null && snsImageList.size() > 0)
                adapter.addAll(snsImageList);

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        if (isSuspended) return;

        if (listener != null) {
            listener.onFinishedLoadPhoto(isFirstLoding
                    ? IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.FIRST_LOAD_COMPLATED
                    : IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.MORE_LOAD_COMPLATE);
        }
    }
}
