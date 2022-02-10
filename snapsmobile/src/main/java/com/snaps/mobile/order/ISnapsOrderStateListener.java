package com.snaps.mobile.order;

public interface ISnapsOrderStateListener {

	int ORDER_STATE_PREPARING = -99;
	int ORDER_STATE_STOP 		= 0;
	int ORDER_STATE_UPLOADING = 1;
	int ORDER_STATE_CANCEL = 200;

	void onOrderStateChanged(int state);
}
