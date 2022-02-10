package com.snaps.common.data.img;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.snaps.common.data.smart_snaps.SmartSnapsImgInfo;
import com.snaps.common.data.smart_snaps.interfacies.ISmartSnapImgDataAnimationState;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsXML;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.imageloader.filters.ImageEffectBitmap.EffectType;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo.CropImageRect;
import com.snaps.common.utils.imageloader.recoders.BaseCropInfo;
import com.snaps.common.utils.imageloader.recoders.CropInfo;
import com.snaps.common.utils.imageloader.recoders.CropInfo.CORP_ORIENT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.DateUtil;
import com.snaps.common.utils.ui.StringUtil;

import org.xml.sax.Attributes;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyPhotoSelectImageData implements Parcelable, Serializable {
	private static final String TAG = MyPhotoSelectImageData.class.getSimpleName();

	public static int INVALID_ROTATE_ANGLE = -999;
	/**
	 * 
	 */
	private static final long serialVersionUID = 7234109387488444236L;

	// imageData와 매칭시키기 위한 키
	private double imageDataKey = 0d;

	// --- Parcel 객체에 포함되어 Intent에 담겨 Activity 사이를 이동하게 될 값들 ---
	public String SELECTED = "false";
	public int KIND = 0;
	public long IMAGE_ID = 0;
	public String PATH = "";
	public String THUMBNAIL_PATH = "";
	public String LOCAL_THUMBNAIL_PATH = "";
	public String EFFECT_PATH = "";
	public String EFFECT_THUMBNAIL_PATH = "";

	public String ORIGINAL_PATH = ""; // 파일 업로드 후 원본 URL
	public int FREE_ANGLE = 0;// 패닝 동작에 의해 회전된 자유 회전각도
	public int ORIGINAL_ROTATE_ANGLE = INVALID_ROTATE_ANGLE;// 초기화를 위해 초기 각도를 저장 해 둠.
	public int ORIGINAL_THUMB_ROTATE_ANGLE = INVALID_ROTATE_ANGLE;// 초기화를 위해 초기 각도를 저장 해 둠.
	public int ROTATE_ANGLE = 0;// 원본 이미지를 기준으로 rotation된 전체각도
	public int ROTATE_ANGLE_THUMB = -1;// UIL에 의해 선택된 이미지는 자동으로 rotation 되어있고, 이
										// 상태에서 사용자가 rotation을 했을 때 추가적으로
										// rotation을 해야할 값.
	public int RESTORE_ANGLE = INVALID_ROTATE_ANGLE;
	public String DATE_TIME = "";

	public String F_IMG_YEAR = "";
	public String F_IMG_SQNC = "";
	public String F_UPLOAD_PATH = "";

	public String F_PIC_NO = "";
	public String F_FLDR_CODE = "";
	public String F_MIMG_PATH = "";
	public String F_SSIMG_PATH = "";
	public String F_IMG_NAME = "";
	public String F_IMG_WIDTH = "";
	public String F_IMG_HEIGHT = "";
	public long F_IMG_FILESIZE = 0;

	// Crop 내용
	public CropInfo CROP_INFO = new CropInfo();

	// 크기 조절이 자유로운 Crop
	public AdjustableCropInfo ADJ_CROP_INFO = new AdjustableCropInfo();

	// 카카오북 내용
	public String KAKAOBOOK_DATE = "";// 카카오북용 날짜
	public String KAKAOBOOK_CONTENT = "";// 카카오북용 내용
	
	// 페이스북 포토북 내용
	public String FACEBOOK_PHOTOBOOK_DATE = "";// 페이스북 포토북 날짜
	public String FACEBOOK_PHOTOBOOK_CONTENT = "";// 페이스북 포토북 내용

	// 페북북 내용
	public String FB_OBJECT_ID = "";
	public MyFacebookData FB_DATA = new MyFacebookData();

	// --- Parcel 객체에 포함되지 않고, 해당 데이터 또는 화면에 도움이 되는 값들 ---
	public int selectIdx = 0;// 삭제 시 사용할 이미지 idx
	public boolean isDelete = false;// 삭제모드여부
	public String YEAR_KEY = "";
	public String SQNC_KEY = "";
	public long photoTakenDateTime;

	// 테마북때문에 추가됨..
	public int IMG_IDX = -1;
	public int isModify = -1; // 이미지의 영역 수정여부.. -1이 아니면 수정된 이미지...
	public double cropRatio = 0.0f; // 이미지마다 크롭비율이 다르기 때문에... 커버에 들어가는 사진과 속지와
									// 다르기 때문데..
	public int pageIDX = -1; // 몇페이지에 있는 이미지인지 확인..

	// 심플포토북 사진선택시 애니메이션 끝나고 이미지가 들어가야할 위치 설정.
	public int insertIDX = -1;

	public boolean isAdjustableCropMode = false;

	// 효과가 적용 되었는 지에 따라..
	public boolean isApplyEffect = false;

	public boolean isNoPrint = false;
	public boolean isUploadFailedOrgImage = false;
	public String EFFECT_TYPE = EffectType.ORIGIN.toString();

	public float mmPageWidth = 0;
	public int pxPageWidth = 0;
	public String controlWidth = "";

	public int screenWidth = 0;
	public int screenHeight = 0;

	public int imgAlpha = 100; // 0 ~ 100

	public String editorOrientation = "";

	public boolean isCheckedOldEditInfo = false;
	public boolean isTriedRecoveryEffectFilterFile = false; //캐시가 없는 효과 적용 사진을 만들려고 시도 했는지

	private AtomicBoolean isUploading = new AtomicBoolean(false);
	private ImageUploadSyncLocker uploadSyncLocker = new ImageUploadSyncLocker();

	// 사진인화 수량 확인용으로 추가 저장하지는 않는다
	public int photoPrintCount = 1; // 기본은 1장..

	private SmartSnapsImgInfo smartSnapsImgInfo = null;
	private int uploadPriority = 0; //높을 수록 먼저 올린다
	private int sortPriority = 0; //높을 수록 앞쪽에 위치 시킨다..
	private SmartSnapsConstants.eSmartSnapsAnalysisImagePageType pageType = SmartSnapsConstants.eSmartSnapsAnalysisImagePageType.PAGE; //cover, title, page

	private ExifUtil.SnapsExifInfo exifInfo = null;

	public String mineType = null;

	/**
	 * TODO **************** 멤버 변수를 추가할 때는 set() 메서드에도 복사가 되도록 변수 추가를 해야 함. ***********************
	 */
	public MyPhotoSelectImageData() {}

	public MyPhotoSelectImageData(int kIND, String pATH) {
		super();
		KIND = kIND;
		PATH = pATH;
	}

	public void set(MyPhotoSelectImageData imageData) {
		if (imageData == null)
			return;

		imageDataKey = imageData.imageDataKey;

		// --- Parcel 객체에 포함되어 ent에 담겨 Activity 사이를 이동하게 될 값들 ---
		SELECTED = imageData.SELECTED;
		KIND = imageData.KIND;
		IMAGE_ID = imageData.IMAGE_ID;
		PATH = imageData.PATH;
		THUMBNAIL_PATH = imageData.THUMBNAIL_PATH;
		LOCAL_THUMBNAIL_PATH = imageData.LOCAL_THUMBNAIL_PATH;
		EFFECT_PATH = imageData.EFFECT_PATH;
		EFFECT_THUMBNAIL_PATH = imageData.EFFECT_THUMBNAIL_PATH;

		ORIGINAL_PATH = imageData.ORIGINAL_PATH; // 파일 업로드 후 원본 URL
		FREE_ANGLE = imageData.FREE_ANGLE;// 패닝 동작에 의해 회전된 자유 회전각도
		ORIGINAL_ROTATE_ANGLE = imageData.ORIGINAL_ROTATE_ANGLE;
		ORIGINAL_THUMB_ROTATE_ANGLE = imageData.ORIGINAL_THUMB_ROTATE_ANGLE;
		ROTATE_ANGLE = imageData.ROTATE_ANGLE;// 원본 이미지를 기준으로 rotation된 전체각도
		ROTATE_ANGLE_THUMB = imageData.ROTATE_ANGLE_THUMB;// UIL에 의해 선택된 이미지는 자동으로 rotation 되어있고, 이 상태에서
		// 사용자가 rotation을 했을 때 추가적으로 rotation을 해야할 값.
		RESTORE_ANGLE = imageData.RESTORE_ANGLE;
		DATE_TIME = imageData.DATE_TIME;

		F_IMG_YEAR = imageData.F_IMG_YEAR;
		F_IMG_SQNC = imageData.F_IMG_SQNC;
		F_UPLOAD_PATH = imageData.F_UPLOAD_PATH;

		F_PIC_NO = imageData.F_PIC_NO;
		F_FLDR_CODE = imageData.F_FLDR_CODE;
		F_MIMG_PATH = imageData.F_MIMG_PATH;
		F_SSIMG_PATH = imageData.F_SSIMG_PATH;
		F_IMG_NAME = imageData.F_IMG_NAME;
		F_IMG_WIDTH = imageData.F_IMG_WIDTH;
		F_IMG_HEIGHT = imageData.F_IMG_HEIGHT;
		F_IMG_FILESIZE = imageData.F_IMG_FILESIZE;

		// 카카오북 내용
		KAKAOBOOK_DATE = imageData.KAKAOBOOK_DATE;// 카카오북용 날짜
		KAKAOBOOK_CONTENT = imageData.KAKAOBOOK_CONTENT;// 카카오북용 내용

		// 페북북 내용
		FB_OBJECT_ID = imageData.FB_OBJECT_ID;

		// --- Parcel 객체에 포함되지 않고, 해당 데이터 또는 화면에 도움이 되는 값들 ---
		selectIdx = imageData.selectIdx;// 삭제 시 사용할 이미지 idx
		isDelete = imageData.isDelete;// 삭제모드여부
		YEAR_KEY = imageData.YEAR_KEY;
		SQNC_KEY = imageData.SQNC_KEY;
		photoTakenDateTime = imageData.photoTakenDateTime;

		// 테마북때문에 추가됨..
		IMG_IDX = imageData.IMG_IDX;
		isModify = imageData.isModify;
		cropRatio = imageData.cropRatio;
		pageIDX = imageData.pageIDX;

		// 심플포토북 사진선택시 애니메이션 끝나고 이미지가 들어가야할 위치 설정.
		insertIDX = imageData.insertIDX;

		isAdjustableCropMode = imageData.isAdjustableCropMode;

		// 효과가 적용 되었는 지에 따라..
		isApplyEffect = imageData.isApplyEffect;

		isNoPrint = imageData.isNoPrint;
		isUploadFailedOrgImage = imageData.isUploadFailedOrgImage;

		mmPageWidth = imageData.mmPageWidth;
		pxPageWidth = imageData.pxPageWidth;
		controlWidth = imageData.controlWidth;

		screenWidth = imageData.screenWidth;
		screenHeight = imageData.screenHeight;

		imgAlpha = imageData.imgAlpha; // 0 ~ 100
		EFFECT_TYPE = imageData.EFFECT_TYPE;

		MyFacebookData fbDataObject = imageData.FB_DATA;
		if (fbDataObject != null) {
			FB_DATA = new MyFacebookData();
			FB_DATA.set(fbDataObject);
		}

		CropInfo cropInfoObject = imageData.CROP_INFO;
		if (cropInfoObject != null) {
			CROP_INFO = new CropInfo();
			CROP_INFO.set(cropInfoObject);
		}

		AdjustableCropInfo adjCropInfoObject = imageData.ADJ_CROP_INFO;
		if (adjCropInfoObject != null) {
			ADJ_CROP_INFO = new AdjustableCropInfo();
			ADJ_CROP_INFO.set(adjCropInfoObject);
		}

		editorOrientation = imageData.editorOrientation;
		isCheckedOldEditInfo = imageData.isCheckedOldEditInfo;
		isTriedRecoveryEffectFilterFile = imageData.isTriedRecoveryEffectFilterFile;
		photoPrintCount = imageData.photoPrintCount;
		smartSnapsImgInfo = imageData.smartSnapsImgInfo;
		exifInfo = imageData.exifInfo;

		uploadPriority = imageData.uploadPriority;
		sortPriority = imageData.sortPriority;
		pageType = imageData.pageType;
		mineType = imageData.mineType;
	}

	public void weakCopy(MyPhotoSelectImageData imageData) {
		if (imageData == null)
			return;

		// 테마북때문에 추가됨..
//		public int IMG_IDX = -1;
//		public int pageIDX = -1; // 몇페이지에 있는 이미지인지 확인..
		KIND = imageData.KIND;
		IMAGE_ID = imageData.IMAGE_ID;
		F_IMG_NAME = imageData.F_IMG_NAME;
		PATH = imageData.PATH;
		THUMBNAIL_PATH = imageData.THUMBNAIL_PATH;
		ORIGINAL_PATH = imageData.ORIGINAL_PATH;

		ROTATE_ANGLE = imageData.ORIGINAL_ROTATE_ANGLE == MyPhotoSelectImageData.INVALID_ROTATE_ANGLE ? imageData.ROTATE_ANGLE : imageData.ORIGINAL_ROTATE_ANGLE;
		ROTATE_ANGLE_THUMB = imageData.ORIGINAL_THUMB_ROTATE_ANGLE == MyPhotoSelectImageData.INVALID_ROTATE_ANGLE ? imageData.ROTATE_ANGLE_THUMB : imageData.ORIGINAL_THUMB_ROTATE_ANGLE;

		F_IMG_WIDTH = imageData.F_IMG_WIDTH;
		F_IMG_HEIGHT = imageData.F_IMG_HEIGHT;
		IMG_IDX = imageData.IMG_IDX;
		smartSnapsImgInfo = new SmartSnapsImgInfo(imageData.smartSnapsImgInfo);
		photoTakenDateTime = imageData.photoTakenDateTime;
		imgAlpha = imageData.imgAlpha;

//		if (KIND == Const_VALUES.SELECT_PHONE) {
//			LOCAL_THUMBNAIL_PATH = imageData.LOCAL_THUMBNAIL_PATH;
//			THUMBNAIL_PATH = imageData.LOCAL_THUMBNAIL_PATH;
			DATE_TIME = imageData.DATE_TIME;
			F_IMG_FILESIZE = imageData.F_IMG_FILESIZE;

			exifInfo = imageData.exifInfo;
//		}

		F_IMG_YEAR = imageData.F_IMG_YEAR;
		F_IMG_SQNC = imageData.F_IMG_SQNC;
		F_UPLOAD_PATH = imageData.F_UPLOAD_PATH;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {// !! Parcel 객체에 write 하는
														// 순서는 read 하는 순서와 같아야
														// 함. !!
		dest.writeString(SELECTED);
		dest.writeInt(KIND);
		dest.writeLong(IMAGE_ID);
		dest.writeString(PATH);
		dest.writeString(THUMBNAIL_PATH);
		dest.writeString(LOCAL_THUMBNAIL_PATH);
		dest.writeString(EFFECT_PATH);
		dest.writeString(EFFECT_THUMBNAIL_PATH);

		dest.writeInt(FREE_ANGLE);
		dest.writeInt(ORIGINAL_ROTATE_ANGLE);
		dest.writeInt(ORIGINAL_THUMB_ROTATE_ANGLE);
		dest.writeInt(ROTATE_ANGLE);
		dest.writeInt(ROTATE_ANGLE_THUMB);
		dest.writeString(DATE_TIME);

		dest.writeString(F_IMG_YEAR);
		dest.writeString(F_IMG_SQNC);
		dest.writeString(F_UPLOAD_PATH);

		dest.writeString(F_PIC_NO);
		dest.writeString(F_FLDR_CODE);
		dest.writeString(F_MIMG_PATH);
		dest.writeString(F_SSIMG_PATH);
		dest.writeString(F_IMG_NAME);
		dest.writeString(F_IMG_WIDTH);
		dest.writeString(F_IMG_HEIGHT);
		dest.writeLong(F_IMG_FILESIZE);

		dest.writeParcelable(CROP_INFO, 0);
		dest.writeParcelable(ADJ_CROP_INFO, 0);

		dest.writeString(KAKAOBOOK_DATE);
		dest.writeString(KAKAOBOOK_CONTENT);

		dest.writeInt(IMG_IDX);
		dest.writeInt(isModify);
		dest.writeDouble(cropRatio);
		dest.writeInt(pageIDX);

		dest.writeLong(photoTakenDateTime);

		dest.writeString(ORIGINAL_PATH);

		dest.writeString(FB_OBJECT_ID);
		dest.writeParcelable(FB_DATA, 0);

		boolean[] arrBool = { isAdjustableCropMode, isApplyEffect, isNoPrint, isUploadFailedOrgImage, isCheckedOldEditInfo, isTriedRecoveryEffectFilterFile, isUploading.get() };
		dest.writeBooleanArray(arrBool);
		dest.writeString(EFFECT_TYPE);

		dest.writeFloat(mmPageWidth);
		dest.writeInt(pxPageWidth);
		dest.writeString(controlWidth);
		dest.writeInt(screenWidth);
		dest.writeInt(screenHeight);

		dest.writeInt(imgAlpha);
		dest.writeString(editorOrientation);
		dest.writeParcelable(uploadSyncLocker, 0);

		dest.writeParcelable(smartSnapsImgInfo, 0);
		dest.writeParcelable(exifInfo, 0);
		dest.writeInt(uploadPriority);
		dest.writeInt(sortPriority);
		dest.writeInt(pageType != null ? pageType.ordinal() : 0);
		dest.writeString(mineType);
	}

	private void readFromParcel(Parcel in) {// !! Parcel 객체에 write 하는 순서는 read
											// 하는 순서와 같아야 함. !!
		SELECTED = in.readString();
		KIND = in.readInt();
		IMAGE_ID = in.readLong();
		PATH = in.readString();
		THUMBNAIL_PATH = in.readString();
		LOCAL_THUMBNAIL_PATH = in.readString();
		EFFECT_PATH = in.readString();
		EFFECT_THUMBNAIL_PATH = in.readString();

		FREE_ANGLE = in.readInt();
		ORIGINAL_ROTATE_ANGLE = in.readInt();
		ORIGINAL_THUMB_ROTATE_ANGLE = in.readInt();
		ROTATE_ANGLE = in.readInt();
		ROTATE_ANGLE_THUMB = in.readInt();
		DATE_TIME = in.readString();

		F_IMG_YEAR = in.readString();
		F_IMG_SQNC = in.readString();
		F_UPLOAD_PATH = in.readString();

		F_PIC_NO = in.readString();
		F_FLDR_CODE = in.readString();
		F_MIMG_PATH = in.readString();
		F_SSIMG_PATH = in.readString();
		F_IMG_NAME = in.readString();
		F_IMG_WIDTH = in.readString();
		F_IMG_HEIGHT = in.readString();
		F_IMG_FILESIZE = in.readLong();

		CROP_INFO = in.readParcelable(CropInfo.class.getClassLoader());
		ADJ_CROP_INFO = in.readParcelable(AdjustableCropInfo.class.getClassLoader());

		KAKAOBOOK_DATE = in.readString();
		KAKAOBOOK_CONTENT = in.readString();

		IMG_IDX = in.readInt();
		isModify = in.readInt();
		cropRatio = in.readDouble();
		pageIDX = in.readInt();

		photoTakenDateTime = in.readLong();

		ORIGINAL_PATH = in.readString();

		FB_OBJECT_ID = in.readString();
		FB_DATA = in.readParcelable(MyFacebookData.class.getClassLoader());
		boolean[] arrBool = new boolean[7];
		in.readBooleanArray(arrBool);
		isAdjustableCropMode = arrBool[0];
		isApplyEffect = arrBool[1];
		isNoPrint = arrBool[2];
		isUploadFailedOrgImage = arrBool[3];
		isCheckedOldEditInfo = arrBool[4];
		isTriedRecoveryEffectFilterFile = arrBool[5];
		isUploading = new AtomicBoolean(arrBool[6]);

		EFFECT_TYPE = in.readString();

		mmPageWidth = in.readFloat();
		pxPageWidth = in.readInt();
		controlWidth = in.readString();
		screenWidth = in.readInt();
		screenHeight = in.readInt();

		imgAlpha = in.readInt();
		editorOrientation = in.readString();
		uploadSyncLocker = in.readParcelable(ImageUploadSyncLocker.class.getClassLoader());
		smartSnapsImgInfo = in.readParcelable(SmartSnapsImgInfo.class.getClassLoader());
		exifInfo = in.readParcelable(ExifUtil.SnapsExifInfo.class.getClassLoader());
		uploadPriority = in.readInt();
		sortPriority = in.readInt();

		int pageTypeOrdinal = in.readInt();
		pageType = SmartSnapsConstants.eSmartSnapsAnalysisImagePageType.values()[pageTypeOrdinal];
		mineType = in.readString();
	}

	public MyPhotoSelectImageData(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		@Override
		public MyPhotoSelectImageData createFromParcel(Parcel in) {
			return new MyPhotoSelectImageData(in);
		}

		@Override
		public MyPhotoSelectImageData[] newArray(int size) {
			return new MyPhotoSelectImageData[size];
		}
	};

	public void writeSmartSnapsImageInfo(SnapsXML xml) {
		try {
			if (xml == null || getSmartSnapsImageAreaInfo() == null) return;

			SmartSnapsImageAreaInfo smartSnapsImageAreaInfo = getSmartSnapsImageAreaInfo();
			xml.attribute(null, "orientation", String.valueOf(smartSnapsImageAreaInfo.getUploadedImageOrientationTag()));

			BRect searchedRect = smartSnapsImageAreaInfo.getSearchedAreaRect();
			if (searchedRect != null) {
				StringBuilder analysisTextBuilder = new StringBuilder();
				analysisTextBuilder.append(searchedRect.left).append(",").append(searchedRect.top).append(",").append(searchedRect.width()).append(",").append(searchedRect.height()).append(".");
				BSize thumbnailSize = smartSnapsImageAreaInfo.getUploadedImageThumbnailSize();
				analysisTextBuilder.append(thumbnailSize.width).append(",").append(thumbnailSize.height);
				xml.attribute(null, "imgAnalysis", analysisTextBuilder.toString());
			}

			xml.attribute(null, "lastModified", String.valueOf(photoTakenDateTime));
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public SnapsXML getSaveXML(SnapsXML xml, boolean isAddTag) {
		if (isAddTag)
			xml.startTag(null, "MyImageData");
		xml.attribute(null, "startPos", String.valueOf(CROP_INFO.startPercent));
		xml.attribute(null, "endPos", String.valueOf(CROP_INFO.endPercent));
		xml.attribute(null, "movePos", String.valueOf(CROP_INFO.movePercent));
		xml.attribute(null, "cropOrient", String.valueOf(CROP_INFO.cropOrient.ordinal()));

		// 이미지 정보 저장..
		xml.attribute(null, "imgSeq", F_IMG_SQNC);
		xml.attribute(null, "imageID", String.valueOf(IMAGE_ID));
		xml.attribute(null, "uploadPath", StringUtil.convertEmojiUniCodeToAlias(F_UPLOAD_PATH));
		xml.attribute(null, "img_width", F_IMG_WIDTH);
		xml.attribute(null, "img_height", F_IMG_HEIGHT);
		xml.attribute(null, "thumbImgPath", StringUtil.convertEmojiUniCodeToAlias(THUMBNAIL_PATH));
		xml.attribute(null, "local_thumbImgPath", StringUtil.convertEmojiUniCodeToAlias(LOCAL_THUMBNAIL_PATH));
		xml.attribute(null, "imgKind", String.valueOf(KIND));
		xml.attribute(null, "imgYear", F_IMG_YEAR);
//		xml.attribute(null, "fileName", StringUtil.convertEmojiUniCodeToAlias(F_IMG_NAME));

		/** 파일 경로에 이모지가 들어 있다면, XML parser에서 에러가 발생해서 쓰지 않는다. **/
		String imgName = StringUtil.removeInvalidXMLChar(StringUtil.convertEmojiUniCodeToAlias(F_IMG_NAME));
		if (StringUtil.isContainsEmoji(imgName)) {
			xml.attribute(null, "fileName", "");
		} else {
			xml.attribute(null, "fileName", imgName);
		}

		String imgPath = StringUtil.convertEmojiUniCodeToAlias(PATH);
		if (StringUtil.isContainsEmoji(imgPath)) {
			xml.attribute(null, "img_path", "");
		} else {
			xml.attribute(null, "img_path", imgPath);
		}

		xml.attribute(null, "oriImgPath", StringUtil.convertEmojiUniCodeToAlias(ORIGINAL_PATH));
		xml.attribute(null, "img_angle", ROTATE_ANGLE + "");
		xml.attribute(null, "img_thumbAngle", ROTATE_ANGLE_THUMB + "");

		// 이미지 편집 적용된 내용
		xml.attribute(null, "effect_path", StringUtil.removeInvalidXMLChar(StringUtil.convertEmojiUniCodeToAlias(EFFECT_PATH)));
		xml.attribute(null, "effect_thumbnail_path", StringUtil.removeInvalidXMLChar(StringUtil.convertEmojiUniCodeToAlias(EFFECT_THUMBNAIL_PATH)));
		xml.attribute(null, "effect_free_angle", FREE_ANGLE + "");
		xml.attribute(null, "effect_original_rotate_angle", ORIGINAL_ROTATE_ANGLE + "");
		xml.attribute(null, "effect_original_thumb_rotate_angle", ORIGINAL_THUMB_ROTATE_ANGLE + "");
		xml.attribute(null, "isAdjustableCropMode", isAdjustableCropMode + "");
		xml.attribute(null, "isApplyEffect", isApplyEffect + "");
		xml.attribute(null, "isScaleable", isNoPrint + "");
		xml.attribute(null, "isUploadFailedOrgImage", isUploadFailedOrgImage + "");
		xml.attribute(null, "isCheckedOldEdInfo", isCheckedOldEditInfo + "");
		xml.attribute(null, "effect_type", EFFECT_TYPE + "");

		if (ADJ_CROP_INFO != null) {
			CropImageRect imgRect = ADJ_CROP_INFO.getImgRect();
			if (imgRect != null) {
				xml.attribute(null, "adj_img_rect_resWidth", imgRect.resWidth + "");
				xml.attribute(null, "adj_img_rect_resHeight", imgRect.resHeight + "");
				xml.attribute(null, "adj_img_rect_width", imgRect.width + "");
				xml.attribute(null, "adj_img_rect_height", imgRect.height + "");
				xml.attribute(null, "adj_img_rect_centerX", imgRect.centerX + "");
				xml.attribute(null, "adj_img_rect_centerY", imgRect.centerY + "");
				xml.attribute(null, "adj_img_rect_rotate", imgRect.rotate + "");
				xml.attribute(null, "adj_img_rect_angle", imgRect.angle + "");
				xml.attribute(null, "adj_img_rect_scaleX", imgRect.scaleX + "");
				xml.attribute(null, "adj_img_rect_scaleY", imgRect.scaleY + "");
				xml.attribute(null, "adj_img_rect_movedX", imgRect.movedX + "");
				xml.attribute(null, "adj_img_rect_movedY", imgRect.movedY + "");

				if (imgRect.matrixValue != null) {
					for (int ii = 0; ii < imgRect.matrixValue.length; ii++) {
						xml.attribute(null, "adj_img_rect_matrix_" + ii, imgRect.matrixValue[ii] + "");
					}
				}
			}

			CropImageRect clipRect = ADJ_CROP_INFO.getClipRect();
			if (clipRect != null) {
				xml.attribute(null, "adj_clip_rect_resWidth", clipRect.resWidth + "");
				xml.attribute(null, "adj_clip_rect_resHeight", clipRect.resHeight + "");
				xml.attribute(null, "adj_clip_rect_width", clipRect.width + "");
				xml.attribute(null, "adj_clip_rect_height", clipRect.height + "");
				xml.attribute(null, "adj_clip_rect_centerX", clipRect.centerX + "");
				xml.attribute(null, "adj_clip_rect_centerY", clipRect.centerY + "");
				xml.attribute(null, "adj_clip_rect_rotate", clipRect.rotate + "");
				xml.attribute(null, "adj_clip_rect_angle", clipRect.angle + "");
				xml.attribute(null, "adj_clip_rect_scaleX", clipRect.scaleX + "");
				xml.attribute(null, "adj_clip_rect_scaleY", clipRect.scaleY + "");
				xml.attribute(null, "adj_clip_rect_movedX", clipRect.movedX + "");
				xml.attribute(null, "adj_clip_rect_movedY", clipRect.movedY + "");

				if (clipRect.matrixValue != null) {
					for (int ii = 0; ii < clipRect.matrixValue.length; ii++) {
						xml.attribute(null, "adj_clip_rect_matrix_" + ii, clipRect.matrixValue[ii] + "");
					}
				}
			}

			xml.attribute(null, "isCropped", ADJ_CROP_INFO.isCropped() + "");
			xml.attribute(null, "mmPageWidth", mmPageWidth + "");
			xml.attribute(null, "pxPageWidth", pxPageWidth + "");
			xml.attribute(null, "controlWidth", controlWidth + "");
			xml.attribute(null, "screenWidth", screenWidth + "");
			xml.attribute(null, "screenHeight", screenHeight + "");

			xml.attribute(null, "imgAlpha", imgAlpha + "");

			xml.attribute(null, "editorOrientation", editorOrientation);

			xml.attribute(null, "photoTakenTime", photoTakenDateTime + "");
            xml.attribute(null, "mineType", mineType);
		}

		if (isAddTag)
			xml.endTag(null, "MyImageData");
		return xml;
	}

	public boolean isEditedImage() {
		return ADJ_CROP_INFO != null && ADJ_CROP_INFO.isCropped();
	}

	public void setSmartSnapsImageAreaInfo(SmartSnapsImageAreaInfo smartSnapsImageAreaInfo) {
		if (getSmartSnapsImgInfo() != null)
			getSmartSnapsImgInfo().setSmartSnapsImageAreaInfo(smartSnapsImageAreaInfo);
	}

	public SmartSnapsImageAreaInfo getSmartSnapsImageAreaInfo() {
		return getSmartSnapsImgInfo() != null ? getSmartSnapsImgInfo().getSmartSnapsImageAreaInfo() : null;
	}

	public int getSmartSnapsSearchedFaceCount() {
		return getSmartSnapsImageAreaInfo() != null ? getSmartSnapsImageAreaInfo().getSearchedAreaCount() : 0;
	}

	public SmartSnapsConstants.eSmartSnapsAnalysisImagePageType getPageType() {
		return pageType;
	}

	public void setPageType(SmartSnapsConstants.eSmartSnapsAnalysisImagePageType pageType) {
		this.pageType = pageType;
	}

	public String getSafetyThumbnailPath() {
		return StringUtil.convertEmojiUniCodeToAlias(!StringUtil.isEmpty(THUMBNAIL_PATH) ? THUMBNAIL_PATH : (!StringUtil.isEmpty(PATH) ? PATH : ORIGINAL_PATH));
	}

	public String getFdThumbnailJsonStr() {
		if (getSmartSnapsImageAreaInfo() == null) return "";
		String json = getSmartSnapsImageAreaInfo().getJsonStrFromServer();
		if (StringUtil.isEmpty(json) || !json.startsWith("{")) return "";
		return json;
	}

	static public float parseStrToFloat(String str) {
		if (str == null || str.trim().length() < 1)
			return 0.f;
		try {
			return Float.parseFloat(str);
		} catch (NumberFormatException e) {
			Dlog.e(TAG, e);
		}
		return 0.f;
	}

	static public long parseStrToLong(String str) {
		if (str == null || str.trim().length() < 1)
			return 0;
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			Dlog.e(TAG, e);
		}
		return 0;
	}

	static public MyPhotoSelectImageData xmlToMyPhotoSeletImageData(Attributes attributes) {
		MyPhotoSelectImageData d = new MyPhotoSelectImageData();
		d.F_IMG_SQNC = getValue(attributes, "imgSeq");

		String imageId = getValue(attributes, "imageID");
		if (!StringUtil.isEmpty(imageId)) {
			try {
				d.IMAGE_ID = Integer.parseInt(imageId);
			} catch (NumberFormatException e) {
				Dlog.e(TAG, e);
			}
		}

		d.F_UPLOAD_PATH = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "uploadPath"));
		d.F_IMG_WIDTH = getValue(attributes, "img_width");
		d.F_IMG_HEIGHT = getValue(attributes, "img_height");
		d.THUMBNAIL_PATH = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "thumbImgPath"));
		d.LOCAL_THUMBNAIL_PATH = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "local_thumbImgPath"));
		d.KIND = getValue(attributes, "imgKind").equalsIgnoreCase("") ? -1 : Integer.parseInt(getValue(attributes, "imgKind"));
		d.F_IMG_YEAR = getValue(attributes, "imgYear");
		d.F_IMG_NAME = getValue(attributes, "fileName");
		d.PATH = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "img_path"));

		d.ORIGINAL_PATH = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "oriImgPath"));
		d.ROTATE_ANGLE = Integer.parseInt(getValue(attributes, "img_angle"));
		d.ROTATE_ANGLE_THUMB = Integer.parseInt(getValue(attributes, "img_thumbAngle"));

		int cropOrient = Integer.parseInt(getValue(attributes, "cropOrient"));
		float movePercent = parseStrToFloat(getValue(attributes, "movePos"));
		int startPos = Integer.parseInt(getValue(attributes, "startPos"));
		int endPos = Integer.parseInt(getValue(attributes, "endPos"));

		CORP_ORIENT[] ori = CORP_ORIENT.values();
		CropInfo c = new CropInfo(ori[cropOrient], movePercent, startPos, endPos);
		d.CROP_INFO = c;
		// 테마북인 경우에만 1000.f으로 한다 정밀도 향상...
		if (com.snaps.common.utils.constant.Config.isSnapsSticker())
			d.CROP_INFO.CROP_ACCURACY = 100.f;
		else
			d.CROP_INFO.CROP_ACCURACY = 1000.f;

		// 이미지 편집 적용된 내용
		try {
			d.EFFECT_PATH = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "effect_path"));
			d.EFFECT_THUMBNAIL_PATH = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "effect_thumbnail_path"));
			d.EFFECT_TYPE = getValue(attributes, "effect_type");

			String szFreeAngle = getValue(attributes, "effect_free_angle");
			if (szFreeAngle != null && szFreeAngle.length() > 0)
				d.FREE_ANGLE = Integer.parseInt(szFreeAngle);

			String szOriginAngle = getValue(attributes, "effect_original_rotate_angle");
			if (szOriginAngle != null && szOriginAngle.length() > 0)
				d.ORIGINAL_ROTATE_ANGLE = Integer.parseInt(szOriginAngle);

			String szOriginThumbAngle = getValue(attributes, "effect_original_thumb_rotate_angle");
			if (szOriginThumbAngle != null && szOriginThumbAngle.length() > 0)
				d.ORIGINAL_THUMB_ROTATE_ANGLE = Integer.parseInt(szOriginThumbAngle);

			d.isAdjustableCropMode = parseBoolean(getValue(attributes, "isAdjustableCropMode"));
			d.isApplyEffect = parseBoolean(getValue(attributes, "isApplyEffect"));
			d.isNoPrint = parseBoolean(getValue(attributes, "isScaleable"));
			d.isUploadFailedOrgImage = parseBoolean(getValue(attributes, "isUploadFailedOrgImage"));
			d.isCheckedOldEditInfo = parseBoolean(getValue(attributes, "isCheckedOldEdInfo"));
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		// 효과가 적용된 사진은 로컬에 저장하기 때문에 Upload로 간주하지 않는다.
//		if (!d.isApplyEffect && d.KIND == Const_VALUES.SELECT_PHONE)
		if (d.KIND == Const_VALUES.SELECT_PHONE) //saveXML에 저장된 사진들은 어차피 다 서버에 올라가 있다.
			d.KIND = Const_VALUES.SELECT_UPLOAD;

		d.ADJ_CROP_INFO = new AdjustableCropInfo();
		CropImageRect imgRect = new CropImageRect();
		CropImageRect clipRect = new CropImageRect();
		try {
			imgRect.resWidth = parseStrToFloat(getValue(attributes, "adj_img_rect_resWidth"));
			imgRect.resHeight = parseStrToFloat(getValue(attributes, "adj_img_rect_resHeight"));
			imgRect.width = parseStrToFloat(getValue(attributes, "adj_img_rect_width"));
			imgRect.height = parseStrToFloat(getValue(attributes, "adj_img_rect_height"));
			imgRect.centerX = parseStrToFloat(getValue(attributes, "adj_img_rect_centerX"));
			imgRect.centerY = parseStrToFloat(getValue(attributes, "adj_img_rect_centerY"));
			imgRect.rotate = parseStrToFloat(getValue(attributes, "adj_img_rect_rotate"));
			imgRect.angle = parseStrToFloat(getValue(attributes, "adj_img_rect_angle"));
			imgRect.scaleX = parseStrToFloat(getValue(attributes, "adj_img_rect_scaleX"));
			imgRect.scaleY = parseStrToFloat(getValue(attributes, "adj_img_rect_scaleY"));
			imgRect.movedX = parseStrToFloat(getValue(attributes, "adj_img_rect_movedX"));
			imgRect.movedY = parseStrToFloat(getValue(attributes, "adj_img_rect_movedY"));

			imgRect.matrixValue = new float[9];
			for (int ii = 0; ii < imgRect.matrixValue.length; ii++) {
				imgRect.matrixValue[ii] = parseStrToFloat(getValue(attributes, "adj_img_rect_matrix_" + ii));
			}

			clipRect.resWidth = parseStrToFloat(getValue(attributes, "adj_clip_rect_resWidth"));
			clipRect.resHeight = parseStrToFloat(getValue(attributes, "adj_clip_rect_resHeight"));
			clipRect.width = parseStrToFloat(getValue(attributes, "adj_clip_rect_width"));
			clipRect.height = parseStrToFloat(getValue(attributes, "adj_clip_rect_height"));
			clipRect.centerX = parseStrToFloat(getValue(attributes, "adj_clip_rect_centerX"));
			clipRect.centerY = parseStrToFloat(getValue(attributes, "adj_clip_rect_centerY"));
			clipRect.rotate = parseStrToFloat(getValue(attributes, "adj_clip_rect_rotate"));
			clipRect.angle = parseStrToFloat(getValue(attributes, "adj_clip_rect_angle"));
			clipRect.scaleX = parseStrToFloat(getValue(attributes, "adj_clip_rect_scaleX"));
			clipRect.scaleY = parseStrToFloat(getValue(attributes, "adj_clip_rect_scaleY"));
			clipRect.movedX = parseStrToFloat(getValue(attributes, "adj_clip_rect_movedX"));
			clipRect.movedY = parseStrToFloat(getValue(attributes, "adj_clip_rect_movedY"));

			clipRect.matrixValue = new float[9];
			for (int ii = 0; ii < clipRect.matrixValue.length; ii++) {
				clipRect.matrixValue[ii] = parseStrToFloat(getValue(attributes, "adj_clip_rect_matrix_" + ii));
			}

			String szMMPageWidth = getValue(attributes, "mmPageWidth");
			if (szMMPageWidth != null && szMMPageWidth.length() > 0)
				d.mmPageWidth = parseStrToFloat(szMMPageWidth);

			String szPXPageWidth = getValue(attributes, "pxPageWidth");
			if (szPXPageWidth != null && szPXPageWidth.length() > 0)
				d.pxPageWidth = Integer.parseInt(szPXPageWidth);

			d.controlWidth = getValue(attributes, "controlWidth");

			String szScreenWidth = getValue(attributes, "screenWidth");
			if (szScreenWidth != null && szScreenWidth.length() > 0)
				d.screenWidth = Integer.parseInt(szScreenWidth);

			String szScreenHeight = getValue(attributes, "screenHeight");
			if (szScreenHeight != null && szScreenHeight.length() > 0)
				d.screenHeight = Integer.parseInt(szScreenHeight);

			String szAlpha = getValue(attributes, "imgAlpha");
			if (szAlpha != null && szAlpha.length() > 0)
				d.imgAlpha = Integer.parseInt(szAlpha);

			String editorOrientation = getValue(attributes, "editorOrientation");
			d.editorOrientation = editorOrientation;

			String photoTakenTime = getValue(attributes, "photoTakenTime");
			if (photoTakenTime != null && photoTakenTime.length() > 0)
				d.photoTakenDateTime = parseStrToLong(photoTakenTime);
			d.mineType = getValue(attributes, "mineType");
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		d.ADJ_CROP_INFO.setCropped(parseBoolean(getValue(attributes, "isCropped")));
		d.ADJ_CROP_INFO.setImgRect(imgRect);
		d.ADJ_CROP_INFO.setClipRect(clipRect);
		return d;
	}

	public String getImageIdOnSnapsPageCanvas(boolean isThumbnail) {
		return IMG_IDX + (isThumbnail ? "_t" : "_c");
	}

	public double getImageDataKey() {
		return imageDataKey;
	}

	public void setImageDataKey(double imageDataKey) {
		this.imageDataKey = imageDataKey;
	}

	static String getValue(Attributes target, String name) {
		String value = target.getValue(name);
		return (value == null) ? "" : value;
	}

	static boolean parseBoolean(String name) {
		return (name != null && name.trim().equalsIgnoreCase("true")) ? true : false;
	}

	public void startUploadSyncLock() {
		isUploading.set(true);
	}

	public void waitIfUploading() {
		if (isUploading.get()) {
			synchronized (getUploadSyncLocker()) {
				if (isUploading.get()) {
					try {
						getUploadSyncLocker().wait();
					} catch (InterruptedException e) {
						Dlog.e(TAG, e);
					}
				}
			}
		}
	}

	public void finishUploadSyncLock() {
		if (isUploading.get()) {
			isUploading.set(false);
			synchronized (getUploadSyncLocker()) {
				getUploadSyncLocker().notifyAll();
			}
		}
	}

	private ImageUploadSyncLocker getUploadSyncLocker() {
		return uploadSyncLocker;
	}

	public SmartSnapsImgInfo getSmartSnapsImgInfo() {
		return smartSnapsImgInfo;
	}

	public void setSmartSnapsImgInfo(SmartSnapsImgInfo smartSnapsImgInfo) {
		this.smartSnapsImgInfo = smartSnapsImgInfo;
	}

	public boolean isFindSmartSnapsFaceArea() {
		return getSmartSnapsImgInfo() != null && !getSmartSnapsImgInfo().isFailedSearchFace()
			&& getSmartSnapsImageAreaInfo() != null && getSmartSnapsImageAreaInfo().isExistAllInfoInSmartImageArea();
	}

	public void requestSmartSnapsAnimation() throws Exception {
		SmartSnapsImgInfo smartSnapsImgInfo = getSmartSnapsImgInfo();
		if (smartSnapsImgInfo != null) {
			ISmartSnapImgDataAnimationState animationStateListener = smartSnapsImgInfo.getSmartSnapImgDataAnimationStateListener();
			if (animationStateListener != null) {
				animationStateListener.onRequestedAnimation();
			}
		}
	}

	public BaseCropInfo getCropInfo() {
		if (isAdjustableCropMode) {
			try {
				AdjustableCropInfo cropInfo = ADJ_CROP_INFO.getAdjustedCropInfo();
				if (cropInfo != null)
					cropInfo.setEffectType(EFFECT_TYPE);
				return cropInfo;
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
			return CROP_INFO;
		} else {
			return CROP_INFO;
		}
	}

	public boolean isSameImage(MyPhotoSelectImageData compareImage) {
		if (compareImage == null) return false;
		return  (photoTakenDateTime > 0 && photoTakenDateTime == compareImage.photoTakenDateTime) || getImageSelectMapKey() != null &&  getImageSelectMapKey().equalsIgnoreCase(compareImage.getImageSelectMapKey());
	}

	public boolean isSmartSnapsSupport() {
		return KIND == Const_VALUES.SELECT_PHONE || KIND == Const_VALUES.SELECT_UPLOAD
				|| Config.isSNSPhoto(KIND);
	}

	public void increaseUploadPriority() {
		uploadPriority++;
	}

	public int getUploadPriority() {
		return uploadPriority;
	}

	public void setSortPriority(int sortPriority) {
		this.sortPriority = sortPriority;
	}

	public int getSortPriority() {
		return sortPriority;
	}

	public boolean isTriedRecoveryEffectFilterFile() {
		return isTriedRecoveryEffectFilterFile;
	}

	public void setTriedRecoveryEffectFilterFile(boolean recoveryEffectFilterFile) {
		isTriedRecoveryEffectFilterFile = recoveryEffectFilterFile;
	}

	public ExifUtil.SnapsExifInfo getExifInfo() {
		return exifInfo;
	}

	public void setExifInfo(ExifUtil.SnapsExifInfo exifInfo) {
		this.exifInfo = exifInfo;
	}

	public String getImageSystemDateTime() {
		try {
			Date takenDate = new Date(photoTakenDateTime);
			long takenTime = takenDate.getTime();
			if (DateUtil.isValidSmartSnapsDate(takenTime)) {
				String convertedDate = StringUtil.convertLongTimeToSmartAnalysisFormat(takenDate.getTime());
				if (!StringUtil.isEmpty(convertedDate)) return convertedDate;
			}

			//만약, 로컬 Media 날짜가 부정확하다면 lastmodified 날짜를 쓰도록 한다.
			File file = new File(LOCAL_THUMBNAIL_PATH);
			if (file.exists()) {
				Date lastModDate = new Date(file.lastModified());
				long dateTime = lastModDate.getTime();
				if (DateUtil.isValidSmartSnapsDate(dateTime)) {
                    String convertedDate = StringUtil.convertLongTimeToSmartAnalysisFormat(lastModDate.getTime());
                    if (!StringUtil.isEmpty(convertedDate)) return convertedDate;
                }
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return StringUtil.convertLongTimeToSmartAnalysisFormat(Calendar.getInstance().getTimeInMillis());
	}

	public String getImageSelectMapKey() {
//		if (pageType == SmartSnapsConstants.eSmartSnapsAnalysisImagePageType.COVER || pageType == SmartSnapsConstants.eSmartSnapsAnalysisImagePageType.TITLE) return "";
		return KIND + "_" + IMAGE_ID;
	}

	public String getImagePathForWebLog() {
		return !StringUtil.isEmpty(ORIGINAL_PATH) ? ORIGINAL_PATH : THUMBNAIL_PATH;
	}
}
