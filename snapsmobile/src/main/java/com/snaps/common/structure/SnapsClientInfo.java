package com.snaps.common.structure;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class SnapsClientInfo implements Parcelable, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6573705505000496315L;
	
	public String os = "";
	public String language = "";
	public String screendpi = "";
	public String playertype = "";
	public String screenresolution = "";

	public SnapsClientInfo() {}
	
	public SnapsClientInfo(Parcel in) {
		readFromParcel(in);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(os);
		dest.writeString(language);
		dest.writeString(screendpi);
		dest.writeString(playertype);
		dest.writeString(screenresolution);
	}
	
	private void readFromParcel(Parcel in) {
		os = in.readString();
		language = in.readString();
		screendpi = in.readString();
		playertype = in.readString();
		screenresolution = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		@Override
		public SnapsClientInfo createFromParcel(Parcel in) {
			return new SnapsClientInfo(in);
		}

		@Override
		public SnapsClientInfo[] newArray(int size) {
			return new SnapsClientInfo[size];
		}
	};
}
