package com.snaps.mobile.component;

import android.app.Activity;
import android.webkit.WebView;

import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;

public class SnapsNativeListViewProcess implements ImpWebViewProcess {

	private SnapsShouldOverrideUrlLoader processShouldOverrideUrlLoader = null;

    public SnapsNativeListViewProcess(Activity activity, IFacebook facebook, IKakao kakao) {
        init(activity, facebook, kakao);
	}

    private void init(Activity activity, IFacebook facebook, IKakao kakao) {
        processShouldOverrideUrlLoader = new SnapsShouldOverrideUrlLoader(activity, facebook, kakao);
    }

	@Override
	public boolean shouldOverrideUrlLoading(final WebView view, String url) {
		return processShouldOverrideUrlLoader.shouldOverrideUrlLoading(view, url);
	}

	@Override
	public boolean getCheckProcess() {
		return true;
	}

	public void setActivity(Activity activity) {
		if (processShouldOverrideUrlLoader != null) {
			processShouldOverrideUrlLoader.setActivity(activity);
		}
	}
}
