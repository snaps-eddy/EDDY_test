package com.snaps.common.utils.net.http;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import com.snaps.common.data.event.member_verify.SnapsMemberVerifyEventInfo;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.order.MyOrderInfo;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.log.SnapsInterfaceLogListener;
import com.snaps.common.utils.net.xml.XmlResult;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.StringUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class HttpReq {
    private static final String TAG = HttpReq.class.getSimpleName();

    public interface ISendErrMsgListener {
        void onPosted();
    }

    /**
     * 에러로그 전송
     *
     * @return
     * @params F_PROD_CODE 상품코드
     * @params F_APP_TYPE 어플리케이션타입
     * @params F_HPPN_TYPE 발생경로
     * @params F_DEVICE_NO 기기번호(device_token)
     * @params F_DEVICE_TYPE 기기TYPE
     * @params F_OTHER_ID 기기별 회원번호(uuid)
     * @params F_UUSER_ID 협력사회원ID
     * @params F_USER_NO 사용자번호
     * @params F_ERR_MSG 에러메시지
     * @params F_OS_TYPE OS타입
     * @params F_OS_VER OS버젼
     * @params F_APP_VER 앱버젼
     * @params F_ERR_CLASS 에러클래스
     */
    public static void sendErrorLog(final String F_PROD_CODE, final String F_APP_TYPE, final String F_HPPN_TYPE, final String F_DEVICE_NO, final String F_DEVICE_TYPE, final String F_OTHER_ID,
                                    final String F_UUSER_ID, final String F_USER_NO, final String F_ERR_MSG, final String F_OS_TYPE, final String F_OS_VER, final String F_APP_VER, final String F_ERR_CLASS, final ISendErrMsgListener listener) {

        new Thread() {
            public void run() {
                try {
                    List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                    postParameters.add(new BasicNameValuePair("F_PROD_CODE", F_PROD_CODE));
                    postParameters.add(new BasicNameValuePair("F_APP_TYPE", F_APP_TYPE));
                    postParameters.add(new BasicNameValuePair("F_HPPN_TYPE", F_HPPN_TYPE));
                    postParameters.add(new BasicNameValuePair("F_DEVICE_NO", F_DEVICE_NO));
                    postParameters.add(new BasicNameValuePair("F_DEVICE_TYPE", F_DEVICE_TYPE));
                    postParameters.add(new BasicNameValuePair("F_OTHER_ID", F_OTHER_ID));
                    postParameters.add(new BasicNameValuePair("F_UUSER_ID", F_UUSER_ID));
                    postParameters.add(new BasicNameValuePair("F_USER_NO", F_USER_NO));
                    postParameters.add(new BasicNameValuePair("F_ERR_MSG", F_ERR_MSG));
                    postParameters.add(new BasicNameValuePair("F_OS_TYPE", F_OS_TYPE));
                    postParameters.add(new BasicNameValuePair("F_OS_VER", F_OS_VER));
                    postParameters.add(new BasicNameValuePair("F_APP_VER", F_APP_VER));
                    postParameters.add(new BasicNameValuePair("F_ERR_CLASS", F_ERR_CLASS));

                    HttpUtil.connectPost(SnapsAPI.POST_API_INSERT_ERRORLOG(), postParameters, null);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                } finally {
                    if (listener != null)
                        listener.onPosted();
//					System.exit(2);
                }
            }

            ;
        }.start();
    }

    /**
     * 디바이스 푸쉬정보 전송
     *
     * @param context
     * @param userId
     * @param deviceToken
     * @return
     */
    public static boolean pushInfo(Context context, String userId, String deviceToken, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("f_user_id", userId));
        postParameters.add(new BasicNameValuePair("f_device_token", deviceToken));
        postParameters.add(new BasicNameValuePair("f_uuid", SystemUtil.getDeviceId(context)));

        String result = HttpUtil.connectPost(SnapsAPI.POST_API_PUSH_INFO(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                XmlResult xmlResult = new XmlResult(result);
                String rCode = xmlResult.get("RETURN_CODE");
                xmlResult.close();
                if ("true".equals(rCode))
                    return true;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    public static void registerGCMInfoToSnapsServerOnBackground(final Activity activity, final String userNo, final SnapsInterfaceLogListener interfaceLogListener) {
        try {
            Intent intent = activity.getIntent();
            if (intent == null || !intent.getBooleanExtra(Const_EKEY.PUSH_RUN, false)) return;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String regId = Setting.getString(activity, Const_VALUE.KEY_GCM_REGID);
                    HttpReq.pushInfo(activity, userNo, regId, interfaceLogListener);
                }
            }).start();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * 이벤트 응모하기
     *
     * @param userId
     * @param prodCode
     * @param projCode
     * @param publicType
     * @return
     */
    public static boolean eventApply(String userId, String prodCode, String projCode, String publicType, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("f_user_id", userId));
        if (prodCode != null && !"".equals(prodCode))
            postParameters.add(new BasicNameValuePair("f_prod_code", prodCode));
        if (projCode != null && !"".equals(projCode))
            postParameters.add(new BasicNameValuePair("f_proj_code", projCode));
        postParameters.add(new BasicNameValuePair("f_public_type", publicType));

        String result = HttpUtil.connectPost(SnapsAPI.POST_API_EVENT_APPLY(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                XmlResult xmlResult = new XmlResult(result);
                String rCode = xmlResult.get("RETURN_CODE");
                xmlResult.close();
                if ("true".equals(rCode))
                    return true;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    /**
     * 티몬 쿠폰 등록
     *
     * @param userId
     * @return
     */
    public static String couponInsert(String userId, String eventCode, String pinCode, String F_REG_FLAG, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

        postParameters.add(new BasicNameValuePair("F_USER_ID", userId));
        postParameters.add(new BasicNameValuePair("F_event_code", eventCode));
        postParameters.add(new BasicNameValuePair("F_pin_code", pinCode));
        postParameters.add(new BasicNameValuePair("F_MODE", "insert"));
        postParameters.add(new BasicNameValuePair("F_REG_FLAG", F_REG_FLAG));

        String result = HttpUtil.connectPost(SnapsAPI.SET_COUPON(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                JSONObject jObj = new JSONObject(result);
                String rCode = jObj.getString("RETURN_CODE");
                String rMsg = jObj.getString("RETURN_MSG");
                if ("true".equals(rCode.toLowerCase())) {
                    return "true";
                } else {
                    return rMsg;
                }

            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return "true";
    }

    public static final String FROM_YOBOOK = "140001";
    public static final String FROM_YAHOO_BOX = "140002";
    public static final String FROM_FACEBOOK = "140003";
    public static final String FROM_KAKAOSTORY = "140004";
    public static final String FROM_INSTAGRAM = "140005";
    public static final String FROM_GOOGLE_PHOTO = "140006";

    /***
     * SNS 이미지 서버에 저장용.
     *
     * @param kind
     * @param prjCode
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String saveSNSImage(String oriUrl, String thumbUrl, int kind, String prjCode, String mineType, SnapsInterfaceLogListener interfaceLogListener) {
        String imageType = "";
        switch (kind) {
            case Const_VALUES.SELECT_FACEBOOK:
                imageType = FROM_FACEBOOK;
                break;
            case Const_VALUES.SELECT_KAKAO:
                imageType = FROM_KAKAOSTORY;
                break;
            case Const_VALUES.SELECT_INSTAGRAM:
                imageType = FROM_INSTAGRAM;
                break;
            case Const_VALUES.SELECT_GOOGLEPHOTO:
                imageType = FROM_GOOGLE_PHOTO;
                break;
        }
        if (StringUtil.isEmpty(imageType)) return null;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        try {
            params.add(new BasicNameValuePair("part", "mall.smartphotolite.SmartPhotoLiteInterface"));
            String cmd = "uploadSNSPhoto";
            params.add(new BasicNameValuePair("cmd", cmd));
            params.add(new BasicNameValuePair("nextPage", "uploadSNSPhoto"));
            params.add(new BasicNameValuePair("prmProjCode", prjCode));
            params.add(new BasicNameValuePair("prmImgUrl", URLEncoder.encode(oriUrl, "UTF-8")));
            params.add(new BasicNameValuePair("prmThumImgUrl", URLEncoder.encode(thumbUrl, "UTF-8")));
            params.add(new BasicNameValuePair("prmOrgnCode", imageType));
            params.add(new BasicNameValuePair("prmchnlcode", Config.getCHANNEL_CODE()));
            if (!TextUtils.isEmpty(mineType)) {
                params.add(new BasicNameValuePair("prmMimeType", mineType));
            }
        } catch (UnsupportedEncodingException e) {
            Dlog.e(TAG, e);
            return null;
        }

        return HttpUtil.connectGet(SnapsAPI.DOMAIN() + "/servlet/Command.do", params, interfaceLogListener);
    }

    public static String saveSNSOrgImgForSmartSnaps(MyPhotoSelectImageData uploadImageData, String prjCode, SnapsInterfaceLogListener interfaceLogListener) {
        if (uploadImageData == null) return "";
        String oriUrl = uploadImageData.PATH;
        String thumbUrl = uploadImageData.getSafetyThumbnailPath();
        if (StringUtil.isEmpty(thumbUrl)) thumbUrl = oriUrl;

        String imageType = "";
        switch (uploadImageData.KIND) {
            case Const_VALUES.SELECT_FACEBOOK:
                imageType = FROM_FACEBOOK;
                break;
            case Const_VALUES.SELECT_KAKAO:
                imageType = FROM_KAKAOSTORY;
                break;
            case Const_VALUES.SELECT_INSTAGRAM:
                imageType = FROM_INSTAGRAM;
                break;
            case Const_VALUES.SELECT_GOOGLEPHOTO:
                imageType = FROM_GOOGLE_PHOTO;
                break;
        }
        if (StringUtil.isEmpty(imageType)) return null;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        try {
            params.add(new BasicNameValuePair("part", "mall.smartphotolite.SmartPhotoLiteInterface"));
            String cmd = "smartPhotoSNSOriginalUpload";
            params.add(new BasicNameValuePair("cmd", cmd));
//			params.add(new BasicNameValuePair("nextPage", "uploadSNSPhoto"));
            params.add(new BasicNameValuePair("prmProjCode", prjCode));
            params.add(new BasicNameValuePair("prmImgUrl", URLEncoder.encode(oriUrl, "UTF-8")));
            params.add(new BasicNameValuePair("prmThumImgUrl", URLEncoder.encode(thumbUrl, "UTF-8")));
            params.add(new BasicNameValuePair("prmOrgnCode", imageType));
            params.add(new BasicNameValuePair("prmchnlcode", Config.getCHANNEL_CODE()));
            params.add(new BasicNameValuePair("prmImgYear", uploadImageData.F_IMG_YEAR));
            params.add(new BasicNameValuePair("prmImgSqnc", uploadImageData.F_IMG_SQNC));
            params.add(new BasicNameValuePair("analysisYN", "N"));
        } catch (UnsupportedEncodingException e) {
            Dlog.e(TAG, e);
            return null;
        }

        return HttpUtil.connectGet(SnapsAPI.DOMAIN() + "/servlet/Command.do", params, interfaceLogListener);
    }

    public static String saveSNSThumbImage(String oriUrl, String thumbUrl, int kind, String prjCode, boolean isSmartSnapsProduct, String mineType, SnapsInterfaceLogListener interfaceLogListener) {
        String imageType = "";
        switch (kind) {
            case Const_VALUES.SELECT_FACEBOOK:
                imageType = FROM_FACEBOOK;
                break;
            case Const_VALUES.SELECT_KAKAO:
                imageType = FROM_KAKAOSTORY;
                break;
            case Const_VALUES.SELECT_INSTAGRAM:
                imageType = FROM_INSTAGRAM;
                break;
            case Const_VALUES.SELECT_GOOGLEPHOTO:
                imageType = FROM_GOOGLE_PHOTO;
                break;
        }
        if (StringUtil.isEmpty(imageType)) return null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        try {
            params.add(new BasicNameValuePair("part", "mall.smartphotolite.SmartPhotoLiteInterface"));
            String cmd = isSmartSnapsProduct ? "smartPhotoSNSThumbnaillUpload" : "uploadSNSPhoto";
            params.add(new BasicNameValuePair("cmd", cmd));
//			params.add(new BasicNameValuePair("nextPage", "uploadSNSPhoto"));
            params.add(new BasicNameValuePair("prmProjCode", prjCode));
            params.add(new BasicNameValuePair("prmImgUrl", URLEncoder.encode(oriUrl, "UTF-8")));
            params.add(new BasicNameValuePair("prmThumImgUrl", URLEncoder.encode(thumbUrl, "UTF-8")));
            params.add(new BasicNameValuePair("prmOrgnCode", imageType));
            params.add(new BasicNameValuePair("prmchnlcode", Config.getCHANNEL_CODE()));
            if (!TextUtils.isEmpty(mineType)) {
                params.add(new BasicNameValuePair("prmMimeType", mineType));
            }
            if (isSmartSnapsProduct) {
                params.add(new BasicNameValuePair("analysisYN", "Y"));
            }

        } catch (UnsupportedEncodingException e) {
            Dlog.e(TAG, e);
            return null;
        }

        return HttpUtil.connectGet(SnapsAPI.DOMAIN() + "/servlet/Command.do", params, interfaceLogListener);
    }


    /***
     * 스냅스 설치 이벤트 쿠폰 등록하기
     *
     * @param userId
     * @param f_doc_issue_code
     * @param regFlag
     * @return
     */
    public static String couponEventInsert(String userId, String f_doc_issue_code, String regFlag, String evtCode, String deviceNum, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

        postParameters.add(new BasicNameValuePair("F_USER_ID", userId));
        postParameters.add(new BasicNameValuePair("F_DOC_ISSUE_CODE", f_doc_issue_code));
        postParameters.add(new BasicNameValuePair("F_MODE", "insert"));
        postParameters.add(new BasicNameValuePair("F_REG_FLAG", regFlag));
        postParameters.add(new BasicNameValuePair("F_EVENT_CODE", evtCode));

        if (deviceNum != null)
            postParameters.add(new BasicNameValuePair("F_DEVICE_NO", deviceNum));

        String result = HttpUtil.connectPost(SnapsAPI.SET_COUPON(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                JSONObject jObj = new JSONObject(result);
                String rCode = jObj.getString("RETURN_CODE");
                String rMsg = jObj.getString("RETURN_MSG");
                if ("true".equals(rCode.toLowerCase())) {
                    return "true";
                } else {
                    return rMsg;
                }

            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return "true";
    }


    /***
     * 스냅스 쿠폰 등록하
     *
     * @param userId
     * @param pinCode1
     * @param pinCode2
     * @param pinCode3
     * @param regFlag
     * @return
     */
    public static String couponInsert(String userId, String pinCode1, String pinCode2, String pinCode3, String regFlag, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

        postParameters.add(new BasicNameValuePair("F_USER_ID", userId));
        postParameters.add(new BasicNameValuePair("F_CPN_CODE1", pinCode1));
        postParameters.add(new BasicNameValuePair("F_CPN_CODE2", pinCode2));
        postParameters.add(new BasicNameValuePair("F_CPN_CODE3", pinCode3));
        postParameters.add(new BasicNameValuePair("F_REG_FLAG", regFlag));
        postParameters.add(new BasicNameValuePair("F_MODE", "insert"));

        String result = HttpUtil.connectPost(SnapsAPI.SET_COUPON(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                JSONObject jObj = new JSONObject(result);
                String rCode = jObj.getString("RETURN_CODE");
                String rMsg = jObj.getString("RETURN_MSG");
                if ("true".equals(rCode.toLowerCase())) {
                    return "true";
                } else {
                    return rMsg;
                }

            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return "true";
    }

    /**
     * 쿠폰 사용
     *
     * @param userId
     * @param docCode
     * @param projCode
     * @param orderCode
     * @return
     */
    public static String couponUse(String userId, String docCode, String projCode, String orderCode, String projCnt, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        // http://117.52.102.177/servlet/Command.do?part=mobile.SetData&cmd=MY_CPN&nextPage=MY_CPN&F_SID=MY_CPN&F_CHNL_CODE=KOR0031&
        // F_USER_ID=720521&f_doc_code=TMSN45201145&F_PROJ_CODE=193001&f_mode=use

        postParameters.add(new BasicNameValuePair("F_USER_ID", userId));
        postParameters.add(new BasicNameValuePair("f_doc_code", docCode));
        postParameters.add(new BasicNameValuePair("f_proj_code", projCode));
        postParameters.add(new BasicNameValuePair("f_order_code", orderCode));
        postParameters.add(new BasicNameValuePair("F_PROJ_CNT", projCnt));
        postParameters.add(new BasicNameValuePair("F_MODE", "use"));

        String result = HttpUtil.connectPost(SnapsAPI.SET_COUPON(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                JSONObject jObj = new JSONObject(result);
                String rCode = jObj.getString("RETURN_CODE");
                String rMsg = jObj.getString("RETURN_MSG");
                if ("true".equals(rCode.toLowerCase())) {
                    return "true";
                } else {
                    return rMsg;
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return "true";
    }

    /**
     * 쿠폰 취소
     *
     * @param userId
     * @param projCode
     * @return
     */
    public static String couponCancel(String userId, String projCode, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        // http://117.52.102.177/servlet/Command.do?part=mobile.SetData&cmd=MY_CPN&nextPage=MY_CPN&F_SID=MY_CPN&F_CHNL_CODE=KOR0031&
        // F_USER_ID=720521&F_PROJ_CODE=193001&f_mode=delete

        postParameters.add(new BasicNameValuePair("F_USER_ID", userId));
        postParameters.add(new BasicNameValuePair("f_proj_code", projCode));
        postParameters.add(new BasicNameValuePair("F_MODE", "delete"));

        String result = HttpUtil.connectPost(SnapsAPI.SET_COUPON(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                JSONObject jObj = new JSONObject(result);
                String rCode = jObj.getString("RETURN_CODE");
                String rMsg = jObj.getString("RETURN_MSG");
                if ("true".equals(rCode.toLowerCase())) {
                    return "true";
                } else {
                    return rMsg;
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return "true";
    }

    /**
     * 이벤트 결과확인
     *
     * @param userId
     * @param userName
     * @param userCell
     * @return
     */
    public static String eventConfirmGift(String userId, String userName, String userCell, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("f_user_id", userId));
        postParameters.add(new BasicNameValuePair("f_user_name", userName));
        postParameters.add(new BasicNameValuePair("f_user_cell", userCell));

        String result = HttpUtil.connectPost(SnapsAPI.POST_API_EVENT_GIFT(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                return new XmlResult(result).get("RETURN_CODE");
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return "false";
    }

    /**
     * Snaps 회원가입
     *
     * @param userId
     * @param userPwd
     * @param joinType
     * @return
     */
    public static String snapsJoin(String mode, String userId, String userName, String userPwd, String joinType, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("F_MODE", mode));
        postParameters.add(new BasicNameValuePair("F_USER_ID", userId));
        postParameters.add(new BasicNameValuePair("F_USER_NAME", userName));
        postParameters.add(new BasicNameValuePair("F_USER_PWD", userPwd));
        postParameters.add(new BasicNameValuePair("F_JOIN_TYPE", joinType));

        String result = HttpUtil.connectPost(SnapsAPI.POST_API_SNAPS_JOIN(), postParameters, interfaceLogListener);
        if (result != null) {
//			try {
//				return new XmlResult(result).get("RETURN_CODE");
//			} catch (Exception e) {
//				Dlog.e(TAG, e);
//			}
            return result;
        }
        return "false";
    }

    /**
     * 비밀번호 재설정
     *
     * @param tempPass
     * @param newPass
     * @return
     */
    public static String snapsPwdReset(String userId, String tempPass, String newPass, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("F_USER_ID", userId));
        postParameters.add(new BasicNameValuePair("F_TMP_PASS", Base64.encodeToString(tempPass.getBytes(), 0)));
        postParameters.add(new BasicNameValuePair("F_NEW_PASS", Base64.encodeToString(newPass.getBytes(), 0)));

        String result = HttpUtil.connectPost(SnapsAPI.POST_API_SNAPS_PWDRESET(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                return jsonObj.optString("status", "").toLowerCase();
                // 07-11 16:39:21.104: D/ROK(18210): result:{"status":"COMPLETE","success":true}

            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return "fail";
    }

    /**
     * 스냅스 회원 패스워드 찾기
     *
     * @param context
     * @param userEmail
     * @return
     */
    public static boolean snapsPwdFind(Context context, String userEmail, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("f_user_mail", userEmail));

        String result = HttpUtil.connectPost(SnapsAPI.POST_API_SNAPS_PWDFIND(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                XmlResult xmlResult = new XmlResult(result);
                String rCode = xmlResult.get("RETURN_CODE");
                xmlResult.close();
                if ("true".equals(rCode))
                    return true;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    /**
     * 스냅스 회원 패스워드 찾기
     *
     * @param context
     * @param userEmail
     * @return
     */
    public static boolean snapsNewPwdFind(Context context, String userEmail, String userName, SnapsInterfaceLogListener interfaceLogListener) {

        String newVersion = "Y";
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("F_USER_MAIL", userEmail));
        postParameters.add(new BasicNameValuePair("F_USER_NAME", userName));
        postParameters.add(new BasicNameValuePair("f_new_version", newVersion));

        String result = HttpUtil.connectPost(SnapsAPI.POST_API_SNAPS_PWDFIND(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                XmlResult xmlResult = new XmlResult(result);
                String rCode = xmlResult.get("RETURN_CODE");
                xmlResult.close();
                if ("true".equals(rCode))
                    return true;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    /**
     * 스냅스 회원 탈퇴
     *
     * @param context
     * @return
     */
    public static boolean snapsRetire(Context context, String mode, String userId, String userPwd, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("f_mode", mode));
        postParameters.add(new BasicNameValuePair("f_user_id", userId));
        if (userPwd != null)
            postParameters.add(new BasicNameValuePair("f_user_pwd", userPwd));

        String result = HttpUtil.connectPost(SnapsAPI.POST_API_SNAPS_RETIRE(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                XmlResult xmlResult = new XmlResult(result);
                String rCode = xmlResult.get("RETURN_CODE");
                xmlResult.close();
                if ("true".equals(rCode))
                    return true;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    /**
     * 푸쉬 서비스를 위해 기기정보 등록
     *
     * @param regId
     * @param userName
     * @param appVer
     * @return
     */
    public static boolean regPushDevice(String regId, String userNo, String userName, String appVer, String deviceID, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

        /**
         * 160222 - push 디바이스 등록방식 변경
         */
        if (regId != null && regId.length() > 0)
            postParameters.add(new BasicNameValuePair("f_device_token", regId));
        if (userNo != null && userName != null && userNo.length() > 0 && userName.length() > 0) {
            //파라미터는 아이디이지만 userno로 넣어주어야 한다
            //헷갈리지 말자!!
            postParameters.add(new BasicNameValuePair("f_user_id", userNo));
            postParameters.add(new BasicNameValuePair("f_user_name", userName));
        }
        postParameters.add(new BasicNameValuePair("f_uuid", deviceID));
        postParameters.add(new BasicNameValuePair("f_os_type", Const_VALUE.GCM_ANDROID_OS_TYPE));
        postParameters.add(new BasicNameValuePair("f_os_ver", Build.VERSION.RELEASE));
        postParameters.add(new BasicNameValuePair("f_app_ver", appVer));
        postParameters.add(new BasicNameValuePair("f_fcm_yorn", "Y"));
        String result = HttpUtil.connectPost(SnapsAPI.POST_API_PUSH_DEV(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                XmlResult xmlResult = new XmlResult(result);
                String rCode = xmlResult.get("RETURN_CODE");
                xmlResult.close();
                if ("true".equals(rCode))
                    return true;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    /**
     * 주문취소
     *
     * @param F_ORDER_CODE
     * @return
     */
    public static boolean orderCancel(String F_ORDER_CODE, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("F_ORDER_CODE", F_ORDER_CODE));
        String result = HttpUtil.connectPost(SnapsAPI.POST_API_ORDER_CANCEL(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                XmlResult xmlResult = new XmlResult(result);
                String rCode = xmlResult.get("RETURN_CODE");
                xmlResult.close();
                if ("true".equals(rCode)) {
                    if ("0".equals(new XmlResult(result).get("Status")))
                        return true;
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    /**
     * 주문 배송지,금액정보 저장
     *
     * @param myOrderInfo
     * @return
     */
    public static boolean orderAddrSave(MyOrderInfo myOrderInfo, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("F_ORDER_CODE", myOrderInfo.F_ORDER_CODE));
        postParameters.add(new BasicNameValuePair("F_ORDR_NAME", myOrderInfo.F_ORDR_NAME));
        postParameters.add(new BasicNameValuePair("F_ORDR_CELL", myOrderInfo.F_ORDR_CELL));
        postParameters.add(new BasicNameValuePair("F_ORDR_MAIL", myOrderInfo.F_ORDR_MAIL));
        postParameters.add(new BasicNameValuePair("F_RCPT_NAME", myOrderInfo.F_RCPT_NAME));
        postParameters.add(new BasicNameValuePair("F_RCPT_CELL", myOrderInfo.F_RCPT_CELL));
        postParameters.add(new BasicNameValuePair("F_RCPT_MAIL", myOrderInfo.F_RCPT_MAIL));
        postParameters.add(new BasicNameValuePair("F_RCPT_ZIP", myOrderInfo.F_RCPT_ZIP));
        postParameters.add(new BasicNameValuePair("F_RCPT_ADDR1", myOrderInfo.F_RCPT_ADDR1));
        postParameters.add(new BasicNameValuePair("F_RCPT_ADDR2", myOrderInfo.F_RCPT_ADDR2));
        postParameters.add(new BasicNameValuePair("F_RCPT_ADDR3", myOrderInfo.F_RCPT_ADDR3));
        postParameters.add(new BasicNameValuePair("F_RMRK_CLMN", myOrderInfo.F_RMRK_CLMN));
        postParameters.add(new BasicNameValuePair("F_ORDR_AMNT", String.valueOf(myOrderInfo.F_ORDR_AMNT)));
        postParameters.add(new BasicNameValuePair("F_STTL_AMNT", String.valueOf(myOrderInfo.F_STTL_AMNT)));
        postParameters.add(new BasicNameValuePair("F_DLVR_AMNT", String.valueOf(myOrderInfo.F_DLVR_AMNT)));
        postParameters.add(new BasicNameValuePair("F_DLVR_MTHD", myOrderInfo.F_DLVR_MTHD));
        if (Config.getCHANNEL_CODE() != null && Config.getCHANNEL_CODE().equalsIgnoreCase(Config.CHANNEL_SNAPS_JPN)) {
            postParameters.add(new BasicNameValuePair("F_STTL_MTHD", myOrderInfo.F_STTL_MTHD));
        }

        // // 일본의 경우 후리가나 추가
        // if (Config.CHANNEL_CODE == Config.CHANNEL_SNAPS_JPN) {
        // postParameters.add(new BasicNameValuePair("F_ORDR_NAME_KTKN", myOrderInfo.F_ORDR_NAME_KTKN));
        // postParameters.add(new BasicNameValuePair("F_RCPT_NAME_KTKN", myOrderInfo.F_RCPT_NAME_KTKN));
        // }

        String result = HttpUtil.connectPost(SnapsAPI.POST_API_ORDER_ADDR_SAVE(), postParameters, interfaceLogListener);
        if (result != null) {
            try {
                XmlResult xmlResult = new XmlResult(result);
                String rCode = xmlResult.get("RETURN_CODE");
                xmlResult.close();
                if ("true".equals(rCode))
                    return true;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    /**
     * 작품 -> 장바구니
     *
     * @param context
     * @param userId
     * @param flagType
     * @param projCode
     * @return
     */
//	public static boolean myartworkCartAdd(Context context, String userId, String projCode) {
//		List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
//		getParameters.add(new BasicNameValuePair("f_user_id", userId));
//		getParameters.add(new BasicNameValuePair("f_bag_stat", "146001"));
//		getParameters.add(new BasicNameValuePair("f_flag", Const_VALUES.PROJ_SAVE));
//		getParameters.add(new BasicNameValuePair("f_proj_code", Base64.encodeToString(projCode.getBytes(), 0)));
//		String result = HttpUtil.connectPost(SnapsAPI.POST_API_MY_PROJECT_OPTION_FLAG, getParameters);
//
//		// 장바구니 갯수 받아서 preference에 저장
//		try {
//			int F_CNT = 0;
//			try {
//				F_CNT = Integer.valueOf(new XmlResult(result).get("F_CNT"));
//				Setting.set(context, Const_VALUE.KEY_CART_COUNT, F_CNT);
//			} catch (NumberFormatException e) {
//			}
//			Logg.d("F_CNT:" + F_CNT);
//		} catch (Exception e) {
//			Dlog.e(TAG, e););
//		}
//		/*
//		 * <?xml version="1.0" encoding="utf-8" ?><SCENE ID="MY_PRJ"><ITEM><MY_PRJ_SAV><RETURN_CODE>true</RETURN_CODE><RETURN_MSG>장바구니 담기
//		 * 완료</RETURN_MSG></MY_PRJ_SAV></ITEM><ITEM><MY_PRJ_CNT><F_CNT>12</F_CNT></MY_PRJ_CNT></ITEM></SCENE>
//		 */
//
//		if (result != null) {
//			try {
//				XmlResult xmlResult = new XmlResult(result);
//				String rCode = xmlResult.get("RETURN_CODE");
//				xmlResult.close();
//				if ("true".equals(rCode))
//					return true;
//			} catch (Exception e) {
//				Dlog.e(TAG, e);
//			}
//		}
//		return false;
//	}

    /**
     * 결재된 작품 -> 장바구니
     *
     * @param context
     * @param userId
     * @param projCode
     * @return
     */
    public static boolean myartworkCartReAdd(Context context, String userId, String projCode, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
        getParameters.add(new BasicNameValuePair("f_user_id", userId));
        getParameters.add(new BasicNameValuePair("f_bag_stat", "146001"));
        getParameters.add(new BasicNameValuePair("f_proj_code", Base64.encodeToString(projCode.getBytes(), 0)));
        String result = HttpUtil.connectPost(SnapsAPI.POST_API_MY_PROJECT_CART_READD(), getParameters, interfaceLogListener);

        // 장바구니 갯수 받아서 preference에 저장
        // try {
        // int F_CNT = 0;
        // try {
        // F_CNT = Integer.valueOf(new XmlResult(result).get("F_CNT"));
        // Setting.set(context, Const_VALUE.KEY_CART_COUNT, F_CNT);
        // } catch (NumberFormatException e) {}
        // Logg.d("F_CNT:"+F_CNT);
        // } catch (Exception e) {
        // Dlog.e(TAG, e);
        // }

        if (result != null) {
            try {
                XmlResult xmlResult = new XmlResult(result);
                String rCode = xmlResult.get("RETURN_CODE");
                xmlResult.close();
                if ("true".equals(rCode))
                    return true;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    /**
     * 내작품 삭제
     *
     * @param userId
     * @param projCode
     * @return
     */
    public static boolean myArtworkDel(String userId, String projCode, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
        getParameters.add(new BasicNameValuePair("f_user_id", userId));
        getParameters.add(new BasicNameValuePair("f_flag", Const_VALUES.PROJ_DEL));
        getParameters.add(new BasicNameValuePair("f_proj_code", Base64.encodeToString(projCode.getBytes(), 0)));
        getParameters.add(new BasicNameValuePair("f_bag_stat", "146000"));
        String result = HttpUtil.connectPost(SnapsAPI.POST_API_MY_PROJECT_OPTION_FLAG(), getParameters, interfaceLogListener);

        if (result != null) {
            try {
                XmlResult xmlResult = new XmlResult(result);
                String rCode = xmlResult.get("RETURN_CODE");
                xmlResult.close();
                if ("true".equals(rCode))
                    return true;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    /**
     * 장바구니 삭제(장바구니 -> 작품)
     *
     * @param userId
     * @param projCode
     * @return
     */
    public static boolean cartDel(String userId, String projCode, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
        getParameters.add(new BasicNameValuePair("f_user_id", userId));
        getParameters.add(new BasicNameValuePair("f_flag", Const_VALUES.PROJ_DEL));
        getParameters.add(new BasicNameValuePair("f_proj_code", Base64.encodeToString(projCode.getBytes(), 0)));
        getParameters.add(new BasicNameValuePair("f_bag_stat", "146001"));
        String result = HttpUtil.connectPost(SnapsAPI.POST_API_MY_PROJECT_OPTION_FLAG(), getParameters, interfaceLogListener);

        if (result != null) {
            try {
                XmlResult xmlResult = new XmlResult(result);
                String rCode = xmlResult.get("RETURN_CODE");
                xmlResult.close();
                if ("true".equals(rCode))
                    return true;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    /**
     * 주문결제 삭제
     *
     * @param userId
     * @param projCode
     * @return
     */
    public static boolean orderDel(String userId, String projCode, SnapsInterfaceLogListener interfaceLogListener) {
        // http://117.52.102.177/servlet/Command.do?part=mobile.SetData&cmd=MY_PRJ&nextPage=MY_PRJ&F_SID=MY_PRJ&F_FLAG=ordel&F_CHNL_CODE=KOR0031&f_user_id=&F_PROJ_CODE=
        List<NameValuePair> getParameters = new ArrayList<NameValuePair>();
        getParameters.add(new BasicNameValuePair("f_user_id", userId));
        getParameters.add(new BasicNameValuePair("f_flag", Const_VALUES.PROJ_ORDER_DEL));
        getParameters.add(new BasicNameValuePair("f_proj_code", Base64.encodeToString(projCode.getBytes(), 0)));
        String result = HttpUtil.connectPost(SnapsAPI.POST_API_MY_PROJECT_OPTION_FLAG(), getParameters, interfaceLogListener);

        if (result != null) {
            try {
                XmlResult xmlResult = new XmlResult(result);
                String rCode = xmlResult.get("RETURN_CODE");
                xmlResult.close();
                if ("true".equals(rCode))
                    return true;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    public static boolean resetCouponInfo(String userID, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("F_USER_ID", userID));

        String result = HttpUtil.connectGet(SnapsAPI.COUPON_INFO_RESET(), params, interfaceLogListener);
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(result);
            String r = jsonObj.optString("RETURN_CODE");

            return (r != null && r.equals("TRUE"));
        } catch (JSONException e) {
            Dlog.e(TAG, e);
        }

        return false;

    }


    /***
     * 웹뷰 데이터 preload
     *
     * @param url
     * @param
     * @return
     */
    public static String getWebViewData(String url, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

//		postParameters.add(new BasicNameValuePair("F_USER_ID", userId));
//		postParameters.add(new BasicNameValuePair("F_CPN_CODE1", pinCode1));
//		postParameters.add(new BasicNameValuePair("F_CPN_CODE2", pinCode2));
//		postParameters.add(new BasicNameValuePair("F_CPN_CODE3", pinCode3));
//		postParameters.add(new BasicNameValuePair("F_REG_FLAG", regFlag));
//		postParameters.add(new BasicNameValuePair("F_MODE", "insert"));

        String result = HttpUtil.connectPost(url, null, interfaceLogListener);
        if (result != null) {
//			try {
//				JSONObject jObj = new JSONObject(result);
//				String rCode = jObj.getString("RETURN_CODE");
//				String rMsg = jObj.getString("RETURN_MSG");
//				if ("true".equals(rCode.toLowerCase())) {
//					return "true";
//				} else {
//					Logg.d("RETURN_MSG", rMsg);
//
//					return rMsg;
//				}
//
//			} catch (Exception e) {
//				Dlog.e(TAG, e);
//			}
            return result;
        }
        return "";
    }

    public static void sendSaveErrorLog(final String F_PROJ_CODE, final String F_PROD_CODE, final String F_USER_NO, final String F_APP_VER, final String F_PAGE_CODE, final String F_PAGE_NUM, final SnapsInterfaceLogListener interfaceLogListener) {


        new Thread() {
            public void run() {
                try {
                    List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                    postParameters.add(new BasicNameValuePair("f_proj_code", F_PROJ_CODE));
                    postParameters.add(new BasicNameValuePair("f_prod_code", F_PROD_CODE));
                    postParameters.add(new BasicNameValuePair("f_user_no", F_USER_NO));
                    postParameters.add(new BasicNameValuePair("f_os_type", "190002"));
                    postParameters.add(new BasicNameValuePair("f_app_ver", F_APP_VER));
                    postParameters.add(new BasicNameValuePair("f_page_code", F_PAGE_CODE));
                    postParameters.add(new BasicNameValuePair("f_page_num", F_PAGE_NUM));


                    HttpUtil.connectPost(SnapsAPI.SAVELOG_URL(), postParameters, interfaceLogListener);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                } finally {

                }
            }

            ;
        }.start();
    }

    /**
     * 인증번호 요청
     *
     * @return
     */
    public static boolean regVerifySend(String _AESParams, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();

        parameters.add(new BasicNameValuePair("part", "mobile.SetData"));
        parameters.add(new BasicNameValuePair("cmd", "SEND_AUTHKEY_N"));
        parameters.add(new BasicNameValuePair("PARAM", _AESParams));

        String result = HttpUtil.connectGet(SnapsAPI.DOMAIN() + "/servlet/Command.do", parameters, interfaceLogListener);
        if (result != null) {
            try {
                JSONObject jObj = new JSONObject(result);
                String resultMsg = jObj.getString("RESULT");

                return "SUCCESS".equals(resultMsg);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    /**
     * 인증번호 확인
     */
    public static String regVerifyNumber(String _AESParams, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();

        parameters.add(new BasicNameValuePair("part", "mobile.SetData"));
        parameters.add(new BasicNameValuePair("cmd", "CHECK_AUTHKEY_N"));
        parameters.add(new BasicNameValuePair("PARAM", _AESParams));

        return HttpUtil.connectGet(SnapsAPI.DOMAIN() + "/servlet/Command.do", parameters, interfaceLogListener);
    }

    /**
     * 추가인증 배너 이미지 url 가져오기
     *
     * @return
     */
    public static SnapsMemberVerifyEventInfo verifyBannerGetImage(String userId, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();

        parameters.add(new BasicNameValuePair("part", "mobile.GetData"));
        parameters.add(new BasicNameValuePair("cmd", "AUTH_IMAGE"));
        parameters.add(new BasicNameValuePair("USER_NO", userId));

        String result = HttpUtil.connectGet(SnapsAPI.DOMAIN() + "/servlet/Command.do", parameters, interfaceLogListener);
        if (result != null) {
            try {
                SnapsMemberVerifyEventInfo snapsMemberVerifyEventInfo = new SnapsMemberVerifyEventInfo();
                JSONObject jObj = new JSONObject(result);

                JSONObject couponInfoObj = (JSONObject) jObj.get("COUPON_INFO");
                String title = couponInfoObj.getString("TITLE");

                JSONObject conponsObj = (JSONObject) couponInfoObj.get("COUPON");
                LinkedHashMap<String, String> conponMap = jsonToMap(conponsObj);

                String authPopImage = jObj.getString("AUTH_POP_IMAGE");

                String newMemberYN = jObj.getString("NEW_MEMBER");

                snapsMemberVerifyEventInfo.setAuthPopImage(authPopImage);
                snapsMemberVerifyEventInfo.setCoupons(conponMap);
                snapsMemberVerifyEventInfo.setTitle(title);
                snapsMemberVerifyEventInfo.setShouldShowCouponUI(!StringUtil.isEmpty(newMemberYN) && newMemberYN.equalsIgnoreCase("Y"));
                return snapsMemberVerifyEventInfo;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return null;
    }


    public static String requestAcrylicProductPrice(float width, float height, float discountRate, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();

        //TODO::
        parameters.add(new BasicNameValuePair("part", "mobile.SetData"));
        parameters.add(new BasicNameValuePair("cmd", "PUT_AI_USE_YN"));
        parameters.add(new BasicNameValuePair("width", Integer.toString((int)width)));
        parameters.add(new BasicNameValuePair("height", Integer.toString((int)height)));
        parameters.add(new BasicNameValuePair("discountRate", Integer.toString((int)discountRate)));
        parameters.add(new BasicNameValuePair("prmchnlcode", Config.getCHANNEL_CODE()));

        //TODO::
        //return HttpUtil.connectGet(SnapsAPI.DOMAIN() + "/servlet/Command.do", parameters, interfaceLogListener);
        return "" + (width * height);
    }


    /**
     * AI 사진 업로드 프로그래스바를 그리기 위해서
     * @param apiDomain
     * @param prmUserNo
     * @param prmDeviceId
     * @param interfaceLogListener
     * @return
     */
    /*
    public static String requestPutAIInit(String apiDomain, String prmUserNo, String prmDeviceId,
                                          SnapsInterfaceLogListener interfaceLogListener)
    {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();

        parameters.add(new BasicNameValuePair("part", "mobile.SetData"));
        parameters.add(new BasicNameValuePair("cmd", "PUT_AI_INITIALIZATION"));
        parameters.add(new BasicNameValuePair("prmUserNo", prmUserNo));
        parameters.add(new BasicNameValuePair("prmDeviceId", prmDeviceId));

        return HttpUtil.connectGet(apiDomain + "/servlet/Command.do", parameters, interfaceLogListener);
    }
    */

    /**
     * AI 기능 사용 여부를 서버에 전송하는 메서드
     *
     * @param prmUserNo
     * @param isUse
     * @param interfaceLogListener
     * @return
     */
    /*
    public static String requestPutAIUse(String prmUserNo, boolean isUse, SnapsInterfaceLogListener interfaceLogListener) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();

        parameters.add(new BasicNameValuePair("part", "mobile.SetData"));
        parameters.add(new BasicNameValuePair("cmd", "PUT_AI_USE_YN"));
        parameters.add(new BasicNameValuePair("prmUserNo", prmUserNo));
        parameters.add(new BasicNameValuePair("prmAgreYN", isUse ? "Y" : "N"));

        return HttpUtil.connectGet(SnapsAPI.DOMAIN() + "/servlet/Command.do", parameters, interfaceLogListener);
    }
    */

    /**
     * AI 업로드 시, LTE 사용 여부를 서버에 전송하는 메서드
     *
     * @param prmUserNo
     * @param isUseLTE
     * @param interfaceLogListener
     * @return
     */
    /*
    public static String requestPutAISyncWithLTE(String prmUserNo, boolean isUseLTE, SnapsInterfaceLogListener interfaceLogListener) {

        List<NameValuePair> parameters = new ArrayList<NameValuePair>();

        parameters.add(new BasicNameValuePair("part", "mobile.SetData"));
        parameters.add(new BasicNameValuePair("cmd", "PUT_AI_USE_LTE_YN"));
        parameters.add(new BasicNameValuePair("prmUserNo", prmUserNo));
        parameters.add(new BasicNameValuePair("prmLteYN", isUseLTE ? "Y" : "N"));

        return HttpUtil.connectGet(SnapsAPI.DOMAIN() + "/servlet/Command.do", parameters, interfaceLogListener);
    }
    */

    private static LinkedHashMap<String, String> jsonToMap(JSONObject jObject) throws Exception {
        if (jObject == null) return null;
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = jObject.getString(key);
            result.put(key, value);
        }
        return result;
    }
}
