package com.snaps.common.utils.ui;

public interface IImageData {

	void setImageId(String id);

	void setImageOriginalWidth(String ori_width);

	void setImageOriginalHeight(String ori_height);

	void setImageOriginalPath(String path);

	void setImageThumbnailPath(String path);

	void setImageCreateAt(String createAt);

	void setImageAngle(int angle);

	String getImageId();

	String getImageOriginalWidth();

	String getImageOriginalHeight();

	String getImageOriginalPath();

	String getImageThumbnailPath();

	String getImageCreateAt();

	int getImageAngle(int angle);

	String getMineType();

	void setMineType(String mineType);

}
