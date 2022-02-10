package com.snaps.common.structure;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class SnapsSaveInfo implements Parcelable, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3053072801411868672L;
	public String maker = "oem";
	public String projectName = "";
	public String projectIndex = "0";
	public String coverExtended = "false";
	public String validate = "true";
	public String year = "";
	public String month = "";
	public String noday = "false";
	public String id = "0";
	public String complete = "A";
	public String orderCount = "1";
	public String orgPrice = "0";
	public String tmbPath = "null";
	public String imgYear = "";
	public String imgSeq = "";
	
	public SnapsSaveInfo() {}
	
	public SnapsSaveInfo(Parcel in) {
		readFromParcel(in);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(maker);
		dest.writeString(projectName);
		dest.writeString(projectIndex);
		dest.writeString(coverExtended);
		dest.writeString(validate);
		dest.writeString(year);
		dest.writeString(month);
		dest.writeString(noday);
		dest.writeString(id);
		dest.writeString(complete);
		dest.writeString(orderCount);
		dest.writeString(orgPrice);
		dest.writeString(tmbPath);
		dest.writeString(imgYear);
		dest.writeString(imgSeq);
	}
	
	private void readFromParcel(Parcel in) {
		maker = in.readString();
		projectName = in.readString();
		projectIndex = in.readString();
		coverExtended = in.readString();
		validate = in.readString();
		year = in.readString();
		month = in.readString();
		noday = in.readString();
		id = in.readString();
		complete = in.readString();
		orderCount = in.readString();
		orgPrice = in.readString();
		tmbPath = in.readString();
		imgYear = in.readString();
		imgSeq = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		@Override
		public SnapsSaveInfo createFromParcel(Parcel in) {
			return new SnapsSaveInfo(in);
		}

		@Override
		public SnapsSaveInfo[] newArray(int size) {
			return new SnapsSaveInfo[size];
		}
	};
}
