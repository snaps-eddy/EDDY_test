package com.snaps.common.data.between;

import com.google.gson.annotations.SerializedName;


public class BWProfilePhotoResponse extends BaseResponse {
	
	private static final long serialVersionUID = -2496733948587447568L;
	
	@SerializedName("width")
	private int width;
	
	@SerializedName("height")
	private int height;
	
	@SerializedName("source")
	private String source;
	
	public void set(BWProfilePhotoResponse profilePhoto) {
		if(profilePhoto == null) return;
		width = profilePhoto.width;
		height = profilePhoto.height;
		source = profilePhoto.source;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
