package com.snaps.mobile.utils.kakao;

import com.snaps.common.utils.constant.SnapsAPI;

/**
 * 카카오 결제 관련 상수들
 * @author crjung
 *
 */
public class KakaoConst {
	/** 카카오에서 발금한 결제 서비스 인증 티켓 */
	public static final String CHECKOUT_TICKER = "aac546ff80e53099f3650a219a7410d0";	// TODO : 실서버 티켓. 테스트서버용은 snaps_kakao로..
	public static final String APP_SCHEME_URL = "kakao" + KakaoConst.CLIENT_ID + "://snapscheckout/isp_payment_result";//"kakao87801776877628656://checkout/isp_payment_result";// kakao89647209812816817
	/** 카카오 주문요청 url(post로 던지면 pay_id, checkout_hash 를 얻어 주문페이지에 요청을 날릴 수 있다.) */
	public static final String BILL_REQUEST_URL = "http://sandbox-checkout-api.kakao.com/order/create_order";
	/** 카카오 결제내역 확인 url 
	 * parameter : checkout_ticket, pay_id, checkout_hash */
	public static final String BILLED_CHECK_URL = "http://sandbox-checkout-api.kakao.com/payment/payment_result";
	
	/** 카카오 결제 시 결제완료 후 보여질 snaps쪽 결제완료 Redirect Url */
	public static final String KAKAO_REDIRECT_URL = SnapsAPI.WEB_DOMAIN()+"/mobile/jsp/redirectUrl.jsp";
	
	
	/** 	카카오 스토리 연동 Key		**/
	public static String CLIENT_ID = "89483842363180881";
	public static String CLIENT_SECRET = "n-KieULAEfAfUn4uyNZwIkYhbWnzgWNSlHKvdoF5hWL6mEVgtfP7-nm0A_bay_eYqSbnf0VTVOElhrzRPRsBPQ";
	public static String CLIENT_REDIRECT_URI = "snapsmobilekr://exec";
	public static final String PREF_KEY = "test.kakao.story.auth.pref.key";
	
	
	/** webview로 보여질 카카오 주문페이지 Real 여부[true:REAL, false:SANDBOX] */
	static final boolean IS_REAL = true;
	static final String SANDBOX = "http://sandbox-checkout-web.kakao.com/";
	static final String REAL = "https://checkout-web.kakao.com/";
	
	/** 카카오 주문페이지 url
	 * @params pay_id=541
	 * @params checkout_hash=339a12f */
	public static final String BILLING_PAGE_URL = (IS_REAL ? REAL : SANDBOX) + "checkout/order";
	
	/** 테스트 페이지 url */
	public static final String TEST_BILLING_PAGE_URL = "http://sandbox-checkout-web.kakao.com/test/test_order";
}
