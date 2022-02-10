package com.snaps.common.data.img;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.ui.IImageData;

/***
 * 카카오,페이스북, 인스타,구글포토에서 같이 사용을 하기 위해 만들었다...
 * 
 * @author yeonsungbae
 *
 */
public class MyNetworkImageData implements Parcelable, IImageData {
	public String ID = "";
	public String ORIGIN_IMAGE_URL = "";
	public String THUMBNAIL_IMAGE_URL = "";
	// public String CONTENT = "";
	public String CREATED_AT = "";
	public String ORIGIN_IMAGE_WIDTH = "";
	public String ORIGIN_IMAGE_HEIGHT = "";
	public int imgType = 0;
	public boolean isInclude = true;
	public String MINE_TYPE = "";
	

	public MyNetworkImageData() {
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(ORIGIN_IMAGE_URL);
		dest.writeString(THUMBNAIL_IMAGE_URL);

		dest.writeString(CREATED_AT);
		dest.writeString(ORIGIN_IMAGE_WIDTH);
		dest.writeString(ORIGIN_IMAGE_HEIGHT);
	}

	private void readFromParcel(Parcel in) {
		ORIGIN_IMAGE_URL = in.readString();
		THUMBNAIL_IMAGE_URL = in.readString();
		// CONTENT = in.readString();
		CREATED_AT = in.readString();
		ORIGIN_IMAGE_WIDTH = in.readString();
		ORIGIN_IMAGE_HEIGHT = in.readString();
	}

	public MyNetworkImageData(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public MyPhotoSelectImageData createFromParcel(Parcel in) {
			return new MyPhotoSelectImageData(in);
		}

		public MyNetworkImageData[] newArray(int size) {
			return new MyNetworkImageData[size];
		}
	};

	@Override
	public void setImageId(String id) {
		this.ID = id;
		
	}

	@Override
	public void setImageOriginalWidth(String ori_width) {
		this.ORIGIN_IMAGE_WIDTH = ori_width;
		
	}

	@Override
	public void setImageOriginalHeight(String ori_height) {
		this.ORIGIN_IMAGE_HEIGHT = ori_height;
		
	}

	@Override
	public void setImageOriginalPath(String path) {
		this.ORIGIN_IMAGE_URL = path;
		
	}

	@Override
	public void setImageThumbnailPath(String path) {
		this.THUMBNAIL_IMAGE_URL = path;
		
	}

	@Override
	public void setImageCreateAt(String createAt) {
		this.CREATED_AT = createAt;
		
	}

	@Override
	public void setImageAngle(int angle) {
		
	}

	@Override
	public String getImageId() {
		return this.ID;
	}

	@Override
	public String getImageOriginalWidth() {
		// TODO Auto-generated method stub
		return ORIGIN_IMAGE_WIDTH;
	}

	@Override
	public String getImageOriginalHeight() {
		// TODO Auto-generated method stub
		return ORIGIN_IMAGE_HEIGHT;
	}

	@Override
	public String getImageOriginalPath() {
		// TODO Auto-generated method stub
		return ORIGIN_IMAGE_URL;
	}

	@Override
	public String getImageThumbnailPath() {
		// TODO Auto-generated method stub
		return THUMBNAIL_IMAGE_URL;
	}

	@Override
	public String getImageCreateAt() {
		return CREATED_AT;
	}

	@Override
	public int getImageAngle(int angle) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMineType() {
		return MINE_TYPE;
	}

	@Override
	public void setMineType(String mineType) {
		MINE_TYPE = mineType;
	}
}
