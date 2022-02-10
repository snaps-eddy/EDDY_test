package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

public class Xml_Notice {
	public int F_PAGE_CNT;
	public List<NoticeData> noticeList = new ArrayList<Xml_Notice.NoticeData>();
	
	public static class NoticeData {
		public String F_TITLE;
		public String F_CONTENTS;
		public String F_REG_DATE;
		public NoticeData(String f_TITLE, String f_CONTENTS, String f_REG_DATE) {
			F_TITLE = f_TITLE;
			F_CONTENTS = f_CONTENTS;
			F_REG_DATE = f_REG_DATE;
		}
	}
	
	/*<SCENE ID="MY_PBL">
<ITEM>
<MY_PBL_CNT>
<F_PAGE_CNT>1</F_PAGE_CNT>
</MY_PBL_CNT>
</ITEM>
<ITEM>
<MY_PBL_LST>
<F_FLAG>161001</F_FLAG>
<F_SEQ>205301</F_SEQ>
<F_TITLE>근로자의 날 휴무 공지 - 5월 1일 수요일(정상 배송 합니다.)</F_TITLE>
<F_CONTENTS>
<P>안녕하세요.</P> <P>스냅스를 사랑해 마지않는 모든 고객님들!</P> <P>&nbsp;</P> <P>5월 1일 근로자의 날을 맞이하여</P> <P>스냅스 임직원 모두 하루동안의 달콤한 임시휴일을 갖기로 결정하였습니다.</P> <P>&nbsp;</P> <P>저희 스냅스 직원들도 모두 근로자니까요^^; </P> <P>이해해주실꺼죠?</P> <P>&nbsp;</P> <P>급한 문의가 있으시면 게시판을 이용해 주시고</P> <P>제작 및 배송은 정상 처리 되오니 걱정하지 마세요~!</P> <P>&nbsp;</P> <P>임시휴업공지 : 5월 1일 수요일 (근로자의 날)</P> <P>목요일부터는 모든 업무가 정상 처리됩니다.<BR></P>
</F_CONTENTS>
<F_REG_DATE>2013.05.02</F_REG_DATE>
</MY_PBL_LST>
<MY_PBL_LST>
<F_FLAG>161001</F_FLAG>
<F_SEQ>205300</F_SEQ>
<F_TITLE>모바일 공지사항 신규</F_TITLE>
<F_CONTENTS>공지사항 1번....</F_CONTENTS>
<F_REG_DATE>2013.05.02</F_REG_DATE>
</MY_PBL_LST>
</ITEM>
</SCENE>*/
}
