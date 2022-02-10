package com.snaps.mobile.activity.selectimage.adapter.viewholder;

import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;

public class ImageDetailHolder {
	public ImageView imgDetail;
	public ImageView imgChoiceBg;
	public ImageView imgChoiceBgWhite;
	public ImageView imgUnderPixel;
	public TextView tvDetail;

	public boolean IsNoSelect = false; // 선택이 가능 여부 설정..
	public boolean isPngImage = false; // png 이미지 여부 설정... 확장자가 jpg 파일은 png 걸러내기 위해 필요...

	public String mapKey;
	public MyPhotoSelectImageData imgData;

	public String osType;

	public ImageDetailHolder() {

	}

	public ImageDetailHolder(ImageView imgDetail, ImageView imgChoiceBg) {
		this.imgDetail = imgDetail;
		this.imgChoiceBg = imgChoiceBg;
	}

	public ImageDetailHolder(ImageView imgDetail, ImageView imgChoiceBg, ImageView imgUnderPixel) {
		this.imgDetail = imgDetail;
		this.imgChoiceBg = imgChoiceBg;
		this.imgUnderPixel = imgUnderPixel;
	}
	
	public ImageDetailHolder(ImageView imgDetail, ImageView imgChoiceBg, ImageView imgChoiceBgWhite, ImageView imgUnderPixel) {
		this.imgDetail = imgDetail;
		this.imgChoiceBg = imgChoiceBg;
		this.imgChoiceBgWhite = imgChoiceBgWhite;
		this.imgUnderPixel = imgUnderPixel;
	}
	
	public ImageDetailHolder(ImageView imgDetail, ImageView imgChoiceBg, ImageView imgUnderPixel, TextView tvDetail) {
		this.imgDetail = imgDetail;
		this.imgChoiceBg = imgChoiceBg;
		this.imgUnderPixel = imgUnderPixel;
		this.tvDetail = tvDetail;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public void setWhiteBg(ImageView bg) {
		this.imgChoiceBgWhite = bg;
	}

	public void setNoPrint(boolean isNoPrint) {
		if (imgData == null) return;
		imgData.isNoPrint = isNoPrint;
	}

	public void noSelect(boolean noselect) {
		IsNoSelect = noselect;
	}

	public void setPngImage(boolean isPngImage) {
		this.isPngImage = isPngImage;
	}

	public void setImgData(String mapKey, int photoKind, long imgId, String displayName, String path, String thumbnailPath) {
		if( path == null ) path = "";
		if( thumbnailPath == null ) thumbnailPath = "";
		
		this.mapKey = mapKey;
		imgData = new MyPhotoSelectImageData();
		imgData.KIND = photoKind;
		imgData.IMAGE_ID = imgId;
		imgData.F_IMG_NAME = displayName;
		imgData.PATH = path;
		imgData.THUMBNAIL_PATH = thumbnailPath;
		imgData.LOCAL_THUMBNAIL_PATH = thumbnailPath;
		imgData.IMAGE_ID = path.hashCode();
	}

	public void setImgData(String mapKey, int photoKind, String fbObjId, String displayName, String path, String thumbnailPath, long kakaobookDate) {
		this.mapKey = mapKey;
		imgData = new MyPhotoSelectImageData();
		imgData.KIND = photoKind;
		imgData.FB_OBJECT_ID = fbObjId;
		imgData.F_IMG_NAME = displayName;
		imgData.PATH = path;
		imgData.THUMBNAIL_PATH = thumbnailPath;
		imgData.LOCAL_THUMBNAIL_PATH = thumbnailPath;
		imgData.photoTakenDateTime = kakaobookDate;
		imgData.IMAGE_ID = path.hashCode();

	}

	public void setImgData(String mapKey, int photoKind, long imgId, String displayName, String path, String thumbnailPath, int rotate) {
		this.mapKey = mapKey;
		imgData = new MyPhotoSelectImageData();
		imgData.KIND = photoKind;
		imgData.IMAGE_ID = imgId;
		imgData.F_IMG_NAME = displayName;
		imgData.PATH = path;
		imgData.THUMBNAIL_PATH = thumbnailPath;
		imgData.LOCAL_THUMBNAIL_PATH = thumbnailPath;
		imgData.ROTATE_ANGLE = rotate;
		imgData.IMAGE_ID = path.hashCode();
	}

	public void setImgData(String mapKey, int photoKind, long imgId, String displayName, String path, String thumbnailPath, String width, String height) {
		this.mapKey = mapKey;
		imgData = new MyPhotoSelectImageData();
		imgData.KIND = photoKind;
		imgData.IMAGE_ID = imgId;
		imgData.F_IMG_NAME = displayName;
		imgData.PATH = path;
		imgData.THUMBNAIL_PATH = thumbnailPath;
		imgData.LOCAL_THUMBNAIL_PATH = thumbnailPath;
		// 원본 이미지 w, h 추
		imgData.F_IMG_WIDTH = width;
		imgData.F_IMG_HEIGHT = height;
		imgData.IMAGE_ID = path.hashCode();
	}

	public void setImgData(String mapKey, int photoKind, String fbObjId, String displayName, String path, String thumbnailPath, long kakaobookDate, String width, String height) {
		this.mapKey = mapKey;
		imgData = new MyPhotoSelectImageData();
		imgData.KIND = photoKind;
		imgData.FB_OBJECT_ID = fbObjId;
		imgData.F_IMG_NAME = displayName;
		imgData.PATH = path;
		imgData.THUMBNAIL_PATH = thumbnailPath;
		imgData.LOCAL_THUMBNAIL_PATH = thumbnailPath;
		imgData.photoTakenDateTime = kakaobookDate;
		// 원본 이미지 w, h 추가
		imgData.F_IMG_WIDTH = width;
		imgData.F_IMG_HEIGHT = height;
		imgData.IMAGE_ID = path.hashCode();

	}

	public void setImgData(String mapKey, int photoKind, String fbObjId, String displayName, String path, String thumbnailPath, String kakaobookDate, String kakaobookcontext, String width,
			String height,long lKakaobookDate) {
		this.mapKey = mapKey;
		imgData = new MyPhotoSelectImageData();
		imgData.KIND = photoKind;
		imgData.FB_OBJECT_ID = fbObjId;
		imgData.F_IMG_NAME = displayName;
		imgData.PATH = path;
		imgData.THUMBNAIL_PATH = thumbnailPath;
		imgData.LOCAL_THUMBNAIL_PATH = thumbnailPath;
		imgData.KAKAOBOOK_DATE = kakaobookDate;
		imgData.KAKAOBOOK_CONTENT = kakaobookcontext;
		imgData.photoTakenDateTime = lKakaobookDate;
		// 원본 이미지 w, h 추가
		imgData.F_IMG_WIDTH = width;
		imgData.F_IMG_HEIGHT = height;
		imgData.IMAGE_ID = path.hashCode();

	}

	public void setImgData(String mapKey, int photoKind, long imgId, String displayName, String path, String thumbnailPath, int rotate, String width, String height) {
		this.mapKey = mapKey;
		imgData = new MyPhotoSelectImageData();
		imgData.KIND = photoKind;
		imgData.IMAGE_ID = imgId;
		imgData.F_IMG_NAME = displayName;
		imgData.PATH = path;
		imgData.THUMBNAIL_PATH = thumbnailPath;
		imgData.LOCAL_THUMBNAIL_PATH = thumbnailPath;
		imgData.ROTATE_ANGLE = rotate;
		// 원본 이미지 w, h 추
		imgData.F_IMG_WIDTH = width;
		imgData.F_IMG_HEIGHT = height;
	}
}
