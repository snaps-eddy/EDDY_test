package com.snaps.mobile.activity.selectimage.adapter;


import androidx.recyclerview.widget.RecyclerView;

import com.snaps.common.data.img.ExifUtil;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.ui.BSize;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectPhonePhotoInfo;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

import java.util.HashSet;
import java.util.Set;

public abstract class GalleryCursorRecord {
	
	public static class GalleryAlbumCursor implements IAlbumData {
		int phoneFolderId;
		int imageID;
		String phoneFolderName;
		int phoneDetailOrientation;
		int phoneFolderImgs;
		String thumbnailPath;

		public void set(int phoneFolderId, int imageID, String phoneFolderName, int phoneDetailOrientation, int phoneFolderImgs,String thumbnailPath) {
			this.phoneFolderId = phoneFolderId;
			this.imageID = imageID;
			this.phoneFolderName = phoneFolderName;
			this.phoneDetailOrientation = phoneDetailOrientation;
			this.phoneFolderImgs = phoneFolderImgs;
			this.thumbnailPath = thumbnailPath;
		}

		public int getPhoneFolderId() {
			return phoneFolderId;
		}

		public int getImageID() {
			return imageID;
		}

		public String getPhoneFolderName() {
			return phoneFolderName;
		}

		public int getPhoneDetailOrientation() {
			return phoneDetailOrientation;
		}

		public int getPhoneFolderImgs() {
			return phoneFolderImgs;
		}

		public String getThumbnailPath() {
			return thumbnailPath;
		}

		public void setPhoneFolderImgs(int phoneFolderImgs) {
			this.phoneFolderImgs = phoneFolderImgs;
		}

		@Override
		public String getAlbumId() {
			return String.valueOf(getPhoneFolderId());
		}

		@Override
		public String getUserId() {
			return null;
		}

		@Override
		public String getAlbumUrl() {
			return null;
		}

		@Override
		public String getAlbumName() {
			return getPhoneFolderName();
		}

		@Override
		public String getAlbumThumnbail() {
			return getThumbnailPath();
		}

		@Override
		public String getPhotoCnt() {
			return String.valueOf(getPhoneFolderImgs());
		}
	}

	public static class PhonePhotoFragmentItem {
		String groupKey;
		String imageKey;
		long phoneDetailId; 
		ImageSelectPhonePhotoInfo photoInfo;
		String phoneDetailName;
		int phoneDetailOrientation;
		int imgOutWidth;
		int imgOutHeight;
		boolean isVerificationImageRatio = false;
		boolean isCoverPhoto = false;
		ExifUtil.SnapsExifInfo exifInfo = null;
		int listPosition;

		private ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE holderType = ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL;
		private MyPhotoSelectImageData imgData;
		private RecyclerView.ViewHolder viewHolder;
		private ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH uiDepth;
		private Set<Long> subKeys;
		private BSize convertedStaggeredSize = new BSize(0, 0);

		public void set(PhonePhotoFragmentItem item){
			groupKey = item.groupKey;
			imageKey = item.imageKey;
			phoneDetailId = item.phoneDetailId;
			photoInfo = item.photoInfo;
			phoneDetailName = item.phoneDetailName;
			phoneDetailOrientation = item.phoneDetailOrientation;
			imgOutWidth = item.imgOutWidth;
			imgOutHeight = item.imgOutHeight;
			holderType = item.holderType;
			imgData = item.imgData;
			isVerificationImageRatio = item.isVerificationImageRatio;
			if (convertedStaggeredSize == null)
				convertedStaggeredSize = new BSize(0, 0);
			convertedStaggeredSize.set(item.convertedStaggeredSize);
			exifInfo = item.exifInfo;
			isCoverPhoto = item.isCoverPhoto;
		}

		public void set(long phoneDetailId, ImageSelectPhonePhotoInfo thumbnail, String phoneDetailName, int phoneDetailOrientation) {
			this.phoneDetailId = phoneDetailId;
			this.photoInfo = thumbnail;
			this.phoneDetailName = phoneDetailName;
			this.phoneDetailOrientation = phoneDetailOrientation;

			setImageData();
		}

		public MyPhotoSelectImageData getImgData() {
			return imgData;
		}

		private void setImageData() {
			this.imageKey = ImageSelectUtils.getPhonePhotoMapKey(phoneDetailId);
			imgData = new MyPhotoSelectImageData();
			imgData.KIND = Const_VALUES.SELECT_PHONE;
			imgData.IMAGE_ID = getPhoneDetailId();
			imgData.F_IMG_NAME = getPhoneDetailName();
			imgData.PATH = getPhotoOrgPath();
			imgData.THUMBNAIL_PATH = getPhotoOrgPath();
			imgData.LOCAL_THUMBNAIL_PATH = getPhotoOrgPath();
			imgData.ROTATE_ANGLE = getPhoneDetailOrientation();
			// 원본 이미지 w, h 추
			imgData.F_IMG_WIDTH = String.valueOf(getImgOutWidth());
			imgData.F_IMG_HEIGHT = String.valueOf(getImgOutHeight());

			if (photoInfo != null)
				imgData.photoTakenDateTime = photoInfo.getTakenTime();
		}

		public boolean isEqualsDimension(PhonePhotoFragmentItem compareItem) {
			if (compareItem == null) return false;
			return getImgOutWidth() == compareItem.getImgOutWidth() && getImgOutHeight() == compareItem.getImgOutHeight();
		}

		public boolean checkSubKey(long id) {
			return subKeys != null && subKeys.contains(id);
		}

		public void addSubKey(long subKey) {
			if (subKeys == null) subKeys = new HashSet<>();
			this.subKeys.add(subKey);
		}

		public ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH getUiDepth() {
			return uiDepth;
		}

		public void setUiDepth(ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH uiDepth) {
			this.uiDepth = uiDepth;
		}

		public BSize getConvertedStaggeredSize() {
			return convertedStaggeredSize;
		}

		public void setConvertedStaggeredSize(BSize convertedStaggeredSize) {
			this.convertedStaggeredSize = convertedStaggeredSize;
		}

		public RecyclerView.ViewHolder getViewHolder() {
			return viewHolder;
		}

		public void setViewHolder(RecyclerView.ViewHolder viewHolder) {
			this.viewHolder = viewHolder;
		}

		public String getImageKey() {
			return imageKey;
		}

		public void setImageKey(String imageKey) {
			this.imageKey = imageKey;
		}

		public String getGroupKey() {
			return groupKey;
		}

		public void setGroupKey(String groupKey) {
			this.groupKey = groupKey;
		}

		public void setPhotoInfo(ImageSelectPhonePhotoInfo photoInfo) {
			this.photoInfo = photoInfo;
		}

		public void setImageDimension(int width, int height) {
			imgOutWidth = width;
			imgOutHeight = height;

			if (imgData != null) {
				imgData.F_IMG_WIDTH = String.valueOf(getImgOutWidth());
				imgData.F_IMG_HEIGHT = String.valueOf(getImgOutHeight());
			}
		}

		public boolean isCoverPhoto() {
			return isCoverPhoto;
		}

		public void setCoverPhoto(boolean coverPhoto) {
			isCoverPhoto = coverPhoto;
		}

		public long getPhotoTakenTime() {
			if (photoInfo == null) return 0;
			return photoInfo.getTakenTime();
		}

		public int getPhotoTakenYear() {
			if (photoInfo == null) return 0;
			return photoInfo.getYear();
		}

		public int getPhotoTakenMonth() {
			if (photoInfo == null) return 0;
			return photoInfo.getMonth();
		}

		public int getPhotoTakenDay() {
			if (photoInfo == null) return 0;
			return photoInfo.getDay();
		}

		public String getPhotoOrgPath() {
			if (photoInfo == null) return "";
			return photoInfo.getOrgImgPath();
		}

		public String getPhotoTakenDayOfWeek() {
			if (photoInfo == null) return "";
			return photoInfo.getDayOfWeek();
		}

		public ImageSelectPhonePhotoInfo getPhotoInfo() {
			return photoInfo;
		}

		public long getPhoneDetailId() {
			return phoneDetailId;
		}

		public String getPhoneDetailName() {
			return phoneDetailName;
		}

		public int getPhoneDetailOrientation() {
			return phoneDetailOrientation;
		}

		public int getImgOutWidth() {
			return imgOutWidth;
		}

		public int getImgOutHeight() {
			return imgOutHeight;
		}

		public ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE getHolderType() {
			return holderType;
		}

		public void setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE holderType) {
			this.holderType = holderType;
		}

		public boolean isVerificationImageRatio() {
			return isVerificationImageRatio;
		}

		public void setVerificationImageRatio(boolean verificationImageRatio) {
			isVerificationImageRatio = verificationImageRatio;
		}

		public boolean shouldGetSmartAnalysisExifInfo() {
			return Config.isSmartSnapsRecommendLayoutPhotoBook() && exifInfo == null;
		}

		public void setExifInfo(ExifUtil.SnapsExifInfo exifInfo) {
			this.exifInfo = exifInfo;
		}

		public ExifUtil.SnapsExifInfo getExifInfo() {
			return exifInfo;
		}

		public void setListPosition(int position) {
			this.listPosition = position;
		}

		public int getListPosition() {
			return listPosition;
		}
	}
}
