package com.snaps.mobile.activity.ui.menu.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.data.between.BaseResponse;


public class UINavigatorMainResponse extends BaseResponse {
	
	private static final long serialVersionUID = -2496733948587447568L;
	
	@SerializedName("res_version")
	private String resVersion;
	
	@SerializedName("param")
	private String param;
	
	@SerializedName("store_main")
	private List<UINavigatorStoreMainResponse> storeMainList;
	
	private Map<String, Object> mapJsonSubList = new HashMap<String, Object>();
	
	private Map<String, UINavigatorProducts> subMenus = new HashMap<String, UINavigatorProducts>();
	
	private Map<String, Object> mapJsonNativeUIList = new HashMap<String, Object>();
	
	private Map<String, List<SnapsStoreProductResponse>> NativeUIInfos = new HashMap<String, List<SnapsStoreProductResponse>>();
	
	public Map<String, Object> getMapJsonNativeUIList() {
		return mapJsonNativeUIList;
	}

	public void setMapJsonNativeUIList(Map<String, Object> mapJsonNativeUIList) {
		this.mapJsonNativeUIList = mapJsonNativeUIList;
	}
	
	public Map<String, List<SnapsStoreProductResponse>> getNativeUIInfos() {
		return NativeUIInfos;
	}

	public void setNativeUIInfos(
			Map<String, List<SnapsStoreProductResponse>> nativeUIInfos) {
		NativeUIInfos = nativeUIInfos;
	}

	public Map<String, UINavigatorProducts> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(Map<String, UINavigatorProducts> subMenus) {
		this.subMenus = subMenus;
	}

	public Map<String, Object> getMapJsonSubList() {
		return mapJsonSubList;
	}

	public void setMapJsonSubList(Map<String, Object> mapJsonSubList) {
		this.mapJsonSubList = mapJsonSubList;
	}

	public String getResVersion() {
		return resVersion;
	}

	public void setResVersion(String resVersion) {
		this.resVersion = resVersion;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public List<UINavigatorStoreMainResponse> getStoreMainList() {
		return storeMainList;
	}

	public void setStoreMainList(List<UINavigatorStoreMainResponse> storeMainList) {
		this.storeMainList = storeMainList;
	}
}
