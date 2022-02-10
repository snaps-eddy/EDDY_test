package com.snaps.common.utils.net.xml.bean;


public class Xml_MyOrderReqInfo {
	public String CHECKOUT_HASH;
	public String STATUS;
	public String PAY_ID;
	public String F_ORDER_CODE;
	
	public Xml_MyOrderReqInfo(String cHECKOUT_HASH, String sTATUS, String pAY_ID, String f_ORDER_CODE) {
		CHECKOUT_HASH = cHECKOUT_HASH;
		STATUS = sTATUS;
		PAY_ID = pAY_ID;
		F_ORDER_CODE = f_ORDER_CODE;
	}
	
	/*<CHECKOUT_HASH	카카오에서 전송되는 해쉬값
STATUS	전송상태
PAY_ID	카카오 결재요청번호
F_order_code	주문번호
*/
}
