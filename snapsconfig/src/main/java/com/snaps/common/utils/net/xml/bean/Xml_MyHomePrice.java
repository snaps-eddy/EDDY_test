package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

public class Xml_MyHomePrice {
	public List<MyPriceData> myPriceList = new ArrayList<Xml_MyHomePrice.MyPriceData>();

	public static class MyPriceData {
		public String F_PROD_CODE;
		public String F_PROD_NAME;
		public String F_SELL_PRICE;
		public String F_ORG_PRICE;
		public String F_MAX_PAGE;
		public String F_DLVR_MTHD;
		public String F_DLVR_DAY;
		public String F_DISC_RATE;

		public MyPriceData(String f_PROD_CODE, String f_PROD_NAME,
				String f_SELL_PRICE, String f_ORG_PRICE, String f_MAX_PAGE,
				String f_DLVR_MTHD, String f_DLVR_DAY , String f_DISC_RATE) {
			super();
			F_PROD_CODE = f_PROD_CODE;
			F_PROD_NAME = f_PROD_NAME;
			F_SELL_PRICE = f_SELL_PRICE;
			F_ORG_PRICE = f_ORG_PRICE;
			F_MAX_PAGE = f_MAX_PAGE;
			F_DLVR_MTHD = f_DLVR_MTHD;
			F_DLVR_DAY = f_DLVR_DAY;
			F_DISC_RATE = f_DISC_RATE;
		}

	}
}
/*
 * <SCENE ID="HM_INFO"> <ITEM> <HM_PROD_INFO>
 * <F_PROD_CODE>00800600080001</F_PROD_CODE> <F_PROD_NAME>콜라주</F_PROD_NAME>
 * <F_SELL_PRICE>16900</F_SELL_PRICE> <F_MAX_PAGE>20장</F_MAX_PAGE>
 * <F_DLVR_MTHD>무료배송</F_DLVR_MTHD> </HM_PROD_INFO> <HM_PROD_INFO>
 * <F_PROD_CODE>00800900050003</F_PROD_CODE> <F_PROD_NAME>카카오명함</F_PROD_NAME>
 * <F_SELL_PRICE>9900</F_SELL_PRICE> <F_MAX_PAGE>50매</F_MAX_PAGE>
 * <F_DLVR_MTHD>무료배송</F_DLVR_MTHD> </HM_PROD_INFO> <HM_PROD_INFO>
 * <F_PROD_CODE>00802100010001</F_PROD_CODE> <F_PROD_NAME>스티커킷</F_PROD_NAME>
 * <F_SELL_PRICE>7900</F_SELL_PRICE> <F_MAX_PAGE>90개</F_MAX_PAGE>
 * <F_DLVR_MTHD>무료배송</F_DLVR_MTHD> </HM_PROD_INFO> </ITEM> </SCENE>
 */