package com.snaps.common.structure.control;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class LineText implements Parcelable, Serializable {

	private static final long serialVersionUID = 3245259024205834940L;
	public String x = "0";
	public String y = "0";
	public String width = "0";
	public String height = "0";
	public String text = "";

	public float getFloatX() {
		return Float.parseFloat(x);
	}

	public float getFloatY() {
		return Float.parseFloat(y);
	}

	public LineText() {}
	
	public LineText(Parcel in) {
		readFromParcel(in);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(x);
		dest.writeString(y);
		dest.writeString(width);
		dest.writeString(height);
		dest.writeString(text);
	}
	
	private void readFromParcel(Parcel in) {
		x = in.readString();
		y = in.readString();
		width = in.readString();
		height = in.readString();
		text = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		@Override
		public LineText createFromParcel(Parcel in) {
			return new LineText(in);
		}

		@Override
		public LineText[] newArray(int size) {
			return new LineText[size];
		}
	};
	
}
