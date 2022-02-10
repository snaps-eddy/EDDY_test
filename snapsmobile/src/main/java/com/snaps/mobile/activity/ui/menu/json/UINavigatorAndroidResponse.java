package com.snaps.mobile.activity.ui.menu.json;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.data.between.BaseResponse;

public class UINavigatorAndroidResponse extends BaseResponse {

	private static final long serialVersionUID = -2496733948587447568L;

	@SerializedName("name")
	private String name;

	@SerializedName("menu_new")
	private String menu_new;

	@SerializedName("data")
	private String data;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMenu_new() {
		return menu_new;
	}

	public void setMenu_new(String menu_new) {
		this.menu_new = menu_new;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public boolean isMenuNew() {
		return BaseResponse.parseBool(getMenu_new());
	}

}
