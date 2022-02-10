package com.snaps.common.data.between;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;


public class BWPhotoMainResponse extends BaseResponse {
	
	private static final long serialVersionUID = -2496733948587447568L;
	
	@SerializedName("data")
	private List<BWPhotoDataResponse> dataList;
	
	@SerializedName("cursors")
	private BWPhotoCursorsResponse cursors;
	
	@SerializedName("expiry")
	private int expiry;

	public void set(BWPhotoMainResponse userPhotos) {
		if(userPhotos == null) return;
		
		if(userPhotos.dataList != null) {
			
			dataList = new ArrayList<BWPhotoDataResponse>();
			
			for(BWPhotoDataResponse data : userPhotos.dataList) {
				dataList.add(data);
			}
		}
		
		if(userPhotos.cursors != null) {
			cursors = new BWPhotoCursorsResponse();
			cursors.set(userPhotos.cursors);
		}
		
		expiry = userPhotos.expiry;
	}
	
	public List<BWPhotoDataResponse> getDataList() {
		return dataList;
	}

	public void setDataList(List<BWPhotoDataResponse> dataList) {
		this.dataList = dataList;
	}

	public BWPhotoCursorsResponse getCursors() {
		return cursors;
	}

	public void setCursors(BWPhotoCursorsResponse cursors) {
		this.cursors = cursors;
	}

	public int getExpiry() {
		return expiry;
	}

	public void setExpiry(int expiry) {
		this.expiry = expiry;
	}
	
	public boolean isExistData() {
		return dataList != null && !dataList.isEmpty();
	}
	
}
