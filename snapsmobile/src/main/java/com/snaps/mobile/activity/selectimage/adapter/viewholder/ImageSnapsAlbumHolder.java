package com.snaps.mobile.activity.selectimage.adapter.viewholder;

import android.widget.ImageView;
import android.widget.TextView;

public class ImageSnapsAlbumHolder {
	public ImageView imgPhoneAlbum;
	public TextView txtPhoneAlbumName;
	public TextView txtPhoneCnt;
	
	public String categoryCode;
	
	public ImageSnapsAlbumHolder(ImageView imgPhoneAlbum, TextView txtPhoneAlbumName, TextView txtPhoneCnt) {
		this.imgPhoneAlbum = imgPhoneAlbum;
		this.txtPhoneAlbumName = txtPhoneAlbumName;
		this.txtPhoneCnt = txtPhoneCnt;
	}

	public ImageSnapsAlbumHolder(ImageView imgPhoneAlbum, TextView txtPhoneAlbumName) {
		this.imgPhoneAlbum = imgPhoneAlbum;
		this.txtPhoneAlbumName = txtPhoneAlbumName;
	}
	
}
