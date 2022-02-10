package com.snaps.common.utils.imageloader.recoders;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class AdjustableCropInfo extends BaseCropInfo implements Parcelable, Serializable {

	private static final long serialVersionUID = 3205517744790488488L;
	
	private CropImageRect imgRect;
	private CropImageRect clipRect;
	private String effectType;

	public AdjustableCropInfo() {
		setAdjustableCropSize(true);
	}
	
	public void set(AdjustableCropInfo cropInfo) {
		if(cropInfo == null) return;
		
		if(cropInfo.imgRect != null) {
			imgRect = new CropImageRect();
			imgRect.set(cropInfo.imgRect);
		}
		
		if(cropInfo.clipRect != null) {
			clipRect = new CropImageRect();
			clipRect.set(cropInfo.clipRect);
		}
		
		this.effectType = cropInfo.effectType;
		this.isAdjustableCropSize = cropInfo.isAdjustableCropSize;
		this.isCropped = cropInfo.isCropped;
	}

	public AdjustableCropInfo(CropImageRect img, CropImageRect clip) {
		setAdjustableCropSize(true);
		setImgRect(img);
		setClipRect(clip);

		checkCropped();
	}

	public AdjustableCropInfo(Parcel in) {
		readFromParcel(in);
	}
	
	public String getEffectType() {
		return effectType;
	}

	public void setEffectType(String effectType) {
		this.effectType = effectType;
	}

	public CropImageRect getImgRect() {
		return imgRect;
	}

	public void setImgRect(CropImageRect imgRect) {
		this.imgRect = imgRect;
	}

	public CropImageRect getClipRect() {
		return clipRect;
	}

	public void setClipRect(CropImageRect clipRect) {
		this.clipRect = clipRect;
	}

	private void readFromParcel(Parcel in) {
		imgRect = in.readParcelable(CropImageRect.class.getClassLoader());
		clipRect = in.readParcelable(CropImageRect.class.getClassLoader());

		boolean[] arrBool = new boolean[2];
		in.readBooleanArray(arrBool);
		isAdjustableCropSize = arrBool[0];
		isCropped = arrBool[1];
		
		effectType = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeParcelable(imgRect, 0);
		dest.writeParcelable(clipRect, 0);

		dest.writeBooleanArray(new boolean[] { isAdjustableCropSize, isCropped });
		
		dest.writeString(effectType);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	private void checkCropped() {
		isCropped = true;
	}

	public AdjustableCropInfo getAdjustedCropInfo() throws Exception {
		return this;
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public AdjustableCropInfo createFromParcel(Parcel in) {
			return new AdjustableCropInfo(in);
		}

		public AdjustableCropInfo[] newArray(int size) {
			return new AdjustableCropInfo[size];
		}
	};

	public static class CropImageRect implements Parcelable, Serializable {
		private static final long serialVersionUID = -2655799900540392885L;
		
		public float resWidth;
		public float resHeight;
		public float width;
		public float height;
		public float centerX;
		public float centerY;
		public float rotate; //90, 180, 270 방향으로 도는 각도
		public float angle; //패닝에 의해 변한 자유 회전 각도
		public float scaleX;
		public float scaleY;
		public float movedX;
		public float movedY;

		public float[] matrixValue = new float[9];

		// public float getOriginWidth() {
		// return width / scaleX;
		// }
		//
		// public float getOriginHeight() {
		// return height / scaleY;
		// }
		
		public void set(CropImageRect rect) {
			if(rect == null) return;
			
			this.resWidth = rect.resWidth;
			this.resHeight = rect.resHeight;
			this.width = rect.width;
			this.height = rect.height;
			this.centerX = rect.centerX;
			this.centerY = rect.centerY;
			this.rotate = rect.rotate;
			this.angle = rect.angle;
			this.scaleX = rect.scaleX;
			this.scaleY = rect.scaleY;
			this.movedX = rect.movedX;
			this.movedY = rect.movedY;
			if(rect.matrixValue != null)
				this.matrixValue = rect.matrixValue.clone();
		}
		
		public void clear() {
			resWidth = 0;
			resHeight = 0;
			width = 0;
			height = 0;
			centerX = 0;
			centerY = 0;
			rotate = 0;
			angle = 0;
			scaleX = 0;
			scaleY = 0;
			movedX = 0;
			movedY = 0;
			matrixValue = new float[9];
		}

		@Override
		public int describeContents() {
			return 0;
		}

		public CropImageRect() {
		}

		public CropImageRect(Parcel in) {
			readFromParcel(in);
		}

		private void readFromParcel(Parcel in) {// !! Parcel 객체에 write 하는 순서는
												// read 하는 순서와 같아야 함. !!
			resWidth = in.readFloat();
			resHeight = in.readFloat();
			width = in.readFloat();
			height = in.readFloat();
			centerX = in.readFloat();
			centerY = in.readFloat();
			rotate = in.readFloat();
			angle = in.readFloat();
			scaleX = in.readFloat();
			scaleY = in.readFloat();
			movedX = in.readFloat();
			movedY = in.readFloat();

			in.readFloatArray(matrixValue);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeFloat(resWidth);
			dest.writeFloat(resHeight);
			dest.writeFloat(width);
			dest.writeFloat(height);
			dest.writeFloat(centerX);
			dest.writeFloat(centerY);
			dest.writeFloat(rotate);
			dest.writeFloat(angle);
			dest.writeFloat(scaleX);
			dest.writeFloat(scaleY);
			dest.writeFloat(movedX);
			dest.writeFloat(movedY);

			dest.writeFloatArray(matrixValue);
		}

		public static final Parcelable.Creator<CropImageRect> CREATOR = new Parcelable.Creator<CropImageRect>() {
			public CropImageRect createFromParcel(Parcel in) {
				return new CropImageRect(in);
			}

			public CropImageRect[] newArray(int size) {
				return new CropImageRect[size];
			}
		};
	}
}