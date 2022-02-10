package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

public class Xml_MyArtworkDetail {
	public String F_PROJ_CODE;
	public String F_PROJ_NAME;
	public String F_PROD_CODE;
	public String F_USER_NAME;
	public String F_USER_NO;
	
	public List<MyArtworkDetail> myartworkDetail = new ArrayList<Xml_MyArtworkDetail.MyArtworkDetail>();
	
	public static class MyArtworkDetail {
		public String idx;
		public String source;
		public MyArtworkDetail(String idx, String source) {
			this.idx = idx;
			this.source = source;
		}
	}
	
	/*<scene id="MY_DTL">
<image_info>
<F_PROJ_CODE>20130515001404</F_PROJ_CODE>
<F_PROJ_NAME>2013/05/15스티커 킷</F_PROJ_NAME>
<images>
<image idx="0" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_0.jpg"/>
<image idx="1" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_1.jpg"/>
<image idx="2" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_2.jpg"/>
<image idx="3" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_3.jpg"/>
<image idx="4" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_4.jpg"/>
<image idx="5" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_5.jpg"/>
<image idx="6" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_6.jpg"/>
<image idx="7" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_7.jpg"/>
<image idx="8" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_8.jpg"/>
<image idx="9" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_9.jpg"/>
<image idx="10" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_10.jpg"/>
<image idx="11" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_11.jpg"/>
<image idx="12" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_12.jpg"/>
<image idx="13" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_13.jpg"/>
<image idx="14" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_14.jpg"/>
<image idx="15" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_15.jpg"/>
<image idx="16" source="/Upload/Cart15/KOR0000/2013/Q2/20130515/20130515001404/thum/2013051510172984108_16.jpg"/>
</images>
</image_info>
</scene>*/
}
