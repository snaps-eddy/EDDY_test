package com.snaps.mobile.activity.google_style_image_selector.datas;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.utils.constant.Config;

import java.io.Serializable;

/**
 * Created by ysjeong on 2016. 12. 1..
 */

public class ImageSelectIntentData implements Serializable, Parcelable {
    private static final long serialVersionUID = -3604486043879044909L;

    private String diaryXMLPath = null;
    private String homeSelectProductCode = null;
    private String homeSelectKind = null;
    private String homeSelectPaperType = null;

    private String whereIs = null;
    private String themeSelectTemplate = null;

    private String webPaperCode = null;
    private String webSelectKind = null;
    private String webTitleKey = null;
    private String webStartDate = null;
    private String webEndDate = null;
    private String webPostCount = null;
    private String webCommentCount = null;

    private String webPhotoCount = null;
    private String webAnswerCount = null;

    private int homeSelectProduct = 0; //homeselect???
    private int recommendWidth = 0;
    private int recommendHeight = 0;
    private int pageIndex = 0;

    private boolean isOrientationChanged = false;
    private boolean isDiaryProfilePhoto = false;//DIARY_REQUEST_PROFILE_PHOTO_SELECT
    private boolean isComebackFromEditActivity = false;

    private SmartSnapsConstants.eSmartSnapsImageSelectType smartSnapsImageSelectType = SmartSnapsConstants.eSmartSnapsImageSelectType.NONE;

    private ImageSelectIntentData(Builder builder) {
        this.diaryXMLPath = builder.diaryXMLPath;
        this.homeSelectProductCode = builder.homeSelectProductCode;
        this.homeSelectKind = builder.homeSelectKind;
        this.homeSelectProduct = builder.homeSelectProduct;
        this.homeSelectPaperType = builder.homeSelectPaperType;
        this.isOrientationChanged = builder.isOrientationChanged;
        this.isDiaryProfilePhoto = builder.isDiaryProfilePhoto;
        this.whereIs = builder.whereIs;
        this.themeSelectTemplate = builder.themeSelectTemplate;
        this.recommendWidth = builder.recommendWidth;
        this.recommendHeight = builder.recommendHeight;

        this.webPaperCode = builder.webPaperCode;
        this.webSelectKind = builder.webSelectKind;
        this.webTitleKey = builder.webTitleKey;
        this.webStartDate = builder.webStartDate;
        this.webEndDate = builder.webEndDate;
        this.webPostCount = builder.webPostCount;
        this.webCommentCount = builder.webCommentCount;
        this.webPhotoCount = builder.webPhotoCount;
        this.webAnswerCount = builder.webAnswerCount;
        this.isComebackFromEditActivity = builder.isComebackFromEditActivity;
        this.smartSnapsImageSelectType = builder.smartSnapsImageSelectType;
        this.pageIndex = builder.pageIndex;
    }

    protected ImageSelectIntentData(Parcel in) {
        diaryXMLPath = in.readString();
        homeSelectProductCode = in.readString();
        homeSelectKind = in.readString();
        homeSelectPaperType = in.readString();
        whereIs = in.readString();
        themeSelectTemplate = in.readString();
        webPaperCode = in.readString();
        webSelectKind = in.readString();
        webTitleKey = in.readString();
        webStartDate = in.readString();
        webEndDate = in.readString();
        webPostCount = in.readString();
        webCommentCount = in.readString();
        webPhotoCount = in.readString();
        webAnswerCount = in.readString();
        homeSelectProduct = in.readInt();
        recommendWidth = in.readInt();
        recommendHeight = in.readInt();
        pageIndex = in.readInt();
        isOrientationChanged = in.readByte() != 0;
        isDiaryProfilePhoto = in.readByte() != 0;
        isComebackFromEditActivity = in.readByte() != 0;
        int smartSnapsImageSelectTypeOrdinal = in.readInt();
        smartSnapsImageSelectType = smartSnapsImageSelectTypeOrdinal >= 0 ? SmartSnapsConstants.eSmartSnapsImageSelectType.values()[smartSnapsImageSelectTypeOrdinal] : SmartSnapsConstants.eSmartSnapsImageSelectType.NONE;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(diaryXMLPath);
        dest.writeString(homeSelectProductCode);
        dest.writeString(homeSelectKind);
        dest.writeString(homeSelectPaperType);
        dest.writeString(whereIs);
        dest.writeString(themeSelectTemplate);
        dest.writeString(webPaperCode);
        dest.writeString(webSelectKind);
        dest.writeString(webTitleKey);
        dest.writeString(webStartDate);
        dest.writeString(webEndDate);
        dest.writeString(webPostCount);
        dest.writeString(webCommentCount);
        dest.writeString(webPhotoCount);
        dest.writeString(webAnswerCount);
        dest.writeInt(homeSelectProduct);
        dest.writeInt(recommendWidth);
        dest.writeInt(recommendHeight);
        dest.writeInt(pageIndex);
        dest.writeByte((byte) (isOrientationChanged ? 1 : 0));
        dest.writeByte((byte) (isDiaryProfilePhoto ? 1 : 0));
        dest.writeByte((byte) (isComebackFromEditActivity ? 1 : 0));

        int smartSnapsImageSelectTypeOrdinal = smartSnapsImageSelectType == null ? -1 : smartSnapsImageSelectType.ordinal();
        dest.writeInt(smartSnapsImageSelectTypeOrdinal);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageSelectIntentData> CREATOR = new Creator<ImageSelectIntentData>() {
        @Override
        public ImageSelectIntentData createFromParcel(Parcel in) {
            return new ImageSelectIntentData(in);
        }

        @Override
        public ImageSelectIntentData[] newArray(int size) {
            return new ImageSelectIntentData[size];
        }
    };

    public boolean isSinglePhotoChoose() {
        return getHomeSelectProduct() == Config.SELECT_SINGLE_CHOOSE_TYPE;
    }

    public boolean isMultiPhotoChoose() {
        return getHomeSelectProduct() == Config.SELECT_MULTI_CHOOSE_TYPE;
    }

    public String getWebPhotoCount() {
        return webPhotoCount;
    }

    public String getWebAnswerCount() {
        return webAnswerCount;
    }

    public String getWebPaperCode() {
        return webPaperCode;
    }

    public String getWebSelectKind() {
        return webSelectKind;
    }

    public String getWebTitleKey() {
        return webTitleKey;
    }

    public String getWebStartDate() {
        return webStartDate;
    }

    public String getWebEndDate() {
        return webEndDate;
    }

    public String getWebPostCount() {
        return webPostCount;
    }

    public String getWebCommentCount() {
        return webCommentCount;
    }

    public void setRecommendWidth(int recommendWidth) {
        this.recommendWidth = recommendWidth;
    }

    public void setRecommendHeight(int recommendHeight) {
        this.recommendHeight = recommendHeight;
    }

    public int getRecommendWidth() {
        return recommendWidth;
    }

    public int getRecommendHeight() {
        return recommendHeight;
    }

    public String getWhereIs() {
        return whereIs;
    }

    public String getThemeSelectTemplate() {
        return themeSelectTemplate;
    }

    public boolean isDiaryProfilePhoto() {
        return isDiaryProfilePhoto;
    }

    public String getHomeSelectPaperType() {
        return homeSelectPaperType;
    }

    public boolean isOrientationChanged() {
        return isOrientationChanged;
    }

    public String getDiaryXMLPath() {
        return diaryXMLPath;
    }

    public String getHomeSelectProductCode() {
        return homeSelectProductCode;
    }

    public String getHomeSelectKind() {
        return homeSelectKind;
    }

    public int getHomeSelectProduct() {
        return homeSelectProduct;
    }

    public boolean isComebackFromEditActivity() {
        return isComebackFromEditActivity;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public SmartSnapsConstants.eSmartSnapsImageSelectType getSmartSnapsImageSelectType() {
        return smartSnapsImageSelectType;
    }

    public static class Builder {
        private String diaryXMLPath = null;
        private String homeSelectProductCode = null;
        private String homeSelectKind = null;
        private String homeSelectPaperType = null;
        private String whereIs = null;
        private String themeSelectTemplate = null;

        private String webPaperCode = null;
        private String webSelectKind = null;
        private String webTitleKey = null;
        private String webStartDate = null;
        private String webEndDate = null;
        private String webPostCount = null;
        private String webCommentCount = null;
        private String webPhotoCount = null;
        private String webAnswerCount = null;

        private int homeSelectProduct = 0;
        private int recommendWidth = 0;
        private int recommendHeight = 0;
        private int pageIndex = 0;

        private boolean isOrientationChanged = false;
        private boolean isDiaryProfilePhoto = false;
        private boolean isComebackFromEditActivity = false;

        private SmartSnapsConstants.eSmartSnapsImageSelectType smartSnapsImageSelectType = SmartSnapsConstants.eSmartSnapsImageSelectType.NONE;

        public Builder setWebPhotoCount(String webPhotoCount) {
            this.webPhotoCount = webPhotoCount;
            return this;
        }

        public Builder setWebAnswerCount(String webAnswerCount) {
            this.webAnswerCount = webAnswerCount;
            return this;
        }

        public Builder setWebPaperCode(String webPaperCode) {
            this.webPaperCode = webPaperCode;
            return this;
        }

        public Builder setWebSelectKind(String webSelectKind) {
            this.webSelectKind = webSelectKind;
            return this;
        }

        public Builder setWebTitleKey(String webTitleKey) {
            this.webTitleKey = webTitleKey;
            return this;
        }

        public Builder setWebStartDate(String webStartDate) {
            this.webStartDate = webStartDate;
            return this;
        }

        public Builder setWebEndDate(String webEndDate) {
            this.webEndDate = webEndDate;
            return this;
        }

        public Builder setWebPostCount(String webPostCount) {
            this.webPostCount = webPostCount;
            return this;
        }

        public Builder setWebCommentCount(String webCommentCount) {
            this.webCommentCount = webCommentCount;
            return this;
        }

        public Builder setRecommendWidth(int recommendWidth) {
            this.recommendWidth = recommendWidth;
            return this;
        }

        public Builder setRecommendHeight(int recommendHeight) {
            this.recommendHeight = recommendHeight;
            return this;
        }

        public Builder setWhereIs(String whereIs) {
            this.whereIs = whereIs;
            return this;
        }

        public Builder setThemeSelectTemplate(String themeSelectTemplate) {
            this.themeSelectTemplate = themeSelectTemplate;
            return this;
        }

        public Builder setDiaryProfilePhoto(boolean diaryProfilePhoto) {
            isDiaryProfilePhoto = diaryProfilePhoto;
            return this;
        }

        public Builder setHomeSelectPaperType(String homeSelectPaperType) {
            this.homeSelectPaperType = homeSelectPaperType;
            return this;
        }

        public Builder setOrientationChanged(boolean orientationChanged) {
            isOrientationChanged = orientationChanged;
            return this;
        }

        public Builder setHomeSelectProductCode(String homeSelectProductCode) {
            this.homeSelectProductCode = homeSelectProductCode;
            return this;
        }

        public Builder setHomeSelectKind(String homeSelectKind) {
            this.homeSelectKind = homeSelectKind;
            return this;
        }

        public Builder setHomeSelectProduct(int homeSelectProduct) {
            this.homeSelectProduct = homeSelectProduct;
            return this;
        }

        public Builder setDiaryXMLPath(String diaryXMLPath) {
            this.diaryXMLPath = diaryXMLPath;
            return this;
        }

        public Builder setSmartSnapsImageSelectType(SmartSnapsConstants.eSmartSnapsImageSelectType smartSnapsImageSelectType) {
            this.smartSnapsImageSelectType = smartSnapsImageSelectType;
            return this;
        }

        public Builder setComebackFromEditActivity(boolean comebackFromEditActivity) {
            isComebackFromEditActivity = comebackFromEditActivity;
            return this;
        }

        public Builder setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
            return this;
        }

        public ImageSelectIntentData create() {
            return new ImageSelectIntentData(this);
        }
    }
}
