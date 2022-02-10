package com.snaps.common.utils.net.xml.bean;

import com.snaps.common.utils.log.Dlog;

import java.util.ArrayList;
import java.util.List;

public class Xml_MyArtwork {
	private static final String TAG = Xml_MyArtwork.class.getSimpleName();
	public String F_DLVY_DATE;
	public int F_POST_PRICE;
	public int F_PAGE_CNT;
	public List<MyArtworkData> myartworkList = new ArrayList<Xml_MyArtwork.MyArtworkData>();

	public void setF_POST_PRICE(String f_POST_PRICE) {
		try {
			if (f_POST_PRICE != null && f_POST_PRICE.length() > 0)
				F_POST_PRICE = Integer.valueOf(f_POST_PRICE);
		} catch (NumberFormatException e) {
			Dlog.e(TAG, e);
		}
	}

	public static class MyArtworkData {
		public String F_PROJ_CODE;
		public String F_SIMG_PATH;
		public String F_PROJ_NAME;
		public String F_PROD_NAME;
		public String F_PROD_CODE;
		public String F_REG_DATE;

		// 장바구니 항목
		public int F_PROJ_CNT = 0;
		//담품 하나의 판매(세일) 가격 
		public int F_UNIT_PRICE = 0;
		//단품 하나의 원래 가격 
		public int F_UNIT_ORG_PRICE = 0;
		// 세일인 경우 세일 가격...
		public int F_ORDER_PRICE = 0;
		// 원래가
		public int F_ORDR_ORG_PRICE = 0;

		/**
		 * 작품상태
		 * 
		 * @params 146000 작품
		 * @params 146001, 146002 장바구니
		 * @params 146003 결재완료
		 */
		public String F_BAG_STAT;

		// 별도데이터
		public int cartTotalAmout;// 현재 상품의 전체가격
		public boolean isSelect = false;// 장바구니 선택여부
		public int f_discount_price = 0;// 할인가격
		public int f_remain_price = -1;// 할인이 적용된 가격

		/**
		 * 내작품함 조회결과
		 * 
		 * @param f_PROJ_CODE
		 * @param f_SIMG_PATH
		 * @param f_PROJ_NAME
		 * @param f_PROD_NAME
		 * @param f_BAG_STAT
		 */
		public MyArtworkData(String f_PROJ_CODE, String f_SIMG_PATH, String f_PROJ_NAME, String f_PROD_NAME, String f_PROD_CODE, String f_REG_DATE, String f_BAG_STAT) {
			F_PROJ_CODE = f_PROJ_CODE;
			F_SIMG_PATH = f_SIMG_PATH;
			F_PROJ_NAME = f_PROJ_NAME;
			F_PROD_NAME = f_PROD_NAME;
			F_PROD_CODE = f_PROD_CODE;
			F_REG_DATE = f_REG_DATE;
			F_BAG_STAT = f_BAG_STAT;
		}

		/**
		 * 장바구니 조회결과
		 * 
		 * @param f_PROJ_CODE
		 * @param f_SIMG_PATH
		 * @param f_PROJ_NAME
		 * @param f_PROD_NAME
		 * @param f_PRICE
		 * @param f_COUNT
		 */
		public MyArtworkData(String f_PROJ_CODE, String f_SIMG_PATH, String f_PROJ_NAME, String f_PROD_CODE, String f_PROD_NAME, String f_REG_DATE, String f_PROJ_CNT, String f_ORDER_PRICE,
				String f_UNIT_PRICE, String f_ORDR_ORG_PRICE,String f_UNIT_ORG_PRICE) {
			F_PROJ_CODE = f_PROJ_CODE;
			F_SIMG_PATH = f_SIMG_PATH;
			F_PROJ_NAME = f_PROJ_NAME;
			F_PROD_CODE = f_PROD_CODE;
			F_PROD_NAME = f_PROD_NAME;
			F_REG_DATE = f_REG_DATE;
			try {
				if (f_PROJ_CNT != null && f_PROJ_CNT.length() > 0)
					F_PROJ_CNT = Integer.valueOf(f_PROJ_CNT);
			} catch (NumberFormatException e) {
				Dlog.e(TAG, e);
			}
			try {
				if (f_ORDER_PRICE != null && f_ORDER_PRICE.length() > 0)
					F_ORDER_PRICE = Integer.valueOf(f_ORDER_PRICE);
			} catch (NumberFormatException e) {
				Dlog.e(TAG, e);
			}

			try {
				if (f_UNIT_PRICE != null && f_UNIT_PRICE.length() > 0)
					F_UNIT_PRICE = Integer.valueOf(f_UNIT_PRICE);
			} catch (NumberFormatException e) {
				Dlog.e(TAG, e);
			}

			try {
				if (f_ORDR_ORG_PRICE != null && f_ORDR_ORG_PRICE.length() > 0)
					F_ORDR_ORG_PRICE = Integer.valueOf(f_ORDR_ORG_PRICE);
			} catch (NumberFormatException e) {
				Dlog.e(TAG, e);
			}
			
			try {
				if (f_UNIT_ORG_PRICE != null && f_UNIT_ORG_PRICE.length() > 0)
					F_UNIT_ORG_PRICE = Integer.valueOf(f_UNIT_ORG_PRICE);
			} catch (NumberFormatException e) {
				Dlog.e(TAG, e);
			}
		}
	}

	/*
	 * <SCENE ID="MY_PRJ"> <ITEM> <MY_PRJ_LST>
	 * <F_PROJ_CODE>20130515001404</F_PROJ_CODE> <F_SIMG_PATH/>
	 * <F_PROJ_NAME>2013/05/15스티커 킷</F_PROJ_NAME>
	 * <F_PROD_NAME>스티커킷</F_PROD_NAME> <F_REG_DATE>20130515</F_REG_DATE>
	 * </MY_PRJ_LST> <MY_PRJ_LST> <F_PROJ_CODE>20130513004233</F_PROJ_CODE>
	 * <F_SIMG_PATH/> <F_PROJ_NAME>2013/05/13콜라주</F_PROJ_NAME>
	 * <F_PROD_NAME>스티커킷</F_PROD_NAME> <F_REG_DATE>20130513</F_REG_DATE>
	 * </MY_PRJ_LST> <MY_PRJ_LST> <F_PROJ_CODE>20130513004393</F_PROJ_CODE>
	 * <F_SIMG_PATH/> <F_PROJ_NAME>2013/05/13콜라주</F_PROJ_NAME>
	 * <F_PROD_NAME>스티커킷</F_PROD_NAME> <F_REG_DATE>20130513</F_REG_DATE>
	 * </MY_PRJ_LST> <MY_PRJ_LST> <F_PROJ_CODE>20130514006043</F_PROJ_CODE>
	 * <F_SIMG_PATH/> <F_PROJ_NAME>2013/05/14콜라주</F_PROJ_NAME>
	 * <F_PROD_NAME>스티커킷</F_PROD_NAME> <F_REG_DATE>20130514</F_REG_DATE>
	 * </MY_PRJ_LST> </ITEM> </SCENE>
	 */
}
