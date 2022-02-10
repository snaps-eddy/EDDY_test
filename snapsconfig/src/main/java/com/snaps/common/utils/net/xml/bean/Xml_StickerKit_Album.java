package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

public class Xml_StickerKit_Album {
	public List<StickerKitAlbumData> stickerKitList = new ArrayList<Xml_StickerKit_Album.StickerKitAlbumData>();
	
	public static class StickerKitAlbumData {
		public String F_CATEGORY_CODE;
		public String F_CATEGORY_NAME;
		/** 카테고리 이미지 */
		public String F_EIMG_PATH;
		
		public StickerKitAlbumData(String f_CATEGORY_CODE, String f_CATEGORY_NAME, String f_EIMG_PATH) {
			F_CATEGORY_CODE = f_CATEGORY_CODE;
			F_CATEGORY_NAME = f_CATEGORY_NAME;
			F_EIMG_PATH = f_EIMG_PATH;
		}
	}
	
	/*<SCENE ID="ED_ST">
<ITEM>
<ED_ST_CAT>
<F_CATEGORY_CODE>044106</F_CATEGORY_CODE>
<F_CATEGORY_NAME>하트</F_CATEGORY_NAME>
<F_EIMG_PATH>
/Upload/Data1/Resource/stickerkit/edit/mEst1_im.png
</F_EIMG_PATH>
</ED_ST_CAT>
<ED_ST_CAT>
<F_CATEGORY_CODE>044107</F_CATEGORY_CODE>
<F_CATEGORY_NAME>별</F_CATEGORY_NAME>
<F_EIMG_PATH>
/Upload/Data1/Resource/stickerkit/edit/mEst1_xr.png
</F_EIMG_PATH>
</ED_ST_CAT>
.....*/
}
