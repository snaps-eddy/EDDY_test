package com.snaps.mobile.activity.selectimage.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;

public class PhotoData //implements Parcelable
{
	private static final String TAG = PhotoData.class.getSimpleName();
	public String ID = "";
	public String ORIGIN_IMAGE_DATA = "";
	public String THUMBNAIL_IMAGE_DATA = "";
	public String CONTENT = "";
	public String CREATED_AT = "";
	public long createdAt;
	
	//원본이미지 넓이, 높이 추가...
	public String ORIGIN_IMAGE_WIDTH = "";
	public String ORIGIN_IMAGE_HEIGHT = "";
	
	public PhotoData() {
    }
	
	public void setCreateAt(String createAtStr) {
		try {
			createdAt = Long.valueOf(createAtStr);
			CREATED_AT = StringUtil.getDateFormatKakao(createdAt);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void readFromParcel(Parcel in){
		ORIGIN_IMAGE_DATA = in.readString();
		THUMBNAIL_IMAGE_DATA = in.readString();	
		CONTENT = in.readString();
		CREATED_AT = in.readString();
		
		
		ORIGIN_IMAGE_WIDTH = in.readString();
		ORIGIN_IMAGE_HEIGHT = in.readString();
	}
	
	public PhotoData(Parcel in) {
		readFromParcel(in);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PhotoData createFromParcel(Parcel in) {
             return new PhotoData(in);
       }

       public PhotoData[] newArray(int size) {
            return new PhotoData[size];
       }
   };
}
