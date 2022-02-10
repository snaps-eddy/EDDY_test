package com.snaps.mobile.activity.ui.menu.json;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.data.between.BaseResponse;

public class UINavigatorStoreMainResponse extends BaseResponse {

	private static final long serialVersionUID = -2496733948587447568L;

	@SerializedName("F_CLSS_NAME")
	private String F_CLSS_NAME;

	@SerializedName("F_USE_YORN")
	private String F_USE_YORN;

	@SerializedName("F_MENU_NEW")
	private String F_MENU_NEW;

	@SerializedName("F_MENU_NEW2")
	private String F_MENU_NEW2;

	@SerializedName("F_USE_YORN2")
	private String F_USE_YORN2;

	@SerializedName("data")
	private String data;
	
	@SerializedName("F_MENU_SNO")
	private String F_MENU_SNO;
	
	public int getF_MENU_SNO() {
		if(F_MENU_SNO == null) return 100;
		try {
			return Integer.parseInt(F_MENU_SNO);
		} catch (NumberFormatException e) {
			return 100;
		}
	}

	public void setF_MENU_SNO(String f_MENU_SNO) {
		F_MENU_SNO = f_MENU_SNO;
	}

	public String getF_CLSS_NAME() {
		return F_CLSS_NAME;
	}

	public void setF_CLSS_NAME(String f_CLSS_NAME) {
		F_CLSS_NAME = f_CLSS_NAME;
	}

	public String getF_USE_YORN() {
		return F_USE_YORN;
	}

	public void setF_USE_YORN(String f_USE_YORN) {
		F_USE_YORN = f_USE_YORN;
	}

	public String getF_MENU_NEW() {
		return F_MENU_NEW;
	}

	public void setF_MENU_NEW(String f_MENU_NEW) {
		F_MENU_NEW = f_MENU_NEW;
	}

	public String getF_MENU_NEW2() {
		return F_MENU_NEW2;
	}

	public void setF_MENU_NEW2(String f_MENU_NEW2) {
		F_MENU_NEW2 = f_MENU_NEW2;
	}

	public String getF_USE_YORN2() {
		return F_USE_YORN2;
	}

	public void setF_USE_YORN2(String f_USE_YORN2) {
		F_USE_YORN2 = f_USE_YORN2;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public String getDataKey() {
		String data = getData();
		if(data != null && data.contains("=")) {
			String[] arr = data.split("=");
			if(arr != null && arr.length > 1)
				return arr[1];
		}
		return null;
	}
}
