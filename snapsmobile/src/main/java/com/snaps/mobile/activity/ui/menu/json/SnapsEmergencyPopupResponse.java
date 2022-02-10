package com.snaps.mobile.activity.ui.menu.json;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.data.between.BaseResponse;

public class SnapsEmergencyPopupResponse extends BaseResponse {

	private static final long serialVersionUID = 3833645865598906672L;

	@SerializedName("use")
	private String use;
	
	@SerializedName("version")
	private String version;
	
	@SerializedName("title")
	private String title;

	@SerializedName("msg")
	private String msg;
}
