package com.snaps.common.data.between;

import com.google.gson.annotations.SerializedName;


public class BWPhotoLikesResponse extends BaseResponse {
	
	private static final long serialVersionUID = -2496733948587447568L;
	
	@SerializedName("from")
	private String from;
	
	@SerializedName("created_time")
	private String created_time;
	
	public void set(BWPhotoLikesResponse likes) {
		if(likes == null) return;
		from = likes.from;
		created_time = likes.created_time;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getCreated_time() {
		return created_time;
	}

	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}
}
