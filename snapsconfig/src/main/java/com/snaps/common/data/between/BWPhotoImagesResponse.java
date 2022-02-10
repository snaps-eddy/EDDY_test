package com.snaps.common.data.between;

import com.google.gson.annotations.SerializedName;


public class BWPhotoImagesResponse extends BaseResponse {
	
	private static final long serialVersionUID = -2496733948587447568L;

	@SerializedName("width")
	private int width;
	
	@SerializedName("height")
	private int height;
	
	private double extent;
	
	@SerializedName("source")
	private String source;
	
	public void set(BWPhotoImagesResponse images) {
		if(images == null) return;
		width = images.width;
		height = images.height;
		source = images.source;
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
	
	public void setExtent() {
		this.extent = width * height;
	}
	
	public double getExtent() {
		return extent;
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
