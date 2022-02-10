package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

public class Xml_EventList {
	public List<EventApply> eventList = new ArrayList<EventApply>();
	
	public static class EventApply {
		public String F_PROD_CODE;
		public String F_WIN_YORN;
		public EventApply(String f_PROD_CODE, String f_WIN_YORN) {
			F_PROD_CODE = f_PROD_CODE;
			F_WIN_YORN = f_WIN_YORN;
		}
	}
	
	/*<SCENE ID="EV_LST">
<ITEM>
<EV_APPLY_LST>
<F_PROD_CODE>00802100010001</F_PROD_CODE>
<F_WIN_YORN>N</F_WIN_YORN>
</EV_APPLY_LST>
<EV_APPLY_LST>
<F_PROD_CODE>00800600080001</F_PROD_CODE>
<F_WIN_YORN>N</F_WIN_YORN>
</EV_APPLY_LST>
<EV_APPLY_LST>
<F_PROD_CODE>00800900050003</F_PROD_CODE>
<F_WIN_YORN>N</F_WIN_YORN>
</EV_APPLY_LST>
</ITEM>
</SCENE>*/
}
