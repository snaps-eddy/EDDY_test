package com.snaps.common.utils.net.xml.bean;


public class Xml_KakaoBillConfirm {
	public String status;
	public String pay_id;
	public String external_order_id;
	public String checkout_hash;
	public String pay_by;
	public String amount;
	public String paid_at;
	public String order_status;
	public String remain_amount;
	public String message;
	public String allmsg;
	
	public Xml_KakaoBillConfirm(String status, String pay_id, String external_order_id, String checkout_hash, String pay_by, String amount, String paid_at, String order_status, String remain_amount, String message) {
		this.status = status;
		this.pay_id = pay_id;
		this.external_order_id = external_order_id;
		this.checkout_hash = checkout_hash;
		this.pay_by = pay_by;
		this.amount = amount;
		this.paid_at = paid_at;
		this.order_status = order_status;
		this.remain_amount = remain_amount;
		this.message = message;
		this.allmsg = "/status="+ status + "/pay_id=" + pay_id + "/external_order_id=" +external_order_id + "/checkout_hash=" + checkout_hash 
				+ "/pay_by=" + pay_by + "/amount=" + amount + "/paid_at=" + paid_at + "/order_status=" + order_status + "/remain_amount=" + remain_amount + "/message=" + message;
	}
	
//	public String AlltoString()
//	{
//		allmsg = "/status="+ status + "/pay_id=" + pay_id + "/external_order_id=" +external_order_id + "/checkout_hash=" + checkout_hash 
//				+ "/pay_by=" + pay_by + "/amount=" + amount + "/paid_at=" + paid_at + "/order_status=" + order_status + "/remain_amount=" + remain_amount + "/message=" + message;
//		return allmsg;
//	}
	
	/*status	상태값
pay_id	카카오 결재요청번호
external_order_id	스냅스 주문코드
checkout_hash	주문 위변조 방지위한 해쉬값
pay_by	결제수단
amount	최종결재금액
paid_at	결재완료일자
order_status	결재완료/취소완료
remain_amount	취소되지 않은 결재번호
message	오류일경우 메시지 처리
*/
}
