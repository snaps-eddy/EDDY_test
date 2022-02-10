package com.snaps.mobile.activity.ui.menu.json;

import java.util.List;

import com.snaps.common.data.between.BaseResponse;


public class UINavigatorProducts extends BaseResponse {
	
	private static final long serialVersionUID = -2496733948587447568L;
	
	private String prod_name;
	
	private List<UINavigatorAndroidResponse> arrAndroidData;

	public String getProd_name() {
		return prod_name;
	}

	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}

	public boolean isMultiSubMenu() {
		return arrAndroidData != null && arrAndroidData.size() > 1;
	}

	public List<UINavigatorAndroidResponse> getArrAndroidData() {
		return arrAndroidData;
	}

	public void setArrAndroidData(List<UINavigatorAndroidResponse> arrAndroidData) {
		this.arrAndroidData = arrAndroidData;
	}
}
