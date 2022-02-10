package com.snaps.common.upload;

import com.snaps.common.data.img.MyPhotoSelectImageData;

import java.util.ArrayList;

public class UploadProject {

	String mVersion;
	String mOrderCode;
	String mProjectCode;
	float mProgress;
	float mTotalPrice;
	ArrayList<UploadItem> mItems = new ArrayList<UploadItem>();

	/***
	 *
	 * @param data
	 * @param projectCode
	 */

	public void addItems(ArrayList<MyPhotoSelectImageData> data, String projectCode) {

	}
}
