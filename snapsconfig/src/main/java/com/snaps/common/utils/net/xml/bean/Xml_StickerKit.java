package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

public class Xml_StickerKit {
	public List<StickerKitData> stickerKitList = new ArrayList<Xml_StickerKit.StickerKitData>();
//	public Map<String, List<StickerKitData>> stickerKitMap = new HashMap<String, List<StickerKitData>>();
//	public void putData(String code, StickerKitData data) {
//		List<StickerKitData> stickerKitList = stickerKitMap.get(code);
//		if (stickerKitList == null)
//			stickerKitList = new ArrayList<Xml_StickerKit.StickerKitData>();
//		stickerKitList.add(data);
//	}
	
	public static class StickerKitData {
		public String F_RSRC_CODE;
		public String F_RSRC_NAME;
		/** 썸네일 이미지 */
		public String F_DIMG_PATH;
		/** 원본 이미지 */
		public String F_EIMG_PATH;
		public String F_SEARCH_TAG;
		public String F_CATEGORY_CODE;
		
		public StickerKitData(String f_RSRC_CODE, String f_RSRC_NAME, String f_DIMG_PATH, String f_EIMG_PATH, String f_SEARCH_TAG, String f_CATEGORY_CODE) {
			F_RSRC_CODE = f_RSRC_CODE;
			F_RSRC_NAME = f_RSRC_NAME;
			F_DIMG_PATH = f_DIMG_PATH;
			F_EIMG_PATH = f_EIMG_PATH;
			F_SEARCH_TAG = f_SEARCH_TAG;
			F_CATEGORY_CODE = f_CATEGORY_CODE;
		}
	}
	
	/*<SCENE ID="ED_ST">
<ITEM>
<ED_ST_LST>
	<F_RSRC_CODE>0390260432</F_RSRC_CODE>
	<F_RSRC_NAME>mst110_ha</F_RSRC_NAME>
	<F_DIMG_PATH>
	/Upload/Data1/Resource/stickerkit/dp/mDst110_ha.jpg
	</F_DIMG_PATH>
	<F_EIMG_PATH>
	/Upload/Data1/Resource/stickerkit/edit/mEst110_ha.png
	</F_EIMG_PATH>
	<F_REG_DATE>2013-06-10</F_REG_DATE>
	<F_SEARCH_TAGS>, 텍스트</F_SEARCH_TAGS>
	<F_CATEGORY_CODE>044110</F_CATEGORY_CODE>
</ED_ST_LST>
<ED_ST_LST>
	<F_RSRC_CODE>0390260431</F_RSRC_CODE>
	<F_RSRC_NAME>mst111_ha</F_RSRC_NAME>
	<F_DIMG_PATH>
	/Upload/Data1/Resource/stickerkit/dp/mDst111_ha.jpg
	</F_DIMG_PATH>
	<F_EIMG_PATH>
	/Upload/Data1/Resource/stickerkit/edit/mEst111_ha.png
	</F_EIMG_PATH>
	<F_REG_DATE>2013-06-10</F_REG_DATE>
	<F_SEARCH_TAGS>, 텍스트</F_SEARCH_TAGS>
	<F_CATEGORY_CODE>044110</F_CATEGORY_CODE>
</ED_ST_LST>
.....*/
}
