package com.snaps.common.structure.page;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.control.SnapsControl;

public class SnapsCalendarRecoverPage implements Parcelable, Serializable {

	private static final long serialVersionUID = 6682860049109763110L;

	private ArrayList<SnapsControl> layoutControls; 
	
	private ArrayList<MyPhotoSelectImageData> images = null;
	
	private ArrayList<String> thumbnailPaths = null;
	
	private int lastPageIdx = 0;
	
	public SnapsCalendarRecoverPage() {
		layoutControls = new ArrayList<SnapsControl>();
	}
	
	public int getLastPageIdx() {
		return lastPageIdx;
	}

	public void setLastPageIdx(int lastPageIdx) {
		this.lastPageIdx = lastPageIdx;
	}

	public ArrayList<SnapsControl> getLayouts() {
		return layoutControls;
	}

	public void setImageList(ArrayList<MyPhotoSelectImageData> images) {
		this.images = images;
	}
	
	public ArrayList<MyPhotoSelectImageData> getImageList() {
		return images;
	}
	
	public ArrayList<String> getThumbnailPaths() {
		return thumbnailPaths;
	}

	public void setThumbnailPaths(ArrayList<String> thumbnailPaths) {
		this.thumbnailPaths = thumbnailPaths;
	}

	public SnapsCalendarRecoverPage(Parcel in) {
		readFromParcel(in);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(layoutControls);
		dest.writeTypedList(images);
		dest.writeList(thumbnailPaths);
		dest.writeInt(lastPageIdx);
	}
	
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		in.readTypedList(layoutControls, SnapsControl.CREATOR);
		in.readTypedList(images, MyPhotoSelectImageData.CREATOR);
		in.readList(thumbnailPaths, String.class.getClassLoader());
		lastPageIdx = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		@Override
		public SnapsCalendarRecoverPage createFromParcel(Parcel in) {
			return new SnapsCalendarRecoverPage(in);
		}

		@Override
		public SnapsCalendarRecoverPage[] newArray(int size) {
			return new SnapsCalendarRecoverPage[size];
		}
	};
}
