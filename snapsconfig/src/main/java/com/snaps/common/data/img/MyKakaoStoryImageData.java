package com.snaps.common.data.img;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;

public class MyKakaoStoryImageData implements Parcelable {
	private static final String TAG = MyKakaoStoryImageData.class.getSimpleName();
	public String ID = "";
	public String ORIGIN_IMAGE_DATA = "";
	public String THUMBNAIL_IMAGE_DATA = "";
	public String CONTENT = "";
	public String CREATED_AT = "";
	public long createdAt;

	public String ORIGIN_IMAGE_WIDTH = "";
	public String ORIGIN_IMAGE_HEIGHT = "";

	public String realId = "";

	public MyKakaoStoryImageData() {
	}

	public void setCreateAt(String createAtStr) {
		try {

			String stepleftString1 = createAtStr.replace("-", "");

			String stepleftString2 = stepleftString1.replace(":", "");

			String stepleftString3 = stepleftString2.replace("T", "");

			String stepleftString4 = stepleftString3.replace("Z", "");

			createdAt = Long.valueOf(stepleftString4);

			CREATED_AT = StringUtil.getDateFormatKakao2(createdAt);

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	/***
	 * 이전에 사용하던 method
	 * 
	 * @param createAtStr
	 * @param addtime
	 */
	public void setCreateAt(String createAtStr, int addtime) {
		try {
			createdAt = Long.valueOf(createAtStr) + addtime;
			CREATED_AT = StringUtil.getDateFormatKakao(createdAt);

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(ORIGIN_IMAGE_DATA);
		dest.writeString(THUMBNAIL_IMAGE_DATA);
		dest.writeString(CONTENT);
		dest.writeString(CREATED_AT);
		dest.writeString(ORIGIN_IMAGE_WIDTH);
		dest.writeString(ORIGIN_IMAGE_HEIGHT);
	}

	private void readFromParcel(Parcel in) {
		ORIGIN_IMAGE_DATA = in.readString();
		THUMBNAIL_IMAGE_DATA = in.readString();
		CONTENT = in.readString();
		CREATED_AT = in.readString();
		ORIGIN_IMAGE_WIDTH = in.readString();
		ORIGIN_IMAGE_HEIGHT = in.readString();
	}

	public MyKakaoStoryImageData(Parcel in) {
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

		public MyKakaoStoryImageData[] newArray(int size) {
			return new MyKakaoStoryImageData[size];
		}
	};
}
