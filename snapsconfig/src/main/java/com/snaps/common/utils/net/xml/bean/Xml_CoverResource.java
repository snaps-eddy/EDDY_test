package com.snaps.common.utils.net.xml.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Xml_CoverResource implements Parcelable {
	public int idx;
	public String target;
	public String coverColor;
	public String bgColor;
	public String coverImgUrl;
	public String fontColor;
	
	public String titleBgColor;
	public String titleFontColor;
	public String titleTarget;
	public String titleCoverImgUrl;

	public Xml_CoverResource(int idx, String target, String coverColor, String bgColor, String coverImgUrl, String fontColor) {
		this.idx = idx;
		this.target = target;
		this.coverColor = coverColor;
		this.bgColor = bgColor;
		this.coverImgUrl = coverImgUrl;
		this.fontColor = fontColor;
	}
	
	public Xml_CoverResource() {}
	public Xml_CoverResource(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idx);
		dest.writeString(target);
		dest.writeString(coverColor);
		dest.writeString(bgColor);
		dest.writeString(coverImgUrl);
		dest.writeString(fontColor);
		
		dest.writeString(titleBgColor);
		dest.writeString(titleFontColor);
		dest.writeString(titleTarget);
		dest.writeString(titleCoverImgUrl);
	}
	
	private void readFromParcel(Parcel in){// !! Parcel 객체에 write 하는 순서는 read 하는 순서와 같아야 함. !!
		idx = in.readInt();
		target = in.readString();
		coverColor = in.readString();
		bgColor = in.readString();
		coverImgUrl = in.readString();
		fontColor = in.readString();
		
		titleBgColor = in.readString();
		titleFontColor = in.readString();
		titleTarget = in.readString();
		titleCoverImgUrl = in.readString();
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Xml_CoverResource createFromParcel(Parcel in) {
             return new Xml_CoverResource(in);
       }
       public Xml_CoverResource[] newArray(int size) {
            return new Xml_CoverResource[size];
       }
   };
}