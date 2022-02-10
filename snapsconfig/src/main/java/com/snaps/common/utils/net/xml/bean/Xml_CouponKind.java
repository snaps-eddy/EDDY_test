package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

public class Xml_CouponKind {
	public List<CouponKindInfo> couponKindList = new ArrayList<Xml_CouponKind.CouponKindInfo>();

	public static class CouponKindInfo {
		public String F_CLSS_CODE; // 쿠폰분류코드
		public String F_CLSS_NAME; // 쿠폰분류명
		public String F_CLSS_DIGIT; // 쿠폰자릿수 포
		public CouponKindInfo(String F_CLSS_CODE, String F_CLSS_NAME, String F_CLSS_DIGIT) {
			this.F_CLSS_CODE = F_CLSS_CODE;
			this.F_CLSS_NAME = F_CLSS_NAME;
			this.F_CLSS_DIGIT = F_CLSS_DIGIT;

		}
	}
}
