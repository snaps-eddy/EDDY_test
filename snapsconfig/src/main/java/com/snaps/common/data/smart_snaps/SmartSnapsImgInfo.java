package com.snaps.common.data.smart_snaps;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.data.img.BSize;
import com.snaps.common.data.img.SmartSnapsImageAreaInfo;
import com.snaps.common.data.smart_snaps.interfacies.ISmartSnapImgDataAnimationState;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;

import java.io.Serializable;

/**
 * Created by ysjeong on 2018. 1. 16..
 */

public class SmartSnapsImgInfo implements Parcelable, Serializable {
    private static final String TAG = SmartSnapsImgInfo.class.getSimpleName();
    private static final long serialVersionUID = -7265760220580741669L;
    private int page = -1;
    private SmartSnapsConstants.eSmartSnapsImgState smartSnapsImgState = SmartSnapsConstants.eSmartSnapsImgState.NONE;

    private SmartSnapsImageAreaInfo smartSnapsImageAreaInfo;

    private boolean isFailedSearchFace = false; //얼굴 검색 실패 데이터
    private String searchFaceFailedMsg = "";

    private transient ISmartSnapImgDataAnimationState smartSnapImgDataAnimationStateListener = null;    //TODO ... 이것도 serialize..

    public SmartSnapsImgInfo(SmartSnapsImgInfo info) {
        if (info == null) return;
        set(info);
    }

    public void set(SmartSnapsImgInfo info) {
        page = info.page;
        smartSnapsImgState = info.smartSnapsImgState;
        smartSnapsImageAreaInfo = new SmartSnapsImageAreaInfo();
        smartSnapsImageAreaInfo.set(info.smartSnapsImageAreaInfo);
        isFailedSearchFace = info.isFailedSearchFace;
        searchFaceFailedMsg = info.searchFaceFailedMsg;
        smartSnapImgDataAnimationStateListener = info.smartSnapImgDataAnimationStateListener;
    }

    public static SmartSnapsImgInfo createImgInfo(int page) {
        return new SmartSnapsImgInfo(page);
    }

    public static SmartSnapsImgInfo createImgInfoWithSmartSnapsSaveXmlImageInfo(SmartSnapsSaveXmlImageInfo smartSnapsSaveXmlImageInfo) {
        if (smartSnapsSaveXmlImageInfo == null) return null;
        try {
            SmartSnapsImgInfo smartSnapsImgInfo = new SmartSnapsImgInfo(smartSnapsSaveXmlImageInfo.getPageIdx());

            String imageAnalysis = smartSnapsSaveXmlImageInfo.getImgAnalysis();
            String orientation = smartSnapsSaveXmlImageInfo.getOrientation();

            if (!StringUtil.isEmpty(imageAnalysis) && !StringUtil.isEmpty(orientation)) {
                int x = 0, y = 0, xw = 0, yh = 0;
                float tw = 0, th = 0;
                String[] arOffset = imageAnalysis.substring(0, imageAnalysis.indexOf(".")).split(",");
                if (arOffset.length == 4) {
                    x = Integer.parseInt(arOffset[0]); //x
                    y = Integer.parseInt(arOffset[1]); //y
                    xw = Integer.parseInt(arOffset[2]); //w
                    yh = Integer.parseInt(arOffset[3]); //h
                }

                String[] arThumbnail = imageAnalysis.substring(imageAnalysis.indexOf(".")+1, imageAnalysis.length()).split(",");
                if (arThumbnail.length == 2) {
                    tw = Float.parseFloat(arThumbnail[0]); //w
                    th = Float.parseFloat(arThumbnail[1]); //h
                }

                SmartSnapsImageAreaInfo imageAreaInfo = new SmartSnapsImageAreaInfo();
                com.snaps.common.data.img.BRect searchAreaRect = new com.snaps.common.data.img.BRect();
                searchAreaRect.set(x, y, x + xw, y + yh);
                imageAreaInfo.setSearchedAreaRect(searchAreaRect);

                imageAreaInfo.setUploadedImageThumbnailSize(new BSize(tw, th));

                int orientationTag = (int)Float.parseFloat(orientation);

                imageAreaInfo.setUploadedImageOrientationTag(orientationTag);

                imageAreaInfo.setUploadedImageOrientation(convertOtToAngle(orientationTag));

                smartSnapsImgInfo.setSmartSnapsImageAreaInfo(imageAreaInfo);
            }

            smartSnapsImgInfo.smartSnapsImgState = SmartSnapsConstants.eSmartSnapsImgState.FINISH_ANIMATION;

            return smartSnapsImgInfo;
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        }
    }

    public static int convertOtToAngle(int ot) {
        switch (ot) {
            case 3:
            case 4:
                return 180;
            case 5:
            case 6:
                return 90;
            case 7:
            case 8:
                return 270;
        }
        return 0;
    }

    private SmartSnapsImgInfo(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public SmartSnapsConstants.eSmartSnapsImgState getSmartSnapsImgState() {
        return smartSnapsImgState;
    }

    public void setSmartSnapsImgState(SmartSnapsConstants.eSmartSnapsImgState smartSnapsImgState) {
        this.smartSnapsImgState = smartSnapsImgState;
    }

    public SmartSnapsImageAreaInfo getSmartSnapsImageAreaInfo() {
        return smartSnapsImageAreaInfo;
    }

    public void setSmartSnapsImageAreaInfo(SmartSnapsImageAreaInfo smartSnapsImageAreaInfo) {
        this.smartSnapsImageAreaInfo = smartSnapsImageAreaInfo;
    }

    public String getSearchFaceFailedMsg() {
        return searchFaceFailedMsg;
    }

    public void setSearchFaceFailedMsg(String searchFaceFailedMsg) {
        this.searchFaceFailedMsg = searchFaceFailedMsg;
    }

    public boolean isFailedSearchFace() {
        return isFailedSearchFace;
    }

    public void setFailedSearchFace(boolean failedSearchFace) {
        isFailedSearchFace = failedSearchFace;
    }

    public ISmartSnapImgDataAnimationState getSmartSnapImgDataAnimationStateListener() {
        return smartSnapImgDataAnimationStateListener;
    }

    public void setSmartSnapImgDataAnimationStateListener(ISmartSnapImgDataAnimationState smartSnapImgDataAnimationStateListener) {
        this.smartSnapImgDataAnimationStateListener = smartSnapImgDataAnimationStateListener;
    }

    public void removeAnimationStateListener() {
        smartSnapImgDataAnimationStateListener = null;
    }

    protected SmartSnapsImgInfo(Parcel in) {
        page = in.readInt();
        smartSnapsImageAreaInfo = in.readParcelable(SmartSnapsImageAreaInfo.class.getClassLoader());
        isFailedSearchFace = in.readByte() != 0;
        searchFaceFailedMsg = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(page);
        dest.writeParcelable(smartSnapsImageAreaInfo, flags);
        dest.writeByte((byte) (isFailedSearchFace ? 1 : 0));
        dest.writeString(searchFaceFailedMsg);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SmartSnapsImgInfo> CREATOR = new Creator<SmartSnapsImgInfo>() {
        @Override
        public SmartSnapsImgInfo createFromParcel(Parcel in) {
            return new SmartSnapsImgInfo(in);
        }

        @Override
        public SmartSnapsImgInfo[] newArray(int size) {
            return new SmartSnapsImgInfo[size];
        }
    };
}
