package com.snaps.mobile.activity.webview;

import android.view.View;
import android.webkit.WebView;

import com.snaps.mobile.utils.kakao.KakaoStoryPostingEventor;

public interface IUIWebViewActBridge {

    View getBtnEdit();

    View getBtnComplete();

    KakaoStoryPostingEventor getKakaoStoryPostingEventor();

    String getSnsShareCallBack();

    void setSnsShareCallBack(String str);

    boolean isINISIS_Page();

    void setIsPaymentComplete(boolean flag);

    void shouldOverrideUrlLoading(WebView view, String url);
}
