package com.snaps.common.structure;

import android.os.Build;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.SystemUtil;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;

/**
 * 
 * com.snaps.kakao.structure SnapsSaveXML.java
 * 
 * @author JaeMyung Park
 * @Date : 2013. 5. 27.
 * @Version :
 */
public class SnapsSaveXML extends SnapsXML {
	private static final String TAG = SnapsSaveXML.class.getSimpleName();
	public SnapsSaveXML(XmlSerializer xml) {
		super(xml);
	}

	public SnapsSaveXML(StringWriter writer) {
		super(writer);
	}

	public SnapsSaveXML(FileOutputStream file) {
		super(file);
	}

	/**
	 * @param template
	 * @param projname
	 * @param year
	 * @param month
	 */
	public void getAlbumInfoXml_save_item(SnapsTemplate template, String projname, String year, String month) {
		try {
			// oem.
			attribute(null, "maker", template.saveInfo.maker);
			attribute(null, "type", template.type);
			attribute(null, "prodType", template.info.F_PROD_TYPE);
			attribute(null, "projectName", projname);
			attribute(null, "projectIndex", template.saveInfo.projectIndex);
			attribute(null, "coverExtended", template.saveInfo.coverExtended);
			attribute(null, "validate", template.saveInfo.validate);
			attribute(null, "coverColorCode", Config.getTMPL_COVER() != null ? Config.getTMPL_COVER() : "");
			// TODO : 년 / 월 수정해야함.
			attribute(null, "year", year);
			attribute(null, "month", month);
			attribute(null, "noday", template.saveInfo.noday);
			attribute(null, "id", template.saveInfo.id);
			attribute(null, "complete", template.saveInfo.complete);
			attribute(null, "orderCount", template.saveInfo.orderCount);
			attribute(null, "firstPage", template.info.F_PAGE_START_NUM);
			attribute(null, "sellPrice", template.priceList.get(0).F_SELL_PRICE);
			attribute(null, "totalPrice", template.priceList.get(0).F_SELL_PRICE);
			attribute(null, "orgPrice", template.saveInfo.orgPrice);
			attribute(null, "unitText", template.info.F_UNITTEXT);
			attribute(null, "tmplID", template.info.F_TMPL_CODE);
			attribute(null, "tmplData", "prmprodcode=" + template.info.F_PROD_CODE + "&;prmtmplid=" + template.info.F_TMPL_CODE + "&;prmdsplcode2=&;categorycode=");
			attribute(null, "tmbPath", template.saveInfo.tmbPath);
			attribute(null, "backType", Config.getBACK_TYPE());
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsLogger.appendOrderLog("save XML make exception : getAlbumInfoXml_save_item"  );
		}
	}

	/**
	 * @param info
	 * @param thumbYear
	 * @param thumbSeq
	 */
	public void getAlbumInfoXml_save_info(SnapsTemplateInfo info, String thumbYear, String thumbSeq) {
		Dlog.d("getAlbumInfoXml_save_info() thumbYear:" + thumbYear + ", thumbSeq:" + thumbSeq);
		try {
			startTag(null, "info");
			// "EXTENDED"
			attribute(null, "platform", info.F_EDIT_PLATFORM);
			// "0003";
			attribute(null, "cpCode", info.F_CP_CODE);
			// "00800600070003";
			attribute(null, "prodCode", info.F_PROD_CODE);
			// "포켓북-세로형"
			attribute(null, "prodName", info.F_PROD_NAME);
			// "포켓소프트[4X6]";
			attribute(null, "prodNickName", info.F_PROD_NICK_NAME);
			// "포켓북-세로형";
			attribute(null, "prodTitle", info.F_TMPL_TITLE);
			// "4 X 6";
			attribute(null, "prodSize", info.F_PROD_SIZE);
			// "045006002383";
			attribute(null, "tmplCode", info.F_TMPL_CODE);
			attribute(null, "tmplSub", "");
			// "045006002383";
			attribute(null, "tmplID", info.F_TMPL_ID);
			// "G";
			attribute(null, "glossyType", info.F_GLOSSY_TYPE);
			attribute(null, "minCount", info.F_MIN_QTY);
			attribute(null, "sellUnit", info.F_SELL_UNIT);
			attribute(null, "useZoom", "null");
			// "N";
			attribute(null, "useAnalecta", info.F_USE_ANALECTA);
			attribute(null, "useFormBoard", info.F_USE_FORMBOARD);
			attribute(null, "typeFormBoard", "0");
			// "N";
			attribute(null, "useWatermark", info.F_USE_WATERMARK);
			// "PDF";
			attribute(null, "printType", info.F_PRNT_TYPE);
			// "book";
			attribute(null, "prodType", info.F_PROD_TYPE);
			attribute(null, "minPage", info.F_BASE_QUANTITY);
			attribute(null, "maxPage", info.F_MAX_QUANTITY);
			// "80";
			attribute(null, "coverChangeQuantity", info.F_COVER_CHANGE_QUANTITY);
			// "794 537";
			attribute(null, "pagePX", info.F_PAGE_PIXEL_WIDTH + " " + info.F_PAGE_PIXEL_HEIGHT);
			// "210 142";
			attribute(null, "pageMM", info.F_PAGE_MM_WIDTH + " " + info.F_PAGE_MM_HEIGHT);
			// "816 537";
			attribute(null, "coverVTPX", info.F_COVER_VIRTUAL_WIDTH + " " + info.F_COVER_VIRTUAL_HEIGHT);
			// "817 537";
			attribute(null, "coverXMLPX", info.F_COVER_XML_WIDTH + " " + info.F_COVER_XML_HEIGHT);
			// "216 142";
			attribute(null, "coverMM", info.F_COVER_MM_WIDTH + " " + info.F_COVER_MM_HEIGHT);
			// "216 142";
			attribute(null, "coverVTMM", info.F_COVER_MM_WIDTH + " " + info.F_COVER_MM_HEIGHT);
			// "223 147";
			attribute(null, "cover2VTMM", info.F_COVER2_MM_WIDTH + " " + info.F_COVER2_MM_HEIGHT);
			// "210 142";
			attribute(null, "titleMM", info.F_TITLE_MM_WIDTH + " " + info.F_TITLE_MM_HEIGHT);
			// "Y";
			attribute(null, "editCover", info.F_EDIT_COVER);
			// "Y";
			attribute(null, "splitCoverMidSize", info.F_SPLIT_COVER_MIDSIZE);

			attribute(null, "pageEditCover", "null");
			// "soft";
			attribute(null, "coverType", info.F_COVER_TYPE);
			attribute(null, "coverSplit", info.F_COVER_MID_WIDTH);
			attribute(null, "cover2Split", info.F_COVER2_MID_WIDTH);

			attribute(null, "deliveryPrice", info.F_DLVR_PRICE);
			// "3";
			attribute(null, "thumbnailStep", info.F_THUMBNAIL_STEP);
			// "2";
			attribute(null, "pageStart", info.F_PAGE_START_NUM);

			attribute(null, "pageExploreType", "null");
			attribute(null, "foldType", "null");
			attribute(null, "pageIndexType", "null");
			// "CHECK";
			attribute(null, "centerLine", info.F_CENTER_LINE);
			// "N";
			attribute(null, "useCoverTap", info.F_UI_COVER);
			// "Y";
			attribute(null, "useBackgroundTap", info.F_UI_BACKGROUND);
			// "Y";
			attribute(null, "useLayoutTap", info.F_UI_LAYOUT);
			// "Y";
			attribute(null, "useBorderTap", info.F_UI_BORDER);

			attribute(null, "useRimTap", "null");
			attribute(null, "useAddInfo", "null");
			attribute(null, "addPrice", "null");
			attribute(null, "addPriceType", "null");
			// "PRINT";
			attribute(null, "textSizeBase", info.F_TEXT_SIZE_BASE);

			attribute(null, "cuttingSize", "null");
			attribute(null, "cuttingColor", "null");
			// "40"; ?????
			attribute(null, "dpcRCMM", info.F_RES_MIN);
			// "25"; ?????
			attribute(null, "dpcDrop", info.F_RES_DISABLE);
			// "권";
			attribute(null, "unitText", info.F_UNITTEXT);
			// Thumb Year
			attribute(null, "thumbImgYear", thumbYear);
			// Thumb Seq
			attribute(null, "thumbImgSeq", thumbSeq);

			attribute(null, "projectCode", Config.getPROJ_CODE());
			
			//스토리북 정보
			attribute(null, "storybookUserName", info.F_SNS_BOOK_INFO_USER_NAME);
			
			attribute(null, "storybookPeriod", info.F_SNS_BOOK_INFO_PERIOD);
			
			attribute(null, "storybookThumnail", info.F_SNS_BOOK_INFO_THUMBNAIL);

			attribute(null, "card_quantity", Config.getCARD_QUANTITY());

			attribute(null, "activity", info.F_ACTIVITY);



			endTag(null, "info");
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsLogger.appendOrderLog("save XML make exception : getAlbumInfoXml_save_info"  );
		}
	}

	/**
	 * @param price
	 */
	public void getAlbumInfoXml_save_price(SnapsTemplatePrice price) {
		try {
			startTag(null, "price");
			attribute(null, "beginCount", price.F_PRNT_BQTY);
			attribute(null, "endCount", price.F_PRNT_EQTY);
			attribute(null, "sellPrice", price.F_SELL_PRICE);
			attribute(null, "discountRate", price.F_DISC_RATE);
			attribute(null, "pagePrice", price.F_PAGE_ADD_PRICE);
			attribute(null, "pageOrgPrice", price.F_ORG_PAGE_ADD_PRICE);
			endTag(null, "price");
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsLogger.appendOrderLog("save XML make exception : getAlbumInfoXml_save_price"  );
		}

	}

	/**
	 * @param info
	 */
	public void getAlbumInfoXml_save_clientInfo(SnapsClientInfo info) {
		try {
			startTag(null, "clientInfo");

			this.addTag(null, "os", String.valueOf(Config.ANDROID_VERSION));
			this.addTag(null, "language", SystemUtil.getLanguage());
			this.addTag(null, "screenDPI", info.screendpi);
			this.addTag(null, "playerType", "android");
			this.addTag(null, "screenResolution", info.screenresolution);

			this.addTag(null, "brand", Build.BRAND);
			this.addTag(null, "device", Build.DEVICE);
			this.addTag(null, "product", Build.PRODUCT);
			this.addTag(null, "version.release", Build.VERSION.RELEASE);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			this.addTag(null, "write.time", dateFormat.format(new Date()));

			endTag(null, "clientInfo");
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}
}
