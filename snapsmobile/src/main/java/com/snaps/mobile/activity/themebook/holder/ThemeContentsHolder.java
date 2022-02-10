package com.snaps.mobile.activity.themebook.holder;

import android.widget.ImageView;

public class ThemeContentsHolder {
	public ImageView imgCoverAlbum;
	public ImageView imgCoverSelect;
	public int index;

	public ThemeContentsHolder(ImageView imgCoverAlbum, ImageView imgCoverSelect, int index) {
		this.imgCoverAlbum = imgCoverAlbum;
		this.imgCoverSelect = imgCoverSelect;
		this.index = index;
	}

}
