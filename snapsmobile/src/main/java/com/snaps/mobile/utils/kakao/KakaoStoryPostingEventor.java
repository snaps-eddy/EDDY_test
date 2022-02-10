package com.snaps.mobile.utils.kakao;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.webkit.WebView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.IPostingResult;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventBaseHandler;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import errorhandle.logger.Logg;

public class KakaoStoryPostingEventor {

	IKakao kakao = null;
	Activity activity = null;
	String saveFilePath = null;

	LoginReceiver receiver = null;
	WebView webView = null;

	public KakaoStoryPostingEventor(Activity act) {
		activity = act;
		kakao = SnsFactory.getInstance().queryIntefaceKakao();
		// 리시버 등록...
		if (receiver == null)
			receiver = new LoginReceiver();
		IntentFilter filterkakao = new IntentFilter(Const_VALUE.KAKAOLOING_ACTION);
		activity.registerReceiver(receiver, filterkakao);
	}

	public boolean isStoryPostingEvent(HashMap<String, String> hashmap, WebView webView, String url) {
		String schemeHost = SnapsWebEventBaseHandler.findHost(url);
		if (schemeHost == null || schemeHost.length() < 1) {
			if(hashmap != null && hashmap.containsKey(Const_EKEY.WEB_CMD_KEY)) {
				schemeHost = hashmap.get(Const_EKEY.WEB_CMD_KEY);
			}
		}

		if (schemeHost != null && schemeHost.length() > 0) {
			if (schemeHost.equals("storyLink")) {
				sendStoryPhotoPosting(hashmap, webView);
				return true;
			} else if (schemeHost.equals("kakaoStoryCheck")) {
				checkKakaoStoryLogin(webView, hashmap);
				return true;
			}
		}
		return false;
	}

	public void release() {
		if (receiver != null) {
			activity.unregisterReceiver(receiver);
			receiver = null;
		}
	}

	class LoginReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action != null && TextUtils.equals(action, Const_VALUE.KAKAOLOING_ACTION)) {
				kakao = SnsFactory.getInstance().queryIntefaceKakao();

				if (checkCmd != null) {
					webView.loadUrl(checkCmd, SystemUtil.getWebviewVersionMapData(activity));
				}
			}
		}

	}

	String callback = null;
	/***
	 * 카카오스토리 포스팅
	 * 
	 * @param urlData
	 * @return
	 */
	boolean sendStoryPhotoPosting(final HashMap<String, String> urlData, final WebView view) {
		String desc = urlData.get(Const_EKEY.WEB_DESCRIPTION);
		String imgUrl = urlData.get("linkUrl");
		callback = urlData.get("callback");

		try {
			desc = URLDecoder.decode(desc, "utf-8");
			imgUrl = URLDecoder.decode(imgUrl, "utf-8");
		} catch (UnsupportedEncodingException e) {
			desc = "";
		}

		final List<File> files = new ArrayList<File>();
		final String content = desc;
		final String imgPath = imgUrl;

		ATask.executeVoid(new OnTask() {
			File file = null;

			@Override
			public void onPre() {
			}

			@Override
			public void onPost() {
				if (file != null && file.exists()) {
					files.add(file);
					kakao.sendPostingPhoto(files, content, new IPostingResult() {
						@Override
						public void OnPostingComplate(boolean isSucess,
								String errMsg) {
							// 포스팅 완료 cmd
							String ret = isSucess ? "0" : "1";
							String returnCmd = String.format("javascript:%s(\"%s\")", callback, ret);
							view.loadUrl(returnCmd, SystemUtil.getWebviewVersionMapData(activity));							
						}
					});
				} else {
					String cmd = String.format("javascript:%s(\"%s\")", callback, "1");
					view.loadUrl(cmd, SystemUtil.getWebviewVersionMapData(activity));
				}
			}

			@Override
			public void onBG() {
				// 이미지를 다운받는다.
				saveFilePath = Config.getExternalCacheDir(activity) + "/photo_thumbnails/" + imgPath.hashCode();
				file = new File(saveFilePath);
				if (file != null && file.exists()) {
					;
				} else {
					HttpUtil.saveUrlToFile(imgPath, saveFilePath);
					file = new File(saveFilePath);
				}
			}
		});

		return true;
	}

	String checkCmd = null;

	void checkKakaoStoryLogin(final WebView webView, final HashMap<String, String> urlData) {

		String callback = urlData.get("callback");

		String cmdText = "";

		if (kakao == null) {
			cmdText = String.format("javascript:%s(\"%s\")", callback, "1");
			sendWebCommand(webView, cmdText);
		}

		if (kakao.isKakaoLogin()) {
			cmdText = String.format("javascript:%s(\"%s\")", callback, "0");
			sendWebCommand(webView, cmdText);
		}

		else {
			this.webView = webView;
			kakao.startKakaoLoginActivity(activity);
			checkCmd = String.format("javascript:%s(\"%s\")", callback, "0");
		}
	}

	void sendWebCommand(WebView webView, String cmdText) {
		webView.loadUrl(cmdText, SystemUtil.getWebviewVersionMapData(activity));
	}
}
