package com.snaps.common.utils.net.xml;

import android.content.Context;
import android.util.Base64;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snaps.common.data.parser.GetTemplateXMLHandler;
import com.snaps.common.data.parser.GetThemeBookTemplateXMLHandler;
import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplatePrice;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.log.SnapsInterfaceLogListener;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.net.xml.bean.Xml_BgCover;
import com.snaps.common.utils.net.xml.bean.Xml_BgCover.BgCoverData;
import com.snaps.common.utils.net.xml.bean.Xml_CouponKind;
import com.snaps.common.utils.net.xml.bean.Xml_CouponKind.CouponKindInfo;
import com.snaps.common.utils.net.xml.bean.Xml_CouponList;
import com.snaps.common.utils.net.xml.bean.Xml_CouponList.CouponData;
import com.snaps.common.utils.net.xml.bean.Xml_CoverResource;
import com.snaps.common.utils.net.xml.bean.Xml_KakaoBillConfirm;
import com.snaps.common.utils.net.xml.bean.Xml_Kakao_Transfer;
import com.snaps.common.utils.net.xml.bean.Xml_Kakao_Transfer_Data;
import com.snaps.common.utils.net.xml.bean.Xml_MyArtwork;
import com.snaps.common.utils.net.xml.bean.Xml_MyArtwork.MyArtworkData;
import com.snaps.common.utils.net.xml.bean.Xml_MyArtworkDetail;
import com.snaps.common.utils.net.xml.bean.Xml_MyArtworkDetail.MyArtworkDetail;
import com.snaps.common.utils.net.xml.bean.Xml_MyBadge;
import com.snaps.common.utils.net.xml.bean.Xml_MyHomePrice;
import com.snaps.common.utils.net.xml.bean.Xml_MyHomePrice.MyPriceData;
import com.snaps.common.utils.net.xml.bean.Xml_MyOrder;
import com.snaps.common.utils.net.xml.bean.Xml_MyOrder.MyOrderData;
import com.snaps.common.utils.net.xml.bean.Xml_MyOrderDetail;
import com.snaps.common.utils.net.xml.bean.Xml_MyOrderDetail.MyOrderDetail;
import com.snaps.common.utils.net.xml.bean.Xml_MyOrderReqInfo;
import com.snaps.common.utils.net.xml.bean.Xml_Notice;
import com.snaps.common.utils.net.xml.bean.Xml_Notice.NoticeData;
import com.snaps.common.utils.net.xml.bean.Xml_PostAddress;
import com.snaps.common.utils.net.xml.bean.Xml_PushAllUser;
import com.snaps.common.utils.net.xml.bean.Xml_PushAllUser.PushAllUser;
import com.snaps.common.utils.net.xml.bean.Xml_QnA;
import com.snaps.common.utils.net.xml.bean.Xml_QnA.QnAData;
import com.snaps.common.utils.net.xml.bean.Xml_RecentAddress;
import com.snaps.common.utils.net.xml.bean.Xml_SnapsLoginInfo;
import com.snaps.common.utils.net.xml.bean.Xml_StickerKit;
import com.snaps.common.utils.net.xml.bean.Xml_StickerKit.StickerKitData;
import com.snaps.common.utils.net.xml.bean.Xml_StickerKit_Album;
import com.snaps.common.utils.net.xml.bean.Xml_StickerKit_Album.StickerKitAlbumData;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeCategory;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeCategory.ThemeCategory;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeContents;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeContents.ThemeContents;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeCover;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeCover.ThemeCover;
import com.snaps.common.utils.net.xml.bean.Xml_ThemePage;
import com.snaps.common.utils.net.xml.bean.Xml_ThemePage.ThemePage;
import com.snaps.common.utils.net.xml.bean.Xml_UpdateInfo;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.component.SnapsEventView;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import errorhandle.SnapsAssert;
import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

public class GetParsedXml {
    private static final String TAG = GetParsedXml.class.getSimpleName();

    public static final String REQUEST_PARAM_FOR_SMART_SNAPS_ANALYSIS_PAGE_TYPE_COVER = "cover";
    public static final String REQUEST_PARAM_FOR_SMART_SNAPS_ANALYSIS_PAGE_TYPE_TITLE = "title";
    public static final String REQUEST_PARAM_FOR_SMART_SNAPS_ANALYSIS_PAGE_TYPE_PAGE = "page";

    public static final String HEADER_URL_ENCODED_FORM = "application/x-www-form-urlencoded";
    public static final String HEADER_CONTENT_TYPE_APP_JSON = "Content-Type : application/json";

    /**
     * 업데이트 정보 수신(for mobile-다국어)
     *
     * @return
     */
    public static HashMap<String, String> hashDate = null;

    public static Xml_UpdateInfo getUpdateInfoForMobile(SnapsInterfaceLogListener interfaceLogListener) {
        Xml_UpdateInfo xmlUpdateInfo = null;
        try {
            String url = String.format(SnapsAPI.GET_UPDATEINFO_MOBILE(), SnapsTPAppManager.getChannelCode()) + (Config.isRealServer() ? "N" : "Y");

            String result = HttpUtil.connectGet(url, interfaceLogListener);
            if (StringUtil.isEmpty(result)) return null;

            JsonObject parent = new JsonParser().parse(result).getAsJsonObject(), tempObject;
            xmlUpdateInfo = new Xml_UpdateInfo();
            xmlUpdateInfo.setAppVersion(parent.get("versions").getAsJsonObject().get("android").getAsString());
            xmlUpdateInfo.setDoingFriendEvent("true".equalsIgnoreCase(parent.get("friendEvent").getAsJsonObject().get("android").getAsString()));
            tempObject = parent.get("tracker").getAsJsonObject();
            xmlUpdateInfo.setEnableAdbrix("true".equalsIgnoreCase(tempObject.get("adbrix").getAsJsonObject().get("android").getAsString()));
            xmlUpdateInfo.setEnableGoogleAnalytics("true".equalsIgnoreCase(tempObject.get("ga").getAsJsonObject().get("android").getAsString()));
            if (tempObject.has("appsflyer")) {
                xmlUpdateInfo.setEnableAppsFlyer("true".equalsIgnoreCase(tempObject.get("appsflyer").getAsJsonObject().get("android").getAsString()));
            } else {
                xmlUpdateInfo.setEnableAppsFlyer(false);
            }
            if (parent.has("instagramLogin")) {
                tempObject = parent.get("instagramLogin").getAsJsonObject();
                xmlUpdateInfo.setInstargramLogin(tempObject.get("android").getAsString());
            }
            tempObject = parent.get("notice").getAsJsonObject().get("android").getAsJsonObject();
            xmlUpdateInfo.setNoticeVersion(tempObject.get("version").getAsString());
            xmlUpdateInfo.setNoticeMsg(tempObject.get("content").getAsString());
            xmlUpdateInfo.setNoticeUrl(tempObject.get("url").getAsString());


            if (parent.has("imageCache")) {
                tempObject = parent.get("imageCache").getAsJsonObject();
                if (tempObject.has("android"))
                    xmlUpdateInfo.setImageCache("true".equalsIgnoreCase(tempObject.get("android").getAsString()));
            }

            if (xmlUpdateInfo.isEnableAdbrix()) xmlUpdateInfo.setEnableAdbrix(true);

            if (parent.has("certification")) {
                String certificationObj = parent.get("certification").getAsJsonObject().get("android").getAsString();
                xmlUpdateInfo.setUsePhoneCertification(!StringUtil.isEmpty(certificationObj) && "true".equalsIgnoreCase(certificationObj));
            }

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlUpdateInfo;
    }

    /**
     * 스냅스 로그인
     *
     * @return
     */
    public static Xml_SnapsLoginInfo snapsLogin(Context context, String userId, String userPwd, String userName1, String userName2, String joinType) {
        Xml_SnapsLoginInfo xmlSnapsLoginInfo = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("F_USER_ID", userId));
            getParameters.add(new BasicNameValuePair("F_USER_PWD", userPwd));
            if (userName1 != null && !"".equals(userName1))
                getParameters.add(new BasicNameValuePair("F_USER_NAME1", userName1));
            if (userName2 != null && !"".equals(userName2))
                getParameters.add(new BasicNameValuePair("F_USER_NAME2", userName2));
            getParameters.add(new BasicNameValuePair("F_SNS_TYPE", joinType));
            // 20140411 f_hppn_type 추가..

            getParameters.add(new BasicNameValuePair("f_hppn_type", "190002"));

            getParameters.add(new BasicNameValuePair("f_event_code", SnapsEventView.COUPON_INSERT_EVENT_CODE));

            getParameters.add(new BasicNameValuePair("f_device_no", SystemUtil.getDeviceId(context)));

            if (!Config.isRealServer())
                getParameters.add(new BasicNameValuePair("f_use_yorn", "T")); // TODO test code

            String result = HttpUtil.connectPost(SnapsAPI.getSnapsLoginPostApi(), getParameters, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

            if (result == null) {
                return new Xml_SnapsLoginInfo(true);
            }

            XmlResult xmlResult = new XmlResult(result);
            if ("false".equals(xmlResult.get("RETURN_CODE"))) {
                String f_RETURN_CODE = "false";
                String f_RETURN_MSG = xmlResult.get("RETURN_MSG");
                xmlResult.close();
                xmlSnapsLoginInfo = new Xml_SnapsLoginInfo(f_RETURN_CODE, f_RETURN_MSG);
            } else {

                String f_RETURN_CODE = "true";
                String f_USER_NO = xmlResult.get("F_USER_NO");
                String f_USER_ID = xmlResult.get("F_USER_ID");
                String f_USER_NAME = xmlResult.get("F_USER_NAME");
                String f_USER_MAIL = xmlResult.get("F_USER_MAIL");
                String f_USER_LVL = xmlResult.get("F_USER_LVL");

                String f_EVENT_TERM = xmlResult.get("F_EVENT_TERM");
                String f_COUPON = xmlResult.get("F_COUPON");
                String f_REVIEW = xmlResult.get("F_REVIEW");
                String f_DEVICE = xmlResult.get("F_DEVICE");
                String f_FILE_PATH = xmlResult.get("F_FILE_PATH");
                String f_USER_AUTH = xmlResult.get("F_AUTH_YORN");
                String f_USER_PHONENUMBER = xmlResult.get("F_USER_PHONENUMBER");
                String f_USER_AI_SYNC = xmlResult.get("F_AI_USE_YN");
                String f_USER_AI_TOS_AGREE = xmlResult.get("F_TOS_YN");
                String restCode = xmlResult.get("F_REST_STAT");
                Setting.set(context, Const_VALUE.KEY_SNAPS_REST_ID, Const_VALUE.VALUE_SNAPS_REST_ID.equals(restCode));

                xmlResult.close();
                xmlSnapsLoginInfo = new Xml_SnapsLoginInfo(f_USER_NO, f_USER_ID, f_USER_NAME, f_USER_MAIL, f_USER_LVL, f_EVENT_TERM, f_COUPON, f_REVIEW, f_DEVICE, f_FILE_PATH, f_USER_AUTH, f_USER_PHONENUMBER, f_RETURN_CODE, f_USER_AI_SYNC, f_USER_AI_TOS_AGREE);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlSnapsLoginInfo;
    }

    public static SnapsTemplate requestGetTemplateWithAnalysisInfo(List<NameValuePair> getParameters, SnapsInterfaceLogListener interfaceLogListener) {
        InputStream inputStream = null;
        InputStream stringStream = null;
        SnapsTemplate template = null;
        try {
            HttpResponse response = HttpUtil.connectPostReturnHttpResponse(SnapsAPI.POST_API_SMART_SNAPS_GET_RECOMMEND_TEMPALTE_URL(), HEADER_URL_ENCODED_FORM, getParameters, interfaceLogListener);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                if (inputStream != null) {
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    factory.setValidating(false);
                    SAXParser parser = factory.newSAXParser();
                    XMLReader reader = parser.getXMLReader();

                    GetTemplateXMLHandler xml = new GetTemplateXMLHandler();

                    reader.setContentHandler(xml);
                    String result = FileUtil.convertStreamToString(inputStream);
                    stringStream = new ByteArrayInputStream(result.getBytes("UTF-8"));
                    reader.parse(new InputSource(stringStream));
                    template = xml.getTemplate();

                    if (interfaceLogListener != null)
                        interfaceLogListener.onSnapsInterfaceResult(200, "success get template.");

					SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_complete_processLayout_RES)
							.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE())
							.appendPayload(WebLogConstants.eWebLogPayloadType.RESPONSE_CONTENTS, result));
				}
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}

			if (stringStream != null) {
				try {
					stringStream.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}
		}

        return template;
    }

    public static Xml_MyOrderReqInfo kakaoBillReq(String userid, String projCodes, String oldPrice) {
        Xml_MyOrderReqInfo xmlOrderInfo = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("f_user_id", userid));
            getParameters.add(new BasicNameValuePair("f_proj_codes", Base64.encodeToString(projCodes.getBytes(), 0)));
            if (oldPrice != null)
                getParameters.add(new BasicNameValuePair("f_old_price", oldPrice));
            String result = HttpUtil.connectPost(SnapsAPI.POST_API_KAKAO_BILL_REQ(), getParameters, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            XmlResult xmlResult = new XmlResult(result);

            String CHECKOUT_HASH = xmlResult.get("CHECKOUT_HASH");
            String STATUS = xmlResult.get("STATUS");
            String PAY_ID = xmlResult.get("PAY_ID");
            String F_ORDER_CODE = xmlResult.get("F_ORDER_CODE");

            xmlResult.close();
            xmlOrderInfo = new Xml_MyOrderReqInfo(CHECKOUT_HASH, STATUS, PAY_ID, F_ORDER_CODE);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlOrderInfo;
    }

    public static Xml_Kakao_Transfer kakaoTrnasfer(String kakaoid) {
        Xml_Kakao_Transfer xmlkakaotrans = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("f_kakao_id", kakaoid));

            String result = HttpUtil.connectPost(SnapsAPI.KAKAO_TRNASFER_ID_INTERFACE(), getParameters, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            XmlResult xmlResult = new XmlResult(result);

            String f_proj_cnt = xmlResult.get("F_PROJ_CNT");
            String f_order_cnt = xmlResult.get("F_ORDER_CNT");

            xmlResult.close();
            xmlkakaotrans = new Xml_Kakao_Transfer(f_proj_cnt, f_order_cnt);

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlkakaotrans;
    }

    public static Xml_Kakao_Transfer_Data kakaoTrnasferData(String kakaoid, String userid, String type) {
        Xml_Kakao_Transfer_Data xmlkakaotransdata = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("f_kakao_id", kakaoid));
            getParameters.add(new BasicNameValuePair("f_user_no", userid));
            getParameters.add(new BasicNameValuePair("f_trans_type", type));

            String result = HttpUtil.connectPost(SnapsAPI.KAKAO_TRNASFER_DATA_INTERFACE(), getParameters, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            XmlResult xmlResult = new XmlResult(result);

            String code = xmlResult.get("RETURN_CODE");
            String msg = xmlResult.get("RETURN_MSG");

            xmlResult.close();
            xmlkakaotransdata = new Xml_Kakao_Transfer_Data(code, msg);

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlkakaotransdata;
    }

    /**
     * 카카오 결재내역 확인
     *
     * @param pay_id
     * @param checkout_hash
     * @return
     */
    public static Xml_KakaoBillConfirm kakaoBillConfirm(String checkoutTicket, String pay_id, String checkout_hash) {
        Xml_KakaoBillConfirm xmlKakaoBillConfirm = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("checkout_ticket", checkoutTicket));
            getParameters.add(new BasicNameValuePair("pay_id", pay_id));
            getParameters.add(new BasicNameValuePair("checkout_hash", checkout_hash));
            String result = HttpUtil.connectPost(SnapsAPI.POST_API_KAKAO_BILL_CONFIRM(), getParameters, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            XmlResult xmlResult = new XmlResult(result);

            String status = xmlResult.get("STATUS");
            String PAY_ID = xmlResult.get("PAY_ID");
            String external_order_id = xmlResult.get("EXTERNAL_ORDER_ID");
            String CHECKOUT_HASH = xmlResult.get("CHECKOUT_HASH");
            String pay_by = xmlResult.get("PAY_BY");
            String amount = xmlResult.get("AMOUNT");
            String paid_at = xmlResult.get("PAID_AT");
            String order_status = xmlResult.get("ORDER_STATUS");
            String remain_amount = xmlResult.get("REMAIN_AMOUNT");
            String message = xmlResult.get("MESSAGE");

            xmlResult.close();
            xmlKakaoBillConfirm = new Xml_KakaoBillConfirm(status, PAY_ID, external_order_id, CHECKOUT_HASH, pay_by, amount, paid_at, order_status, remain_amount, message);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlKakaoBillConfirm;
    }

    /**
     * 스냅스 주문요청
     *
     * @param userid
     * @param projCodes 프로젝트코드^프로젝트코드
     * @return
     */
    public static String snapsBillReq(String userid, String projCodes) {
        return snapsBillReq(userid, projCodes, null);
    }

    /**
     * 스냅스 프로젝트별 수량 업데이트
     *
     * @param userid
     * @param projCodes 프로젝트코드@수량^프로젝트코드@수량
     * @param orderCode
     * @return
     */
    public static String snapsBillReq(String userid, String projCodes, String orderCode) {
        String res = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("f_user_id", userid));
            getParameters.add(new BasicNameValuePair("f_proj_codes", Base64.encodeToString(projCodes.getBytes(), 0)));
            if (orderCode != null)
                getParameters.add(new BasicNameValuePair("f_order_code", orderCode));
            if (orderCode != null)
                getParameters.add(new BasicNameValuePair("f_mode", "step2"));
            else
                getParameters.add(new BasicNameValuePair("f_mode", "step1"));
            String result = HttpUtil.connectPost(SnapsAPI.POST_API_SNAPS_BILL_REQ(), getParameters, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            XmlResult xmlResult = new XmlResult(result);

            if (orderCode == null)
                res = xmlResult.get("F_ORDER_CODE");
            else
                res = xmlResult.get("RESULT_CODE");

            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return res;
    }

    /**
     * 주문배송 리스트 조회
     *
     * @return
     */
    public static Xml_MyOrder getMonthMyOrder(int page_no, String userid, String month, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_MyOrder xmlOrder = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("f_user_id", userid));
            getParameters.add(new BasicNameValuePair("f_page_no", String.valueOf(page_no)));
            getParameters.add(new BasicNameValuePair("f_search_term", month));

            getParameters.add(new BasicNameValuePair("f_os_type", "190002"));

            String result = "";

            result = HttpUtil.connectGet(SnapsAPI.GET_API_ORDER_LIST(), getParameters, interfaceLogListener);

            XmlResult xmlResult = new XmlResult(result);

            xmlOrder = new Xml_MyOrder();
            xmlOrder.F_PAGE_CNT = Integer.valueOf(xmlResult.get("F_PAGE_CNT"));
            int arrSize = xmlResult.getList("MY_DVY_LST");
            for (int i = 0; i < arrSize; i++) {
                String f_ORDER_CODE = xmlResult.getListItemD1(i, "F_ORDER_CODE");
                if (f_ORDER_CODE == null || "".equals(f_ORDER_CODE))
                    continue;
                String f_ORDER_STATUS = xmlResult.getListItemD1(i, "F_ORDR_STAT");
                String f_PROJ_NAME = xmlResult.getListItemD1(i, "F_PROJ_NAME");
                String f_STTL_AMNT = xmlResult.getListItemD1(i, "F_STTL_AMNT");
                String f_REG_DATE = xmlResult.getListItemD1(i, "F_REG_DATE");
                String f_SIMG_PATH = SnapsAPI.DOMAIN(false) + xmlResult.getListItemD1(i, "F_SIMG_PATH");
                String f_DLVR_NUMB = xmlResult.getListItemD1(i, "F_DLVR_NUMB");
                xmlOrder.myOrderList.add(new MyOrderData(f_ORDER_CODE, f_ORDER_STATUS, f_PROJ_NAME, f_STTL_AMNT, f_REG_DATE, f_SIMG_PATH, f_DLVR_NUMB));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlOrder;
    }

    /**
     * 주문배송 상세조회
     *
     * @param orderCode
     * @param userid
     * @return
     */
    public static Xml_MyOrderDetail getMyOrderDetail(String orderCode, String userid, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_MyOrderDetail xmlMyOderDetail = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("f_order_code", orderCode));
            getParameters.add(new BasicNameValuePair("f_user_id", userid));
            String result = HttpUtil.connectGet(SnapsAPI.GET_API_ORDER_DETAIL(), getParameters, interfaceLogListener);

            XmlResult xmlResult = new XmlResult(result);

            String f_DLVY_DATE = xmlResult.get("F_DLVY_DATE");
            String f_ORDR_NAME = xmlResult.get("F_ORDR_NAME");
            String f_ORDR_CELL = xmlResult.get("F_ORDR_CELL");
            String f_ORDR_MAIL = xmlResult.get("F_ORDR_MAIL");
            String f_RCPT_NAME = xmlResult.get("F_RCPT_NAME");
            String f_RCPT_CELL = xmlResult.get("F_RCPT_CELL");
            String f_RCPT_ZIP = xmlResult.get("F_RCPT_ZIP");
            String f_RCPT_ADDR1 = xmlResult.get("F_RCPT_ADDR1");
            String f_RCPT_ADDR2 = xmlResult.get("F_RCPT_ADDR2");
            String f_RCPT_ADDR3 = xmlResult.get("F_RCPT_ADDR3");
            String f_RCPT_MAIL = xmlResult.get("F_RCPT_MAIL");
            String f_PAY_DATE = xmlResult.get("F_PAY_DATE");
            String f_ORDR_STAT = xmlResult.get("F_ORDR_STAT");
            String f_ORDR_STAT_CODE = xmlResult.get("F_ORDR_STAT_CODE");
            String f_DLVR_NUMB = xmlResult.get("F_DLVR_NUMB");

            String f_ORDR_AMNT = xmlResult.get("F_ORDR_AMNT");
            String f_DLVR_AMNT = xmlResult.get("F_DLVR_AMNT");
            String f_STTL_AMNT = xmlResult.get("F_STTL_AMNT");
            String f_USE_AMT = xmlResult.get("F_USE_AMT");
            String f_POINT_AMNT = xmlResult.get("F_POINT_AMNT");

            String f_BANK_NAME = xmlResult.get("F_BANK_NAME");
            String f_ACCOUNT = xmlResult.get("F_ACCOUNT");
            String f_SEND_EXPR_DATE = xmlResult.get("F_SEND_EXPR_DATE");
            String f_DEPOSIT = xmlResult.get("F_DEPOSIT");

            xmlMyOderDetail = new Xml_MyOrderDetail(f_DLVY_DATE, f_ORDR_NAME, f_ORDR_CELL, f_ORDR_MAIL, f_RCPT_NAME, f_RCPT_CELL, f_RCPT_ZIP, f_RCPT_ADDR1, f_RCPT_ADDR2, f_RCPT_ADDR3, f_RCPT_MAIL, f_PAY_DATE, f_ORDR_STAT, f_ORDR_STAT_CODE, f_DLVR_NUMB, f_ORDR_AMNT, f_DLVR_AMNT, f_STTL_AMNT,
                    f_USE_AMT, f_POINT_AMNT, f_BANK_NAME, f_ACCOUNT, f_SEND_EXPR_DATE, f_DEPOSIT);
            int arrSize = xmlResult.getList("MY_ODL_PROD");
            for (int i = 0; i < arrSize; i++) {
                String f_PROJ_CODE = xmlResult.getListItemD1(i, "F_PROJ_CODE");
                if (f_PROJ_CODE == null || "".equals(f_PROJ_CODE))
                    continue;
                String f_SIMG_PATH = SnapsAPI.DOMAIN(false) + xmlResult.getListItemD1(i, "F_SIMG_PATH");
                String f_IMG_YEAR = xmlResult.getListItemD1(i, "F_IMG_YEAR");
                String f_IMG_SQNC = xmlResult.getListItemD1(i, "F_IMG_SQNC");
                String f_PROJ_NAME = xmlResult.getListItemD1(i, "F_PROJ_NAME");
                String f_ORDR_PRICE = xmlResult.getListItemD1(i, "F_ORDR_PRICE");
                String f_PROJ_CNT = xmlResult.getListItemD1(i, "F_PROJ_CNT");
                String f_PROD_CODE = xmlResult.getListItemD1(i, "F_PROD_CODE");
                String f_PROD_NAME = xmlResult.getListItemD1(i, "F_PROD_NAME");
                String f_REG_DATE = xmlResult.getListItemD1(i, "F_REG_DATE");
                String f_CHNL_CODE = xmlResult.getListItemD1(i, "F_CHNL_CODE"); // pc,mobile 상품 구분
                xmlMyOderDetail.myOrderDetailList.add(new MyOrderDetail(f_PROJ_CODE, f_SIMG_PATH, f_IMG_YEAR, f_IMG_SQNC, f_PROJ_NAME, f_ORDR_PRICE, f_PROJ_CNT, f_PROD_CODE, f_PROD_NAME, f_REG_DATE, f_CHNL_CODE));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlMyOderDetail;
    }

    /**
     * 장바구니 조회
     *
     * @return
     */
    public static Xml_MyArtwork getCartList(String userid, SnapsInterfaceLogListener interfaceLogListener) {
        return getCartList(userid, null, null, interfaceLogListener);
    }

    public static Xml_MyArtwork getCartList(String userid, String hppnType, String oldPrice, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_MyArtwork xmlMyArtwork = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("f_user_id", userid));
            if (hppnType != null)
                getParameters.add(new BasicNameValuePair("f_hppn_type", hppnType));
            if (oldPrice != null)
                getParameters.add(new BasicNameValuePair("f_old_price", oldPrice));
            getParameters.add(new BasicNameValuePair("f_bag_stat", Const_VALUES.MYART_STATUS1_CART1));
            String result = HttpUtil.connectGet(SnapsAPI.GET_API_MY_PROJECT_LIST(), getParameters, interfaceLogListener);
            XmlResult xmlResult = new XmlResult(result);

            xmlMyArtwork = new Xml_MyArtwork();
            xmlMyArtwork.F_DLVY_DATE = xmlResult.get("F_DLVY_DATE");
            xmlMyArtwork.setF_POST_PRICE(xmlResult.get("F_POST_PRICE"));

            int arrSize = xmlResult.getList("MY_PRJ_LST");
            for (int i = 0; i < arrSize; i++) {
                String f_PROJ_CODE = xmlResult.getListItemD1(i, "F_PROJ_CODE");
                if (f_PROJ_CODE == null || "".equals(f_PROJ_CODE))
                    continue;
                String f_SIMG_PATH = SnapsAPI.DOMAIN(false) + xmlResult.getListItemD1(i, "F_SIMG_PATH");
                String f_PROJ_NAME = xmlResult.getListItemD1(i, "F_PROJ_NAME");
                String f_PROD_CODE = xmlResult.getListItemD1(i, "F_PROD_CODE");
                String f_PROD_NAME = xmlResult.getListItemD1(i, "F_PROD_NAME");
                String f_REG_DATE = xmlResult.getListItemD1(i, "F_REG_DATE");
                String f_PROJ_CNT = xmlResult.getListItemD1(i, "F_PROJ_CNT");
                String f_ORDER_PRICE = xmlResult.getListItemD1(i, "F_ORDR_PRICE");
                String f_UNIT_PRICE = xmlResult.getListItemD1(i, "F_UNIT_PRICE");
                String f_UNIT_ORG_PRICE = xmlResult.getListItemD1(i, "F_UNIT_ORG_PRICE");
                String f_ORDR_ORG_PRICE = xmlResult.getListItemD1(i, "F_ORDR_ORG_PRICE");
                xmlMyArtwork.myartworkList.add(new MyArtworkData(f_PROJ_CODE, f_SIMG_PATH, f_PROJ_NAME, f_PROD_CODE, f_PROD_NAME, f_REG_DATE, f_PROJ_CNT, f_ORDER_PRICE, f_UNIT_PRICE, f_ORDR_ORG_PRICE, f_UNIT_ORG_PRICE));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlMyArtwork;
    }

    /**
     * 내 작품함 조회
     *
     * @return
     */
    public static Xml_MyArtwork getMyArtwork(int pageNo, String userid, SnapsInterfaceLogListener interfaceLogListener) {
        return getMyArtwork(pageNo, userid, null, interfaceLogListener);
    }

    public static Xml_MyArtwork getMyArtwork(int pageNo, String userid, String hppnType, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_MyArtwork xmlMyArtwork = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("f_page_no", String.valueOf(pageNo)));
            getParameters.add(new BasicNameValuePair("f_user_id", userid));
            if (hppnType != null)
                getParameters.add(new BasicNameValuePair("f_hppn_type", hppnType));
            getParameters.add(new BasicNameValuePair("f_bag_stat", Const_VALUES.MYART_STATUS0_ART));
            String result = HttpUtil.connectGet(SnapsAPI.GET_API_MY_PROJECT_LIST(), getParameters, interfaceLogListener);
            XmlResult xmlResult = new XmlResult(result);

            xmlMyArtwork = new Xml_MyArtwork();
            try {
                xmlMyArtwork.F_PAGE_CNT = Integer.valueOf(xmlResult.get("F_PAGE_CNT"));
            } catch (NumberFormatException e) {
                Dlog.e(TAG, e);
            }

            int arrSize = xmlResult.getList("MY_PRJ_LST");
            for (int i = 0; i < arrSize; i++) {
                String f_PROJ_CODE = xmlResult.getListItemD1(i, "F_PROJ_CODE");
                if (f_PROJ_CODE == null || "".equals(f_PROJ_CODE))
                    continue;
                String f_SIMG_PATH = SnapsAPI.DOMAIN(false) + xmlResult.getListItemD1(i, "F_SIMG_PATH");
                String f_PROJ_NAME = xmlResult.getListItemD1(i, "F_PROJ_NAME");
                String f_PROD_NAME = xmlResult.getListItemD1(i, "F_PROD_NAME");
                String f_PROD_CODE = xmlResult.getListItemD1(i, "F_PROD_CODE");
                String f_REG_DATE = xmlResult.getListItemD1(i, "F_REG_DATE");
                String f_BAG_STAT = xmlResult.getListItemD1(i, "F_BAG_STAT");
                xmlMyArtwork.myartworkList.add(new MyArtworkData(f_PROJ_CODE, f_SIMG_PATH, f_PROJ_NAME, f_PROD_NAME, f_PROD_CODE, f_REG_DATE, f_BAG_STAT));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlMyArtwork;
    }

    /**
     * 내 작품함 상세조회
     *
     * @param projCode
     * @return
     */
    public static Xml_MyArtworkDetail getMyArtworkDetail(String projCode, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_MyArtworkDetail xmlMyArtworkDetail = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("f_proj_code", Base64.encodeToString(projCode.getBytes(), 0)));
            String result = HttpUtil.connectGet(SnapsAPI.GET_API_MY_PROJECT_DETAIL(), getParameters, interfaceLogListener);
            XmlResult xmlResult = new XmlResult(result);

            xmlMyArtworkDetail = new Xml_MyArtworkDetail();

            xmlMyArtworkDetail.F_PROJ_CODE = xmlResult.get("F_PROJ_CODE");
            xmlMyArtworkDetail.F_PROJ_NAME = xmlResult.get("F_PROJ_NAME");
            xmlMyArtworkDetail.F_PROD_CODE = xmlResult.get("F_PROD_CODE");
            xmlMyArtworkDetail.F_USER_NAME = xmlResult.get("F_USER_NAME");
            xmlMyArtworkDetail.F_USER_NO = xmlResult.get("F_USER_NO");

            int arrSize = xmlResult.getList("image");
            for (int i = 0; i < arrSize; i++) {
                String idx = xmlResult.getListAttrD1(i, "idx");
                if (idx == null || "".equals(idx))
                    continue;
                String source = SnapsAPI.DOMAIN(false) + xmlResult.getListAttrD1(i, "source");
                xmlMyArtworkDetail.myartworkDetail.add(new MyArtworkDetail(idx, source));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlMyArtworkDetail;
    }

    /**
     * 공지사항 조회
     *
     * @return
     */
    public static Xml_Notice getNotice(int pageNum, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_Notice xmlNotice = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("F_PAGE_NO", String.valueOf(pageNum)));
            String result = HttpUtil.connectGet(SnapsAPI.GET_API_NOTICE(), getParameters, interfaceLogListener);
            XmlResult xmlResult = new XmlResult(result);

            xmlNotice = new Xml_Notice();
            xmlNotice.F_PAGE_CNT = Integer.valueOf(xmlResult.get("F_PAGE_CNT"));

            int arrSize = xmlResult.getList("MY_PBL_LST");
            for (int i = 0; i < arrSize; i++) {
                String f_TITLE = xmlResult.getListItemD1(i, "F_TITLE");
                if (f_TITLE == null || "".equals(f_TITLE))
                    continue;
                String f_CONTENTS = xmlResult.getListItemD1(i, "F_CONTENTS");
                String f_REG_DATE = xmlResult.getListItemD1(i, "F_REG_DATE");
                xmlNotice.noticeList.add(new NoticeData(f_TITLE, f_CONTENTS, f_REG_DATE));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlNotice;
    }

    static final int IO_BUFFER_SIZE = 8 * 1024 * 10;// 80kb

    public static boolean makeDiaryCacheFile(String templateContents) {
        if (!SnapsDiaryDataManager.isAliveSnapsDiaryService()) return false;

        String outputPath = SnapsDiaryDataManager.getInstance().getLayoutTemplateCachePath();
        File targetDir = new File(outputPath).getParentFile();
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outputPath));
            writer.write(templateContents);
            return true;
        } catch (IOException e) {
            Dlog.e(TAG, e);
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (IOException e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    public static Xml_ThemePage getDiaryLayoutList(String prmProdCode, String prodClassCode, String tmpCode, String paramSide, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_ThemePage xmlThemePage = null;
        try {
            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            File cacheFile = new File(dataManager.getLayoutTemplateCachePath());
            String result = null;
            if (cacheFile.exists()) {
                result = FileUtil.readFile(cacheFile);
            }

            if (result == null || result.length() < 1) {
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("prmProdCode", prmProdCode));
                if (prodClassCode != null)
                    parameters.add(new BasicNameValuePair("prmTmplClssCode", prodClassCode));

                if (tmpCode != null)
                    parameters.add(new BasicNameValuePair("prmTmplCode", tmpCode));

                if (paramSide != null)
                    parameters.add(new BasicNameValuePair("prmSide", paramSide));

                //프리미엄 디자인으로 인해 추가.
                if (Config.getDesignId().length() > 0) {
                    parameters.add(new BasicNameValuePair("prmDesignerId", Config.getDesignId()));
                }

                parameters.add(new BasicNameValuePair("sortType", "S4"));

                result = HttpUtil.connectGet(SnapsAPI.GET_API_THEMEBOOK_ADD_PAGE(), parameters, interfaceLogListener);
            }

            ArrayList<ThemePage> themePageList = new ArrayList<>();

            XmlResult xmlResult = new XmlResult(result);
            int arrSize = xmlResult.getList("F_BGCOLOR");
            for (int i = 0; i < arrSize; i++) {
                String tmpl_ssurl = xmlResult.getListItemD1(i, "F_SSMPL_URL");
                String tmpl_mmurl = xmlResult.getListItemD1(i, "F_MMPL_URL");
                String tmpl_id = xmlResult.getListItemD1(i, "F_TMPL_ID");
                String tmpl_code = xmlResult.getListItemD1(i, "F_TMPL_CODE");
                String tmpl_path = xmlResult.getListItemD1(i, "F_XML_PATH");
                String tmpl_tags = xmlResult.getListItemD1(i, "F_SEARCH_TAGS");
                String tmpl_num = xmlResult.getListItemD1(i, "F_DSPL_NUM");
                String tmpl_yn = xmlResult.getListItemD1(i, "F_MYITEM_YN");
                String tmpl_item_code = xmlResult.getListItemD1(i, "F_MYITEM_CODE");
                String tmpl_item = xmlResult.getListItemD1(i, "F_MYMAKE_ITEM");
                String tmpl_yorn = xmlResult.getListItemD1(i, "F_NEW_YORN");
                String tmpl_mask_cnt = xmlResult.getListItemD1(i, "F_MASK_CNT");

                themePageList.add(new ThemePage(tmpl_ssurl, tmpl_mmurl, tmpl_id, tmpl_code, tmpl_path, tmpl_tags,
                        tmpl_num, tmpl_yn, tmpl_item_code, tmpl_item, tmpl_yorn, tmpl_mask_cnt));
            }

            xmlResult.close();

            if (themePageList.size() > 0) {
                xmlThemePage = new Xml_ThemePage();
                xmlThemePage.bgList.addAll(themePageList);
                makeDiaryCacheFile(result);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        Dlog.d("getDiaryLayoutList() return:" + xmlThemePage);
        return xmlThemePage;
    }

    public static Xml_ThemeCover getSmartSnapsAnalysisPhotoBookCoverLayoutList(String prmProdCode, SnapsInterfaceLogListener interfaceLogListener) {
        String result = getSmartSnapsAnalysisPhotoBookLayoutList(prmProdCode, REQUEST_PARAM_FOR_SMART_SNAPS_ANALYSIS_PAGE_TYPE_COVER, "1", interfaceLogListener);
        if (result == null) return null;

        try {
            XmlResult xmlResult = new XmlResult(result);
            Xml_ThemeCover xmlThemeCover = new Xml_ThemeCover();
            int arrSize = xmlResult.getList("F_LAYOUT");

            for (int i = 0; i < arrSize; i++) {
                String tmpl_ssurl = xmlResult.getListItemD1(i, "F_SSMPL_URL");
                String tmpl_mmurl = xmlResult.getListItemD1(i, "F_MMPL_URL");
                String tmpl_id = xmlResult.getListItemD1(i, "F_TMPL_ID");
                String tmpl_code = xmlResult.getListItemD1(i, "F_TMPL_CODE");
                String tmpl_path = xmlResult.getListItemD1(i, "F_XML_PATH");
                String tmpl_tags = xmlResult.getListItemD1(i, "F_SEARCH_TAGS");
                String tmpl_num = xmlResult.getListItemD1(i, "F_DSPL_NUM");
                String tmpl_yn = xmlResult.getListItemD1(i, "F_MYITEM_YN");
                String tmpl_item_code = xmlResult.getListItemD1(i, "F_MYITEM_CODE");
                String tmpl_item = xmlResult.getListItemD1(i, "F_MYMAKE_ITEM");
                String tmpl_yorn = xmlResult.getListItemD1(i, "F_NEW_YORN");
                String tmpl_resize = xmlResult.getListItemD1(i, "F_RESIZE_320_URL");

                xmlThemeCover.bgList.add(new ThemeCover(tmpl_ssurl, tmpl_mmurl, tmpl_id, tmpl_code, tmpl_path, tmpl_tags, tmpl_num, tmpl_yn, tmpl_item_code, tmpl_item, tmpl_yorn, tmpl_resize));
            }

            xmlResult.close();

            return xmlThemeCover;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return null;
    }

    public static Xml_ThemePage getSmartSnapsAnalysisPhotoBookIndexPageLayoutList(String prmProdCode, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_ThemePage xmlThemePage = null;
        try {

            String result = getSmartSnapsAnalysisPhotoBookLayoutList(prmProdCode,
                    REQUEST_PARAM_FOR_SMART_SNAPS_ANALYSIS_PAGE_TYPE_TITLE
                    , "1"
                    , interfaceLogListener);
            if (result == null) return null;

            XmlResult xmlResult = new XmlResult(result);

            xmlThemePage = new Xml_ThemePage();
            int arrSize = xmlResult.getList("F_LAYOUT");

            for (int i = 0; i < arrSize; i++) {
                xmlThemePage.bgList.add(new ThemePage(xmlResult.getListD1(i)));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return xmlThemePage;
    }

    public static Xml_ThemePage getSmartSnapsAnalysisPhotoBookPageLayoutList(String prmProdCode, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_ThemePage xmlThemePage = null;
        try {

            String result = getSmartSnapsAnalysisPhotoBookLayoutList(prmProdCode,
                    REQUEST_PARAM_FOR_SMART_SNAPS_ANALYSIS_PAGE_TYPE_PAGE
                    , ""
                    , interfaceLogListener);
            if (result == null) return null;

            XmlResult xmlResult = new XmlResult(result);

            xmlThemePage = new Xml_ThemePage();
            int arrSize = xmlResult.getList("F_LAYOUT");

            for (int i = 0; i < arrSize; i++) {
                xmlThemePage.bgList.add(new ThemePage(xmlResult.getListD1(i)));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return xmlThemePage;
    }

    public static Xml_ThemePage getSmartSnapsAnalysisPhotoBookPageBGResList(String prmProdCode, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_ThemePage xmlThemePage = null;
        try {

            String result = getSmartSnapsAnalysisPhotoBookBGResList(prmProdCode, interfaceLogListener);
            if (result == null) return null;

            XmlResult xmlResult = new XmlResult(result);

            xmlThemePage = new Xml_ThemePage();
            int arrSize = xmlResult.getList("F_LAYOUT");

            for (int i = 0; i < arrSize; i++) {
                xmlThemePage.bgList.add(new ThemePage(xmlResult.getListD1(i)));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return xmlThemePage;
    }

    private static String getSmartSnapsAnalysisPhotoBookLayoutList(String prmProdCode, String pageType, String maskCnt, SnapsInterfaceLogListener interfaceLogListener) {
        try {
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("prmChnlCode", Config.getCHANNEL_CODE()));
            parameters.add(new BasicNameValuePair("prmProdCode", prmProdCode));
            parameters.add(new BasicNameValuePair("prmPageType", pageType));
            parameters.add(new BasicNameValuePair("prmMaskCount", maskCnt));

            //애니 2.5에서 삭제됨.
//			parameters.add(new BasicNameValuePair("prmTmplCode", Config.getTMPL_CODE()));

            return HttpUtil.connectGet(SnapsAPI.GET_API_SMART_SNAPS_LAYOUT_LIST_URL(), parameters, interfaceLogListener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return null;
    }

    private static String getSmartSnapsAnalysisPhotoBookBGResList(String prmProdCode, SnapsInterfaceLogListener interfaceLogListener) {
        try {

//			https://dev-m.snaps.com/servlet/Command.do?cmd=getBackgroundList&part=mall.smartsnaps.SmartSnapsInterface&prmChnlCode=KOR0031&prmProdCode=00800600130001
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("prmChnlCode", Config.getCHANNEL_CODE()));
            parameters.add(new BasicNameValuePair("prmProdCode", prmProdCode));
//			parameters.add(new BasicNameValuePair("prmPageType", pageType));
//			parameters.add(new BasicNameValuePair("prmTmplCode", SmartSnapsManager.getInstance().getSmartAnalysisPhotoBookTemplateCode()));
//			parameters.add(new BasicNameValuePair("prmMaskCount", maskCnt));

            return HttpUtil.connectGet(SnapsAPI.GET_API_SMART_SNAPS_BG_RES_LIST_URL(), parameters, interfaceLogListener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return null;
    }

    public static SnapsTemplate getSmartSnapsAnalysisPhotoBookLayoutXML(String prmProdCode, String prmTmplId, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("prmChnlCode", Config.getCHANNEL_CODE()));
        parameters.add(new BasicNameValuePair("prmProdCode", prmProdCode));
        parameters.add(new BasicNameValuePair("prmTmplId", prmTmplId));

        String requestUrl = HttpUtil.getEncodedUrlWithParams(SnapsAPI.GET_API_SMART_SNAPS_LAYOUT_XML_URL(), parameters);
        return GetTemplateLoad.getThemeBookTemplate(requestUrl, false, interfaceLogListener);
    }

    public static Xml_ThemePage getPhotoBookPage(String prmProdCode, String prodClassCode, String tmpCode, String paramSide, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_ThemePage xmlThemePage = null;
        try {

            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("prmProdCode", prmProdCode));
            if (prodClassCode != null)
                parameters.add(new BasicNameValuePair("prmTmplClssCode", prodClassCode));

            if (tmpCode != null)
                parameters.add(new BasicNameValuePair("prmTmplCode", tmpCode));

            if (paramSide != null)
                parameters.add(new BasicNameValuePair("prmSide", paramSide));

            //프리미엄 디자인으로 인해 추가.
            if (Config.getDesignId().length() > 0)
                parameters.add(new BasicNameValuePair("prmDesignerId", Config.getDesignId()));

            String result = HttpUtil.connectGet(SnapsAPI.GET_API_THEMEBOOK_ADD_PAGE(), parameters, interfaceLogListener);
            Dlog.d(Dlog.PRE_FIX_CS + "Design list : " + SnapsAPI.GET_API_THEMEBOOK_ADD_PAGE());

            XmlResult xmlResult = new XmlResult(result);

            xmlThemePage = new Xml_ThemePage();
            int arrSize = xmlResult.getList("F_BGCOLOR");

            for (int i = 0; i < arrSize; i++) {
                xmlThemePage.bgList.add(new ThemePage(xmlResult.getListD1(i)));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        Dlog.d("getPhotoBookPage() result:" + xmlThemePage);
        return xmlThemePage;
    }

    public static Xml_ThemeCover getThemeBookCover(String prmProdCode, String prodClassCode, boolean isLeatherCover, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_ThemeCover xmlThemeCover = null;
        try {

            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("prmProdCode", prmProdCode));
            if (prodClassCode != null)
                parameters.add(new BasicNameValuePair("prmTmplClssCode", prodClassCode));

            //프리미엄 디자인으로 인해 추가.
            if (!isLeatherCover) { //2016.09.08 운영 개발팀에서 레더커버는 디자이너 아이디를 빼고 요청해 달라고 함.
                if (Config.getDesignId().length() > 0) {
                    parameters.add(new BasicNameValuePair("prmDesignerId", Config.getDesignId()));
                }
            }

            String result = HttpUtil.connectGet(SnapsAPI.GET_API_THEMEBOOK_COVER(), parameters, interfaceLogListener);

            XmlResult xmlResult = new XmlResult(result);

            xmlThemeCover = new Xml_ThemeCover();
            int arrSize = xmlResult.getList("F_BGCOLOR");

            for (int i = 0; i < arrSize; i++) {
                String tmpl_ssurl = xmlResult.getListItemD1(i, "F_SSMPL_URL");
                String tmpl_mmurl = xmlResult.getListItemD1(i, "F_MMPL_URL");
                String tmpl_id = xmlResult.getListItemD1(i, "F_TMPL_ID");
                String tmpl_code = xmlResult.getListItemD1(i, "F_TMPL_CODE");
                String tmpl_path = xmlResult.getListItemD1(i, "F_XML_PATH");
                String tmpl_tags = xmlResult.getListItemD1(i, "F_SEARCH_TAGS");
                String tmpl_num = xmlResult.getListItemD1(i, "F_DSPL_NUM");
                String tmpl_yn = xmlResult.getListItemD1(i, "F_MYITEM_YN");
                String tmpl_item_code = xmlResult.getListItemD1(i, "F_MYITEM_CODE");
                String tmpl_item = xmlResult.getListItemD1(i, "F_MYMAKE_ITEM");
                String tmpl_yorn = xmlResult.getListItemD1(i, "F_NEW_YORN");
                String tmpl_resize = xmlResult.getListItemD1(i, "F_RESIZE_320_URL");

                xmlThemeCover.bgList.add(new ThemeCover(tmpl_ssurl, tmpl_mmurl, tmpl_id, tmpl_code, tmpl_path, tmpl_tags, tmpl_num, tmpl_yn, tmpl_item_code, tmpl_item, tmpl_yorn, tmpl_resize));
            }

            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        Dlog.d("getThemeBookCover() result:" + xmlThemeCover);
        return xmlThemeCover;
    }

    public static Xml_ThemeCategory getThemeBookCategory(SnapsInterfaceLogListener interfaceLogListener) {
        Xml_ThemeCategory xmlThemeCategory = null;
        try {

            String result = HttpUtil.connectGet(SnapsAPI.GET_API_THEMEBOOK_CATEGORY(), interfaceLogListener);

            XmlResult xmlResult = new XmlResult(result);

            xmlThemeCategory = new Xml_ThemeCategory();
            int arrSize = xmlResult.getList("ED_ST_CAT");

            for (int i = 0; i < arrSize; i++) {
                String tmpl_code = xmlResult.getListItemD1(i, "F_CATEGORY_CODE");
                String tmpl_name = xmlResult.getListItemD1(i, "F_CATEGORY_NAME");
                String tmpl_imgPath = xmlResult.getListItemD1(i, "F_EIMG_PATH");

                xmlThemeCategory.bgList.add(new ThemeCategory(tmpl_code, tmpl_name, tmpl_imgPath));
            }

            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        Dlog.d("getThemeBookCategory() result:" + xmlThemeCategory);
        return xmlThemeCategory;

    }

    public static Xml_ThemeContents getThemeBookContents(String categoryCode, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_ThemeContents xmlThemeContents = null;
        try {

            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("F_CATE_CODE", categoryCode));

            String result = HttpUtil.connectGet(SnapsAPI.GET_API_THEMEBOOK_CONTENTS(), getParameters, interfaceLogListener);

            XmlResult xmlResult = new XmlResult(result);

            xmlThemeContents = new Xml_ThemeContents();
            int arrSize = xmlResult.getList("ED_ST_LST");

            for (int i = 0; i < arrSize; i++) {
                String tmpl_code = xmlResult.getListItemD1(i, "F_RSRC_CODE");
                String tmpl_name = xmlResult.getListItemD1(i, "F_RSRC_NAME");

                String tmpl_dimg = xmlResult.getListItemD1(i, "F_DIMG_PATH");
                String tmpl_eimg = xmlResult.getListItemD1(i, "F_EIMG_PATH");

                String tmpl_date = xmlResult.getListItemD1(i, "F_REG_DATE");
                String tmpl_tags = xmlResult.getListItemD1(i, "F_SEARCH_TAGS");
                String tmpl_categorycode = xmlResult.getListItemD1(i, "F_CATEGORY_CODE");

                xmlThemeContents.bgList.add(new ThemeContents(tmpl_code, tmpl_name, tmpl_dimg, tmpl_eimg, tmpl_date, tmpl_tags, tmpl_categorycode));
            }

            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        Dlog.d("getThemeBookContents() result:" + xmlThemeContents);
        return xmlThemeContents;
    }

    /**
     * 이용문의 조회
     *
     * @return
     */
    public static Xml_QnA getQnA(int pageNum, String userId, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_QnA xmlQnA = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("F_PAGE_NO", String.valueOf(pageNum)));
            getParameters.add(new BasicNameValuePair("F_USER_id", userId));
            String result = HttpUtil.connectGet(SnapsAPI.GET_API_QUESTION_LIST(), getParameters, interfaceLogListener);
            XmlResult xmlResult = new XmlResult(result);

            xmlQnA = new Xml_QnA();
            xmlQnA.F_PAGE_CNT = Integer.valueOf(xmlResult.get("F_PAGE_CNT"));
            int arrSize = xmlResult.getList("MY_C1_LST");
            for (int i = 0; i < arrSize; i++) {
                String f_DATA_TYPE = xmlResult.getListItemD1(i, "F_DATA_TYPE");
                if (f_DATA_TYPE == null || "".equals(f_DATA_TYPE))
                    continue;
                String f_BBS_NUMB = xmlResult.getListItemD1(i, "F_BBS_NUMB");
                String f_BBS_CONTENTS = xmlResult.getListItemD1(i, "F_BBS_CONTENTS");
                String f_REG_DATE = xmlResult.getListItemD1(i, "F_REG_DATE").split(" ")[0];
                xmlQnA.qnaList.add(new QnAData(f_DATA_TYPE, f_BBS_NUMB, f_BBS_CONTENTS, f_REG_DATE));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlQnA;
    }

    /**
     * 스냅스 스티커 상세 리스트 조회
     *
     * @return
     */
    public static Xml_StickerKit getStickerKit(String categoryCode, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_StickerKit xmlStickerKit = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("F_CATE_CODE", categoryCode));
            String result = HttpUtil.connectGet(SnapsAPI.GET_API_IMAGE_LIST_STICKER(), getParameters, interfaceLogListener);
            XmlResult xmlResult = new XmlResult(result);

            xmlStickerKit = new Xml_StickerKit();
            int arrSize = xmlResult.getList("ED_ST_LST");
            for (int i = 0; i < arrSize; i++) {
                String f_RSRC_CODE = xmlResult.getListItemD1(i, "F_RSRC_CODE");
                if (f_RSRC_CODE == null || "".equals(f_RSRC_CODE))
                    continue;
                String f_RSRC_NAME = xmlResult.getListItemD1(i, "F_RSRC_NAME");
                String f_DIMG_PATH = SnapsAPI.DOMAIN(false) + xmlResult.getListItemD1(i, "F_DIMG_PATH");
                String f_EIMG_PATH = SnapsAPI.DOMAIN(false) + xmlResult.getListItemD1(i, "F_EIMG_PATH");
                String f_SEARCH_TAG = xmlResult.getListItemD1(i, "F_SEARCH_TAG");
                String f_CATEGORY_CODE = xmlResult.getListItemD1(i, "F_CATEGORY_CODE");
                xmlStickerKit.stickerKitList.add(new StickerKitData(f_RSRC_CODE, f_RSRC_NAME, f_DIMG_PATH, f_EIMG_PATH, f_SEARCH_TAG, f_CATEGORY_CODE));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlStickerKit;
    }

    /**
     * 스냅스 스티커 앨범 리스트 조회
     *
     * @return
     */
    public static Xml_StickerKit_Album getStickerKitAlbum(SnapsInterfaceLogListener interfaceLogListener) {

        Xml_StickerKit_Album xmlStickerKitAlbum = null;
        try {
            String result = HttpUtil.connectGet(SnapsAPI.GET_API_IMAGE_ALBUMLIST_STICKER(), interfaceLogListener);
            XmlResult xmlResult = new XmlResult(result);

            xmlStickerKitAlbum = new Xml_StickerKit_Album();
            int arrSize = xmlResult.getList("ED_ST_CAT");
            for (int i = 0; i < arrSize; i++) {
                String f_CATEGORY_CODE = xmlResult.getListItemD1(i, "F_CATEGORY_CODE");
                if (f_CATEGORY_CODE == null || "".equals(f_CATEGORY_CODE) || (!Config.useKorean() && checkKoreanOnlySticker(f_CATEGORY_CODE)))
                    continue;
                String f_CATEGORY_NAME = xmlResult.getListItemD1(i, "F_CATEGORY_NAME");
                String f_EIMG_PATH = SnapsAPI.DOMAIN(false) + xmlResult.getListItemD1(i, "F_EIMG_PATH");
                xmlStickerKitAlbum.stickerKitList.add(new StickerKitAlbumData(f_CATEGORY_CODE, f_CATEGORY_NAME, f_EIMG_PATH));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlStickerKitAlbum;
    }

    /**
     * 한국어 버전이 아닐때 노출할 스티커인지 체크.
     *
     * @param categoryCode
     * @return
     */
    public static boolean checkKoreanOnlySticker(String categoryCode) {
        final int[] onlyKoreanCodes = {044112, 044110, 044116, 044115, 044114, 044107}; // 개골씨, 텍스트, 사물, 감정날씨, 시즌기념일, 별;
        if (!StringUtil.isEmpty(categoryCode)) {
            int intCode = -1;
            try {
                intCode = Integer.parseInt(categoryCode);
            } catch (NumberFormatException e) {
                return true;
            }

            for (int i = 0; i < onlyKoreanCodes.length; ++i)
                if (onlyKoreanCodes[i] == intCode) return true;
        }
        return false;
    }

    /**
     * 우편번호 검색
     *
     * @param searchWord
     * @return
     */
    public static List<Xml_PostAddress> getMyZip(String searchWord, SnapsInterfaceLogListener interfaceLogListener) {
        List<Xml_PostAddress> postLists = new ArrayList<Xml_PostAddress>();
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            if (Config.getCHANNEL_CODE() != null && Config.getCHANNEL_CODE().equalsIgnoreCase(Config.CHANNEL_SNAPS_JPN))
                getParameters.add(new BasicNameValuePair("F_ZIPCD", searchWord));
            else
                getParameters.add(new BasicNameValuePair("F_DONG", searchWord));
            String result = HttpUtil.connectGet(SnapsAPI.GET_API_MY_ZIP(), getParameters, interfaceLogListener);
            XmlResult xmlResult = new XmlResult(result);

            int arrSize = xmlResult.getList("MY_ZIP_LST");
            for (int i = 0; i < arrSize; i++) {
                String f_ZIPCD = xmlResult.getListItemD1(i, "F_ZIPCD");
                if (f_ZIPCD == null || "".equals(f_ZIPCD))
                    continue;
                String f_ADDR = xmlResult.getListItemD1(i, "F_ADDR");
                String f_ADDR2 = xmlResult.getListItemD1(i, "F_ADDR2");
                String f_ADD_PRICE = xmlResult.getListItemD1(i, "F_ADD_PRICE");
                postLists.add(new Xml_PostAddress(f_ZIPCD, f_ADDR, f_ADDR2, f_ADD_PRICE));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        }
        return postLists;
    }

    /**
     * 최근 배송지 조회
     *
     * @param userId
     * @return
     */
    public static List<Xml_RecentAddress> getMyDeliveryOrder(String userId, SnapsInterfaceLogListener interfaceLogListener) {
        List<Xml_RecentAddress> orderLists = new ArrayList<Xml_RecentAddress>();
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("F_USER_ID", userId));
            String result = HttpUtil.connectGet(SnapsAPI.GET_API_MY_DLEIVERY_ORDER(), getParameters, interfaceLogListener);
            XmlResult xmlResult = new XmlResult(result);

            int arrSize = xmlResult.getList("MY_HIS_LST");
            for (int i = 0; i < arrSize; i++) {
                String f_RCPT_NAME = xmlResult.getListItemD1(i, "F_RCPT_NAME");
                if (f_RCPT_NAME == null || "".equals(f_RCPT_NAME))
                    continue;
                String f_RCPT_TELP = xmlResult.getListItemD1(i, "F_RCPT_TELP");
                String f_RCPT_CELL = xmlResult.getListItemD1(i, "F_RCPT_CELL");
                String f_RCPT_ZIP = xmlResult.getListItemD1(i, "F_RCPT_ZIP");
                String f_RCPT_ADDR1 = xmlResult.getListItemD1(i, "F_RCPT_ADDR1");
                String f_RCPT_ADDR2 = xmlResult.getListItemD1(i, "F_RCPT_ADDR2");
                String f_RCPT_ADDR3 = xmlResult.getListItemD1(i, "F_RCPT_ADDR3");
                String f_ADD_PRICE = xmlResult.getListItemD1(i, "F_ADD_PRICE");
                orderLists.add(new Xml_RecentAddress(f_RCPT_NAME, f_RCPT_TELP, f_RCPT_CELL, f_RCPT_ZIP, f_RCPT_ADDR1, f_RCPT_ADDR2, f_RCPT_ADDR3, f_ADD_PRICE));
            }
            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        }
        return orderLists;
    }

    /**
     * 내작품함, 장바구니, 쿠폰 갯수 조회
     *
     * @param userId
     * @return
     */
    // POST_API_BADGE_COUNT
    public static Xml_MyBadge getMyBadge(String userId, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
        getParameters.add(new BasicNameValuePair("F_USER_ID", userId));
        getParameters.add(new BasicNameValuePair("f_os_type", "190002"));
        return getMyBadge(getParameters, interfaceLogListener);
    }

    /**
     * 내작품함, 장바구니, 쿠폰 갯수 조회
     *
     * @return
     */
    // POST_API_BADGE_COUNT
    public static Xml_MyBadge getMyBadge(List<NameValuePair> getParameters, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_MyBadge badgeCNT = null;
        try {
            String result = HttpUtil.connectGet(SnapsAPI.POST_API_BADGE_COUNT(), getParameters, interfaceLogListener);
            XmlResult xmlResult = new XmlResult(result);

            String f_PROJ_CNT = xmlResult.get("F_PROJ_CNT");
            String f_CART_CNT = xmlResult.get("F_CART_CNT");
            String f_CPN_CNT = xmlResult.get("F_CPN_CNT");
            String f_PBL_CNT = xmlResult.get("F_PBL_CNT");
            String f_EVENT_CNT = xmlResult.get("F_EVENT_CNT");
            badgeCNT = new Xml_MyBadge(f_PROJ_CNT, f_CART_CNT, f_CPN_CNT, f_PBL_CNT, f_EVENT_CNT);

            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        }
        return badgeCNT;
    }

    /**
     * 홈 상품 가격정보 조회
     *
     * @return
     */
    public static Xml_MyHomePrice getMyPrice(SnapsInterfaceLogListener interfaceLogListener) {
        Xml_MyHomePrice xmlPrice = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            String result = HttpUtil.connectGet(SnapsAPI.GET_API_HOME_PRICE(), getParameters, interfaceLogListener);
            XmlResult xmlResult = new XmlResult(result);

            xmlPrice = new Xml_MyHomePrice();

            int arrSize = xmlResult.getList("HM_PROD_INFO");
            for (int i = 0; i < arrSize; i++) {
                String f_PROD_CODE = xmlResult.getListItemD1(i, "F_PROD_CODE");
                if (f_PROD_CODE == null || "".equals(f_PROD_CODE))
                    continue;
                String f_PROD_NAME = xmlResult.getListItemD1(i, "F_PROD_NAME");
                String f_SELL_PRICE = xmlResult.getListItemD1(i, "F_SELL_PRICE");
                String f_ORG_PRICE = xmlResult.getListItemD1(i, "F_ORG_PRICE");
                String f_MAX_PAGE = xmlResult.getListItemD1(i, "F_MAX_PAGE");
                String f_DLVR_MTHD = xmlResult.getListItemD1(i, "F_DLVR_MTHD");
                String f_DLVR_DAY = xmlResult.getListItemD1(i, "F_DLVR_DAY");
                String f_DISC_RATE = xmlResult.getListItemD1(i, "F_DISC_RATE");

                xmlPrice.myPriceList.add(new MyPriceData(f_PROD_CODE, f_PROD_NAME, f_SELL_PRICE, f_ORG_PRICE, f_MAX_PAGE, f_DLVR_MTHD, f_DLVR_DAY, f_DISC_RATE));
            }

            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlPrice;
    }

    /**
     * 프로젝트 코드 GET
     *
     * @param userId 카카오 스토리 ID
     * @return
     */
    public static String getProjectCode(String userId, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userno", userId));
        params.add(new BasicNameValuePair("appver", "mobile"));

        return getProjectCode(params, interfaceLogListener);
    }

    public static String getProjectCode(List<NameValuePair> params, SnapsInterfaceLogListener interfaceLogListener) {
        params.add(new BasicNameValuePair("appver", "mobile"));
        String result = HttpUtil.connectGet(SnapsAPI.GET_API_PROJECT_CODE(), params, interfaceLogListener);

        return result;
    }

    public static String getResultVerifyProjectCode(String projectCode, SnapsInterfaceLogListener interfaceLogListener) {
        if (projectCode == null) return null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("albm_id", projectCode));
        String result = HttpUtil.connectGet(SnapsAPI.GET_API_VERIFY_PROJECT_CODE(), params, interfaceLogListener);
        return result;
    }

    /**
     * 배경, 레이아웃 리스트.
     *
     * @param code 상품코드
     * @param type 상품타입
     * @param item 아이템코드 ( 스티커킷에서만 필요함 )
     * @return
     */
    public static Xml_BgCover getChangeList(String code, String type, String item, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_BgCover xmlBgCover = null;

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            // 상품 코드
            params.add(new BasicNameValuePair("prmProdCode", code));
            // 상품 타입
            if (type.equals("") == false) {
                params.add(new BasicNameValuePair("prmTmplClssCode", type));
            }

            Dlog.d("getChangeList() params:" + params);

            String result = HttpUtil.connectGet(SnapsAPI.GET_API_MOBILE_TMPLRSRC_LIST(), params, interfaceLogListener);

            XmlResult xmlResult = new XmlResult(result);

            xmlBgCover = new Xml_BgCover();
            int arrSize = xmlResult.getList("BGCOVER");

            for (int i = 0; i < arrSize; i++) {
                String tmpl_id = xmlResult.getListItemD1(i, "F_TMPL_ID");
                String tmpl_code = xmlResult.getListItemD1(i, "F_TMPL_CODE");
                String smpl_url = SnapsAPI.DOMAIN(false) + xmlResult.getListItemD1(i, "F_SMPL_URL");
                String mmpl_url = SnapsAPI.DOMAIN(false) + xmlResult.getListItemD1(i, "F_MMPL_URL");
                String xml_path = SnapsAPI.DOMAIN(false) + xmlResult.getListItemD1(i, "F_XML_PATH");

                xmlBgCover.bgList.add(new BgCoverData(tmpl_id, tmpl_code, smpl_url, mmpl_url, xml_path));
            }

            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return xmlBgCover;
    }

    /**
     * 커버리스트 내 커버 리소스 조회
     *
     * @param coverUrl
     * @return
     */
    public static Xml_CoverResource getCoverColor(int idx, String coverUrl, SnapsInterfaceLogListener interfaceLogListener) {
        Xml_CoverResource listCoverRs = null;
        try {
            String result = HttpUtil.connectGet(coverUrl, interfaceLogListener);
            XmlResult xmlResult = new XmlResult(result);
            String target = xmlResult.get("source", "target");
            String coverColor = xmlResult.get("source", "covercolor");
            String bgColor = xmlResult.get("source", "bgcolor");
            String coverImgUrl = SnapsAPI.DOMAIN(false) + xmlResult.get("source", "resourceURL");
            String fontColor = xmlResult.get("source", "font_color");
            xmlResult.close();

            listCoverRs = new Xml_CoverResource(idx, target, "#" + coverColor, "#" + bgColor, coverImgUrl, "#" + fontColor);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return listCoverRs;
    }

    /**
     * 쿠폰 리스트 조회
     *
     * @return
     */
    public static Xml_CouponList getCouponList(String userId, String projcode, String cnt) {
        Xml_CouponList xmlCoupon = null;
        try {
            List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
            getParameters.add(new BasicNameValuePair("F_USER_ID", userId));
            getParameters.add(new BasicNameValuePair("F_PROJ_CODE", projcode));
            getParameters.add(new BasicNameValuePair("F_PROJ_CNT", cnt));
            getParameters.add(new BasicNameValuePair("F_MODE", "list"));
            getParameters.add(new BasicNameValuePair("F_VER", Const_VALUE.API_VERSION));
            getParameters.add(new BasicNameValuePair("f_os_type", "190002"));
            String result = HttpUtil.connectPost(SnapsAPI.SET_COUPON(), getParameters, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

            JSONArray jsonResult = new JSONArray(result);
            JSONObject jsonObject = null;
            xmlCoupon = new Xml_CouponList();

            for (int i = 0; i < jsonResult.length(); i++) {
                jsonObject = jsonResult.getJSONObject(i);

                String cpn_name = jsonObject.getString("F_ISSUE_CAUSE");
                String cpn_code = jsonObject.getString("F_DOC_CODE");
                String cpn_issdate = jsonObject.getString("F_ISSUE_DATE");
                String cpn_expdate = jsonObject.getString("F_EXPR_DATE");
                String cpn_ordprice = jsonObject.getString("F_ORDR_PRICE");
                String cpn_reprice = jsonObject.getString("F_REMAIN_PRICE");
                String cpn_dcprice = jsonObject.getString("F_DISC_PRICE");
                String cpn_chcode = jsonObject.getString("F_CHNL_CODE");

                // 20131015 사용가능 여부 추가
                boolean F_USE_YORN = false;
                try {
                    F_USE_YORN = jsonObject.getString("F_USE_YORN").equals("Y") ? true : false;
                } catch (JSONException e) {
                    F_USE_YORN = true;
                }

                xmlCoupon.couponList.add(new CouponData(cpn_name, cpn_code, cpn_issdate, cpn_expdate, cpn_ordprice, cpn_reprice, cpn_dcprice, F_USE_YORN, cpn_chcode));
            }

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xmlCoupon;
    }

    // 푸쉬 공지 및 이벤트
    public static Xml_PushAllUser PushAllUser(String user_no) {
        Xml_PushAllUser xmlalluser = null;
        try {
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

            postParameters.add(new BasicNameValuePair("f_user_no", user_no));
            postParameters.add(new BasicNameValuePair("f_os_type", "190002"));

            String result = HttpUtil.connectPost(SnapsAPI.PUSH_INTERFACE(), postParameters, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

            Dlog.d("PushAllUser() result:" + result);

            JSONObject jsonObject1 = new JSONObject(result);
            JSONObject jsonObject2 = new JSONObject(result);
            JSONObject jsonObject3 = new JSONObject(result);
            xmlalluser = new Xml_PushAllUser();

            jsonObject2 = (JSONObject) jsonObject2.get("result");

            String status = jsonObject2.getString("status");

            if (status.equals("ok")) {
                jsonObject1 = (JSONObject) jsonObject1.get("broadcast_info");

                String brdcst = jsonObject1.getString("f_brdcst_code");
                String exist = jsonObject1.getString("f_img_include");
                String close = jsonObject1.getString("f_close_yorn");
                String rcv = jsonObject1.getString("f_rcv_type");

                jsonObject3 = (JSONObject) jsonObject3.get("images");
                JSONArray jsonResult = jsonObject3.getJSONArray("img_sources");

                String imgpath = "";
                String target = "";

                for (int i = 0; i < jsonResult.length(); i++) {
                    jsonObject3 = jsonResult.getJSONObject(i);

                    imgpath = jsonObject3.getString("f_img_path");
                    target = jsonObject3.getString("f_target_url");
                }

                xmlalluser.pushAllUserList.add(new PushAllUser(brdcst, exist, close, rcv, imgpath, target, status));
            } else {
                xmlalluser.pushAllUserList.add(new PushAllUser("", "", "", "", "", "", status));
            }

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return xmlalluser;
    }

    /***
     * 쿠폰 분류 정보 조회
     *
     * @return
     */
    public static Xml_CouponKind getCouponKindInfo(SnapsInterfaceLogListener interfaceLogListener) {
        Xml_CouponKind couponKind = null;
        try {
            String result = HttpUtil.connectGet(SnapsAPI.COUPON_KIND_INFO(), interfaceLogListener);
            JSONArray jsonResult = new JSONArray(result);
            JSONObject jsonObject = null;
            couponKind = new Xml_CouponKind();

            for (int i = 0; i < jsonResult.length(); i++) {
                jsonObject = jsonResult.getJSONObject(i);

                String F_CLSS_CODE = jsonObject.getString("F_CLSS_CODE");
                String F_CLSS_NAME = jsonObject.getString("F_CLSS_NAME");
                String F_CLSS_DIGIT = jsonObject.getString("F_CLSS_DIGIT");

                couponKind.couponKindList.add(new CouponKindInfo(F_CLSS_CODE, F_CLSS_NAME, F_CLSS_DIGIT));
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return couponKind;
    }

    /***
     * 프로젝트 코드로 save.xml을 받아오는 함수...
     *
     * @param prjCode
     * @return
     */
    public static String getSaveXML(String prjCode, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("prmProjCode", prjCode));

        String url = SnapsAPI.GET_API_SAVE_XML();
        String result = HttpUtil.connectGet(url, params, interfaceLogListener);

        Dlog.d("getSaveXML() url:" + url);
        Dlog.d("getSaveXML() result:" + result);
        return result;
    }

    /***
     * facebook imgseq 가져오는 함수.
     *
     * @param imgUrl
     * @return
     */
    public static String getSNSImageImgSEQ(String imgUrl, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("prmImagePath", imgUrl));
        String result = HttpUtil.connectGet(SnapsAPI.GET_API_SNS_IMAGE_IMGSEQ(), params, interfaceLogListener);
        return result;
    }

    public static boolean requestReviewEventReg(String userID, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("f_user_ID", userID));

        String result = HttpUtil.connectGet(SnapsAPI.SET_API_REVIEWEVENT(), params, interfaceLogListener);

        return true;
    }

    public static boolean requestPushLog(String userID, String brdcst_code, SnapsInterfaceLogListener interfaceLogListener) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("f_user_no", userID));
        params.add(new BasicNameValuePair("f_brdcst_code", brdcst_code));

        String result = HttpUtil.connectGet(SnapsAPI.PUSH_SEND_INTERFACE(), params, interfaceLogListener);
        Dlog.d("requestPushLog() result:" + result);

        return true;
    }

    public static ArrayList<String> getSampleViewUrl(String tempid, SnapsInterfaceLogListener interfaceLogListener) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("f_tmpl_id", tempid));

        String result = HttpUtil.connectGet(SnapsAPI.SET_API_SAMPLEVIEW(), params, interfaceLogListener);

        ArrayList<String> retvalue = new ArrayList<String>();

        try {
            XmlResult xmlResult = new XmlResult(result);
            int arrSize = xmlResult.getList("image");
            for (int i = 0; i < arrSize; i++) {
                String url = xmlResult.getListAttrD1(i, "source");
                retvalue.add(url);
            }

            xmlResult.close();

        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        }

        return retvalue;

    }

    public static void initTitleInfo(SnapsInterfaceLogListener interfaceLogListener) {
        if (hashDate != null) return;

        String result = HttpUtil.connectGet(SnapsAPI.CALENDAR_TITLE_INFO(), interfaceLogListener);
        Dlog.d("initTitleInfo() titleInfo:" + SnapsAPI.CALENDAR_TITLE_INFO());

        int currentYearNumber = Calendar.getInstance().get(Calendar.YEAR);
        currentYearNumber = currentYearNumber - 2;
        String currentYear = Integer.toString(currentYearNumber);

        HashMap<String, String> titleDayInfoMap = new HashMap<String, String>();
        StringBuilder sb = new StringBuilder();
        try {
            XmlResult xmlResult = new XmlResult(result);
            NodeList nodeList = xmlResult.getNodeList("title_day");
            int nodeListCount = nodeList.getLength();
            for (int i = 0; i < nodeListCount; i++) {
                Node node = nodeList.item(i);
                NodeList itemList = node.getChildNodes();

                int childCount = itemList.getLength();
                for (int j = 0; j < childCount; j++) {
                    Node itemNode = itemList.item(j);
                    if (itemNode.getNodeType() != Node.ELEMENT_NODE) continue;
                    Element element = (Element) itemNode;
                    String year = element.getAttribute("year");
                    if (currentYear.compareTo(year) > 0) continue;

                    String type = element.getAttribute("insert");
                    String month = element.getAttribute("month");
                    String day = element.getAttribute("day");
                    String dayTitle = element.getTextContent();

                    sb.setLength(0);
                    if (type.equals("day_title")) {
                        sb.append("DAY_TITLE").append(year).append("_").append(month).append("_").append(day);
                        titleDayInfoMap.put(sb.toString(), dayTitle);
                    } else if (type.equals("day_title_2")) {
                        sb.append("DAY_TITLE2").append(year).append("_").append(month).append("_").append(day);
                        titleDayInfoMap.put(sb.toString(), dayTitle);
                    }
                }
            }

            nodeList = xmlResult.getNodeList("holy_day");
            nodeListCount = nodeList.getLength();
            for (int i = 0; i < nodeListCount; i++) {
                Node node = nodeList.item(i);
                NodeList itemList = node.getChildNodes();

                int childCount = itemList.getLength();
                for (int j = 0; j < childCount; j++) {
                    Node itemNode = itemList.item(j);
                    if (itemNode.getNodeType() != Node.ELEMENT_NODE) continue;
                    Element element = (Element) itemNode;
                    String year = element.getAttribute("year");
                    if (currentYear.compareTo(year) > 0) continue;

                    String month = element.getAttribute("month");
                    String day = element.getAttribute("day");
                    String dayTitle = element.getTextContent();

                    sb.setLength(0);
                    sb.append("holy_date").append(year).append("_").append(month).append("_").append(day);

                    titleDayInfoMap.put(sb.toString(), dayTitle);
                }
            }

            xmlResult.close();

            hashDate = titleDayInfoMap;

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static String getDayTitle(int nMonth, int nDay, int nYear) {

        String cmpMonth = String.format("%d", nMonth);

        String cmpYear = String.format("%d", nYear);

        String cmpDay = String.format("%d", nDay);

        String dayTitle = hashDate.get("DAY_TITLE" + cmpYear + "_" + cmpMonth + "_" + cmpDay);

        return dayTitle;

    }

    public static String getDayTitle2(int nMonth, int nDay, int nYear) {

        String cmpMonth = String.format("%d", nMonth);

        String cmpYear = String.format("%d", nYear);

        String cmpDay = String.format("%d", nDay);

        String dayTitle2 = hashDate.get("DAY_TITLE2" + cmpYear + "_" + cmpMonth + "_" + cmpDay);

        return dayTitle2;

    }

    public static boolean isHolliday(int nMonth, int nDay, int nYear) {
        if (Config.useKorean()) {
            String cmpMonth = String.format("%d", nMonth);

            String cmpYear = String.format("%d", nYear);
            String cmpDay;

            if (nDay > 0 && nDay <= 9) {
                cmpDay = String.format("0%d", nDay);

            } else
                cmpDay = String.format("%d", nDay);

            String holyday = hashDate.get("holy_date" + cmpYear + "_" + cmpMonth + "_" + cmpDay);

            if (holyday == null)
                return false;
            return true;
        } else {
            //외국어 버전에서는 일요일만 붉은 색으로 표시한다.
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, nYear);
            calendar.set(Calendar.MONTH, nMonth - 1);
            calendar.set(Calendar.DAY_OF_MONTH, nDay);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            return dayOfWeek == 1;
        }
    }

    public static int getStarCalendarIndex(int nMonth, int nYear) {
        int day = 0;
        Calendar cal = Calendar.getInstance();

        cal.set(nYear, nMonth - 1, 1);
        int ret = cal.get(Calendar.DAY_OF_MONTH);
        int ret2 = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        day = cal.get(Calendar.DAY_OF_WEEK);
        // 일,월,화,수,목,금,토 -> 1,2,3,4,5,6,7

        return day - 1;
    }

    public static int getMaximumDay(int nMonth, int nYear) {
        int day = 0;
        Calendar cal = Calendar.getInstance();

        cal.set(nYear, nMonth - 1, 1);
        int nMaximum = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        return nMaximum;
    }

    public static String getCouponApplyList(String userno, String couponNum, String chnlCode, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("F_USER_ID", userno));
        params.add(new BasicNameValuePair("F_DOC_CODE", couponNum));
        params.add(new BasicNameValuePair("F_CHNL_CODE", chnlCode));

        String result = HttpUtil.connectGet(SnapsAPI.GET_API_COUPON_APPLYLIST(), params, interfaceLogListener);

        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        String ret = "";
        try {

            jsonArray = new JSONArray(result);
            jsonObject = (JSONObject) jsonArray.getJSONObject(0);
            ret = jsonObject.getString("F_PROD_NAME");
        } catch (JSONException e) {
            Dlog.e(TAG, e);
        }

        return ret;

    }

    /***
     * 카카오 친구 추가 이벤트 등록 함수..
     *
     * @param myUserNo
     * @return
     */
    public static String regKakaoInvite(String myUserNo, String sendno, String eventcode, String deviceid, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("f_user_no", myUserNo));
        params.add(new BasicNameValuePair("f_device_no", deviceid));

        //카카오친구초대 개선
        params.add(new BasicNameValuePair("params", sendno));
        params.add(new BasicNameValuePair("f_status", "315002"));
        params.add(new BasicNameValuePair("f_event_code", "314013"));


        String retString = HttpUtil.connectGet(SnapsAPI.REG_KAKAO_INVITE_EVENT(), params, interfaceLogListener);

        JSONObject jsonObject = null;
        String ret = "fail";
        try {
            jsonObject = new JSONObject(retString);
            ret = (String) jsonObject.get("result");
        } catch (JSONException e) {
            Dlog.e(TAG, e);
            ret = "error";
        }

        return ret;
    }

    public static SnapsTemplatePrice getProductPriceInfo(String prmProjCode, SnapsInterfaceLogListener interfaceLogListener) {
        SnapsTemplatePrice priceInfo = null;
        try {

            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("prmProjCode", prmProjCode));

            String result = HttpUtil.connectGet(SnapsAPI.GET_API_PRODUCT_PRICE_URL(), parameters, interfaceLogListener);

            XmlResult xmlResult = new XmlResult(result);

            priceInfo = new SnapsTemplatePrice();

            priceInfo.F_COMP_CODE = xmlResult.get("F_COMP_CODE");
            priceInfo.F_PROD_CODE = xmlResult.get("F_PROD_CODE");
            priceInfo.F_SELL_PRICE = xmlResult.get("F_SELL_PRICE");
            priceInfo.F_ORG_PRICE = xmlResult.get("F_ORG_PRICE");
            priceInfo.F_PRICE_NUM = xmlResult.get("F_PRICE_NUM");
            priceInfo.F_PRNT_BQTY = xmlResult.get("F_PRNT_BQTY");
            priceInfo.F_PRNT_EQTY = xmlResult.get("F_PRNT_EQTY");
            priceInfo.F_PAGE_ADD_PRICE = xmlResult.get("F_PAGE_ADD_PRICE");
            priceInfo.F_ORG_PAGE_ADD_PRICE = xmlResult.get("F_ORG_PAGE_ADD_PRICE");
            priceInfo.F_DISC_RATE = xmlResult.get("F_DISC_RATE");

            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return priceInfo;
    }

    public static void postAppLaunchCount(String appVer, SnapsInterfaceLogListener interfaceLogListener) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("prmAppVer", appVer));

        String result = HttpUtil.connectGet(SnapsAPI.POST_API_APP_LAUNCH_COUNT(), params, interfaceLogListener);
        if (result != null && result.length() > 0)
            Dlog.d("postAppLaunchCount() result:" + result);
    }

    public static void postAppInstallCount(String appVer, SnapsInterfaceLogListener interfaceLogListener) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("prmAppVer", appVer));
        params.add(new BasicNameValuePair("prmLogType", "341002"));

        String result = HttpUtil.connectGet(SnapsAPI.POST_API_APP_LAUNCH_COUNT(), params, interfaceLogListener);
        if (result != null && result.length() > 0)
            Dlog.d("postAppInstallCount() result:" + result);
    }

    public static Map<String, String> getStoryImageList(String uuid, String userNo, String searchType, String searchValue, String searchDate, SnapsInterfaceLogListener interfaceLogListener) {

        Map<String, String> resultList = new HashMap<>();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("prmAppType", "android"));
        params.add(new BasicNameValuePair("prmChnlCode", Config.getCHANNEL_CODE()));
        params.add(new BasicNameValuePair("prmDeviceId", uuid));
        params.add(new BasicNameValuePair("prmUserNo", userNo));
        params.add(new BasicNameValuePair("prmSearchType", searchType));
        params.add(new BasicNameValuePair("prmSearchValue", searchValue));
        params.add(new BasicNameValuePair("prmSearchDate", searchDate));

        try {

            String result = HttpUtil.connectGet(SnapsAPI.GET_API_RECOMMEND_PHOTOBOOK_GET_STORY_IMAGE_LIST_URL(), params, interfaceLogListener);

            XmlResult xmlResult = new XmlResult(result);


            int imgCnt = Integer.parseInt(xmlResult.get("F_IMAGE_CNT"));
            int arrSize = xmlResult.getList("F_ORG_PATH");
            NodeList nodeList = xmlResult.getNodeList("F_ORG_PATH");

            SnapsAssert.assertTrue(imgCnt == arrSize);
            for (int i = 0; i < nodeList.getLength(); i++) {
                String f_ORG_PATH = nodeList.item(i).getChildNodes().item(0).getNodeValue();
                resultList.put(f_ORG_PATH, f_ORG_PATH);
            }

            xmlResult.close();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return resultList;
    }

    public static SnapsTemplate getRecommendProduct(String uuid, String userNo, String productCode, String projCode, String recommendSeq, SnapsInterfaceLogListener interfaceLogListener) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("prmAppType", "android"));
        params.add(new BasicNameValuePair("prmChnlCode", Config.getCHANNEL_CODE()));
        params.add(new BasicNameValuePair("prmDeviceId", uuid));
        params.add(new BasicNameValuePair("prmUserNo", userNo));
        params.add(new BasicNameValuePair("prmProdCode", productCode));
        params.add(new BasicNameValuePair("prmProjCode", projCode));
        params.add(new BasicNameValuePair("prmRecommendSeq", recommendSeq));

        InputStream inputStream = null;
        InputStream stringStream = null;
        SnapsTemplate template = null;
        try {
            HttpResponse response = HttpUtil.connectPostReturnHttpResponse(SnapsAPI.GET_API_RECOMMEND_PHOTOBOOK_GET_RECOMMEND_PRODUCT_URL(), HEADER_URL_ENCODED_FORM, params, interfaceLogListener);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                if (inputStream != null) {
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    factory.setValidating(false);
                    SAXParser parser = factory.newSAXParser();
                    XMLReader reader = parser.getXMLReader();

                    GetTemplateXMLHandler xml = new GetThemeBookTemplateXMLHandler();

                    reader.setContentHandler(xml);
                    String result = FileUtil.convertStreamToString(inputStream);
                    stringStream = new ByteArrayInputStream(result.getBytes("UTF-8"));
                    reader.parse(new InputSource(stringStream));
                    template = xml.getTemplate();

                    if (interfaceLogListener != null)
                        interfaceLogListener.onSnapsInterfaceResult(200, "success get template.");

					SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_complete_processLayout_RES)
							.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE())
							.appendPayload(WebLogConstants.eWebLogPayloadType.RESPONSE_CONTENTS, result));
				}
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}

			if (stringStream != null) {
				try {
					stringStream.close();
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
			}
		}

        return template;

    }

    public static String getFaceDetection(String uuid, String userNo, String recommendSeq, SnapsInterfaceLogListener interfaceLogListener) {

        String result = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("prmAppType", "android"));
        params.add(new BasicNameValuePair("prmChnlCode", Config.getCHANNEL_CODE()));
        params.add(new BasicNameValuePair("prmDeviceId", uuid));
        params.add(new BasicNameValuePair("prmUserNo", userNo));
        params.add(new BasicNameValuePair("prmRecommendSeq", recommendSeq));
        try {

            result = HttpUtil.connectGet(SnapsAPI.GET_API_RECOMMEND_PHOTOBOOK_GET_FACE_DETECTION_URL(), params, interfaceLogListener);

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return result;
    }
}
