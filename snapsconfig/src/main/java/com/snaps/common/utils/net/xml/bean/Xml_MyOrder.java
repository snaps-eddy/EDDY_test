package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

public class Xml_MyOrder {
	public int F_PAGE_CNT;
	public List<MyOrderData> myOrderList = new ArrayList<Xml_MyOrder.MyOrderData>();
	
	public static class MyOrderData {
		public String F_ORDER_CODE;
		public String F_ORDER_STATUS;
		public String F_PROJ_NAME;
		public String F_STTL_AMNT;
		public String F_REG_DATE;
		public String F_SIMG_PATH;
		public String F_DLVR_NUMB;
		
		public MyOrderData(String f_ORDER_CODE, String f_ORDER_STATUS, String f_PROJ_NAME, String f_STTL_AMNT, String f_REG_DATE, String f_SIMG_PATH , String f_DLVR_NUMB) {
			F_ORDER_CODE = f_ORDER_CODE;
			F_ORDER_STATUS = f_ORDER_STATUS;
			F_PROJ_NAME = f_PROJ_NAME;
			F_STTL_AMNT = f_STTL_AMNT;
			F_REG_DATE = f_REG_DATE;
			F_SIMG_PATH = f_SIMG_PATH;
			F_DLVR_NUMB = f_DLVR_NUMB;
		}
	}
	
	/*<SCENE ID="MY_PRJ">
<ITEM>
<MY_PRJ_LST>
<F_PROJ_CODE>20130515001404</F_PROJ_CODE>
<F_SIMG_PATH/>
<F_PROJ_NAME>2013/05/15스티커 킷</F_PROJ_NAME>
<F_PROD_NAME>스티커킷</F_PROD_NAME>
<F_REG_DATE>20130515</F_REG_DATE>
</MY_PRJ_LST>
<MY_PRJ_LST>
<F_PROJ_CODE>20130513004233</F_PROJ_CODE>
<F_SIMG_PATH/>
<F_PROJ_NAME>2013/05/13콜라주</F_PROJ_NAME>
<F_PROD_NAME>스티커킷</F_PROD_NAME>
<F_REG_DATE>20130513</F_REG_DATE>
</MY_PRJ_LST>
<MY_PRJ_LST>
<F_PROJ_CODE>20130513004393</F_PROJ_CODE>
<F_SIMG_PATH/>
<F_PROJ_NAME>2013/05/13콜라주</F_PROJ_NAME>
<F_PROD_NAME>스티커킷</F_PROD_NAME>
<F_REG_DATE>20130513</F_REG_DATE>
</MY_PRJ_LST>
<MY_PRJ_LST>
<F_PROJ_CODE>20130514006043</F_PROJ_CODE>
<F_SIMG_PATH/>
<F_PROJ_NAME>2013/05/14콜라주</F_PROJ_NAME>
<F_PROD_NAME>스티커킷</F_PROD_NAME>
<F_REG_DATE>20130514</F_REG_DATE>
</MY_PRJ_LST>
</ITEM>
</SCENE>*/
}
