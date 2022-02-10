package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

public class Xml_QnA {
	public int F_PAGE_CNT;
	public List<QnAData> qnaList = new ArrayList<Xml_QnA.QnAData>();
	
	public static class QnAData {
		public String F_DATA_TYPE;
		public String F_BBS_NUMB;
		public String F_BBS_CONTENTS;
		public String F_REG_DATE;
		
		public QnAData(String f_DATA_TYPE, String f_BBS_NUMB, String f_BBS_CONTENTS, String f_REG_DATE) {
			F_DATA_TYPE = f_DATA_TYPE;
			F_BBS_NUMB = f_BBS_NUMB;
			F_BBS_CONTENTS = f_BBS_CONTENTS;
			F_REG_DATE = f_REG_DATE;
		}
	}
	
	/*
	 * <SCENE ID="MY_C1">
<ITEM>
<MY_C1_CNT>
<F_PAGE_CNT>0</F_PAGE_CNT>
</MY_C1_CNT>
</ITEM>
<ITEM/>
</SCENE>

F_BBS_NUMB	댓글SEQ (키)
F_BBS_CONTENTS	댓글
F_REG_DATE	작성시간
F_DATA_TYPE	질문 OR 답변
*/
}
