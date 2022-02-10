package com.snaps.mobile.activity.webview;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.ui.menu.webinterface.ISnapsWebEventCMDConstants;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;

import java.net.URLDecoder;
import java.util.HashMap;

public class WebViewCmdGotoPage {
	private static final String TAG = WebViewCmdGotoPage.class.getSimpleName();
	static public boolean gotoPage(Activity activity, final HashMap<String, String> urlData, String url) {
		if (activity == null || urlData == null || url == null)
			return false;

		Intent intent = new Intent(activity, RenewalHomeActivity.class);
		// 페이지 이동...
		String pageCode = urlData.get("pageCode");
        //이벤트 화면 안지우고 가기 위해 현재 액티비에서 회원가입 소환
		if(pageCode != null&&pageCode.equals("P0012")){
			String userNo = SnapsLoginManager.getUUserNo(activity);
			String loginType="";
			if(TextUtils.isEmpty(userNo)) {
				loginType=Const_VALUES.LOGIN_P_JOIN;
			}else{
				loginType=Const_VALUES.LOGIN_P_VERRIFY_POPUP;
			}
			SnapsLoginManager.startLogInProcess(activity,loginType);
		}else {
			if (pageCode == null)
				pageCode = urlData.get("pageNum");

			if (pageCode == null) return false;

			if (pageCode.equals("P0008")) {

				String eventUrl = urlData.get("optionUrl");
				String title = urlData.get("naviBarTitle");
				if (title == null)
					title = urlData.get("title");
				String fullUrl = eventUrl;
				try {
					eventUrl = URLDecoder.decode(eventUrl, "utf-8");
					title = URLDecoder.decode(title, "utf-8");
					fullUrl = eventUrl;
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}

				intent.putExtra("goKakaoEvent", true);
				intent.putExtra("eventUrl", fullUrl);
				intent.putExtra("naviTitle", title);

			} else {
				intent.putExtra("gototarget", pageCode);
				intent.putExtra("fullurl", url);
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			activity.startActivity(intent);


			if (!(activity instanceof RenewalHomeActivity)) activity.finish();
		}


		return true;
	}

	static public boolean gotoPage(Activity activity, final SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
		if (handleDatas == null || handleDatas.getHost() == null || !handleDatas.getHost().equalsIgnoreCase(ISnapsWebEventCMDConstants.SNAPS_CMD_GO_TO_PAGE))
			return false;

		String url = handleDatas.getUrl();

		final HashMap<String, String> urlData = Config.ExtractWebURL(url);
		if (urlData == null) return false;

		return gotoPage(activity, urlData, url);
	}
}
