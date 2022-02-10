package com.snaps.common.data.between;

import com.snaps.common.data.between.BaseResponse;

public interface IBetweenAPIResultListener {
	public void onResultSuccess(BaseResponse response);
	public void onResultFailed(String msg);
}
