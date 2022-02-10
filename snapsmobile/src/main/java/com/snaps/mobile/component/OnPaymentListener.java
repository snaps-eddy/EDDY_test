package com.snaps.mobile.component;

public interface OnPaymentListener {
	/***
	 * 결제가 완료가 되었을때 호출을 한다.
	 * 
	 * @param url
	 */
	void onPaymentResult(String url);
}
