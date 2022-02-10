package com.snaps.common.utils.imageloader.recoders;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class CropInfo extends BaseCropInfo implements Parcelable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3303681495098727174L;

	public enum CORP_ORIENT {
		NONE, WIDTH, HEIGHT
	}

	public CORP_ORIENT cropOrient = CORP_ORIENT.NONE;
	public float movePercent;
	public int startPercent;
	public int endPercent;

	// 크롭 정밀도.. 테마북만 1000.f로 한다.
	public float CROP_ACCURACY = 100.f;

	public CropInfo() {
		setAdjustableCropSize(false);
	}
	
	public void set(CropInfo cropInfo) {
		if(cropInfo == null) return;
		
		this.cropOrient = cropInfo.cropOrient;
		this.movePercent = cropInfo.movePercent;
		this.startPercent = cropInfo.startPercent;
		this.endPercent = cropInfo.endPercent;
		this.CROP_ACCURACY = cropInfo.CROP_ACCURACY;
		this.endPercent = cropInfo.endPercent;
		this.isAdjustableCropSize = cropInfo.isAdjustableCropSize;
		this.isCropped = cropInfo.isCropped;
	}
	
	public boolean isCropped() {
		return cropOrient != CORP_ORIENT.NONE;
	}

	public CropInfo(CORP_ORIENT cropOrient, float movePercent, int startPercent, int endPercent) {
		setAdjustableCropSize(false);
		
		this.cropOrient = cropOrient;
		this.movePercent = movePercent;
		this.startPercent = startPercent;
		this.endPercent = endPercent;
	}

	@Override
	public String toString() {
		return "CropInfo [cropOrient=" + cropOrient + ", movePercent=" + movePercent + "%, startPercent=" + startPercent + "%, endPercent=" + endPercent + "%]";
	}

	public CropInfo(Parcel in) {
		setAdjustableCropSize(false);
		
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {// !! Parcel 객체에 write 하는 순서는 read 하는 순서와 같아야 함. !!
		cropOrient = CORP_ORIENT.valueOf(in.readString());
		movePercent = in.readFloat();
		startPercent = in.readInt();
		endPercent = in.readInt();
		CROP_ACCURACY = in.readFloat();
		
		boolean[] arrBool = new boolean[2];
		in.readBooleanArray(arrBool);
		isAdjustableCropSize = arrBool[0];
		isCropped = arrBool[1];
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(cropOrient.name());
		dest.writeFloat(movePercent);
		dest.writeInt(startPercent);
		dest.writeInt(endPercent);
		dest.writeFloat(CROP_ACCURACY);
		dest.writeBooleanArray(new boolean[] { isAdjustableCropSize, isCropped });
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public CropInfo createFromParcel(Parcel in) {
			return new CropInfo(in);
		}

		public CropInfo[] newArray(int size) {
			return new CropInfo[size];
		}
	};

	private int getCropOrientToInt() {
		switch (cropOrient) {
		case NONE:
			return 0;
		case WIDTH:
			return 1;
		case HEIGHT:
			return 2;
		default:
			break;
		}
		return -1;
	}

	private void setCropOrientByInt(int orient) {
		switch (orient) {
		case 0:
			cropOrient = CORP_ORIENT.NONE;
			break;
		case 1:
			cropOrient = CORP_ORIENT.WIDTH;
			break;
		case 2:
			cropOrient = CORP_ORIENT.HEIGHT;
			break;
		default:
			break;
		}

	}

	public String getCropinfoByString() {
		return "" + getCropOrientToInt() + " " + movePercent + " " + startPercent + " " + endPercent;
	}

	public void setCropInfoByString(String info) {
		String[] s = info.split(" ");
		setCropOrientByInt(Integer.parseInt(s[0]));
		movePercent = Integer.parseInt(s[1]);
		startPercent = Integer.parseInt(s[2]);
		endPercent = Integer.parseInt(s[3]);
	}
}