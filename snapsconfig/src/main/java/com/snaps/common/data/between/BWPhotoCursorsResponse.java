package com.snaps.common.data.between;

import com.google.gson.annotations.SerializedName;



public class BWPhotoCursorsResponse extends BaseResponse {
	
	private static final long serialVersionUID = -2496733948587447568L;
	
	@SerializedName("next_page")
	private String next_page;
	
	public void set(BWPhotoCursorsResponse cursors) {
		if(cursors == null) return;
		next_page = cursors.next_page;
	}

	public String getNext_page() {
		return next_page;
	}

	public void setNext_page(String next_page) {
		this.next_page = next_page;
	}
}
