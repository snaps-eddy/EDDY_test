package com.snaps.common.utils.imageloader.recoders;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class BaseCropInfo implements Parcelable, Serializable {
	
	private static final long serialVersionUID = 8199231649984008791L;
	protected boolean isAdjustableCropSize = false;
	protected boolean isCropped	= false;
	protected EffectFilerMaker effectFilerMaker = null;

	protected boolean shouldCreateFilter = false; //필터 적용 후 저장을 했는데, 로딩할 때 효과 필터가 적용 된 파일이 없을 경우 새로 생성 해 주어야 한다.

	private Bitmap.Config bitmapConfig = null;

	public boolean shouldCreateFilter() {
		return shouldCreateFilter;
	}

	public void setShouldCreateFilter(boolean shouldCreateFilter) {
		this.shouldCreateFilter = shouldCreateFilter;
	}

	public EffectFilerMaker getEffectFilerMaker() {
		return effectFilerMaker;
	}

	public void setEffectFilerMaker(EffectFilerMaker effectFilerMaker) {
		this.effectFilerMaker = effectFilerMaker;
	}

	public boolean isAdjustableCropSize() {
		return isAdjustableCropSize;
	}

	public void setAdjustableCropSize(boolean isAdjustableCropSize) {
		this.isAdjustableCropSize = isAdjustableCropSize;
	}

	public boolean isCropped() {
		return isCropped;
	}

	public void setCropped(boolean isCropped) {
		this.isCropped = isCropped;
	}

	public Config getBitmapConfig() {
		return bitmapConfig;
	}

	public void setBitmapConfig(Config bitmapConfig) {
		this.bitmapConfig = bitmapConfig;
	}

	private void readFromParcel(Parcel in) {// !! Parcel 객체에 write 하는 순서는 read 하는 순서와 같아야 함. !!
		
		boolean[] arrBool = new boolean[3];
		in.readBooleanArray(arrBool);
		isAdjustableCropSize = arrBool[0];
		isCropped = arrBool[1];
		shouldCreateFilter = arrBool[2];

		effectFilerMaker = in.readParcelable(effectFilerMaker.getClass().getClassLoader());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeBooleanArray(new boolean[] { isAdjustableCropSize, isCropped, shouldCreateFilter });

		dest.writeParcelable(effectFilerMaker, 0);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public BaseCropInfo() {
	}
	
	public BaseCropInfo(Parcel in) {
		readFromParcel(in);
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public BaseCropInfo createFromParcel(Parcel in) {
			return new BaseCropInfo(in);
		}

		public BaseCropInfo[] newArray(int size) {
			return new BaseCropInfo[size];
		}
	};
}
