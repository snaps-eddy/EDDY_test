package com.snaps.common.data.img;

import android.os.Parcel;
import android.os.Parcelable;

//home stickerkit image data
public class MyHomeStickerImageData implements Parcelable {  
	public String ORIGIN_IMAGE_DATA = "";
	public String THUMBNAIL_IMAGE_DATA = "";
	
	public MyHomeStickerImageData() {
    }
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(ORIGIN_IMAGE_DATA);
		dest.writeString(THUMBNAIL_IMAGE_DATA);
	}
	
	private void readFromParcel(Parcel in){
		ORIGIN_IMAGE_DATA = in.readString();
		THUMBNAIL_IMAGE_DATA = in.readString();		
	}
	
	public MyHomeStickerImageData(Parcel in) {
		readFromParcel(in);
	}
	    
	@Override
	public int describeContents() {
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MyHomeStickerImageData createFromParcel(Parcel in) {
             return new MyHomeStickerImageData(in);
       }

       public MyHomeStickerImageData[] newArray(int size) {
            return new MyHomeStickerImageData[size];
       }
   };
}

