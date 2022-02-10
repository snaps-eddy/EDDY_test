package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.strategies;

import android.net.Uri;

import com.snaps.common.data.img.ImageSelectSNSImageData;
import com.snaps.common.data.img.MyNetworkAlbumData;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_StickerKit;
import com.snaps.common.utils.net.xml.bean.Xml_StickerKit_Album;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectNetworkPhotoAttribute;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectSNSData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectGetAlbumListListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectLoadPhotosListener;

import java.util.ArrayList;
import java.util.List;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;


/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class ImageSelectSNSPhotoForSnapsSticker extends ImageSelectSNSPhotoBase {

    public ImageSelectSNSPhotoForSnapsSticker(ImageSelectActivityV2 activity) {
        super(activity);
    }

    @Override
    public void initialize(ImageSelectSNSData snsData, IImageSelectLoadPhotosListener listener) {
        super.initialize(snsData, listener);
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
    public void loadImageIfExistCreateAlbumList(final IImageSelectGetAlbumListListener albumListListener) {
        ATask.executeVoidDefProgress(activity, new ATask.OnTask() {
            private ArrayList<IAlbumData> albumDataList = null;
            @Override
            public void onPre() {
            if (albumListListener != null)
                albumListListener.onPreprare();
            }

            @Override
            public void onPost() {
                if (albumListListener != null)
                    albumListListener.onCreatedAlbumList(albumDataList);
            }

            @Override
            public void onBG() {
                Xml_StickerKit_Album xmlStickerKitAlbum = GetParsedXml.getStickerKitAlbum(SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                if (xmlStickerKitAlbum == null) return;

                List<Xml_StickerKit_Album.StickerKitAlbumData> resultList = xmlStickerKitAlbum.stickerKitList;
                if (resultList == null) return;

                albumDataList = new ArrayList<>();
                for (Xml_StickerKit_Album.StickerKitAlbumData albumData : resultList) {
                    if (albumData == null) continue;
                    MyNetworkAlbumData data = new MyNetworkAlbumData();

                    data.ALBUM_ID = albumData.F_CATEGORY_CODE;
                    data.ALBUM_NAME = albumData.F_CATEGORY_NAME;
                    data.THUMBNAIL_IMAGE_URL = albumData.F_EIMG_PATH;
                    data.PHOTO_CNT = ""; //FIXME 없다....
                    albumDataList.add(data);
                }
            }
        });
    }

    @Override
    public int getSNSTypeCode() {
        return Const_VALUES.SELECT_SNAPS;
    }


    @Override public boolean isExistAlbumList() {
        return true;
    }

    @Override
    public int getTitleResId() {
        return R.string.snaps_sticker;
    }


    @Override
    public void loadImage(ImageSelectNetworkPhotoAttribute attri) {

        if (!isAliveNetwork()) {
            if (listener != null) {
                listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.NETWORK_ERROR);
            }
            return;
        }

        this.attribute = attri;
        isFirstLoding = attribute.getPage() == 0;

        ATask.executeVoid(new ATask.OnTask() {
            IAlbumData albumData = null;

            Xml_StickerKit xmlStickerKit = new Xml_StickerKit();

            @Override
            public void onPre() {
                if (isFirstLoding) {
                    if (listener != null)
                        listener.onLoadPhotoPreprare();
                }
            }

            @Override
            public void onBG() {
                if (ImageSelectSNSPhotoForSnapsSticker.this.attribute == null) return;
                albumData = ImageSelectSNSPhotoForSnapsSticker.this.attribute.getAlbumCursorInfo();
                if (albumData == null) return;

                xmlStickerKit = GetParsedXml.getStickerKit(albumData.getAlbumId(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            }

            @Override
            public void onPost() {
                if (isSuspended) return;

                if ((xmlStickerKit == null || xmlStickerKit.stickerKitList == null || xmlStickerKit.stickerKitList.size() < 1) && adapter != null && adapter.getItemCount() < 1) {
                    if (listener != null) {
                        listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.EMPTY);
                        return;
                    }
                }

                if (xmlStickerKit == null) return;

                List<Xml_StickerKit.StickerKitData> arStickerDataList  = xmlStickerKit.stickerKitList;
                if (arStickerDataList == null) return;

                ArrayList<ImageSelectSNSImageData> snsImageList = new ArrayList<>();
                for (Xml_StickerKit.StickerKitData stickerKitData : arStickerDataList) {

                    if (isSuspended) return;

                    if (stickerKitData == null) continue;
                    ImageSelectSNSImageData imageData = new ImageSelectSNSImageData();
                    imageData.setId(stickerKitData.F_RSRC_CODE);
                    imageData.setOrgImageUrl(stickerKitData.F_EIMG_PATH);
                    imageData.setThumbnailImageUrl(stickerKitData.F_DIMG_PATH);
                    imageData.setOrgImageWidth("100");
                    imageData.setOrgImageHeight("100");
                    imageData.setStrCreateAt("");

                    snsImageList.add(imageData);

                }

                if (adapter != null && snsImageList.size() > 0) {

                    adapter.addAll(snsImageList);
                }

                if (isSuspended) return;

                if (listener != null)
                    listener.onFinishedLoadPhoto(IImageSelectLoadPhotosListener.eIMAGE_LOAD_RESULT_TYPE.FIRST_LOAD_COMPLATED);
            }
        });
    }

    @Override
    public void setBaseAlbumIfExistAlbumList(ArrayList<IAlbumData> list) {
        super.setBaseAlbumIfExistAlbumList(list);
    }
}
