package com.snaps.mobile.component;

import android.app.Activity;
import android.webkit.WebView;

import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;

public class SnapsWebviewProcess implements ImpWebViewProcess {

	SnapsShouldOverrideUrlLoader processShouldOverrideUrlLoader = null;
	Activity activity = null;

	public SnapsWebviewProcess(Activity activity, IFacebook facebook, IKakao kakao) {
		this.activity = activity;
		processShouldOverrideUrlLoader = new SnapsShouldOverrideUrlLoader(activity, facebook, kakao);
	}

	public void setOnCartCountListener(OnBadgeCountChangeListener onCartCountListener) {
		if (processShouldOverrideUrlLoader == null) {
			return;
		}
		processShouldOverrideUrlLoader.setOnCartCountListener(onCartCountListener);
	}

	@Override
	public boolean shouldOverrideUrlLoading(final WebView view, String url) {
		return processShouldOverrideUrlLoader.shouldOverrideUrlLoading(view, url);
	}

	@Override
	public boolean getCheckProcess() {
		return true;
	}

	public String getFileAttachCMD(String url) {
		if (processShouldOverrideUrlLoader == null) {
			return null;
		}
		return processShouldOverrideUrlLoader.getFileAttachCMD(url);
	}

	public void initPhotoPrint() {

		if (processShouldOverrideUrlLoader == null) {
			return;
		}
		processShouldOverrideUrlLoader.initPhotoPrint();
	}

}
