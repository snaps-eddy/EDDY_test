package com.snaps.kakao.utils.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.webkit.WebView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IFBActivityResult;
import com.snaps.common.utils.ui.IPostingResult;
import com.snaps.kakao.utils.kakao.KaKaoUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
//

public class SNSShareUtil implements ISNSShareConstants {
	private static final String TAG = SNSShareUtil.class.getSimpleName();

	private static IFBActivityResult gFBCallback = null;
	
	public static void postActivityForResult(int requestCode, int  resultCode, Intent data) {
		if(gFBCallback != null)
			gFBCallback.onFBActivityResult(requestCode, resultCode, data);
	}
	
	public static void releaseCallback() {
		gFBCallback = null;
	}
	
	//FIXME 웹팀 작업 중..
	public static void loadSnsShareCallBack(boolean success, String errMsg,
			String snsShareCallBack, WebView webview) {
		if(snsShareCallBack == null || snsShareCallBack.length() < 1) return;
		
//		성공 : { "status" : "success" , "msg" : "ok",  "code" :"0000" }
//		실패 : { "status" : " fail" , "msg" : "이유",  "code" :"코드" }
		StringBuffer sbErrMsg = new StringBuffer();
		sbErrMsg.append("{ \"status\" : \"").append(success ? "success" : "fail").append("\", ")
		.append("\"msg\" : \"").append(success ? "ok" : (errMsg != null ? errMsg : "unknown")).append("\", ")
		.append("\"code\" : \"").append("0000").append("\"").append(" }");
			
		String url = String.format("javascript:%s(%s)", snsShareCallBack, sbErrMsg.toString());
		if(webview != null)
			webview.loadUrl(url);
	}
	
	public static String sendSNSShareContents(Activity activity, HashMap<String, String> urlData) {
		return sendSNSShareContents(activity, urlData, null);
	}
	
	//return callback 
	public static String sendSNSShareContents(Activity activity, HashMap<String, String> urlData, IPostingResult listener) {
		if(urlData == null || urlData.isEmpty()) return null;
		
		try {
			String type = urlData.get("type");
			String linkUrl = urlData.get("linkUrl");
			String subject = urlData.get("title");
			String desc = urlData.get("desc");
			String btnTitle = urlData.get("btnTitle");
			String imgUrl = urlData.get("imgUrl");
			String imgWidth = urlData.get("imgWidth");
			String imgHeight = urlData.get("imgHeight");
			String callback = urlData.get("callback");
			String postType = urlData.get("postType");

			if(type != null) type = URLDecoder.decode(type, "utf-8");
			if(linkUrl != null) linkUrl = URLDecoder.decode(linkUrl, "utf-8");
			if(subject != null) subject = URLDecoder.decode(subject, "utf-8");
			if(desc != null) desc = URLDecoder.decode(desc, "utf-8");
			if(btnTitle != null) btnTitle = URLDecoder.decode(btnTitle, "utf-8");
			if(imgUrl != null) imgUrl = URLDecoder.decode(imgUrl, "utf-8");
			if(imgWidth != null) imgWidth = URLDecoder.decode(imgWidth, "utf-8");
			if(imgHeight != null) imgHeight = URLDecoder.decode(imgHeight, "utf-8");
			if(callback != null) callback = URLDecoder.decode(callback, "utf-8");
		
			if(type.equalsIgnoreCase("facebook")) {
				SNSShareContentsStruct.SNSShareContentsFaceBook sns = new SNSShareContentsStruct.SNSShareContentsFaceBook(activity);
				sns.setLink(linkUrl);
				sns.setSubject(subject);
				sns.setDescription(desc);
				
				SNSShareUtil.post(SNSShareUtil.SNS_TYPE_FACEBOOK, sns, listener);
			} else if(type.equalsIgnoreCase("band")) {
				SNSShareContentsStruct.SNSShareContentsBand sns = new SNSShareContentsStruct.SNSShareContentsBand(activity);
				sns.setLink(linkUrl);
				sns.setSubject(subject);
				SNSShareUtil.post(SNSShareUtil.SNS_TYPE_BAND, sns);
			} else if(type.equalsIgnoreCase("line")) {
				SNSShareContentsStruct.SNSShareContentsLine sns = new SNSShareContentsStruct.SNSShareContentsLine(activity);
				sns.setLink(linkUrl);
				sns.setSubject(subject);
				SNSShareUtil.post(SNSShareUtil.SNS_TYPE_LINE, sns);
			} else if(type.equalsIgnoreCase("kakaoStory")) {
				SNSShareContentsStruct.SNSShareContentsKakaoStory sns = new SNSShareContentsStruct.SNSShareContentsKakaoStory(activity);
				sns.setLink(linkUrl);
				sns.setSubject(subject);
				sns.setDescription(desc);
				sns.setUrlText(btnTitle);
				sns.setImgUrl(imgUrl);
				sns.setImgWidth(imgWidth);
				sns.setImgHeight(imgHeight);
				if(postType != null) {
					sns.setPostType(postType);
				}
				SNSShareUtil.post(SNSShareUtil.SNS_TYPE_KAKAO_STORY, sns, listener);
			} else if(type.equalsIgnoreCase("kakaoTalk")) {
				SNSShareContentsStruct.SNSShareContentsKakaoTalk sns = new SNSShareContentsStruct.SNSShareContentsKakaoTalk(activity);
				sns.setLink(linkUrl);
				sns.setSubject(subject);
				sns.setBtnTitle(btnTitle);
				sns.setImgUrl(imgUrl);
				sns.setImgWidth(imgWidth);
				sns.setImgHeight(imgHeight);
				SNSShareUtil.post(SNSShareUtil.SNS_TYPE_KAKAO_TALK, sns);
			}
			
			return callback;
		} catch (UnsupportedEncodingException e1) {
			Dlog.e(TAG, e1);
		} catch (Exception e) {
			Dlog.e(TAG, e);
			return null;
		}
		return null;
	}
	
	public static void post(byte snsKind, SNSShareContentsStruct.SNSShareContents contents, IPostingResult listener) throws Exception {
		if(contents == null) return;
		
		SNSShareParent snsShare = null;
		KaKaoUtil kUtil = null;
		
		switch (snsKind) {
		case SNS_TYPE_BAND:
		case SNS_TYPE_LINE:
			snsShare = (SNSShareParent) SNSShareFactory.createInstance(snsKind, contents, listener);
			snsShare.post(listener);
			break;
		case SNS_TYPE_FACEBOOK:
			snsShare = (SNSShareParent) SNSShareFactory.createInstance(snsKind, contents, listener);
			gFBCallback = snsShare.postFB(listener);
			break;
		case SNS_TYPE_KAKAO_STORY:
			kUtil = (KaKaoUtil) SNSShareFactory.createInstance(snsKind, contents, listener);
			kUtil.sendPostingLink((Activity)contents.getContext(), contents, listener);
			break;
		case SNS_TYPE_KAKAO_TALK:
			kUtil = (KaKaoUtil) SNSShareFactory.createInstance(snsKind, contents, listener);
			kUtil.sendProjectShareLink(contents.getContext(), contents, listener);
			break;
		}
	}
	
	public static void post(byte snsKind, SNSShareContentsStruct.SNSShareContents contents) throws Exception {
		post(snsKind, contents, null);
	}
	
	public static void gotoGooglePlay(Context context, String packageName) {
		if(context == null) return;
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + packageName));
			context.startActivity(intent);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}
	
	
	public static boolean isInstalledApp(Context context, String packageName) {
		if(context == null || packageName == null || packageName.length() < 1) return false;
		
		PackageManager pm = context.getPackageManager();
		ApplicationInfo appInfo = null;
		try {
			appInfo = pm.getApplicationInfo(packageName,
					PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			Dlog.e(TAG, e);
		}

		return appInfo != null;
	}
}
