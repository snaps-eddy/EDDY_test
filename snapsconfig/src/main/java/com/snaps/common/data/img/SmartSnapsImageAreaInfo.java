package com.snaps.common.data.img;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.imageloader.recoders.CropInfo;

import java.io.Serializable;


/**
 * Created by ysjeong on 2017. 9. 8..
 */

public class SmartSnapsImageAreaInfo implements Parcelable, Serializable {

    private static final long serialVersionUID = 2689582057474753717L;

    private BRect searchedAreaRect;
    private BSize uploadedImageSize;
    private BSize uploadedImageThumbnailSize;
    private int uploadedImageOrientation; //0 90 180 270 ....
    private int uploadedImageOrientationTag; // 1 2 3 4 ....

    private int searchedAreaCount = 0;
    private String dateInfo;
    private String jsonStrFromServer;

    private CropInfo.CORP_ORIENT cropOrientation;

    public SmartSnapsImageAreaInfo() {}

    public void set(SmartSnapsImageAreaInfo info) {
        if (info == null) return;

        this.searchedAreaRect = new BRect(info.searchedAreaRect);
        this.uploadedImageSize = new BSize(info.uploadedImageSize);
        this.uploadedImageThumbnailSize = new BSize(info.uploadedImageThumbnailSize);
        this.uploadedImageOrientation = info.uploadedImageOrientation;
        this.uploadedImageOrientationTag = info.uploadedImageOrientationTag;
        this.searchedAreaCount = info.searchedAreaCount;
        this.dateInfo = info.dateInfo;
        this.jsonStrFromServer = info.jsonStrFromServer;
    }

    protected SmartSnapsImageAreaInfo(Parcel in) {
        searchedAreaRect = in.readParcelable(BRect.class.getClassLoader());
        uploadedImageSize = in.readParcelable(BSize.class.getClassLoader());
        uploadedImageThumbnailSize = in.readParcelable(BSize.class.getClassLoader());
        uploadedImageOrientation = in.readInt();
        uploadedImageOrientationTag = in.readInt();
        searchedAreaCount = in.readInt();
        dateInfo = in.readString();
        jsonStrFromServer = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(searchedAreaRect, flags);
        dest.writeParcelable(uploadedImageSize, flags);
        dest.writeParcelable(uploadedImageThumbnailSize, flags);
        dest.writeInt(uploadedImageOrientation);
        dest.writeInt(uploadedImageOrientationTag);
        dest.writeInt(searchedAreaCount);
        dest.writeString(dateInfo);
        dest.writeString(jsonStrFromServer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SmartSnapsImageAreaInfo> CREATOR = new Creator<SmartSnapsImageAreaInfo>() {
        @Override
        public SmartSnapsImageAreaInfo createFromParcel(Parcel in) {
            return new SmartSnapsImageAreaInfo(in);
        }

        @Override
        public SmartSnapsImageAreaInfo[] newArray(int size) {
            return new SmartSnapsImageAreaInfo[size];
        }
    };

    public boolean isExistSearchedImageArea() {
        BRect searchedAreaRect = getSearchedAreaRect();
        return searchedAreaRect != null && searchedAreaRect.width() > 0 && searchedAreaRect.height() > 0;
    }

    public boolean isExistAllInfoInSmartImageArea() {
        BRect searchedAreaRect = getSearchedAreaRect();
        BSize uploadedThumbnailImageSize = getUploadedImageThumbnailSize();
        return uploadedThumbnailImageSize != null && uploadedThumbnailImageSize.getWidth() > 0 && uploadedThumbnailImageSize.getHeight() > 0
                && searchedAreaRect != null && searchedAreaRect.width() > 0 && searchedAreaRect.height() > 0;
    }


    public void calculateCropOrientation(SmartSnapsLayoutControlInfo layoutControlInfo) throws Exception {
        int imageWidth = getImageWidthInt(layoutControlInfo);
        int imageHeight = getImageHeightInt(layoutControlInfo);

//        if (isRotated(layoutControlInfo)) {
//            int temp = imageWidth;
//            imageWidth = imageHeight;
//            imageHeight = temp;
//        }

        float clipRectRatio = getClipRectWidthInt(layoutControlInfo) / (float) getClipRectHeightInt(layoutControlInfo);
        boolean isStandard = (imageWidth / (float)imageHeight) > clipRectRatio;
        this.cropOrientation = isStandard ? CropInfo.CORP_ORIENT.WIDTH : CropInfo.CORP_ORIENT.HEIGHT;
    }

    public int getSearchedAreaCount() {
        return searchedAreaCount;
    }

    public void setSearchedAreaCount(int searchedAreaCount) {
        this.searchedAreaCount = searchedAreaCount;
    }

    public String getDateInfo() {
        return dateInfo;
    }

    public void setDateInfo(String dateInfo) {
        this.dateInfo = dateInfo;
    }

    public boolean isRotated(SmartSnapsLayoutControlInfo layoutControlInfo) throws Exception {
        return getImageRotation(layoutControlInfo) == 90 || getImageRotation(layoutControlInfo) == 270;
    }

    public int getImageWidthInt(SmartSnapsLayoutControlInfo layoutControlInfo) throws Exception {
        return (int) layoutControlInfo.getImageSize().width;
    }

    public int getImageHeightInt(SmartSnapsLayoutControlInfo layoutControlInfo) throws Exception {
        return (int) layoutControlInfo.getImageSize().height;
    }

    public int getClipRectWidthInt(SmartSnapsLayoutControlInfo layoutControlInfo) throws Exception {
        return (int) layoutControlInfo.getClipRect().width();
    }

    public int getClipRectHeightInt(SmartSnapsLayoutControlInfo layoutControlInfo) throws Exception {
        return (int) layoutControlInfo.getClipRect().height();
    }

    public int getImageRotation(SmartSnapsLayoutControlInfo layoutControlInfo) throws Exception {
        return layoutControlInfo.getImageData().ROTATE_ANGLE;
    }

    public CropInfo.CORP_ORIENT getCropOrientation() {
        return cropOrientation;
    }

    public BRect getSearchedAreaRect() {
        return searchedAreaRect;
    }

    public void setSearchedAreaRect(BRect searchedAreaRect) {
        this.searchedAreaRect = searchedAreaRect;
    }

//    public BSize getUploadedImageSize() {
//        return uploadedImageSize;
//    }

    public void setUploadedImageSize(BSize uploadedImageSize) {
        this.uploadedImageSize = uploadedImageSize;
    }

    public BSize getUploadedImageThumbnailSize() {
        return uploadedImageThumbnailSize;
    }

    public void setUploadedImageThumbnailSize(BSize uploadedImageThumbnailSize) {
        this.uploadedImageThumbnailSize = uploadedImageThumbnailSize;
    }

    public int getUploadedImageOrientation() {
        return uploadedImageOrientation;
    }

    public void setUploadedImageOrientation(int ot) {
        this.uploadedImageOrientation = ot;
    }

    public int getUploadedImageOrientationTag() {
        return uploadedImageOrientationTag;
    }

    public void setUploadedImageOrientationTag(int uploadedImageOrientationTag) {
        this.uploadedImageOrientationTag = uploadedImageOrientationTag;
    }

    public BSize getFixedUploadedThumbnailImageSizeByOrientation() {
        if (getUploadedImageOrientation() == 90 || getUploadedImageOrientation() == 270) {
            BSize uploadedImageThumbnailSize = getUploadedImageThumbnailSize();
            BSize fixedImageSize = new BSize();
            if (uploadedImageThumbnailSize != null) {
                fixedImageSize.setWidth(uploadedImageThumbnailSize.getHeight());
                fixedImageSize.setHeight(uploadedImageThumbnailSize.getWidth());
            }
            return fixedImageSize;
        } else {
            return getUploadedImageThumbnailSize();
        }
    }

    public String getJsonStrFromServer() {
        return jsonStrFromServer;
    }

    public void setJsonStrFromServer(String jsonStrFromServer) {
        this.jsonStrFromServer = jsonStrFromServer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("searchedAreaCount:").append(searchedAreaCount);
        if (searchedAreaCount > 0) {
            sb.append(", ");
            sb.append("searchedAreaRect:").append("[");
            sb.append("left:").append(searchedAreaRect.left).append(", ");
            sb.append("top:").append(searchedAreaRect.top).append(", ");
            sb.append("right:").append(searchedAreaRect.right).append(", ");
            sb.append("bottom:").append(searchedAreaRect.top);
            sb.append("]");

        }
        return sb.toString();
    }
}
