//package com.snaps.common.data.request;
//
//import android.content.Context;
//import android.content.res.XmlResourceParser;
//import android.os.Handler;
//
//import com.snaps.common.structure.SnapsDelImage;
//import com.snaps.common.structure.SnapsTemplate;
//import com.snaps.common.structure.SnapsTemplatePrice;
//import com.snaps.common.structure.control.SnapsBgControl;
//import com.snaps.common.structure.control.SnapsControl;
//import com.snaps.common.structure.control.SnapsLayoutControl;
//import com.snaps.common.structure.control.SnapsTextControl;
//import com.snaps.common.structure.page.SnapsPage;
//import com.snaps.common.utils.constant.Config;
//
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
//import org.xmlpull.v1.XmlPullParserFactory;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import errorhandle.logger.Logg;
//
//public class GetSaveLoadHandler {
//	public interface AsyncResult {
//		public void downLoadComplete();
//	}
//
//	private String urlPath = "";
//	private String returnCode = "0000";
//	private String returnMessage = "";
//
//	private SnapsTemplate template;
//
//	public static final GetSaveLoadHandler instance = new GetSaveLoadHandler();
//
//	private final Handler handler = new Handler();
//
//	public SnapsTemplate getTemplate() {
//		return this.template;
//	}
//
//	public void getSaveLoadHandler(Context context, String url, AsyncResult result) {
//
//		this.urlPath = url;
//
//		Thread thread = new Thread(new LoadData(result));
//		thread.start();
//	}
//
//	public String GetReturnCode() {
//		return this.returnCode;
//	}
//
//	public String GetReturnMessage() {
//		return this.returnMessage;
//	}
//
//	private class LoadData implements Runnable {
//		private AsyncResult result;
//
//		public LoadData(AsyncResult result) {
//			this.result = result;
//		}
//
//		private void communicationError() {
//			returnCode = "";
//			returnMessage = "";
//
//			handler.post(new Runnable() {
//				public void run() {
//					result.downLoadComplete();
//				}
//			});
//		}
//
//		@Override
//		public void run() {
//			URL url = null;
//			try {
//				url = new URL(urlPath);
//
//				Logg.d("URL Path -" + urlPath);
//
//			} catch (MalformedURLException e2) {
//				Dlog.e(TAG, e2);
//			}
//
//			XmlPullParserFactory factory = null;
//			try {
//				factory = XmlPullParserFactory.newInstance();
//			} catch (XmlPullParserException e1) {
//				Dlog.e(TAG, e1);
//				this.communicationError();
//				return;
//			}
//			factory.setNamespaceAware(true);
//			XmlPullParser xml = null;
//			try {
//				xml = factory.newPullParser();
//			} catch (XmlPullParserException e1) {
//				Dlog.e(TAG, e1);
//				this.communicationError();
//				return;
//			}
//			try {
//				try {
//					xml.setInput(url.openStream(), "utf-8");
//				} catch (IOException e) {
//					Dlog.e(TAG, e);
//				}
//			} catch (XmlPullParserException e1) {
//				Dlog.e(TAG, e1);
//				this.communicationError();
//				return;
//			}
//
//			try {
//				int eventType = xml.getEventType();
//
//				String tag = "";
//				String type = "";
//
//				template = new SnapsTemplate();
//
//				SnapsTemplatePrice price = null;
//				SnapsPage page = null;
//				SnapsTextControl textControl = null;
//				SnapsLayoutControl layout = null;
//				SnapsBgControl bg = null;
//				SnapsDelImage delimg = null;
//
//				String[] temp = null;
//
//				while (eventType != XmlResourceParser.END_DOCUMENT) {
//					switch (eventType) {
//					case XmlResourceParser.START_TAG:
//						tag = xml.getName();
//
//						if (tag.equals("basket")) {
//							template.version = xml.getAttributeValue(null, "version");
//						}
//
//						if (tag.equals("item")) {
//							template.saveInfo.maker = xml.getAttributeValue(null, "maker");
//							template.type = xml.getAttributeValue(null, "type");
//							template.info.F_PROD_TYPE = xml.getAttributeValue(null, "prodType");
//							template.saveInfo.projectName = xml.getAttributeValue(null, "projectName");
//							template.saveInfo.projectIndex = xml.getAttributeValue(null, "projectIndex");
//							template.saveInfo.coverExtended = xml.getAttributeValue(null, "coverExtended");
//							template.saveInfo.validate = xml.getAttributeValue(null, "validate");
//							template.saveInfo.year = xml.getAttributeValue(null, "year");
//							template.saveInfo.month = xml.getAttributeValue(null, "month");
//							template.saveInfo.noday = xml.getAttributeValue(null, "noday");
//							template.saveInfo.id = xml.getAttributeValue(null, "id");
//							template.saveInfo.complete = xml.getAttributeValue(null, "complete");
//							template.saveInfo.orderCount = xml.getAttributeValue(null, "orderCount");
//							template.info.F_PAGE_START_NUM = xml.getAttributeValue(null, "firstPage");
//							template.saveInfo.orgPrice = xml.getAttributeValue(null, "orgPrice");
//							template.info.F_UNITTEXT = xml.getAttributeValue(null, "unitText");
//							template.info.F_TMPL_CODE = xml.getAttributeValue(null, "tmplID");
//							template.info.F_TMPL_CODE = xml.getAttributeValue(null, "tmplID");
//
//							template.saveInfo.tmbPath = xml.getAttributeValue(null, "tmbPath");
//						}
//
//						if (tag.equals("info")) {
//
//							template.info.F_EDIT_PLATFORM = xml.getAttributeValue(null, "platform");
//							template.info.F_CP_CODE = xml.getAttributeValue(null, "cpCode");
//							template.info.F_PROD_CODE = xml.getAttributeValue(null, "prodCode");
//							template.info.F_PROD_NAME = xml.getAttributeValue(null, "prodName");
//							template.info.F_PROD_NICK_NAME = xml.getAttributeValue(null, "prodNickName");
//							template.info.F_TMPL_TITLE = xml.getAttributeValue(null, "prodTitle");
//							template.info.F_PROD_SIZE = xml.getAttributeValue(null, "prodSize");
//							template.info.F_TMPL_CODE = xml.getAttributeValue(null, "tmplCode");
//
//							template.info.F_TMPL_ID = xml.getAttributeValue(null, "tmplID");
//							template.info.F_GLOSSY_TYPE = xml.getAttributeValue(null, "glossyType");
//							template.info.F_MIN_QTY = xml.getAttributeValue(null, "minCount");
//							template.info.F_SELL_UNIT = xml.getAttributeValue(null, "sellUnit");
//
//							template.info.F_USE_ANALECTA = xml.getAttributeValue(null, "useAnalecta");
//							template.info.F_USE_FORMBOARD = xml.getAttributeValue(null, "useFormBoard");
//
//							template.info.F_USE_WATERMARK = xml.getAttributeValue(null, "useWatermark");
//							template.info.F_PRNT_TYPE = xml.getAttributeValue(null, "printType");
//							template.info.F_PROD_TYPE = xml.getAttributeValue(null, "prodType");
//							template.info.F_BASE_QUANTITY = xml.getAttributeValue(null, "minPage");
//							template.info.F_MAX_QUANTITY = xml.getAttributeValue(null, "maxPage");
//							template.info.F_COVER_CHANGE_QUANTITY = xml.getAttributeValue(null, "coverChangeQuantity");
//
//							temp = xml.getAttributeValue(null, "pagePX").replace(" ", "|").split("\\|");
//							template.info.F_PAGE_PIXEL_WIDTH = temp[0];
//							template.info.F_PAGE_PIXEL_HEIGHT = temp[1];
//
//							temp = xml.getAttributeValue(null, "pageMM").replace(" ", "|").split("\\|");
//							template.info.F_PAGE_MM_WIDTH = temp[0];
//							template.info.F_PAGE_MM_HEIGHT = temp[1];
//
//							temp = xml.getAttributeValue(null, "coverVTPX").replace(" ", "|").split("\\|");
//							template.info.F_COVER_VIRTUAL_WIDTH = temp[0];
//							template.info.F_COVER_VIRTUAL_HEIGHT = temp[1];
//
//							temp = xml.getAttributeValue(null, "coverXMLPX").replace(" ", "|").split("\\|");
//							template.info.F_COVER_XML_WIDTH = temp[0];
//							template.info.F_COVER_XML_HEIGHT = temp[1];
//
//							temp = xml.getAttributeValue(null, "coverMM").replace(" ", "|").split("\\|");
//							template.info.F_COVER_MM_WIDTH = temp[0];
//							template.info.F_COVER_MM_HEIGHT = temp[1];
//
//							temp = xml.getAttributeValue(null, "cover2VTMM").replace(" ", "|").split("\\|");
//							template.info.F_COVER2_MM_WIDTH = temp[0];
//							template.info.F_COVER2_MM_HEIGHT = temp[1];
//
//							temp = xml.getAttributeValue(null, "titleMM").replace(" ", "|").split("\\|");
//							template.info.F_TITLE_MM_WIDTH = temp[0];
//							template.info.F_TITLE_MM_HEIGHT = temp[1];
//
//							template.info.F_EDIT_COVER = xml.getAttributeValue(null, "editCover");
//							template.info.F_SPLIT_COVER_MIDSIZE = xml.getAttributeValue(null, "splitCoverMidSize");
//
//							template.info.F_COVER_TYPE = xml.getAttributeValue(null, "coverType");
//							template.info.F_COVER_MID_WIDTH = xml.getAttributeValue(null, "coverSplit");
//							template.info.F_COVER2_MID_WIDTH = xml.getAttributeValue(null, "cover2Split");
//							template.info.F_DLVR_PRICE = xml.getAttributeValue(null, "deliveryPrice");
//							template.info.F_THUMBNAIL_STEP = xml.getAttributeValue(null, "thumbnailStep");
//							template.info.F_PAGE_START_NUM = xml.getAttributeValue(null, "pageStart");
//
//							template.info.F_CENTER_LINE = xml.getAttributeValue(null, "centerLine");
//							template.info.F_UI_COVER = xml.getAttributeValue(null, "useCoverTap");
//							template.info.F_UI_BACKGROUND = xml.getAttributeValue(null, "useBackgroundTap");
//							template.info.F_UI_LAYOUT = xml.getAttributeValue(null, "useLayoutTap");
//							template.info.F_UI_BORDER = xml.getAttributeValue(null, "useBorderTap");
//
//							template.info.F_TEXT_SIZE_BASE = xml.getAttributeValue(null, "textSizeBase");
//
//							template.info.F_RES_MIN = xml.getAttributeValue(null, "dpcRCMM");
//							template.info.F_RES_DISABLE = xml.getAttributeValue(null, "dpcDrop");
//							template.info.F_UNITTEXT = xml.getAttributeValue(null, "unitText");
//
//							template.info.F_SNS_BOOK_INFO_USER_NAME = xml.getAttributeValue(null, "storybookUserName");
//							template.info.F_SNS_BOOK_INFO_PERIOD = xml.getAttributeValue(null, "storybookPeriod");
//							template.info.F_SNS_BOOK_INFO_THUMBNAIL = xml.getAttributeValue(null, "storybookThumnail");
//
//							String cardQuantity = xml.getAttributeValue(null, "card_quantity");
//							if(cardQuantity != null) {
//								try {
//									Config.setCARD_QUANTITY(cardQuantity);
//								} catch (NumberFormatException e) {
//									Dlog.e(TAG, e);
//								}
//							}
//
//							template.saveInfo.imgYear = xml.getAttributeValue(null, "thumbImgYear");
//							template.saveInfo.imgSeq = xml.getAttributeValue(null, "thumbImgSeq");
//						}
//
//						if (tag.equals("price")) {
//							price = new SnapsTemplatePrice();
//
//							price.F_PRNT_BQTY = xml.getAttributeValue(null, "beginCount");
//							price.F_PRNT_EQTY = xml.getAttributeValue(null, "endCount");
//							price.F_SELL_PRICE = xml.getAttributeValue(null, "sellPrice");
//							price.F_DISC_RATE = xml.getAttributeValue(null, "discountRate");
//							price.F_PAGE_ADD_PRICE = xml.getAttributeValue(null, "pagePrice");
//							price.F_ORG_PAGE_ADD_PRICE = xml.getAttributeValue(null, "pageOrgPrice");
//						}
//
//						if (tag.equals("scene")) {
//							page = new SnapsPage(template.getPages().size(), template.info);
//
//							temp = xml.getAttributeValue(null, "rc").replace(" ", "|").split("\\|");
//
//							page.setWidth(temp[2]);
//							page.height = temp[3];
//							page.embedCount = xml.getAttributeValue(null, "embedCount");
//							page.type = xml.getAttributeValue(null, "type");
//							page.layout = xml.getAttributeValue(null, "layout");
//							page.border = xml.getAttributeValue(null, "border");
//							page.background = xml.getAttributeValue(null, "background");
//							page.year = xml.getAttributeValue(null, "year");
//							page.month = xml.getAttributeValue(null, "month");
//							page.dynamicMode = xml.getAttributeValue(null, "dynamicMode");
//						}
//
//						if (tag.equals("image")) {
//
//							String regName = xml.getAttributeValue(null, "regName");
//							SnapsControl control = null;
//
//							if (regName.equals("background")) {
//								type = "background";
//								// backGround
//								bg = new SnapsBgControl();
//
//								bg.imgYear = xml.getAttributeValue(null, "imgYear");
//								bg.imgSeq = xml.getAttributeValue(null, "imgSeq");
//								bg.uploadPath = xml.getAttributeValue(null, "uploadPath");
//								// bg.rcClip = xml.getAttributeValue(null, "rcClip");
//								bg.angleClip = xml.getAttributeValue(null, "angleClip");
//								bg.sizeOrgImg = xml.getAttributeValue(null, "sizeOrgImg");
//								bg.mstPath = xml.getAttributeValue(null, "mstPath");
//								bg.orgPath = xml.getAttributeValue(null, "orgPath");
//								bg.type = xml.getAttributeValue(null, "srcType");
//								bg.srcTargetType = xml.getAttributeValue(null, "srcTargetType");
//								bg.srcTarget = xml.getAttributeValue(null, "srcTarget");
//								bg.fit = xml.getAttributeValue(null, "fit");
//								bg.bgColor = xml.getAttributeValue(null, "bgColor");
//								bg.exchange = xml.getAttributeValue(null, "exchange");
//								bg.helper = xml.getAttributeValue(null, "helper");
//								bg.noclip = xml.getAttributeValue(null, "noclip");
//								bg.useAlpha = xml.getAttributeValue(null, "useAlpha");
//								bg.alpha = xml.getAttributeValue(null, "alpha");
//								bg.formItem = xml.getAttributeValue(null, "formItem");
//								bg.checkFull = xml.getAttributeValue(null, "checkFull");
//								bg.stick = xml.getAttributeValue(null, "stick");
//
//								control = (SnapsControl) bg;
//							} else if (regName.equals("user_image")) {
//								type = "user_image";
//								// user Image
//								layout = new SnapsLayoutControl();
//
//								layout.imgYear = xml.getAttributeValue(null, "imgYear");
//								layout.imgSeq = xml.getAttributeValue(null, "imgSeq");
//								layout.uploadPath = xml.getAttributeValue(null, "uploadPath");
//								layout.angleClip = xml.getAttributeValue(null, "angleClip");
//								layout.sizeOrgImg = xml.getAttributeValue(null, "sizeOrgImg");
//								layout.mstPath = xml.getAttributeValue(null, "mstPath");
//								layout.imagePath = xml.getAttributeValue(null, "orgPath");
//								layout.type = xml.getAttributeValue(null, "srcType");
//								layout.srcTargetType = xml.getAttributeValue(null, "srcTargetType");
//								layout.srcTarget = xml.getAttributeValue(null, "srcTarget");
//								layout.fit = xml.getAttributeValue(null, "fit");
//								layout.bgColor = xml.getAttributeValue(null, "bgColor");
//								layout.exchange = xml.getAttributeValue(null, "exchange");
//								layout.helper = xml.getAttributeValue(null, "helper");
//								layout.noclip = xml.getAttributeValue(null, "noclip");
//								layout.useAlpha = xml.getAttributeValue(null, "useAlpha");
//								layout.alpha = xml.getAttributeValue(null, "alpha");
//								layout.formItem = xml.getAttributeValue(null, "formItem");
//								layout.checkFull = xml.getAttributeValue(null, "checkFull");
//								layout.stick = xml.getAttributeValue(null, "stick");
//
//								control = (SnapsControl) layout;
//							} else if (regName.equals("like") || regName.equals("more")) {
//								type = regName;
//
//								layout = new SnapsLayoutControl();
//								layout.resourceURL = xml.getAttributeValue(null, "resourceURL");
//
//								control = (SnapsControl) layout;
//							} else {
//								control = new SnapsControl();
//							}
//
//							temp = xml.getAttributeValue(null, "rc").replace(" ", "|").split("\\|");
//
//							control.setX(temp[0]);
//							control.y = temp[1];
//							control.width = temp[2];
//							control.height = temp[3];
//							control.priority = xml.getAttributeValue(null, "priority");
//							control.angle = xml.getAttributeValue(null, "angle");
//							control.readOnly = xml.getAttributeValue(null, "readOnly");
//							control.move = xml.getAttributeValue(null, "move");
//							control.resize = xml.getAttributeValue(null, "resize");
//							control.rotate = xml.getAttributeValue(null, "rotate");
//							control.delete = xml.getAttributeValue(null, "delete");
//							control.copy = xml.getAttributeValue(null, "copy");
//							control.exclude = xml.getAttributeValue(null, "exclude");
//							control.regName = xml.getAttributeValue(null, "regName");
//							control.regValue = xml.getAttributeValue(null, "regValue");
//							control.layername = xml.getAttributeValue(null, "layerName");
//						}
//
//						if (tag.equals("textlist")) {
//							textControl = new SnapsTextControl();
//
//							temp = xml.getAttributeValue(null, "rc").replace(" ", "|").split("\\|");
//
//							textControl.setX(temp[0]);
//							textControl.y = temp[1];
//							textControl.width = temp[2];
//							textControl.height = temp[3];
//
//							textControl.priority = xml.getAttributeValue(null, "priority");
//							textControl.angle = xml.getAttributeValue(null, "angle");
//							textControl.readOnly = xml.getAttributeValue(null, "readOnly");
//							textControl.move = xml.getAttributeValue(null, "move");
//							textControl.resize = xml.getAttributeValue(null, "resize");
//							textControl.rotate = xml.getAttributeValue(null, "rotate");
//							textControl.delete = xml.getAttributeValue(null, "delete");
//							textControl.copy = xml.getAttributeValue(null, "copy");
//							textControl.exclude = xml.getAttributeValue(null, "exclude");
//
//							textControl.format.fontFace = xml.getAttributeValue(null, "fontFace");
//							textControl.format.fontSize = xml.getAttributeValue(null, "fontSize");
//							// textlist12.MaxFontSize = xml.getAttributeValue(null, "maxFontSize");
//							textControl.format.align = xml.getAttributeValue(null, "align");
//							textControl.format.bold = xml.getAttributeValue(null, "bold");
//							textControl.format.italic = xml.getAttributeValue(null, "italic");
//							textControl.format.fontColor = xml.getAttributeValue(null, "color");
//
//							type = tag;
//						}
//
//						if (tag.equals("del_item")) {
//							delimg = new SnapsDelImage();
//
//							delimg.imgYear = xml.getAttributeValue(null, "imgYear");
//							delimg.imgSeq = xml.getAttributeValue(null, "imgSeq");
//							delimg.uploadPath = xml.getAttributeValue(null, "uploadPath");
//							delimg.tinyPath = xml.getAttributeValue(null, "tinyPath");
//							delimg.oriPath = xml.getAttributeValue(null, "oriPath");
//							delimg.sizeOrgImg = xml.getAttributeValue(null, "sizeOrgImg");
//							delimg.realFileName = xml.getAttributeValue(null, "realFileName");
//							delimg.shootDate = xml.getAttributeValue(null, "shootDate");
//							delimg.usedImgCnt = xml.getAttributeValue(null, "usedImgCnt");
//						}
//
//						if (tag.equals("del_image")) {
//							delimg = new SnapsDelImage();
//
//							delimg.imgYear = xml.getAttributeValue(null, "imgYear");
//							delimg.imgSeq = xml.getAttributeValue(null, "imgSeq");
//							delimg.uploadPath = xml.getAttributeValue(null, "uploadPath");
//							delimg.tinyPath = xml.getAttributeValue(null, "tinyPath");
//							delimg.oriPath = xml.getAttributeValue(null, "oriPath");
//							delimg.sizeOrgImg = xml.getAttributeValue(null, "sizeOrgImg");
//							delimg.realFileName = xml.getAttributeValue(null, "realFileName");
//							delimg.shootDate = xml.getAttributeValue(null, "shootDate");
//							delimg.usedImgCnt = xml.getAttributeValue(null, "usedImgCnt");
//						}
//
//						if (tag.equals("clientInfo")) {
//							type = tag;
//						}
//						break;
//
//					case XmlResourceParser.END_TAG:
//						tag = xml.getName();
//						if (tag.equals("item")) {
//						}
//
//						if (tag.equals("info")) {
//						}
//
//						if (tag.equals("price")) {
//							template.priceList.add(price);
//						}
//
//						if (tag.equals("scene")) {
//							template.getPages().add(page);
//						}
//
//						if (tag.equals("image")) {
//
//							if (type.equals("user_image")) {
//								page.addLayout(layout);
//							} else if (type.equals("background")) {
//								page.addBg(bg);
//							}
//						}
//
//						if (tag.equals("textlist")) {
//
//							page.addControl(textControl);
//						}
//
//						if (tag.equals("del_item")) {
//
//						}
//
//						if (tag.equals("del_image")) {
//
//						}
//
//						if (tag.equals("clientInfo")) {
//						}
//
//						break;
//					case XmlResourceParser.TEXT:
//
//						String text = xml.getText();
//						if (tag == null || text.trim().replace("\r", "").replace("\n", "").length() == 0) {
//							break;
//						}
//
//						if ("htmlText".equals(tag) && type.equals("textlist")) {
//							textControl.htmlText = text;
//						} else if ("os".equals(tag) && type.equals("clientInfo")) {
//							template.clientInfo.os = text;
//						} else if ("language".equals(tag) && type.equals("clientInfo")) {
//							template.clientInfo.language = text;
//						} else if ("screenDPI".equals(tag) && type.equals("clientInfo")) {
//							template.clientInfo.screendpi = text;
//						} else if ("playerType".equals(tag) && type.equals("clientInfo")) {
//							template.clientInfo.playertype = text;
//						} else if ("screenResolution".equals(tag) && type.equals("clientInfo")) {
//							template.clientInfo.screenresolution = text;
//						}
//						break;
//					}
//
//					eventType = xml.next();
//				}
//
//			} catch (XmlPullParserException e) {
//				Dlog.e(TAG, e);
//				this.communicationError();
//				return;
//			} catch (IOException e) {
//				Dlog.e(TAG, e);
//				this.communicationError();
//				return;
//			}
//
//			handler.post(new Runnable() {
//				public void run() {
//					result.downLoadComplete();
//				}
//			});
//		}
//	}
//}
