package com.snaps.common.data.order;

/**
 * 카카오결제, 가격, 배송지 정보
 * @author crjung
 *
 */
public class MyOrderInfo {
	// -- 카카오 결제 관련
	/** 카카오결제 id */
	public String kakao_pay_id;
	/** 카카오결제 hash 값 */
	public String kakao_checkout_hash;
	
	// -- 가격정보
	/** 결재방법 (012001:신용카드) */
	public String F_STTL_MTHD;
	/** 주문금액(배송비 제외 상품가격) */
	public int F_ORDR_AMNT;
	/** 쿠폰할인금액(전체할인가격) */
	public int F_DISCOUNT_AMNT;
	/** 결재금액(배송비 포함 전체가격) */
	public int F_STTL_AMNT;
	/** 배송비 */
	public int F_DLVR_AMNT;
	/** 택배구분값? */
	public String F_DLVR_MTHD = "011005";
	
	// -- 배송지정보
	/** snaps의 주문코드(ordercode) */
	public String F_ORDER_CODE;
	/** 주문자명 */
	public String F_ORDR_NAME;	
	/** 주문자휴대전화 */
	public String F_ORDR_CELL;	
	/** 주문자이메일 */
	public String F_ORDR_MAIL;	
	/** 받는이 이름 */
	public String F_RCPT_NAME;	
	/** 받는이 휴대전화 */
	public String F_RCPT_CELL;	
	/** 받는이이메일 */
	public String F_RCPT_MAIL;	
	/** 배송지우편번호 */
	public String F_RCPT_ZIP;	
	/** 배송지주소1 */
	public String F_RCPT_ADDR1;	
	/** 배송지주소2 */
	public String F_RCPT_ADDR2;	
	/** 배송지주소3(일본일때) */
	public String F_RCPT_ADDR3;	
	/** 배송메세지 */
	public String F_RMRK_CLMN;
	
	// 일본 주문의 경우 후리가나 추가됨.
	/** 주문자 후리가나 */
	public String F_ORDR_NAME_KTKN;	
	/** 받는이 후리가나 */
	public String F_RCPT_NAME_KTKN;
	
	/** 결제할 상품갯수 */
	public int selectCnt;
	/** 결제할 상품명 */
	public String orderItemName;
	/** 기본 배송금액 */
	public int baseDlvrAmnt;
	
	@Override
	public String toString() {
		return "MyOrderInfo [kakao_pay_id=" + kakao_pay_id + ", kakao_checkout_hash=" + kakao_checkout_hash + ", F_STTL_MTHD=" + F_STTL_MTHD + ", F_ORDR_AMNT=" + F_ORDR_AMNT + ", F_DISCOUNT_AMNT=" + F_DISCOUNT_AMNT + ", F_STTL_AMNT=" + F_STTL_AMNT + ", F_DLVR_AMNT=" + F_DLVR_AMNT + ", F_DLVR_MTHD=" + F_DLVR_MTHD + ", F_ORDER_CODE=" + F_ORDER_CODE + ", F_ORDR_NAME=" + F_ORDR_NAME + ", F_ORDR_CELL=" + F_ORDR_CELL + ", F_ORDR_MAIL=" + F_ORDR_MAIL + ", F_RCPT_NAME=" + F_RCPT_NAME + ", F_RCPT_CELL=" + F_RCPT_CELL + ", F_RCPT_MAIL=" + F_RCPT_MAIL + ", F_RCPT_ZIP=" + F_RCPT_ZIP + ", F_RCPT_ADDR1=" + F_RCPT_ADDR1 + ", F_RCPT_ADDR2=" + F_RCPT_ADDR2 + ", F_RCPT_ADDR3=" + F_RCPT_ADDR3 + ", F_RMRK_CLMN=" + F_RMRK_CLMN + ", F_ORDR_NAME_KTKN=" + F_ORDR_NAME_KTKN + ", F_RCPT_NAME_KTKN=" + F_RCPT_NAME_KTKN + ", selectCnt=" + selectCnt + ", orderItemName=" + orderItemName + ", baseDlvrAmnt=" + baseDlvrAmnt + "]";
	}
	
}
