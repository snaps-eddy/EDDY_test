package com.snaps.mobile.activity.selectimage.adapter.viewholder;

import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;

public class ImageAlbumHolder {
	public ImageView imgPhoneAlbum;
	public ImageView imgPhoneAlbumFrame;
	public TextView txtPhoneAlbumImgs;
	public TextView txtPhoneAlbumName;
	
	public int phoneFolderId;
	public String mapKey;
	public MyPhotoSelectImageData imgData;

	public String imageURL;

	public ImageAlbumHolder(ImageView imgPhoneAlbum, TextView txtPhoneAlbumImgs, TextView txtPhoneAlbumName, ImageView imgPhoneAlbumFrame) {
		this.imgPhoneAlbum = imgPhoneAlbum;
		this.txtPhoneAlbumImgs = txtPhoneAlbumImgs;
		this.txtPhoneAlbumName = txtPhoneAlbumName;
		this.imgPhoneAlbumFrame = imgPhoneAlbumFrame;
	}

	public ImageAlbumHolder(ImageView imgPhoneAlbum, TextView txtPhoneAlbumImgs, TextView txtPhoneAlbumName, String imageURL) {
		this.imgPhoneAlbum = imgPhoneAlbum;
		this.txtPhoneAlbumImgs = txtPhoneAlbumImgs;
		this.txtPhoneAlbumName = txtPhoneAlbumName;
		this.imageURL = imageURL;
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
	}
}
