package com.snaps.mobile.activity.themebook.holder;

import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ThemeCoverHolder {
	public ImageView imgCoverAlbum;
	public ImageView imgCoverSelect;
	public ImageView imgOutLine;
	public RelativeLayout selectLayout;
	public int index;

	public ThemeCoverHolder(ImageView imgCoverAlbum, ImageView imgCoverSelect, ImageView imgOutLine,RelativeLayout selectLayout, int index) {
		this.imgCoverAlbum = imgCoverAlbum;
		this.imgCoverSelect = imgCoverSelect;
		this.imgOutLine = imgOutLine;
		this.selectLayout = selectLayout;
		this.index = index;
	}

	public ThemeCoverHolder(ImageView imgCoverAlbum, ImageView imgCoverSelect, ImageView imgOutLine, int index) {
		this.imgCoverAlbum = imgCoverAlbum;
		this.imgCoverSelect = imgCoverSelect;
		this.imgOutLine = imgOutLine;
		this.index = index;
	}

	public ThemeCoverHolder(ImageView imgCoverAlbum, ImageView imgCoverSelect, int index) {
		this.imgCoverAlbum = imgCoverAlbum;
		this.imgCoverSelect = imgCoverSelect;
		this.index = index;
	}
}
