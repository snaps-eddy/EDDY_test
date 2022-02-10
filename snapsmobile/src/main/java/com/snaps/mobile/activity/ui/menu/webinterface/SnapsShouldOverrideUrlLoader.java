package com.snaps.mobile.activity.ui.menu.webinterface;

import android.app.Activity;
import android.webkit.WebView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventBaseHandler;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventCartUrlHandler;
import com.snaps.mobile.activity.webview.IUIWebViewActBridge;
import com.snaps.mobile.activity.webview.PopupWebviewActivity;
import com.snaps.mobile.component.ImpWebViewProcess;
import com.snaps.mobile.component.ObserveScrollingWebView;
import com.snaps.mobile.component.OnBadgeCountChangeListener;
import com.snaps.mobile.utils.ui.UrlUtil;

import java.util.HashSet;

import errorhandle.logger.SnapsLogger;

import static com.snaps.common.utils.constant.Const_VALUE.KEY_DEVELOP_URL_PATH;
import static com.snaps.common.utils.constant.Const_VALUE.KEY_DEVELOP_URL_SETTING_MODE;

public class SnapsShouldOverrideUrlLoader implements ISnapsShouldOverrideUrlLoader {
	private static final String TAG = SnapsShouldOverrideUrlLoader.class.getSimpleName();
	public static SnapsShouldOverrideUrlLoader createInstanceForCart(Activity activity, IUIWebViewActBridge uiWebViewActBridge) {
		SnapsShouldOverrideUrlLoader snapsShouldOverrideUrlLoader = new SnapsShouldOverrideUrlLoader(activity, false);
		snapsShouldOverrideUrlLoader.isFromCart = true;
		snapsShouldOverrideUrlLoader.uiWebViewActBridge = uiWebViewActBridge;
		return snapsShouldOverrideUrlLoader;
	}

	public static final boolean NATIVE = true;
	public static final boolean WEB =  false;

	private ISnapsWebViewLoadListener snapsWebViewLoadListener = null;
	private Activity activity = null;
	private boolean isNativeUI = false;
	private boolean isFromCart = false;
	private IUIWebViewActBridge uiWebViewActBridge;

	private IFacebook facebook;
	private IKakao kakao;

	private SnapsWebEventBaseHandler webEventHandler;

	public ISnapsWebViewLoadListener getSnapsWebViewLoadListener() {
		return snapsWebViewLoadListener;
	}

	public void setSnapsWebViewLoadListener(
			ISnapsWebViewLoadListener snapsWebViewLoadListener) {
		this.snapsWebViewLoadListener = snapsWebViewLoadListener;
	}
	
	public SnapsShouldOverrideUrlLoader(Activity activity, boolean isNative) {
		this.activity = activity;
		this.isNativeUI = isNative;
	}

	public SnapsShouldOverrideUrlLoader(Activity activity, IFacebook facebook, IKakao kakao) {
		this.activity = activity;
		this.facebook = facebook;
		this.kakao = kakao;
	}

	private HashSet<ImpWebViewProcess> processes = null;
	private ObserveScrollingWebView observerWebView = null;

	//***** 주체가 되는 activity로 변경해 주지 않으면, 정상적으로 동작하지 않을 수 있다.
	public void setActivity(Activity activity) {
		this.activity = activity;
		//TODO  SNS객체의 주체도 바꾸어 주어야 하나..?
	}

	public Activity getActivity() {
		return activity != null ? activity : (Activity) ContextUtil.getContext();
	}

	public void setProcesses(HashSet<ImpWebViewProcess> processes) {
		this.processes = processes;
	}
	
	public void setObserveScrollingWebView (ObserveScrollingWebView webView) {
		this.observerWebView = webView;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView webView, String url) {
		return shouldOverrideUrlLoading(webView, url, null);
	}

	public boolean shouldOverrideUrlLoading(WebView webView, String url, PopupWebviewActivity.IPopupDialogFragmentCallback popupDialogCallback) {
		url = UrlUtil.convertNativeURLIfOldUrl(url);

		url = getTestUrlIfSetDevelopUrl(url);

		SnapsLogger.appendSnapsSchemeUrlLog(url);

        SnapsWebEventBaseHandler.sendPageEventTracker( getActivity(), url );

		SnapsShouldHandleData handleDatas = new SnapsShouldHandleData();
		handleDatas.setProcesses(processes);
		handleDatas.setObserveScrollingWebView(observerWebView);
		handleDatas.setWebview(webView);
		handleDatas.setUrl(url);
		handleDatas.setIsNativeUI(isNativeUI);
		handleDatas.setSnapsWebViewLoadListener(getSnapsWebViewLoadListener());
		handleDatas.setFacebook(facebook);
		handleDatas.setKakao(kakao);
		handleDatas.setPopupDialogFragmentCallback(popupDialogCallback);

		if (isFromCart)
			webEventHandler = SnapsWebEventCartUrlHandler.createHandler(getActivity(), handleDatas, uiWebViewActBridge);
		else
			webEventHandler = SnapsWebEventHandlerFactory.createWebEventHandler(getActivity(), handleDatas);

		if (webEventHandler != null) {
			webEventHandler.sendHomeMenuClickAnalysis();
			return webEventHandler.handleEvent();
		}

		return false;
	}

	//개발을 위한 편리 기능(홈에서 메뉴키로 접근할 수 있다)
	private String getTestUrlIfSetDevelopUrl(String url) {
		if (!Config.isDevelopVersion()) return url;
		try {
			String developUrl = Setting.getString(activity, KEY_DEVELOP_URL_PATH);
			if (!StringUtil.isEmpty(developUrl)) {
				return developUrl;
			}

			if (url != null && url.contains("selectProduct")) {
				boolean isDevelopUrlSettingMode = Setting.getBoolean(activity, KEY_DEVELOP_URL_SETTING_MODE);
				if (isDevelopUrlSettingMode) {
					Setting.set(activity, KEY_DEVELOP_URL_PATH, url);
					Setting.set(activity, KEY_DEVELOP_URL_SETTING_MODE, false);
				}
			}
		} catch (Exception e) { Dlog.e(TAG, e); }
		return url;
	}

	public static class SnapsShouldHandleData {
		private HashSet<ImpWebViewProcess> processes;
		private ObserveScrollingWebView observeScrollingWebView;
		private WebView webview;
		private String url;
		private boolean isNativeUI;
		private ISnapsWebViewLoadListener snapsWebViewLoadListener;
		private IFacebook facebook;
		private IKakao kakao;
		private SnapsWebEventBaseHandler webEventHandler;
		private PopupWebviewActivity.IPopupDialogFragmentCallback popupDialogFragmentCallback;
		private String host;
		private String thirdPartySchema;

		public String getThirdPartySchema() {
			return thirdPartySchema;
		}

		public void setThirdPartySchema(String thirdPartySchema) {
			this.thirdPartySchema = thirdPartySchema;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public PopupWebviewActivity.IPopupDialogFragmentCallback getPopupDialogFragmentCallback() {
			return popupDialogFragmentCallback;
		}

		public void setPopupDialogFragmentCallback(PopupWebviewActivity.IPopupDialogFragmentCallback popupDialogFragmentCallback) {
			this.popupDialogFragmentCallback = popupDialogFragmentCallback;
		}

		public IFacebook getFacebook() {
			return facebook;
		}

		public void setFacebook(IFacebook facebook) {
			this.facebook = facebook;
		}

		public IKakao getKakao() {
			return kakao;
		}

		public void setKakao(IKakao kakao) {
			this.kakao = kakao;
		}

		public SnapsWebEventBaseHandler getWebEventHandler() {
			return webEventHandler;
		}

		public void setWebEventHandler(SnapsWebEventBaseHandler webEventHandler) {
			this.webEventHandler = webEventHandler;
		}

		public ISnapsWebViewLoadListener getSnapsWebViewLoadListener() {
			return snapsWebViewLoadListener;
		}

		public void setSnapsWebViewLoadListener(ISnapsWebViewLoadListener snapsWebViewLoadListener) {
			this.snapsWebViewLoadListener = snapsWebViewLoadListener;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public boolean isNativeUI() {
			return isNativeUI;
		}

		public void setIsNativeUI(boolean isNativeUI) {
			this.isNativeUI = isNativeUI;
		}

		public HashSet<ImpWebViewProcess> getProcesses() {
			return processes;
		}

		public void setProcesses(HashSet<ImpWebViewProcess> processes) {
			this.processes = processes;
		}

		public ObserveScrollingWebView getObserveScrollingWebView() {
			return observeScrollingWebView;
		}

		public void setObserveScrollingWebView(ObserveScrollingWebView observeScrollingWebView) {
			this.observeScrollingWebView = observeScrollingWebView;
		}

		public WebView getWebview() {
			return webview;
		}

		public void setWebview(WebView webview) {
			this.webview = webview;
		}
	}

	@Override
	public boolean shouldOverrideUrlLoading(String url) {
		return shouldOverrideUrlLoading(null, url);
	}

	public void setOnCartCountListener(OnBadgeCountChangeListener onCartCountListener) {
		if (webEventHandler != null) {
			webEventHandler.setOnBadgeCountListener(onCartCountListener);
		}
	}

	public String getFileAttachCMD(String url) {
		String fileAttachCallBack = Setting.getString(getActivity(), Const_VALUE.KEY_FILE_ATTACH_CALLBACK_MSG, null);

		if(fileAttachCallBack == null || fileAttachCallBack.length() < 1) return null;

		return String.format("javascript:%s(\"%s\")", fileAttachCallBack, url);
	}

	public void initPhotoPrint() {
		if (webEventHandler != null)
			webEventHandler.initPhotoPrint();
	}
}
